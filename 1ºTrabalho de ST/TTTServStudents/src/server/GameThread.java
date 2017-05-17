/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
            Grupo VI (António Pereira e Filipe Perestrelo) 18h20
 */
package server;

import game.GameState;
import game.TTTConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Thread that runs all the logic of one game.
 *
 * @author lflb@fct.unl.pt
 */
public class GameThread extends Thread {    // inherits from Thread class

    /**
     * Internal class to store the player's information
     */
    class PlayerInfo {
        InetAddress ip;
        int port;
        int key;
        int faltas=0; // Nº de jogadas que cada jogador faz fora da sua vez
        
        PlayerInfo(InetAddress ip, int port, int key) {
            this.ip= ip;
            this.port= port;
            this.key= key;
        }        
    };

    
    /**
     * UDP thread state - if true is waiting for new packets
     */
    private volatile boolean keepRunning = true;
    /**
     * Reference to the main window object
     */
    private final TTTServer root;
    /**
     * Datagram socket associated to the game
     */
    private DatagramSocket ds;
    /**
     * Auxiliary random number generator
     */
    private Random keygen;
    
    /**
     * Player information
     */
    private final PlayerInfo[] players= new PlayerInfo[2];
        
    /**
     * Counter of the number of players associated to the game
     */
    private int player_count = 0;

    /**
     * The game is active
     */
    private boolean active;
    
   /*Variáveis novas criadas para o trabalho final*/
    
    private int continua= 2; /* Variável que verifica se o envio de pacotes demorou mais de 
                                TTTServer.PLAY_TIMEOUT milisegundos. 
                                Fica com o valor 1 se o jogador demorar 20 segundos a responder, 
                                fica com valor 2 em caso contrário*/
    
    private int jogador=0 ; // Variável que indica qual é o jogador que está na vez de jogar
    
    /*Variáveis novas criadas para o trabalho final*/
    
    public GameState state= new GameState();
    
    /**
     * Constructor; sets player_count to -1 if initialization fails.
     *
     * @param root Main window object
     */
    public GameThread(TTTServer root) {
        this.root = root;
        try {
            ds = new DatagramSocket();
            keygen = new Random();
            active = false;
        } catch (SocketException ex) {
            Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            root.Log("Failed creation of game thread - ending\n");
            player_count = -1;
        }
    }

    /**
     * Return the char with the player name
     *
     * @param player - the player number 0('X') or 1 ('0')
     * @return 'X', '0' or 'E' in case of error
     */
    public char player_name(int player) {
        switch (player) {
            case 0:
                return 'X';
            case 1:
                return '0';
            default:
                return 'E';
        }
    }

    /**
     * Return the char with the player name
     *
     * @param player - the player name 0('X') or 1 ('0')
     * @return 0, 1 or -1 in case of error
     */
    public int player_number(char player) {
        switch (player) {
            case 'X':
                return 0;
            case '0':
                return 1;
            default:
                return -1;
        }
    }

    /**
     * Get the number of players in the game
     *
     * @return the number of players
     */
    public int get_count() {
        return player_count;
    }

    /**
     * Get the port number of the UDP socket associated to the game
     *
     * @return the port number, or -1 if it is not initialized
     */
    public int get_port() {
        if (ds != null) {
            return ds.getLocalPort();
        } else {
            return -1;
        }
    }

    /**
     * Returns the key associated to one player
     *
     * @param player the player number: 0('X') or 1('0').
     * @return the key value, or -1 if it is not valid
     */
    public int get_key(int player) {
        if ((player < 0) || (player >= player_count)) {
            return -1;
        }
        return players[player].key;
    }

    /**
     * Writes the message in the GUI and in the command line.
     *
     * @param str
     */
    public void Log(String str) {
        if (root != null) {
            root.Log("" + get_port() + " " + str);
        } else {
            System.out.print("" + get_port() + " " + str);
        }
    }

    /**
     * Add a player to a game
     *
     * @param _ip IP address of the player
     * @param _port port number of the player
     * @return the number of clients in the game; -1 if error
     */
    public int add_player(InetAddress _ip, int _port) {
        if (player_count < 2) {
            players[player_count]= new PlayerInfo(_ip, _port,  
                    keygen.nextInt(1000000));  // Generates a random key between 0 and 999999
            player_count++;
            return player_count;
        }
        return -1;
    }

