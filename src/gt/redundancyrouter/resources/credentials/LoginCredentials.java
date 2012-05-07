package gt.redundancyrouter.resources.credentials;


import gt.redundancyrouter.resources.AbstractResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({LoginCredentialsPassword.class,LoginCredentialsPrivateKey.class})
public abstract class LoginCredentials extends AbstractResource {
	
	protected String username;
	
	private LoginCredentials() {
		super(null);
	}
	
	public LoginCredentials(String name, String username) {
		super(name);
		this.username = username;
	}
	
	@XmlElement
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
