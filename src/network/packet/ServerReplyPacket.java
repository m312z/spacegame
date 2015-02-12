package network.packet;

import network.ByteConversions;

public class ServerReplyPacket extends Packet {

	boolean accepted;
	String gameName;
	String welcomeMessage;
	String rejectionReason;
	int seed;

	public ServerReplyPacket(byte[] data) {
		super(PacketID.SERVERREPLY, data);
		
		int offset = 1;
		
		// time
		time = ByteConversions.byteArrayToFloat(data, offset);
		offset += 4;
		
		// accepted
		accepted = ByteConversions.byteToBoolean(data[offset]);
		offset += 1;
		
		// game name
		gameName = ByteConversions.byteToString32(data,offset).trim();
		offset += 64;

		// seed
		seed = ByteConversions.byteArrayToInt(data, offset);
		
		// game name
		welcomeMessage = ByteConversions.byteToString32(data,offset).trim();
		offset += 64;
		
		// game name
		rejectionReason = ByteConversions.byteToString32(data,offset).trim();
		offset += 64;
	}

	public ServerReplyPacket(boolean accepted, String gameName, int seed, String welcomeMessage,String rejectionReason, float time) {
		super(PacketID.SERVERREPLY, new byte[1024]);
		
		byte[] b;
		int offset = 1;
		
		// set time
		this.time = time;
		b = ByteConversions.floatToByteArray(time);
		for(int i=0;i<4;i++) rawData[i+offset] = b[i];
		offset += 4;
		
		// accepted
		rawData[offset] = ByteConversions.BooleanToByte(accepted);
		offset += 1;
			
		// game name
		b = ByteConversions.String32toByteArray(gameName);
		for(int i=0;i<64;i++) rawData[i+offset] = b[i];
		offset += 64;

		// set seed
		this.seed = seed;
		b = ByteConversions.intToByteArray(seed);
		for(int i=0;i<4;i++) rawData[i+offset] = b[i];
		offset += 4;
		
		// welcome message 
		b = ByteConversions.String32toByteArray(welcomeMessage);
		for(int i=0;i<64;i++) rawData[i+offset] = b[i];
		offset += 64;
		
		// rejection message
		b = ByteConversions.String32toByteArray(rejectionReason);
		for(int i=0;i<64;i++) rawData[i+offset] = b[i];
		offset += 64;
	}

	/*---------*/
	/* GETTERS */
	/*---------*/

	public boolean isAccepted() {
		return accepted;
	}
	
	public String getGameName() {
		return gameName;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}
	
	public String getRejectionReason() {
		return rejectionReason;
	}
	
	public int getSeed() {
		return seed;
	}
}
