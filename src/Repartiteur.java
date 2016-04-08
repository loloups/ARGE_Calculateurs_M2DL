import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class Repartiteur {

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Port attendu");
		} else {

			WebServer webServer = new WebServer(Integer.parseInt(args[0]));

			XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

			PropertyHandlerMapping phm = new PropertyHandlerMapping();
			/*
			 * Load handler definitions from a property file. The property file
			 * might look like: Calculator=org.apache.xmlrpc.demo.Calculator
			 * org.apache.xmlrpc.demo.proxy.Adder=org.apache.xmlrpc.demo.proxy.
			 * AdderImpl
			 */
			phm.load(Thread.currentThread().getContextClassLoader(), "XmlRpcServlet.properties");

			/*
			 * You may also provide the handler classes directly, like this:
			 * phm.addHandler("Calculator",
			 * org.apache.xmlrpc.demo.Calculator.class);
			 * phm.addHandler(org.apache.xmlrpc.demo.proxy.Adder.class.getName()
			 * , org.apache.xmlrpc.demo.proxy.AdderImpl.class);
			 */
			xmlRpcServer.setHandlerMapping(phm);

			XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
			serverConfig.setEnabledForExtensions(true);
			serverConfig.setContentLengthOptional(false);

			webServer.start();
		}
	}
}
