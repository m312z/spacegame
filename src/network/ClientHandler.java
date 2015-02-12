package network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

import network.packet.Packet;

public class ClientHandler implements Runnable {

	private Server masterServer;
	private String playerIdent;
	private Socket socket = null;
	
	public ClientHandler(Server masterServer,
			String playerIdent,
			Socket socket) {
		this.masterServer = masterServer;
		this.playerIdent = playerIdent;
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getPlayerIdent() {
		return playerIdent;
	}

	public void disconnect() {
		try {
			if(!socket.isClosed())
				socket.close();
		} catch (IOException e) {
			System.err.println("Error closing client socket.");
		}
	}

	public void run() {
		try {
			InputStream in = socket.getInputStream();
			byte[] kiloByte = new byte[1024];

			while (in.read(kiloByte) != -1)
				masterServer.addTask(Packet.read(kiloByte));

			in.close();

		} catch (SocketException e) {
		} catch (IOException e) {
		} finally {
			disconnect();
			masterServer.disconnectClient(playerIdent);
		}
	}
}
