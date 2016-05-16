package manager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.webserver.WebServer;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Image;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.openstack.OSFactory;

import repartitor.UpdateRepartitor;
import utils.WorkerNode;
import utils.WorkerNode.State;
import utils.XmlRpcUtil;

public class AutonomicManager {

    private Set<WorkerNode>        workerNodes;
    private WorkerNode             VM0;
    public static AutonomicManager manager;

    private String idImageCalc;

    public AutonomicManager() {
        idImageCalc = null;
        this.workerNodes = Collections.newSetFromMap(new ConcurrentHashMap<utils.WorkerNode, Boolean>());
    }

    public Set<utils.WorkerNode> getWorkerNodes() {
        return workerNodes;
    }

    public WorkerNode getVM0() {
        return VM0;
    }

    public void setVM0(WorkerNode vM0) {
        VM0 = vM0;
    }

    public String getIdImageCalc() {
        return idImageCalc;
    }

    public void setIdImageCalc(String idImageCalc) {
        this.idImageCalc = idImageCalc;
    }

    /**
     * Increments the number of requests of a worker node
     * 
     * @param address
     * @param port
     */
    public boolean incr(String address, int port) {

        for (WorkerNode image : manager.getWorkerNodes()) {
            if (address.equals(image.getAddress())) {
                image.setNbRequest(image.getNbRequest() + 1);
                break;
            }
        }
        return true;
    }

    /**
     * Decrements the number of requests of a worker node
     * 
     * @param address
     * @param port
     */
    public boolean decr(String address, int port) {
        for (WorkerNode image : manager.getWorkerNodes()) {
            if (address.equals(image.getAddress())) {
                image.setNbRequest(image.getNbRequest() - 1);
                break;
            }
        }
        return true;
    }

    /**
     * List all of the worker nodes
     * 
     * @param os
     * @param manager
     */
    public static void listWorkerNodes(OSClient os, AutonomicManager manager) {

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
                            new WorkerNode(server.getAddresses().getAddresses().get("private").get(0).getAddr(),
                                2001, server.getId()));
                        break;
                    }
                }
            }
        }
        while (manager.getVM0() == null);
    }

    /**
     * Handle the addition and the deletion of the worker nodes. It depends on
     * the number of requests handled by each worker node.
     * 
     * @param manager
     * @param os
     */
    public static void handleWN(OSClient os) {

        while (true) {

            int nbsatures = 0;
            for (WorkerNode image : manager.getWorkerNodes()) {

                System.out.println(image.getNbRequest());
                if (image.getNbRequest() >= 0.90 * WorkerNode.NB_MAX_REQUEST) {
                    nbsatures++;
                }
                else if (image.getNbRequest() < 0.1 * WorkerNode.NB_MAX_REQUEST) {
                    if (manager.getWorkerNodes().size() > 1) {

                        image.setState(State.TO_DELETE);
                        String[] argsUpRep = { manager.getVM0().getAddress(),
                            Integer.toString(manager.getVM0().getPort()), "del", image.getAddress(),
                            Integer.toString(image.getPort()) };
                        UpdateRepartitor.main(argsUpRep);

                        if (State.TO_DELETE.name().equals(image.getState().name()) && image.getNbRequest() == 0) {
                            manager.deleteVM(image.getId(), os);
                        }
                    }
                }

            }

            if (nbsatures == manager.getWorkerNodes().size()) {
                // Je cree un VM
                System.out.println("yolo");
                manager.addVM(os);
            }

            try {
                Thread.sleep(12000);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the web server
     */
    public static void startWebServer() {
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
    }

    /**
     * Add a new VM on the cloud
     * 
     * @param os
     */
    public void addVM(OSClient os) {

        // Get the image to use for the creation of the WN
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

        workerNodes.add(new WorkerNode(addressServer, 8080, id));
        System.out.println("Addition of calculator with port 8080 address " + addressServer + " and id " + id);

        String[] args = { getVM0().getAddress(), Integer.toString(getVM0().getPort()), "add", Integer.toString(8080),
            addressServer };
        UpdateRepartitor.main(args);
    }

    /**
     * Delete a VM on the cloud
     * 
     * @param id
     * @param os
     */
    private void deleteVM(String id, OSClient os) {

        os.compute().servers().delete(id);
        System.out.println("Deletion of webserver " + id);
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

    public static void main(String args[]) {

        manager = new AutonomicManager();

        startWebServer();

        OSClient os = manager.connectCloudmip();

        listWorkerNodes(os, manager);

        manager.addVM(os);

        handleWN(os);

    }

}
