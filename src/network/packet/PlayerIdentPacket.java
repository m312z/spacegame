package network.packet;

import model.Player;
import network.ByteConversions;

public class PlayerIdentPacket extends Packet {

	String playerIdent;
	String password;
	Player player;	
	
	public PlayerIdentPacket(String password, Player player, float time) {
		super(PacketID.PLAYERIDENT,new byte[141]);
		
		byte[] b;
		int offset = 1;
		
		// set time
		this.time = time;
		b = ByteConversions.floatToByteArray(time);
		for(int i=0;i<4;i++) rawData[i+offset] = b[i];
		offset += 4;
		
		// set name
		this.playerIdent = player.getPlayerID();
		b = ByteConversions.String32toByteArray(playerIdent);
		for(int i=0;i<b.length;i++)
			rawData[i+offset] = b[i];
		offset += 64;
		
		// set password
		this.password = password;
		b = ByteConversions.String32toByteArray(password);
		for(int i=0;i<b.length;i++)
			rawData[i+offset] = b[i];
		offset += 64;
		
		this.player = player;
	}
	
	public PlayerIdentPacket(byte[] data) {
		super(PacketID.PLAYERIDENT,data);
		
		int offset = 1;
		
		// time
		time = ByteConversions.byteArrayToFloat(data, offset);
		offset += 4;
		
		// name
		playerIdent = ByteConversions.byteToString32(data,offset);
		offset += 64;
		
		// password
		password = ByteConversions.byteToString32(data,offset);
		offset += 64;
		
		player = new Player(playerIdent);
	}

	public String getPlayerID() {
		return playerIdent;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Player getPlayer() {
		return player;
	}
}
