package utils;

public class WorkerNode {
	public enum State{ACTIVE,TO_DELETE};
		
	private String address;
	private int port;
	private String id;
	private State state;
	
	public WorkerNode(String id) {
		super();
		this.address = null;
		this.port = 0;
		this.id = id;
		this.state = State.ACTIVE;
	}
	
	
	public WorkerNode(String address, int port, String id) {
		super();
		this.address = address;
		this.port = port;
		this.id = id;
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

	public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}
}
