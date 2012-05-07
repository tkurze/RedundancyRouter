package gt.redundancyrouter.management;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jdom2.JDOMException;

import gt.redundancyrouter.BasicManager;
import gt.redundancyrouter.resources.ResourceFabric;
import gt.redundancyrouter.resources.ResourceFabric.ResourceFabricException;
import gt.redundancyrouter.resources.credentials.PrivateKey;
import gt.redundancyrouter.resources.iaas.Node;
import gt.redundancyrouter.resources.iaas.compute.*;
import gt.redundancyrouter.resources.provider.AbstractProvider;
import gt.redundancyrouter.resources.template.AbstractTemplate;

@XmlRootElement
public class NodeManager extends BasicManager<Node> {

	private static NodeManager theNodeManager = null;


	public static synchronized void setResourceManager(
			NodeManager resourceManager) {
		NodeManager.theNodeManager = resourceManager;
	}

	public static synchronized NodeManager getResourceManager() {
		return NodeManager.getNodeManager(null);
	}

	public static synchronized NodeManager getNodeManager(String name) {
		if (NodeManager.theNodeManager == null)
			NodeManager.theNodeManager = new NodeManager(name);
		return NodeManager.theNodeManager;
	}

	private NodeManager(String name) {
		super(name);
	}

	public ComputeNode allocateAndAddComputeNode(AbstractProvider provider,
			AbstractTemplate template) {

		ComputeNodeFabric fab = ComputeNodeFabric.getComputeNodeFabric();
		ComputeNode res;
		try {

			res = (ComputeNode) fab.build(provider,
					provider.getProviderCredentials(), template);
			this.managededObjects.put(res.getName(), res);
			return res;
		} catch (ResourceFabricException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void shutdown() {
		// TODO release resources (shutdown VMs, etc.)
		// ...
		this.managededObjects.clear();
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
			jaxbContext = JAXBContext.newInstance(ComputeNode.class);

			for (String xml : childStrings) {
				final Node node = (Node) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(xml));
				this.addManagedObject(node);
			}

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
