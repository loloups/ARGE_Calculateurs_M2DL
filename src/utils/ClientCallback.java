package utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import calculator.CalculatorDetails;

public class ClientCallback implements AsyncCallback {
	private CalculatorDetails calculatorDetails;
	private String addressManager;
	
	public ClientCallback(CalculatorDetails calculatorDetails, String address) {
		super();
		this.calculatorDetails = calculatorDetails;
		this.addressManager = address;
	}

	@Override
	public void handleError(XmlRpcRequest request, Throwable t) {
	    System.out.println("In error");
	    t.printStackTrace();
	}

	@Override
	public void handleResult(XmlRpcRequest request, Object result) {
	    System.out.println("In result");
	    System.out.println(request.getMethodName() + ": " + result);
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL("http://" + addressManager + ":8080/xmlrpc"));
			config.setEnabledForExtensions(true);
			config.setConnectionTimeout(60 * 1000);
			config.setReplyTimeout(60 * 1000);
			XmlRpcClient clientManager = new XmlRpcClient();
			clientManager.setTransportFactory(
	                new XmlRpcCommonsTransportFactory(clientManager));
			clientManager.setConfig(config);
			Object[] params = new Object[] { calculatorDetails.getAddress(), calculatorDetails.getPort()};
			clientManager.execute("Manager.decr", params);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}
}
