package gt.redundancyrouter.resources;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;

public abstract class AbstractResource implements Resource {

	@XmlElement
	protected final String name;
	
	
	@SuppressWarnings("unused")
	private AbstractResource(){
		this.name = null;
	}
	
	protected AbstractResource(String name) {
		this.name = name;
		
	}
	
	public String getName(){
		return this.name;
	}
	
	
	@Override
	public String serializeXML() throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());

		Marshaller m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(Marshaller.JAXB_FRAGMENT, true);
		m.marshal(this, writer);
		return writer.toString();
	}
}
