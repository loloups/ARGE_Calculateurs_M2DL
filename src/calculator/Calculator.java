package calculator;

import org.apache.xmlrpc.webserver.WebServer;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import utils.XmlRpcUtil;

public class Calculator {
	
	public static Sigar sigar = new Sigar();
    
	public int add(int a, int b) {
		return a + b;
	}
	
	public double getLoad() {
		try {
		return sigar.getCpuPerc().getCombined()*100;
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return 50.0;
	}

	public static void main(String[] args) throws Exception {
		if(args.length != 1) {
			System.err.println("Calculator needs exactly 1 parameter to start : port of local machine.");
		}
		else {
			
			System.out.println("Attenmpting to start Web server ...");

			WebServer webServer = new WebServer(Integer.parseInt(args[0]));
			XmlRpcUtil.createXmlRpcServer(webServer, "CalculatorHandlers.properties");
			webServer.start();
			System.out.println("The server has been started successfully and is now accepting requests.");
			System.out.println("Halt program to stop server.");
		}
	}
}