    /**
     * Prepare and send the TURN message to one player
     *
     * @param to destination and next player number - 0('X') or 1('0').
     * @return true if successful, false otherwise.
     */
    private boolean send_TURN(int to) {
        jogador=to;
        if (!active) {
            Log("Game not active in send_TURN\n");
            return false;
        }
        if ((to < 0) || (to >= player_count) || (ds == null)
                || (players[to] == null) || (players[to].ip == null) || 
                (players[to].port <= 0)) {
            Log("Invalid parameter in send_TURN\n");
            return false;
        }
        try {
            // Create and send packet to trader server
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            Log("Sent TURN to '"
                    + player_name(to) + "'\n");
            dos.writeShort(TTTConfig.PCKT_TURN);
            dos.writeInt(players[to].key);
            dos.writeChar(player_name(to));
            byte[] buffer = os.toByteArray();       // Convert to byte array
            // Create packet
            DatagramPacket dpr = new DatagramPacket(buffer, buffer.length, 
                    players[to].ip, players[to].port);
            // Send TURN packet
            ds.send(dpr);
            return true;
        } catch (IOException ex) {
            Log("Error sending TURN packet to '" + player_name(to)
                    + "' (" + players[to].ip.getHostAddress() + ":" + 
                    players[to].port + "): " + ex + "\n");
            return false;
        } catch (Exception e2) { // Handle null pointers
            Log("Error sending TURN packet to '" + player_name(to)
                    + "': " + e2 + "\n");
            return false;
        }
    }

    /**
     * Prepare and send PLAYED message to one player
     *
     * @param to destination player number - 0('X') or 1('0').
     * @param p player playing - 0('X') or 1('0').
     * @param row row number (0, 1, or 2).
     * @param col column number (0, 1, or 2).
     * @return true if successful, false otherwise.
     */
    private boolean send_PLAYED(int to, int p, int row, int col) {
        if (!active) {
            Log("Game not active in send_PLAYED\n");
            return false;
        }
        if ((to < 0) || (to >= player_count) || (ds == null)
                || (players[to] == null) || (players[to].ip == null) || 
                (players[to].port <= 0)) {
            Log("Invalid parameter in send_PLAYED\n");
            return false;
        }
        try {
            // Create and send packet to trader server
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            Log("Send_PLAYED() not implemented yet\n");
            Log("Please complete function send_PLAYED in file GameThread.java\n");
            /* ---- TASK 2 ----
            Place here the code to create the message PLAYED
            Take a look at send_TURN and send_END before you start coding!
             */
            dos.writeShort(TTTConfig.PCKT_PLAYED);
            dos.writeInt(players[to].key);
            dos.writeChar(player_name(p));
            dos.writeByte(row);
            dos.writeByte(col);

            byte[] buffer = os.toByteArray();       // Convert to byte array
            // Create packet
            DatagramPacket dpr = new DatagramPacket(buffer, buffer.length, 
                    players[to].ip, players[to].port);
            // Send PLAYED packet
                ds.send(dpr);    
                return true;
        } catch (IOException ex) {
            Log("Error sending PLAYED packet to '" + player_name(to)
                    + "' (" + players[to].ip.getHostAddress() + ":" + 
                    players[to].port + "): " + ex + "\n");
            return false;
        } catch (Exception e2) { // null pointer
            Log("Error sending PLAYED packet to '" + player_name(to)
                    + "': " + e2 + "\n");
            return false;
        }
    }

