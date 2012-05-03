package gt.redundancyrouter.dataService.resources.provider;


import gt.redundancyrouter.dataService.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.dataService.resources.template.AbstractTemplate;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.jclouds.compute.domain.NodeMetadata;

/**
 * Class that represents a Cloud provider. Providers can be
 * added with {@link gt.redundancyrouter.dataService.InitData}
 * <p>
 * 
 * @author tobias
 * 
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION)
// @Extension(vendorName="datanucleus", key="mysql-engine-type", value="MyISAM")
// //don't use innoDB, problem with keys longer than 767 bytes (255xUTF8)
public abstract class CustomProvider extends AbstractProvider{

	@Persistent
	private String apiType; // amazonStyle, OpenStack style...

	@Persistent
	private String serviceEndpoint;

	@Persistent
	private Integer serviceEndpointPort;

	public Integer getServiceEndpointPort() {
		return serviceEndpointPort;
	}

	public void setServiceEndpointPort(Integer serviceEndpointPort) {
		this.serviceEndpointPort = serviceEndpointPort;
	}

	@Persistent
	private String costModels;

	@Persistent
	private String status; // available, unavailable, unknown, etc...

	public CustomProvider(String name, ProviderCredentials providerCredentials) {
		super(name, providerCredentials);
	}
	private CustomProvider(){
		super(null,null);
	}

	public abstract NodeMetadata createNode(AbstractTemplate t);
	
	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}

	public String getCostModels() {
		return costModels;
	}

	public void setCostModels(String costModels) {
		this.costModels = costModels;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
}
