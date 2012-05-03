package gt.redundancyrouter.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProcessExecution implements Callable<Integer> {
	protected final ProcessBuilder pb;
	protected StringBuffer output = new StringBuffer();
	protected StringBuffer error = new StringBuffer();
	protected Process process;
	protected static ExecutorService executor = Executors.newFixedThreadPool(4);

	public ProcessExecution(String[] command) {
		pb = new ProcessBuilder(command);
	}

	public ProcessExecution(ProcessBuilder processBuilder) {
		pb = processBuilder;
	}

	public OutputStream getProcessInputStream() {
		return process.getOutputStream();
	}

	public StringBuffer getProcessOutput() {
		return this.output;
	}

	public StringBuffer getProcessError() {
		return this.error;
	}

	public Future<Integer> execute() {
		if (executor == null)
			executor = Executors.newFixedThreadPool(4);
		ExecutorCompletionService<Integer> processExecutor = new ExecutorCompletionService<Integer>(
				executor);
		return processExecutor.submit(this);
	}

	public void shutdown() {
		executor.shutdown();
	}

	public static void main(String args[]) throws IOException {

		String[] cmd = { "ls" };
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.directory(new File("/home/tobias"));
		// ProcessExecution pe = new ProcessExecution(cmd);
		ProcessExecution pe = new ProcessExecution(pb);

		ExecutorService executor = Executors.newFixedThreadPool(1);
		ExecutorCompletionService<Integer> processExecutor = new ExecutorCompletionService<Integer>(
				executor);
		int result = -1;
		Future<Integer> executingProcess = null;
		try {
			executingProcess = processExecutor.submit(pe);
			result = executingProcess.get(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			if (executingProcess != null)
				executingProcess.cancel(true);
			e.printStackTrace();
		}
		executor.shutdown();

		System.out.println("res: " + result);
		System.out.println("out: " + pe.getProcessOutput().substring(0));
		System.out.println("err: " + pe.getProcessError().substring(0));
	}

	@Override
	public Integer call() throws Exception {
		this.process = pb.start();
		new Thread(new Runnable() {
			public void run() {
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				try {
					while ((line = br.readLine()) != null) {
						output.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				InputStream is = process.getErrorStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				try {
					while ((line = br.readLine()) != null) {
						error.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();

		return process.waitFor();
	}

}
