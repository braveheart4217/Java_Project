
public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		readExcel myExcel = new readExcel("test.xls");
		System.out.print("������"+myExcel.rowNum+" ");
		System.out.println("������"+myExcel.columNum);
		System.out.println();
		System.out.println("������"+myExcel.tableName);
		System.out.println();
		myExcel.printAllContent();
		System.out.println();
		System.out.println("�ֶ�����"+myExcel.field);
		System.out.println();
		System.out.println("��һ�м�¼��"+myExcel.getSpecifiedRow(1));
		System.out.println();
		System.out.println("���ָ����Ԫ�����ݣ�"+myExcel.getSpecifieldCell(1, 2));
		System.out.println();
		System.out.println("���ָ���������ֶε�ֵ��"+myExcel.getSpecifiedFieldValue(1, "����"));
		
		
	}

}
