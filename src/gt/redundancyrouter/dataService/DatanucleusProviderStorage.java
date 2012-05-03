package gt.redundancyrouter.dataService;

import gt.redundancyrouter.dataService.resources.provider.CustomProvider;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;



public class DatanucleusProviderStorage implements ProviderStorage {

	@Override
	public boolean updateProvider(CustomProvider provider) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		boolean updated = false;
		try {
			tx.begin();
			Extent<CustomProvider> providerExtent = pm.getExtent(CustomProvider.class);
			Iterator<CustomProvider> providerIt = providerExtent.iterator();
			while (providerIt.hasNext()) {
				CustomProvider storedProvider = providerIt.next();
				if (storedProvider.getName().equals(provider.getName())) {
					storedProvider.setApiType(provider.getApiType());
					storedProvider.setCostModels(provider.getCostModels());
					storedProvider.setServiceEndpoint(provider
							.getServiceEndpoint());
					storedProvider.setServiceEndpointPort(provider
							.getServiceEndpointPort());
					storedProvider.setStatus(provider.getStatus());
					pm.makePersistent(storedProvider);
					updated = true;
					break;
				}
			}
			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		pm.close();
		return updated;
	}
	
	@Override
	public boolean updateProviders(List<CustomProvider> providers) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		boolean updated = true;
		try {
			tx.begin();
			Extent<CustomProvider> providerExtent = pm.getExtent(CustomProvider.class);
			if(!providerExtent.iterator().hasNext())
				return false;
			for (CustomProvider provider : providers) {
				Iterator<CustomProvider> providerIt = providerExtent.iterator();
				while (providerIt.hasNext()) {
					CustomProvider storedProvider = providerIt.next();
					if (storedProvider.getName().equals(provider.getName())) {
						storedProvider.setApiType(provider.getApiType());
						storedProvider.setCostModels(provider.getCostModels());
						storedProvider.setServiceEndpoint(provider
								.getServiceEndpoint());
						storedProvider.setServiceEndpointPort(provider
								.getServiceEndpointPort());
						storedProvider.setStatus(provider.getStatus());
						pm.makePersistent(storedProvider);
						updated = updated && true;
					} else
						updated = false;
				}
			}
			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		pm.close();
		return updated;
	}


	@Override
	public void addProvider(CustomProvider provider) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistent(provider);
			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		pm.close();
	}

	@Override
	public void addProviders(List<CustomProvider> providers) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistentAll(providers);
			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		pm.close();
	}


	@Override
	public void removeProvider(String name) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			@SuppressWarnings("unchecked")
			List<CustomProvider> providerList = ((List<CustomProvider>) pm.newQuery(CustomProvider.class, "name == \"" + name + "\"").execute());
			CustomProvider provider = (providerList!=null && providerList.size()>0) ? providerList.get(0) : null;
			pm.deletePersistent(provider);
			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		pm.close();
	}

	@Override
	public void removeProvider(CustomProvider provider) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			@SuppressWarnings("unchecked")
			List<CustomProvider> providerList = ((List<CustomProvider>) pm.newQuery(CustomProvider.class, "name == \"" + provider.getName() + "\"").execute());
			provider = (providerList!=null && providerList.size()>0) ? providerList.get(0) : null;
			pm.deletePersistent(provider);
			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		pm.close();
	}

	@Override
	public void removeAll() {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		List<CustomProvider> providers = this.getProviders();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.deletePersistentAll(providers);
			tx.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (tx.isActive())
				tx.rollback();
			pm.close();
		}
		pm.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CustomProvider getProvider(String name) {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		List<CustomProvider> providerList = null;
		try {
			providerList = ((List<CustomProvider>) pm.newQuery(CustomProvider.class, "name == \"" + name + "\"").execute());
			providerList = new LinkedList<CustomProvider>(providerList);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			pm.close();
		}
		pm.close();
		return (providerList!=null && providerList.size()>0) ? providerList.get(0) : null;
				
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustomProvider> getProviders() {
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		List<CustomProvider> providerList = null;
		try {
			providerList = ((List<CustomProvider>) pm.newQuery(CustomProvider.class).execute());
			providerList = new LinkedList<CustomProvider>(providerList);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			pm.close();
		}
		pm.close();
		return providerList;
				
	}

		public static void main(String[] args) {
		DatanucleusProviderStorage providerStore = new DatanucleusProviderStorage();

		List<CustomProvider> providers = new LinkedList<CustomProvider>();
		providers = providerStore.getProviders();

		CustomProvider provider = providerStore.getProvider("einsUndEins");
		providers.add(provider);
//		provider = providerStore.getProvider("einsUndEins");
//		providers.add(provider);
		
		
		for (CustomProvider prov : providers) {
			System.out.println("Name: " + prov.getName());
			System.out.println("API-Type: " + prov.getApiType());
			System.out.println("Cost Modells: " + prov.getCostModels());
			System.out
					.println("Service Endpoint: " + prov.getServiceEndpoint());
			System.out.println("Service Endpoint Port: "
					+ prov.getServiceEndpointPort());
			System.out.println("Status: " + prov.getStatus());
		}

	}

}
