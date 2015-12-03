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
import java.util.Vector;


import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.util.RegistryCommandParser;
import cs455.overlay.util.StatisticsCollectorAndDisplay;
import cs455.overlay.wireformats.NodeReportsOverlaySetupStatus;
import cs455.overlay.wireformats.OverlayNodeReportsTaskFinished;
import cs455.overlay.wireformats.OverlayNodeReportsTrafficSummary;
import cs455.overlay.wireformats.OverlayNodeSendsDeregistration;
import cs455.overlay.wireformats.OverlayNodeSendsRegistration;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.RegistryReportsDeregistrationStatus;
import cs455.overlay.wireformats.RegistryReportsRegistrationStatus;
import cs455.overlay.wireformats.RegistryRequestsTrafficSummary;

public class Registry {
	
	TCPReceiverThread tcpReceive;
	public ArrayList<Node> mNodeList = new ArrayList<Node>();
	public ArrayList<Integer> assignedNodeIdentifiers= new ArrayList<Integer>();
	Node mNode;
	TCPSender tcpSend;
	Socket connectionSocket;
	//public ArrayList<Socket> clientSockets = new ArrayList<>();
	public Vector<Socket> ClientSockets = new Vector<>();
	private int overlaySetupCount=0;
	private volatile int taskReportedCount=0; 
	public ArrayList<StatisticsCollectorAndDisplay> trafficSummaryList= new ArrayList<StatisticsCollectorAndDisplay>();
	private ServerSocket server; 
	
	
	public static void main(String args[]) throws NumberFormatException, IOException
	{
		Registry registryObj=  new Registry(Integer.parseInt(args[0]));
		
	}
	
	
	public Registry(int RegistryPort) throws IOException
	{
		server = new ServerSocket(RegistryPort); 
		RegistryCommandParser registryMenu= new RegistryCommandParser(mNodeList,ClientSockets);
		
		registryMenu.start();
		
		System.out.println("initiated Registry: waiting for client request...\n");
		while(true)
		{
			connectionSocket = server.accept();
			RecieveCommunicationsThread obj= new RecieveCommunicationsThread(connectionSocket);
			
		}
		
		
		
	}
	
	public class RecieveCommunicationsThread extends Thread
	  {
			public RecieveCommunicationsThread(Socket socket) throws IOException
			{
				ClientSockets.add(socket);
				//System.out.println("in RecieveCommunicationsThread- Size of ClientSockets Vector:"+ ClientSockets.size());
				tcpReceive = new TCPReceiverThread(socket,this, null);
				tcpReceive.start();
				tcpSend= new TCPSender(connectionSocket);
				
				//System.out.println("Recieving Data..");
				//byte[] data= tcpReceive.receiveData();
				//System.out.println("calling onEvent");
				//onEvent(data);
				
			}
			
