/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import java.io.IOException;

/**
 * TODO
 */
public class MinesweeperServerTest {
    
    private static Thread startMinesweeperServer() {

      final String[] args = new String[] {
              "--debug",
              "--port", "4444",
              "--file", "test/minesweeper/server/board_file"
      };
      Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
      serverThread.start();
      return serverThread;
  }
    
}
