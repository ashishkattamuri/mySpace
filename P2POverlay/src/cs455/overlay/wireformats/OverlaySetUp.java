package cs455.overlay.wireformats;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class OverlaySetUp {
	
	public int type;
	public int port;
	public int nodeId;
	public String connectionStatus;
	
	public OverlaySetUp(int type, int p, int id, String connectionStatus)
	{ 
		this.type= type;
		port = p;
		nodeId = id;
		this.connectionStatus = connectionStatus;
	}
	
	public OverlaySetUp(byte[] marshalledBytes) throws IOException    //constructor in case this message is received
	  {
		  ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		  DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
			
		  type= din.readInt();
		  port = din.readInt();
		  nodeId = din.readInt();
		  
		  int add_info_length = din.readInt();
		  byte[] add_info_bytes = new byte[add_info_length];
		  din.readFully(add_info_bytes);
			
		  connectionStatus= new String(add_info_bytes);
			
		  
		  baInputStream.close();
		  din.close();	
	  }
	
	public byte[] getBytes() throws IOException
	{
		byte[] marshalledBytes = null;
		  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		  
		  dout.writeInt(type);
		  dout.writeInt(port);
		  dout.writeInt(nodeId);
		  
		  byte[] add_info_bytes = connectionStatus.getBytes();
		  int elementlength = add_info_bytes.length;
		  dout.writeInt(elementlength);
		  dout.write(add_info_bytes);
		  
		  
		  
		  dout.flush();
		  marshalledBytes = baOutputStream.toByteArray();
		  
		  baOutputStream.close();
		  dout.close();
		  return marshalledBytes;
	}

}
