package gt.redundancyrouter.dataService.resources.provider;

import gt.redundancyrouter.resourceMgmt.iaas.compute.Server1u1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Client1u1 {

	private DefaultHttpClient httpclient = null;

	private final String authorizationHeaderValue;

	public static String DEFAULT_HOST_ENDPOINT = "servermanagement-api.1und1.de";
	public static int DEFAULT_HOST_PORT = -1; // 443
	public final String host;
	public final int port;

	public Client1u1(final String host, final int port, final String username,
			final String password) throws ClientProtocolException, IOException {
		String str = username + ":" + password;
		// for some strange reason, the encoded string ends with a newline
		// symbol. trim it away!
		this.authorizationHeaderValue = "Basic "
				+ Base64.encodeBase64String(str.getBytes()).trim();
		this.host = host;
		this.port = port;
	}

	public Client1u1(final String username, final String password)
			throws ClientProtocolException, IOException {
		this(DEFAULT_HOST_ENDPOINT, DEFAULT_HOST_PORT, username, password);
	}

	public JSONObject doGet(final String path) throws ClientProtocolException,
			IOException, JSONException, URISyntaxException {
		String result = doHttpGet(path);
		if (result != null)
			return new JSONObject(result);
		else
			return null;
	}

	public JSONArray doList(final String path) throws ClientProtocolException,
			IOException, JSONException, URISyntaxException {
		String result = doHttpGet(path);
		if (result != null)
			return new JSONArray(result);
		else
			return null;
	}

	public void doPut(final String path) {

	}

	public JSONArray doGetServers() throws ClientProtocolException,
			IOException, JSONException, URISyntaxException {
		return doList("/cloudServer/mobile/servers/");
	}

	private Server1u1.ServerState JSONObject2ServerState(JSONObject serverState) {
		try {
			String serverStateString = serverState
					.getString(Server1u1.ServerState.JSON_KEY_STATE);
			return Server1u1.ServerState.getServerState(serverStateString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private List<Server1u1.ServerAction> JSONObject2ServerActions(
			JSONObject serverAction) throws JSONException {

		List<Server1u1.ServerAction> actions = new LinkedList<Server1u1.ServerAction>();
		JSONArray a = (JSONArray) serverAction
				.get(Server1u1.ServerAction.JSON_KEY_POSSIBLE_ACTIONS);
		for (int i = 0; i < a.length(); i++) {
			actions.add(Server1u1.ServerAction.getServerAction(a.getString(i)));
		}
		return actions;
	}

	private Server1u1 JSONObject2Server(JSONObject server) {
		Server1u1 s = new Server1u1();
		try {
			s.setConfigurable(server
					.getBoolean(Server1u1.JSON_KEY_CONFIGURABLE));
			s.setContractId(server.getInt(Server1u1.JSON_KEY_CONTRACT_ID));
			s.setHarddiskSize(server.getInt(Server1u1.JSON_KEY_HARDDISK_SIZE));
			s.setHostname(server.getString(Server1u1.JSON_KEY_HOSTNAME));
			s.setImageId(server.getInt(Server1u1.JSON_KEY_IMAGE_ID));
			s.setImageName(server.getString(Server1u1.JSON_KEY_IMAGE_NAME));
			s.setIp(server.getString(Server1u1.JSON_KEY_IP));
			s.setNumberOfCpus(server.getInt(Server1u1.JSON_KEY_NUMBER_OF_CPUS));
			s.setOperatingSystem(server.getString(Server1u1.JSON_KEY_OS));
			s.setRam(server.getInt(Server1u1.JSON_KEY_RAM));
			s.setVmid(server.getInt(Server1u1.JSON_KEY_VM_ID));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return s;
	}

	public List<Server1u1> getServers() throws ClientProtocolException,
			IOException, JSONException, URISyntaxException {
		List<Server1u1> servers = new LinkedList<Server1u1>();

		JSONArray JSONServers = this.doGetServers();

		for (int i = 0; i < JSONServers.length(); i++) {
			JSONObject o = (JSONObject) JSONServers.get(i);
			servers.add(JSONObject2Server(o));
		}
		return servers;
	}

	public JSONObject doGetServer(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doGet("/cloudServer/mobile/servers/" + vmId);
	}

	public Server1u1 getServer(final String vmId) throws ClientProtocolException,
			IOException, JSONException, URISyntaxException {
		return this.JSONObject2Server(doGetServer(String.valueOf(vmId)));
	}

	public Server1u1.ServerState getServerState(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return this.JSONObject2ServerState(this.doGetServerState(vmId));
	}

	public List<Server1u1.ServerAction> getServerActions(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return this.JSONObject2ServerActions(this.doGetServerState(vmId));
	}

	public JSONObject doGetServerState(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doGet("/cloudServer/mobile/servers/" + vmId + "/state");
	}

	public JSONArray doGetServerAvailableImages(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doList("/cloudServer/mobile/servers/" + vmId + "/images");
	}

	public JSONObject doGetServerPriceInfo(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doGet("/cloudServer/mobile/servers/" + vmId
				+ "/hardware/priceInfo");
	}

	/***
	 * Useless!!
	 * @param vmId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws URISyntaxException
	 */
	public JSONObject doGetServerTerms(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doGet("/cloudServer/mobile/servers/" + vmId + "/hardware/terms");
	}

	public JSONArray doGetServerPredefinedHardwareConfigurations(
			final String vmId) throws ClientProtocolException, IOException,
			JSONException, URISyntaxException {
		return doList("/cloudServer/mobile/servers/" + vmId
				+ "/hardware/priceInfo");
	}

	public JSONObject doGetServerAvailableHardware(final String vmId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doGet("/cloudServer/mobile/servers/" + vmId
				+ "/hardware/available");
	}

	public JSONObject doGetServerEstimatedDurationOfHardwareChange(
			final String vmId, String cpu, String hdd, String ram)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doGet("/cloudServer/mobile/servers/" + vmId
				+ "/hardware/configurationEstimate?cpu=" + cpu + "&hdd=" + hdd
				+ "&ram=");
	}

	public JSONObject doGetServerEstimatedDurationOfImageInstallTime(
			final String vmId, final String imageId)
			throws ClientProtocolException, IOException, JSONException,
			URISyntaxException {
		return doGet("/cloudServer/mobile/servers/" + vmId
				+ "/images/configurationEstimate?imageId=" + imageId);
	}

	public String doPutHardwareConfiguration(final String vmId, String cpu,
			String hdd, String ram) throws ClientProtocolException,
			IOException, URISyntaxException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("cpu", cpu);
		params.put("hdd", hdd);
		params.put("ram", ram);
		return doHttpPut("/cloudServer/mobile/servers/" + vmId + "/hardware",
				params);
	}

	// FIXME Unfortunately we don't know the ACTION parameters...
	public String doPutServerStateChange(final String vmId, String action)
			throws ClientProtocolException, IOException, URISyntaxException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("action", action);
		return doHttpPut("/cloudServer/mobile/servers/" + vmId + "/state",
				params);
	}

	public void stopServer(final String vmId) throws ClientProtocolException, IOException, URISyntaxException {
		this.doPutServerStateChange(vmId, Server1u1.ServerAction.CAN_STOP.getActionString());
	}

	public void startServer(final String vmId) throws ClientProtocolException, IOException, URISyntaxException {
		this.doPutServerStateChange(vmId, Server1u1.ServerAction.CAN_START.getActionString());
	}
	
	public void restartServer(final String vmId) throws ClientProtocolException, IOException, URISyntaxException {
		this.doPutServerStateChange(vmId, Server1u1.ServerAction.CAN_RESTART.getActionString());
	}
	
	public void suspendServer(final String vmId) throws ClientProtocolException, IOException, URISyntaxException {
		this.doPutServerStateChange(vmId, Server1u1.ServerAction.CAN_SUSPEND.getActionString());
	}
	
	public void poweroffServer(final String vmId) throws ClientProtocolException, IOException, URISyntaxException {
		this.doPutServerStateChange(vmId, Server1u1.ServerAction.CAN_POWER_OFF.getActionString());
	}

	public String doPutInstallImage(final String vmId, String newImageId)
			throws ClientProtocolException, IOException, URISyntaxException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("newImageId", newImageId);
		return doHttpPut("/cloudServer/mobile/servers/" + vmId + "/images",
				params);
	}

	public void close() {
		httpclient.getConnectionManager().shutdown();
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private String doHttpGet(final String path) throws ClientProtocolException,
			IOException, JSONException, URISyntaxException {
		httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(URIUtils.createURI("https", host, port,
				path, null, null));
		String toReturn = null;
		httpget.addHeader("Authorization", authorizationHeaderValue);
		httpget.addHeader("Accept", "application/json");

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();

		// convert entity in json object
		if (entity != null) {
			InputStream instream = entity.getContent();
			toReturn = convertStreamToString(instream);
			instream.close();
		}
		// clean up
		EntityUtils.consume(entity);
		close();
		return toReturn;
	}

	private String doHttpPut(final String path,
			final HashMap<String, String> params)
			throws ClientProtocolException, IOException, URISyntaxException {
		httpclient = new DefaultHttpClient();
		HttpPut httpput = new HttpPut(URIUtils.createURI("https", host, port,
				path, null, null));
		String toReturn = null;
		final String prettyParams = printParams(params);
		httpput.addHeader("Authorization", authorizationHeaderValue);
		// httpput.addHeader("Accept", "application/json");
		httpput.addHeader("Content-Type", "application/x-www-form-urlencoded");
		// httpput.setHeader("Content-Length", ""+prettyParams.length());

		HttpEntity entity = new StringEntity(prettyParams, "UTF-8");
		httpput.setEntity(entity);
		HttpResponse resp = httpclient.execute(httpput);
		System.out
				.println(convertStreamToString(resp.getEntity().getContent()));
		toReturn = resp.getStatusLine().toString();

		close();
		return toReturn;
	}

	private String printParams(HashMap<String, String> params) {
		StringBuffer toReturn = new StringBuffer();
		int size = params.size();
		int i = 0;
		for (String param : params.keySet()) {
			toReturn.append(param);
			toReturn.append("=");
			toReturn.append(params.get(param));
			if (++i < size)
				toReturn.append("&");
		}
		return toReturn.toString();
	}

	public static void main(String[] args) {

		try {
			Client1u1 client = new Client1u1("158341849", "emergent");
			// List<Server1u1> servers = client.getServers();
			String id = "53135";
//			System.out.println(client.doGetServerState(id));
//			System.out.println("stopping...");
////			client.stopServer(id);
//			System.out.println(client.doGetServerAvailableImages(id));
//			System.out.println(client.doGetServerAvailableHardware(id));

			System.out.println(client.doGet("/cloudServer/mobile/servers/"+id+""));

			
//			System.out.println(client.doGetServerState(id));
			
			// for (Server1u1 s : servers) {
			// System.out.println("id: " + s.getId());
			// System.out.println("contractID: " + s.getContractId());
			// System.out.println("1: "
			// + client.doGetServerAvailableImages(s.getId()));
			// System.out.println(client.getServerActions(s.getId()).get(0).getActionString());
			// }

			// System.out.println(client.doGetServer("53139"));
			// System.out.println(client.doGetServerAvailableImages("53139"));
			// System.out.println(client.doGetServerEstimatedDurationOfImageInstallTime("53139","10107"));
			// System.out.println(client.doGetServerState("53139"));
			// System.out.println(client.doPutInstallImage("53139","10107"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
