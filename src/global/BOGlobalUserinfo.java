package global;

//用户信息全局变量
public class BOGlobalUserinfo {

	public static String name;
	
	public static String getUserName(){
		return name;
	}
	
	public static void setUserName(String username){
		name = username;
	}
	
}