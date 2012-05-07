package gt.redundancyrouter;

import gt.redundancyrouter.Configuration.ConfigurationManagerException;
import gt.redundancyrouter.resources.NodeManager;
import gt.redundancyrouter.resources.credentials.LoginCredentialsManager;
import gt.redundancyrouter.resources.iaas.compute.ComputeNode;
import gt.redundancyrouter.resources.provider.AbstractProvider;
import gt.redundancyrouter.resources.provider.ProviderManager;
import gt.redundancyrouter.resources.template.AbstractTemplate;
import gt.redundancyrouter.resources.template.TemplateManager;
import gt.redundancyrouter.utils.Delegate;
import gt.redundancyrouter.utils.Runner;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class CloudEnvironmentBuilder {

	public static long DEFAULT_WAITING_TIME_MIN = 2;

	public static void buildEnvironment(File f) throws JDOMException,
			IOException {

		final SAXBuilder parser = new SAXBuilder();
		// final XMLOutputter printer = new XMLOutputter();

		final List<String> providersInEnvironment = new LinkedList<String>();
		final List<String> nodeTemplatesInEnvironment = new LinkedList<String>();
		final List<String> credentialsInEnvironment = new LinkedList<String>();

		Document doc = parser.build(f);
		final String name = doc.getRootElement().getAttribute("name")
				.getValue();
		if (name != null) {
			System.out.println(name);
		}

		List<Element> nodes = doc.getRootElement().getChildren();
		for (Element node : nodes) {
			providersInEnvironment.add(node.getChild("provider").getValue());
			nodeTemplatesInEnvironment
					.add(node.getChild("template").getValue());
			credentialsInEnvironment.add(node.getChild("credentials")
					.getValue());
		}

		// check if the cloud environment specifications file is valid...
		try {
			if (!CloudEnvironmentBuilder.providerCheck(providersInEnvironment)) {
				System.err.println("unknown provider(s) specified!");
				Configuration config = Configuration.getConfiguration();
				ManagerFabric mf = ManagerFabric.getManagerFabric(config);
				System.out.println("Available providers are:");
				mf.getProviderManager().printManagedObjects(System.out);
				System.out.println("quitting");
				return;
			}
			if (!CloudEnvironmentBuilder
					.nodeTemplateCheck(nodeTemplatesInEnvironment)) {
				System.err.println("unknown template(s) specified!");
				Configuration config = Configuration.getConfiguration();
				ManagerFabric mf = ManagerFabric.getManagerFabric(config);
				System.out.println("Available templates are:");
				mf.getTemplateManager().printManagedObjects(System.out);
				System.out.println("quitting");
				return;
			}

			if (!CloudEnvironmentBuilder
					.credentialsCheck(credentialsInEnvironment)) {
				System.err.println("unknown credential(s) specified!");
				Configuration config = Configuration.getConfiguration();
				ManagerFabric mf = ManagerFabric.getManagerFabric(config);
				System.out.println("Available credentials are:");
				mf.getLoginCredentialManager().printManagedObjects(System.out);
				System.out.println("quitting");
				return;
			}
		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			return;
		}

		// if we reched this point the configuration file is OK and all
		// requested resources are available

		// start the nodes
		try {
			if ((CloudEnvironmentBuilder.startNodesInParallel(nodes)) != null)
				System.out.println("All went fine!");
			else {
				System.err
						.println("There was a problem creating the Cloud Environment!");
				System.err
						.println("The environment might have been partially created!");
			}
		} catch (ConfigurationManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Serially starts new nodes to create the specified cloud environment. This
	 * implementation doesn't use threads!
	 * 
	 * @param nodes
	 * @return
	 * @throws ConfigurationManagerException
	 */
	@SuppressWarnings("unused")
	private static List<ComputeNode> startNodes(List<Element> nodes)
			throws ConfigurationManagerException {
		final Configuration config = Configuration.getConfiguration();
		final ManagerFabric mf = ManagerFabric.getManagerFabric(config);
		final TemplateManager tm = mf.getTemplateManager();
		final ProviderManager pm = mf.getProviderManager();
		final NodeManager nm = mf.getNodeManager();

		List<ComputeNode> startedNodes = new LinkedList<ComputeNode>();
		String providerName, templateName;
		for (Element node : nodes) {
			providerName = node.getChild("provider").getValue();
			templateName = node.getChild("template").getValue();
			startedNodes.add(nm.allocateAndAddComputeNode(
					pm.getManagedObject(providerName),
					tm.getManagedObject(templateName)));
		}

		return startedNodes;
	}

	/**
	 * Starts new nodes in parallel, to create the specified cloud environment. This
	 * implementation uses threads organized in a thread pool!
	 * 
	 * @param nodes
	 * @return
	 * @throws ConfigurationManagerException
	 */
	private static List<ComputeNode> startNodesInParallel(List<Element> nodes)
			throws ConfigurationManagerException {

		final Configuration config = Configuration.getConfiguration();
		final ManagerFabric mf = ManagerFabric.getManagerFabric(config);
		final TemplateManager tm = mf.getTemplateManager();
		final ProviderManager pm = mf.getProviderManager();
		final NodeManager nm = mf.getNodeManager();

		String providerName, templateName;

		Delegate<ComputeNode> d = new Delegate<ComputeNode>() {
			public ComputeNode invoke(Object... params) {
				return nm.allocateAndAddComputeNode(
						(AbstractProvider) params[0],
						(AbstractTemplate) params[1]);
			}
		};

		final ExecutorService executor = Executors.newCachedThreadPool();
		ExecutorCompletionService<ComputeNode> execComplService = new ExecutorCompletionService<ComputeNode>(
				executor);
		try {
			for (Element node : nodes) {
				providerName = node.getChild("provider").getValue();
				templateName = node.getChild("template").getValue();

				Runner<ComputeNode> computeNodesStarter = new Runner<ComputeNode>(
						d);
				computeNodesStarter.execute(execComplService,
						pm.getManagedObject(providerName),
						tm.getManagedObject(templateName));
			}

			List<ComputeNode> startedNodes = new LinkedList<ComputeNode>();
			for (int i = 0; i < nodes.size(); i++) {
				try {
					Future<ComputeNode> task = execComplService.poll(
							CloudEnvironmentBuilder.DEFAULT_WAITING_TIME_MIN,
							TimeUnit.MINUTES);
					startedNodes.add(task.get());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return startedNodes;
		} catch (Exception e) {

		} finally {
			executor.shutdownNow();
		}
		return null;
	}

	private static boolean providerCheck(List<String> providers)
			throws ConfigurationManagerException {
		Configuration config = Configuration.getConfiguration();
		ManagerFabric mf = ManagerFabric.getManagerFabric(config);
		ProviderManager pm = mf.getProviderManager();
		return pm.getManagedObjects().containsAll(providers);
	}

	private static boolean nodeTemplateCheck(List<String> nodeTemplates)
			throws ConfigurationManagerException {
		Configuration config = Configuration.getConfiguration();
		ManagerFabric mf = ManagerFabric.getManagerFabric(config);
		TemplateManager tm = mf.getTemplateManager();
		return tm.getManagedObjects().containsAll(nodeTemplates);
	}

	private static boolean credentialsCheck(List<String> credentials)
			throws ConfigurationManagerException {
		Configuration config = Configuration.getConfiguration();
		ManagerFabric mf = ManagerFabric.getManagerFabric(config);
		LoginCredentialsManager cm = mf.getLoginCredentialManager();
		return cm.getManagedObjects().containsAll(credentials);
	}

	public static void main(String[] args) {
		try {
			CloudEnvironmentBuilder.buildEnvironment(new File(
					"/home/tobias/.redundancyRouter/test.xml"));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
