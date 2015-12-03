package cs455.overlay.node;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.OverlayLayout;

import cs455.overlay.transport.TCPConnectionsCache;
import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.OverlayNodeSendsData;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.OverlaySetUp;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTaskInitiate;
import cs455.overlay.wireformats.RegistrySendsNodeManifest;

public class MessagingNode {
	
	public String mNodeIP;
	public int mNodePort;
	public String mNodeSelfName;
	public Socket clientSocket;
	TCPSender tcpSend;
	TCPReceiverThread tcpRecieve;
	private int NodeId;
	private int routingTablelength;
	int connectionCount=0;
	ArrayList<Integer> listOfNodesinSystem;
	ArrayList<Node> PeerlistInfo;
	ArrayList<Integer> visitedNodes;
	boolean registryRequestedTrafficSummary=false;
	
	public Vector<TCPConnectionsCache> connList=new Vector<TCPConnectionsCache>();
	 
	public Vector<TCPSender> tcpConnections = new Vector<TCPSender>();
	public ArrayList<Socket> serverConncetionSockets= new ArrayList<Socket>();
	
	
	String registryHost;
	int registryport;
	
	
	int sendTracker=0;
	int recieveTracker=0;
	int relayTracker=0;
	long sendSummation=0;
	long recieveSummation=0;
	
	public static void main(String args[]) throws NumberFormatException, IOException{
		
		MessagingNode mNode = new MessagingNode(args[0], Integer.parseInt(args[1]) );
		
		
	}
	
	public MessagingNode( String hostName, int hostPort) throws IOException
	{
		registryHost= hostName;
		registryport= hostPort;
		
		mNodeIP= InetAddress.getLocalHost().getHostAddress();
		
		Random randomNo = new Random();
		mNodePort= randomNo.nextInt(15000) + 15000;
		
	
		System.out.println("");
		System.out.println(" NodeIP:"+mNodeIP+"\n NodePort:"+mNodePort);
		System.out.println("");
		
		int type= Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
		
		//Send registration request to Registry
		OverlayNodeSendsRegistration register = new OverlayNodeSendsRegistration(type,mNodeIP,mNodePort);
		clientSocket = new Socket(hostName,hostPort);
		tcpSend = new TCPSender(clientSocket);
		tcpSend.sendData(register.getBytes());
		
		Menu m = new Menu();
		m.start();
		
		tcpRecieve = new TCPReceiverThread(clientSocket,null,this);
		tcpRecieve.start();
		
		
	}
	
	public class Menu extends  Thread
	{
		private Scanner sc;

