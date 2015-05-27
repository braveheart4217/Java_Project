import java.io.*;

public class infor implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private int num;
	private int readLength;
	private long offset;
	private String fileName;
	private boolean endFlag;

	public void setNum(int num){
		this.num = num;
	}
	public void setReadLength(int readLength){
		this.readLength = readLength;
	}
	public void setOffset(long offset){
		this.offset = offset;
	}
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public void setFlag(boolean endFlag){
		this.endFlag = endFlag;
	}
	
	public int getNum(){
		
		return this.num;
	}
	public int getReadLength(){
		
		return this.readLength;
	}
	public long getOffset(){
		
		return this.offset;
	}
	public String getFileName(){
		
		return this.fileName;
	}
	public boolean getFlag(){
		
		return this.endFlag;
	}
	
	
	public infor() {
		num = 0;
		readLength = 0;
		offset = 0;
		fileName = "nimei";
		endFlag = false;

	}

	public void print() {
		System.out.println(num);
		System.out.println(readLength);
		System.out.println(offset);
		System.out.println(fileName);
		System.out.println(endFlag);
	}

	public void setAll(int num, int readlength, long offset, String fileName,
			boolean endFlag) {

		this.num = num;
		this.readLength = readlength;
		this.offset = offset;
		this.fileName = fileName;
		this.endFlag = endFlag;

	}

	public infor(int num, int readLength, long offset, String fileName,
			boolean endFlag) {
		this.num = num;
		this.readLength = readLength;
		this.offset = offset;
		this.fileName = fileName;
		this.endFlag = endFlag;

	}

	public static byte[] Serialize(infor object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 构造一个字节输出流
		ObjectOutputStream oos = null;

		oos = new ObjectOutputStream(baos); // 构造一个类输出流
		oos.writeObject(object);
		oos.flush();
		byte[] buf = baos.toByteArray();

		return buf;
	}

	public static infor NonSerialize(byte[] buf) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		ObjectInputStream ois = new ObjectInputStream(bais);
		infor out = new infor();
		out = (infor) ois.readObject();

		return out;
	}

	/*
	 * public static void main(String args[]) throws IOException,
	 * ClassNotFoundException { infor nmie = new infor(100, 100, 0, "nimei",
	 * false);
	 * 
	 * infor nnnn = NonSerialize(Serialize(nmie)); nnnn.print(); }
	 */
}
