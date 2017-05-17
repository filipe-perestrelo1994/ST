/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
            Grupo VI (António Pereira e Filipe Perestrelo) 18h20
 */
package server;

import game.TTTConfig;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tic Tac Toe server main class.
 *
 * @author lflb@fct.unl.pt
 */
public class TTTServer extends javax.swing.JFrame {
    

    /**
     * User play timeout.
     */
    public final static int PLAY_TIMEOUT = 20000;
    /**
     * Maximum number of invalid plays before excluding a player.
     */
    public final static int MAX_FAULTS = 3;


/////////////////////////////////////////////////////////////////////////
//      Graphical functions
/////////////////////////////////////////////////////////////////////////
    /**
     * The number of the last line used in the game's table.
     */
    private int last_Line = 0;
    
    // Variáveis criadas para a tarefa 6
    
    private HashMap Jogos= new HashMap(); // HashMap que vai guardar todos os jogos.
    
    
    // Variáveis criadas para a tarefa 6

    /**
     * Returns the line of game with port 'port' in the game's table
     *
     * @param port the port number of the UDP socket of a game
     * @return the line number in the game's table
     */
    public int GUI_find_game(int port) {
        String ports = Integer.toString(port);
        for (int i = 0; i < last_Line; i++) {
            String p = (String) jTable1.getValueAt(i, 0);
            if (p == null) {
                return -1;
            }
            if (p.equals(ports)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds a new game to the game's table with the 'X' player
     *
     * @param port the port number of the UDP socket of a game
     * @param playerX a string that identifies the player 'X'
     * @param status a string that specifies the status of the game
     * @return true in case of success; false otherwise
     */
    public boolean GUI_add_new_game(int port, String playerX, String status) {
        try {
            jTable1.setValueAt("" + port, last_Line, 0);
            jTable1.setValueAt(playerX, last_Line, 1);
            jTable1.setValueAt(status, last_Line, 3);
            last_Line++;
            return true;
        } catch (Exception e) {
            Log("Error in GUI_add_new_game: " + e + "\n");
            return false;
        }
    }

    /**
     * Adds the player '0' to a game in the game's table
     *
     * @param port the port number of the UDP socket of a game
     * @param player0 a string that identifies the player '0'
     * @return true in case of success; false otherwise
     */
    public boolean GUI_add_player_0(int port, String player0) {
        try {
            int n = GUI_find_game(port);
            if (n != -1) {
                jTable1.setValueAt(player0, n, 2);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log("Error in GUI_add_player_0: " + e + "\n");
            return false;
        }
    }

    /**
     * Update the state of a game in the game's table
     *
     * @param port the port number of the UDP socket of a game
     * @param status a string that specifies the status of the game
     * @return true in case of success; false otherwise
     */
    public boolean GUI_set_state(int port, String status) {
        try {
            int n = GUI_find_game(port);
            if (n != -1) {
                jTable1.setValueAt(status, n, 3);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log("Error in GUI_set_status: " + e + "\n");
            return false;
        }
    }

    /**
     * Deletes a game from the game's table
     *
     * @param port the port number of the UDP socket of a game
     * @return true in case of success; false otherwise
     */
    public boolean GUI_del_game(int port) {
        try {
            int n = GUI_find_game(port);
            if (n != -1) {
                if (n < last_Line - 1) {
                    for (int x = n; x < last_Line; x++) {
                        for (int r = 0; r < 4; r++) {
                            jTable1.setValueAt(jTable1.getValueAt(x + 1, r), x, r);
                        }
                    }
                }
                for (int i = 0; i < 4; i++) {
                    jTable1.setValueAt("", last_Line - 1, i);
                }
                last_Line--;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log("Error in GUIdel_game: " + e + "\n");
            return false;
        }
    }

    /**
     * Delete all games from the table
     */
    public void del_all_games() {
        for (int x = 0; x < jTable1.getRowCount(); x++) {
            for (int r = 0; r < 4; r++) {
                jTable1.setValueAt("", x, r);
            }
        }
        last_Line = 0;
    }

/////////////////////////////////////////////////////////////////////////
//      Server logic functions
/////////////////////////////////////////////////////////////////////////
    /**
     * Creates new form Server
     */
    public TTTServer() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextIP = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextPort = new javax.swing.JTextField();
        jButtonClear = new javax.swing.JButton();
        jToggleButtonActive = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tic Tac Toe S by 11111,22222,33333");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setMinimumSize(new java.awt.Dimension(267, 29));

        jLabel3.setText("IP ");
        jPanel1.add(jLabel3);

        jTextIP.setText("127.0.0.1");
        jTextIP.setPreferredSize(new java.awt.Dimension(120, 27));
        jPanel1.add(jTextIP);

        jLabel2.setText(" Port");
        jPanel1.add(jLabel2);

        jTextPort.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextPort.setText("20000");
        jTextPort.setToolTipText("");
        jPanel1.add(jTextPort);

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonClear);

        jToggleButtonActive.setText("Active");
        jToggleButtonActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonActiveActionPerformed(evt);
            }
        });
        jPanel1.add(jToggleButtonActive);

        getContentPane().add(jPanel1);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 162));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Game", "Player 1", "Player 2", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1);

