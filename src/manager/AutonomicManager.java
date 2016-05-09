package manager;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.webserver.WebServer;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Image;
import org.openstack4j.openstack.OSFactory;

import utils.XmlRpcUtil;

public class AutonomicManager {
	
	private Set<utils.Image> images;
	private String id;
	
	public AutonomicManager() {
		this.images = new HashSet<>();
	}
	
	public Set<utils.Image> getImages() {
		return images;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void incr(String address, int port) {
		for(utils.Image image: this.getImages()) {
			if(image.getId().equals(id) && image.getAddress()==null) {
				image.setAddress(address);
				image.setPort(port);
				image.setNbRequest(image.getNbRequest()+1);
				break;
			}
			if(address.equals(image.getAddress())) {
				image.setNbRequest(image.getNbRequest()+1);
				break;
			}
		}
		
	}

	public void decr(String address, int port) {
		for(utils.Image image: this.getImages()) {
			if(address.equals(image.getAddress())) {
				image.setNbRequest(image.getNbRequest()-1);
				break;
			}
		}	
	}
	


	public static void main(String args []) {
		
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
		OSClient os = OSFactory.builder()
	            .endpoint("http://195.220.53.61:5000/v2.0")
	            .credentials("ens30","74J2O1")
	            .tenantName("service")
	            .authenticate();
		
		// List all Images (detailed @see #list(boolean detailed) for brief)
		List<? extends Image> images = os.compute().images().list();
		for(Image image : images) {
			if("Moskaland-Ubunutu".equals(image.getName())){
				manager.setId(image.getId());
				manager.getImages().add(new utils.Image(image.getId()));
				break;
			}
		}
		
		while(true) {
			
			int nbsatures=0;
			int nbInsatures = 0;
			for(utils.Image image : manager.getImages()) {
				if(image.getNbRequest() >= 0.90 * utils.Image.NB_MAX_REQUEST){
					nbsatures++;
				}else if(image.getNbRequest() < 0. * utils.Image.NB_MAX_REQUEST){
					if(!image.getId().equals(manager.id)) {
						nbInsatures--;
					}
				}
				
			}
			
			if(nbsatures == manager.getImages().size()){
				//Je cree un VM
			} else if(nbInsatures >= 1) {
				//Je del une vm 
			}
			
		}
		
		
	}

}
