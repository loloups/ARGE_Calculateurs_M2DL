package utils;

public class Image {
	
	public static final int NB_MAX_REQUEST = 100;
	
	private String address;
	private int port;
	private int nbRequest;
	private String id;
	
	public Image(String id) {
		super();
		this.address = null;
		this.port = 0;
		this.id = id;
		this.nbRequest = 0;
	}
	
	
	public Image(String address, int port, String id) {
		super();
		this.address = address;
		this.port = port;
		this.id = id;
		this.nbRequest = 0;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public int getNbRequest() {
		return nbRequest;
	}


	public void setNbRequest(int nbRequest) {
		this.nbRequest = nbRequest;
	}
	
}
