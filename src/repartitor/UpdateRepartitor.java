package repartitor;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

public class UpdateRepartitor {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println(
                "UpdateRepartitor needs exactly 5 parameters to start : operator address, operator port, action, calculator address, calculator port.");
        }
        else {
            try {
		System.out.println("http://" + args[0] + ":"+args[1]+"/xmlrpc");
            	XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    			config.setServerURL(new URL("http://" + args[0] + ":"+args[1]+"/xmlrpc"));
    			config.setEnabledForExtensions(true);
    			config.setConnectionTimeout(60 * 1000);
    			config.setReplyTimeout(60 * 1000);
    			XmlRpcClient clientManager = new XmlRpcClient();
    			clientManager.setTransportFactory(
    	                new XmlRpcCommonsTransportFactory(clientManager));
    			clientManager.setConfig(config);

				Object[] params = new Object[] { new Integer(args[3]), args[4] };
				clientManager.execute("Repartitor." + args[2], params);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
