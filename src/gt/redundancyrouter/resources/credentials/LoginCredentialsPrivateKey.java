package gt.redundancyrouter.resources.credentials;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso(LoginCredentials.class)
@XmlRootElement
public class LoginCredentialsPrivateKey extends LoginCredentials {

	private PrivateKey key;
	
	private String keyName;
	
	private LoginCredentialsPrivateKey(){
		super(null,null);
	}
	
	public LoginCredentialsPrivateKey(String name, String username, PrivateKey key) {
		super(name, username);
		this.key = key;
		this.keyName = this.key.getName();
	}
	
	@XmlElement
	public String getKeyName(){
		return this.keyName;
	}
	
	
	@SuppressWarnings("unused")
	private void setKeyName(String keyName){
		this.keyName = keyName;
	}

	@XmlTransient
	public PrivateKey getKey() {
		return key;
	}

	public void setKey(PrivateKey key) {
		this.key = key;
	}
	
//	@XmlSeeAlso(LoginCredentialsPrivateKey.class)
//	@XmlRootElement
//	public static class LoginCredentialsPrivateKeyWrapper extends LoginCredentialsPrivateKey {
//		
//		private final String keyName;
//		
//		protected LoginCredentialsPrivateKeyWrapper(){
//			this.keyName = null;
//		}
//		
//		public LoginCredentialsPrivateKeyWrapper(LoginCredentialsPrivateKey lc){
//			this.keyName = lc.getKey().getName();
//		}
//		
//		@XmlElement
//		public String getKeyName(){
//			return this.keyName;
//		}
//	}
}
