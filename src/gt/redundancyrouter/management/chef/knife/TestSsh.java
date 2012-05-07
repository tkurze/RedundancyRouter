package gt.redundancyrouter.management.chef.knife;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

public class TestSsh {

	public static void main(String[] args) throws IOException {
		SSHClient ssh = new SSHClient();
		ssh.loadKnownHosts();
		ssh.connect("localhost");
		try {
			ssh.authPublickey(System.getProperty("user.name"));
			HostKeyVerifier v = new MyHostKeyVerifier();
			ssh.addHostKeyVerifier(v);
//			ssh.auth("root", null);
			// Present here to demo algorithm renegotiation - could have just
			// put this before connect()
			// Make sure JZlib is in classpath for this to work
			ssh.useCompression();

			final String src = System.getProperty("user.home") + File.separator
					+ "test_file";
			ssh.newSCPFileTransfer().upload(new FileSystemFile(src), "/tmp/");
		} finally {
			ssh.disconnect();
		}
	}
	
	public static class MyHostKeyVerifier implements HostKeyVerifier{

		@Override
		public boolean verify(String hostname, int port, PublicKey key) {
			if(hostname.equalsIgnoreCase("localhost"))
				return true;
			return false;
		}
		
	}
}