        jScrollPane3.setPreferredSize(new java.awt.Dimension(279, 150));

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(10);
        jTextArea1.setToolTipText("");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setMinimumSize(new java.awt.Dimension(50, 57));
        jTextArea1.setPreferredSize(new java.awt.Dimension(260, 10000));
        jScrollPane3.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane3);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Handle 'Active' button
     *
     * @param evt button event - not used
     */
    private void jToggleButtonActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonActiveActionPerformed
        if (jToggleButtonActive.isSelected()) {    // The button is ON                                                   
            int port;
            try {   // Read the port number in Local Port text field
                port = Integer.parseInt(jTextPort.getText());
            } catch (NumberFormatException e) {
                Log("Invalid local port number: " + e + "\n");
                jToggleButtonActive.setSelected(false);     // Set the button off
                return;
            }
            try {
                sock = new DatagramSocket(port);    // Create UDP socket
                jTextPort.setText("" + sock.getLocalPort());
                serv = new ConnectThread(this, sock); // Create the receiver thread
                serv.start();                       // Start the receiver thread
                // Start a unique game
                new_game= new GameThread(this);
                if (new_game.get_port() == -1) {
                    reset_all();
                    return;
                }
                Log("Created game at port " + new_game.get_port() + "\n");
                
                // ---- TASK ----
                // You must modify this code, to start new games when new players connect
                Log("Server active\n");
                Log("Simplified server - only runs one game\n");
                Log("You will complete TTTServer during this lab work\n");
            } catch (SocketException e) {
                Log("Socket creation failure: " + e + "\n");
                jToggleButtonActive.setSelected(false);     // Set the button off
            }
            InetAddress ip;
            try {
                ip = InetAddress.getLocalHost();
                jTextIP.setText(ip.getHostAddress());
            } catch (UnknownHostException ex) {
                Log("Unknown local IP address");
                Logger.getLogger(TTTServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {    // The button is OFF
            reset_all();
            elimina_jogadores(); // Função que elimina todos os jogadores quando o servidor fica desactivado.
            Log("Server stopped\n");
        }
    }//GEN-LAST:event_jToggleButtonActiveActionPerformed

    /**
     * Handle 'Clear' button
     *
     * @param evt button event - not used
     */
    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        reset_all();
        System.out.println("Window closed");
    }//GEN-LAST:event_formWindowClosing

    /**
     * Sends a CONFIG packet to the destination defined in 'dp'
     *
     * @param dp datagram packet with the destination's IP and port
     * @param port local game's port
     * @param player player role: 'X' or '0'
     * @param key a secret key shared by the game and the player
     * @return true in case of success; false otherwise
     */
    private boolean send_CONFIG(DatagramPacket dp, int port, char player, int key) {
        try {
            // Create and send packet to one player
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            dos.writeShort(TTTConfig.PCKT_CONFIG);
            dos.writeInt(key);
            dos.writeChar(player);
            dos.writeInt(port);

            byte[] buffer = os.toByteArray();       // Convert to byte array
            dp.setData(buffer);                     // Reuse address
            // Send configuration packet
            sock.send(dp);

            Log("Sent CONFIG('" + player + "' port=" + port + " key=" + key
                    + ") to " + dp.getAddress().getHostAddress() + ":"
                    + dp.getPort() + "\n");
            return true;
        } catch (IOException ex) {
            Log("Error sending configuration packet to player(" + dp.getAddress().getHostAddress() + ":" + dp.getPort() + "): " + ex + "\n");
            return false;
        } catch (Exception e2) {    // Catch null pointers
            Log("Error sending packet to player: " + e2 + "\n");
            return false;
        }
    }
    
    private void elimina_jogadores(){
        GameThread jogo;
        while(Jogos.values().iterator().hasNext()){
            jogo=((GameThread)(Jogos.values().iterator().next()));
            jogo.stop_game(' ');
            Jogos.remove((Object)(jogo.get_port()));
        }
    }
   
    
    /**
     * Handle the processing of a CONNECT packet: Starts new game threads.
     *
     * @param dp the packet received
     */
    public void handle_CONNECT_packet(DatagramPacket dp) {
        try { 
            // ---- TASK 6 ----
            // Add a new player to the game thread.
            // It should get the number of players (new_game.get_count()) and take
            //      intelligent decisions. But this is just a dummy version!
            int cnt = new_game.add_player(dp.getAddress(), dp.getPort());
            if ((cnt >= 1) && (cnt <= 2)) {
                // Send the configuration to client
                boolean ok = send_CONFIG(dp, new_game.get_port(), cnt - 1 == 0 ? 'X' : '0',
                        new_game.get_key(cnt - 1));
                if (!ok) {
                    // Failed to send the CONFIG message
                    reset_all();
                    return;
                }
                if (cnt == 1) {
                    // Add game to the graphical interface, with player 'X' information
                    GUI_add_new_game(new_game.get_port(),
                            dp.getAddress().getHostAddress() + ":" + dp.getPort(),
                            "WAIT CONNECT '0'");
                } else {
                    // Add player '0' to the graphical interface
                    GUI_add_player_0(new_game.get_port(),
                            dp.getAddress().getHostAddress() + ":" + dp.getPort());
                    // start the new game
                    // Notice that this version only runs once.
                    //       you should correct this limitation!
                    //System.out.println("Olha aqui está o n_jogos: " + n_jogos);
                    ok = new_game.start_game();
                    if (!ok) {
                        reset_all();
                    }
                }

            } else {
                Log("The server already handled 2 players\n");
                Log("Please modify handle_CONNECT_packet at TTTServer.java\n to support more players\n");
            }
            if(new_game.get_count()==2){
               Jogos.put((new_game.get_port()), new_game); // Coloca o novo jogador cuja chave será o respectivo porto
               new_game= new GameThread(serv.root);
               if (new_game.get_port() == -1) {
                    reset_all();
                    return;
                }
               Log("Created game at port " + new_game.get_port() + "\n");
            }

        } catch (Exception ex) {
            Log("Error in handle_CONNECT_packet: " + ex + "\n");
        }
    }

    /**
     * Handle the termination of a game
     *
     * @param localPort port number of the game's UDP socket
     */
    void game_ended(int localPort) {
        // Remove from table in graphical interface
        this.GUI_del_game(localPort);
        Jogos.remove((Object)localPort); // Remove o jogador com o porto correspondente ao porto local (localPort)
        // Remove the game from everywhere else
        // ????
    }

    /**
     * Stops everything and resets the server state to the initial state.
     */
    public void reset_all(){
        del_all_games();
        
        // Stop application        
        if (serv != null) {         // If thread is running
            serv.stopRunning();     // Stop the server thread
            serv = null;    // Thread will be garbadge collected after it stops
        }
        if (sock != null) {     // If socket is active
            sock.close();       // Close the socket
            sock = null;    // Forces garbadge collecting
        }
        // Set off the Active button
        jToggleButtonActive.setSelected(false);
    }

    /**
     * Output a message to the "Remote" text area
     *
     * @param s text
     */
    public synchronized void Log(String s) {
        try {
            System.out.print(s);  // Write to the terminal
            jTextArea1.append(s); // Write to the Remote text area
        } catch (Exception e) {
            System.err.println("Error in Log: " + e + "\n");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TTTServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TTTServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TTTServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TTTServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TTTServer().setVisible(true);
            }
        });
    }

    // Variables declaration
    private DatagramSocket sock;     // datagram socket
    private ConnectThread serv;      // thread of access server
    // Game thread - only one is supported in this version!
   private GameThread new_game;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextIP;
    private javax.swing.JTextField jTextPort;
    private javax.swing.JToggleButton jToggleButtonActive;
    // End of variables declaration//GEN-END:variables
}
