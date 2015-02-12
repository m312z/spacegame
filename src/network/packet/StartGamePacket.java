package network.packet;

import network.ByteConversions;

public class StartGamePacket extends Packet {
	
	public StartGamePacket() {
		super(Packet.PacketID.STARTGAME, new byte[5]);
				
		int offset = 1;
		byte[] b;
		
		// set time
		b = ByteConversions.floatToByteArray(time);
		for(int i=0;i<4;i++) rawData[i+offset] = b[i];
		offset += 4;
	}	
	
	public StartGamePacket(byte[] data) {
		super(Packet.PacketID.STARTGAME, data);
		int offset = 1;
		
		// time
		time = ByteConversions.byteArrayToFloat(data, offset);
		offset+=4;
	}
}
