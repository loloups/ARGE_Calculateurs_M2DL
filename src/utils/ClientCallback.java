package utils;

import java.net.MalformedURLException;

import java.net.URL;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import utils.XmlRpcUtil;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.webserver.WebServer;

public class ClientCallback implements AsyncCallback {

	XmlRpcClient client;
	public ClientCallback(XmlRpcClient client) {
		super();
		this.client = client;
	}

	public ClientCallback() {
		super();
}

	@Override
	public void handleError(XmlRpcRequest request, Throwable t) {
	    System.out.println("In error");
	    t.printStackTrace();
	}

	@Override
	public void handleResult(XmlRpcRequest request, Object result) {
	    System.out.println("In result");

	    if (this.client != null) {
		try {

				XmlRpcClientConfigImpl configCalc = new XmlRpcClientConfigImpl();
	            configCalc.setServerURL(new URL("http://192.168.0.162:2000/xmlrpc"));
	            configCalc.setEnabledForExtensions(true);
	            configCalc.setConnectionTimeout(300 * 1000);
	            configCalc.setReplyTimeout(300 * 1000);

	            XmlRpcClient client = new XmlRpcClient();

	            client.setTransportFactory(
	                new XmlRpcCommonsTransportFactory(client));
	            client.setConfig(configCalc);		
    
	    Object[] params = new Object[] { new Integer((int) result) };
            
                client.executeAsync("Operator.receive", params, new ClientCallback());
            }
            catch (XmlRpcException | MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	    }

	    System.out.println(request.getMethodName() + ": " + result);
	}
}
