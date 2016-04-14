package repartitor;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.webserver.WebServer;

import calculator.Calculator;
import calculator.CalculatorDetails;
import utils.ClientCallback;
import utils.XmlRpcUtil;

public class Repartitor {

	public static Set<CalculatorDetails> calculators;
	
	public CalculatorDetails add(Integer port, String address) {
	
		String[] args = {port.toString()};
		
		try {
			Calculator.main(args);
			CalculatorDetails calculatorDetails = new CalculatorDetails(address,port, Calculator.lastCreatedCalculatorServer);
			calculators.add(calculatorDetails);
			return calculatorDetails;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public boolean delete(Integer port, String address) {
				
		try {
			for (CalculatorDetails calculatorDetails : calculators) {
			    if (calculatorDetails.getPort() == port && calculatorDetails.getAddress().equals(address)) {
			    	calculatorDetails.getWebServer().shutdown();
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
			boolean sended = false;
			for (CalculatorDetails calculatorDetails : calculators) {
			    if(sended) {
			    	if(calculatorDetails.getNbCurrentRequest() == 0) {
			    		delete(calculatorDetails.getPort(), calculatorDetails.getAddress());
			    	}
			    } else if(calculatorDetails.getNbCurrentRequest() < calculatorDetails.getNbMaxRequest()){
			    	XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(calculatorDetails.getAddress(), calculatorDetails.getPort());
					Object[] params = new Object[] { new Integer(i), new Integer(i + 1) };
					calculatorDetails.incrRequest();
					client.executeAsync("Calculator.add", params, new ClientCallback(calculatorDetails));
					sended = true;
			    }	    
			}
			if(!sended) {
				CalculatorDetails calculatorDetails = add(8080+calculators.size()+1, "127.0.0.1");
				if(calculatorDetails != null) {
					System.out.println("Je crée un calculateur");
					XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(calculatorDetails.getAddress(), calculatorDetails.getPort());
					Object[] params = new Object[] { new Integer(i), new Integer(i + 1) };
					calculatorDetails.incrRequest();
					client.executeAsync("Calculator.add", params, new ClientCallback(calculatorDetails));
				}
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
