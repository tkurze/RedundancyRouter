package gt.redundancyrouter.dataService.resources.credentials;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.jdom2.JDOMException;

import gt.redundancyrouter.BasicManager;
import gt.redundancyrouter.Configuration;
import gt.redundancyrouter.Configuration.ConfigurationManagerException;
import gt.redundancyrouter.ManagerFabric;

public class LoginCredentialsManager extends BasicManager<LoginCredentials> {

	protected static LoginCredentialsManager theLoginCredentialsManager = null;

	public static synchronized void setLoginCredentialsManager(
			LoginCredentialsManager loginCredentialsManager) {
		LoginCredentialsManager.theLoginCredentialsManager = loginCredentialsManager;
	}

	public static synchronized LoginCredentialsManager getLoginCredentialsManager(
			String name) {
		if (LoginCredentialsManager.theLoginCredentialsManager == null)
			LoginCredentialsManager.theLoginCredentialsManager = new LoginCredentialsManager(
					name);
		return LoginCredentialsManager.theLoginCredentialsManager;
	}

	protected LoginCredentialsManager() {
		super(null);
	}

	public LoginCredentialsManager(String name) {
		super(name);
	}

	@Override
	public void loadConfig(File f) {
		File configFile = new File(f, this.getName());
		if (!configFile.exists())
			return;
		List<String> childStrings = null;
		try {
			childStrings = this.getXmlChildStrings(configFile);
		} catch (JDOMException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(
					LoginCredentialsPassword.class,
					LoginCredentialsPrivateKey.class);

			for (String xml : childStrings) {
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				unmarshaller.setEventHandler(new ValidationEventHandler() {
					public boolean handleEvent(ValidationEvent event) {
						throw new RuntimeException(event.getMessage(), event
								.getLinkedException());
					}
				});
				final LoginCredentials loginCreds = (LoginCredentials) unmarshaller
						.unmarshal(new StringReader(xml));
				if (loginCreds instanceof LoginCredentialsPrivateKey) {
					String keyName = ((LoginCredentialsPrivateKey) loginCreds)
							.getKeyName();
					Configuration config;
					try {
						config = Configuration.getConfigurationManager();
						ManagerFabric mf = ManagerFabric
								.getManagerFabric(config);
						PrivateKeyManager keyManager = mf.getKeyManager();
						((LoginCredentialsPrivateKey) loginCreds)
								.setKey(keyManager.getManagedObject(keyName));

					} catch (ConfigurationManagerException e) {
						e.printStackTrace();
					}
				}
				this.addManagedObject(loginCreds);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
