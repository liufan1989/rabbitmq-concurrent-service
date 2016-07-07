/**
 * 
 */
package com.database.service;

import com.database.rabbitmq.IRabbitMQTask;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * @author liufan
 *
 */
public class MysqlTask extends IRabbitMQTask {
	
	private final static String mysqldriver = "com.mysql.jdbc.Driver";
	private static String ip = "192.168.1.241";
	private static int port = 3306;
	private static String database = "test_performance";
	private static String user = "root";
	private static String password = "123456";
	private static String url = String.format("jdbc:MySQL://%s:%d/%s?characterEncoding=utf8&useSSL=false", ip, port, database);
	
	private Connection conn = null;
	private Statement stmt = null;
	
	
	public MysqlTask(String name, String key) {
		super(name, key);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.database.process.IRabbitMQTask#preInitialize()
	 */
	public void preInitialize() throws ClassNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("MysqlTask preInitialize....");
		Class.forName(mysqldriver);
	}

	/* (non-Javadoc)
	 * @see com.database.process.IRabbitMQTask#initialize()
	 */
	public void initialize() throws SQLException {
		// TODO Auto-generated method stub
		System.out.println("MysqlTask initialize....");
		conn = (Connection) DriverManager.getConnection(url, user, password);
		stmt = (Statement) conn.createStatement();
	}

	/* (non-Javadoc)
	 * @see com.database.process.IRabbitMQTask#process(java.lang.String)
	 */
	public void process(String message) {
		// TODO Auto-generated method stub
		//System.out.println(Thread.currentThread().getName() + "[process]:" + message);
		try {
			stmt.execute(message);
			
		} catch (SQLException e) {
			e.printStackTrace();
	        //如果执行有断链异常发生，比如mysql服务器重启，可以在这个catch中重新调用preInitialize和initialize函数
			this.finitialize();
			this.over();
			try {
				this.preInitialize();
				this.initialize();
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see com.database.process.IRabbitMQTask#finitialize()
	 */
	public void finitialize() {
		// TODO Auto-generated method stub
		System.out.println("MysqlTask finitialize....");
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.database.process.IRabbitMQTask#over()
	 */
	public void over() {
		// TODO Auto-generated method stub
		System.out.println("MysqlTask over....");
	}

}
