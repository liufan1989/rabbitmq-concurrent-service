/**
 * 
 */
package com.database.testtools;


import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * @author Administrator
 *
 */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;  
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;  


public class MysqlDemoByRabbitmq {

	private final static String EXCHANGE_NAME = "SQL_dispatcher";
	private final static String ROUTING_KEY[] = {"test","test","test","test","test"};
	/**
	 * @param args
	 * @throws TimeoutException 
	 */
	public static void main(String[] args) throws java.io.IOException, TimeoutException {
		
		 //设置RabbitMQ所在主机ip,port,user,passwd  
		 ConnectionFactory factory = new ConnectionFactory(); 
		 factory.setHost("192.168.1.241");
		 factory.setPort(5672);
		 factory.setUsername("guest");
		 factory.setPassword("guest");
		 Connection connection = factory.newConnection();  
		 Channel channel = connection.createChannel(); 
		 channel.exchangeDeclare(EXCHANGE_NAME, "direct", true); 
		 
		 //================================
		 int sqlnum = 20000;
		 boolean duration = true;
		 Random random = new Random();
		 long begin = new Date().getTime();
		 System.out.println("starting send sql:" + Integer.toString(sqlnum));
		 for(int i = 0; i < sqlnum; i++)
		 {
			 String routingkey = getRoutingKey();
			 String phone = "1235467890";
			 String username = getRandomString(10).toUpperCase();
			 String password = UUID.randomUUID().toString().trim().replaceAll("-", "");
			 String email = "lfmcqs126com";
			 String nickname = getRandomString(12);
			 String gender = "M";
			 int age = random.nextInt(100) + 1;
			 int usertype = random.nextInt(10);
			 String signature = getRandomString(64);
			 String hobbies = "basketball_programming_xiaoshuo";
			 String avatar = getRandomString(32);
			 String background = getRandomString(32);
			 int feedbackflag = 1;
			 int messageflag = 1;
			 String position = "jinyelu";
			 String location = "china_shannxi_province_xian_city";
			 
			 String sql = "INSERT INTO test (telphone,username,password,usertype,"
						+ "nickname,gender,age,email,signature,hobbies,"
						+ "avatar,background,feedbackflag,messageflag,position,location) "
						+ "VALUES ('%s','%s','%s','%d','%s','%s','%d','%s','%s','%s','%s','%s','%d','%d','%s','%s')";
			 
			 String message = String.format(sql,phone,username,password,usertype,
					 nickname,gender,age,email,signature,hobbies,avatar,background,
					 feedbackflag,messageflag,position,location);
			 
			 //System.out.println("Send Message:" + message);
			 if(duration){
				 channel.basicPublish(EXCHANGE_NAME, routingkey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			 }else{
				 channel.basicPublish(EXCHANGE_NAME, routingkey, null, message.getBytes());
			 }
		 }
		 System.out.println("ending send sql");
		 
		 //================================
		 long end = new Date().getTime();
		 System.out.println(String.format("Begin:%d",begin));
		 System.out.println(String.format("End  :%d",end));
		 System.out.println(String.format("TimeDiff:%d ms", end-begin));
		 
		 //关闭频道和连接  
		 channel.close(); 
		 connection.close(); 
	}
	
	private static String getRoutingKey()  
    {  
        Random random = new Random();  
        int ranVal = random.nextInt(5);  
        return ROUTING_KEY[ranVal];  
    }  
	
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
