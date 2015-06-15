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
import java.util.logging.*;

public class Client extends Socket {

	// private static final String SERVER_IP = "10.176.172.123";
	private static final String SERVER_IP = "127.0.0.1";
	// private static final String SERVER_IP = "10.177.6.182";
	private static final int SERVER_PORT = 2013;
	

	private static Socket client;
	private boolean isReady = true;     //�Ƿ�������ӵ���������־
	private RandomAccessFile fis;
	private DataOutputStream dos;

	private static File breakInfor = new File("./infor"); // ��Ŷϵ���Ϣ���ļ�
	private readExcel RdExcel;             // xls�ļ�����������

	private String searchPath = "./data/"; // ָ���ļ�����·��
	private String xlsName = "test.xls";   // ָ��xls�ļ����ļ���
	private File file = new File(searchPath + xlsName);

	private static infor translateRecord = new infor();
	private byte[] sendBytes = new byte[1024]; // ��ȡ�ļ�buff
	private static byte buf[] = null;          // ���л�buff
	
	private static Logger log;
	private FileHandler fileHandler = null;
	

	public Client() throws SecurityException, IOException {

		log = Logger.getLogger("test");
        fileHandler = new FileHandler("./log/test.xml"); 
        fileHandler.setLevel(Level.OFF); 
        log.addHandler(fileHandler); 
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

		dos.writeInt(buf.length); // �ȷ����л���byte���鳤��
		// dos.flush();

		dos.write(buf, 0, buf.length); // �ٷ����л�����
		// dos.flush();

		fis.seek(offset); // ��ָ����offsetλ�ö�ȡ�ļ���Ϊ�ϵ�������׼��
		read = fis.read(sendBytes, 0, sendBytes.length);// ��ȡָ���ļ�
		dos.write(sendBytes, 0, readLength); // �ٷ���ʵ����
		
		log.fine("fileName:" + fileName + " readLength:" + readLength + " offset:" + offset ); 

		return read;
	}

	// ָ���ļ������ļ���������ƫ�����Ϳ��Է��ʹ��ļ�
	public void Send(RandomAccessFile fis, int num, long offset)
			throws IOException {

		long fileLength = file.length();
		String fileName = file.getName();
		boolean endFlag = false;
		int readLength = 1024; // ����ÿ�η����ļ����ֽ���

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
//		file.delete();
	}

	/*
	 * linkFile����˵�� filed ָ���ļ���xls�ļ��е��ֶ��� mode ָ���ļ��Ĵ�ģʽ filename
	 * ��������Է���xls�ļ�ʱʹ�ã����������Ϊ�� i ָ���ֶ���xls�ļ��е�����
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

		Send(linkFile("���˼��", "rw", null, i), num, 0);
		Send(linkFile("�Ǽ���", "rw", null, i), num, 0);
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
		byte SerializeBuf[] = new byte[size]; // �������л�����
		fis.read(SerializeBuf, 0, SerializeBuf.length);

		fis.close(); // ����fis�ļ�������رղ��ܳɹ�ɾ���ϵ���Ϣ�ļ�����Ϊ��ռ���ļ�
		if (breakInfor.delete()) // ɾ���ϵ���Ϣ�ļ�
		{
			System.out.println("�ϵ���Ϣ�ļ�ɾ���ɹ�");
		}

		translateRecord = infor.NonSerialize(SerializeBuf);
		System.out.println("�ϵ�������ʼ,�ϵ��ļ���" + translateRecord.getFileName()
				+ ",�ϵ�λ����" + translateRecord.getOffset());
		file = new File("./data/" + translateRecord.getFileName()); // �򿪶ϵ��ļ�
		fis = new RandomAccessFile(file, "rw"); // ������fis
		
		newClient();
		if (client.isConnected()) // �жϵ�ǰ����״̬
			init();
		Send(fis, translateRecord.getNum(), translateRecord.getOffset());
	}
	
	public void newClient() throws UnknownHostException, IOException{
		if(isReady){
			client = new Socket(SERVER_IP, SERVER_PORT);
			isReady = false;
			while(!client.isConnected());  //ֱ�����ӵ���������֮��ŷ���
		}
		
	}

	public static void SockerHander() throws IOException{
		
		FileOutputStream fos = new FileOutputStream(breakInfor);
		buf = infor.Serialize(translateRecord);
		fos.write(buf.length);
		fos.write(buf, 0, buf.length);
		fos.close();
		
		log.fine("�ϵ���Ϣ������ϣ�"); 
		
		System.out.println("�����Ѿ��Ͽ����ӣ����򼴽��˳���������������������");
		client.close();
		System.exit(1); 
	}
	
	public Runnable start() throws IOException, ClassNotFoundException,
			InterruptedException {
		
		
//		while (true) {
			
			if (breakInfor.exists()) {
				try{
					log.fine("�ϵ�������ʼ��"); 
					recoverTransport(); // ��ʼ�ļ�����
				}
				catch (SocketException e) {
					
					log.fine("Socket error!"); 
					SockerHander();
					e.printStackTrace();
				}finally{
					log.removeHandler(fileHandler);
					client.close();
				}
			}

			if (file.exists()) { // ��xls�ļ�����ʱ���������ӵ������������������ӵ�������
				
				newClient();
				if (client.isConnected()) // �жϵ�ǰ����״̬
					init();
				
				log.info("�������俪ʼ��"); 
				transport(); // ��ʼ�����ļ�����
				
			} else {
				System.out.println("wait for the transport file!");
				Thread.currentThread();
				Thread.sleep(1000 * 30);
			}

			
//		}
			
//		log.removeHandler(fileHandler);
		fileHandler.close();
		
		return null;
	}
	;

/*
	public static void main(String[] args) throws IOException,
	ClassNotFoundException, InterruptedException {
		try {

			new Client().start();
			

		} catch (SocketException e) {

			log.fine("Socket error!"); 
			SockerHander();
			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
			log.fine("IOException error!"); 
			
		}
	}
*/
}
