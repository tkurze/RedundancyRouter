package gt.redundancyrouter.resourceMgmt.iaas;

import javax.xml.bind.annotation.XmlSeeAlso;

import gt.redundancyrouter.dataService.resources.provider.AbstractProvider;
import gt.redundancyrouter.resourceMgmt.AbstractResource;
import gt.redundancyrouter.resourceMgmt.iaas.compute.ComputeNode;

@XmlSeeAlso({ComputeNode.class})
public abstract class Node extends AbstractResource {
	
	protected AbstractProvider provider;
	
	private Node(){
		super(null);
	}
	
	protected Node(String name, AbstractProvider provider) {
		super(name);
		this.provider = provider;
		// TODO Auto-generated constructor stub
	}
	
	public AbstractProvider getProvider(){
		return provider;
	}

//	protected String address;
	
//	public String getNodeAddress(){
//		return this.address;
//	}
	


}
