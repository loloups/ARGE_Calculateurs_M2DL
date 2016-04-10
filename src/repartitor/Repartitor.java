package repartitor;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.webserver.WebServer;

import utils.XmlRpcUtil;

public class Repartitor {

	public int send(int i) {
		int result = -1;
		try {

			XmlRpcClient client = XmlRpcUtil.createXmlRpcClient("127.0.0.1", 8080);

			Object[] params = new Object[] { new Integer(i), new Integer(i + 1) };
			result = (Integer) client.execute("Calculator.add", params);

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
			webServer.start();

			System.out.println("Repartitor is ready and is now accepting requests.");
			System.out.println("Halt program to stop server.");
		}
	}

}
