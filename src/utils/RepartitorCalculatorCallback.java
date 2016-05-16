package utils;

import java.net.MalformedURLException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;

import calculator.CalculatorDetails;

public class RepartitorCalculatorCallback implements AsyncCallback {

    private CalculatorDetails calculatorDetails;
    private String            addressManager;
    private int parameter;
    
    public RepartitorCalculatorCallback(CalculatorDetails calculatorDetails, String address, int i) {
        super();
        this.calculatorDetails = calculatorDetails;
        this.addressManager = address;
        this.parameter = i;
    }

    @Override
    public void handleError(XmlRpcRequest request, Throwable t) {
//        System.out.println("In error");
        Object[] params = new Object[] { new Integer(parameter) };
        try {
            XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(calculatorDetails.getAddress(), calculatorDetails.getPort());
            client.executeAsync("Calculator.add", params, new RepartitorCalculatorCallback(calculatorDetails, addressManager, parameter));
        }
        catch (XmlRpcException | MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        t.printStackTrace();
    }

    @Override
    public void handleResult(XmlRpcRequest request, Object result) {
        System.out.println("In result");
        System.out.println(request.getMethodName() + ": " + result);

        try {

            XmlRpcClient clientManager = XmlRpcUtil.createXmlRpcClient(addressManager, 8080);
            Object[] params = new Object[] { calculatorDetails.getAddress(), calculatorDetails.getPort() };
            clientManager.execute("Manager.decr", params);

            XmlRpcClient client = XmlRpcUtil.createXmlRpcClient("localhost", 2000);
            Object[] paramsOperator = new Object[] { new Integer((int) result) };
            client.executeAsync("Operator.receive", paramsOperator, new OperatorRepartitorCallback("receive"));
        }
        catch (XmlRpcException | MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
