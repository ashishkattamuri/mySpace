package cs455.overlay.transport;

import java.net.Socket;

public class TCPConnectionsCache {
	
	  public TCPSender tcpsender;
	  public int nodeID;
	  
	  public TCPConnectionsCache(TCPSender  s , int n)
	  {
		  tcpsender = s;
		  nodeID =n;
	  }
	  

}
