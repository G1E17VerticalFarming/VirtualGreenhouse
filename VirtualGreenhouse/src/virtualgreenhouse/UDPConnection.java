/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package virtualgreenhouse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 *
 * @author mads
 */
public class UDPConnection { 
   
    public static final int PORT = 3333;
    
    public UDPConnection() {
        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[110];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // Create packet ready for receiving incoming UDP datagrams
        System.out.println("Server running on port " + PORT);
        socket.receive(packet);
        byte[] data = java.util.Arrays.copyOf(packet.getData(), packet.getLength());
    }
}
