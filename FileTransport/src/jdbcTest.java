

import java.sql.Connection;

public class jdbcTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		infoDaoImpl info = new infoDaoImpl();
		people p = new people();
		
		if(!info.hasTable("./data/test.xls")){
			info.createTbl("./data/test.xls");
		}
		
		
		p.setId("14");
		p.setName("BiggggJim");
		p.setSex("F");
		p.setSelfIntro("1.txt");
		p.setSelfPic("1.jpg");
//		info.insert(p);
		
//		info.update(p);
		
//		info.update(p);
//		info.query();
//		info.delete("29");
		
		System.out.println("name: "+info.getNameById("12").getName());
//		System.out.println("name: "+info.getNameById(3).getName());
//		info.query();
		
	}

}
