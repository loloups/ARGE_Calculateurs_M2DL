package utils;

import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;

import calculator.CalculatorDetails;

public class ClientCallback implements AsyncCallback {
	private CalculatorDetails calculatorDetails;
	
	public ClientCallback(CalculatorDetails calculatorDetails) {
		super();
		this.calculatorDetails = calculatorDetails;
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
	    XmlRpcClient client;
		try {
			client = XmlRpcUtil.createXmlRpcClient("localhost",8080);
			Object[] params = new Object[] { calculatorDetails.getAddress(), calculatorDetails.getPort()};
			client.execute("Manager.decr", params);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}
}
