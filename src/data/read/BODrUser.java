package data.read;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import data.BOSqliteAbstract;


public class BODrUser extends BOSqliteAbstract{
	
	public String query = "bones";
	
	public int db_type = DB_READ;
	
	public String name;
	
	public Cursor cursor;
	
	public BODrUser(Context context){
		super();
		super.query = this.query;
		super.db_type = this.db_type;
		try {
			this.init(context);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//获取用户名
		public String getUserName(){
			this.cursor = this.db.query("user", new String[]{"id","name"}, "id=?", new String[]{"1"}, null, null, null);
			while(this.cursor.moveToNext()){
				this.name = this.cursor.getString(cursor.getColumnIndex("name"));
			}
			return this.name;
		} 
}