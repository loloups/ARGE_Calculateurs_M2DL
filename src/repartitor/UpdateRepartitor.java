package repartitor;

import org.apache.xmlrpc.client.XmlRpcClient;

import utils.XmlRpcUtil;

public class UpdateRepartitor {

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println(
                "UpdateRepartitor needs exactly 4 parameters to start : operator address, operator port, action, calculator port (if action=add) or name of the worker node (if action=delete).");
        }
        else {
            try {

                XmlRpcClient client = XmlRpcUtil.createXmlRpcClient(args[0], Integer.parseInt(args[1]));

                String action = args[2];

                if (action == "add") {
                    Object[] params = new Object[] { new Integer(args[3]) };
                    client.execute("Repartitor." + args[2], params);
                }
                else if (action == "del"){
                    Object[] params = new Object[] { args[3] };
                    client.execute("Repartitor." + args[2], params);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
