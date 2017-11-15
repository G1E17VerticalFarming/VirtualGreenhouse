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
import java.sql.Connection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class VirtualGreenhouse implements IMessage, ICommands {


    private GreenHouse green = new GreenHouse();
    private static int PORT;
    String fixedServerName;
    private Connection connection;

    public static void main(String[] args) throws SocketException, IOException {
        VirtualGreenhouse vgh = new VirtualGreenhouse();
        LinkedList<byte[]> queue = new LinkedList();

        GreenHouse greenhouse = new GreenHouse().getInstance();
        greenhouse.askForPort();
        PORT = greenhouse.getPort();
        DatagramSocket socket = new DatagramSocket(PORT);


        Runnable UDPConnection = () -> {
            while (true) {
                try {

                    byte[] buffer = new byte[110];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // Create packet ready for receiving incoming UDP datagrams
                    System.out.println("Server running on: " + greenhouse.getIp() + ":" + PORT);
                    socket.receive(packet);
                    byte[] data = java.util.Arrays.copyOf(packet.getData(), packet.getData().length);

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
            try {
                ByteArrayDecoder bad = new ByteArrayDecoder(queue.poll());
                bad.decoder();
            } catch (SocketException ex) {
                Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
            }
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
