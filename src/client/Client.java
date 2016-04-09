package client;

import org.apache.xmlrpc.client.XmlRpcClient;

import utils.XmlRpcUtil;

public class Client {

	public final static int numberOfrequests = 10;

	public static void main(String[] args) {
		try {

			XmlRpcClient client = XmlRpcUtil.createXmlRpcClient("127.0.0.1", 8080);

			for(int i=0; i< numberOfrequests ;i++) {
				Object[] params = new Object[] {};
				Integer result = (Integer) client.execute("Repartitor.send", params);
				System.out.println("The result of 2 + 3 is " + result + ".");
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}