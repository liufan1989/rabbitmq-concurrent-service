/**
 * 
 */
package com.database.testtools;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * @author liufan
 *
 */
public class MysqlDemoByDriect {
	
	private final static String mysqldriver = "com.mysql.jdbc.Driver";
	
	private static String ip = "192.168.1.241";
	private static int port = 3306;
	private static String database = "test_performance";
	private static String user = "root";
	private static String password = "123456";
	private static String url = String.format("jdbc:MySQL://%s:%d/%s?characterEncoding=utf8&useSSL=false", ip, port, database);
	
	
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Class.forName(mysqldriver);
		Connection conn = null;
		//Statement stmt =  null;
		System.out.println("connecting Mysql server:" + ip);
		try {
			conn = (Connection) DriverManager.getConnection(url, user, password);
			//stmt = (Statement) conn.createStatement();
			Random random = new Random();
			int sqlnum = 20000;
			//开始时间
			long begin = new Date().getTime();
			System.out.println("starting execute sql:" + Integer.toString(sqlnum));
			for (int i = 0;i < sqlnum; i++)
			{
				String phone = "13609183715";
				String username = getRandomString(10).toUpperCase();
				String password = UUID.randomUUID().toString().trim().replaceAll("-", "");
				String email = "lfmcqs@126.com";
				String nickname = getRandomString(12);
				String gender = "M";
				int age = random.nextInt(100) + 1;
				int usertype = random.nextInt(10);
				String signature = getRandomString(64);
				String hobbies = "basketball,programming,xiaoshuo";
				String avatar = getRandomString(32);
				String background = getRandomString(32);
				int feedbackflag = 1;
				int messageflag = 1;
				String position = "jinyelu";
				String location = "china shannxi province xian city";
				
				String sql = "INSERT INTO test (telphone,username,password,usertype,"
						+ "nickname,gender,age,email,signature,hobbies,"
						+ "avatar,background,feedbackflag,messageflag,position,location) "
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
				ps.setString(1, phone);
				ps.setString(2, username);
				ps.setString(3, password);
				ps.setInt(4,usertype);
				ps.setString(5, nickname);
				ps.setString(6, gender);
				ps.setInt(7, age);
				ps.setString(8, email);
				ps.setString(9, signature);
				ps.setString(10, hobbies);
				ps.setString(11, avatar);
				ps.setString(12, background);
				ps.setInt(13, feedbackflag);
				ps.setInt(14, messageflag);
				ps.setString(15, position);
				ps.setString(16, location);
				
				ps.executeUpdate();
			}
			System.out.println("ending execute sql..................");
			//结束时间
			long end = new Date().getTime();
			System.out.println(String.format("Begin:%d",begin));
			System.out.println(String.format("End  :%d",end));
			System.out.println(String.format("TimeDiff:%d ms", end-begin));
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			conn.close();
		}
		
	}
	
	//length表示生成字符串的长度
	public static String getRandomString(int length) { 
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";   
	    Random random = new Random();   
	    StringBuffer sb = new StringBuffer();   
	    for (int i = 0; i < length; i++) {   
	        int number = random.nextInt(base.length());   
	        sb.append(base.charAt(number));   
	    }   
	    return sb.toString();   
	}
	
}
