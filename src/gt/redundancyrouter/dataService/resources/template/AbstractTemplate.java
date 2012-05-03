package gt.redundancyrouter.dataService.resources.template;

import gt.redundancyrouter.resourceMgmt.AbstractResource;

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
