

package cs455.overlay.wireformats;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class RegistryReportsDeregistrationStatus 
{
	  public int type;
	  public byte status_code;
	  public String add_info;
	  
	  public RegistryReportsDeregistrationStatus(int type,byte status_code,String add_info)
	  {
		this.type=type;
		this.status_code=status_code;
		this.add_info=add_info;
	  }
	  
	  public RegistryReportsDeregistrationStatus(byte[] marshalledBytes) throws IOException
	  {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		status_code = din.readByte();
		
		int add_info_length = din.readInt();
		byte[] add_info_bytes = new byte[add_info_length];
		din.readFully(add_info_bytes);
		
		add_info = new String(add_info_bytes);
		
		baInputStream.close();
		din.close();		
	  }
	  
	  
	  public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
	  {
		  byte[] marshalledBytes = null;
		  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		  
		  dout.writeInt(type);
		  dout.writeByte(status_code);
		  
		  byte[] add_info_bytes = add_info.getBytes();
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

