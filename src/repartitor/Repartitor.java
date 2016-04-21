package repartitor;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.webserver.WebServer;

import calculator.CalculatorDetails;
import utils.ClientCallback;
import utils.XmlRpcUtil;

public class Repartitor {

	public static Set<CalculatorDetails> calculators;
	public static int curCalculator = 0;
	
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

	public boolean delete(Integer port, String address) {
				
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
