package calculator;

import org.apache.xmlrpc.webserver.WebServer;

public class CalculatorDetails {

	String address;
	int port;
	WebServer webServer;
	
	public CalculatorDetails(String address, int port, WebServer webServer) {
		super();
		this.address = address;
		this.port = port;
		this.webServer = webServer;
	}

	public WebServer getWebServer() {
		return webServer;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
}
