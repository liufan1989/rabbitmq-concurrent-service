/**
 * 
 */
package com.database.service;

import com.database.rabbitmq.RabbitMQService;

/**
 * @author liufan
 *
 */
public class Server {
	
	public static void main(String[] args) throws Exception {

		RabbitMQService rs = new RabbitMQService("192.168.1.241", 5672, "guest", "guest");
		rs.connect();
		
		int tasknum = 10;
		MysqlTask[] t = new MysqlTask[tasknum];
		for(int i = 0;i < tasknum;i++){
			t[i] = new MysqlTask("", "test");
			rs.executeTask(t[i]);
		}
		
//		rs.blockwait();
//		try {
//			Thread.sleep(10000);//10s
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println("sleep 10s over.............");
//		try {
//			for(int i = 0;i < tasknum;i++){
//				rs.cancel(t[i]);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		System.out.println("start close service.............");
//		rs.close();
//		System.out.println("finish close service.................");
	}
	
}
