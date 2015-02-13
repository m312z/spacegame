package core.game;

import model.Board;
import model.building.Building;
import model.building.BuildingFactory;
import model.building.BuildingType;
import network.packet.Packet;
import network.packet.PlayerInputPacket;

public class PacketHandler {

	/**
	 * Apply a user generated event to the game.  Could be from remote player.
	 * Generally these are game actions; the validity of which is not checked here, 
	 * but by the model. 
	 * @param board		the model
	 * @param packet	the action/event to apply
	 */
	public static void applyPacket(Board board, Packet packet) {
		switch (packet.getType()) {
		case PLAYERINPUT:
			String[] command = ((PlayerInputPacket)packet).getCommand().split("/");
			switch(command[0]) {
				case "GOO":
					// make some goo
					board.getMap().getGooMass()[Integer.valueOf(command[1])][Integer.valueOf(command[2])] += 5000;
					break;
				case "B":
					// build building
					BuildingType type = BuildingType.values()[Integer.parseInt(command[1])];
					Building b = BuildingFactory.makeBuilding(type, Integer.parseInt(command[2]),Integer.parseInt(command[3]));
					board.addBuilding(b);
					break;
				case "T":
					// activate/deactivate building
					Integer[] togPos = new Integer[] {Integer.valueOf(command[1]),Integer.valueOf(command[2])};
					if(board.getBuildings().containsKey(togPos));
					board.toggleBuilding(togPos);
					break;
				case "R":
					// build building
					board.removeBuilding(new Integer[] {Integer.valueOf(command[1]),Integer.valueOf(command[2])});
					break;
			}
			break;
		}
	}
}