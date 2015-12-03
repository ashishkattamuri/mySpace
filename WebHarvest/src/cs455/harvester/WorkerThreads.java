package cs455.harvester;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class WorkerThreads extends Thread{

	Crawler crawler;
	private static BufferedReader configFile ;
	Socket socket;
	TCPSender tcpSend;
	
	public WorkerThreads(Crawler  c)
	{
		crawler = c;

	}

	public void run()
	{
		//System.out.println("Starting Thread:"+ Thread.currentThread().getName());
		while(true)
		{
			String t = crawler.dequeueTasks();
			//System.out.println("Task:"+t);
			try {
				
				LinkedList<String> completedTasks= crawler.getCompletedTasks();
				if(!completedTasks.contains(t))
				{
						processTask(t);
					
					crawler.addCompletedTasks(t);
					System.out.println(""+t);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public  void processTask(String t) throws MalformedURLException, IOException //retrieve webPage
	{
		//String url = resovleRedirects(t.getUrl());
		String url = t;
		
		Boolean isSameDomain = checkDomain(url, crawler.rootUrl);
		
		try {
			if(isSameDomain)
			{
				URLExtractor(url);
			}
			else
			{
				System.out.println(url+" doesnt belong to domain:"+crawler.rootUrl+"<-->Hand Off task to other crawler");
				//handOffTask(url);
				
			}
		} catch (Exception e) {
			
			//e.printStackTrace();
		}

	}
	
	public void handOffTask(String url) throws IOException
	{
		String sCurrentLine;
		configFile = new BufferedReader(new FileReader("..\\cFile.txt"));
		
		while ((sCurrentLine = configFile.readLine()) != null) {
		
			String hostInfo= sCurrentLine.split(",")[0];
			String domainName= sCurrentLine.split(",")[1];
			
			String[] hostInfoArray= hostInfo.split(":");
			
			if(domainName.equals(new URL(url).getHost()))
			{
				String hostName= hostInfoArray[0];
				int hostPort= Integer.parseInt(hostInfoArray[1]);
				
				CrawlerInteraction cInteration= new CrawlerInteraction(Protocols.CRAWLER_HANDS_OFFJOB, url);
				
				
				System.out.println("Handing off Task to:"+domainName);
				
				socket= new Socket(hostName,hostPort);
				tcpSend = new TCPSender(socket);
				tcpSend.sendData(cInteration.getBytes());
				
				break;
				
			}
			
			
			
		}
		
	}

	public  String resovleRedirects(String url) 
	{
		HttpURLConnection con = null;
		int responseCode=0;
		try {
			con = (HttpURLConnection)(new URL(url).openConnection());
			con.setInstanceFollowRedirects(false);
			con.connect();
			responseCode = con.getResponseCode();
			
		} catch (Exception e) {
			
			//e.printStackTrace();
		}
		
		if(responseCode == 301){
			return con.getHeaderField( "Location" );
		} else {
			return url;
		}

	}

	public static boolean checkDomain(String pageUrl, String rootUrl)  {
		Boolean domainCheck=true;
		try {
			domainCheck= new URL(pageUrl).getHost().equals(new URL(rootUrl).getHost());
		} catch (MalformedURLException e) {
			//e.printStackTrace();
		}
		
		return domainCheck;
		
		
	}
	
	
	
	public  void URLExtractor(String url)
	{
		try {             // web page that needs to be parsed 
			final String pageUrl = url;
			//System.out.println("pageUrl:"+pageUrl);
			Source source = new Source(new URL(pageUrl));
			// get all 'a' tags
			List<Element> aTags = source.getAllElements(HTMLElementName.A);
			// get the URL ("href" attribute) in each 'a' tag
			for (Element aTag : aTags) {
				// print the url
				String outGoingUrl=aTag.getAttributeValue("href");
				
				if(outGoingUrl==null )
				{
					continue;
				}

				else if(  !new URI(outGoingUrl).isAbsolute()){
					URI resolvedUrl = null;
					try {
						resolvedUrl = new URI(pageUrl).resolve(outGoingUrl);
					} catch (Exception e) {
						//e.printStackTrace();
					}
					
					//System.out.println("Resolved URL: " + resolvedUrl.toString());
					
					//Task t = new Task();
					//t.setUrl(resolvedUrl.toString());
					String t= resolvedUrl.toString();
					crawler.AddTask(t);
					//System.out.println("adding task :"+ t);
					//AddTask(t);
					
					//***********
					//System.out.println(""+resolvedUrl.toString());
					
					
				}
				
				else{
					
					//Task t = new Task();
					//t.setUrl(outGoingUrl);
					String t= outGoingUrl;
					crawler.AddTask(t);
					//System.out.println("adding task :"+ t);
					//AddTask(t);
					//***********
					//System.out.println(""+outGoingUrl);
					
				}

			}



		}
		catch(Exception e)
		{
			
		}
		
	}



}
