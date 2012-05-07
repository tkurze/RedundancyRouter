package gt.redundancyrouter.dataService;

import gt.redundancyrouter.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.resources.provider.CustomProvider;

public interface CredentialsStorage {
	void addOrUpdateCredentials(String user, CustomProvider provider, ProviderCredentials creds);
	ProviderCredentials getCredentials(String user, CustomProvider provider);
}
