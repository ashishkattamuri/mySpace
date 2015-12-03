package cs455.overlay.util;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;

import cs455.overlay.node.Node;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;


public class RegistryCommandParser extends Thread {
	
	ArrayList<Node> mNodeList= new ArrayList<Node>();
	ArrayList<Integer> mNodeUniqueIds = new ArrayList<Integer>();
	int[][] peerList;
	public Vector<Socket> ClientSockets = new Vector<>();
	ArrayList<Node> PeerListInfo ;
	private Scanner sc;
	int routingTableLength;
	
	public RegistryCommandParser(ArrayList<Node> mNodeList, Vector<Socket> connectionSockets )
	{
		this.mNodeList=mNodeList;
		ClientSockets = connectionSockets;
		
	}
	
	public void run()
	{
		while(true)
		{
			System.out.println("Enter Registry Command:");
			sc = new Scanner(System.in);
			String s = sc.nextLine();
			
			if(s.contains("setup-overlay"))
			{
			   routingTableLength= Integer.parseInt(s.replace("setup-overlay", "").trim());
			   System.out.println("Routing table length or connection requirement:"+ routingTableLength);
			   
			   for(int i=0; i< mNodeList.size();i++ )
			   {
				   mNodeUniqueIds.add(mNodeList.get(i).getNodeIdentifier());
			   }
			   
			   Collections.sort(mNodeUniqueIds);
			   
			   peerList= new int[mNodeUniqueIds.size()][routingTableLength];
			   
			   for(int node=0; node< mNodeUniqueIds.size() ; node++)
			   {  
				   int currentNodePosition= node;
				   for(int peer=0; peer< routingTableLength ; peer++)
				   {
					   
						int indexOfPeer= (currentNodePosition+ (int)(Math.pow(2, peer))) % (mNodeUniqueIds.size());
						peerList[node][peer]= mNodeUniqueIds.get(indexOfPeer);
						   
					   
				   }
			   }
			   
			  
			   System.out.println("\nRouting Table Info for Nodes:");
			   for(int j=0; j< mNodeUniqueIds.size() ; j++ )
			   {
				   System.out.print(" "+mNodeUniqueIds.get(j));
				   
			   }
			   System.out.println("\n");
			   System.out.println("-----------------------------------------------");
			   System.out.println("Node\t\tPeer Nodes");
			   System.out.println("-----------------------------------------------");
			   
			   for(int i=0;i <mNodeUniqueIds.size() ; i++)
			   {
				   System.out.print(mNodeUniqueIds.get(i)+"\t\t\t");
				   for(int j=0;j< peerList[i].length ;j++)
				   {
					   
					   System.out.print( peerList[i][j]+", ");
				   }
				   
				   System.out.println("\n");
				   
			   }
			   System.out.println("-----------------------------------------------");
			   
			   
			   for(int i=0;i< mNodeUniqueIds.size() ;i++)
			   {
				       int[] peers = new int[peerList[i].length];
				       
					   PeerListInfo = new ArrayList<Node>();
					   
					   for(int j=0;j< routingTableLength ;j++ )
					   {
						   peers[j]= peerList[i][j];
						   for(int k=0;k< mNodeList.size(); k++ )
						   {
							   if(mNodeList.get(k).nodeIdentifier== peerList[i][j])
							   {
								   PeerListInfo.add(mNodeList.get(k));
							   }
							   
						   }
					   }
					   
					   RegistrySendsNodeManifest sendManifest = new RegistrySendsNodeManifest(Protocol.REGISTRY_SENDS_NODE_MANIFEST, routingTableLength, peers ,PeerListInfo , mNodeUniqueIds);
					   
					   
					   int CurrentNodeindexInInfoList=0;
					   int CurrentNode  = mNodeUniqueIds.get(i);
					   
					   for(int j=0;j< mNodeList.size(); j++)
					   {
						   if(mNodeList.get(j).nodeIdentifier== CurrentNode)
						   {
							   CurrentNodeindexInInfoList=j;
							   break;
						   }
						   
					   }
					   
					try {
						System.out.println("Sending Info to "+mNodeList.get(CurrentNodeindexInInfoList).nodeIdentifier+" ..." );
						TCPSender tcpSend= new TCPSender(ClientSockets.get(CurrentNodeindexInInfoList));
						tcpSend.sendData(sendManifest.getBytes());
					
					} catch (IOException e) {
						e.printStackTrace();
					}
					   
				   
				/*	   
				   System.out.println("");
				   System.out.println("mNOde: "+ mNodeUniqueIds.get(i));
				   System.out.println("remote Port:"+ ClientSockets.get(i).getPort());
				   System.out.println("local Port:"+ ClientSockets.get(i).getLocalPort());
				 */
				   
			   }
			   
				
			}
			else if(s.equals("list-messaging-nodes"))
			{
				System.out.println("");
				System.out.println("No. of messaging nodes in Registry:"+mNodeList.size());
				System.out.println("");
				System.out.println("-----------------------------------------------");
				System.out.println("UniqueId\tIPAddress\tPort\tStatus");
				System.out.println("-----------------------------------------------");
				for(int i=0;i<mNodeList.size();i++)
				{
					String ipAddress= mNodeList.get(i).ip_address;
					int portNo= mNodeList.get(i).port;
					int UniqueId = mNodeList.get(i).nodeIdentifier;
					
					System.out.println(UniqueId+"\t\t"+ipAddress+"\t"+portNo);
				}
				
				System.out.println("-----------------------------------------------");
			}
			else if(s.contains("start"))
			{
				try
				{	
					int noOfMessages= Integer.parseInt(s.replace("start", "").trim());
					RegistryRequestsTaskInitiate taskInitiate= new RegistryRequestsTaskInitiate(Protocol.REGISTRY_REQUESTS_TASK_INITIATE,noOfMessages);
					
					TCPSender tcpSend1;
					for(int i=0;i< ClientSockets.size() ; i++)
					{
						tcpSend1 = new TCPSender(ClientSockets.get(i));
						tcpSend1.sendData(taskInitiate.getBytes());
						
					}
					
					
					
				}
				catch(Exception e)
				{
					System.out.println(e);
				}
				
				
				
			}
			
			else if(s.equals("list-routing-tables"))
			{
				 
				   peerList= new int[mNodeUniqueIds.size()][routingTableLength];
				   
				   for(int node=0; node< mNodeUniqueIds.size() ; node++)
				   {  
					   int currentNodePosition= node;
					   for(int peer=0; peer< routingTableLength ; peer++)
					   {
						   
							int indexOfPeer= (currentNodePosition+ (int)(Math.pow(2, peer))) % (mNodeUniqueIds.size());
							peerList[node][peer]= mNodeUniqueIds.get(indexOfPeer);
							   
						   
					   }
				   }
				   
				  for(int node=0; node< mNodeUniqueIds.size() ; node++)
				  {
					  System.out.println("\n\nRouting table info for node:"+mNodeUniqueIds.get(node));
					  System.out.println("-------------------------------------------------------------");
					  System.out.println("Node ID\t\t IPAdress\t\t Port");
					  System.out.println("-------------------------------------------------------------");
					  
					  for(int i=0;i<routingTableLength;i++)
					  {
						  System.out.println();
						  int nodeId= peerList[node][i];
						  for(int j=0;j<mNodeList.size();j++)
						  {
							  if(mNodeList.get(j).nodeIdentifier==nodeId)
							  {
								  System.out.println(nodeId+"\t\t"+mNodeList.get(j).ip_address+"\t\t"+mNodeList.get(j).port);
								  break;
							  }
							  
						  }
						  
					  }
					  System.out.println("-------------------------------------------------------------");
					  
					  
				  }
				   
				   
				   
				   
				
			}
	         
			
		}
	}
	
	
	
	
	

}
