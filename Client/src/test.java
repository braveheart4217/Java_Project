import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test {

	private final static int NUM = 4;

//	private static ExecutorService exec = Executors.newFixedThreadPool(Runtime
//			.getRuntime().availableProcessors() * NUM);// Executors.newCachedThreadPool(1000);

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		try {

			for(int i = 0;i<100;i++){
//				exec.execute(new Client().start());
				new Thread(new Client().start());
			}
			
		} catch (SocketException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
//		exec.wait();
//		exec.shutdown();
	}

}
