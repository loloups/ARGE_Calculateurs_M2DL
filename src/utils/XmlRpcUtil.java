package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class XmlRpcUtil {

	public static XmlRpcClient createXmlRpcClient(String adress, int port) throws MalformedURLException {
		XmlRpcClientConfigImpl clientConfig = createXmlRpcClientConfig(adress, port);
		XmlRpcClient client = new XmlRpcClient();

		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		client.setConfig(clientConfig);

		return client;
	}

	public static XmlRpcClientConfigImpl createXmlRpcClientConfig(String address, int port) throws MalformedURLException {
		XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
		String urlAsString = createUrl(address, port);

		clientConfig.setServerURL(new URL(urlAsString));
		clientConfig.setEnabledForExtensions(true);  
		clientConfig.setConnectionTimeout(60000);
		clientConfig.setReplyTimeout(300000);

		return clientConfig;
	}

	private static String createUrl(String address, int port) {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("http://").append(address).append(":").append(port).append("/xmlrpc");
		return stringBuffer.toString();
	}

	public static void createXmlRpcServer(WebServer webServer, String handlersProperties) throws IOException, XmlRpcException {
		PropertyHandlerMapping propertyHandlerMapping = new PropertyHandlerMapping();
		propertyHandlerMapping.load(Thread.currentThread().getContextClassLoader(), handlersProperties);

		XmlRpcServer server = webServer.getXmlRpcServer();
		server.setHandlerMapping(propertyHandlerMapping);

		XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) server.getConfig();
		serverConfig.setEnabledForExtensions(true);
		serverConfig.setContentLengthOptional(false);
	}

}
