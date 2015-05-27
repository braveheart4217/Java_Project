import java.io.*;
import java.net.*;



public class fileTransPort {

	private static final int SERVER_PORT = 2013;
	private ServerSocket Server;
	private Socket ClientList[];
	
	public fileTransPort() throws IOException
	{
		Server = new ServerSocket(SERVER_PORT);
		ClientList = new Socket[1000];
	}
	
	public fileTransPort(int num) throws IOException
	{
		Server = new ServerSocket(SERVER_PORT);
		ClientList = new Socket[num];
	}
	
	public ServerSocket getServer()
	{
		return Server;
	}
	
	public Socket[] getClient()
	{
		return ClientList;
	}
	
	
	public static void main(String[] args) throws IOException
	{
		fileTransPort ss = new fileTransPort(1000);
		
		int i = 0;
		while(true)
		{
			ss.getClient()[(i++)%1000] = ss.getServer().accept();
			System.out.println(ss.getClient()[i - 1].getRemoteSocketAddress().toString() + " is connected");
			new Thread( new FileThread(ss.getClient()[i - 1]) ).start();
		}
	}
}
