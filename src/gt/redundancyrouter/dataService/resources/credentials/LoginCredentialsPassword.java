package gt.redundancyrouter.dataService.resources.credentials;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(LoginCredentials.class)
@XmlRootElement
public class LoginCredentialsPassword extends LoginCredentials {

	private String password;
	
	private LoginCredentialsPassword(){
		super(null,null);
	}
	
	public LoginCredentialsPassword(String name, String username, String password) {
		super(name, username);
		this.password = password;
	}

	@XmlElement
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
 