package gt.redundancyrouter.dataService.resources.provider;

import java.io.IOException;
import java.net.URISyntaxException;

import gt.redundancyrouter.dataService.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.dataService.resources.template.AbstractTemplate;
import gt.redundancyrouter.resourceMgmt.iaas.compute.Server1u1;

import org.apache.http.client.ClientProtocolException;
import org.jclouds.compute.domain.NodeMetadata;
import org.json.JSONException;

public class Provider1u1 extends CustomProvider {

	public Provider1u1(String name, ProviderCredentials providerCredentials) {
		super(name, providerCredentials);
	}
	
	public Provider1u1(String name){
		super(name,null);
	}

	private Provider1u1() {
		super(null,null);
	}

	@Override
	public NodeMetadata createNode(AbstractTemplate t) {
		try {
			//TODO use template to reconfigure a machine, or to select a more or less correctly configured machine!
			
			Client1u1 c = new Client1u1(this.providerCredentials.getIdentity(),
					this.providerCredentials.getCredential());
			String vmId = String.valueOf(this.getFreeServerId());
			if (!(c.getServerState(vmId) == Server1u1.ServerState.STOPPED))
				return null;
			log.info("starting 1&1 server with id: "+vmId);
			c.startServer(vmId);
			
			//TODO: Do all this with a future, etc.
			
			Server1u1.ServerState state = c.getServerState(vmId);
			System.out.println("state: "+state.getStateString());
//			while(state!=Server1u1.ServerState.RUNNING){
//				try {
//					Thread.sleep(2000);
//					state = c.getServerState(vmId);
//					System.out.println("state: "+state.getStateString());
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				log.info("slepping a bit...");
//			}
			
			this.markServerIdAsUsed(Integer.valueOf(vmId));
			return c.getServer(vmId);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int getFreeServerId() {
		
		return 53135;
	}
	
	public void markServerIdAsUsed(int id){
		
	}

}
