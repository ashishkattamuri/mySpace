package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryRequestsTaskInitiate {

	public int type;
	public int numberOfPackets;

	public RegistryRequestsTaskInitiate(int type, int no)
	{
		this.type= type;
		numberOfPackets= no;

	}


	public RegistryRequestsTaskInitiate(byte[] marshalledBytes) throws IOException
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

		type = din.readInt();
		numberOfPackets = din.readInt();

		baInputStream.close();
		din.close();		
	}


	public byte[] getBytes() throws IOException   // getbytes for this structure  -- ie convert this structure to bytearray
	{
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

		dout.writeInt(type);
		dout.writeInt(numberOfPackets);

		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();

		baOutputStream.close();
		dout.close();
		return marshalledBytes;		  
	}




}
