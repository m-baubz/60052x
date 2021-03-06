/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import java.io.*;
import java.net.*;
import java.util.*;

import minesweeper.Board;

/**
 * Multiplayer Minesweeper server.
 */
public class MinesweeperServer {



    /** Default server port. */
    private static final int DEFAULT_PORT = 4444;
    /** Maximum port number as defined by ServerSocket. */
    private static final int MAXIMUM_PORT = 65535;
    /** Default square board size. */
    private static final int DEFAULT_SIZE = 10;

    /** Socket for receiving incoming connections. */
    private final ServerSocket serverSocket;
    
    
    /** Thread safety argument 
     * The following are the variables accessed by multiple threads:
     * debug is written only by main thread at the start of execution. Subsequent reads by threads don't cause thread-safety issues.
     * board is designed as a thread-safe type using monitor pattern.
     * players is only accessed using a dedicated lock playersLock
    */
    private final boolean debug; // True if the server should *not* disconnect a client after a BOOM message.
    private static Board gameBoard;
    private int players = 0; Object playersLock = new Object(); // number of players and its lock

    // TODO: Abstraction function, rep invariant, rep exposure

    /**
     * Make a MinesweeperServer that listens for connections on port.
     * 
     * @param port port number, requires 0 <= port <= 65535
     * @param debug debug mode flag
     * @throws IOException if an error occurs opening the server socket
     */
    public MinesweeperServer(int port, boolean debug) throws IOException {
        serverSocket = new ServerSocket(port);
        this.debug = debug;
    }

    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();
            synchronized (playersLock){ players ++;}
            new Thread(new Runnable() {
                public void run(){    
                    try {                        
                        // handle the client
                        handleConnection(socket);
                        socket.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace(); // but don't terminate serve()                        
                    } finally {
                        synchronized (playersLock){ players --;}
                    }
                }
            }).start();
            
        }
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        int p;
        synchronized (playersLock){ p = players; }
        out.println(String.format("Welcome to Minesweeper. Players: %d including you. Board: %d columns by %d rows. Type 'help' for help.", p, gameBoard.getSizeX(), gameBoard.getSizeY()));
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String output = handleRequest(line);
                if (output.equals("BYE!")){
                    out.println(output);
                    break;
                } else if (output.equals("BOOM!")){
                    out.println(output);
                    if (! debug) {break;}
                } else {
                    Scanner outputScanner = new Scanner(output);
                    while (outputScanner.hasNextLine()){
                        out.println(outputScanner.nextLine());
                    }
                    outputScanner.close();
                }
            }
        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * Handler for client input, performing requested operations and returning an output message.
     * 
     * @param input message from client
     * @return message to client, or null if none
     */
    private String handleRequest(String input) {
        String regex = "(look)|(help)|(bye)|"
                     + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            return "Invalid command. Type 'help' for instructions.";
        }
        String[] tokens = input.split(" ");
        if (tokens[0].equals("look")) {
            char[][] boardView = gameBoard.getView();
            return boardMsg(boardView);
        } else if (tokens[0].equals("help")) {
            return "Read the FM.";
        } else if (tokens[0].equals("bye")) {
            return "BYE!";
        } else {
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            if (tokens[0].equals("dig")) {                
                char[][] boardView = gameBoard.dig(x, y);
                if (boardView[0][0] == 'B'){
                    return "BOOM!";
                } else {
                    return boardMsg(boardView);
                }
            } else if (tokens[0].equals("flag")) {
                char[][] boardView = gameBoard.flag(x, y);            
                return boardMsg(boardView); 
            } else if (tokens[0].equals("deflag")) {
                char[][] boardView = gameBoard.deflag(x, y);            
                return boardMsg(boardView);
            }
        }
        // TODO: Should never get here, make sure to return in each of the cases above
        throw new UnsupportedOperationException();
    }
    /**
     * builds a board message from a char[][] returned from Board object.
     */
    private String boardMsg(char[][] boardView) {
        String boardMsg = "";                    
        for (int r = 0; r < boardView.length; r++){
            for (int c = 0; c < boardView[0].length; c++){
                String space = c < boardView[0].length-1 ? " " : ""; // don't add space to the end of line
                boardMsg += String.valueOf(boardView[r][c]) + space;
            }
            String newline = r < boardView.length-1 ? String.format("%n") : ""; // don't add newline chars at the end of message 
            boardMsg = boardMsg + newline;                
        }
        return boardMsg;        
    }

    /**
     * Start a MinesweeperServer using the given arguments.
     * 
     * <br> Usage:
     *      MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]
     * 
     * <br> The --debug argument means the server should run in debug mode. The server should disconnect a
     *      client after a BOOM message if and only if the --debug flag was NOT given.
     *      Using --no-debug is the same as using no flag at all.
     * <br> E.g. "MinesweeperServer --debug" starts the server in debug mode.
     * 
     * <br> PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the server
     *      should be listening on for incoming connections.
     * <br> E.g. "MinesweeperServer --port 1234" starts the server listening on port 1234.
     * 
     * <br> SIZE_X and SIZE_Y are optional positive integer arguments, specifying that a random board of size
     *      SIZE_X*SIZE_Y should be generated.
     * <br> E.g. "MinesweeperServer --size 42,58" starts the server initialized with a random board of size
     *      42*58.
     * 
     * <br> FILE is an optional argument specifying a file pathname where a board has been stored. If this
     *      argument is given, the stored board should be loaded as the starting board.
     * <br> E.g. "MinesweeperServer --file boardfile.txt" starts the server initialized with the board stored
     *      in boardfile.txt.
     * 
     * <br> The board file format, for use with the "--file" option, is specified by the following grammar:
     * <pre>
     *   FILE ::= BOARD LINE+
     *   BOARD ::= X SPACE Y NEWLINE
     *   LINE ::= (VAL SPACE)* VAL NEWLINE
     *   VAL ::= 0 | 1
     *   X ::= INT
     *   Y ::= INT
     *   SPACE ::= " "
     *   NEWLINE ::= "\n" | "\r" "\n"?
     *   INT ::= [0-9]+
     * </pre>
     * 
     * <br> If neither --file nor --size is given, generate a random board of size 10x10.
     * 
     * <br> Note that --file and --size may not be specified simultaneously.
     * 
     * @param args arguments as described
     */
    public static void main(String[] args) {
        // Command-line argument parsing is provided. Do not change this method.
        boolean debug = false;
        int port = DEFAULT_PORT;
        int sizeX = DEFAULT_SIZE;
        int sizeY = DEFAULT_SIZE;
        Optional<File> file = Optional.empty();

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--debug")) {
                        debug = true;
                    } else if (flag.equals("--no-debug")) {
                        debug = false;
                    } else if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > MAXIMUM_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else if (flag.equals("--size")) {
                        String[] sizes = arguments.remove().split(",");
                        sizeX = Integer.parseInt(sizes[0]);
                        sizeY = Integer.parseInt(sizes[1]);
                        file = Optional.empty();
                    } else if (flag.equals("--file")) {
                        sizeX = -1;
                        sizeY = -1;
                        file = Optional.of(new File(arguments.remove()));
                        if ( ! file.get().isFile()) {
                            throw new IllegalArgumentException("file not found: \"" + file.get() + "\"");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]");
            return;
        }

        try {
            runMinesweeperServer(debug, file, sizeX, sizeY, port);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Start a MinesweeperServer running on the specified port, with either a random new board or a
     * board loaded from a file.
     * 
     * @param debug The server will disconnect a client after a BOOM message if and only if debug is false.
     * @param file If file.isPresent(), start with a board loaded from the specified file,
     *             according to the input file format defined in the documentation for main(..).
     * @param sizeX If (!file.isPresent()), start with a random board with width sizeX
     *              (and require sizeX > 0).
     * @param sizeY If (!file.isPresent()), start with a random board with height sizeY
     *              (and require sizeY > 0).
     * @param port The network port on which the server should listen, requires 0 <= port <= 65535.
     * @throws IOException if a network error occurs
     */
    public static void runMinesweeperServer(boolean debug, Optional<File> file, int sizeX, int sizeY, int port) throws IOException {
        if (file.isPresent()){
            boolean[][] bombsFromFile = parseBoardFromFile(file.get());
            gameBoard = new Board(bombsFromFile);
            
        } else {
            gameBoard = new Board(sizeX, sizeY);            
        }
        
        MinesweeperServer server = new MinesweeperServer(port, debug);
        server.serve();
    }
    
    private static boolean[][] parseBoardFromFile(File file) throws IOException {        
        try(
                BufferedReader fileIn = new BufferedReader(new FileReader(file));
        ) {
            String[] sizes = fileIn.readLine().split(" ");
            int sizeX = Integer.parseInt(sizes[0]);
            int sizeY = Integer.parseInt(sizes[1]);
            boolean[][] bombsFromFile = new boolean[sizeY][sizeX];
            for (int r = 0; r < sizeY; r++){
                Arrays.fill(bombsFromFile[r], false);
                String[] line = fileIn.readLine().split(" ");
                for (int c = 0; c < sizeX; c++){
                    bombsFromFile[r][c] = line[c].equals("1");
                }
            }
            return bombsFromFile;
        }
    }
}
