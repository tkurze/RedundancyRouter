package gt.redundancyrouter.chefMgmt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import gt.redundancyrouter.BasicManager;
import gt.redundancyrouter.chefMgmt.knife.KnifeController;
import gt.redundancyrouter.chefMgmt.knife.KnifeController.KnifeControllerException;
import gt.redundancyrouter.chefMgmt.knife.KnifeController.KnifeOperatingSystem;
import gt.redundancyrouter.dataService.resources.credentials.LoginCredentials;
import gt.redundancyrouter.dataService.resources.credentials.LoginCredentialsPassword;
import gt.redundancyrouter.dataService.resources.credentials.LoginCredentialsPrivateKey;
import gt.redundancyrouter.resourceMgmt.iaas.Node;

import org.apache.log4j.Logger;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jdom2.JDOMException;

/**
 * 
 * @author tobias
 * 
 */

@XmlRootElement
public class ChefNodeManager extends BasicManager<Node> {

	private static Logger log = Logger.getLogger("ChefNodeManager");

	public static final String DEFAULT_CHEF_HOST = "s15902927.onlinehome-server.info";
	public static final int DEFAULT_CHEF_PORT = 4000;
	public static final LoginCredentials DEFAULT_CHEF_CREDENTIALS = new LoginCredentialsPassword(
			"chef host login creds", "root", "redundant");

	final private String chefHost;
	final private LoginCredentials chefHostCredentials;

	private boolean useRemoteKnife = true;

	protected static ChefNodeManager theChefManager = null;

	public static synchronized void setChefNodeManager(
			ChefNodeManager chefNodeManager) {
		ChefNodeManager.theChefManager = chefNodeManager;
	}

	public static synchronized ChefNodeManager getChefNodeManager() {
		return ChefNodeManager.getChefNodeManager(null);
	}

	/**
	 * Creates a {@link ChefNodeManager} instance that expects knife to be
	 * configured on the local machine
	 * 
	 * @return ChefNodeManagement
	 */
	public static synchronized ChefNodeManager getChefNodeManager(String name) {
		if (ChefNodeManager.theChefManager == null)
			ChefNodeManager.theChefManager = new ChefNodeManager(name);
		return ChefNodeManager.theChefManager;
	}

	protected ChefNodeManager() {
		super(null);
		this.chefHost = DEFAULT_CHEF_HOST;
		this.chefHostCredentials = DEFAULT_CHEF_CREDENTIALS;
	}

	/**
	 * Creates a {@link ChefNodeManager} instance that expects knife to be
	 * configured on the local machine
	 */
	protected ChefNodeManager(String name) {
		super(name);
		this.chefHost = DEFAULT_CHEF_HOST;
		this.chefHostCredentials = DEFAULT_CHEF_CREDENTIALS;
	}

	/**
	 * Creates a {@link ChefNodeManager} instance that can handle local knife
	 * installations, as well as remote knife installations
	 * 
	 * @param knifeHost
	 *            host where knife is installed and configured
	 * @param knifeHostCredentials
	 *            {@link LoginCredentials} to connect to the host
	 */
	public ChefNodeManager(String chefHost, LoginCredentials chefHostCredentials) {
		super(null);
		this.chefHost = chefHost;
		this.chefHostCredentials = chefHostCredentials;
	}

	public boolean useRemoteKnife() {
		return useRemoteKnife;
	}

	public void useRemoteKnife(boolean useRemoteKnife) {
		this.useRemoteKnife = useRemoteKnife;
	}

	public List<String> bootstrapNodes(List<String> chefNodeNames,
			List<NodeMetadata> nodes, LoginCredentials nodeCredentials,
			OperatingSystem operatingSystem) {
		
		List<OperatingSystem> os = new LinkedList<OperatingSystem>();
		List<LoginCredentials> nc = new LinkedList<LoginCredentials>();
		
		for(@SuppressWarnings("unused") String nodeName : chefNodeNames){
			os.add(operatingSystem);
			nc.add(nodeCredentials);
		}
		
		return this.bootstrapNodes(chefNodeNames, nodes, nc, os);
	}
	
