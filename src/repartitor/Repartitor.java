package repartitor;

import org.apache.xmlrpc.client.XmlRpcClient;

import utils.XmlRpcUtil;

public class Repartitor {

	public int send() {
		int result = -1;
		try {

			XmlRpcClient client = XmlRpcUtil.createXmlRpcClient("127.0.0.1", 8080);

			Object[] params = new Object[] { new Integer(10), new Integer(7) };
			result = (Integer) client.execute("Calculator.add", params);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
