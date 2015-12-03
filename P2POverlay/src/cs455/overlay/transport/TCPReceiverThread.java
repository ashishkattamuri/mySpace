package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.node.MessagingNode;
import cs455.overlay.node.Registry;


public class TCPReceiverThread extends Thread{
	private Socket socket;
	private DataInputStream din;
	public Registry.RecieveCommunicationsThread RcT;
	public MessagingNode Node;
	
	public TCPReceiverThread(Socket socket, Registry.RecieveCommunicationsThread c,MessagingNode n) throws IOException {
		this.socket = socket;
		din = new DataInputStream(socket.getInputStream());
		RcT= c;
		Node=n;
		
	}

	public void run() {
		int dataLength;
		byte[] data;
		while (socket != null) {
			try {
				
				dataLength = din.readInt();

				data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
				
				if(Node!=null)
				{
					Node.onEvent(data);
				}
				else
				{
					RcT.onEvent(data);
				}
				
			} 
			catch (SocketException se) {
				System.out.println(se.getMessage());
				
				break;
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage()) ;
				break;
			}
		}
	}
	
	public byte[] receiveData() throws IOException
	  {
		int dataLength;
		dataLength = din.readInt();
		byte[] data = new byte[dataLength];
		din.readFully(data, 0, dataLength);
		
		return data;
		
		
	  }
	  
	 
	
}