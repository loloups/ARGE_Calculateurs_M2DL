package server;

import org.apache.xmlrpc.webserver.WebServer;

import utils.XmlRpcUtil;

public class Server {

	private static final int port = 8080;

	public static void main(String[] args) throws Exception {
		System.out.println("Attenmpting to start Web server ...");

		WebServer webServer = new WebServer(port);
		XmlRpcUtil.createXmlRpcServer(webServer);
		webServer.start();

		System.out.println("The server has been started successfully and is now accepting requests.");
		System.out.println("Halt program to stop server.");
	}

}