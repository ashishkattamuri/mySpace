
package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeReportsTrafficSummary {

	public int type;
	public int assignedNodeID;
	public int packetsSent;
	public int packetsRelayed;
	public long payloadSent;
	public int packetsRecieved;
	public long payloadRecieved;
	

	public OverlayNodeReportsTrafficSummary(int type, int nodeId, int pSent, int pRelay, long PLsent, int pRecieved, long PLRecieve)
	{

		this.type=type ;
		assignedNodeID=nodeId;
		packetsSent=pSent;
		packetsRelayed=pRelay;
		payloadSent=PLsent;
		packetsRecieved=pRecieved;
		payloadRecieved=PLRecieve;


	}


	public OverlayNodeReportsTrafficSummary(byte[] marshalledBytes) throws IOException
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		type = din.readInt();
		assignedNodeID= din.readInt();
		packetsSent= din.readInt();
		packetsRelayed= din.readInt();
		payloadSent=din.readLong();
		packetsRecieved=din.readInt();
		payloadRecieved=din.readLong();


		baInputStream.close();
		din.close();		
	}


	public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeInt(type);
		dout.writeInt(assignedNodeID);
		dout.writeInt(packetsSent);
		dout.writeInt(packetsRelayed);
		dout.writeLong(payloadSent);
		dout.writeInt(packetsRecieved);
		dout.writeLong(payloadRecieved);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();

		baOutputStream.close();
		dout.close();
		return marshalledBytes;		  
	}




}
