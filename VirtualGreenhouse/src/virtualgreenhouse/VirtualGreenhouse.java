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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualGreenhouse implements IMessage, ICommands {

    private GreenHouse green = new GreenHouse();
    private static int PORT;
    String fixedServerName;
    private Connection connection;

    public static void main(String[] args) throws SocketException, IOException {
        VirtualGreenhouse vgh = new VirtualGreenhouse();
        Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
        Queue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();

        GreenHouse greenhouse = new GreenHouse().getInstance();
        greenhouse.askForPort();
        PORT = greenhouse.getPort();
        DatagramSocket socket = new DatagramSocket(PORT);

        Runnable UDPConnection = () -> {
            while (true) {
                try {
                    byte[] buffer = new byte[110];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    System.out.println("Server running on: " + greenhouse.getIp() + ":" + PORT);
                    socket.receive(packet);
                    byte[] data = java.util.Arrays.copyOf(packet.getData(), packet.getData().length);

                        queue.add(data);
                        packetQueue.add(packet);
                        
                } catch (IOException ex) {
                    Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        Thread connection = new Thread(UDPConnection);
        connection.start();

        while (true) {
                if (!queue.isEmpty()) {
                    System.out.println(queue.isEmpty());
                    try {
                        ByteArrayDecoder bad = new ByteArrayDecoder(queue.poll());
                        byte[] returnData = bad.decoder();
                        DatagramPacket returnPacket = packetQueue.poll();
                        DatagramPacket replyPacket = new DatagramPacket(returnData, returnData.length, returnPacket.getAddress(), returnPacket.getPort());
                        socket.send(replyPacket);
                    } catch (SocketException ex) {
                        Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(VirtualGreenhouse.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

