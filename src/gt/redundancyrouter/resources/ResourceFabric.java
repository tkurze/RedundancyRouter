package gt.redundancyrouter.resources;

import gt.redundancyrouter.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.resources.provider.AbstractProvider;
import gt.redundancyrouter.resources.template.AbstractTemplate;

public abstract class ResourceFabric {

	abstract protected AbstractResource allocateResource(AbstractProvider provider, ProviderCredentials creds, AbstractTemplate template) throws ResourceFabricException;
	abstract protected void configureResource(AbstractResource res) throws ResourceFabricException;
	
	public AbstractResource build(AbstractProvider provider, ProviderCredentials creds, AbstractTemplate template) throws ResourceFabricException{
		AbstractResource res = allocateResource(provider, creds, template);
		configureResource(res);
		return res;
	}
	
	public class ResourceFabricException extends Exception{
		private static final long serialVersionUID = 1L;
		
		public ResourceFabricException(Exception e){
			super(e);
		}
		public ResourceFabricException(String msg){
			super(msg);
		}
		
	}
}
