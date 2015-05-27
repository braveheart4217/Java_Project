
public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		readExcel myExcel = new readExcel("test.xls");
		System.out.print("行数："+myExcel.rowNum+" ");
		System.out.println("列数："+myExcel.columNum);
		System.out.println();
		System.out.println("表名："+myExcel.tableName);
		System.out.println();
		myExcel.printAllContent();
		System.out.println();
		System.out.println("字段名："+myExcel.field);
		System.out.println();
		System.out.println("第一行记录："+myExcel.getSpecifiedRow(1));
		System.out.println();
		System.out.println("获得指定单元格内容："+myExcel.getSpecifieldCell(1, 2));
		System.out.println();
		System.out.println("获得指定行数的字段的值："+myExcel.getSpecifiedFieldValue(1, "姓名"));
		
		
	}

}
