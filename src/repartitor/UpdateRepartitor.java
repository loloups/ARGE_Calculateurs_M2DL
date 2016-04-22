package repartitor;

import org.apache.xmlrpc.client.XmlRpcClient;

import utils.XmlRpcUtil;

public class UpdateRepartitor {

	public static void main(String[] args) {
		if(args.length != 5) {
			System.err.println("UpdateRepartitor needs exactly 5 parameters to start : operator address, port, action, calculator port, calculator address.");
		}
		else {
			try {

				XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(args[0], Integer.parseInt(args[1]));

				Object[] params = new Object[] { new Integer(args[3]), args[4] };
				client.execute("Repartitor." + args[2], params);

			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
