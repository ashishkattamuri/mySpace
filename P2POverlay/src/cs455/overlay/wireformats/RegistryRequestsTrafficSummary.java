
package cs455.overlay.wireformats;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class RegistryRequestsTrafficSummary 
{
	  public int type;
	  
	  public RegistryRequestsTrafficSummary(int type)
	  {
		this.type=type;
	  }
	  
	  public RegistryRequestsTrafficSummary(byte[] marshalledBytes) throws IOException
	  {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		baInputStream.close();
		din.close();		
	  }
	  
	  
	  public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
	  {
		  byte[] marshalledBytes = null;
		  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		  
		  dout.writeInt(type);
		  
		  dout.flush();
		  marshalledBytes = baOutputStream.toByteArray();
		  
		  baOutputStream.close();
		  dout.close();
		  return marshalledBytes;		  
	  }
}
