
  import java.net.MalformedURLException;
  import java.net.URL;

  import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
  import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
  import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
  import org.apache.xmlrpc.client.util.ClientFactory;
//  import org.apache.xmlrpc.demo.proxy.Adder;

  public class Client {
      public static void main(String[] args) throws Exception {
    	  if(args.length != 3) {
    		  System.out.println("3 arguments attendu");
    	  } else {
              // create configuration
              XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
              String url = "http://" + args[1] + ":" + args[2] + "/xmlrpc";
              config.setServerURL(new URL(url));
              config.setEnabledForExtensions(true);  
              config.setConnectionTimeout(60 * 1000);
              config.setReplyTimeout(60 * 1000);

              XmlRpcClient client = new XmlRpcClient();
            
              // use Commons HttpClient as transport
              client.setTransportFactory(
                  new XmlRpcCommonsTransportFactory(client));
              // set configuration
              client.setConfig(config);

              
              // make the a regular call
              for(int i=0;i< (Integer.parseInt(args[0])) ;i++) {
            	  Object[] params = new Object[]
                          { new Integer(2), new Integer(3) };
                      Integer result = (Integer) client.execute("Calculator.add", params);
                      System.out.println("2 + 3 = " + result);
              }
            
              // make a call using dynamic proxy
    	  /*          ClientFactory factory = new ClientFactory(client);
              Adder adder = (Adder) factory.newInstance(Adder.class);
              int sum = adder.add(2, 4);
              System.out.println("2 + 4 = " + sum);
    	  */
    	  }
      }
  }