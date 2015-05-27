import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

public class FileThread implements Runnable {

	private Socket client;
	private DataInputStream dis;
	private RandomAccessFile fos;
	private infor translateRecord = new infor();
	byte[] sendBytes = new byte[1024];
	byte classInfor[] = new byte[1];

	public FileThread(Socket client) {
		this.client = client;
	}

	public void CreateTable() {
		// �����ݿ⽨��
	}

	public void recvSiganlFile(Long fileLength) throws IOException {

		int ReadSize = 0;
		int read = 0;

		while (ReadSize < fileLength) {
			if (fileLength - ReadSize < 1024)
				read = dis.read(sendBytes, 0, (int) (fileLength - ReadSize));
			else
				read = dis.read(sendBytes, 0, sendBytes.length);

			fos.write(sendBytes, 0, read);

			ReadSize += read;
		}

	}

	public void recvAllFile() throws IOException, ClassNotFoundException {

		int size = 0;
		int fileNum = 0;
		boolean fileStart = false;

		do {
			
			size = dis.readInt(); // ��ȡ���л����鳤��
			if (size != classInfor.length)
				classInfor = new byte[size];
			dis.read(classInfor); // ��ȡ���л�����
			translateRecord = infor.NonSerialize(classInfor);

			if(fileStart == false){
				System.out.println("----��ʼ�����ļ�<" + translateRecord.getFileName()
					+ ">----");
				fileStart = true;
			}
			
			
			fos = new RandomAccessFile("./data/" + translateRecord.getFileName(),
					"rw");
			dis.read(sendBytes, 0, translateRecord.getReadLength()); // ��ȡ�ļ�����
			fos.seek(translateRecord.getOffset());
			fos.write(sendBytes, 0, translateRecord.getReadLength());
			fos.close();

			if(translateRecord.getFlag() == true) {
				fileNum++;
				fileStart = false;
				System.out.println("----�����ļ�<" + translateRecord.getFileName()
						+ ">�ɹ�-------");

			}

		} while (fileNum < translateRecord.getNum());

		dis.close();
		client.close();// �ر�����
	}


	public void run() {

		try {

			dis = new DataInputStream(client.getInputStream());

			recvAllFile();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}