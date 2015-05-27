import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class readExcel 
{
	Sheet sheet;    //页
	Workbook book;  //工作簿
	int columNum,rowNum; //表的行列数
	String field = null;//字段 以（，）分隔
	String tableName;   //表名
	public readExcel(String path)
	{
		try {
			File file = new File(path);
			tableName = file.getName().replaceAll("[.][^.]+$", "");
			book = Workbook.getWorkbook(file);
			sheet = book.getSheet(0);
			columNum = sheet.getColumns();
			rowNum	= sheet.getRows();
			getField();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
	}
	
	public void printAllContent()
	{
		for (int i = 0; i < rowNum; i++)
        {
            for (int j = 0; j < columNum; j++) {
                Cell cell1 = sheet.getCell(j, i);
                String result = cell1.getContents();
                System.out.print(result);
                System.out.print("\t");
            }
            System.out.println();
        }
	}
	
	public void getField() 
	{
		for (int i = 0; i < columNum; i++) 
		{
			Cell cell = sheet.getCell(i,0);
			String ret = cell.getContents();
			if(i==0)
				field = ret;
			else 
			{
				field += ret;
			}
			if(i != columNum-1)
				field += ",";
		}
	}
	
	public String getSpecifieldCell(int i,int j) 
	{
		Cell cell = sheet.getCell(j,i);
		String ret = cell.getContents();
		return ret;
	}
	
	public String getSpecifiedRow(int i) 
	{
		String rowContents = null;
		int col = 0;
		Cell[] cells = sheet.getRow(i);
		
		for (Cell cell : cells) 
		{
			String ret = cell.getContents();
			if(col==0)
				rowContents = ret;
			else 
			{
				rowContents += ret;
			}
			if(col != columNum-1)
				rowContents += ",";
			col++;
		}
		return rowContents;
	}
	
	public String getSpecifiedFieldValue(int row,String value) 
	{
		Cell cell = sheet.findCell(value);
		String ret = sheet.getCell(cell.getColumn(),row).getContents();
		return ret;
	}
	
	
	
}
