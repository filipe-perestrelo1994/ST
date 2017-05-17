/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
            Grupo VI (António Pereira e Filipe Perestrelo) 18h20
 */
package game;

/**
 * Constants used in Tic Tac Toe application
 * 
 * @author lflb@fct.unl.pt
 */
public class TTTConfig {

    /**
     * Maximum Message Length.
     */
    public static final int MAX_PLENGTH = 8096;
    
    /**
     * Packet types.
     */
    public final static short PCKT_CONNECT = 99;
    public final static short PCKT_CONFIG = 98;
    public final static short PCKT_QUIT = 97;
    public final static short PCKT_END = 90;
    public final static short PCKT_PLAY = 20;
    public final static short PCKT_TURN = 10;
    public final static short PCKT_PLAYED = 11;

    /**
     * Game result types.
     */
    public final static byte END_GAME_WON = 1;
    public final static byte END_GAME_LOSE = 2;
    public final static byte END_GAME_TIE = 3; 
    
    
    /**
     * Returns a string with the name of the packet type
     *
     * @param type packet type
     * @return a string with the packet type
     */
    public static String type_str(short type) {
        switch (type) {
            case PCKT_CONNECT:
                return "CONNECT";
            case PCKT_CONFIG:
                return "CONFIG";
            case PCKT_QUIT:
                return "QUIT";
            case PCKT_END:
                return "END";
            case PCKT_PLAY:
                return "PLAY";
            case PCKT_TURN:
                return "TURN";
            case PCKT_PLAYED:
                return "PLAYED";
            default:
                return "INVALID";
        }
    }

    
    /**
     * Returns a string with the result of a game
     * @param result the numeric value of the result
     * @return a string with the result
     */
    public static String end_result_str(int result) {
        switch (result) {
            case END_GAME_WON:
                return "Won";
            case END_GAME_LOSE:
                return "Lose";
            case END_GAME_TIE:
                return "Tie";
            default:
                return "Invalid";
        }
    }    
}
