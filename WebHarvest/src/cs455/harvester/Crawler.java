package cs455.harvester;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import net.htmlparser.jericho.Config;
import net.htmlparser.jericho.LoggerProvider;


public class Crawler {
	
	
	
	 static int portNo;
	 static int numberOfThreadPools;
	 static String rootUrl,pathToConfigFile ;
	 static int recursionDepth;
	 
	 private ServerSocket server;
	 Socket connectionSocket;
	 
	 TCPReceiverThread tcpReceive;
	 TCPSender tcpSend;
	
	 ArrayList<WorkerThreads> threads= new ArrayList<WorkerThreads>();
	 //LinkedList<Task> tasks = new LinkedList<Task>();
	 LinkedList<String> tasks = new LinkedList<String>();
	 LinkedList<String> Finishedtasks = new LinkedList<String>();
	 
	 public Crawler()
	 {
		 
	 }
	
	public Crawler(int port, int threadPools, String root_Url, String pathToConfig_File ) throws IOException
	{
		portNo= port;
		numberOfThreadPools =threadPools;
		rootUrl = root_Url;
		pathToConfigFile = pathToConfig_File;
		
		//CommunicationThread cThread= new CommunicationThread(portNo);
		//cThread.start();
		
		initializeThreadPool();
		
		
		
	}
	
	
	public static void main(String args[]) throws IOException
	{
		Config.LoggerProvider=LoggerProvider.DISABLED;
		//Crawler crawler= new Crawler(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3]);
		//Crawler crawler= new Crawler(12476,1,"http://www.cs.colostate.edu/~cs455","");
		Crawler crawler= new Crawler(12476,4,"http://www.cs.colostate.edu/cstop/index.html","");
		
	}
	
	public void initializeThreadPool()
	{
		//Task task= new Task();
		//task.setUrl("http://www.cs.colostate.edu/~cs455");
		
		//tasks.add("http://www.cs.colostate.edu/~cs455");
		tasks.add(rootUrl);
		
		for(int i=0;i< numberOfThreadPools ;i++){
			WorkerThreads thread= new WorkerThreads(this);
			threads.add(thread);
			
		}
		
		for(WorkerThreads t : threads)   // start the threads
		{
			t.start();
		}	
		
		
	}
	
	public synchronized String dequeueTasks()
	{
		//synchronized (Finishedtasks) {
			while(true)
			{
				System.out.print(""); 
				//int taskSize=tasks.size();
				//String currentThread=Thread.currentThread().getName();
				if(tasks.size()>0){
					
					//Task t= tasks.getFirst();
					
					String t=tasks.getFirst();
					tasks.removeFirst();
					return t;
				}
				
				
			}
		//}
	
		
	}
	
	
	public void AddTask(String t)
	{
		//System.out.println("Adding task:"+t);
		tasks.add(t);
		//System.out.println("task.size:"+tasks.size());
		
	}
	
	public  void addCompletedTasks(String t)
	{
		Finishedtasks.add(t);
	}
	
	public  LinkedList<String> getCompletedTasks()
	{
		return Finishedtasks;
	}
	
	
	
	
	
	
	
	public int getType(byte[] data) throws IOException
	{
		ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(data);
		DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(localByteArrayInputStream));

		int i = localDataInputStream.readInt();

		localByteArrayInputStream.close();
		localDataInputStream.close();
		return i;
	}
	
	public void onEvent(byte[] data) throws IOException 
	{
		
		int type= getType(data);
		System.out.println("in OnEvent:"+type);
		
		if(type==Protocols.CRAWLER_HANDS_OFFJOB)
		{
			
			CrawlerInteraction cInteration= new CrawlerInteraction(data);
			String url= cInteration.url;
			System.out.println("recieved request ->URL:"+url);
			//AddTask(url);
			
		}
		
		
		
		
	}
	
	public class CommunicationThread extends Thread
	{
		int port;
		public CommunicationThread(int p)
		{
			port=p;
		}
		
		public void run()
		{
			try {
				server = new ServerSocket(port);
				while(true)
				{
					connectionSocket = server.accept();
					tcpReceive = new TCPReceiverThread(connectionSocket);
					tcpReceive.start();
					tcpSend= new TCPSender(connectionSocket);
					
				}
				
			} catch (IOException e) {
				//e.printStackTrace();
			} 
			
		}
		
		
	}
	
	

}
