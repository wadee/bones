package data.write;

import android.content.ContentValues;
import data.BOSqliteAbstract;

public class BODwUser extends BOSqliteAbstract{
	
	public String query = "bones";
	
	public int db_type = DB_WRITE;
	
	public void BODwUser(){
		
	}
	
	//写入用户名
	public void insertUserName(String name){
		//生成ContentValues对象
		ContentValues values = new ContentValues();
		//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致
		values.put("username",name);
		//调用insert方法，就可以将数据插入到数据库当中
		this.db.insert("user", null, values);
	} 
	
} 

