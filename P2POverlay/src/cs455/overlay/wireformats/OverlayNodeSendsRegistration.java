package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OverlayNodeSendsRegistration {
	
	
	  public int type;
	  public String ip_address;
	  public int port;
	  
	  public OverlayNodeSendsRegistration(int type,String ip, int port)
	  {
		  this.type=type;
		  this.ip_address=ip;
		  this.port=port;		  
	  }
	  
	  public OverlayNodeSendsRegistration(byte[] marshalledBytes) throws IOException    //constructor in case this message is received - ie load structure from bytes
	  {
		  
		  ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		  DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		  type = din.readInt();
			
		  int ip_address_length = din.readInt();
		  byte[] ip_address_bytes = new byte[ip_address_length];
		  din.readFully(ip_address_bytes);
		  
		  ip_address = new String(ip_address_bytes);
		  
		  port = din.readInt();
			
		  baInputStream.close();
		  din.close();	
	  }
	  
	  public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
	  {
		  byte[] marshalledBytes = null;
		  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		  
		  dout.writeInt(type);
		  
		  byte[] ipbytes = ip_address.getBytes();
		  int elementlength = ipbytes.length;
		  dout.writeInt(elementlength);
		  dout.write(ipbytes);
		  
		  dout.writeInt(port);
		  
		  dout.flush();
		  marshalledBytes = baOutputStream.toByteArray();
		  
		  baOutputStream.close();
		  dout.close();
		  return marshalledBytes;
	  }
	
	

}
