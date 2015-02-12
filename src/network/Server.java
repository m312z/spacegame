package network;

import gui.ServerFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import core.game.Game;

import network.packet.Packet;
import network.packet.PlayerIdentPacket;
import network.packet.ServerReplyPacket;

public class Server implements Runnable {
		
	ServerFrame frame;
	
	public String gameName;
	public String motd;
	
	boolean running = true;
	Thread welcomeThread;
	ServerSocket serverSocket;
	ExecutorService threadPool = Executors.newFixedThreadPool(10);
	TreeMap<String,ClientHandler> clients;
	
	Lock messageLock = new ReentrantLock(true);
	BlockingQueue<ServerMessage> messages;

	Lock taskLock = new ReentrantLock();
	TreeMap<Float,Packet> tasks;
	
	/*--------------*/
	/* Constructors */
	/*--------------*/
	
	public Server(String gameName, ServerFrame frame) {
		this(gameName,"This server is "+gameName, frame);
	}
	
	public Server(String gameName, String motd, ServerFrame frame) {
		
		this.frame = frame;
		this.gameName = gameName;
		this.motd = motd;
		
		messages = new LinkedBlockingQueue<ServerMessage>();
		tasks = new TreeMap<Float,Packet>();
		
		clients = new TreeMap<String,ClientHandler>();
		welcomeThread = new Thread(this);
		welcomeThread.start();
		
		new Thread(new Demagogue()).start();
	}
	
	public void finish() {
		running = false;
		if(serverSocket!=null && serverSocket.isBound() && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getNoConnections() {
		return clients.size();
	}
	
	public void addTask(Packet packet) {
		taskLock.lock();
		tasks.put(packet.getTime(),packet);
		taskLock.unlock();
	}
	
	public boolean hasTasks() {
		return (tasks.size()>0);
	}
	
	public Packet getTask(float threshold) {
		if(hasTasks()) {
			Packet p = null;
			taskLock.lock();
			if(tasks.firstKey() <= threshold)
				p = tasks.remove(tasks.firstKey());
			taskLock.unlock();
			return p;
		} else {
			return null;
		}
	}
	
	public void killTasks() {
		taskLock.lock();
		tasks.clear();
		taskLock.unlock();
	}
	
	
	/**
	 * Listen for and accept clients.
	 */
	public void run() {
		
		serverSocket = null;
		try {
			serverSocket = new ServerSocket(7777);
			// AWAIT PLAYER IDENT PACKET
			while(running) {
				addClient(serverSocket.accept());
			}
		} catch (IOException e) {
			frame.output("Could not listen on port: 7777.");
		} finally {
			try {
				frame.output("Closing All Clients");
				if(serverSocket!=null) serverSocket.close();
			} catch (IOException e) {
				frame.output("Error while closing server connection.");
			} finally {
				disconnectAll();
			}
		}
	}
	
	/**
	 * Set up a clientHandler for the new client.
	 * @param socket	the client socket
	 */
	public synchronized void addClient(Socket socket) {
		try {
			if(!running) {
				socket.close();
				return;
			}
			
			InputStream in = socket.getInputStream();
			byte[] kiloByte = new byte[1024];
			in.read(kiloByte);
			Packet p = Packet.read(kiloByte);
			
			OutputStream out = socket.getOutputStream();
			Packet reply;
			
			switch(p.getType()) {
			case PLAYERIDENT:
				PlayerIdentPacket pip = ((PlayerIdentPacket)p); 
				if(clients.containsKey(pip.getPlayerID())) {
					reply = new ServerReplyPacket(false,gameName,Game.getRandomSeed(),motd,"Username already taken on this server.",0);
					out.write(reply.getRawData());
					socket.close();
				} else {
					// accept player
					frame.output("Accepting player: "+pip.getPlayerID());
					ServerReplyPacket srp = new ServerReplyPacket(true,gameName,Game.getRandomSeed(),motd,"",0);
					out.write(srp.getRawData());
					
					// TODO send other players
										
					// start client handler
					clients.put(pip.getPlayerID(),new ClientHandler(this,pip.getPlayerID(),socket));
					
					// TODO inform all other players
					
					threadPool.execute(clients.get(pip.getPlayerID()));
					frame.output("client added: "+pip.getPlayerID());
				}
				break;
			case UNRECOGNIZED:
			default:
				reply = new ServerReplyPacket(false,gameName,Game.getRandomSeed(),motd,"Unrecognised packet.",0);
				out.write(reply.getRawData());
				socket.close();
				break;
			}								
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void disconnectClient(String name) {
		if(clients.containsKey(name))
			clients.get(name).disconnect();
		clients.remove(name);
		frame.output("client disconnected: "+name);
	}

	public synchronized void disconnectAll() {
		for(String s: clients.keySet())
			disconnectClient(s);
	}
	
	public boolean hasClient(String playerIdent) {
		return (playerIdent!=null && clients.containsKey(playerIdent));
	}
	
	public void sendMessage(String recipient, Packet packet) {
		messageLock.lock();
		messages.offer(new ServerMessage(recipient,packet));
		messageLock.unlock();
	}
	
	public void sendMessageToAll(Packet packet) {
		messageLock.lock();
		for(String recipient: clients.keySet())
			messages.offer(new ServerMessage(recipient,packet));
		messageLock.unlock();
	}
	
	class Demagogue implements Runnable {
		public void run() {
			while(running) {
				try {
					ServerMessage message = (messages.take());
					if(clients.containsKey(message.recipient)) {
						try {
							ClientHandler ch = clients.get(message.recipient);
							OutputStream out = ch.getSocket().getOutputStream();
							out.write(message.message.getRawData());
						} catch (IOException e) {
							frame.output("Demagogue: IOException while trying to send to "+message.recipient);
							if(clients.containsKey(message.recipient)) {
								disconnectClient(message.recipient);
							}
						}
					}
				} catch (InterruptedException e) {
					frame.output("Demagogue: Interrupted while taking.");
				}
			}
		}
	}
}
