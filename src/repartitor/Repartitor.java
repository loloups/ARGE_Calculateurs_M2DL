package repartitor;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.webserver.WebServer;
import java.net.MalformedURLException;
import org.apache.xmlrpc.XmlRpcException;



import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.openstack.OSFactory;

import calculator.CalculatorDetails;
import utils.RepartitorCalculatorCallback;
import utils.XmlRpcUtil;

public class Repartitor {

	public static Set<CalculatorDetails> calculators;
	public static String adressManager;
	public static int curCalculator = 0;

	/**
	 * Notify the manager when the number of requests is modified
	 * @param numberOfRequestModified
	 * @return
	 */
	public boolean setNumberOfRequestModified(boolean numberOfRequestModified) {
	    
        XmlRpcClient clientManager;
        try {
            clientManager = XmlRpcUtil.createXmlRpcClient(adressManager, 8080);
            Object[] paramsManager = new Object[] {numberOfRequestModified};
            clientManager.execute("Manager.setNumberOfRequestModified", paramsManager);
        }
        catch (MalformedURLException | XmlRpcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return numberOfRequestModified;
	}


	
	/**
	 * Add a calculator
	 * @param port
	 * @param address
	 * @return
	 */
	public boolean add(Integer port, String address) {
		try {
			calculators.add(new CalculatorDetails(address,port));
			System.out.println("Addition of calculator with port : "+port.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Remove a calculator
	 * @param port
	 * @param address
	 * @return
	 */
	public boolean del(Integer port, String address) {
				
		try {
			for (CalculatorDetails calculatorDetails : calculators) {
			    if (calculatorDetails.getPort() == port && calculatorDetails.getAddress().equals(address)) {
			        calculators.remove(calculatorDetails);
			        System.out.println("Deletion of webserver with port " + port.toString());
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Send a request to the current calculator
	 * @param i
	 * @return
	 */
	public int send(int i) {
	    
		int result = -1;
		try {
			CalculatorDetails calculatorDetails = null;
			int numCal = 0;
			for (CalculatorDetails calculator : calculators) {
				if(numCal == curCalculator){
					calculatorDetails = calculator;
				}
				numCal++;
			}
			
			if(calculatorDetails != null){
			    
			     //Call Autonomic to incr request                
                XmlRpcClient clientManager = XmlRpcUtil.createXmlRpcClient(adressManager, 8080);
                Object[] paramsManager = new Object[] { calculatorDetails.getAddress(), calculatorDetails.getPort()};
                clientManager.execute("Manager.incr", paramsManager);
                
				System.out.println("Current calculator"+curCalculator);
				
				//Call Calculator
				XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(calculatorDetails.getAddress(), calculatorDetails.getPort());
	            Object[] params = new Object[] { new Integer(i) };
                client.executeAsync("Calculator.add", params, new RepartitorCalculatorCallback(calculatorDetails, adressManager, i));
				
				curCalculator = (curCalculator+1) % (calculators.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}


	/**
     	* Authenticate to Cloudmip
     	* 
     	* @* @return OSClient
     	*/
    	public static OSClient connectCloudmip() {

        	OSClient os = OSFactory.builder().endpoint("http://195.220.53.61:5000/v2.0").credentials("ens30", "74J2O1")
            	.tenantName("service").authenticate();

        	System.out.println("Connected to cloudmip");

        	return os;
    	}

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.err.println("Repartitor needs exactly 1 parameter to start : repartitor port and addressManager.");
		}
		else {
			System.out.println("Attenmpting to start Repartitor web server ...");

			WebServer webServer = new WebServer(Integer.parseInt(args[0]));
			XmlRpcUtil.createXmlRpcServer(webServer, "RepartitorHandlers.properties");
			calculators = Collections.newSetFromMap(new ConcurrentHashMap<CalculatorDetails, Boolean>());
			webServer.start();
			System.out.println("Repartitor is ready and is now accepting requests.");
			System.out.println("Halt program to stop server.");
			
			OSClient os = connectCloudmip();
			
			do {
				System.out.println("Searching for Manager");
            			List<? extends Server> servers = os.compute().servers().list();

            			for (Server server : servers) {
                			if ("Moskaland-AutonomicManager".equals(server.getName())) {
                    				if (server.getAddresses().getAddresses() != null) {
                        				System.out.println("Manager found");
                        				System.out.println(
                            					"Manager address" + server.getAddresses().getAddresses().get("private").get(0).getAddr());
                        				adressManager = server.getAddresses().getAddresses().get("private").get(0).getAddr();
                        				break;
                    				}
                			}
            			}
			}
        		while (adressManager == null);	
		}
	}

}
