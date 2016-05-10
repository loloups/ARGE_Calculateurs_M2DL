package manager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
import utils.Image.State;
import utils.XmlRpcUtil;

public class AutonomicManager {

	private Set<utils.Image> images;
	private utils.Image VM0;
	private OSClient os;

	public AutonomicManager() {
		this.images = Collections.newSetFromMap(new ConcurrentHashMap<utils.Image, Boolean>());
		this.os = connectCloudmip();
	}

	public Set<utils.Image> getImages() {
		return images;
	}

	public utils.Image getVM0() {
		return VM0;
	}

	public void setVM0(utils.Image vM0) {
		VM0 = vM0;
	}

	public OSClient getOs() {
		return os;
	}

	public void incr(String address, int port) {
		for (utils.Image image : this.getImages()) {
			if (address.equals(image.getAddress())) {
				image.setNbRequest(image.getNbRequest() + 1);
				break;
			}
		}

	}

	public void decr(String address, int port) {
		for (utils.Image image : this.getImages()) {
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
		} catch (IOException | XmlRpcException e) {
			e.printStackTrace();
		}
		OSClient os = OSFactory.builder().endpoint("http://195.220.53.61:5000/v2.0").credentials("ens30", "74J2O1")
				.tenantName("service").authenticate();

		// List all Images (detailed @see #list(boolean detailed) for brief)
		do {
			System.out.println("Searching for VMO");
			List<? extends Image> images = os.compute().images().list();
		
			for (Image image : images) {
				if ("Moskaland-Ubunutu".equals(image.getName())) {
					System.out.println("VMO found");
					manager.setVM0(new utils.Image(image.getId(),2001,args[0]));
					manager.getImages().add(new utils.Image(image.getId()));
					break;
				}
			}
		} while(manager.getVM0() == null);
		
		while (true) {

			int nbsatures = 0;
			for (utils.Image image : manager.getImages()) {
				if (image.getNbRequest() >= 0.90 * utils.Image.NB_MAX_REQUEST) {
					nbsatures++;
				} else if (image.getNbRequest() < 0.1 * utils.Image.NB_MAX_REQUEST) {
					if (manager.getImages().size() > 1) {
						if (State.ACTIVE.name().equals(image.getState().name())) {
							image.setState(State.TO_DELETE);
							String [] argsUpRep = {manager.getVM0().getAddress(), Integer.toString(manager.getVM0().getPort()), "del", image.getAddress(), Integer.toString(image.getPort()) };
							UpdateRepartitor.main(argsUpRep);
						}
						if (State.TO_DELETE.name().equals(image.getState().name()) && image.getNbRequest() == 0) {
							manager.deleteVM(image.getId());
						}
					}
				}

			}

			if (nbsatures == manager.getImages().size()) {
				// Je cree un VM
				manager.addVM();
			}

		}

	}

	public void addVM() {

		// Create a Server Model Object
		List<String> networks = Arrays.asList("c1445469-4640-4c5a-ad86-9c0cb6650cca");

		String name = "Moskito_" + images.size();
		ServerCreate serverCreate = Builders.server().name(name).flavor("2")
				.image("652e70e5-48d0-40d0-a725-5aa12680ba20").networks(networks).keypairName("MoskitoKey").build();

		// Boot and wait for the server
		Server server = os.compute().servers().bootAndWaitActive(serverCreate, 6000);
		System.out.println("WN created");

		// Get the address of the WN
		Map<String, List<? extends Address>> addresses = server.getAddresses().getAddresses();
		String addressServer = addresses.get("private").get(0).getAddr();

		String id = server.getId();
		images.add(new utils.Image(id, 8080, addressServer));
		System.out.println("Addition of calculator with port 8080 address " + addressServer + " and id " + id);
		String [] args = {getVM0().getAddress(), Integer.toString(getVM0().getPort()), "add", addressServer, Integer.toString(8080) };
		UpdateRepartitor.main(args);
	}

	/**
	 * Authenticate to Cloudmip
	 * 
	 * @return OSClient
	 */
	private OSClient connectCloudmip() {

		OSClient os = OSFactory.builder().endpoint("http://195.220.53.61:5000/v2.0").credentials("ens30", "74J2O1")
				.tenantName("service").authenticate();

		System.out.println("Connected to cloudmip");

		return os;
	}

	private void deleteVM(String id) {
		os.compute().servers().delete(id);
		System.out.println("Deletion of webserver " + id);
	}

}
