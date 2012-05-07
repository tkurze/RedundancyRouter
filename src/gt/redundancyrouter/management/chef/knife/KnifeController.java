package gt.redundancyrouter.management.chef.knife;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.jcraft.jsch.JSchException;

import gt.redundancyrouter.resources.credentials.LoginCredentials;
import gt.redundancyrouter.utils.Utils;
import gt.redundancyrouter.utils.process.ProcessExecution;
import gt.redundancyrouter.utils.ssh.SshClient;
import gt.redundancyrouter.utils.ssh.SshClient.CommandExecution;

/**
 * -s, --server-url URL Chef Server URL -k, --key KEY API Client Key --color Use
 * colored output -c, --config CONFIG The configuration file to use --defaults
 * Accept default values for all questions -e, --editor EDITOR Set the editor to
 * use for interactive commands -E, --environment ENVIRONMENT Set the Chef
 * environment -F, --format FORMAT Which format to use for output --no-color
 * Don't use colors in the output -n, --no-editor Do not open EDITOR, just
 * accept the data as is -u, --user USER API Client Username --print-after Show
 * the data after a destructive operation -V, --verbose More verbose output. Use
 * twice for max verbosity -v, --version Show chef version -y, --yes Say yes to
 * all prompts for confirmation -h, --help
 * 
 * @author tobias
 * 
 */

public class KnifeController {

	private static Logger logger = Logger.getLogger("KnifeController");

	// knife command
	private static final String KNIFE = "knife";

	final private String knifeHost;
	public String getKnifeHost() {
		return knifeHost;
	}

	final private LoginCredentials knifeHostCredentials;

	public KnifeController(String knifeHost,
			LoginCredentials knifeHostCredentials) {
		this.knifeHost = knifeHost;
		this.knifeHostCredentials = knifeHostCredentials;
	}

	public KnifeController() {
		this.knifeHost = null;
		this.knifeHostCredentials = null;
	}

	public static void main(String[] args) {
		System.out.println("getBootstrapCmdLine: "
				+ Utils.CommandArray2CommandLine(getBootstrapCmdLine(
						"ec2-174-129-44-67.compute-1.amazonaws.com",
						".aws/awsKeyPair.pem", "ubuntu", true, false,
						"ubuntu10.04-apt", true)));
	}

	/**
	 * @param bootstrap
	 *            args
	 * 
	 *            -i, --identity-file IDENTITY_FILE: The SSH identity file used
	 *            for authentication
	 * 
	 *            -N, --node-name NAME: The Chef node name for your new node
	 * 
	 *            -P, --ssh-password PASSWORD: The ssh password
	 * 
	 *            -x, --ssh-user USERNAME: The ssh username
	 * 
	 *            -p, --ssh-port PORT: The ssh port
	 * 
	 *            --bootstrap-version VERSION: The version of Chef to install
	 * 
	 *            --bootstrap-proxy PROXY_URL: The proxy server for the node
	 *            being bootstrapped
	 * 
	 *            --prerelease: Install pre-release Chef gems
	 * 
	 *            -r, --run-list RUN_LIST: Comma separated list of roles/recipes
	 *            to apply
	 * 
	 *            --template-file TEMPLATE: Full path to location of template to
	 *            use
	 * 
	 *            --sudo: Execute the bootstrap via sudo
	 * 
	 *            -d, --distro DISTRO: Bootstrap a distro using a template
	 *            centos5-gems fedora13-gems ubuntu10.04-gems ubuntu10.04-apt
	 * 
	 *            --no-host-key-verify: Disable host key verification
	 * 
	 */

	// knife bootstrap ec2-174-129-44-67.compute-1.amazonaws.com -i
	// .aws/awsKeyPair.pem -x ubuntu --sudo -d ubuntu10.04-apt

	public static String[] getBootstrapCmdLine(String host) {
		return new String[] { KNIFE, "bootstrap", host };
	}

