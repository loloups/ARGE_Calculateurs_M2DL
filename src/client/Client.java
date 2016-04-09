package client;

import org.apache.xmlrpc.client.XmlRpcClient;

import utils.XmlRpcUtil;

public class Client {

	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("Client needs exactly 3 parameters to start : number of requests per second, repartitor address and port.");
		}
		else {
			try {

				XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(args[1], Integer.parseInt(args[2]));

				while(true) {
					for(int i=0; i< Integer.parseInt(args[0]) ;i++) {
						Object[] params = new Object[] {new Integer(i)};
						Integer result = (Integer) client.execute("Repartitor.send", params);
						System.out.println("The result of (" + i + " + " + new Integer(i+1) + ") is " + result + ".");
					}
					System.out.println("1 second left ...");
				}

			}catch(Exception e) {
				e.printStackTrace();
			}	
		}
	}

}