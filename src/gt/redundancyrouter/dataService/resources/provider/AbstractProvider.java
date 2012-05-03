package gt.redundancyrouter.dataService.resources.provider;

import javax.xml.bind.annotation.XmlSeeAlso;
import org.apache.log4j.Logger;


import gt.redundancyrouter.dataService.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.resourceMgmt.AbstractResource;

@XmlSeeAlso({CustomProvider.class,JCloudProvider.class})
public abstract class AbstractProvider extends AbstractResource {
	
	protected static Logger log = Logger.getLogger(AbstractProvider.class);
	
	protected ProviderCredentials providerCredentials;

	public void setProviderCredentials(ProviderCredentials providerCredentials) {
		this.providerCredentials = providerCredentials;
	}

	private AbstractProvider(){
		super(null);
	}
	
	public AbstractProvider(String name, ProviderCredentials providerCredentials) {
		super(name);
		this.providerCredentials = providerCredentials;
	}
	
	public ProviderCredentials getProviderCredentials() {
		return providerCredentials;
	}

	public String getName() {
		return this.name;
	}

}
