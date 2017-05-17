/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
 *   Grupo VI (António Pereira e Filipe Perestrelo) 16h20
 */
package protocol;

import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 2 : Simplex protocol - sender; does not receive data
 *
 * @author ????
 */
public class Simplex_snd extends Base_Protocol implements Callbacks {

    public Simplex_snd(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;
    }

    /**
     * Fetches the network layer for the next packet and starts it transmission
     *
     * @return true is started data frame transmission, false otherwise
     */
    private boolean send_next_data_packet_simplex() {
        if (packet != null) {
            // The ACK field of the DATA frame is always the sequence number before zero, because no packets will be received
            Frame frame = Frame.new_Data_Frame(next_frame_to_send, decr_seq(0), packet);
            sim.to_physical_layer(frame);
            next_frame_to_send = incr_seq(next_frame_to_send);
            return true;
        }
        return false;
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nSimplex Sender Protocol\n\n");
        sim.Log("\nNot implemented yet\n\n");
        //pa
        send_next_data_packet_simplex();
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
        sim.Log(time + " Data Timeout\n"); // Ignore key
        sim.Log("handle_Data_Timer not implemented\n");
        next_frame_to_send = decr_seq(next_frame_to_send); /*Vai decrementar, de modo a enviar de novo a frame não confirmada, 
                                                             uma vez que o data timer disparou*/
        send_next_data_packet_simplex(); // Envia a frame
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log(time + " ACK Timeout\n");
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
        if (frame.kind() == Frame.ACK_FRAME) {
            if (frame.ack() == decr_seq(next_frame_to_send)) {
                cancel_data_timer();
                // net.to_network_layer(frame.info());
                //packet = net....
               packet = net.from_network_layer(); /* Coloca na variável packet o valor do pacote 
                                                   presente na camada de network */
                send_next_data_packet_simplex(); // Envia a próxima frame de dados

            }
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
    /**
     * Sequence number of the next data frame
     */
    int next_frame_to_send;
    String packet = net.from_network_layer();
    //criar string = null
}
