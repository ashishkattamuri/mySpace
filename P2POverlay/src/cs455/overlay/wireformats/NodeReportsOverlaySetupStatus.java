package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NodeReportsOverlaySetupStatus {
	
	public int type;
	public int status;
	public String infoString;
		
	public NodeReportsOverlaySetupStatus(int type,int status_code,String add_info)
		  {
			this.type=type;
			this.status=status_code;
			this.infoString=add_info;
		  }
		  
		  public NodeReportsOverlaySetupStatus(byte[] marshalledBytes) throws IOException
		  {
			ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
			DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
			
			type = din.readInt();
			status = din.readByte();
			
			int add_info_length = din.readInt();
			byte[] add_info_bytes = new byte[add_info_length];
			din.readFully(add_info_bytes);
			
			infoString = new String(add_info_bytes);
			
			baInputStream.close();
			din.close();		
		  }
		  
		  
		  public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
		  {
			  byte[] marshalledBytes = null;
			  ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
			  DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
			  
			  dout.writeInt(type);
			  dout.writeByte(status);
			  
			  byte[] add_info_bytes = infoString.getBytes();
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
