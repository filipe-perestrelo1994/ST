/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
 */
package protocol;

import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 1 receiver : Utopian protocol that does not retransmit failed frames
 * 
 * @author lflb@fct.unl.pt
 */
public class Utopian_rcv extends Base_Protocol implements Callbacks {

    public Utopian_rcv(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        frame_expected = 0;
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nUtopian Protocol - receiver\n\n");
        // Waits for packets
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, and start 
     * sending the next frame
     * @param time current simulation time
     * @param seq  sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.Log(time + " DATA_END(" + seq + ") not expected\n");
    }

    /**
     * CALLBACK FUNCTION: handle the timer event; ignores it in this case
     * @param time current simulation time
     * @param key  timer key
     */
    @Override
    public void handle_Data_Timer(long time, int key) {
        sim.Log(time + " Timeout " + key + " not expected\n");
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; ignores it in this case
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log(time + " ACK Timeout not expected\n");
    }
    
    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        sim.Log(time + " protocol1 received: " + frame.toString() +"\n");
        if (frame.kind() == Frame.DATA_FRAME) {     // Check the frame kind
            if (frame.seq() == frame_expected) {    // Check the sequence number
                net.to_network_layer(frame.info()); // Send the frame to the network layer
                frame_expected = incr_seq(frame_expected);
            }
        }
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }
    
    
    /* Variables */
    
     /**
     * Reference to the simulator (Terminal), to get the configuration and send commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    
    /**
     * Expected sequence number of the next data frame received
     */
    int frame_expected;
}
