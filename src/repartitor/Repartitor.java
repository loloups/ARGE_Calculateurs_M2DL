package repartitor;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.webserver.WebServer;

import calculator.CalculatorDetails;
import utils.ClientCallback;
import utils.XmlRpcUtil;

public class Repartitor {

	public static Set<CalculatorDetails> calculators;
	public static int curCalculator = 0;
	public static String adresseManager;
	
	public boolean add(Integer port, String address) {
		try {
			calculators.add(new CalculatorDetails(address,port));
			System.out.println("Addition of calculator with port : "+port.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
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
				if(numCal == curCalculator){
					calculatorDetails = calculator;
				}
				numCal++;
			}
			
			if(calculatorDetails != null){
				System.out.println("Current calculator"+curCalculator);
				
				//Call Autonomic to incr request
				XmlRpcClientConfigImpl configManager = new XmlRpcClientConfigImpl();
				configManager.setServerURL(new URL("http://" + adresseManager + ":8080/xmlrpc"));
				configManager.setEnabledForExtensions(true);
				configManager.setConnectionTimeout(60 * 1000);
				configManager.setReplyTimeout(60 * 1000);
				XmlRpcClient clientManager = new XmlRpcClient();
				clientManager.setTransportFactory(
		                new XmlRpcCommonsTransportFactory(clientManager));
				clientManager.setConfig(configManager);
				Object[] paramsManager = new Object[] { calculatorDetails.getAddress(), calculatorDetails.getPort()};
				clientManager.execute("Manager.incr", paramsManager);
				
				//Call Calculator
				XmlRpcClientConfigImpl configCalc = new XmlRpcClientConfigImpl();
	            configCalc.setServerURL(new URL("http://" + calculatorDetails.getAddress() + ":"+calculatorDetails.getPort()+"/xmlrpc"));
	            configCalc.setEnabledForExtensions(true);
	            configCalc.setConnectionTimeout(60 * 1000);
	            configCalc.setReplyTimeout(60 * 1000);

	            XmlRpcClient client = new XmlRpcClient();

	            client.setTransportFactory(
	                new XmlRpcCommonsTransportFactory(client));
	            client.setConfig(configCalc);				
	            Object[] params = new Object[] { new Integer(i), new Integer(i + 1) };
				client.executeAsync("Calculator.add", params, new ClientCallback(calculatorDetails,adresseManager));
				
				
				curCalculator = (curCalculator+1) % (calculators.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 2) {
			System.err.println("Repartitor needs exactly 1 parameter to start : repartitor port.");
		}
		else {
			System.out.println("Attenmpting to start Repartitor web server ...");

			WebServer webServer = new WebServer(Integer.parseInt(args[0]));
			adresseManager = args[1];
			XmlRpcUtil.createXmlRpcServer(webServer, "RepartitorHandlers.properties");
			calculators = Collections.newSetFromMap(new ConcurrentHashMap<CalculatorDetails, Boolean>());
			webServer.start();
			System.out.println("Repartitor is ready and is now accepting requests.");
			System.out.println("Halt program to stop server.");
		}
	}

}
