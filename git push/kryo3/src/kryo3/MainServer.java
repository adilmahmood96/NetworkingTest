package kryo3;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import packet.Packet;
import packet.Packet2ClientConnected;
import packet.Packet3ClientDisconnect;
import packet.Packet4Chat;
import packet.PacketConnect;

public class MainServer {
	
	//holder where key is String (username) and connection which is client is connected
	private static HashMap<String, Connection> clients = new HashMap<String, Connection>();
	
	public static void main(String[]args) throws IOException{
		
		final Server server = new Server();
		server.start();
		server.bind(23900, 23901);
		
		server.addListener(new Listener(){
			public void received(Connection connection, Object object){
				if(object instanceof Packet){
					if(object instanceof PacketConnect){
						PacketConnect p1 = (PacketConnect) object;
						//when a client connects we put it into the map
						clients.put(p1.username, connection);
						Packet2ClientConnected p2 = new Packet2ClientConnected(); 
						p2.clientName = p1.username;
						server.sendToAllExceptTCP(connection.getID(), p2);
						
						//if the server recieves this packet then client disconnected so we remove.. key is usernsme string
					}else if(object instanceof Packet3ClientDisconnect){
						Packet3ClientDisconnect p3 = (Packet3ClientDisconnect) object;
						clients.remove(p3.clientName);
						//removing them from clients if recieves packet3clientdisconnect..
						server.sendToAllExceptTCP(clients.get(p3.clientName).getID(), p3);
					
					}else if(object instanceof Packet4Chat){
						//client sends chat we want to send it to everyone but person who sent it..
						Packet4Chat p4 = (Packet4Chat) object;
						System.out.println("Chat: " + p4.message);
						server.sendToAllTCP(p4);
					}
				}
				
			}
			
			//this function called whenever client disconnected so we let all other clients know.
			   public void disconnected(Connection connection) {
				   Packet3ClientDisconnect p3 = new Packet3ClientDisconnect();
				   //hashmap cant use for loop gota use iterator..
				   Iterator it = clients.entrySet().iterator();
				   //want to get next value in iterator & pairs return string in connection
				   String username = ""; 
				   while(it.hasNext()){
					   Map.Entry pairs = (Map.Entry)it.next();	
					   if(pairs.getValue().equals(connection)){
						   username = (String) pairs.getKey();
						   break;
					   }
				   }
				   if(!username.equalsIgnoreCase("")){
					   p3.clientName = username;	
					   server.sendToAllExceptTCP(connection.getID(), p3);

				   }
			   }
			   
		});
		
		//regoster classes..
		server.getKryo().register(Packet.class);
		server.getKryo().register(PacketConnect.class);
		server.getKryo().register(Packet2ClientConnected.class);
		server.getKryo().register(Packet3ClientDisconnect.class);
		server.getKryo().register(Packet4Chat.class);

		//function to add connection to an array
		
		
		
		
	}

}
