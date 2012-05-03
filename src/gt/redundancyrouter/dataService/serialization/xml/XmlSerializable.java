package gt.redundancyrouter.dataService.serialization.xml;

import javax.xml.bind.JAXBException;

public interface XmlSerializable {
	public String serializeXML() throws JAXBException;
}