		public void run()
		{
			while(true)
			{
				
				System.out.println("Enter Command:");
				sc = new Scanner(System.in);
				String s = sc.nextLine();
				
				if(s.equals("exit-overlay"))
				{
					OverlayNodeSendsDeregistration deregistration= new OverlayNodeSendsDeregistration(Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION, mNodeIP, mNodePort, NodeId);
					try {
						Socket socket = new Socket(registryHost,registryport);
						TCPSender sender= new TCPSender(socket);
						sender.sendData(deregistration.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				else if(s.equals("print-counters-and-diagnostics"))
				{
					System.out.println("_______________________________________________________________________________");
					System.out.println("sendTracker:"+sendTracker+"\nrecieveTracker:"+recieveTracker+"\nrelayTracker:"+relayTracker);
					System.out.println("sendSummation:"+sendSummation+"\nrecivedSummation:"+recieveSummation);
					System.out.println("_______________________________________________________________________________");
					
				}
				
				
			}
		}
		
		
	}
	public int getType(byte[] data) throws IOException
	{
		  ByteArrayInputStream baInputStream = new ByteArrayInputStream(data);
		  DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		  
		  int type = din.readInt();
		  baInputStream.close();
		  din.close();
		  return type;
	}
	
	public void onEvent(byte[] data) throws IOException
	{
			
			int type = 0;
			try {
				type=getType(data);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if(type== Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS)
			{
				RegistryReportsRegistrationStatus registrationStatus= new RegistryReportsRegistrationStatus(data);
				System.out.println(""+registrationStatus.add_info);
				
				if(registrationStatus.status_code!=-1)
				{
					NodeId=registrationStatus.status_code;
					System.out.println("Assigned Node Identifier:"+NodeId );
					
				}
				
			}
			else if(type== Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS)
			{
				RegistryReportsDeregistrationStatus deregistrationStatus= new RegistryReportsDeregistrationStatus(data);
				System.out.println(deregistrationStatus.status_code+":"+deregistrationStatus.add_info);
				System.exit(0);
				
				
			}
			else if(type== Protocol.REGISTRY_SENDS_NODE_MANIFEST)
			{
				RegistrySendsNodeManifest nodeManifest = new RegistrySendsNodeManifest(data);
				
				PeerlistInfo = nodeManifest.peerListInfo;
				routingTablelength = nodeManifest.routingTableSize;
				
				listOfNodesinSystem= new ArrayList<Integer>();
				listOfNodesinSystem = nodeManifest.listOfNodeIdsInSystem;
				
				
				System.out.println("\n");
				System.out.println("List of Messaging Nodes in system:");
				for(int i=0 ;i<listOfNodesinSystem.size();i++)
				{
					System.out.print(listOfNodesinSystem.get(i)+"\t");
					
				}
				
				System.out.println("\n");
				System.out.println("\n------------------------------------------------------");
				System.out.println("Routing Table Info for Node Id- "+NodeId+"");
				System.out.println("------------------------------------------------------");
				System.out.println("NodeId\t\t IP Address\t Port");
				System.out.println("------------------------------------------------------");
				
				for(int i=0;i< PeerlistInfo.size() ;i++ )
				{
					int port= PeerlistInfo.get(i).port;
					String ip = PeerlistInfo.get(i).ip_address;
					int nodeId= PeerlistInfo.get(i).nodeIdentifier;
					
					System.out.println(nodeId+"\t\t"+ip+"\t"+port);
				}
				System.out.println("------------------------------------------------------");
				
			
				// Intialize the node server
				AcceptClient obClient= new AcceptClient();
				obClient.start();
				
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				for(int i=0;i< PeerlistInfo.size() ;i++ )
				{
					Node peerNode= PeerlistInfo.get(i);
					
					int nodeIdOfPeer=  peerNode.nodeIdentifier;
					String ip= peerNode.ip_address;
					int hostport = peerNode.port;
					String hostName= InetAddress.getByName(ip).getHostName();
					
					Socket socket = new Socket(hostName,hostport);
					
					
					OverlaySetUp  overlaysetup = new OverlaySetUp(Protocol.OVERLAY_SETUP_MESSAGING_NODES,mNodePort, NodeId,"");
					System.out.println("Sending connection request to Node "+ nodeIdOfPeer+" with Port:"+hostport);
					
					try{
						TCPSender tcpSendForConnection = new TCPSender(socket);
						//tcpConnections.add(tcpSend);
						//serverConncetionSockets.add(socket);
						tcpSendForConnection.sendData(overlaysetup.getBytes());
						
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					Client client= new Client(hostport,socket);
					//client.start();
					
					
				}
				
				NodeReportsOverlaySetupStatus overlayStatus= new NodeReportsOverlaySetupStatus(Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS,NodeId, "setting of connections is successful" );
				tcpSend.sendData(overlayStatus.getBytes());
				
			}
			
			else if(type==Protocol.REGISTRY_REQUESTS_TASK_INITIATE)
			{
				
				RegistryRequestsTaskInitiate taskIntiate= new RegistryRequestsTaskInitiate(data);
				System.out.println("REGISTRY_REQUESTS_TASK_INITIATE:"+taskIntiate.numberOfPackets);
				
				
				SendPacketsToPeer sendPackets = new SendPacketsToPeer(taskIntiate.numberOfPackets);
				sendPackets.start();
				
								
				
				
			}
			else if(type==Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY)
			{
				if(!registryRequestedTrafficSummary)
				{
					System.out.println("\nREGISTRY_REQUESTS_TRAFFIC_SUMMARY");
					System.out.println("Sending traffic Summary...");
					registryRequestedTrafficSummary= true;
					OverlayNodeReportsTrafficSummary trafficSummary= new OverlayNodeReportsTrafficSummary(Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY, NodeId, sendTracker, relayTracker, sendSummation, recieveTracker, recieveSummation);
					
					tcpSend.sendData(trafficSummary.getBytes());
					
				}
				
				
			}
			
	}
	
	
	public class Client extends Thread
	{
		Socket socket;
		TCPReceiverThread tcpRecieve;
		TCPSender tcpSend1;
		int hostPort;
		
		public Client(Socket s) throws IOException
		{
			socket=s;
			tcpRecieve=new TCPReceiverThread(socket,null,null);
			tcpSend1=new TCPSender(socket);
		}
		
		public Client(int hostPort, Socket socket) throws IOException
		{
			this.socket= socket;
			tcpRecieve= new TCPReceiverThread(socket,null,null);
			tcpSend1= new TCPSender(socket);
			this.hostPort= hostPort;
			TCPConnectionsCache cle=new TCPConnectionsCache(tcpSend1,hostPort);
			connList.addElement(cle);
			
		}
		
		public void run()
		{
			 try {
				 while(true)
				 {
					 byte[] data= tcpRecieve.receiveData();
					 //System.out.println("In Client thread.getting type:");
							OverlayNodeSendsData sendData = null;
							try {
								sendData = new OverlayNodeSendsData(data);
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							
							if(sendData.destinationNode== NodeId){
								//System.out.println("Packets sent from ("+sendData.sourceNode+") reached destination ("+NodeId+")");
								recieveSummation= recieveSummation+ sendData.payload;
								recieveTracker = recieveTracker+1;
								
								//System.out.println("recieveTracker:"+recieveTracker);
								
							}
							else
							{
								int traceLength= sendData.noOfHops;
								traceLength++;
								visitedNodes = sendData.visitedNodes;
								visitedNodes.add(NodeId);
								OverlayNodeSendsData nodeSendsData= new OverlayNodeSendsData(Protocol.OVERLAY_NODE_SENDS_DATA, sendData.destinationNode, sendData.sourceNode,sendData.payload, traceLength,visitedNodes);
								
								relayTracker= relayTracker+1;
								//System.out.println("relayTracker:"+relayTracker);
								int destinationNode=nodeSendsData.destinationNode;
								
								int indexInRoutingTable=0;
								boolean foundinRoutingTable= false;
								for(int peer=0;peer< PeerlistInfo.size(); peer++)
								{
									if(PeerlistInfo.get(peer).nodeIdentifier== destinationNode)
									{
										indexInRoutingTable= peer;
										foundinRoutingTable = true;
										break;
									}
									
								}
								
								if(!foundinRoutingTable)
								{
									int sourceIndex= listOfNodesinSystem.indexOf(NodeId);
									int destinationIndex =listOfNodesinSystem.indexOf(destinationNode);
									
									int distance;
									if(destinationIndex>sourceIndex)
									{
										distance= destinationIndex-sourceIndex;
									}
									else
									{
										distance= ((listOfNodesinSystem.size()-destinationIndex)+sourceIndex)%listOfNodesinSystem.size() ;
									}
										
									for(int k=0;k<PeerlistInfo.size();k++)
									{
										if((distance> Math.pow(2,k)) && (distance< Math.pow(2, k+1)) )
										{
											indexInRoutingTable=k;
											break;
										}
											
									}
										
								}
								
								
								
								String ip= PeerlistInfo.get(indexInRoutingTable).ip_address;
								int port=PeerlistInfo.get(indexInRoutingTable).port;

								String hostName = null;
								try {
									hostName = InetAddress.getByName(ip).getHostName();
								} catch (UnknownHostException e1) {
									e1.printStackTrace();
								}
								
								TCPSender sendPackets= null;
								for(int i=0;i<connList.size();i++)
								{
									if(connList.get(i).nodeID==PeerlistInfo.get(indexInRoutingTable).port)
									{
										sendPackets= connList.get(i).tcpsender;
										//System.out.println("found tcpSender");
									}
									
								}
								try {
									sendPackets.sendData(nodeSendsData.getBytes());
								} catch (IOException e) {
									e.printStackTrace();
								}
								
								
						}
					 
				 }
				
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			 
			 
			//System.out.println("connected to host with port:"+ hostPort);
		}
		
	}
	
	
	public class AcceptClient extends  Thread
	{
		ServerSocket serverSocket;
		public AcceptClient() throws IOException
		{
			serverSocket=new ServerSocket(mNodePort);
			
		}
		
		public void run()
		{	
			while(true)
			{
				try {
					Socket clientSocket1 =  serverSocket.accept();
					//System.out.println("recived connection from "+clientSocket1.getLocalPort()+","+clientSocket1.getPort());
					TCPReceiverThread tcpReciever= new TCPReceiverThread(clientSocket1, null, null);
					byte[] data = tcpReciever.receiveData();
					//
						OverlaySetUp overlay;
						try {
							overlay = new OverlaySetUp(data);
							int port= overlay.port;
							int fromClient= overlay.nodeId;
							
							System.out.println("Recieved connection request from Id "+ fromClient +" with Port No:"+port);
							
							//overlay= new OverlaySetUp(Protocol.OVERLAY_SETUP_MESSAGING_NODES,mNodePort,NodeId, "Connected");
							//TCPSender tcpSend = new TCPSender(cSocket);
							//tcpSend.sendData(overlay.getBytes());
							
						} catch (IOException e) {
							
							e.printStackTrace();
						}

					Client c;
					c=new Client(clientSocket1);
					c.start();
				
					//WorkRunnable wr = new WorkRunnable(clientSocket1);
					//wr.start();
					
					//TCPSender tcpSend= new TCPSender(clientSocket);
						
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	
	public class SendPacketsToPeer extends Thread
	{
		int nofpackets;
		public SendPacketsToPeer(int nofpackets)
		{
			this.nofpackets=nofpackets;
		}
		
		public void run()
		{
			for(int round=0; round< nofpackets; round++)
			{
				Random random= new Random();
				int destinationNode=  listOfNodesinSystem.get(random.nextInt(listOfNodesinSystem.size()));
				int sourceNode= NodeId;
				
				while(destinationNode== sourceNode)
				{
					destinationNode= listOfNodesinSystem.get(random.nextInt(listOfNodesinSystem.size()));
				}
				
				int minPayloadValue=  -2147483648;
				int maxPayloadValue=  2147483647;
				
				int payload= minPayloadValue+ random.nextInt(maxPayloadValue);
				//System.out.println("payload for "+round+":"+payload);
				
				int traceLength=0;
				visitedNodes= new ArrayList<Integer>();
				
				
				OverlayNodeSendsData nodeSendsData= new OverlayNodeSendsData(Protocol.OVERLAY_NODE_SENDS_DATA, destinationNode, sourceNode,payload, traceLength,visitedNodes);
				
				// If found in routing table then se the index else send packets to nearest node.
				int indexInRoutingTable=0;
				boolean foundinRoutingTable= false;
				for(int peer=0;peer< PeerlistInfo.size(); peer++)
				{
					if(PeerlistInfo.get(peer).nodeIdentifier== destinationNode)
					{
						indexInRoutingTable= peer;
						foundinRoutingTable = true;
						break;
					}
					
				}
				
				if(!foundinRoutingTable)
				{
					int sourceIndex= listOfNodesinSystem.indexOf(NodeId);
					int destinationIndex =listOfNodesinSystem.indexOf(destinationNode);
					
					int distance;
					if(destinationIndex>sourceIndex)
					{
						distance= destinationIndex-sourceIndex;
					}
					else
					{
						distance= ((listOfNodesinSystem.size()-destinationIndex)+sourceIndex)%listOfNodesinSystem.size() ;
					}
						
					for(int k=0;k<PeerlistInfo.size();k++)
					{
						if((distance> Math.pow(2,k)) && (distance< Math.pow(2, k+1)) )
						{
							indexInRoutingTable=k;
							break;
						}
							
					}
						
				}
					
				String ip= PeerlistInfo.get(indexInRoutingTable).ip_address;
				int port=PeerlistInfo.get(indexInRoutingTable).port;

				try {
					String hostName= InetAddress.getByName(ip).getHostName();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}

				sendSummation= sendSummation+ payload;
				sendTracker= round+1;
				

				//System.out.println("Sending packet"+ round + ": ("+ sourceNode+","+destinationNode+") via :" +PeerlistInfo.get(indexInRoutingTable).nodeIdentifier);
				//System.out.println("sending :"+sendTracker);
				
				TCPSender sendPackets= null;
				for(int i=0;i<connList.size();i++)
				{
					if(connList.get(i).nodeID==PeerlistInfo.get(indexInRoutingTable).port)
					{
						sendPackets= connList.get(i).tcpsender;
					}
					
				}
				try {
					sendPackets.sendData(nodeSendsData.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				

		
			}
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				
			}
			
			
			System.out.println("Done sending packets from Node:"+NodeId);
			
			System.out.println("_______________________________________________________________________________");
			System.out.println("sendTracker:"+sendTracker+",recieveTracker:"+recieveTracker+",relayTracker:"+relayTracker);
			System.out.println("sendSummation:"+sendSummation+",recivedSummation:"+recieveSummation);
			System.out.println("_______________________________________________________________________________");
			
			
			OverlayNodeReportsTaskFinished taskFinish = new OverlayNodeReportsTaskFinished(Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED,mNodeIP,mNodePort,NodeId);
			try {
				tcpSend.sendData(taskFinish.get_bytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	

}
