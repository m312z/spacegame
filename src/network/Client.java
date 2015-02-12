package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import core.game.Game;

import model.Player;
import network.packet.Packet;
import network.packet.Packet.PacketID;
import network.packet.PlayerIdentPacket;
import network.packet.ServerReplyPacket;

public class Client {

	/* identity */
	String playerIdent;
	
	/* connection */
	String ip;
	Socket socket;
	boolean successfulConnection = false;
	String serverName;
	
	/* messages to be sent */
	BlockingQueue<byte[]> messages;
	
	/* game time */
	float startTime;
	
	/* output thread to server */
	Thread shoutThread;
	
	/* incoming tasks */
	Lock taskLock = new ReentrantLock(true);
	TreeMap<Float,Packet> tasks;

	public Client(String password, String ip, Player player) {
		
		this.playerIdent = player.getPlayerID();
		messages = new LinkedBlockingQueue<byte[]>();
		tasks = new TreeMap<Float,Packet>();
		socket = null;
		
		try {
			socket = new Socket(ip, 7777);
			
			OutputStream out = socket.getOutputStream();
			InputStream in = socket.getInputStream();
			
			// SEND PLAYER IDENT PACKET
			System.out.println("Sending player identification.");
			out.write(new PlayerIdentPacket(password,player,0).getRawData());
			
			// AWAIT SERVER REPLY PACKET
			System.out.println("Waiting for confirmation...");
			byte[] kiloByte = new byte[1024];
			in.read(kiloByte);
			Packet pack = Packet.read(kiloByte);

			while(pack.getType()!=PacketID.SERVERREPLY) {
				in.read(kiloByte);
				pack = Packet.read(kiloByte);	
			}
			
			if(((ServerReplyPacket)pack).isAccepted()) {
				System.out.println("Connected!");
				System.out.println(((ServerReplyPacket)pack).getWelcomeMessage());
				serverName = ((ServerReplyPacket)pack).getGameName();
				startTime =  ((ServerReplyPacket)pack).getTime();
				Game.setSeed(((ServerReplyPacket)pack).getSeed());

				successfulConnection = true;
				
				new Thread(new Ear()).start();
				shoutThread = new Thread(new Mouth());
				shoutThread.start();
			} else {
				System.out.println("Connection rejected by server: ");
				System.out.println(((ServerReplyPacket)pack).getRejectionReason());
				socket.close();
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Don't know about host: "+ip);
		} catch (IOException e) {
			System.out.println("Couldn't get I/O for the connection to: "+ip);
		} finally {
			try {
				if(!successfulConnection && socket!=null)
					socket.close();
			} catch(IOException e) {
				// nothing
			}
		}
	}
	
	public void disconnect() {
		try {
			socket.close();
			shoutThread.interrupt();
			successfulConnection = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSuccessfulConnection() {
		return successfulConnection;
	}
		
	public String getServerName() {
		return serverName;
	}
	
	public float getStartTime() {
		return startTime;
	}
	
	/**
	 * @param threshold a time value.
	 * @return the earliest task, if it is before threshold, otherwise nothing.
	 */
	public Packet getTask(float threshold) {
		if(hasTasks()) {
			taskLock.lock();
			Packet p = null;
			if(tasks.firstKey()<threshold)
				p = tasks.remove(tasks.firstKey());
			taskLock.unlock();
			return p;
		} else {
			return null;
		}
	}
	
	public boolean hasTasks() {
		return (tasks.size()>0);
	}
	
	/**
	 * Add a local task to the task list.
	 * @param packet the local task.
	 */
	public void addTask(Packet packet) {
		taskLock.lock();
		tasks.put(packet.getTime(),packet);
		taskLock.unlock();
	}
	
	/**
	 * Send a task to the server.
	 * @param packet the task.
	 */
	public void sendMessage(Packet packet) {
		byte[] data = packet.getRawData();
		messages.offer(data);
	}
	
	/*------------------*/
	/* Internal Classes */
	/*------------------*/
	
	/**
	 * Retrieves incoming messages from server.
	 * @author michael
	 */
	class Ear implements Runnable {
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				byte[] kiloByte = new byte[1024];
				
				while (socket.isConnected() && in.read(kiloByte) != -1) {
					Packet packet = Packet.read(kiloByte);
					taskLock.lock();
					tasks.put(packet.getTime(),packet);
					taskLock.unlock();
				}
				in.close();

			} catch (IOException e) {
			}
			successfulConnection = false;
		}
	}

	/**
	 * Sends messages to server.
	 * @author michael
	 */
	class Mouth implements Runnable {
		public void run() {

			OutputStream out;
			try {
				out = socket.getOutputStream();
				while (socket.isConnected())
					out.write(messages.take());
				
			} catch (IOException e) {
			} catch (InterruptedException e) {
			} finally {
				disconnect();
			}
		}
	}
}
