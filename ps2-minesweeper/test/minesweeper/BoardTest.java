/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import minesweeper.Board;

/**
 * TODO: Description
 */
public class BoardTest {
    
    // TODO: Testing strategy
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // TODO: Tests
    
    @Test
    public void testConstructorWithSize(){
        int rows = 5, cols = 5, booms = 0;
        
        Board board  = new Board(rows, cols);
        
        for (int r = 0; r < rows; r++){
            for (int c = 0; c < cols; c ++){
                if (board.dig(r, c)[0][0] == 'B') { booms++;}
            }
        }
        assertTrue("expected >0 random bombs", booms > 0);        
    }
    
    @Test
    public void testConstructorFromFile(){
        boolean[][] bombsFromFile = new boolean[][]{{false, false, false},
                                                    {false, false, false},
                                                    {false, false, true}};
        Board board = new Board(bombsFromFile);
        
        assertTrue("expexted board size 3x3", board.getSizeX() == 3 && board.getSizeY() == 3);
        assertArrayEquals(board.dig(0, 0), new char[][]{{' ', ' ', ' ',},{' ', '1', '1'}, {' ', '1', '-'}});
    }
    
    @Test
    public void testBoom(){
        boolean[][] bombsFromFile = new boolean[][]{{false, false, false},
                                                    {false, false, false},
                                                    {false, false, true}};
        Board board = new Board(bombsFromFile);
        
        assertArrayEquals(board.dig(2, 2), new char[][]{{'B'}});
        assertArrayEquals(board.getView(), new char[][]{{' ', ' ', ' ',},{' ', ' ', ' '}, {' ', ' ', ' '}});
    }
    
    @Test
    public void testFlagDig(){
        boolean[][] bombsFromFile = new boolean[][]{{false, false, false},
                                                    {false, false, false},
                                                    {false, false, true}};
        Board board = new Board(bombsFromFile);
        
        assertArrayEquals(board.flag(2, 2), board.dig(2, 2));
   }
    
}
