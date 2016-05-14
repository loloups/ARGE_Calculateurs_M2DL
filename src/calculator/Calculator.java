package calculator;

import java.util.Timer;

import org.apache.xmlrpc.webserver.WebServer;

import utils.CPUTask;
import utils.XmlRpcUtil;

public class Calculator {
	
	public static double load = 50;
    
	public int add(int a, int b) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return a + b;
	}
	
	public double getLoad() {
		System.out.println("Current CPU : "+load);
		return load;
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.err.println("Calculator needs exactly 1 parameter to start : port of local machine.");
		}
		else {
			
	        new Timer(true).schedule(new CPUTask(), 0, 1000);
		
			System.out.println("Attenmpting to start Web server ...");

			WebServer webServer = new WebServer(Integer.parseInt(args[0]));
			XmlRpcUtil.createXmlRpcServer(webServer, "CalculatorHandlers.properties");
			webServer.start();
			System.out.println("The server has been started successfully and is now accepting requests.");
			System.out.println("Halt program to stop server.");
		}
	}
}
