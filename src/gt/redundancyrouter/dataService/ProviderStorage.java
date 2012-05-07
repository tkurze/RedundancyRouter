package gt.redundancyrouter.dataService;

import gt.redundancyrouter.resources.provider.CustomProvider;

import java.util.List;



public interface ProviderStorage {
	public boolean updateProvider(CustomProvider provider);
	public boolean updateProviders(List<CustomProvider> providers);
	public void removeProvider(String name);
	void removeProvider(CustomProvider provider);
	void removeAll();
	public CustomProvider getProvider(String name);
	public List<CustomProvider> getProviders();
	void addProvider(CustomProvider provider);
	void addProviders(List<CustomProvider> providers);
	
}
