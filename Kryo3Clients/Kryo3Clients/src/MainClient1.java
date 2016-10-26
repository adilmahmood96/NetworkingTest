
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import packet.Packet;
import packet.Packet2ClientConnected;
import packet.Packet3ClientDisconnect;
import packet.Packet4Chat;
import packet.PacketConnect;
 
 //this class is the basic gui for the client...
//implemts action listener so it checks if send button is pressed
public class MainClient1 implements ActionListener {
 
        private final JFrame frame = new JFrame("Chat Client");
        private final JTextArea textArea = new JTextArea();
        private final JTextField textField = new JTextField(25);
        private final JButton sendButton = new JButton("Send");
        private final Client client;
        private String username;
        
        public MainClient1() {
        	client = new Client();
        	client.start();
        	//try connect client
        	try {
    				client.connect(500,"127.0.0.1", 23900, 23901);
    			} catch (IOException e) {
    				JOptionPane.showMessageDialog(null, "Cannot connect to server :(");
    				return;
    			}
        	
    		//register classes..
    		client.getKryo().register(Packet.class);
    		client.getKryo().register(PacketConnect.class);
    		client.getKryo().register(Packet2ClientConnected.class);
    		client.getKryo().register(Packet3ClientDisconnect.class);
    		client.getKryo().register(Packet4Chat.class);
    		
    		//time for a listener..
    		
    		client.addListener(new Listener(){
    			
    			public void received(Connection connection, Object object){
    				
    				if(object instanceof Packet){
    					if(object instanceof Packet2ClientConnected){
    						Packet2ClientConnected p2 = (Packet2ClientConnected) object;
    						System.out.println("Connected");
    						textArea.append(p2.clientName + " connected. \n");
    						
    					}else if (object instanceof Packet3ClientDisconnect){
    						Packet3ClientDisconnect p3 = (Packet3ClientDisconnect) object;
    						textArea.append(p3.clientName + "disonnected. \n");
    					
    					}else if(object instanceof Packet4Chat){
    						
    						Packet4Chat p4 = (Packet4Chat) object;
    						textArea.append(p4.username + ": " + p4.message + "\n");
    					}
    	
    					
    				}
    			}
    			
    		});
        	
            username = JOptionPane.showInputDialog("Please enter Username: ");
    
        	PacketConnect p1 = new PacketConnect();
        	p1.username = username;
        	client.sendTCP(p1);
        	
        	
        	//first set a fram with size name blah blah.. basic set up 
                frame.setSize(450, 375);
                //centered in page
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               
                Panel p = new Panel();
               
                sendButton.addActionListener(this);
                textArea.setEditable(false);
                //brings it to next lane
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane areaScrollPane = new JScrollPane(textArea);
                areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                areaScrollPane.setPreferredSize(new Dimension(430, 275));
               
                p.add(areaScrollPane);
                p.add(textField);
                p.add(sendButton);
               
                frame.add(p);
                frame.setVisible(true);
        }
       
        public static void main(String[] args) {
                new MainClient1();
        }
 
        @Override
        //when pressend we get message from textfield
        //add it to text area
        //then sets the textfield to empty, so u can send more messages..
        public void actionPerformed(ActionEvent arg0) {
                String message = textField.getText();
                
                //send message to server once press send.. 
                if(!message.equalsIgnoreCase("")){
                	textField.setText("");
                	Packet4Chat p4 = new Packet4Chat();
                	p4.username = username;
                	p4.message = message;
                	//send over the packet which has messages and stuff
                	client.sendTCP(p4); 
                }
                 
              
        }
 
}
