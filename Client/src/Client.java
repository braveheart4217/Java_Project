import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.text.BreakIterator;

@SuppressWarnings("unused")
public class Client extends Socket {

	// private static final String SERVER_IP = "10.176.172.123";
	private static final String SERVER_IP = "127.0.0.1";
	//private static final String SERVER_IP = "10.177.6.182";
	private static final int SERVER_PORT = 2013;

	private Socket client;
//	private FileInputStream fis;
	private RandomAccessFile fis;
	private DataOutputStream dos;
	private File file = null;
	private static File breakInfor = new File("./infor"); // 存放断点信息的文件
	private readExcel RdExcel; // xls文件解析器对象
	private String path = "./data/test.xls"; // 指定xls文件的路径

	private static infor translateRecord = new infor();
	private byte[] sendBytes = new byte[1024]; // 读取文件buff
	private static byte buf[] = null; // 序列化buff

	public Client() {
		try {
			try {
				client = new Socket(SERVER_IP, SERVER_PORT);
				RdExcel = new readExcel(path);
				dos = new DataOutputStream(client.getOutputStream());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		fis.seek(offset);//////从指定的offset位置读取文件，为断点续传做准备
		read = fis.read(sendBytes, 0, sendBytes.length);//读取指定文件
		dos.write(sendBytes, 0, readLength); // 再发真实数据

		return read;
	}

	//指定文件流，文件数量，和偏移量就可以发送此文件
	public void Send(RandomAccessFile fis, int num,long offset) throws IOException {

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

	public void sendInfor(int i, int num) throws IOException {

		file = new File("./data/"
				+ RdExcel.getSpecifiedFieldValue(i + 1, "个人简介"));
		fis = new RandomAccessFile(file,"rw");
		Send(fis, num, 0);

		file = new File("./data/"
				+ RdExcel.getSpecifiedFieldValue(i + 1, "登记照"));
		fis = new RandomAccessFile(file,"rw");
		Send(fis, num, 0);
	}

	public void start() throws IOException, ClassNotFoundException {

		if (breakInfor.exists()) {
			fis = new RandomAccessFile(breakInfor,"rw");
			int size = fis.read();
			byte SerializeBuf[] = new byte[size];    //开辟序列化数组
			fis.read(SerializeBuf, 0, SerializeBuf.length);
			
			fis.close();//首先fis文件流必须关闭才能成功删除断点信息文件，因为其占用文件
			if(breakInfor.delete())  //删除断点信息文件
			{
				System.out.println("断点信息文件删除成功");
			}
			
			
			translateRecord = infor.NonSerialize(SerializeBuf);
			System.out.println("断点续传开始,断点文件是" + translateRecord.getFileName()
					+ ",断点位置是" + translateRecord.getOffset());
			file = new File("./data/"+translateRecord.getFileName());   //打开断点文件
			fis = new RandomAccessFile(file,"rw");                     //关联带fis
			Send(fis,translateRecord.getNum(),translateRecord.getOffset());
			System.out.println("断点续传完成！程序退出");
			client.close();
			System.exit(1);  //
		}

		int i = 0;
		int num = (RdExcel.rowNum - 1) * 2 + 1;

		file = new File(path);
		fis = new RandomAccessFile(file,"rw");
		Send(fis, num, 0);

		for (; i < RdExcel.rowNum - 1; i++) {
			sendInfor(i, num);
		}

		if (dos != null)
			dos.close();
		client.close();

		System.out.println("transport file finished");

		return;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		try {

			new Client().start();

		} catch (SocketException e) {

			System.out.println("the internet hava been disconnected!");
			FileOutputStream fos = new FileOutputStream(breakInfor);
			buf = infor.Serialize(translateRecord);
			fos.write(buf.length);
			fos.write(buf, 0, buf.length);
			fos.close();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

}
