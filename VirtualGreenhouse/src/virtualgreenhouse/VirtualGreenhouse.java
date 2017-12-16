package virtualgreenhouse;

import interfaces.ICommands;
import interfaces.IMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualGreenhouse implements IMessage, ICommands {

    private static int PORT;

    public static void main(String[] args) throws IOException {
        Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
        Queue<DatagramPacket> packetQueue = new ConcurrentLinkedQueue<>();

        GreenHouse greenhouse = new GreenHouse().getInstance();
        greenhouse.askForPort();
        PORT = greenhouse.getPort();
        DatagramSocket socket = new DatagramSocket(PORT);

        /**
         * Runnable UDPConnection in the the main of VirtualGreenhouse.java:
         * This thread starts a DatagramPacket, which receives packages from Scada.
         * The received packet is added to a queue
         * The accompanying packet-information to that packet is saved in another queue.
         */
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


        /**
         * While loop for calling methods in the ByteArrayDecoder
         * Inside this loop the if statement checks if the queue mentioned in the UDPConnection is empty.
         * If the queue is not empty a new instance of the ByteArrayDecoder is started with the accompanying poll from the queue.
         * Once the decoder returns a new package, this is sent back to Scada with socket.send
         */
        while (true) {
            if (!queue.isEmpty()) {
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

