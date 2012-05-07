package gt.redundancyrouter.management;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.jdom2.JDOMException;

import gt.redundancyrouter.BasicManager;
import gt.redundancyrouter.resources.provider.AbstractProvider;
import gt.redundancyrouter.resources.provider.CustomProvider;
import gt.redundancyrouter.resources.provider.JCloudProvider;
import gt.redundancyrouter.resources.provider.Provider1u1;

public class ProviderManager extends BasicManager<AbstractProvider> {

	protected static ProviderManager theProviderManager = null;

	public static synchronized void setProviderManager(
			ProviderManager providerManager) {
		ProviderManager.theProviderManager = providerManager;
	}

	public static synchronized ProviderManager getProviderManager(String name) {
		if (ProviderManager.theProviderManager == null)
			ProviderManager.theProviderManager = new ProviderManager(name);
		return ProviderManager.theProviderManager;
	}

	protected ProviderManager() {
		super(null);
	}

	public ProviderManager(String name) {
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
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(Provider1u1.class,JCloudProvider.class);

			for (String xml : childStrings) {
				final AbstractProvider prov = (AbstractProvider) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(xml));
				this.addManagedObject(prov);
			}

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
