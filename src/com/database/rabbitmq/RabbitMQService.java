/**
 * 
 */
package com.database.rabbitmq;

import java.io.IOException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;  
import com.rabbitmq.client.Connection;  
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;

/**
 * @author liufan
 *
 */
public class RabbitMQService  {

	public enum RABBITMQ_TYPE{PRODUCER,CONSUMER};
	public final static String EXCHANGE_NAME = "SQL_dispatcher";

	private final static String OVER_MESSAGE = "over";
	private ConnectionFactory factory = null;
	private Connection connection = null;
	private ExecutorService cachedThreadPool = null;
	
	//default service type is consumer
	public RABBITMQ_TYPE serviceType = RABBITMQ_TYPE.CONSUMER;
	private Channel producerChannel = null;
	
	//CONSUMER: durable queue and exchange
	private boolean durable = true;
	private boolean autoAck = false;
	private int prefetchCount = 1;
	
	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public void setAutoAck(boolean autoAck) {
		this.autoAck = autoAck;
	}

	public void setPrefetchCount(int prefetchCount) {
		this.prefetchCount = prefetchCount;
	}

	public RabbitMQService(String ip,int port,String user,String passwd) {
		factory = new ConnectionFactory();
		factory.setHost(ip);  
		factory.setPort(port);
		factory.setUsername(user);
		factory.setPassword(passwd);
		cachedThreadPool = Executors.newCachedThreadPool();
	}
	
	public RabbitMQService(String ip,int port,String user,String passwd, RABBITMQ_TYPE serviceType) {
		factory = new ConnectionFactory();
		factory.setHost(ip);  
		factory.setPort(port);
		factory.setUsername(user);
		factory.setPassword(passwd);
		this.serviceType = serviceType;
		if(RABBITMQ_TYPE.CONSUMER == this.serviceType){
			cachedThreadPool = Executors.newCachedThreadPool();
		}
	}
	
	public void connect() throws IOException, TimeoutException{
		if(connection != null && connection.isOpen() ){
			System.out.println("have already connect to rabbitmq server!");
		}
		else{
			connection = factory.newConnection();
		}
		
		if(this.serviceType == RABBITMQ_TYPE.PRODUCER){
			if(producerChannel != null && producerChannel.isOpen() ){
				
			}else{
				producerChannel = connection.createChannel();
			}
		}
	}
	
	private Channel newChannel() throws IOException{
		if(!connection.isOpen())
		{
			throw new IOException("connecting RabbitMQ Server break down!");
		}
		return connection.createChannel();
	}
		
	private void rabbitMQRun(IRabbitMQTask task) throws Exception {
		
		Channel channel = null;
		QueueingConsumer consumer = null;
		QueueingConsumer.Delivery delivery = null;
		
		if(task.getBindingKey() == null){
			throw new Exception(task.toString() + " is not set bindkey!");
		}
		if (task.getQueueName() == null || task.getQueueName() == ""){
			task.setQueueName(task.getBindingKey() + "_Queue");
		}

		try {
			
			channel = this.newChannel();
			channel.basicQos(prefetchCount);
			channel.exchangeDeclare(EXCHANGE_NAME, "direct", durable);
			channel.queueDeclare(task.getQueueName(), durable, false, false, null);
			channel.queueBind(task.getQueueName(), EXCHANGE_NAME, task.getBindingKey());
			consumer = new QueueingConsumer(channel);
			channel.basicConsume(task.getQueueName(), autoAck, consumer);
			
			task.setChannel(channel);
			while (task.isRunFlag())  
			{  
			    delivery = consumer.nextDelivery();
			    String message = new String(delivery.getBody());
			    try{
			    	if(!message.equals("over")){
			    		task.process(message);
			    	}
			    }
			    catch(Exception e){
			    	System.out.println(Thread.currentThread().getName() + " task process exception[" + message + "]");
			    }
			    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);  
			}
			System.out.println(Thread.currentThread().getName() + " Normal exit!");
		} catch (Exception e) {
			if (e instanceof InterruptedException){
			}else{
				e.printStackTrace();
				System.out.println(Thread.currentThread().getName() + ":rabbitMQRun thread exception: exit thread!");
			}
		}finally{
			channel.close();
			System.out.println(Thread.currentThread().getName() +" channel closed!");
		}
	}  
	
	public void executeTask(IRabbitMQTask task) {
		
		//starting task thread................
		cachedThreadPool.execute(new Runnable() {  
		    public void run() {
		    	System.out.println(Thread.currentThread().getName() + " starting..............");

				    try {
				    	
						task.preInitialize();
						task.initialize();
						
				    	rabbitMQRun(task);
				    	
				    } catch (Exception e) {  
				    	e.printStackTrace();
				    	System.out.println("rabbitmq service executeTask Exception: exit thread!");
				    } finally {
				    	task.finitialize();
				    	task.over();
				    }

		    	System.out.println(Thread.currentThread().getName() + " ending.............."); 
		    }
		});
	}
	
	public void sendMessage(String routingkey,String message,boolean duration) throws IOException{
		if(duration){
			producerChannel.basicPublish(EXCHANGE_NAME, routingkey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
		}else{
			producerChannel.basicPublish(EXCHANGE_NAME, routingkey, null, message.getBytes());
		}
	}
	
	public void blockwait(){
		try {
			cachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);  
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//[close不会删除rabbitm中的对应的queue和exchange，只是关闭connection和channel]
	public void close(){
		try {
			if(this.serviceType == RABBITMQ_TYPE.PRODUCER){
				producerChannel.close();
				producerChannel = null;
			}else{
				cachedThreadPool.shutdownNow();
				cachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			}
			System.out.println("rabbitmq service thread pool closed");
			if(connection.isOpen()){
				connection.close();
			}
			//无论链接关闭与否都将connection=null
			connection = null;
			System.out.println("rabbitmq service connection closed!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//[前提是这个queue和consumer没有关联]
	//[cancel函数会删除rabbitmq中的对应的queue以及queue和exchange的绑定关系]
	public void cancel(IRabbitMQTask task) throws Exception{
		task.setRunFlag(false);
		Channel extraChannel = null;
		try {
			extraChannel = newChannel();
			if(task.getChannel().isOpen()){
				extraChannel.basicPublish(EXCHANGE_NAME, task.getBindingKey(), null, OVER_MESSAGE.getBytes());
			}
			extraChannel.queueUnbind(task.getQueueName(), EXCHANGE_NAME, task.getBindingKey());
			extraChannel.queueDelete(task.getQueueName());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			extraChannel.close();
		}
	}
	
}
