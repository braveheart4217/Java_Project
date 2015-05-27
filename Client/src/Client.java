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
	private static File breakInfor = new File("./infor"); // ��Ŷϵ���Ϣ���ļ�
	private readExcel RdExcel; // xls�ļ�����������
	private String path = "./data/test.xls"; // ָ��xls�ļ���·��

	private static infor translateRecord = new infor();
	private byte[] sendBytes = new byte[1024]; // ��ȡ�ļ�buff
	private static byte buf[] = null; // ���л�buff

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

		dos.writeInt(buf.length); // �ȷ����л���byte���鳤��
		// dos.flush();

		dos.write(buf, 0, buf.length); // �ٷ����л�����
		// dos.flush();
		
		fis.seek(offset);//////��ָ����offsetλ�ö�ȡ�ļ���Ϊ�ϵ�������׼��
		read = fis.read(sendBytes, 0, sendBytes.length);//��ȡָ���ļ�
		dos.write(sendBytes, 0, readLength); // �ٷ���ʵ����

		return read;
	}

	//ָ���ļ������ļ���������ƫ�����Ϳ��Է��ʹ��ļ�
	public void Send(RandomAccessFile fis, int num,long offset) throws IOException {

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
	}

	public void sendInfor(int i, int num) throws IOException {

		file = new File("./data/"
				+ RdExcel.getSpecifiedFieldValue(i + 1, "���˼��"));
		fis = new RandomAccessFile(file,"rw");
		Send(fis, num, 0);

		file = new File("./data/"
				+ RdExcel.getSpecifiedFieldValue(i + 1, "�Ǽ���"));
		fis = new RandomAccessFile(file,"rw");
		Send(fis, num, 0);
	}

	public void start() throws IOException, ClassNotFoundException {

		if (breakInfor.exists()) {
			fis = new RandomAccessFile(breakInfor,"rw");
			int size = fis.read();
			byte SerializeBuf[] = new byte[size];    //�������л�����
			fis.read(SerializeBuf, 0, SerializeBuf.length);
			
			fis.close();//����fis�ļ�������رղ��ܳɹ�ɾ���ϵ���Ϣ�ļ�����Ϊ��ռ���ļ�
			if(breakInfor.delete())  //ɾ���ϵ���Ϣ�ļ�
			{
				System.out.println("�ϵ���Ϣ�ļ�ɾ���ɹ�");
			}
			
			
			translateRecord = infor.NonSerialize(SerializeBuf);
			System.out.println("�ϵ�������ʼ,�ϵ��ļ���" + translateRecord.getFileName()
					+ ",�ϵ�λ����" + translateRecord.getOffset());
			file = new File("./data/"+translateRecord.getFileName());   //�򿪶ϵ��ļ�
			fis = new RandomAccessFile(file,"rw");                     //������fis
			Send(fis,translateRecord.getNum(),translateRecord.getOffset());
			System.out.println("�ϵ�������ɣ������˳�");
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