	public static String[] getBootstrapCmdLine(String host, String credentials,
			String userName, boolean doSuDo, boolean hostKeyVerification,
			boolean useIdentityFile) {
		String[] cmd = getBootstrapCmdLine(host);
		int oldArrayLength = cmd.length;
		int newArrayLength = oldArrayLength + 4;
		if (doSuDo)
			newArrayLength++;
		if (!hostKeyVerification)
			newArrayLength++;
		cmd = java.util.Arrays.copyOf(cmd, newArrayLength);
		if (useIdentityFile) {
			cmd[oldArrayLength + 0] = "-i";
		} else {
			cmd[oldArrayLength + 0] = "-P";
		}
		cmd[oldArrayLength + 1] = credentials;
		cmd[oldArrayLength + 2] = "-x";
		cmd[oldArrayLength + 3] = userName;
		if (doSuDo)
			cmd[oldArrayLength + 4] = "--sudo";
		if (!hostKeyVerification && !doSuDo)
			cmd[oldArrayLength + 4] = "--no-host-key-verify";
		else if (!hostKeyVerification)
			cmd[oldArrayLength + 5] = "--no-host-key-verify";
		return cmd;
	}

	public static String[] getBootstrapCmdLine(String host, String credentials,
			String userName, boolean doSuDo, boolean hostKeyVerification,
			String distribution, boolean useIdentityFile) {

		String[] cmd = getBootstrapCmdLine(host, credentials, userName, doSuDo,
				hostKeyVerification, useIdentityFile);
		cmd = java.util.Arrays.copyOf(cmd, cmd.length + 2);
		cmd[cmd.length - 2] = "-d";
		cmd[cmd.length - 1] = distribution;
		return cmd;
	}

	public static String[] getBootstrapCmdLine(String host,
			String identityFile, String userName, boolean doSuDo,
			boolean hostKeyVerification, String distribution,
			String chefNodeName, boolean useIdentityFile) {
		String[] cmd = getBootstrapCmdLine(host, identityFile, userName,
				doSuDo, hostKeyVerification, distribution, useIdentityFile);
		cmd = java.util.Arrays.copyOf(cmd, cmd.length + 2);
		cmd[cmd.length - 2] = "-N";
		cmd[cmd.length - 1] = chefNodeName;
		return cmd;
	}

	/**
	 * Bootstraps (installs and sets-up chef-client, etc.) on a specified host.
	 * 
	 * @param host
	 *            Host where chef-client will be installed and configured
	 * @param credentials
	 *            Either the password to connect to the host via ssh or the
	 *            public key file to be used to identify the user on the node.
	 * @param userName
	 *            Name of the user to be used to login to the remote machine.
	 * @param doSuDo
	 *            Indicates whether sudo should be used to setup chef-client or
	 *            not.
	 * @param hostKeyVerification
	 *            Indicates whether the remote machine's host key should be
	 *            verified or not.
	 * @param os
	 *            Operating system installed on the remote machine.
	 * @param chefNodeName
	 *            The name of the remote machine to be registered with
	 *            chef-server.
	 * @param useIdentityFile
	 *            if set to true, credentials is interpreted as identity file
	 *            name rather then the user's password
	 * 
	 * @return Exit code of the knife command-line tool
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws KnifeControllerException
	 */
	public int bootstrapWithLocalKnife(String host, String credentials,
			String userName, boolean doSuDo, boolean hostKeyVerification,
			KnifeOperatingSystem os, String chefNodeName,
			boolean useIdentityFile) throws InterruptedException,
			ExecutionException, KnifeControllerException {

		if (os.equals(KnifeOperatingSystem.unknown))
			throw new KnifeControllerException(
					"Unknown operating system not supported");

		String[] cmd = KnifeController.getBootstrapCmdLine(host, credentials,
				userName, doSuDo, hostKeyVerification,
				KnifeController.os2distribution(os), chefNodeName,
				useIdentityFile);

		ProcessExecution knifeProcess = new ProcessExecution(cmd);
		logger.info("cmd: " + Utils.CommandArray2CommandLine(cmd));
		Future<Integer> process = knifeProcess.execute();
		logger.info("waiting for result.");
		int result = process.get();
		knifeProcess.shutdown();
		return result;
	}

