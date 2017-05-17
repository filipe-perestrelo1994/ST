/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
 * Grupo VI (António Pereira e Filipe Perestrelo) 16h20
 */
package protocol;

import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 3 : Stop & Wait protocol - sender & receiver
 *
 * @author ????
 */
public class StopWait extends Base_Protocol implements Callbacks {

    public StopWait(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;
        frame_expected = 0;
        // Initialize object fields
    }

    private boolean send_next_data_packet_duplex() {
        if (packet != null) {
            // The ACK field of the DATA frame is always the sequence number before zero, because no packets will be received
            Frame frame = Frame.new_Data_Frame(next_frame_to_send, decr_seq(frame_expected), packet);
            if (sim.isactive_ack_timer()) {
                sim.cancel_ack_timer();
            }
            sim.to_physical_layer(frame);
            next_frame_to_send = incr_seq(next_frame_to_send);
            return true;
        }
        return false;
    }

    private void send_packet_ack() {
        Frame frame = Frame.new_Ack_Frame(decr_seq(frame_expected));
        sim.to_physical_layer(frame);
    }

    private void handle_data_duplex(Frame frame) {
        if (frame.seq() == frame_expected) {    // Check the sequence number
            net.to_network_layer(frame.info()); // Send the frame to the network layer
            frame_expected = incr_seq(frame_expected);
        }
    }

    private void handle_ack_duplex(Frame frame) {
        if (frame.ack() == decr_seq(next_frame_to_send)) {
            cancel_data_timer();
            packet = net.from_network_layer();
            send_next_data_packet_duplex();
        }
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nStop&Wait Protocol\n\n");
        sim.Log("\nNot implemented yet\n\n");
        packet = net.from_network_layer();
        send_next_data_packet_duplex();
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     *
     * @param time current simulation time
     * @param seq sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.Log("handle_Data_end not implemented\n");
        start_data_timer();
    }

    /**
     * CALLBACK FUNCTION: handle the timer event; retransmit failed frames
     *
     * @param time current simulation time
     * @param key timer key
     */
    @Override
    public void handle_Data_Timer(long time, int key) {
        sim.Log(time + " Data Timeout\n");
        sim.Log("handle_Data_Timer not implemented\n");
        next_frame_to_send = decr_seq(next_frame_to_send);
        send_next_data_packet_duplex();
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log("from_ack_Timer not implemented\n");
        send_packet_ack();
    }

    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical
     * layer
     *
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        sim.Log(time + " Frame received: " + frame.toString() + "\n");
        sim.Log("from_physical_layer not implemented\n");
        if (frame.kind() == Frame.DATA_FRAME) {     // Check the frame kind
            handle_data_duplex(frame); /* Verifica se o número de sequência é o correcto, se não fôr, 
                                           não incrementa frame_expected */
            sim.start_ack_timer();
            handle_ack_duplex(frame); /* Analisa a parte de ACK da frame de dados, enviada em piggybacking*/
        }
        if ((frame.kind() == Frame.ACK_FRAME)) {
            handle_ack_duplex(frame);
        }
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     *
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }

    /* Variables */
    /**
     * Reference to the simulator (Terminal), to get the configuration and send
     * commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol   
    int frame_expected;
    int next_frame_to_send;
    String packet = null;
   //int frame_expected; 
}
