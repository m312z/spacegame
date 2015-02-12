package network;

import network.packet.Packet;

public class ServerMessage {

	String recipient;
	Packet message;
	
	public ServerMessage(String recipient, Packet message) {
		this.recipient = recipient;
		this.message = message;
	}
}
