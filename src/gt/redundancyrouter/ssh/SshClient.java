package gt.redundancyrouter.ssh;

/*

 Source code follows: adapted from Exec.java found in jsch examples

 By vaidya.anand at gmail.com

 anand@aries5672:~/workspace/samplejsch$ cat samplejsch.java

 */

import com.jcraft.jsch.*;

import gt.redundancyrouter.chefMgmt.ChefNodeManager;
import gt.redundancyrouter.dataService.resources.credentials.LoginCredentials;
import gt.redundancyrouter.dataService.resources.credentials.LoginCredentialsPassword;
import gt.redundancyrouter.dataService.resources.credentials.LoginCredentialsPrivateKey;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SshClient {

	public static long DEFAULT_WAITING_TIME_PER_CYCLE = 1000;
	public static long MAXIMUM_WAITING_TIME_INACTIVITY = 10000;

	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger("SshClient");

	protected String host = null;
	protected Integer port = null;
	protected String user = null;

	protected final JSch jsch;

	protected String knownHostsFile = null;
	protected Session session = null;;

	public SshClient() throws JSchException {
		this.jsch = new JSch();
	}

	public SshClient(String knownHostsFile) throws JSchException {
		this.knownHostsFile = knownHostsFile;
		this.jsch = new JSch();
		jsch.setKnownHosts(this.knownHostsFile);
	}

	public void addIdentityFile(String identityFileName) throws JSchException {
		this.jsch.addIdentity(identityFileName);
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void createSession(String host, String user, Boolean hostKeyChecking)
			throws JSchException {
		log.info("creating session");
		this.session = this.getSession(host, user, hostKeyChecking);
	}

	public void createSession(String host, Integer port, String user,
			Boolean hostKeyChecking) throws JSchException {
		log.info("creating session");
		this.session = this.getSession(host, port, user, hostKeyChecking);
	}

	public Session getSession(String host, String user, Boolean hostKeyChecking)
			throws JSchException {
		return this.getSession(host, 22, user, hostKeyChecking);
	}

	public Session getSession(String host, Integer port, String user,
			Boolean hostKeyChecking) throws JSchException {
		if (this.session != null)
			return this.session;
		this.setHost(host);
		this.setUser(user);
		this.session = jsch.getSession(user, host, port);
		java.util.Properties config = new java.util.Properties();
		if (hostKeyChecking) {
			log.info("strict host key checking enabled");
			config.put("StrictHostKeyChecking", "yes");
		} else {
			log.info("strict host key checking disabled");
			config.put("StrictHostKeyChecking", "no");
		}
		session.setConfig(config);
		return session;
	}

	public void setSessionPassword(String pw) {
		log.info("password set");
		this.session.setPassword(pw);
	}

	public void connectSession() throws JSchException {
		log.info("connecting session");
		this.session.connect();
	}

	public void disconnectSession() throws JSchException {
		log.info("disconnecting session");
		this.session.disconnect();
	}

	public ChannelExec getExecutionChannel() throws JSchException {
		Channel channel = session.openChannel("exec");
		log.info("opening execution channel");
		return (ChannelExec) channel;
	}

	public ChannelShell getShellChannel() throws JSchException {
		Channel channel = session.openChannel("shell");
		log.info("opening shell channel");
		return (ChannelShell) channel;
	}

	public ChannelSftp getSftpChannel() throws JSchException {
		Channel channel = session.openChannel("sftp");
		log.info("opening sftp channel");
		return (ChannelSftp) channel;
	}

	public void listKnownHosts() {
		HostKeyRepository hkr = this.jsch.getHostKeyRepository();
		HostKey[] hks = hkr.getHostKey();
		if (hks != null) {
			System.out.println("Host keys in "
					+ hkr.getKnownHostsRepositoryID());
			for (int i = 0; i < hks.length; i++) {
				HostKey hk = hks[i];
				System.out.println(hk.getHost() + " " + hk.getType() + " "
						+ hk.getFingerPrint(jsch));
			}
			System.out.println("");
		}
	}

	public void addHostKey(byte[] key) throws JSchException {
		HostKeyRepository hkr = this.jsch.getHostKeyRepository();
		HostKey hk = new HostKey(this.host, key);
		UserInfo userInfo = new MyUserInfo();
		hkr.add(hk, userInfo);
		log.info("hostkey added");
	}

	/**
	 * Executes the specified command on the specified host, using
	 * loginCredentials.
	 * 
	 * The function can't respond to requests on the default input! If a command
	 * waits for an input, the execution will be terminated after a certain
	 * waiting time without changes to stderr or stdin.
	 * 
	 * @param nodeAddress
	 * @param nodeCredentials
	 * @param command
	 * @return
	 */
	public CommandExecution getCommandExecutor(String nodeAddress,
			LoginCredentials nodeCredentials, String command) {
		return new CommandExecution(nodeAddress, nodeCredentials, command);
	}

	public static ChannelExec getExecutionChannel(Session session, String cmd)
			throws JSchException {
		Channel channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(cmd);
		return ((ChannelExec) channel);
	}

	public static class CommandExecution implements Callable<Integer> {

		protected static ExecutorService executor = Executors
				.newFixedThreadPool(4);

		protected StringBuffer output = new StringBuffer();
		protected StringBuffer error = new StringBuffer();

		private Long timeStamp = System.currentTimeMillis();

		private String nodeAddress;
		private LoginCredentials nodeCredentials;
		private String command;

		public CommandExecution(String nodeAddress,
				LoginCredentials nodeCredentials, String command) {
			this.nodeAddress = nodeAddress;
			this.nodeCredentials = nodeCredentials;
			this.command = command;
		}

		public Future<Integer> execute() {
			if (executor == null)
				executor = Executors.newFixedThreadPool(4);
			if (executor.isShutdown())
				executor = Executors.newFixedThreadPool(4);
			ExecutorCompletionService<Integer> commandExecutor = new ExecutorCompletionService<Integer>(
					executor);
			return commandExecutor.submit(this);
		}

		public void shutdown() {
			if (!executor.isShutdown())
				executor.shutdown();
		}

		@Override
		public Integer call() {
			int exitstatus = Integer.MAX_VALUE;
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

			ChannelExec c = null;
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

				c = ssh.getExecutionChannel();

				c.setInputStream(null);
				c.setCommand(command);
				final InputStream is = c.getInputStream();
				final InputStream err = c.getErrStream();

				log.info("executing command: " + command);
				c.connect();

				new Thread(new Runnable() {
					public void run() {
						String line;
						BufferedReader bufferedInputReader = new BufferedReader(
								new InputStreamReader(is));
						try {
							while ((line = bufferedInputReader.readLine()) != null) {
								output.append(line);
								synchronized (timeStamp) {
									timeStamp = System.currentTimeMillis();
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();

				new Thread(new Runnable() {
					public void run() {
						String line;
						BufferedReader bufferedErrorReader = new BufferedReader(
								new InputStreamReader(err));
						try {
							while ((line = bufferedErrorReader.readLine()) != null) {
								error.append(line);
								synchronized (timeStamp) {
									timeStamp = System.currentTimeMillis();
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();

				while (!c.isClosed()) {
					synchronized (this.timeStamp) {
						if (System.currentTimeMillis() - this.timeStamp > MAXIMUM_WAITING_TIME_INACTIVITY) {
							log.warn("command execution seems inactive, canceling!");
							break;
						}
					}
					log.info("waiting for command to finish.");
					try {
						Thread.sleep(SshClient.DEFAULT_WAITING_TIME_PER_CYCLE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				exitstatus = c.getExitStatus();

			} catch (JSchException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (c != null) {
					c.disconnect();
				}

				if (ssh != null) {
					try {
						ssh.disconnectSession();
					} catch (JSchException e) {
						e.printStackTrace();
					}
				}
			}
			return exitstatus;
		}
	}

	public static void main(String[] args) throws JSchException {
		SshClient ssh = new SshClient();
		CommandExecution exec = ssh
				.getCommandExecutor(
						ChefNodeManager.DEFAULT_CHEF_HOST,
						new LoginCredentialsPassword("chef login creds",
								"root", "redundant"),
						// "ps aux");
						"knife bootstrap 23.20.100.107 -i /tmp/creds3268697130713915657.tmp -x ubuntu --sudo --no-host-key-verify -d ubuntu10.04-apt -N mytestNode1");
		Future<Integer> result = exec.execute();

		System.out.println("result: " + result);
		exec.shutdown();
	}

	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			Object[] options = { "yes", "no" };
			int foo = JOptionPane.showOptionDialog(null, str, "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return foo == 0;
		}

		String passwd;
		JTextField passwordField = (JTextField) new JPasswordField(20);

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			Object[] ob = { passwordField };
			int result = JOptionPane.showConfirmDialog(null, ob, message,
					JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				passwd = passwordField.getText();
				return true;
			} else {
				return false;
			}
		}

		public void showMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0);
		private Container panel;

		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt, boolean[] echo) {
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts = new JTextField[prompt.length];
			for (int i = 0; i < prompt.length; i++) {
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]), gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if (echo[i]) {
					texts[i] = new JTextField(20);
				} else {
					texts[i] = new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if (JOptionPane.showConfirmDialog(null, panel, destination + ": "
					+ name, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
				String[] response = new String[prompt.length];
				for (int i = 0; i < prompt.length; i++) {
					response[i] = texts[i].getText();
				}
				return response;
			} else {
				return null; // cancel
			}
		}
	}
}