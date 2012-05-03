package gt.redundancyrouter;

import java.io.File;

public interface Manager{
	public String getName();
	public void loadConfig(File f);
	public void saveConfig(File f);
}
