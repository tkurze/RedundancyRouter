package gt.redundancyrouter.resources.template;

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

import org.jdom2.JDOMException;

import gt.redundancyrouter.BasicManager;
import gt.redundancyrouter.resources.credentials.PrivateKey;

@XmlRootElement
public class TemplateManager extends BasicManager<AbstractTemplate> {

	protected static TemplateManager theTemplateManager = null;

	public static synchronized void setTemplateManager(
			TemplateManager templateManager) {
		TemplateManager.theTemplateManager = templateManager;
	}

	public static synchronized TemplateManager getTemplateManager(String name) {
		if (TemplateManager.theTemplateManager == null)
			TemplateManager.theTemplateManager = new TemplateManager(name);
		return TemplateManager.theTemplateManager;
	}

	protected TemplateManager(){
		super(null);
	}
	
	public TemplateManager(String name) {
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
			jaxbContext = JAXBContext.newInstance(ComputeTemplate.class);

			for (String xml : childStrings) {
				final AbstractTemplate templ = (AbstractTemplate) jaxbContext
						.createUnmarshaller().unmarshal(new StringReader(xml));
				this.addManagedObject(templ);
			}

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