    /**
     * Prepare and send END message to one player
     *
     * @param to destination player number - 0('X') or 1('0').
     * @param result TTTServer.END_GAME_WON; TTTServer.END_GAME_LOSE or TTTServer.END_GAME_TIE (Tie)
     * @return true if successful, false otherwise.
     */
    private boolean send_END(int to, byte result) {
        if (!active) {
            Log("Game not active in send_END\n");
            return false;
        }
        if ((to < 0) || (to >= player_count) || (ds == null)
                || (players[to] == null) || (players[to].ip == null) 
                || (players[to].port <= 0)
                || !((result == TTTConfig.END_GAME_WON)
                || (result == TTTConfig.END_GAME_LOSE)
                || (result == TTTConfig.END_GAME_TIE))) {
            Log("Invalid parameter in send_END\n");
            return false;
        }
        try {
            // Create and send packet to trader server
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            Log("Sent END(" + TTTConfig.end_result_str(result) + ") to '"
                    + player_name(to) + "'\n");
            dos.writeShort(TTTConfig.PCKT_END);
            dos.writeInt(players[to].key);
            dos.writeByte(result);

            byte[] buffer = os.toByteArray();       // Convert to byte array
            // Create packet
            DatagramPacket dpr = new DatagramPacket(buffer, buffer.length, 
                    players[to].ip, players[to].port);
            // Send query packet
            ds.send(dpr);
            return true;
        } catch (IOException ex) {
            Log("Error sending END packet to '" + player_name(to)
                    + "' (" + players[to].ip.getHostAddress() + ":" + 
                    players[to].port + "): " + ex + "\n");
            return false;
        } catch (Exception e2) { // null pointer
            Log("Error sending END packet to '" + player_name(to)
                    + "': " + e2 + "\n");
            return false;
        }
    }

    
    /**
     * Start the game
     *
     * @return true if successful, false otherwise
     */
    public boolean start_game() {
        if ((ds == null) || (player_count != 2)) {
            return false;
        }
        this.start();       // Start thread
        active = true;      // Game is active
        boolean ok = send_TURN(0/* starts 'X'*/); // Send TURN packet
        /*temporizador();*/
        if (ok) {
            root.GUI_set_state(get_port(), "PLAY X");   // Set state text in graphical interface
        }
        return true;
    }

    /**
     * Stop the game and signals the winner
     *
     * @param winner winner: 0='X' ; 1='0' ; 2=tie
     * @return true if sent END successfully; false otherwise
     */
    public boolean stop_game(char winner) {
        // Send END packet
        switch (winner) {
            case 'X':
                if (!send_END(0, TTTConfig.END_GAME_WON)
                        || !send_END(1, TTTConfig.END_GAME_LOSE)) {
                    return false;
                }
                root.GUI_set_state(get_port(), "Ended - X won");
                Log("game ended - X won\n");
                break;
            case '0':
                if (!send_END(0, TTTConfig.END_GAME_LOSE)
                        || !send_END(1, TTTConfig.END_GAME_WON)) {
                    return false;
                }
                root.GUI_set_state(get_port(), "Ended - 0 won");
                Log("game ended - 0 won\n");
                break;
            case ' ':
                if (!send_END(0, TTTConfig.END_GAME_TIE)
                        || !send_END(1, TTTConfig.END_GAME_TIE)) {
                    return false;
                }
                root.GUI_set_state(get_port(), "Ended - Tie");
                Log("game ended with a tie\n");
                break;
            default:
                Log("Invalid winner in 'stop_game'\n");
        }
        // Stop game objects
        stopRunning();
        return true;
    }

    /**
     * Stop UDP receiving loop by turning off keepRunning.
     */
    public void stopRunning() {
        active = true;
        keepRunning = false;
        // Stop timer
        // ????

        if (this.isAlive()) {
            try {
                this.interrupt();
            } catch (Exception e) {
                System.err.println("Error interrupting game thread: " + e + "\n");
            }
        }
    }

    /**
     * Handles the reception of a PLAY message
     *
     * @param p number of the player
     * @param rplayer name of player
     * @param row row number
     * @param col column number
     */
    private void handle_PLAY(int p, char rplayer, int row, int col) {
        Log("handle_PLAY not implemented yet\n");
        Log("Implement the function handle_PLAY in file GameThread.java");
        /* ---- TASK 3 ----
        This function is responsible for handling the PLAY message
        
        Before doing it, you should think about what variables do you need 
        to control the game, including a "state" and another to define the playing order.
        You should declare and initialize them.
        Only them, implement the game logic, described in section 2.3
          
        Do not forget to validate the plays using a state variable:*/
        if(jogador==p){
            if(state.play(rplayer, row, col)){   
                if(state.check_game_ended()){
                    send_PLAYED(0,p,row,col);
                    send_PLAYED(1,p,row,col);
                    stop_game(state.check_winner());
                }
                else{
                    send_PLAYED(0,p,row,col);
                    send_PLAYED(1,p,row,col);
                    if(p==1){
                        send_TURN(0);
                    }
                    else{
                        send_TURN(1);
                    }
                }
            }
        }
        else{
            players[p].faltas++; // Se não se o jogador que joga não é o jogador a quem foi dada a vez, então incrementa-se o número de faltas do jogador que jogou.
        }
    }

