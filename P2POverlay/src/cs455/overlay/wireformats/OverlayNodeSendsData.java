package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class OverlayNodeSendsData {
	
	public int type;
	public int destinationNode;
	public int sourceNode;
	public int payload;
	public int noOfHops;
	public ArrayList<Integer> visitedNodes= new ArrayList<Integer>();
	
	
	
	public OverlayNodeSendsData(int type,int destination, int source, int payload, int noOfHops, ArrayList<Integer> visitedNodes)
	{
		this.type=type;
		destinationNode= destination;
		sourceNode=source;
		this.payload=payload;
		this.noOfHops = noOfHops;
		this.visitedNodes = visitedNodes;
		
	}
	
	
	public OverlayNodeSendsData(byte[] marshalledBytes) throws IOException
	  {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type= din.readInt();
		destinationNode = din.readInt();
		sourceNode= din.readInt();
		payload =din.readInt();;
		noOfHops= din.readInt();
		
		/*
		for(int i=0;i<noOfHops ;i++)
		{
			visitedNodes.add(din.readInt());
		}
		*/
		
		
		baInputStream.close();
		din.close();		
	  }
	  
	
	


	public byte[] getBytes() throws IOException  
	  {
		  byte[] marshalledBytes = null;
		  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		  
		  dout.writeInt(type);
		  dout.writeInt(destinationNode);
		  dout.writeInt(sourceNode);
		  dout.writeInt(payload);
		  dout.writeInt(visitedNodes.size());
		  
		  /*
		  for(int i=0;i<visitedNodes.size() ;i++)
		  {
			  dout.writeInt(visitedNodes.get(i));
			  
		  }
		  */
		  
		  
		  dout.flush();
		  marshalledBytes = baOutputStream.toByteArray();
		  
		  baOutputStream.close();
		  dout.close();
		  return marshalledBytes;		  
	  }

	
	
	
	

}
