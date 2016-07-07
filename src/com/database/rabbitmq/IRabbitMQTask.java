/**
 * 
 */
package com.database.rabbitmq;

import com.rabbitmq.client.Channel;

/**
 * @author liufan
 *
 */
 public abstract class IRabbitMQTask {
	private  String bindingKey = null;
	private  String queueName = null;
	private  Channel channel = null;
	private  boolean runFlag = true;
	
	public IRabbitMQTask(String name,String key){
		setQueueName(name);
		setBindingKey(key);
	}
	
	public String getBindingKey() {
		return bindingKey;
	}
	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public boolean isRunFlag() {
		return runFlag;
	}
	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}
	
	public abstract void preInitialize() throws Exception;
	public abstract void initialize() throws Exception ;
	public abstract void process(String message) throws Exception;
	public abstract void finitialize();
	public abstract void over();

}
