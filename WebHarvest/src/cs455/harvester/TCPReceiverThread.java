package cs455.harvester;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.harvester.Crawler;


public class TCPReceiverThread extends Thread{
	private Socket socket;
	private DataInputStream din;
	
	public TCPReceiverThread(Socket socket) throws IOException {
		this.socket = socket;
		din = new DataInputStream(socket.getInputStream());
		
	}

	public void run() {
		int dataLength;
		byte[] data;
		while (socket != null) {
			try {
				
				dataLength = din.readInt();

				data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
				
				Crawler c= new Crawler();
				c.onEvent(data);
				
				
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