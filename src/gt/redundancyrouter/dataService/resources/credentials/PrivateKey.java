package gt.redundancyrouter.dataService.resources.credentials;

import gt.redundancyrouter.resourceMgmt.AbstractResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PrivateKey extends AbstractResource {

	private String keyFile;
	private String passphrase = null;

	public PrivateKey(String name, String keyPath) {
		super(name);
		this.keyFile = keyPath;
	}

	private PrivateKey() {
		super(null);
	}

	public PrivateKey(String name, String keyFile, String passphrase) {
		super(name);
		this.keyFile = keyFile;
		this.passphrase = passphrase;
	}

	@XmlElement
	public String getKeyPath() {
		return this.keyFile;
	}

	public void setKeyPath(String keyPath) {
		this.keyFile = keyPath;
	}

	@XmlElement
	public String getPassphrase() {
		return this.passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}
}
