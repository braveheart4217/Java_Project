import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.BreakIterator;

@SuppressWarnings("unused")
public class Client extends Socket {

	// private static final String SERVER_IP = "10.176.172.123";
	private static final String SERVER_IP = "127.0.0.1";
	// private static final String SERVER_IP = "10.177.6.182";
	private static final int SERVER_PORT = 2013;

	private static Socket client;
	private boolean isReady = true;     //是否可以连接到服务器标志
	private RandomAccessFile fis;
	private DataOutputStream dos;

	private static File breakInfor = new File("./infor"); // 存放断点信息的文件
	private readExcel RdExcel;             // xls文件解析器对象

	private String searchPath = "./data/"; // 指定文件搜索路径
	private String xlsName = "test.xls";   // 指定xls文件的文件名
	private File file = new File(searchPath + xlsName);

	private static infor translateRecord = new infor();
	private byte[] sendBytes = new byte[1024]; // 读取文件buff
	private static byte buf[] = null;          // 序列化buff

	public Client() {

	}

	public void init() throws UnknownHostException, IOException {

		dos = new DataOutputStream(client.getOutputStream());
		RdExcel = new readExcel(searchPath + xlsName);
	}

	public int sendData(int num, int readLength, long offset, String fileName,
			boolean endFlag) throws IOException {
		int read = 0;

		translateRecord.setAll(num, readLength, offset, fileName, endFlag);

		buf = infor.Serialize(translateRecord);

		dos.writeInt(buf.length); // 先发序列化的byte数组长度
		// dos.flush();

		dos.write(buf, 0, buf.length); // 再发序列化数组
		// dos.flush();

		fis.seek(offset); // 从指定的offset位置读取文件，为断点续传做准备
		read = fis.read(sendBytes, 0, sendBytes.length);// 读取指定文件
		dos.write(sendBytes, 0, readLength); // 再发真实数据

		return read;
	}

	// 指定文件流，文件数量，和偏移量就可以发送此文件
	public void Send(RandomAccessFile fis, int num, long offset)
			throws IOException {

		long fileLength = file.length();
		String fileName = file.getName();
		boolean endFlag = false;
		int readLength = 1024; // 定义每次发送文件的字节数

		while (offset < fileLength) {

			if (offset + readLength < fileLength) {

				offset += sendData(num, readLength, offset, fileName, endFlag);

			} else {

				endFlag = true;
				offset += sendData(num, (int) (fileLength - offset), offset,
						fileName, endFlag);
			}
		}
		fis.close();
	}

	/*
	 * linkFile函数说明 filed 指明文件在xls文件中的字段名 mode 指明文件的打开模式 filename
	 * 其仅仅正对发送xls文件时使用，其余情况下为空 i 指明字段在xls文件中的行数
	 */
	public RandomAccessFile linkFile(String filed, String mode,
			String fileName, int i) throws FileNotFoundException {
		if (filed != null) {
			fileName = RdExcel.getSpecifiedFieldValue(i + 1, filed);
		}
		file = new File(searchPath + fileName);
		fis = new RandomAccessFile(file, mode);
		return fis;
	}

	public void sendFile(int i, int num) throws IOException {

		Send(linkFile("个人简介", "rw", null, i), num, 0);
		Send(linkFile("登记照", "rw", null, i), num, 0);
	}

	public void transport() throws IOException {

		int i = 0;
		int num = (RdExcel.rowNum - 1) * 2 + 1;

		Send(linkFile(null, "rw", xlsName, 0), num, 0); // send xls file

		for (; i < RdExcel.rowNum - 1; i++) {
			sendFile(i, num);
		}

		if (dos != null)
			dos.close();
		client.close();

		System.out.println("transport file finished");

	}

	public void recoverTransport() throws IOException, ClassNotFoundException {
		fis = new RandomAccessFile(breakInfor, "rw");
		int size = fis.read();
		byte SerializeBuf[] = new byte[size]; // 开辟序列化数组
		fis.read(SerializeBuf, 0, SerializeBuf.length);

		fis.close(); // 首先fis文件流必须关闭才能成功删除断点信息文件，因为其占用文件
		if (breakInfor.delete()) // 删除断点信息文件
		{
			System.out.println("断点信息文件删除成功");
		}

		translateRecord = infor.NonSerialize(SerializeBuf);
		System.out.println("断点续传开始,断点文件是" + translateRecord.getFileName()
				+ ",断点位置是" + translateRecord.getOffset());
		file = new File("./data/" + translateRecord.getFileName()); // 打开断点文件
		fis = new RandomAccessFile(file, "rw"); // 关联带fis
		
		newClient();
		if (client.isConnected()) // 判断当前连接状态
			init();
		Send(fis, translateRecord.getNum(), translateRecord.getOffset());

	}
	
	public void newClient() throws UnknownHostException, IOException{
		if(isReady){
			client = new Socket(SERVER_IP, SERVER_PORT);
			isReady = false;
			while(!client.isConnected());  //直道连接到到服务器之后才返回
		}
		
	}

	@SuppressWarnings("static-access")
	public void start() throws IOException, ClassNotFoundException,
			InterruptedException {

		while (true) {

			if (breakInfor.exists()) {
				recoverTransport(); // 开始文件续传
			}

			if (file.exists()) { // 当xls文件存在时，才能连接到服务器，否则不能连接到服务器
				
				newClient();
				if (client.isConnected()) // 判断当前连接状态
					init();
				
				transport(); // 开始正常文件传输
			} else {

				System.out.println("wait for the transport file!");
				Thread.currentThread().sleep(1000 * 10);
			}

		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		try {

			new Client().start();

		} catch (SocketException e) {

			FileOutputStream fos = new FileOutputStream(breakInfor);
			buf = infor.Serialize(translateRecord);
			fos.write(buf.length);
			fos.write(buf, 0, buf.length);
			fos.close();
			System.out.println("网络已经断开连接，程序即将退出，检查网络后请重启程序！");
			client.close();
			System.exit(1); 

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

}
