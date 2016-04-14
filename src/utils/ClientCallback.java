package utils;

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;

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
	    calculatorDetails.decrRequest();
	}

	@Override
	public void handleResult(XmlRpcRequest request, Object result) {
	    System.out.println("In result");
	    System.out.println(request.getMethodName() + ": " + result);
	    calculatorDetails.decrRequest();
	}
}
