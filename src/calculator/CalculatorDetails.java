package calculator;

public class CalculatorDetails {

    String address;
    int    port;

    public CalculatorDetails(String address, int port) {
        super();
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
