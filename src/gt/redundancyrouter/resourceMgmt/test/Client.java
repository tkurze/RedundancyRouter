package gt.redundancyrouter.resourceMgmt.test;

import gt.redundancyrouter.dataService.resources.provider.CustomProvider;

import java.util.LinkedList;
import java.util.List;



public class Client {
	
	public static void main(String[] args){
		//ProviderStorage providerStorage = new DatanucleusProviderStorage();
		//List<Provider> providers = providerStorage.getProviders(); //get available providers
		List<CustomProvider> providers = new LinkedList<CustomProvider>(); //get available providers
		
		for(CustomProvider prov: providers){
			System.out.println("Name: "+prov.getName());
		}
		//user chooses provider
		
		//here provider is set in the code
		//Provider amazon = providerStorage.getProvider("amazonUSeast");
//		CustomProvider einsUndEins = providers.get(1);
		
		//check if credentials are available
		
		
		
//		ResourceManager resMan = ResourceManager.getResourceManager();
		
		
//		try {
//			resMan.addComputeNode(einsUndEins, MachineType.micro, "myFirstAmazonMachine");
//		} catch (ResourceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
