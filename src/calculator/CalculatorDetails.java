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
		this.nbMaxRequest = 100;
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
		synchronized (Integer.valueOf(this.nbCurrentRequest)) {
			this.nbCurrentRequest++;
		}
	}
	
	public void decrRequest() {
		synchronized (Integer.valueOf(this.nbCurrentRequest)) {
			this.nbCurrentRequest--;
		}
	}

	public int getNbMaxRequest() {
		return nbMaxRequest;
	}	
}
