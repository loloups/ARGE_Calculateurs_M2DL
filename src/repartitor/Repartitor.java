package repartitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.webserver.WebServer;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.openstack.OSFactory;

import calculator.CalculatorDetails;
import utils.ClientCallback;
import utils.XmlRpcUtil;

public class Repartitor {

	public static Set<CalculatorDetails> calculators;
	public static int curCalculator = 0;
	
    public boolean add(Integer port, String address) {
        try {
            
            Thread.sleep(1000);

            OSClient os = connectCloudmip();

            // Create a Server Model Object
            List<String> networks = Arrays.asList("c1445469-4640-4c5a-ad86-9c0cb6650cca");

            ServerCreate serverCreate = Builders.server()
                .name("Moskito4")
                .flavor("2")
                .image("2a321e3b-60d9-4411-ae94-51a81827efe9")
                .networks(networks)
                .keypairName("mykey")
                .build();
            System.out.println("WN created");
            
            // Boot the server
            Server server = os.compute().servers().boot(serverCreate);
            
            // Waiting the server
            while (os.compute().servers().get(server.getId()).getStatus() != Status.ACTIVE) {
                Thread.sleep(1000);
            }
            Server s = os.compute().servers().get(server.getId());

            // Get the address of the WN
            Map<String,List<? extends Address>> addresses = s.getAddresses().getAddresses();
            String addressServer = addresses.get("private").get(0).getAddr();
            
            calculators.add(new CalculatorDetails(addressServer, port));
            System.out.println("Addition of calculator with port : " + port.toString());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
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
            .credentials("ens29", "KNO2X4")
            .tenantName("service")
            .authenticate();
        
        return os;
    }

	public boolean del(Integer port, String address) {
				
		try {
			for (CalculatorDetails calculatorDetails : calculators) {
			    if (calculatorDetails.getPort() == port && calculatorDetails.getAddress().equals(address)) {
			        calculators.remove(calculatorDetails);
			        System.out.println("Deletion of webserver with port " + port.toString());
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
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
