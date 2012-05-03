package gt.redundancyrouter.dataService;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public enum PMF {
	INSTANCE;
	private final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
	public static PersistenceManagerFactory getPMF(){
		return INSTANCE.pmf;
	}

}
