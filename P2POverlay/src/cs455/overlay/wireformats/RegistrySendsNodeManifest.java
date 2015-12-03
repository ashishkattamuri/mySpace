package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cs455.overlay.node.Node;

public class RegistrySendsNodeManifest {
	
	public int routingTableSize;
	public int[] peerList;
	public ArrayList<Node> peerListInfo = new ArrayList<Node>() ;
	
	public int type;
	public ArrayList<Integer> listOfNodeIdsInSystem= new ArrayList<Integer>();
	public int[] ipAddressLength;
	public int noOfNodesInSystem;
	
	 public RegistrySendsNodeManifest(int type, int routingTableSize,int[] peerList, ArrayList<Node> peerListInfo, ArrayList<Integer> listOfNodeIdsInSystem )
	  {
		this.routingTableSize= routingTableSize;
		this.peerList=peerList;
		this.peerListInfo= peerListInfo ;
		this.type=type;
		this.listOfNodeIdsInSystem=listOfNodeIdsInSystem;
	  }
	  
	  public RegistrySendsNodeManifest(byte[] marshalledBytes) throws IOException
	  {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		//System.out.println("**************************");
		//System.out.println("TYPE:"+ din.readInt());
		routingTableSize = din.readByte();
		//System.out.println("NR:"+ routingTableSize);
		
		ipAddressLength= new int[routingTableSize];
		peerList= new int[routingTableSize];
		
		for(int i=0;i<routingTableSize; i++)
		{
			peerList[i]= din.readInt();
			//System.out.println("peerList :"+ din.readInt());
			ipAddressLength[i] = din.readByte();
			//System.out.println("ipAdd length:"+ ipAddressLength[i]);
			
			byte[] ip_address_bytes = new byte[ipAddressLength[i]];
			din.readFully(ip_address_bytes);
			//System.out.println("IP:"+new String(ip_address_bytes));
			  
			Node node= new  Node();
			node.setIp_address( new String(ip_address_bytes) );
			node.setPort(din.readInt());
			node.setNodeIdentifier(peerList[i]);
			
			peerListInfo.add(node);
			//System.out.println("port:"+din.readInt());
			
			/*
			noOfNodesInSystem= din.readByte();
			byte[] nodeList= new byte[noOfNodesInSystem];
			nodeList= din.read();
			*/
			
			
		}
		
		noOfNodesInSystem= din.readByte();
		
		for(int j=0;j< noOfNodesInSystem; j++)
		{
			listOfNodeIdsInSystem.add(din.readInt());
		}
		
		//System.out.println("**************************");
		baInputStream.close();
		din.close();		
	  }
	  
	  
	  public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
	  {
		  byte[] marshalledBytes = null;
		  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		  
		  dout.writeInt(type);
		//  System.out.println("*****************************************");
		  //System.out.println("TYPE:"+type);
		  
		  dout.writeByte(routingTableSize);
		  //System.out.println("NR:"+routingTableSize);
		  
		  
		  for(int i=0;i<routingTableSize; i++)
		  {
			  dout.writeInt(peerList[i]);
			  
			//  System.out.println("distannt Node: "+peerList[i]);
			  
			  String ipAddress = peerListInfo.get(i).ip_address;
			  byte[] ipbytes = ipAddress.getBytes();
			  int ipLength = ipbytes.length;
			  
			  dout.writeByte(ipLength);
			  //System.out.println("IP Length:"+ipLength);
			  dout.write(ipbytes);
			  //System.out.println("IP:"+ipAddress);
			  int portNo= peerListInfo.get(i).port;
			  //System.out.println("PortNO:"+ portNo);
			  
			  dout.writeInt(portNo);
			  
			  
			  /*
			  
			  byte[] nodeList= new byte[noOfNodes];
			  for(int j=0;j< noOfNodes; j++)
			  {
				  int temp= listOfNodeIdsInSystem.get(i);
				  nodeList[j]= (byte)temp; 
			  }
			  dout.write(nodeList);
			  
			  */
			  
		  }
		  
		  
		  int noOfnodes= listOfNodeIdsInSystem.size();
		  //System.out.println("No. of nodes in system:"+noOfnodes);
			
		  int noOfNodes=listOfNodeIdsInSystem.size();
		  dout.writeByte(noOfNodes);
		 
		  for(int i=0;i<listOfNodeIdsInSystem.size();i++)
		  {
			  int temp= listOfNodeIdsInSystem.get(i);
			  dout.writeInt(temp);
		  }
		  
		  
		  dout.flush();
		  marshalledBytes = baOutputStream.toByteArray();
		  
		  baOutputStream.close();
		  dout.close();
		  return marshalledBytes;		  
	  }

	

}
