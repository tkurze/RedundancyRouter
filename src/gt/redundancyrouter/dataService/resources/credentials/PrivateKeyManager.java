package gt.redundancyrouter.dataService.resources.credentials;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;

import gt.redundancyrouter.BasicManager;
import gt.redundancyrouter.dataService.serialization.xml.SaveXmlToFile;

@XmlRootElement
public class PrivateKeyManager extends BasicManager<PrivateKey> {

	protected static PrivateKeyManager thePrivateKeyManager = null;

	public static synchronized void setPrivateKeyManager(
			PrivateKeyManager privateKeyManager) {
		PrivateKeyManager.thePrivateKeyManager = privateKeyManager;
	}

	public static synchronized PrivateKeyManager getPrivateKeyManager(
			String name) {
		if (PrivateKeyManager.thePrivateKeyManager == null)
			PrivateKeyManager.thePrivateKeyManager = new PrivateKeyManager(name);
		return PrivateKeyManager.thePrivateKeyManager;
	}

	protected PrivateKeyManager() {
		super(null);
	}

	public PrivateKeyManager(String name) {
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
			jaxbContext = JAXBContext.newInstance(PrivateKey.class);

			for (String xml : childStrings) {
				final PrivateKey key = (PrivateKey) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(xml));
				this.addManagedObject(key);
			}

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//
//	@Override
//	public void saveConfig(File f) {
//		File configFile = new File(f, this.getName());
//		StringBuffer buf = new StringBuffer();
//		for (PrivateKey k : this.managededObjects.values()) {
//			try {
//				buf.append(k.serializeXML());
//			} catch (JAXBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		try {
//			FileUtils.writeStringToFile(configFile,
//					this.addRootTags(buf.toString()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
