package network.packet;

public class Packet {

	public enum PacketID {

		UNRECOGNIZED((byte) 255),

		/* Server accepts new client */
		SERVERREPLY((byte) 64),
		/* adds a player object */
		PLAYERIDENT((byte) 0),
		/* server tells players the game is starting */
		STARTGAME((byte) 2),
		/* frequently passes controller input */
		PLAYERINPUT((byte) 3),
		/* server tells client of loss */
		PLAYERDISCONNECT((byte) 4);
				
		public byte id;

		private PacketID(byte id) {
			this.id = id;
		}

		public static PacketID getByID(byte id) {
			for (PacketID p : values())
				if (p.id == id)
					return p;
			return UNRECOGNIZED;
		}
	}

	protected float time;
	protected PacketID type;
	protected byte[] rawData;
	
	public Packet(PacketID type, byte[] data) {
		this.type = type;
		rawData = data;
		// set type
		rawData[0] = type.id;
	}

	public PacketID getType() {
		return type;
	}
	
	public byte[] getRawData() {
		return rawData;
	}
	
	public float getTime() {
		return time;
	}
	
	public static Packet read(byte[] data) {

		Packet p = null;

		switch (PacketID.getByID(data[0])) {
		case PLAYERIDENT:
			p = new PlayerIdentPacket(data);
			break;
		case PLAYERINPUT:
			p = new PlayerInputPacket(data);
			break;
		case SERVERREPLY:
			p = new ServerReplyPacket(data);
			break;
		case PLAYERDISCONNECT:
			p = new PlayerDisconnectPacket(data);
			break;
		case STARTGAME:
			p = new StartGamePacket(data);
			break;
		case UNRECOGNIZED:
		default:
			p = new Packet(PacketID.UNRECOGNIZED,data);
			break;
		}

		return p;
	}

}
