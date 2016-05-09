package repartitor;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.webserver.WebServer;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.openstack.OSFactory;

import calculator.CalculatorDetails;
import utils.ClientCallback;
import utils.XmlRpcUtil;

public class Repartitor {

	public static Set<CalculatorDetails> calculators;
	public static int curCalculator = 0;
	
    public boolean add(Integer port) {
        try {
            
            Thread.sleep(1000);

            OSClient os = connectCloudmip();

            // Create a Server Model Object
            List<String> networks = Arrays.asList("c1445469-4640-4c5a-ad86-9c0cb6650cca");

            String name = "Moskito_" + calculators.size();
            ServerCreate serverCreate = Builders.server()
                .name(name)
                .flavor("2")
                .image("652e70e5-48d0-40d0-a725-5aa12680ba20")
                .networks(networks)
                .keypairName("MoskitoKey")
                .build();
            
            
            // Boot and wait for the server
            Server server = os.compute().servers().bootAndWaitActive(serverCreate,6000);
            System.out.println("WN created");
            
            // Get the address of the WN
            Map<String,List<? extends Address>> addresses = server.getAddresses().getAddresses();
            String addressServer = addresses.get("private").get(0).getAddr();

            String id = server.getId();
            calculators.add(new CalculatorDetails(id, addressServer, port));
            System.out.println("Addition of calculator with port " + port.toString() + ", address " + addressServer + " and id " + id);
            
            Thread.sleep(5000);
            
            // Example : call a calculator from a workerNode
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://" + addressServer + ":8080/xmlrpc"));
            config.setEnabledForExtensions(true);
            config.setConnectionTimeout(60 * 1000);
            config.setReplyTimeout(60 * 1000);

            XmlRpcClient client = new XmlRpcClient();

            client.setTransportFactory(
                new XmlRpcCommonsTransportFactory(client));
            client.setConfig(config);

            Object[] params = new Object[]
                { new Integer(2), new Integer(3) };
            Integer result = (Integer) client.execute("Calculator.add", params);
            System.out.println("2 + 3 = " + result);

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

	public boolean del(String id) {
			        
		try {
			for (CalculatorDetails calculatorDetails : calculators) {
			    if (calculatorDetails.getId().equals(id)) {
			        calculators.remove(calculatorDetails);
			        OSClient os = connectCloudmip();
                    os.compute().servers().delete(id);
			        System.out.println("Deletion of webserver " + id);
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
    /**
     * Authenticate to Cloudmip
     * @return OSClient
     */
    private OSClient connectCloudmip() {

        OSClient os = OSFactory.builder()
            .endpoint("http://195.220.53.61:5000/v2.0")
            .credentials("ens30", "74J2O1")
            .tenantName("service")
            .authenticate();
        
        System.out.println("Connected to cloudmip");
        
        return os;
    }
    
	public int send(int i) {
		int result = -1;
		try {
			CalculatorDetails calculatorDetails = null;
			int numCal = 0;
			for (CalculatorDetails calculator : calculators) {
				if(i == curCalculator){
					calculatorDetails = calculator;
				}
				numCal++;
			}
			
			if(calculatorDetails != null){
				System.out.println("Current calculator"+curCalculator);
				XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(calculatorDetails.getAddress(), calculatorDetails.getPort());
				Object[] params = new Object[] { new Integer(i), new Integer(i + 1) };
				client.executeAsync("Calculator.add", params, new ClientCallback());
				curCalculator = (curCalculator+1) % (calculators.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.err.println("Repartitor needs exactly 1 parameter to start : repartitor port.");
		}
		else {
			System.out.println("Attenmpting to start Repartitor web server ...");

			WebServer webServer = new WebServer(Integer.parseInt(args[0]));
			XmlRpcUtil.createXmlRpcServer(webServer, "RepartitorHandlers.properties");
			calculators = Collections.newSetFromMap(new ConcurrentHashMap<CalculatorDetails, Boolean>());
			webServer.start();
			System.out.println("Repartitor is ready and is now accepting requests.");
			System.out.println("Halt program to stop server.");
		}
	}

}