			public void run()
			{
				
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
			
			
			public int UniqueIdentifier()
			{
				Random random= new Random();
				int assignedId= random.nextInt(127);
				
				boolean isUniqueId= true;
				for(int i=0;i<assignedNodeIdentifiers.size();i++)
				{
					if(assignedNodeIdentifiers.get(i)==assignedId)
					{
						isUniqueId= false;
					}
				}
				
				if(isUniqueId)
				{
					return assignedId;
				}
				else
				{
					return UniqueIdentifier();
				}
				
			}
			
			public void onEvent(byte[] data) throws IOException 
			{
				//System.out.println("in OnEvent Method");
				int type = 0;
				try {
					type = getType(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//System.out.println("type:"+type);
				
				
				
				if(type==Protocol.OVERLAY_NODE_SENDS_REGISTRATION )
				{
					String errorLog="";
					OverlayNodeSendsRegistration registration = null;
					try {
						registration = new OverlayNodeSendsRegistration(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					mNode= new Node();
					mNode.setIp_address(registration.ip_address);
					mNode.setPort(registration.port);
					
					
					int assignedId=UniqueIdentifier();
					mNode.nodeIdentifier=assignedId;
					assignedNodeIdentifiers.add(assignedId);
					
					Boolean nodeAlreadyRegistered= false;
					for(int i=0;i<mNodeList.size();i++)
					{
						if(mNodeList.get(i).ip_address.equals(mNode.getIp_address()) && mNodeList.get(i).port== mNode.getPort())
						{
							nodeAlreadyRegistered=true;
							errorLog="Node Already Registered";
						}
					}
					
					if(!nodeAlreadyRegistered)
					{
						try {
							mNode.setName(InetAddress.getByName(registration.ip_address).getHostName());
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						mNodeList.add(mNode);
						
						System.out.println("Adding to Registry -> Node ID:"+assignedId+",IP:"+registration.ip_address+",Port:"+registration.port);
					}
					
					
					RegistryReportsRegistrationStatus sendRegistrationReport;
					
					if(!nodeAlreadyRegistered)
					{
						sendRegistrationReport = new RegistryReportsRegistrationStatus(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS,(byte) assignedId,"Registration request successful. The number of messaging nodes currently constituing the overlay is ("+mNodeList.size()+")");
						try {
							tcpSend= new TCPSender(connectionSocket);
							tcpSend.sendData(sendRegistrationReport.getBytes());
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						System.out.println("Node ID with IP:"+registration.ip_address+" and Port:"+registration.port+ "EXISTS already");
						sendRegistrationReport = new RegistryReportsRegistrationStatus(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS,(byte) -1,"Registration Request UnSuccessful:"+errorLog);
						try {
							tcpSend= new TCPSender(connectionSocket);
							tcpSend.sendData(sendRegistrationReport.getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					//System.out.println("\nEnter Registry command:");
					
					
				}
				
				else if(type== Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION)
				{
					
					OverlayNodeSendsDeregistration deregistration= new OverlayNodeSendsDeregistration(data);
					int nodeId= deregistration.nodeId;
					System.out.println("Deregistration request from node:"+nodeId);
					
					for(int i=0;i<mNodeList.size();i++)
					{
						if(mNodeList.get(i).nodeIdentifier==nodeId)
						{
							ClientSockets.removeElementAt(i);
							mNodeList.remove(i);
							break;
						}
						
					}
					
					RegistryReportsDeregistrationStatus deregistrationResponse = new RegistryReportsDeregistrationStatus(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS,(byte) nodeId,"deRegistred:");
					TCPSender tcpsender= new TCPSender(connectionSocket);
					tcpsender.sendData(deregistrationResponse.getBytes());
					
				}
				
				else if(type== Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS)
				{
					NodeReportsOverlaySetupStatus overlayStatus= new NodeReportsOverlaySetupStatus(data);
					System.out.println(overlayStatus.infoString +" for Node ID:"+overlayStatus.status);
					overlaySetupCount++;
					
					if(overlaySetupCount== mNodeList.size())
					{
						System.out.println("Registry now ready to intiate tasks");
						System.out.println("\nEnter Registry command:");
						
					}
					
				}
				else if(type== Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED)
				{
					incrementCountAndSendRequesttoNode(data);
					
					
				}
				else if(type==Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY)
				{
					OverlayNodeReportsTrafficSummary trafficSummary = new OverlayNodeReportsTrafficSummary(data);
					System.out.println("traffic summary Reported: Node "+trafficSummary.assignedNodeID);
					StatisticsCollectorAndDisplay tSummary= new StatisticsCollectorAndDisplay();
					
					tSummary.assignedNodeID= trafficSummary.assignedNodeID ;
					tSummary.packetsRecieved = trafficSummary.packetsRecieved;
					tSummary.packetsRelayed = trafficSummary.packetsRelayed;
					tSummary.packetsSent = trafficSummary.packetsSent;
					tSummary.payloadRecieved = trafficSummary.payloadRecieved;
					tSummary.payloadSent = trafficSummary.payloadSent;
					
					trafficSummaryList.add(tSummary);
					//System.out.println("trafficSummaryList:"+ trafficSummaryList.size());
					if(trafficSummaryList.size()==mNodeList.size())
					{
					
						int totalpacketsSent=0, totalpacketsRelayed=0, totalpacketsRecieved=0;
						long totalpayloadSent=0, totalpayloadRecieved=0;
						
						System.out.println("\t\tSent\tRecieved\tRelayed\tPayLoadSent\tPayloadRecievedn\n");
						for(int i=0;i<trafficSummaryList.size();i++)
						{
							StatisticsCollectorAndDisplay ste= trafficSummaryList.get(i);
							
							int assignedNodeID =ste.assignedNodeID;
							int packetsSent= ste.packetsSent;
							int packetsRelayed=ste.packetsRelayed;
							long payloadSent= ste.payloadSent;
							int packetsRecieved= ste.packetsRecieved;
							long payloadRecieved = ste.payloadRecieved;
							
							totalpacketsSent=totalpacketsSent+packetsSent;
							totalpacketsRelayed=totalpacketsRelayed+packetsRelayed;
							totalpacketsRecieved=totalpacketsRecieved+packetsRecieved;
							totalpayloadSent=totalpayloadSent+payloadSent;
							totalpayloadRecieved= totalpayloadRecieved+payloadRecieved;
							
							
							
							System.out.println(assignedNodeID+"\t\t"+packetsSent+"\t"+packetsRecieved+"\t"+packetsRelayed+"\t"+payloadSent+"\t"+payloadRecieved);
							
						}
						
						System.out.println("Sum\t\t"+totalpacketsSent+"\t"+totalpacketsRecieved+"\t"+totalpacketsRelayed+"\t"+totalpayloadSent+"\t"+totalpayloadRecieved);
						
					}
					
					
				}
				
				
			}
			
			public synchronized void incrementCountAndSendRequesttoNode(byte[] data)
			{
				taskReportedCount= taskReportedCount+1;
				//System.out.println("taskReportedCount:"+taskReportedCount);
				
				OverlayNodeReportsTaskFinished taskFinish = null;
				try {
					taskFinish = new OverlayNodeReportsTaskFinished(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Task finished: Node "+taskFinish.nodeId);
				if(taskReportedCount>=9)
				{
					for(int i=0;i<mNodeList.size();i++)
					{
						RegistryRequestsTrafficSummary reTrafficSummary= new RegistryRequestsTrafficSummary(Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY);
						TCPSender sender;
						try {
							sender = new TCPSender(ClientSockets.get(i));
							sender.sendData(reTrafficSummary.getBytes());
							try{
								Random random= new Random();
								Thread.sleep(random.nextInt(10)+10);
								
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						
						
					}
				}
				
			}
			
			
	  }



	
	
}



