package gt.redundancyrouter.resources.iaas.compute;

import gt.redundancyrouter.resources.provider.Client1u1;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OperatingSystem.Builder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.domain.ResourceMetadata;

public class Server1u1 implements NodeMetadata {

	public static final String JSON_KEY_VM_ID = "vmid";
	public static final String JSON_KEY_CONFIGURABLE = "configurable";
	public static final String JSON_KEY_RAM = "ram";
	public static final String JSON_KEY_CONTRACT_ID = "contract";
	public static final String JSON_KEY_NUMBER_OF_CPUS = "cpu";
	public static final String JSON_KEY_HOSTNAME = "hostname";
	public static final String JSON_KEY_OS = "imagetype"; // aka imagetype
	public static final String JSON_KEY_HARDDISK_SIZE = "hdd";
	public static final String JSON_KEY_IMAGE_ID = "imageid";
	public static final String JSON_KEY_IMAGE_NAME = "imagename";
	public static final String JSON_KEY_IP = "ip";

	protected Integer vmId;
	protected Boolean configurable;
	protected Integer ram;
	protected Integer contractId;
	protected Integer numberOfCpus;
	protected String hostname;
	protected String operatingSystem; // aka imagetype
	protected Integer harddiskSize;
	protected Integer imageId;
	protected String imageName;
	protected String ip;
	
	protected List<ServerAction> possibleServerActions;
	protected ServerState state;

	public Server1u1(Integer vmid, Boolean configurable, Integer ram,
			Integer contractId, Integer numberOfCpus, String hostname,
			String operatingSystem, Integer harddiskSize, Integer imageId,
			String imageName, String ip) {
		super();
		this.vmId = vmid;
		this.configurable = configurable;
		this.ram = ram;
		this.contractId = contractId;
		this.numberOfCpus = numberOfCpus;
		this.hostname = hostname;
		this.operatingSystem = operatingSystem;
		this.harddiskSize = harddiskSize;
		this.imageId = imageId;
		this.imageName = imageName;
		this.ip = ip;
	}

	public Server1u1() {
	}

	public Integer getVmId() {
		return vmId;
	}

	public void setVmid(Integer vmid) {
		this.vmId = vmid;
	}

	public Boolean getConfigurable() {
		return configurable;
	}

	public void setConfigurable(Boolean configurable) {
		this.configurable = configurable;
	}

	public Integer getRam() {
		return ram;
	}

	public void setRam(Integer ram) {
		this.ram = ram;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Integer getNumberOfCpus() {
		return numberOfCpus;
	}

	public void setNumberOfCpus(Integer numberOfCpus) {
		this.numberOfCpus = numberOfCpus;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	// FIXME: add more informations to operating system. also "more correct"
	// information, such as distribution, etc.
	public OperatingSystem getOperatingSystem() {
		Builder b = OperatingSystem.builder();
		if (this.operatingSystem.equalsIgnoreCase("Linux"))
			b.family(OsFamily.LINUX);
		else
			b.family(OsFamily.WINDOWS);
		b.arch("");
		b.description("1&1 VM");
		b.is64Bit(true);
		b.name(this.imageName);
		return b.build();
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	public Integer getHarddiskSize() {
		return harddiskSize;
	}

	public void setHarddiskSize(Integer harddiskSize) {
		this.harddiskSize = harddiskSize;
	}

	public String getImageId() {
		return String.valueOf(imageId);
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String getId() {
		return String.valueOf(this.vmId);
	}

	@Override
	public String getName() {
		return this.hostname;
	}

	@Override
	public String getProviderId() {
		return "1&1";
	}

	@Override
	public Set<String> getTags() {
		return null;
	}

	@Override
	public ComputeType getType() {
		return ComputeType.NODE;
	}

	@Override
	public Location getLocation() {
		List<String> countryCodes = new LinkedList<String>();
		countryCodes.add("DE");
		return new LocationBuilder().iso3166Codes(countryCodes).id("DE")
				.description("1&1 Server Location: Germany")
				.scope(LocationScope.REGION).build();
	}

	@Override
	public URI getUri() {
		URI uri = null;
		try {
			uri = new URI(this.hostname);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	public Map<String, String> getUserMetadata() {
		return null;
	}

	@Override
	public int compareTo(ResourceMetadata<ComputeType> o) {
		return 0;
	}

	@Override
	public String getAdminPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoginCredentials getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGroup() {
		return null;
	}

	@Override
	public Hardware getHardware() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLoginPort() {
		return 22;
	}

	@Override
	public Set<String> getPrivateAddresses() {
		return null;
	}

	@Override
	public Set<String> getPublicAddresses() {
		Set<String> ips = new HashSet<String>();
		ips.add(this.ip);
		return ips;
	}

	@Override
	public NodeState getState() {
		if(this.state==ServerState.RUNNING)
			return NodeState.RUNNING;
		else if (this.state==ServerState.RECONFIGURING)
			return NodeState.PENDING;
		else if (this.state==ServerState.STOPPED)
			return NodeState.TERMINATED;
		else
			return NodeState.UNRECOGNIZED;
	}
	
	public List<ServerAction> getPossibleServerActions() {
		return possibleServerActions;
	}

	public void setPossibleServerActions(List<ServerAction> possibleServerActions) {
		this.possibleServerActions = possibleServerActions;
	}

	public void set1u1State(ServerState state) {
		this.state = state;
	}
	public ServerState get1u1State() {
		return this.state;
	}

	public static final class ServerAction {
		public static final String JSON_KEY_POSSIBLE_ACTIONS = "possibleActions";
		
		public static final ServerAction CAN_SUSPEND = new ServerAction(
				"CAN_SUSPEND");
		public static final ServerAction CAN_POWER_OFF = new ServerAction(
				"CAN_POWER_OFF");
		public static final ServerAction CAN_STOP = new ServerAction("CAN_STOP");
		public static final ServerAction CAN_RESTART = new ServerAction(
				"CAN_RESTART");
		public static final ServerAction CAN_START = new ServerAction(
				"CAN_START");

		private final String action;

		public String getActionString() {
			return action;
		}

		private ServerAction(String action) {
			this.action = action;
		}

		public static ServerAction getServerAction(String action) {
			if (action.equalsIgnoreCase(CAN_SUSPEND.action))
				return CAN_SUSPEND;
			if (action.equalsIgnoreCase(CAN_POWER_OFF.action))
				return CAN_POWER_OFF;
			if (action.equalsIgnoreCase(CAN_STOP.action))
				return CAN_STOP;
			if (action.equalsIgnoreCase(CAN_RESTART.action))
				return CAN_RESTART;
			if (action.equalsIgnoreCase(CAN_START.action))
				return CAN_START;

			return null;
		}
	}

	public static final class ServerState {
		public static final String JSON_KEY_STATE = "state";
		
		public static final ServerState RUNNING = new ServerState("RUNNING");
		public static final ServerState RECONFIGURING = new ServerState(
				"RECONFIGURING");
		public static final ServerState STOPPED = new ServerState("STOPPED");

		private final String state;

		public String getStateString() {
			return state;
		}

		private ServerState(String state) {
			this.state = state;
		}

		public static ServerState getServerState(String state) {
			if (state.equalsIgnoreCase(RUNNING.state))
				return RUNNING;
			if (state.equalsIgnoreCase(RECONFIGURING.state))
				return RECONFIGURING;
			if (state.equalsIgnoreCase(STOPPED.state))
				return STOPPED;

			return null;
		}

	}

}
