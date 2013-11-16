package data.read;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import data.BOSqliteAbstract;


public class BODrUser extends BOSqliteAbstract{
	
	public String query = "bones";
	
	public int db_type = DB_READ;
	
	public String username;
	
	public void BODwUser(){
		
	}
	
	//获取用户名
		public String getUserName(){
			Cursor cursor = this.db.query("user", new String[]{"username"}, "", new String[]{"1"}, null, null, null);
			while(cursor.moveToNext()){
				 username = cursor.getString(cursor.getColumnIndex("username"));
			}
			Log.i("名字", username);
			return "abc";
		} 
}