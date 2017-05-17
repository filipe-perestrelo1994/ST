/*
 * Sistemas de Telecomunicacoes 
 *          2013/2014
            Grupo VI (António Pereira e Filipe Perestrelo) 18h20
 */
package game;

/**
 * Memorizes the state of a game, and detects the winning conditions
 * 
 * @author lflb@fct.unl.pt
 */
public class GameState {
    /**
     * The state of the game board
     */
    private final char[][] state = {{' ', ' ', ' '}, {' ', ' ', ' '}, {' ', ' ', ' '}};
    /**
     * The name of the winning player: 'X', '0', or ' ' if it is a tie.    
     */
    private char winner= ' ';
    /**
     * Number of 'X' in the board.
     */
    private int countX;
    /**
     * Number of '0' in the board.
     */
    private int count0;

    /**
     * Constructor
     */
    public GameState() {
        countX = count0 = 0;
    }

    /**
     * Regist a play from one player in the game state
     * @param player  name of the player.
     * @param row   row number: 0, 1 or 2.
     * @param col   column number: 0, 1 or 2.
     * @return true if it is a valid play, false otherwise.
     */
    public boolean play(char player, int row, int col) {
        if ((player != 'X') && (player != '0')) {
            return false;
        }
        if (check_game_ended()) {
            return false;
        }
        if (state[row][col] == ' ') {
            state[row][col] = player;
            if (player == 'X') {
                countX++;
            } else {
                count0++;
            }
            return true;
        } else {
            System.err.println("Invalid move for '" + player + "' - position already taken\n");
            return false;
        }
    }

    /**
     * Return the player who played in a position of the board 
     * @param row   row number: 0, 1 or 2.
     * @param col   column number: 0, 1 or 2.
     * @return 'X', '0' or ' ' if no one played in the position 
     */
    public char get(int row, int col) {
        return state[row][col];
    }

    /**
     * Test the board to detect any invalid position
     * @return true if valid, false otherwise.
     */
    public boolean check_valid_game() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                switch (state[i][j]) {
                    case 'X':
                        break;
                    case '0':
                        break;
                    case ' ':
                        break;
                    default:
                        System.err.println("Invalid value in (" + i + "," + j + "): '" + state[i][j] + "'");
                        return false;
                }
            }
        }
        return ((countX == count0) || (countX - 1 == count0));
    }

    /**
     * Detect if there is a winner of the game
     * @return the winner name ('X' or '0'), or ' ' if it is a tie, or the game hasn't ended.
     */
    public char check_winner() {
        if (!check_valid_game()) {
            return ' ';
        }
        if (winner != ' ') {
            return winner;
        }

        // X__
        // _X_
        // __X
        if ((state[0][0] != ' ') && (state[0][0] == state[1][1])
                && (state[2][2] == state[1][1])) {
            return (winner = state[0][0]);
        }
        // __X
        // _X_
        // X__
        if ((state[1][1] != ' ') && (state[0][2] == state[1][1])
                && (state[2][0] == state[1][1])) {
            return (winner = state[1][1]);
        }
        // XXX    ___    ___
        // ___    XXX    ___
        // ___    ___    XXX
        for (int i = 0; i < 3; i++) {
            if ((state[i][0] != ' ') && (state[i][0] == state[i][1])
                    && (state[i][2] == state[i][1])) {
                return (winner = state[i][0]);
            }
        }
        // X__    _X_    __X
        // X__    _X_    __X
        // X__    _X_    __X
        for (int i = 0; i < 3; i++) {
            if ((state[0][i] != ' ') && (state[0][i] == state[1][i])
                    && (state[2][i] == state[1][i])) {
                return (winner = state[0][i]);
            }
        }
        // No winner
        return ' ';
    }

    /**
     * Test if the game has ended
     * @return true if it ended, false otherwise.
     */
    public boolean check_game_ended() {
        if (check_winner() != ' ') {
            return true;
        }
        return (countX + count0 == 9);
    }
}
