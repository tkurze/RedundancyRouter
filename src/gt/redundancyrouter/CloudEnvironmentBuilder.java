package gt.redundancyrouter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class CloudEnvironmentBuilder {

	public static void buildEnvironment(File f) throws JDOMException,
			IOException {

		final SAXBuilder parser = new SAXBuilder();
		// final XMLOutputter printer = new XMLOutputter();

		final List<String> providersInEnvironment = new LinkedList<String>();
		final List<String> nodeTemplatesInEnvironment = new LinkedList<String>();
		final List<String> credentialsInEnvironment = new LinkedList<String>();

		Document doc = parser.build(f);
		final String name = doc.getRootElement().getAttribute("name")
				.getValue();
		if (name != null) {
			System.out.println(name);
		}

		List<Element> nodes = doc.getRootElement().getChildren();
		for (Element node : nodes) {
			providersInEnvironment.add(node.getChild("provider").getValue());
			nodeTemplatesInEnvironment
					.add(node.getChild("template").getValue());
			credentialsInEnvironment.add(node.getChild("credentials")
					.getValue());
		}
	}

	private static boolean startNodes(List<Element> nodes) {

		return false;
	}

	private static boolean providerCheck(List<String> providers) {

		return false;
	}

	private static boolean nodeTemplateCheck(List<String> nodeTemplates) {

		return false;
	}

	private static boolean credentialsCheck(List<String> credentials) {

		return false;
	}

	public static void main(String[] args) {
		try {
			CloudEnvironmentBuilder.buildEnvironment(new File(
					"/home/tobias/.redundancyRouter/test.xml"));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
