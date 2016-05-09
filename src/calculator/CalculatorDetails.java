package calculator;

public class CalculatorDetails {

    String id;

    String address;
    int    port;

    public CalculatorDetails(String id, String address, int port) {
        super();
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getId() {
        return id;
    }
}
