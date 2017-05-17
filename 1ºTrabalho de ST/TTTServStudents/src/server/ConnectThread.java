/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
            Grupo VI (António Pereira e Filipe Perestrelo) 18h20
 */
package server;

import game.TTTConfig;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Thread that handles the reception of CONNECT packets 
 * 
 * @author lflb@fct.unl.pt
 */
public class ConnectThread extends Thread {    // inherits from Thread class

    volatile boolean keepRunning = true;
    TTTServer root;                        // Main window object
    DatagramSocket ds;                    // datagram socket

    /**
     * Constructor
     * @param root  Main window object
     * @param ds    Datagram socket
     */
    public ConnectThread(TTTServer root, DatagramSocket ds) {
        this.root = root;
        this.ds = ds;
    }

    /**
     *  Function run by the thread
     */
    @Override
    public void run() {
        byte[] buf = new byte[TTTConfig.MAX_PLENGTH];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        try {
            while (keepRunning) {
                try {
                    dp.setData(buf);    // Restore the buffer to buf
                    ds.receive(dp);     // Wait for packets
                    ByteArrayInputStream BAis =
                            new ByteArrayInputStream(buf, 0, dp.getLength());
                    DataInputStream dis = new DataInputStream(BAis);

                    short type= dis.readShort();
                    if (type == TTTConfig.PCKT_CONNECT) {
                        root.Log("Received CONNECT from "+dp.getAddress().getHostAddress()+
                                ":"+dp.getPort()+"\n");
                        // Handle new connection
                        root.handle_CONNECT_packet(dp);   // process packet in TTTServer object
                    } else {
                        root.Log("Received "+TTTConfig.type_str(type)+
                                " packet from "+dp.getAddress().getHostAddress()+
                                ":"+dp.getPort()+" at access port - ignored\n");
                    }
                    
                } catch (SocketException se) {
                    if (keepRunning) {
                        root.Log("recv UDP SocketException : " + se + "\n");
                    }
                }
            }
        } catch (IOException e) {
            if (keepRunning) {
                root.Log("IO exception receiving data from socket : " + e);
            }
        }
    }

    /**
     *  Stops loop by turning off keepRunning
     */
    public void stopRunning() {
        keepRunning = false;
    }
}
