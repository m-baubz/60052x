/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.junit.Test;

import minesweeper.server.MinesweeperServer;

class ServerStub extends MinesweeperServer {
    public ServerStub(int port, boolean debug) throws IOException {
        super(port, debug);
    }

    @Override
    public void serve(){
        // you've been served
    }
}

public class MinesweeperServerTest {

    
    
}


    

