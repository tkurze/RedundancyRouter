package gt.redundancyrouter;

import gt.redundancyrouter.resourceMgmt.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 * Implementations of this class:
 * 
 * {@link ChefNodeManager} {@link LoginCredentialsManager} {@link NodeManager}
 * {@link PrivateKeyManager} {@link ProviderManager} {@link TemplateManager}
 * 
 * @author tobias
 * 
 * @param <T>
 */
public abstract class BasicManager<T extends Resource> implements Manager,
		Manageable {
	
	
	protected final String name;
	protected final XMLOutputter printer = new XMLOutputter();

	protected final HashMap<String, T> managededObjects;


	@SuppressWarnings("unused")
	private BasicManager() {
		this.managededObjects = new HashMap<String, T>();
		this.name = null;
	}

	public BasicManager(String name) {
		this.managededObjects = new HashMap<String, T>();
		this.name = name;
	}

	protected void addManagedObject(T object, String key) {
		if (!this.managededObjects.containsValue(object))
			this.managededObjects.put(key, object);
	}

	@SuppressWarnings("unchecked")
	public void addManagedObject(Manageable object) {
		this.addManagedObject((T) object, object.getName());
	}

	public T getManagedObject(String key) {
		return this.managededObjects.get(key);
	}

	public void removeManagedObject(String key) {
		this.managededObjects.remove(key);
	}

	public Collection<T> getManagedObjects() {
		return this.managededObjects.values();
	}

	protected List<String> getXmlChildStrings(File f) throws JDOMException,
			IOException {
		List<String> childStrings = new LinkedList<String>();

		Document doc;
		SAXBuilder parser = new SAXBuilder();

		doc = parser.build(f);
		List<Element> childs = doc.getRootElement().getChildren();
		for (Element c : childs) {
			childStrings.add(printer.outputString(c));
		}
		return childStrings;
	}

	protected String addRootTags(String xmlDoc) {
		return "<" + this.getName() + ">\n" + xmlDoc + "\n" + "</"
				+ this.getName() + ">";
	}

	@Override
	public String getName() {
		return this.name;
	}



	@Override
	public void saveConfig(File f) {
		File configFile = new File(f, this.getName());
		StringBuffer buf = new StringBuffer();
		for (T k : this.managededObjects.values()) {
			try {
				buf.append(k.serializeXML());
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileUtils.writeStringToFile(configFile,
					this.addRootTags(buf.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract void loadConfig(File f);
}
