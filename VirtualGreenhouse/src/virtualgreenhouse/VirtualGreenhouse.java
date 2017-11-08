/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualgreenhouse;

import java.io.IOException;
import static java.net.Authenticator.RequestorType.SERVER;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Port;
import javax.swing.JOptionPane;
import sun.security.x509.IPAddressName;
import static virtualgreenhouse.IMessage.COMMAND;
import static virtualgreenhouse.IMessage.DIRECTION;

/**
 *
 * @author chris
 */
public class VirtualGreenhouse implements IMessage, ICommands {

   
    public static final int PORT = 1025;
    public GreenHouse green = new GreenHouse();
    String fixedServerName;
    private Connection connection;

    /**
     * @param args the command line arguments
     * @throws java.net.SocketException
     */
    public GreenHouse createGreenhouse(String IPAddressName, int PORT, String name) {

	GreenHouse greenhouse = new GreenHouse();
	return greenhouse;
    }

    public static void main(String[] args) throws SocketException, IOException {
	VirtualGreenhouse vgh = new VirtualGreenhouse();
	LinkedList<byte[]> queue = new LinkedList();
	vgh.green.activate();
	

	GreenHouse gh = GreenHouse.getInstance();
	DatagramSocket socket = new DatagramSocket(PORT);
	

	Runnable UDPConnection = () -> {
	    while (true) {
		try {

		    byte[] buffer = new byte[110];
		    DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // Create packet ready for receiving incoming UDP datagrams
		    System.out.println("Server running on port " + PORT);
		    socket.receive(packet);
		    byte[] data = java.util.Arrays.copyOf(packet.getData(), packet.getLength());
		    

		    queue.addLast(data);

		    for (int i = 9; i <= 15; i++) {
			if (data[COMMAND] == i) {
			    ByteArrayDecoder bad = new ByteArrayDecoder(data);
			    data[DATA_START] += bad.decoder();
			}
		    }

		    data[COMMAND] += 64;
		    data[DIRECTION] = 1;

		    DatagramPacket replyPacket = new DatagramPacket(data, data.length, packet.getAddress(), packet.getPort());
		    socket.send(replyPacket);
		} catch (SocketException ex) {
		    Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
		    Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	};

	Runnable decoderRunnable = () -> {
	    ByteArrayDecoder bad = new ByteArrayDecoder(queue.poll());
	    bad.decoder();
	};

	Thread connection = new Thread(UDPConnection);
	Thread execution = new Thread(decoderRunnable);
	//connection.setDaemon(true);
	connection.start();
	//execution.setDaemon(true);
	if (queue.size() != 0) {
	    execution.start();
	}

    }

}
