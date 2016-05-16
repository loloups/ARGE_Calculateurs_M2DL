package calculator;

import org.apache.xmlrpc.webserver.WebServer;

import utils.XmlRpcUtil;

public class Calculator {

    public int add(int a) {

        int nbDiviseur = 0;
        double m = 2 * Math.pow(2, a) - 1;
        for (int i = 1; i < m; i++) {

            if (m % i == 0) {
                nbDiviseur++;
            }
        }
        return nbDiviseur;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Calculator needs exactly 1 parameter to start : port of local machine.");
        }
        else {

            System.out.println("Attempting to start Web server ...");

            WebServer webServer = new WebServer(Integer.parseInt(args[0]));
            XmlRpcUtil.createXmlRpcServer(webServer, "CalculatorHandlers.properties");
            webServer.start();
            System.out.println("The server has been started successfully and is now accepting requests.");
            System.out.println("Halt program to stop server.");
        }
    }
}
