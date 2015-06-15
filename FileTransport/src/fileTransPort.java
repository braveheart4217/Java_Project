import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class fileTransPort {

	private static final int SERVER_PORT = 2013;
	private ServerSocket Server;
	private Socket ClientList[];
	private static Logger log;

	private final static int NUM = 4;

	private static ExecutorService exec = Executors.newFixedThreadPool(Runtime
			.getRuntime().availableProcessors() * NUM);

	public fileTransPort() throws IOException {
		Server = new ServerSocket(SERVER_PORT);
		ClientList = new Socket[1000];

		log = Logger.getLogger("test");
		FileHandler fileHandler = new FileHandler("./log/test.xml");
		fileHandler.setLevel(Level.FINE);
		log.addHandler(fileHandler);
	}

	public fileTransPort(int num) throws IOException {
		Server = new ServerSocket(SERVER_PORT);
		ClientList = new Socket[num];

		log = Logger.getLogger("test");
		FileHandler fileHandler = new FileHandler("./log/test.xml");
		fileHandler.setLevel(Level.FINE);
		log.addHandler(fileHandler);
	}

	public static Logger getLogger() {

		return log;
	}

	public ServerSocket getServer() {
		return Server;
	}

	public Socket[] getClient() {
		return ClientList;
	}

	public static void main(String[] args) throws IOException {
		fileTransPort ss = new fileTransPort(1000);

		int i = 0;
		while (true) {
			ss.getClient()[(i++) % 1000] = ss.getServer().accept();
			System.out.println(ss.getClient()[i - 1].getRemoteSocketAddress()
					.toString() + " is connected");
			
			log.info(ss.getClient()[i - 1].getRemoteSocketAddress().toString()
					+ " is connected");
			
//			exec.execute(new FileThread(ss.getClient()[i - 1]).start());
			exec.execute( new Thread( new FileThread(ss.getClient()[i - 1]) ) );

			
		}
	}
}
