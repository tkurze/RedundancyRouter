package gt.redundancyrouter.resources.template;

import gt.redundancyrouter.resources.AbstractResource;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ComputeTemplate.class})
public abstract class AbstractTemplate extends AbstractResource {
	
	private AbstractTemplate() {
		super(null);
	}
	
	public AbstractTemplate(String name) {
		super(name);
	}
	
	public String getName() {
		return name;
	}
}
