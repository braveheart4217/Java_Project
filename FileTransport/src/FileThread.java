import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.logging.*;

public class FileThread implements Runnable{

	private Socket client;
	private DataInputStream dis;
	private RandomAccessFile fos;
	private infor translateRecord = new infor();
	byte[] sendBytes = new byte[1024];
	byte classInfor[] = new byte[1];

	private String searchPath = "./data/"; // ָ���ļ�����·��
	private String xlsName = "test.xls"; // ָ��xls�ļ����ļ���

	private infoDaoImpl sql = null;
	private people p;
	private readExcel rdExcel;

	private Logger log;

	public FileThread(Socket client) throws SecurityException, IOException {
		this.client = client;

		sql = new infoDaoImpl();
		p = new people();

		log = Logger.getLogger("test2");
		FileHandler fileHandler = new FileHandler("./log/test2.xml");
		fileHandler.setLevel(Level.FINE);
		log.addHandler(fileHandler);
		
//		log.removeHandler(new ConsoleHandler());
	}

	public void CreateTable(String path) {
		// �����ݿ⽨��
		if (!sql.hasTable(path)) {
			sql.createTbl(path);
			System.out.println("datebase created");
		}

	}

	public void recvAllFile() throws IOException, ClassNotFoundException {

		int size = 0;
		int fileNum = 0;
		boolean fileStart = false;

		log.info("��ʼ�����ļ�");

		do {

			size = dis.readInt(); // ��ȡ���л����鳤��
			if (size != classInfor.length)
				classInfor = new byte[size];
			dis.read(classInfor); // ��ȡ���л�����
			translateRecord = infor.NonSerialize(classInfor);

			if (fileStart == false) {
				System.out.println("----��ʼ�����ļ�<"
						+ translateRecord.getFileName() + ">----");
				fileStart = true;
			}

			fos = new RandomAccessFile("./data/"
					+ translateRecord.getFileName(), "rw");
			dis.read(sendBytes, 0, translateRecord.getReadLength()); // ��ȡ�ļ�����
			fos.seek(translateRecord.getOffset());
			fos.write(sendBytes, 0, translateRecord.getReadLength());
			fos.close();

			log.fine("fileName:" + translateRecord.getFileName()
					+ " readLength:" + translateRecord.getReadLength()
					+ " offset:" + translateRecord.getOffset());

			if (translateRecord.getFlag() == true) {
				fileNum++;
				fileStart = false;
				System.out.println("----�����ļ�<" + translateRecord.getFileName()
						+ ">�ɹ�-------");

				log.info(translateRecord.getFileName() + "�Ѿ����ܳɹ���");

			}

		} while (fileNum < translateRecord.getNum());

		log.info("�ļ�������ɣ�");

		dis.close();
		client.close();// �ر�����
	}

	public void insertline(readExcel rdExcel, int i, String tName[],
			infoDaoImpl sql) {
		p.setName(rdExcel.getSpecifiedFieldValue(i, tName[0]));
//		p.setId(rdExcel.getSpecifiedFieldValue(i, tName[2])
//				+ client.getRemoteSocketAddress().toString() + i);
		
		p.setId(rdExcel.getSpecifiedFieldValue(i, tName[2]));
		
		p.setSex(rdExcel.getSpecifiedFieldValue(i, tName[1]));
		p.setSelfIntro(rdExcel.getSpecifiedFieldValue(i, tName[3]));
		p.setSelfPic(rdExcel.getSpecifiedFieldValue(i, tName[4]));

		sql.insert(p);
		System.out.println("insert one line");
	}

	public void insertAll(infoDaoImpl sql) {
		int i = 0;

		String tName[] = new String[50];
		while (i < rdExcel.columNum) {
			tName[i] = rdExcel.getSpecifieldCell(0, i);
			i++;
		}

		i = 1;
		while (i < rdExcel.rowNum) {

			insertline(rdExcel, i, tName, sql);
			i++;
		}

	}

	public void DBOperate() {

		rdExcel = new readExcel(searchPath + xlsName);
		CreateTable(searchPath + xlsName);
		insertAll(sql);
	}

	public void start() {

		try {

			dis = new DataInputStream(client.getInputStream());

			recvAllFile();
			DBOperate();

		} catch (IOException e) {
			// TODO Auto-generated catch block

			log.fine("IOException occered!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block

			log.fine("ClassNotFoundException occered!");
			e.printStackTrace();
		}
		return ;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		start();
		
	}

}