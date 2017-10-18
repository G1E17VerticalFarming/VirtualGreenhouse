/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualgreenhouse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import static virtualgreenhouse.IMessage.COMMAND;
import static virtualgreenhouse.IMessage.DIRECTION;
import static virtualgreenhouse.UDPConnection.PORT;

/**
 *
 * @author chris
 */
public class VirtualGreenhouse implements IMessage, ICommands {

    public static final int PORT = 1025;

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws SocketException, IOException {
        LinkedList<byte[]> queue = new LinkedList();

        GreenHouse gh = GreenHouse.getInstance();

        Runnable UDPConnection = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DatagramSocket socket = new DatagramSocket(PORT);
                        byte[] buffer = new byte[110];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // Create packet ready for receiving incoming UDP datagrams
                        System.out.println("Server running on port " + PORT);
                        socket.receive(packet);
                        byte[] data = java.util.Arrays.copyOf(packet.getData(), packet.getLength());

                        queue.addLast(data);

                        data[COMMAND] = COMMAND + 64;
                        data[DIRECTION] = 1;
                        DatagramPacket replyPacket = new DatagramPacket(data, data.length);
                        socket.send(replyPacket);
                    } catch (SocketException ex) {
                        Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };

        Runnable decoderRunnable = new Runnable() {
            @Override
            public void run() {
                ByteArrayDecoder bad = new ByteArrayDecoder(queue.poll());
            }
        };

        Thread connection = new Thread(UDPConnection);
        Thread execution = new Thread(decoderRunnable);
        connection.setDaemon(true);
        connection.start();
        execution.setDaemon(true);
        execution.start();

    }
}
