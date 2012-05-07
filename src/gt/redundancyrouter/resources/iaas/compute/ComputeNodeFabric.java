package gt.redundancyrouter.resources.iaas.compute;

//import org.jclouds.compute.ComputeService;
//import org.jclouds.compute.ComputeServiceContext;
//import org.jclouds.compute.ComputeServiceContextFactory;
//import org.jclouds.logging.log4j.config.Log4JLoggingModule;
//import org.jclouds.sshj.config.SshjSshClientModule;

//import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.apache.log4j.Logger;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;

import gt.redundancyrouter.resources.AbstractResource;
import gt.redundancyrouter.resources.ResourceFabric;
import gt.redundancyrouter.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.resources.provider.AbstractProvider;
import gt.redundancyrouter.resources.provider.CustomProvider;
import gt.redundancyrouter.resources.provider.JCloudProvider;
import gt.redundancyrouter.resources.template.AbstractTemplate;
import gt.redundancyrouter.resources.template.ComputeTemplate;

public class ComputeNodeFabric extends ResourceFabric {
	
	private static Logger log = Logger.getLogger("ComputeNodeFabric");

	public static final String DEFAULT_NODES_GROUP = "redundancyRouter";
	
	private static final ComputeNodeFabric theComputeNodeFabric= new ComputeNodeFabric();
	
	public static ComputeNodeFabric getComputeNodeFabric(){
		return ComputeNodeFabric.theComputeNodeFabric;
	}

	protected String nodesGroupName = null;
	
	
	public String getNodesGroupName() {
		return nodesGroupName;
	}


	public void setNodesGroupName(String nodeGroupName) {
		this.nodesGroupName = nodeGroupName;
	}


	protected ComputeNodeFabric() {
		super();
		this.nodesGroupName = DEFAULT_NODES_GROUP;

	}
	
	@Override
	protected ComputeNode allocateResource(AbstractProvider provider, ProviderCredentials creds,
			AbstractTemplate template) throws ResourceFabricException {
		ComputeNode node = null;

		if (!(template instanceof ComputeTemplate)) {
			throw new ResourceFabricException(
					"ComputeNodeFabric can only handle ComputeTemplates!");
		}
		
		ComputeTemplate t = (ComputeTemplate)template;

		if (provider instanceof JCloudProvider) {
			
			ComputeServiceContext compContext = new ComputeServiceContextFactory()
			.createContext(((JCloudProvider) provider).getjCloudProviderString(),
					creds.getIdentity(),
					creds.getCredential());

			TemplateBuilder tb = compContext.getComputeService().templateBuilder();
			Template jCloudsTemplate = t.buildJCloudsTemplate(tb);
			try {
				Set<? extends NodeMetadata> nodeMetaSet = compContext
						.getComputeService().createNodesInGroup(
								this.nodesGroupName, 1, jCloudsTemplate);

				// there should only be one obabsject in the set
				NodeMetadata n = nodeMetaSet.iterator().next();
				node = new ComputeNode(n.getName(), provider, n);

			} catch (RunNodesException e) {
				throw new ResourceFabricException(e);
			}

		} else if (provider instanceof CustomProvider) {
			
			NodeMetadata n = ((CustomProvider) provider).createNode(template);
			node = new ComputeNode(n.getName(), provider ,n);
		}
		return node;
	}

	protected void configureResource(ComputeNode res) {
		log.info("nothing to configure");

	}

	@Override
	protected void configureResource(AbstractResource res) {
		this.configureResource((ComputeNode) res);
	}

}
