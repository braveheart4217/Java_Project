

import java.util.List;

public interface infoDao {
	
	public boolean hasTable(String path);
	public void createTbl(String path);
	public void insert(people p);
	public void update(people p);
	public void delete(String id);
	public people getNameById(String id);
	public void query();
	

}