    /**
     * Function run by the thread
     */
    @Override
    public void run() {
        byte[] buf = new byte[TTTConfig.MAX_PLENGTH];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        try {
            while (keepRunning) {
                try {
                    continua=1;
                    ds.setSoTimeout(TTTServer.PLAY_TIMEOUT);// Uma alternativa para o temporizador
                    ds.receive(dp);    // Wait for packets
                    continua=2; 
                    ByteArrayInputStream BAis 
                            = new ByteArrayInputStream(buf, 0, dp.getLength());
                    DataInputStream dis = new DataInputStream(BAis);

                    short type;             // Received packet type
                    int rkey = -1;          // Received key
                    char rplayer = ' ';     // Received player name
                    int p = -1;             // Corresponding player number
                    byte row, col;          // linha e coluna
                    type = dis.readShort(); // Read tTASK 1he packet type
                    if (type != TTTConfig.PCKT_CONNECT) {
                        // All packet types except CONNECT have a key following the type
                        rkey = dis.readInt();   // Read the key
                        if (type != TTTConfig.PCKT_END) {
                            // All packet types except CONNECT and END have a player name after the key
                            rplayer = dis.readChar();   // Read the player name
                            p = player_number(rplayer);
                        }
                    }
                    /*  Use this to Log received packets' type and sender
                     Log("Received packet " + TTTConfig.type_str(type)
                     + " from " + dp.getAddress().getHostAddress()
                     + ":" + dp.getPort() + "\n");
                    */
                   
                    
                    // Validate state
                    if (!active) {
                        Log("Packet " + TTTConfig.type_str(type) + " from "
                                + (p >= 0 ? "'" + rplayer + "'" : "")
                                + " ignored - game not active\n");
                        continue;
                    } 
                    // Handle packets Task 1
                    switch (type) {
                        case TTTConfig.PCKT_PLAY:
                         if((get_key(p)==rkey) && (dp.getAddress().equals(players[p].ip)) && (dp.getPort()== players[p].port)){
                                Log("Function run in file GameThread is incomplete\n");
                                Log("Please complete it when the packet received is a PLAY\n");
                                row= dis.readByte();
                                col= dis.readByte();
                                Log("Received PLAY(" + row + "," + col + ") from '"
                                    + player_name(p) + "'\n");
                                handle_PLAY(p, rplayer, row, col); 
                         
                         }
                         break;
                        case TTTConfig.PCKT_QUIT:
                            Log("Received QUIT from '" + player_name(p) + "'\n");
                                stop_game(player_name(1 - p));
                            break;     
                        // case ...:
                        //    ...
                        // default:
                    }
                    verifica_faltas();
                } catch (SocketException se) {
                    if (keepRunning) {
                        Log("recv UDP SocketException : " + se + "\n");
                    }
                }
                dp.setData(buf);    // Restore pointer to buffer
            }

        } catch (IOException e) {
            if (keepRunning) {
                Log("IO exception receiving data from socket : " + e);
            }
        } finally {
            if(continua==1){
                if(jogador==0){
                    stop_game('0');
                }
                else{
                    stop_game('X');
                }
            }
            if ((ds != null)) {
                // It always signal the end of a game and closes the game socket
                root.game_ended(ds.getLocalPort());
                try { 
                    ds.close();
                } catch (Exception e) {
                        //Ignore
                }
                
            }
        }
    }
    
    public void verifica_faltas(){
        if(players[0].faltas==TTTServer.MAX_FAULTS){
            stop_game('0');
        }
        if(players[1].faltas==TTTServer.MAX_FAULTS){
            stop_game('X');
        }
    }
}



