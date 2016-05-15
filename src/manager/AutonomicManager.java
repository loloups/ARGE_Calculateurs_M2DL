package manager;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.webserver.WebServer;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.openstack.OSFactory;

import repartitor.UpdateRepartitor;
import utils.WorkerNode.State;
import utils.XmlRpcUtil;

public class AutonomicManager {

    private Set<utils.WorkerNode> images;
    private utils.WorkerNode      VM0;

    private String idImageCalc;

    public AutonomicManager() {
        idImageCalc = null;
        this.images = Collections.newSetFromMap(new ConcurrentHashMap<utils.WorkerNode, Boolean>());
    }

    public Set<utils.WorkerNode> getImages() {
        return images;
    }

    public utils.WorkerNode getVM0() {
        return VM0;
    }

    public void setVM0(utils.WorkerNode vM0) {
        VM0 = vM0;
    }

    public String getIdImageCalc() {
        return idImageCalc;
    }

    public void setIdImageCalc(String idImageCalc) {
        this.idImageCalc = idImageCalc;
    }
    
    public void incr(String address, int port) {
        for (utils.WorkerNode image : this.getImages()) {
            if (address.equals(image.getAddress())) {
                image.setNbRequest(image.getNbRequest() + 1);
                break;
            }
        }
    }

    public void decr(String address, int port) {
        for (utils.WorkerNode image : this.getImages()) {
            if (address.equals(image.getAddress())) {
                image.setNbRequest(image.getNbRequest() - 1);
                break;
            }
        }
    }

    public static void main(String args[]) {

        AutonomicManager manager = new AutonomicManager();

        WebServer webServer = new WebServer(8080);
        try {
            XmlRpcUtil.createXmlRpcServer(webServer, "AutonomicManagerHandlers.properties");
            webServer.start();
            System.out.println("The manager has been started successfully and is now accepting requests.");
            System.out.println("Halt program to stop server.");
        }
        catch (IOException | XmlRpcException e) {
            e.printStackTrace();
        }
        OSClient os = manager.connectCloudmip();

        // List all Images (detailed @see #list(boolean detailed) for brief)
        do {
            System.out.println("Searching for VMO");
            List<? extends Server> servers = os.compute().servers().list();

            for (Server server : servers) {
                if ("Moskaland-VM0".equals(server.getName())) {
                    if (server.getAddresses().getAddresses() != null) {
                        System.out.println("VMO found");
                        System.out.println(
                            "VMO address" + server.getAddresses().getAddresses().get("private").get(0).getAddr());
                        manager.setVM0(
                            new utils.WorkerNode(server.getAddresses().getAddresses().get("private").get(0).getAddr(),
                                2001, server.getId()));
                        break;
                    }
                }
            }
        }
        while (manager.getVM0() == null);

        manager.addVM(os);
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        while (true) {

            int nbsatures = 0;
            for (utils.WorkerNode image : manager.getImages()) {
                if (image.getNbRequest() >= 0.90 * utils.WorkerNode.NB_MAX_REQUEST) {
                    nbsatures++;
                }
                else if (image.getNbRequest() < 0.1 * utils.WorkerNode.NB_MAX_REQUEST) {
                    if (manager.getImages().size() > 1) {
                        if (State.ACTIVE.name().equals(image.getState().name())) {
                            image.setState(State.TO_DELETE);
                            String[] argsUpRep = { manager.getVM0().getAddress(),
                                Integer.toString(manager.getVM0().getPort()), "del", image.getAddress(),
                                Integer.toString(image.getPort()) };
                            UpdateRepartitor.main(argsUpRep);
                        }
                        if (State.TO_DELETE.name().equals(image.getState().name()) && image.getNbRequest() == 0) {
                            manager.deleteVM(image.getId(), os);
                        }
                    }
                }

            }

            if (nbsatures == manager.getImages().size()) {
                // Je cree un VM
                manager.addVM(os);
            }

            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void addVM(OSClient os) {

        if (getIdImageCalc() == null) {
            List<? extends Image> images = os.compute().images().list();
            for (Image image : images) {
                if ("MoskitoImageCalculator".equals(image.getName())) {
                    System.out.println("Image found");
                    setIdImageCalc(image.getId());
                    break;
                }
            }
        }

        // Create a Server Model Object
        List<String> networks = Arrays.asList("c1445469-4640-4c5a-ad86-9c0cb6650cca");

        String name = "Moskito_" + (new Date()).getTime();
        ServerCreate serverCreate = Builders.server().name(name).flavor("2")
            .image(getIdImageCalc()).networks(networks).build();

        // Boot and wait for the server
        Server server = os.compute().servers().bootAndWaitActive(serverCreate, 6000);
        System.out.println("WN created");

        // Wait server's boot end
        while (server.getAddresses().getAddresses().get("private") == null) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Get the address of the WN
        Map<String, List<? extends Address>> addresses = server.getAddresses().getAddresses();
        String addressServer = addresses.get("private").get(0).getAddr();

        String id = server.getId();
        images.add(new utils.WorkerNode(addressServer, 8080, id));
        System.out.println("Addition of calculator with port 8080 address " + addressServer + " and id " + id);
        String[] args = { getVM0().getAddress(), Integer.toString(getVM0().getPort()), "add", Integer.toString(8080),
            addressServer };
        UpdateRepartitor.main(args);

        // Wait for web server calculator start
        try {
            Thread.sleep(20000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authenticate to Cloudmip
     * 
     * @* @return OSClient
     */
    public OSClient connectCloudmip() {

        OSClient os = OSFactory.builder().endpoint("http://195.220.53.61:5000/v2.0").credentials("ens30", "74J2O1")
            .tenantName("service").authenticate();

        System.out.println("Connected to cloudmip");

        return os;
    }

    private void deleteVM(String id, OSClient os) {
        os.compute().servers().delete(id);
        System.out.println("Deletion of webserver " + id);
    }

}
