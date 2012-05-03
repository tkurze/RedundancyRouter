package gt.redundancyrouter.dataService.resources.credentials;


import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 
 * @author tobias
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, objectIdClass = gt.redundancyrouter.dataService.resources.credentials.ComposedIdKey.class)
public class ProviderCredentials {

	private String name = null;
	
	/**
	 * Holds either the user's name or the user's access key
	 */
	@PrimaryKey
	@Column(length = 255)
	private String identity = null;

	/**
	 * Name of the corresponding provider (aws-ec2, openstack, 1&1, ...) 
	 */
	@PrimaryKey
	@Column(length = 255)
	private String provider = null;


	/**
	 * Holds either the user's password or the user's secret access key
	 */
	@Persistent
	private String credential = null;


	protected ProviderCredentials(){}
	
	
	public ProviderCredentials(String name, String identity, String credential) {
		super();
		this.name = name;
		this.identity = identity;
		this.credential = credential;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * @return user name respectively access key
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * Allows to set the identity
	 * @param identity user name or access key
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}

	/**
	 * @return password respectively secret access key
	 */
	public String getCredential() {
		return credential;
	}

	/**
	 * Allows to set the credential
	 * @param credential password or secret access key
	 */
	public void setCredential(String credential) {
		this.credential = credential;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
}