	// unused for the moment
	public int bootstrapWithLocalKnife(String host, String credentials,
			String userName, boolean doSuDo, boolean hostKeyVerification,
			KnifeOperatingSystem os, String chefNodeName,
			boolean useIdentityFile, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {

		String[] cmd = KnifeController.getBootstrapCmdLine(host, credentials,
				userName, doSuDo, hostKeyVerification,
				KnifeController.os2distribution(os), chefNodeName,
				useIdentityFile);

		ProcessExecution knifeProcess = new ProcessExecution(cmd);
		logger.info("executing cmd: " + Utils.CommandArray2CommandLine(cmd));
		Future<Integer> process = knifeProcess.execute();
		logger.info("waiting " + timeout + " " + unit + " for result.");
		int result = process.get(timeout, unit);
		knifeProcess.shutdown();
		return result;
	}

	public int bootstrapWithRemoteKnife(String host, String credentials,
			String userName, boolean doSuDo, boolean hostKeyVerification,
			KnifeOperatingSystem os, String chefNodeName,
			boolean useIdentityFile, long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException, KnifeControllerException {

		if (os.equals(KnifeOperatingSystem.unknown))
			throw new KnifeControllerException(
					"Unknown operating system not supported");

		String[] cmd = KnifeController.getBootstrapCmdLine(host, credentials,
				userName, doSuDo, hostKeyVerification,
				KnifeController.os2distribution(os), chefNodeName,
				useIdentityFile);

		CommandExecution exec = null;
		try {
			exec = new SshClient().getCommandExecutor(this.knifeHost, this.knifeHostCredentials, Utils.CommandArray2CommandLine(cmd));
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("executing cmd: " + Utils.CommandArray2CommandLine(cmd));
		Future<Integer> process = exec.execute();
		logger.info("waiting " + timeout + " " + unit + " for result.");
		int result = process.get(timeout, unit);
		exec.shutdown();
		return result;
	}
	
	public int bootstrapWithRemoteKnife(String host, String credentials,
			String userName, boolean doSuDo, boolean hostKeyVerification,
			KnifeOperatingSystem os, String chefNodeName,
			boolean useIdentityFile) throws InterruptedException,
			ExecutionException, KnifeControllerException {

		if (os.equals(KnifeOperatingSystem.unknown))
			throw new KnifeControllerException(
					"Unknown operating system not supported");

		String[] cmd = KnifeController.getBootstrapCmdLine(host, credentials,
				userName, doSuDo, hostKeyVerification,
				KnifeController.os2distribution(os), chefNodeName,
				useIdentityFile);

		CommandExecution exec = null;
		try {
			exec = new SshClient().getCommandExecutor(this.knifeHost, this.knifeHostCredentials, Utils.CommandArray2CommandLine(cmd));
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("executing cmd: " + Utils.CommandArray2CommandLine(cmd));
		Future<Integer> process = exec.execute();
		logger.info("waiting for result.");
		int result = process.get();
		exec.shutdown();
		return result;
	}

	public int bootstrap(String host, String credentials, String userName,
			boolean doSuDo, boolean hostKeyVerification,
			KnifeOperatingSystem os, String chefNodeName,
			boolean useIdentityFile) throws InterruptedException,
			ExecutionException, KnifeControllerException {

		if (os.equals(KnifeOperatingSystem.unknown))
			throw new KnifeControllerException(
					"Unknown operating system not supported");

		if (this.isKnifeRemoteHostSet())
			return this.bootstrapWithRemoteKnife(host, credentials, userName,
					doSuDo, hostKeyVerification, os, chefNodeName,
					useIdentityFile);
		else
			return this.bootstrapWithLocalKnife(host, credentials, userName,
					doSuDo, hostKeyVerification, os, chefNodeName,
					useIdentityFile);
	}

	protected boolean isKnifeRemoteHostSet() {
		return knifeHost == null ? false : true;
	}

	public LoginCredentials getKnifeHostCredentials() {
		return knifeHostCredentials;
	}

	private static String os2distribution(KnifeOperatingSystem os) {
		if (KnifeOperatingSystem.centos == os)
			return "centos5-gems";
		if (KnifeOperatingSystem.fedora == os)
			return "fedora13-gems";
		if (KnifeOperatingSystem.ubuntu == os)
			return "ubuntu10.04-apt";
		if (KnifeOperatingSystem.ubuntuGems == os)
			return "ubuntu10.04-gems";
		return null;
	}

	/**
	 * enumeration of supported operating systems Only certain os versions are
	 * supported, but are omitted in the following
	 * 
	 * centos5-gems fedora13-gems ubuntu10.04-gems ubuntu10.04-apt
	 * 
	 * @author tobias
	 * 
	 */
	public static enum KnifeOperatingSystem {
		centos, fedora, ubuntu, ubuntuGems, unknown
	}

	public static class KnifeControllerException extends Exception {
		private static final long serialVersionUID = 1L;

		public KnifeControllerException(String msg) {
			super(msg);
		}

		public KnifeControllerException(String msg, Throwable t) {
			super(msg, t);
		}
	}
}