package cs455.overlay.node;

public class Node {
	
	public String ip_address;
	public int port;
	public String name;
	public boolean taskComplete;
	public int nodeIdentifier;
	
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isTaskComplete() {
		return taskComplete;
	}
	public void setTaskComplete(boolean taskComplete) {
		this.taskComplete = taskComplete;
	}
	public int getNodeIdentifier() {
		return nodeIdentifier;
	}
	public void setNodeIdentifier(int nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

}
