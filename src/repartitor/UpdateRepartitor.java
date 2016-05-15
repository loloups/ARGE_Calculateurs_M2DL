package repartitor;

import org.apache.xmlrpc.client.XmlRpcClient;

import utils.XmlRpcUtil;

public class UpdateRepartitor {

    public static void main(String[] args) {
        
        if (args.length != 5) {
            System.err.println(
                "UpdateRepartitor needs exactly 5 parameters to start : operator address, operator port, action, calculator address, calculator port.");
        }
        else {
            try {
                
                System.out.println("http://" + args[0] + ":"+args[1]+"/xmlrpc");
                XmlRpcClient clientManager = XmlRpcUtil.createXmlRpcClient(args[0], Integer.parseInt(args[1]));
				Object[] params = new Object[] { new Integer(args[3]), args[4] };
				clientManager.execute("Repartitor." + args[2], params);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
