package network.packet;

import network.ByteConversions;

public class PlayerDisconnectPacket extends Packet {

	String playerID;

	public PlayerDisconnectPacket(byte[] data) {
		super(PacketID.PLAYERDISCONNECT, data);
		
		int offset = 1;
		
		// time
		time = ByteConversions.byteArrayToFloat(data, offset);
		offset += 4;
		
		// player name
		playerID = ByteConversions.byteToString32(data,offset).trim();
		offset += 64;
	}

	public PlayerDisconnectPacket(String name, float time) {
		super(PacketID.PLAYERDISCONNECT, new byte[70]);
		
		byte[] b;
		int offset = 1;
		
		// set time
		this.time = time;
		b = ByteConversions.floatToByteArray(time);
		for(int i=0;i<4;i++) rawData[i+offset] = b[i];
		offset += 4;
		
		// player name
		playerID = name;
		b = ByteConversions.String32toByteArray(name);
		for(int i=0;i<64;i++) rawData[i+offset] = b[i];
		offset += 64;
	}

	/*---------*/
	/* GETTERS */
	/*---------*/

	public String getPlayerID() {
		return playerID;
	}
	
}
