

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
//import java.util.Scanner;



import com.mysql.jdbc.DatabaseMetaData;


public class infoDaoImpl implements infoDao{
		
	public boolean hasTable(String path){
		Connection conn=DBUtil.open();
		readExcel myExcl = new readExcel(path);
		String nameTbl = myExcl.tableName;
		try {
			DatabaseMetaData meta=(DatabaseMetaData) conn.getMetaData();
			ResultSet isTables=meta.getTables(null, null, nameTbl, null);
			if(isTables.next()){
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void createTbl(String path) {
		
		Connection conn = DBUtil.open();
		readExcel myExcl = new readExcel(path);
		
		int i=0;
		String tName[] = new String[50];
		
		String nameTbl = myExcl.tableName;
		
		while(i < myExcl.columNum){

			tName[i] = myExcl.getSpecifieldCell(0, i);
			i++;
		}
					
		String sql = "create table " + nameTbl + " ( " 
//											   + tName[2] + " varchar(50)  primary key, "
											   + tName[2] + " varchar(50) ,  "
											   + tName[0] + " varchar(20), "
											   + tName[1] + " varchar(20), "
											   //+ tName[3] + " varchar(21825), "
											   + tName[3] + " longtext, "
											   + tName[4] + " longblob) ";// +" if not exists";
		
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			System.out.println("finish");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.close(conn);
		}		
	}

	@Override
	public void insert(people p) {
		Connection conn = DBUtil.open();
		readExcel myExcl = new readExcel("./data/test.xls");
		
		int i=0;
		String []tName = new String[50];
		
		String nameTbl = myExcl.tableName;
		
		while(i < myExcl.columNum){

			tName[i] = myExcl.getSpecifieldCell(0, i);	
//			System.out.println(tName[i]);
			i++;
		}
		
		String sql = "insert into " + nameTbl  + " ( "
											   + tName[2] + "," + tName[0] + "," + tName[1] + "," + tName[3]+"," + tName[4] + ")" 
											   + "values(?,?,?,?,?)";

//		System.out.println(sql);
		
		try {
			
			System.out.println("inserting....");
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, p.getId());			
			pstmt.setString(2, p.getName());		
			pstmt.setString(3, p.getSex());
			
			//指定插入的文本
			File f1=new File("."+File.separator+"data"+File.separator+p.getSelfIntro());
			
//			System.out.println(f1);
//			System.out.println("1: "+f1.getName());
//			InputStream input1 = null;
///			input1= new FileInputStream(f1);
//			pstmt.setBinaryStream(4, input1, (int)f1.length());
			
			Reader reader= new FileReader(f1);
			pstmt.setCharacterStream(4, reader, (int)f1.length());

			//指定插入的图片
			File f2=new File("."+File.separator+"data"+File.separator+p.getSelfPic());
//			System.out.println(f2);
//			System.out.println("2: "+p.getSelfPic());
			InputStream input2 = null;
			input2= new FileInputStream(f2);
			pstmt.setBinaryStream(5, input2, (int)f2.length());
			
			pstmt.executeUpdate();
			System.out.println("insert finished......");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.close(conn);
		}
		
	}
	
	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		Connection conn = DBUtil.open();
		
		readExcel myExcl = new readExcel("./data/test.xls");
		String nameTbl = myExcl.tableName;
		
		String sql = "delete from " + nameTbl + " where 学号=?";
		try {
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.close(conn);
		}
	}
	
	@Override
	public people getNameById(String id) {
		// TODO Auto-generated method stub
		Connection conn = DBUtil.open();
		readExcel myExcl = new readExcel("./data/test.xls");
		String nameTbl = myExcl.tableName;
		String nameP=myExcl.getSpecifieldCell(0, 0);
		String idP= myExcl.getSpecifieldCell(0, 2);
		
		String sql = "select " + nameP +" from " + nameTbl + " where " + idP +" =?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				String name = rs.getString(1);
				
				people p = new people();
				p.setId(id);
				p.setName(name);
								
				return p;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DBUtil.close(conn);
		}
		return null;
	}
	
	@Override
	public void update(people p) {
		Connection conn = DBUtil.open();
		
		readExcel myExcl = new readExcel("./data/test.xls");

		int i=0;
		String []tName = new String[50];		
		String nameTbl = myExcl.tableName;
		
		while(i < myExcl.columNum){
			tName[i] = myExcl.getSpecifieldCell(0, i);	
//			System.out.println(tName[i]);
			i++;
		}
		
		String sql = "update " + nameTbl  + " set " + tName[0]+"=?,"+tName[1]+"=?,"+tName[3]+"=?," + tName[4] +" =? "+ " where "+ tName[2]+"=?";
//		String sql = "update information set name=?, sex=?, selfIntro=?, picture=? where id = ?";
		try {	
			System.out.println("updating......");
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, p.getName());
			pstmt.setString(2, p.getSex());

			File f1=new File("./data/"+File.separator+p.getSelfIntro());
			Reader reader= new FileReader(f1);
			pstmt.setCharacterStream(3, reader, (int)f1.length());
			
			File f2=new File("./data/"+File.separator+p.getSelfPic());
			InputStream input2 = null;
			input2= new FileInputStream(f2);
			pstmt.setBinaryStream(4, input2, (int)f2.length());
			
			pstmt.setString(5, p.getId());
			
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.out.println("update finished......");
			DBUtil.close(conn);
		}
	}

	public void query() {
		Connection conn = DBUtil.open();
		
		readExcel myExcl = new readExcel("./data/test.xls");

		int i=0;
		String []tName = new String[50];		
		String nameTbl = myExcl.tableName;
		
		while(i < myExcl.columNum){
			tName[i] = myExcl.getSpecifieldCell(0, i);	
//			System.out.println(tName[i]);
			i++;
		}
		
		String sql = "select " + tName[2] + "," + tName[0] + "," + tName[1] + "," + tName[3]+"," + tName[4] + " from " + nameTbl ;
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				String id = rs.getString(1);
				String name = rs.getString(2);
				String sex = rs.getString(3);
				
				File f=new File("./data1",id+"-"+name);
				if(!f.exists()){
					f.mkdirs();
				}
				
				File ftxt=new File(f, id+"-"+name+".txt");
				if(!ftxt.exists()){
					ftxt.createNewFile();
				}
				
				File fjpg=new File(f, id+"-"+name+".jpg");
				if(!fjpg.exists()){
					fjpg.createNewFile();
				}
								
				Blob b1 = rs.getBlob(4);
				FileOutputStream out1 = null;
				out1 = new FileOutputStream(ftxt);
				out1.write(b1.getBytes(1,(int)b1.length()));
				
				Blob b = rs.getBlob(5);
				FileOutputStream out = null;
				out = new FileOutputStream(fjpg);
				out.write(b.getBytes(1,(int)b.length()));

				System.out.println("id: " + id + ", " + "name: " + name + ", " + "sex: " + sex);
				System.out.println("self introdaction is in"+f);
				System.out.println("picture is in"+f);
				System.out.println();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBUtil.close(conn);
		}	
	}

}
