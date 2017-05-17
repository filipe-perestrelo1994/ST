/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
 */
package protocol;

import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Base protocol class that defines support functions for a basic timer interface,
 *     manipulating sequence numbers and default event handlers
 * 
 * @author lflb@fct.unl.pt
 */
public class Base_Protocol implements Callbacks {

    /**
     * Generic constructor for a protocol
     * @param _sim  simulator object
     * @param _net  network layer object
     */
    public Base_Protocol(Simulator _sim, NetworkLayer _net) {
        this.sim = _sim;
        this.net = _net;
    }

    
/******************************************************************************/
// Code to manage sequence numbers
/******************************************************************************/
    
    /**
     * Return true if a <= b < c circularly; false otherwise
     * @param a - lower bound
     * @param b - test value
     * @param c - upper bound
     * @return true is b is between a and c
     */
    static boolean between(int a, int b, int c) {
        return ((a<=b)&&(b<c)) || ((c<a)&&(a<=b)) || ((b<c)&&(c<a));
    }

    /**
     * Increment one unit a sequence number wrapping when the maximum sequence 
     * number is reached
     * @param n sequence number
     * @return the successor of the sequence number
     */
    final int incr_seq(int n) {
        return (n + 1) % (sim.get_max_sequence() + 1);
    }

    /**
     * Increment k units to a sequence number wrapping when the maximum sequence 
     *    number is reached
     * @param n sequence number
     * @param k increment
     * @return the successor of the sequence number
     */
    final int add_seq(int n, int k) {
        return (n + k) % (sim.get_max_sequence() + 1);
    }

    /**
     * Decrement one unit a sequence number wrapping when the maximum sequence 
     * number is reached
     * @param n sequence number
     * @return the predecessor of the sequence number
     */
    final int decr_seq(int n) {
        return (sim.get_max_sequence() + n) % (sim.get_max_sequence() + 1);
    }

    /**
     * Calculates the difference between two sequence numbers taking into 
     * account the wrapping when the maximum sequence number is reached
     * @param a first sequence number
     * @param b first sequence number
     * @return number of numbers between a and b
     */
    int diff_seq(int a, int b) {
        if (b>=a) {
            return b-a;
        }
        else {
            return b+sim.get_max_sequence()+1-a;
        }     
    }

    
/******************************************************************************/
// Code to support a simplified timer interface
/******************************************************************************/
    /**
     * Timer key constant
     */
    private final int TIMER_KEY= 1;
    
    /**
     * Start or restart the timer
     */
    void start_data_timer() {
        sim.start_data_timer(TIMER_KEY);
    }
    
    /**
     * Stop the data timer
     */
    void cancel_data_timer() {
        sim.cancel_data_timer(TIMER_KEY);
    }
    
    /**
     * Test if data timer is running
     */
    boolean isactive_data_timer() {
        return sim.isactive_data_timer(TIMER_KEY);
    }

    
/******************************************************************************/
// Code that defines default event handlers
/******************************************************************************/
        
    /**
     * Default implementation for start_simulation event
     * @param time  current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log(time+" Base_Protocol.start_simulation() ignored\n");
    }

    /**
     * Default implementation for handle_Data_end event
     * @param time  current simulation time
     * @param seq   data sequence number
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.Log(time+" Base_Protocol.Data_end("+seq+") ignored\n");
    }

    /**
     * Default implementation for handle_Data_Timer event
     * @param time  current simulation time
     * @param key timer key
     */
    @Override
    public void handle_Data_Timer(long time, int key) {
        sim.Log(time+" Base_Protocol.data_Timeout("+key+") ignored\n");
    }

    /**
     * Default implementation for handle_ack_Timer event
     * @param time  current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log(time+" Base_Protocol.ack_Timer ignored\n");
    }

    /**
     * Default implementation for from_physical_layer event
     * @param time  current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        sim.Log(time+" Base_Protocol.from_physical_layer("+frame+") ignored\n");
    }

    /**
     * Default implementation for end_simulation event
     * @param time  current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log(time+" Base_Protocol.end_simulation ignored\n");
    }
    

    
    /* Variables */
    
    /**
     * Reference to the simulator (Terminal), to get the configuration and send commands
     */
    final Simulator sim;
    
    /**
     * Reference to the network layer, to send a receive packets
     */
    final NetworkLayer net;
    
}
