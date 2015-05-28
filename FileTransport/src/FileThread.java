import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.logging.*;

public class FileThread implements Runnable {

	private Socket client;
	private DataInputStream dis;
	private RandomAccessFile fos;
	private infor translateRecord = new infor();
	byte[] sendBytes = new byte[1024];
	byte classInfor[] = new byte[1];
	
	private Logger log;

	public FileThread(Socket client) throws SecurityException, IOException {
		this.client = client;
		
		log = Logger.getLogger("test2");
		FileHandler fileHandler = new FileHandler("./log/test2.xml");
		fileHandler.setLevel(Level.FINE);
		log.addHandler(fileHandler);
	}

	public void CreateTable() {
		// 在数据库建表
	}

	
	public void recvAllFile() throws IOException, ClassNotFoundException {

		int size = 0;
		int fileNum = 0;
		boolean fileStart = false;
		
		log.info("开始接受文件");

		do {

			size = dis.readInt(); // 读取序列化数组长度
			if (size != classInfor.length)
				classInfor = new byte[size];
			dis.read(classInfor); // 读取序列化数组
			translateRecord = infor.NonSerialize(classInfor);

			if (fileStart == false) {
				System.out.println("----开始接收文件<"
						+ translateRecord.getFileName() + ">----");
				fileStart = true;
			}

			fos = new RandomAccessFile("./data/"
					+ translateRecord.getFileName(), "rw");
			dis.read(sendBytes, 0, translateRecord.getReadLength()); // 读取文件数据
			fos.seek(translateRecord.getOffset());
			fos.write(sendBytes, 0, translateRecord.getReadLength());
			fos.close();

			log.info("fileName:" + translateRecord.getFileName()
					+ " readLength:" + translateRecord.getReadLength()
					+ " offset:" + translateRecord.getOffset());

			if (translateRecord.getFlag() == true) {
				fileNum++;
				fileStart = false;
				System.out.println("----接收文件<" + translateRecord.getFileName()
						+ ">成功-------");
				
				log.info(translateRecord.getFileName() + "已经接受成功！");

			}

		} while (fileNum < translateRecord.getNum());
		
		log.info("文件传输完成！");

		dis.close();
		client.close();// 关闭连接
	}

	public void run() {

		try {

			dis = new DataInputStream(client.getInputStream());

			recvAllFile();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			log.info("IOException occered!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			
			log.info("ClassNotFoundException occered!");
			e.printStackTrace();
		}

	}

}