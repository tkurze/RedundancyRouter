package gt.redundancyrouter.dataService;

import gt.redundancyrouter.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.resources.provider.CustomProvider;

import java.util.List;

import javax.jdo.PersistenceManager;



public class DatanucleusCredentialsStorage implements CredentialsStorage {

	@Override
	public void addOrUpdateCredentials(String user, CustomProvider provider,
			ProviderCredentials creds) {
		creds.setIdentity(user);
		creds.setProvider(provider.getName());
		PMF.getPMF().getPersistenceManager().makePersistent(creds);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProviderCredentials getCredentials(String user, CustomProvider provider) {
		return ((List<ProviderCredentials>) PMF
				.getPMF()
				.getPersistenceManager()
				.newQuery(
						ProviderCredentials.class,
						"user == \"" + user + "\" && provider == \""
								+ provider.getName() + "\"").execute()).get(0);
	}

	public static void main(String[] args) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();

		ProviderCredentials creds = new ProviderCredentials("tobias 1u1 creds", "tobias", "1u1");

		pm.makePersistent(creds);

		String query = "select from " + ProviderCredentials.class.getName()
				+ " order by date desc range 0,5";
		pm.newQuery(query);
	}

}
