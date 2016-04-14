package calculator;

import org.apache.xmlrpc.webserver.WebServer;

public class CalculatorDetails {

	String address;
	int port;
	WebServer webServer;
	int nbCurrentRequest;
	int nbMaxRequest;
	
	public CalculatorDetails(String address, int port, WebServer webServer) {
		super();
		this.address = address;
		this.port = port;
		this.webServer = webServer;
		this.nbCurrentRequest = 0;
		this.nbMaxRequest = 50;
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

	public int getNbCurrentRequest() {
		return nbCurrentRequest;
	}

	public void incrRequest() {
		this.nbCurrentRequest++;
	}
	
	public void decrRequest() {
		this.nbCurrentRequest--;
	}

	public int getNbMaxRequest() {
		return nbMaxRequest;
	}	
}
