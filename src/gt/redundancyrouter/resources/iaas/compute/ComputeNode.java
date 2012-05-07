package gt.redundancyrouter.resources.iaas.compute;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.compute.domain.NodeMetadata;

import gt.redundancyrouter.resources.iaas.Node;
import gt.redundancyrouter.resources.provider.AbstractProvider;

@XmlRootElement
public class ComputeNode extends Node {
	public ComputeNode(String name, AbstractProvider provider, NodeMetadata metadata) {
		super(name, provider);
		this.metadata = metadata;
	}
	
	protected NodeMetadata metadata;
	
	@XmlElement
	public NodeMetadata getMetadata() {
		return metadata;
	}
}
