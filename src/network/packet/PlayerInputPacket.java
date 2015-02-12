package network.packet;

import network.ByteConversions;
import core.controller.PlayerController;

public class PlayerInputPacket extends Packet {

	String name;
	String command;
	
	public PlayerInputPacket(PlayerController controller, String command, float time) {
		super(PacketID.PLAYERINPUT,new byte[256]);
		
		int offset = 1;
		byte[] b;
		
		// set time
		this.time = time;
		b = ByteConversions.floatToByteArray(time);
		for(int i=0;i<4;i++) rawData[i+offset] = b[i];
		offset += 4;
		
		// name
		name = controller.getPlayerID();
		b = ByteConversions.String32toByteArray(controller.getPlayerID());
		for(int i=0;i<64;i++) rawData[i+offset] = b[i];
		offset += 64;
		
		// set command
		this.command = command;
		b = ByteConversions.String32toByteArray(command);
		for(int i=0;i<64;i++) rawData[i+offset] = b[i];
		offset += 64;
	}
	
	public PlayerInputPacket(byte[] data) {
		super(PacketID.PLAYERINPUT,data);
			
		int offset = 1;
		
		// time
		time = ByteConversions.byteArrayToFloat(data, offset);
		offset += 4;
		
		// name
		name = ByteConversions.byteToString32(data,offset);
		offset += 64;
		
		// command
		command = ByteConversions.byteToString32(data,offset);
		offset += 64;
	}

	/*---------*/
	/* GETTERS */
	/*---------*/
	
	public String getPlayerID() {
		return name;
	}
	
	public String getCommand() {
		return command;
	}
}
