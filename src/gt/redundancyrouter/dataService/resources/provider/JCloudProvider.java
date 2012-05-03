package gt.redundancyrouter.dataService.resources.provider;

import javax.xml.bind.JAXBException;

/**
 * Provider APIs supported by jClouds:
 * <p>
 * atmos, cloudfiles-uk, cloudsigma, openhosting-east1, ninefold-compute,
 * elasticstack, eucalyptus-partnercloud-s3, deltacloud, stub, aws-ec2,
 * rimuhosting, synaptic-storage, slicehost, skalicloud-sdg-my, cloudservers,
 * eucalyptus, vcloud, transient, savvis-symphonyvpdc, nova, cloudservers-us,
 * cloudsigma-zrh, ninefold-storage, swift, hpcloud-objectstorage-lvs,
 * cloudonestorage, cloudfiles-us, stratogen-vcloud-mycloud, cloudsigma-lvs,
 * greenhousedata-element-vcloud, bluelock-vcloud-zone01, azureblob, walrus,
 * gogrid, elastichosts-tor-p, elastichosts-lon-b, softlayer, trmk-ecloud,
 * aws-s3, elastichosts-sat-p, cloudfiles, cloudstack, elastichosts-lon-p,
 * eucalyptus-partnercloud-ec2, serverlove-z1-man, cloudservers-uk, filesystem,
 * elastichosts-lax-p, trmk-vcloudexpress
 * 
 * @author tobias
 * 
 */
public class JCloudProvider extends AbstractProvider {

	private final String jCloudProviderString;

	public static final JCloudProvider getAwsEc2Provider(String name) {
		return new JCloudProvider("aws-ec2", name);
	}

	public static final JCloudProvider getEucalyptusProvider(String name) {
		return new JCloudProvider("eucalyptus", name);
	}

	public static final JCloudProvider getOpentackNovaProvider(String name) {
		return new JCloudProvider("nova", name);
	}

	public static final JCloudProvider getCloudstackProvider(String name) {
		return new JCloudProvider("cloudstack", name);
	}

	private JCloudProvider() {
		super(null, null);
		this.jCloudProviderString = null;
	}

	private JCloudProvider(String jCloudProviderString, String name) {
		super(name, null);
		this.jCloudProviderString = jCloudProviderString;
	}

	public String getjCloudProviderString() {
		return this.jCloudProviderString;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String serializeXML() throws JAXBException {
		// TODO Auto-generated method stub
		return null;
	}
}
