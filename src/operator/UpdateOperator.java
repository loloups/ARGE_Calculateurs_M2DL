package operator;

import org.apache.xmlrpc.client.XmlRpcClient;

import utils.XmlRpcUtil;

public class UpdateOperator {

	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("UpdateOperator needs exactly 3 parameters to start : number of requests per second, operator address and port.");
		}
		else {
			try {

				XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(args[1], Integer.parseInt(args[2]));

				Object[] params = new Object[] { new Integer(args[0]) };
				client.execute("Operator.setNumberRequestsPerSecond", params);

                XmlRpcClient clientManager = XmlRpcUtil.createXmlRpcClient("localhost", 2001);
                Object[] paramsManager = new Object[] { true };
                clientManager.execute("Repartitor.setNumberOfRequestModified", paramsManager);


			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
