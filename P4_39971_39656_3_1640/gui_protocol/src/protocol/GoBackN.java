/*
 * Sistemas de Telecomunicações 
 *          2013/2014
 *  Grupo VI (António Pereira e Filipe Perestrelo) 16h20
 */
package protocol;

import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 4 : Go-back-N protocol with one timer
 *
 * @author ????
 */
public class GoBackN extends Base_Protocol implements Callbacks {

    public GoBackN(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        frame_expected = 0;
        next_frame_to_send = 0;
        tamanho_janela = 0; /*Controla o fluxo de emissão de frames, 
                               se fôr igual ao tamanho da janela de transmissão, 
                               impede a emissão de novas frames*/
        ultima_frame = decr_seq(0); // guarda o número de sequência da última frame confirmada
        
        // Initialize object fields
    }

    private boolean send_next_packet(int tipo_transmissao) {
        if (tamanho_janela < sim.get_send_window()) { 
            switch (tipo_transmissao) {
                case 0:
                    packet = net.from_network_layer();
                    pacotes[next_frame_to_send] = packet;
                    break;
                case 1:
                    packet = pacotes[next_frame_to_send];
                    break;
            }
            tamanho_janela++;
            if (packet != null) {
                Frame frame = Frame.new_Data_Frame(next_frame_to_send, decr_seq(frame_expected), packet);
                if (sim.isactive_ack_timer()) {
                    sim.cancel_ack_timer();
                }
                sim.to_physical_layer(frame);
                next_frame_to_send = incr_seq(next_frame_to_send);
                return true;
            }
        }
        return false;
    }

    private void handle_data(Frame frame) {
        if (frame.seq() == frame_expected) {    // Check the sequence number
            net.to_network_layer(frame.info()); // Send the frame to the network layer
            frame_expected = incr_seq(frame_expected);
        }
    }

    private void handle_ack(Frame frame) {
        if(between(incr_seq(ultima_frame), frame.ack(), next_frame_to_send)){
            tamanho_janela--;
            while(between(incr_seq(ultima_frame), frame.ack(), next_frame_to_send)){
                cancel_data_timer();
                pacotes[ultima_frame] = null;
                ultima_frame = incr_seq(ultima_frame);
            }
            send_next_packet(0);
        }
    }

    private void send_packet_ack() {
        Frame frame = Frame.new_Ack_Frame(decr_seq(frame_expected));
        sim.to_physical_layer(frame);
    }

    // handle_emitter// handle_receiver
    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nGo-Back-N Protocol\n\n");
        send_next_packet(0);
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     * and send next until reaching the end of the sending window
     *
     * @param time current simulation time
     * @param seq sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        //sim.Log("handle_Data_end not implemented\n");
        if (!isactive_data_timer()) {
            start_data_timer();
        }
        send_next_packet(0);
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
        next_frame_to_send = incr_seq(ultima_frame);
        tamanho_janela = 0;
        while (send_next_packet(1));
        //mexer com o tamanho janela
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation tim
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log(time + " ACK Timeout\n");
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
        if (frame.kind() == Frame.DATA_FRAME) {
            handle_data(frame);
            sim.start_ack_timer();
            handle_ack(frame);
        }
        if (frame.kind() == Frame.ACK_FRAME) {
            handle_ack(frame);
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

    String pacotes[] = new String[sim.get_max_sequence() + 1];
    int frame_expected, next_frame_to_send;
    int ultima_frame, tamanho_janela; 
    String packet = null;

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
}
