import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

public class Repartitor {
	static Map<String, Integer> calculateurs = Collections.synchronizedMap(new HashMap<>());
	static int modulo = 0;

	public void add(String machine, int port) {
		synchronized (calculateurs) {
			calculateurs.put(machine, port);
		}
	}

	public void del(String machine, int port) {
		synchronized (calculateurs) {
			calculateurs.remove(machine);
		}
	}

	public void send(int a, int b) {
		modulo = (modulo++) % calculateurs.size();
		synchronized (calculateurs) {
			// create configuration
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			String machine = (String) (calculateurs.keySet().toArray())[modulo];
			String url = "http://" + machine + ":" + calculateurs.get(machine) + "/xmlrpc";
			try {
				config.setServerURL(new URL(url));
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			config.setEnabledForExtensions(true);
			config.setConnectionTimeout(60 * 1000);
			config.setReplyTimeout(60 * 1000);

			XmlRpcClient client = new XmlRpcClient();

			// use Commons HttpClient as transport
			client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
			// set configuration
			client.setConfig(config);

			Object[] params = new Object[] { new Integer(a), new Integer(b) };
			Integer result;
			try {
				result = (Integer) client.execute("Calculator.add", params);
				System.out.println("2 + 3 = " + result);
			} catch (XmlRpcException e) {
				e.printStackTrace();
			}
		}
	}
}
