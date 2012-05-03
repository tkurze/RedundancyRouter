package gt.redundancyrouter.dataService.serialization.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SaveXmlToFile implements XmlSerializable{
	
	public String test = "hallo world";
	
	public static void main(String[] args){
		String xml ="";
		try {
			xml = new SaveXmlToFile().serializeXML();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(xml);
		
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(SaveXmlToFile.class);
			final SaveXmlToFile obj =
		            (SaveXmlToFile) jaxbContext.createUnmarshaller().unmarshal(
		                new StringReader(xml));
			
			System.out.println(obj.test);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public String serializeXML() throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = JAXBContext.newInstance(SaveXmlToFile.class);

		jaxbContext.createMarshaller().marshal(new SaveXmlToFile(), writer);
		return writer.toString();
	}


}
