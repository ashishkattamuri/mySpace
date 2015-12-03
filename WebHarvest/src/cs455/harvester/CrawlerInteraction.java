package cs455.harvester;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CrawlerInteraction {
	
	 public int type;
	 public String url;
	  
	  public CrawlerInteraction(int type,String url)
	  {
		this.type=type;
		this.url=url;
	  }
	  
	  public CrawlerInteraction(byte[] marshalledBytes) throws IOException
	  {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int add_info_length = din.readInt();
		byte[] add_info_bytes = new byte[add_info_length];
		din.readFully(add_info_bytes);
		
		url = new String(add_info_bytes);
		
		baInputStream.close();
		din.close();		
	  }
	  
	  
	  public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
	  {
		  byte[] marshalledBytes = null;
		  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		  
		  dout.writeInt(type);
		  
		  
		  byte[] add_info_bytes = url.getBytes();
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
