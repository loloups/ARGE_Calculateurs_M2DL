package operator;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.webserver.WebServer;

import utils.XmlRpcUtil;

public class Operator {

	public static int numberRequestsPerSecond;

	public int setNumberRequestsPerSecond(int load) {
		System.out.println("Change load from " + numberRequestsPerSecond + " to " + load);
		numberRequestsPerSecond = load;
		return load;
	}

	public static int getNumberRequestsPerSecond() {
		return numberRequestsPerSecond;
	}

	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("Operator needs exactly 3 parameters to start : number of requests per second, repartitor address and port.");
		}
		else {
			try {

				System.out.println("Attenmpting to start Operator web server ...");

				WebServer webServer = new WebServer(2000);
				XmlRpcUtil.createXmlRpcServer(webServer, "OperatorHandlers.properties");

				webServer.start();

				System.out.println("Operator is ready and is now accepting requests.");
				System.out.println("Halt program to stop server.");

				XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(args[1], Integer.parseInt(args[2]));

				Operator operator = new Operator();
				operator.setNumberRequestsPerSecond(Integer.parseInt(args[0]));

				while(true) {
					for(int i=0; i< getNumberRequestsPerSecond() ;i++) {
						Object[] params = new Object[] {new Integer(i)};
						Integer result = (Integer) client.execute("Repartitor.send", params);
						//System.out.println("The result of (" + i + " + " + new Integer(i+1) + ") is " + result + ".");
					}
					System.out.println("1 second left ...");
				}

			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
