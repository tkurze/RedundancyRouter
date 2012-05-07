package gt.redundancyrouter;

import gt.redundancyrouter.management.NodeManager;
import gt.redundancyrouter.management.ProviderManager;
import gt.redundancyrouter.management.TemplateManager;
import gt.redundancyrouter.management.chef.ChefNodeManager;
import gt.redundancyrouter.management.credentials.LoginCredentialsManager;
import gt.redundancyrouter.management.credentials.PrivateKeyManager;

public class ManagerFabric {

	public static final String CHEF_NODE_MANAGER_NAME = "chefNodeManager";
	public static final String KEY_MANAGER_NAME = "keyManager";
	public static final String LOGIN_CREDENTIAL_MANAGER_NAME = "loginCredentialManager";
	public static final String PROVIDER_MANAGER_NAME = "providerManager";
	public static final String TEMPLATE_MANAGER_NAME = "templateManager";
	public static final String NODE_MANAGER_NAME = "nodeManager";

	public static final String[] MANAGER_NAMES = { CHEF_NODE_MANAGER_NAME,
			KEY_MANAGER_NAME, LOGIN_CREDENTIAL_MANAGER_NAME,
			PROVIDER_MANAGER_NAME, TEMPLATE_MANAGER_NAME, NODE_MANAGER_NAME };

	private static ManagerFabric theManagerFabric = null;

	private final Configuration configManager;

	public static ManagerFabric getManagerFabric(
			Configuration configManager) {
		if (ManagerFabric.theManagerFabric == null)
			ManagerFabric.theManagerFabric = new ManagerFabric(configManager);
		return ManagerFabric.theManagerFabric;
	}

	private ManagerFabric(Configuration configManager) {
		this.configManager = configManager;
	}

	public ChefNodeManager getChefNodeManager() {
		BasicManager<?> m = this.configManager
				.getManager(CHEF_NODE_MANAGER_NAME);
		if (m != null)
			return (ChefNodeManager) m;
		m = ChefNodeManager.getChefNodeManager(CHEF_NODE_MANAGER_NAME);
		this.configManager.addManager(m, m.getName());
		return (ChefNodeManager) m;
	}

	public PrivateKeyManager getKeyManager() {
		BasicManager<?> m = this.configManager
				.getManager(KEY_MANAGER_NAME);
		if (m != null)
			return (PrivateKeyManager) m;
		m = PrivateKeyManager.getPrivateKeyManager(KEY_MANAGER_NAME);
		this.configManager.addManager(m, m.getName());
		return (PrivateKeyManager) m;
	}

	public LoginCredentialsManager getLoginCredentialManager() {
		BasicManager<?> m = this.configManager
				.getManager(LOGIN_CREDENTIAL_MANAGER_NAME);
		if (m != null)
			return (LoginCredentialsManager) m;
		m = LoginCredentialsManager
				.getLoginCredentialsManager(LOGIN_CREDENTIAL_MANAGER_NAME);
		this.configManager.addManager(m, m.getName());
		return (LoginCredentialsManager) m;
	}

	public ProviderManager getProviderManager() {
		BasicManager<?> m = this.configManager
				.getManager(PROVIDER_MANAGER_NAME);
		if (m != null)
			return (ProviderManager) m;
		m = ProviderManager.getProviderManager(PROVIDER_MANAGER_NAME);
		this.configManager.addManager(m, m.getName());
		return (ProviderManager) m;
	}

	public TemplateManager getTemplateManager() {
		BasicManager<?> m = this.configManager
				.getManager(TEMPLATE_MANAGER_NAME);
		if (m != null)
			return (TemplateManager) m;
		m = TemplateManager.getTemplateManager(TEMPLATE_MANAGER_NAME);
		this.configManager.addManager(m, m.getName());
		return (TemplateManager) m;
	}

	public NodeManager getNodeManager() {
		BasicManager<?> m = this.configManager
				.getManager(NODE_MANAGER_NAME);
		if (m != null)
			return (NodeManager) m;
		m = NodeManager.getNodeManager(NODE_MANAGER_NAME);
		this.configManager.addManager(m, m.getName());
		return (NodeManager) m;
	}
	
	public BasicManager<?> getManagerByDefaultName(String name){
		if(name.equalsIgnoreCase(CHEF_NODE_MANAGER_NAME))
			return this.getChefNodeManager();
		if(name.equalsIgnoreCase(KEY_MANAGER_NAME))
			return this.getKeyManager();
		if(name.equalsIgnoreCase(LOGIN_CREDENTIAL_MANAGER_NAME))
			return this.getLoginCredentialManager();
		if(name.equalsIgnoreCase(PROVIDER_MANAGER_NAME))
			return this.getProviderManager();
		if(name.equalsIgnoreCase(TEMPLATE_MANAGER_NAME))
			return this.getTemplateManager();
		if(name.equalsIgnoreCase(NODE_MANAGER_NAME))
			return this.getNodeManager();
		return null;
	}

	/*
	 * old fancy code to create basic managers, due to JAXB issues I had to
	 * rewrite the code. Now it's less fancy, but should work
	 * 
	 * @SuppressWarnings("unchecked") private <T> BasicManager<T>
	 * getBasicManager(String managerName, Class<T> c) { Manager m =
	 * this.configManager.getManager(managerName); if (m != null) return
	 * (BasicManager<T>) m; BasicManager<T> bm = new
	 * BasicManager<T>(managerName); this.configManager.registerManager(bm);
	 * return bm; }
	 */

}
