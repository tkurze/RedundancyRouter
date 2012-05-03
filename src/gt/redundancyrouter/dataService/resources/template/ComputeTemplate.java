package gt.redundancyrouter.dataService.resources.template;

import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;

public class ComputeTemplate extends AbstractTemplate {

	protected String imageId = null;
	protected String keyPairName = null;
	protected OsFamily operatingSystemFamily = null;
	protected String operatingSystemVersion = null;
	protected boolean os64Bit = false;
	protected boolean biggest = false;
	protected boolean fastest = false;
	protected boolean smallest = false;
	
	
	public ComputeTemplate(String name) {
		super(name);
	}
	
	private ComputeTemplate(){
		super(null);
	}


	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getKeyPairName() {
		return keyPairName;
	}

	public void setKeyPairName(String keyPairName) {
		this.keyPairName = keyPairName;
	}

	public OsFamily getOperatingSystemFamily() {
		return operatingSystemFamily;
	}

	public void setOperatingSystemFamily(OsFamily operatingSystemFamily) {
		this.operatingSystemFamily = operatingSystemFamily;
	}

	public String getOperatingSystemVersion() {
		return operatingSystemVersion;
	}

	public void setOperatingSystemVersion(String operatingSystemVersion) {
		this.operatingSystemVersion = operatingSystemVersion;
	}

	public boolean isOs64Bit() {
		return os64Bit;
	}

	public void setOs64Bit(boolean os64Bit) {
		this.os64Bit = os64Bit;
	}

	public boolean isBiggest() {
		return biggest;
	}

	public void setBiggest(boolean biggest) {
		this.biggest = biggest;
	}

	public boolean isFastest() {
		return fastest;
	}

	public void setFastest(boolean fastest) {
		this.fastest = fastest;
	}

	public boolean isSmallest() {
		return smallest;
	}

	public void setSmallest(boolean smallest) {
		this.smallest = smallest;
	}

	public Template buildJCloudsTemplate(TemplateBuilder tb){
		if(this.imageId!=null)
			tb.imageId(this.imageId);
		
		if(this.operatingSystemFamily!=null)
			tb.osFamily(this.operatingSystemFamily);
		if(this.operatingSystemVersion!=null)
			tb.osVersionMatches(this.operatingSystemVersion);
		tb.os64Bit(this.os64Bit);
		
		if(this.biggest)
			tb.biggest();
		if(this.fastest)
			tb.fastest();
		if(this.smallest)
			tb.smallest();
		
		Template t = tb.build();
		
		if(this.keyPairName!=null)
			t.getOptions().as(AWSEC2TemplateOptions.class).keyPair(keyPairName);
		
		return t;	
	}

}
