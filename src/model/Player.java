package model;

public class Player {
	
	public enum PlayerType {BUILDER};

	/* player details */
	String playerID;
	String name;
	PlayerType playerType;

	public Player(String name) {
		this.playerID = name;
		this.name = name;
		this.playerType = PlayerType.BUILDER;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	
}
