/**
 * 
 */
package com.database.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.database.rabbitmq.RabbitMQService;

/**
 * @author liufan
 *
 */
public class client {

	/**
	 * @param args
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, TimeoutException {
		// TODO Auto-generated method stub
		RabbitMQService rs = new RabbitMQService("192.168.1.241", 5672, "guest", "guest",RabbitMQService.RABBITMQ_TYPE.PRODUCER);
		rs.connect();
		String message = "INSERT INTO test (telphone,username,password,usertype,nickname,gender,age,"
				+ "email,signature,hobbies,avatar,background,feedbackflag,messageflag,position,location) "
				+ "VALUES ('13609183715','BRP537J2ID','ccffe3625bf74272baa1964c100595fa','4','ee7t1cd5z35q',"
				+ "'M','43','lfmcqs126com','t1qi7f5cffrtmmv1m00bgmzleivijc3p99oom5zvlajfz98stov7ndmojrw0y7qf',"
				+ "'basketball_programming_xiaoshuo','4l7uy4je17ij500gics24vw2ngukhdbl',"
				+ "'1iqjax1h7c7sctafwsscynl91cmdy1ul','1','1','jinyelu','china_shannxi_province_xian_city')";
		rs.sendMessage("test", message, false);
		rs.close();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
