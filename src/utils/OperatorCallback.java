package utils;

import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;

public class OperatorCallback implements AsyncCallback {

    private String type;

    public OperatorCallback(String type) {
        super();
        this.type = type;
    }

    @Override
    public void handleError(XmlRpcRequest request, Throwable t) {
        System.out.println("In error");
        t.printStackTrace();
    }

    @Override
    public void handleResult(XmlRpcRequest request, Object result) {
        System.out.println("In result");

        if (type == "send") {
            System.out.println("Request send");
        } else if (type == "receive"){
            System.out.println(request.getMethodName() + ": " + result);
        }
    }
}