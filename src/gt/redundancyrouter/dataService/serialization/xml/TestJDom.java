package gt.redundancyrouter.dataService.serialization.xml;

import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class TestJDom {

	public static void main(String[] args) {
		String buffer = "";

		SAXBuilder parser = new SAXBuilder();
		// get the dom-document
		Document doc;
		XMLOutputter printer = new XMLOutputter();
		try {
			doc = parser.build("/home/tobias/.redundancyRouter/test");

			List<Element> childs = doc.getRootElement().getChildren();
			for (Element c : childs) {
				System.out.println("========child========");
				System.out.println(printer.outputString(c));
				buffer += printer.outputString(c) + "\n";
			}

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
