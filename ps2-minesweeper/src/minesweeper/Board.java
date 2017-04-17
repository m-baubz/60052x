/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.util.Arrays;

/**
 * Game board for multiplayer minesweeper
 */
public class Board {
    
    // Abstraction function, rep invariant:
    // board is represented by a boolean[][] bombs of size rows*cols, where bombs[r, c] is true if that cell has a bomb,
    // and a char[][] gameView of same size, where the value of each cell can be:
    //      '-'     for untouched cell,
    //      [1-8]   for cell that has 1 to 8 bombs adjacent to it,
    //      ' '     for cell that has no bobms adjacent,
    //      'F'     for flagged cell.
    // Rep exposure:
    //      the variables that hold board state are private and final;
    //      observer and constructor use defensive copies of mutable returns/params;
    // Thread safety:
    //      Board utilizes monitor pattern. 
    
    private static final double BOMB_DENSITY = 0.15; // probability that a cell has a bomb
    
    private final int rows, cols;
    private final boolean[][] bombs;
    private final char[][] boardView; 
    
    /**
     * creates a Board and fills with bombs with a probability BOMB_DENSITY 
     * @param sizeX - horizontal size (cols)
     * @param sizeY - vertical size (rows)
     */
    public Board(int sizeX, int sizeY){
        rows = sizeY;
        cols = sizeX;
        bombs = new boolean[rows][cols];
        
        // populate bombs with bombs :)
        for (int r=0; r<rows; r++){
            for (int c=0; c<cols; c++){
                bombs[r][c] = Math.random() < BOMB_DENSITY;
            }
        }
        
        // fill boardView with '-'
        boardView = new char[rows][cols];
        for (char[] row : boardView){
            Arrays.fill(row, '-');
        }
        checkRep();
    }
    
    /**
     * creates a Board and fills it with bombs from file
     * @param bombsFromFile - a boolean[][] where bombsFromFile[y][x] is true if that cell has a bomb.
     *                        each row must be of equal length
     */
    public Board(boolean[][] bombsFromFile){
        rows = bombsFromFile.length;
        cols = bombsFromFile[0].length;
        bombs = new boolean[rows][cols];
        
        // copy bombs from file
        for (int r=0; r<rows; r++){
            System.arraycopy(bombsFromFile[r], 0, bombs[r], 0, cols);
        }
        
        // fill boardView with '-'
        boardView = new char[rows][cols];
        for (char[] row : boardView){
            Arrays.fill(row, '-');
        }
        checkRep();
    }
    
    /**
     * Observer method that returns a copy of boardView
     */
    public synchronized char[][] getView(){
        char[][] result = new char[rows][cols];
        for (int r=0; r<rows; r++){
            System.arraycopy(boardView[r], 0, result[r], 0, cols);
        }
        return result;
    }
    
    /**
     * Mutator method that performs a dig command
     * @param x - horizontal coordinate (col) of cell to dig
     * @param y - vertical coordinate (row) of cell to dig
     * 
     * side effects:
     *      if bombs[y][x] is true, sets it to false and updates boardView by calling revealAdjacent(y, x);
     *      if bombs[y][x] has adjacent bombs, sets boardView[y][x] to char corresponding to the number of bombs;
     *      if bombs[y][x] has no adjacent bombs, stes boardView[y][x] to ' ' and calls revealAdjacent(y, x).     *      
     * 
     * @return special value [['B']] if bombs[y][x] is true, else copy of boardView via getView() 
     */
    public synchronized char[][] dig(int x, int y){
        if (boardView[y][x] == '-'){            
            if (bombs[y][x]){
                bombs[y][x] = false;
                int b = countBombs(y, x);
                boardView[y][x] = (b == 0) ? ' ' : (char) (b + '0'); // '0' is ascii 48; (char) 3 + 48 -> '3' 
                revealAdjacent(y, x);
                checkRep();
                return new char[][]{{'B'}};
            }else{
                int b = countBombs(y, x);
                if (b == 0){
                    boardView[y][x] = ' ';
                    revealAdjacent(y, x);
                } else {
                    boardView[y][x] = (char) (b + '0');
                }
                checkRep();
                return getView();
            }
        }
        checkRep();
        return getView();
    }

    
    /**
     * Helper/mutator that recursively reveals neighbours of boardView[row][col].
     * If boardView[row][col] is not ' ' (which may happen in caes of a BOOM), 
     * adjacent bomb counts in boardView are updated to account for there being one less bomb.
     */
    private synchronized void revealAdjacent(int row, int col) {
        for (int r = row-1; r <= row+1; r++){
            for (int c = col-1; c <= col+1; c++){
                if (!(r==row && c==col)){ //don't touch initial cell
                    switch (boardView[r][c]){
                    case ' ': case 'F':
                        break;
                    case '-':
                        if (boardView[row][col] == ' '){ // if original cell had no neighbours with bombs
                            int b = countBombs(r, c);                        
                            if (b==0){
                                boardView[r][c] = ' ';
                                revealAdjacent(r, c);
                            } else {
                                boardView[r][c] = (char) (b + '0');
                            }
                            break;
                        } else { // if original cell had some adjacent bombs, untouched cells remain untouched
                            break;
                        }
                    case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8':
                        // in case bomb exploded, bomb counts are updated
                        int b = countBombs(r, c);                        
                        if (b==0){
                            boardView[r][c] = ' ';
                            revealAdjacent(r, c);
                        } else {
                            boardView[r][c] = (char) (b + '0');
                        }
                        break;
                    }
                }
            }
        }
        checkRep();        
    }
    
    public synchronized char[][] flag(int col, int row){
        if (boardView[row][col] == '-'){
            boardView[row][col] = 'F';
        }
        checkRep();
        return getView();
    }
    
    public synchronized char[][] deflag(int col, int row){
        if (boardView[row][col] == 'F'){
            boardView[row][col] = '-';
        }
        return getView();
    }
    
    /**
     * Helper that counts bombs in adjacent cells of bombs[row][col]
     */
    private synchronized int countBombs(int row, int col) {
        int result = 0;
        for (int r = row-1; r <= row+1; r++){
            for (int c = col-1; c <= col+1; c++){
                if (bombs[r][c]) {result++;}
            }
        }
        return result;
    }
    
    private void checkRep(){
        // TODO
    }
    
}
