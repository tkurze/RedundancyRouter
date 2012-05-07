package gt.redundancyrouter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Configuration {

	private static final String CONFIGURATION_FILE_NAME = "redundancyRouter.cfg";
	public static final File DEFAULT_CONFIGURATION_DIRECTORY = new File(
			System.getProperty("user.home") + File.separator
					+ ".redundancyRouter");

	public final static String PROPERTY_MANAGER_CONFIG_DIR = "managerConfig";

	private static Configuration theConfigurationManager = null;

	private final File configurationDirectory;
	private final Properties props = new Properties();

	protected final HashMap<String, BasicManager<?>> managededManagers;

	protected final String name;

	public static synchronized Configuration getConfiguration()
			throws ConfigurationManagerException {
		if (Configuration.theConfigurationManager == null)
			Configuration.theConfigurationManager = new Configuration(
					null);
		return Configuration.theConfigurationManager;
	}

	public static synchronized Configuration getConfigurationManager(
			File configDirectory) throws ConfigurationManagerException {
		if (Configuration.theConfigurationManager == null)
			Configuration.theConfigurationManager = new Configuration(
					configDirectory);
		return Configuration.theConfigurationManager;
	}

	private Configuration() {
		this.name = null;
		this.configurationDirectory = null;
		this.managededManagers = new HashMap<String, BasicManager<?>>();
	};

	private Configuration(File configDirectory)
			throws ConfigurationManagerException {
		this.name = "configManager";
		this.managededManagers = new HashMap<String, BasicManager<?>>();
		if (configDirectory != null)
			configurationDirectory = configDirectory;
		else
			configurationDirectory = DEFAULT_CONFIGURATION_DIRECTORY;
		this.createConfigDir();
		try {
			this.loadProperties();
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.loadConfig(this.getManagerConfigurationsDirectoryProperty());

	}

	public File getConfigurationDirectory() {
		return configurationDirectory;
	}

	private void loadProperties() throws InvalidPropertiesFormatException,
			FileNotFoundException, IOException {
		File configFile = new File(this.configurationDirectory,
				CONFIGURATION_FILE_NAME);

		if (configFile.exists())
			this.props.loadFromXML(new FileInputStream(configFile));
	}

	private void saveProperties() throws FileNotFoundException, IOException {
		File configFile = new File(this.configurationDirectory,
				CONFIGURATION_FILE_NAME);
		this.props.storeToXML(new FileOutputStream(configFile), null);
	}

	private void createConfigDir() throws ConfigurationManagerException {
		if (!configurationDirectory.exists()
				&& configurationDirectory.isDirectory())
			if (!configurationDirectory.mkdirs())
				throw new ConfigurationManagerException(
						"couldn't create configuration directory");
	}

	public void setManagerConfigurationDirectoryProperty(
			File managerConfigurationDirectory) {
		this.props.setProperty(PROPERTY_MANAGER_CONFIG_DIR,
				managerConfigurationDirectory.getAbsolutePath());
	}

	public File getManagerConfigurationsDirectoryProperty() {
		return new File(this.props.getProperty(PROPERTY_MANAGER_CONFIG_DIR,
				this.getConfigurationDirectory().getAbsolutePath()));
	}

	public void setProperty(String key, String value) {
		this.props.setProperty(key, value);
	}

	public String getProperty(String key) {
		return this.props.getProperty(key);
	}

	public static void main(String[] args) {
		try {
			Configuration config = Configuration
					.getConfiguration();

			System.out.println(config
					.getManagerConfigurationsDirectoryProperty());

			config.saveConfig();

		} catch (ConfigurationManagerException e) {
			e.printStackTrace();
		}
	}

	public void addManager(BasicManager<?> object, String key) {
		this.managededManagers.put(key, object);
	}

	public BasicManager<?> getManager(String key) {
		return this.managededManagers.get(key);
	}

	public Collection<BasicManager<?>> getManagers() {
		return this.managededManagers.values();
	}

	public void removeManager(String key) {
		this.managededManagers.remove(key);

	}

	private void loadConfig(File f) {
		try {
			this.loadProperties();
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String managerName : ManagerFabric.MANAGER_NAMES) {
			String isManagerAvailalbe = this.getProperty(managerName);
			if (isManagerAvailalbe != null
					&& isManagerAvailalbe.equalsIgnoreCase("1")) {
				// creates and adds a manager to the managed objects (managers)
				ManagerFabric.getManagerFabric(this).getManagerByDefaultName(
						managerName);
			}
		}

		for (BasicManager<?> b : this.managededManagers.values()) {
			b.loadConfig(f);
		}
	}

	public void saveConfig() {
		File managerConfigurationsDir = this
				.getManagerConfigurationsDirectoryProperty();
		for (BasicManager<?> b : this.managededManagers.values()) {
			b.saveConfig(managerConfigurationsDir);
			this.setProperty(b.getName(), "1");
		}

		try {
			this.saveProperties();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class ConfigurationManagerException extends Exception {
		private static final long serialVersionUID = 1L;

		public ConfigurationManagerException(String string) {
			super(string);
		}

		public ConfigurationManagerException(Throwable t) {
			super(t);
		}
	}

}
