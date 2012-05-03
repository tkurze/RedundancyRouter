package gt.redundancyrouter.dataService;

import gt.redundancyrouter.dataService.resources.credentials.ProviderCredentials;
import gt.redundancyrouter.dataService.resources.provider.CustomProvider;

public interface CredentialsStorage {
	void addOrUpdateCredentials(String user, CustomProvider provider, ProviderCredentials creds);
	ProviderCredentials getCredentials(String user, CustomProvider provider);
}
