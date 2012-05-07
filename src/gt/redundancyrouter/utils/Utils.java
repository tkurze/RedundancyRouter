package gt.redundancyrouter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import gt.redundancyrouter.management.chef.ChefNodeManager;
import gt.redundancyrouter.resources.credentials.LoginCredentials;
import gt.redundancyrouter.resources.credentials.LoginCredentialsPassword;
import gt.redundancyrouter.resources.credentials.LoginCredentialsPrivateKey;
import gt.redundancyrouter.utils.ssh.SshClient;
import gt.redundancyrouter.utils.ssh.SshClient.CommandExecution;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class Utils {

	private static Logger log = Logger.getLogger("Utils");

	public static final int DEFAULT_NUMBER_OF_CONNECTION_RETRIES = 5;
	public static final int DEFAULT_WAITING_TIME = 5000;

	public static String CommandArray2CommandLine(String[] commandArray) {
		String commandLine = "";
		for (String s : commandArray) {
			commandLine += (" " + s);
		}
		return commandLine.substring(1);
	}

	public static boolean waitSshReadyOnHost(String nodeAddress,
			LoginCredentials nodeCredentials, int numberOfRetries,
			int waitingTime) {
		Boolean useIdentityFile = null;
		String credentials = null;
		String userName = nodeCredentials.getUsername();

		if (nodeCredentials instanceof LoginCredentialsPassword) {
			useIdentityFile = false;
			credentials = ((LoginCredentialsPassword) nodeCredentials)
					.getPassword();
		} else if (nodeCredentials instanceof LoginCredentialsPrivateKey) {
			useIdentityFile = true;
			credentials = ((LoginCredentialsPrivateKey) nodeCredentials)
					.getKey().getKeyPath();
		}

		SshClient ssh = null;
		int i = numberOfRetries;
		while (i > 0) {
			try {
				ssh = new SshClient();
				ssh.createSession(nodeAddress, userName, false);

				log.info("connecting with username: " + userName);

				if (useIdentityFile) {
					ssh.addIdentityFile(credentials);
					log.info("connecting using identity file: " + credentials);
				} else {
					log.info("connecting using password: " + credentials);
					ssh.setSessionPassword(credentials);
				}

				ssh.connectSession();

				ChannelExec c = ssh.getExecutionChannel();
				c.setCommand("dir");
				c.connect();
				int exitstatus = c.getExitStatus();
				c.disconnect();
				ssh.disconnectSession();
				if (exitstatus == -1)
					log.info("successfully waited for ssh to become operational on remote system");
				else
					log.warn("ssh should be ready, but received unexpected exit status");
				return true;
			} catch (JSchException e1) {
				if (e1.getCause() instanceof java.net.UnknownHostException)
					log.info("host not yet(?) reachable");
				else if (e1.getCause() instanceof java.net.ConnectException)
					log.info("host reachable, but connection still(?) refused");
				else if (e1.getCause() instanceof java.io.IOException)
					log.info("probably we hit the end of the IO stream, in case of trouble check the exception!");
				else
					e1.printStackTrace();
				try {
					Thread.sleep(waitingTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				i--;
			}
		}
		log.info("giving up: ssh is not operational on the remote system");
		return false;
	}

	public static boolean waitSshReadyOnNode(String nodeAddress,
			LoginCredentials nodeCredentials) {
		return waitSshReadyOnHost(nodeAddress, nodeCredentials,
				DEFAULT_NUMBER_OF_CONNECTION_RETRIES, DEFAULT_WAITING_TIME);
	}

	public static void copyFileToHost(File localFile, String nodeAddress,
			LoginCredentials nodeCredentials, File remoteFile) {

		Boolean useIdentityFile = null;
		String credentials = null;
		String userName = nodeCredentials.getUsername();

		if (nodeCredentials instanceof LoginCredentialsPassword) {
			useIdentityFile = false;
			credentials = ((LoginCredentialsPassword) nodeCredentials)
					.getPassword();
		} else if (nodeCredentials instanceof LoginCredentialsPrivateKey) {
			useIdentityFile = true;
			credentials = ((LoginCredentialsPrivateKey) nodeCredentials)
					.getKey().getKeyPath();
		}

		SshClient ssh = null;
		try {
			ssh = new SshClient();
			ssh.createSession(nodeAddress, userName, false);

			log.info("connecting with username: " + userName);

			if (useIdentityFile) {
				ssh.addIdentityFile(credentials);
				log.info("connecting using identity file: " + credentials);
			} else {
				log.info("connecting using password: " + credentials);
				ssh.setSessionPassword(credentials);
			}

			ssh.connectSession();

			ChannelSftp sftp = ssh.getSftpChannel();
			sftp.connect();
			sftp.cd(remoteFile.getParentFile().getAbsolutePath());
			sftp.put(new FileInputStream(localFile), remoteFile.getName());
			log.info("copying local file \"" + localFile.getAbsolutePath()
					+ "\" to file \"" + remoteFile.getAbsolutePath()
					+ "\" on host " + nodeAddress);

			sftp.disconnect();
			ssh.disconnectSession();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean deleteFileOnHost(String nodeAddress,
			LoginCredentials nodeCredentials, File remoteFile) {

		CommandExecution exec = null;
		SshClient ssh = null;
		try {
			ssh = new SshClient();
			exec = ssh.getCommandExecutor(nodeAddress, nodeCredentials, "rm "
					+ remoteFile.getAbsolutePath());
		} catch (JSchException e1) {
			e1.printStackTrace();
		}
		Future<Integer> process = exec.execute();
		int status = Integer.MAX_VALUE;
		
		try {
			status = process.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exec.shutdown();
		if (status == 0)
			return true;
		return false;
	}
	
	/**
	 * shutdown impossible, can't access executor!
	 * @return
	 */
	public static <T> ExecutorCompletionService<T> getCachedThreadPoolExecutorCompletionService(){
		final ExecutorService executor = Executors.newCachedThreadPool();
		return new ExecutorCompletionService<T>(
				executor);
	}
	
	/**
	 * shutdown impossible, can't access executor!
	 * @return
	 */
	public static <T> ExecutorCompletionService<T> getFixedSizeThreadPoolExecutorCompletionService(int numberOfThreads){
		final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		return new ExecutorCompletionService<T>(
				executor);
	}
	
	/**
	 * shutdown impossible, can't access executor!
	 * @return
	 */
	public static <T> ExecutorCompletionService<T> getSingleThreadExecutorCompletionService(int numberOfThreads){
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		return new ExecutorCompletionService<T>(
				executor);
	}
	
	public static void main(String[] args) throws IOException {
		// waitSshReadyOnNode("localhost", new LoginCredentialsPrivateKey(
		// "tobias private key", "tobias", new PrivateKey("tobiasPK",
		// "/home/tobias/.ssh/id_rsa")));
		// copyFileToHost(File.createTempFile("test", ".tmp"),
		// ChefNodeManager.DEFAULT_CHEF_HOST,
		// new LoginCredentialsPassword("chef login creds", "root",
		// "redundant"), new File("/root/test"));

		deleteFileOnHost(ChefNodeManager.DEFAULT_CHEF_HOST,
				new LoginCredentialsPassword("chef login creds", "root",
						"redundant"), new File("/root/test"));

	}
}
