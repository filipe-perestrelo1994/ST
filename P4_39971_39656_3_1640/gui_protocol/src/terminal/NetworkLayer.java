/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
 */
package terminal;

import simulator.Event;

/**
 * Implements a saturated network layer protocol that keeps sending packets to
 * the data link link, until reaching the number of packets specified
 * 
 * @author lflb@fct.unl.pt
 */
public class NetworkLayer {
    
    /**
     * Constructer
     * @param _root reference to the main window
     */
    NetworkLayer(Terminal _root) {
        this.root= _root;
        this.cnt= 0;
        this.expected= 1;
        this.enabled= true;
    }
    
    /**
     * Called by the data link layer to get the next string to send
     * @return string with the next message
     */
    public String from_network_layer() {
        if (cnt < root.get_packets()) {
            ++cnt;
            String msg= Integer.toString(cnt);
            root.count_statistics(Event.STAT_PAYLOADS_TX);
            root.Log("Network " + root.get_name() + " sent packet: \"" + msg + "\"\n");
            return msg;
        } else {
            return null;
        }
    }
    
    /**
     * Called by the data link layer to deliver the received data in order
     * @param packet the packet received
     * @return true if it was received successfuly, false otherwise
     */
    public boolean to_network_layer(String packet) {
        if (!enabled) {
            return false;
        }
        root.Log("Network " + root.get_name() + " received packet: \"" + packet + "\"\n");
        root.count_statistics(Event.STAT_PAYLOADS_RX);
        // Validate packet
        try {
            int n= Integer.parseInt(packet);
            if (n != expected) {
                root.Log("\tnetwork received messages out of order\n");
                root.count_statistics(Event.STAT_PAYLOADS_RX_INVALID);
            } else {
                expected++;
            }
        } catch(NumberFormatException e) {
            root.Log("\tnetwork received invalid message\n");
            root.count_statistics(Event.STAT_PAYLOADS_RX_INVALID);
        }
        return true;
    }
    
    
    /**
     * Reference to the main window
     */
    private Terminal root;
    /**
     * Not used, yet. It will be used to turn on/off the network layer
     */
    private boolean enabled;
    /**
     * Count with the packets sent; the messages are just the number of the packet
     */
    private int cnt;
    /**
     * Expected next packet
     */
    private int expected;
}