	public List<String> bootstrapNodes(List<String> chefNodeNames,
			List<NodeMetadata> nodes, List<LoginCredentials> nodeCredentials,
			List<OperatingSystem> operatingSystem) {
		
		final List<String> result = new LinkedList<String>();

		final Thread[] ts = new Thread[chefNodeNames.size()]; 
		final ChefNodeBootstrapRunner[] chefRunners = new ChefNodeBootstrapRunner[chefNodeNames.size()];
		
		for (int i = 0; i < chefNodeNames.size(); i++) {

			chefRunners[i] = new ChefNodeBootstrapRunner(chefNodeNames.get(i),
					nodes.get(i), nodeCredentials.get(i), operatingSystem.get(i));

			ts[i] = new Thread(chefRunners[i]);
			ts[i].start();
		}
		
		for(Thread t: ts){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for(ChefNodeBootstrapRunner br : chefRunners){
			result.add(br.getResult());
		}

		return result;
	}

	/**
	 * If knife is installed and configured on the local machine this function
	 * can be used to bootstrap a node.
	 * 
	 * /etc/chef/validation.pem needs to be present as well!
	 * 
	 * @param chefNodeName
	 * @param node
	 * @param nodeCredentials
	 */
	public String bootstrapNode(String chefNodeName, NodeMetadata node,
			LoginCredentials nodeCredentials, OperatingSystem operatingSystem) {

		KnifeOperatingSystem os = this.osName2knifeOs(operatingSystem
				.getFamily().name());

		String ret = null;

		Boolean useIdentityFile = null;
		String credentials = null;
		String userName = null;

		Boolean doSudo = true; // for the moment we assume, that sudo is
								// necessary

		String publicAddress = null;
		for (String a : node.getPublicAddresses()) {
			publicAddress = a;
		}

		if (nodeCredentials instanceof LoginCredentialsPassword) {
			useIdentityFile = false;
			credentials = ((LoginCredentialsPassword) nodeCredentials)
					.getPassword();
		} else if (nodeCredentials instanceof LoginCredentialsPrivateKey) {
			useIdentityFile = true;
			credentials = ((LoginCredentialsPrivateKey) nodeCredentials)
					.getKey().getKeyPath();
		}
		userName = nodeCredentials.getUsername();

		try {
			KnifeController knife;
			// KnifeCOntroller without parameters: local knife
			File remoteCreds = File.createTempFile("creds", ".tmp");
			if (this.useRemoteKnife) {
				knife = new KnifeController(this.chefHost,
						this.chefHostCredentials);
				if (useIdentityFile) {
					gt.redundancyrouter.Utils.copyFileToHost(new File(
							credentials), knife.getKnifeHost(), knife
							.getKnifeHostCredentials(), remoteCreds);
					credentials = remoteCreds.getAbsolutePath();
				}
			}
			// KnifeCOntroller with parameters: remote knife
			else
				knife = new KnifeController();

			int result = knife.bootstrap(publicAddress, credentials, userName,
					doSudo, false, os, chefNodeName, useIdentityFile);

			if (useIdentityFile && this.useRemoteKnife) {
				gt.redundancyrouter.Utils.deleteFileOnHost(
						knife.getKnifeHost(), knife.getKnifeHostCredentials(),
						remoteCreds);
			}

			if (result == 0) {
				ret = chefNodeName;
				log.info("bootstrapping successfull");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KnifeControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	private KnifeOperatingSystem osName2knifeOs(String osName) {
		if (osName.equalsIgnoreCase("ubuntu"))
			return KnifeOperatingSystem.ubuntu;

		return KnifeOperatingSystem.unknown;
	}

	public String getChefHost() {
		return chefHost;
	}

	public LoginCredentials getChefHostCredentials() {
		return chefHostCredentials;
	}

	@Override
	public void loadConfig(File f) {
		File configFile = new File(f, this.getName());
		if (!configFile.exists())
			return;
		List<String> childStrings = null;
		try {
			childStrings = this.getXmlChildStrings(configFile);
		} catch (JDOMException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(Node.class);

			for (String xml : childStrings) {
				final Node node = (Node) jaxbContext.createUnmarshaller()
						.unmarshal(new StringReader(xml));
				this.addManagedObject(node);
			}

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected class ChefNodeBootstrapRunner implements Runnable {

		private String result = null;

		private final String chefNodeName;
		private final NodeMetadata node;
		private final LoginCredentials nodeCredentials;
		private final OperatingSystem operatingSystem;

		public ChefNodeBootstrapRunner(String chefNodeName, NodeMetadata node,
				LoginCredentials nodeCredentials,
				OperatingSystem operatingSystem) {
			this.chefNodeName = chefNodeName;
			this.node = node;
			this.nodeCredentials = nodeCredentials;
			this.operatingSystem = operatingSystem;

		}

		@Override
		public void run() {
			this.result = bootstrapNode(this.chefNodeName, this.node,
					this.nodeCredentials, this.operatingSystem);
		}
		
		public String getResult(){
			return this.result;
		}

	}

}
