/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 *
 * @author Stavros Amanatidis
 *
 */
import java.lang.reflect.Array;
import java.util.*;

import net.sf.javabdd.*;

public class QueensLogic {
    private int x = 0;
    private int y = 0;
    private int[][] board;
    private int cellCount = 0;
    BDDFactory factory = JFactory.init(2000000, 200000);
    BDD bdd;
    BDD restricted;
    private List<Integer> queens;
    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.board = new int[x][y];
        this.cellCount = x * y;
        //Initialize Variable count to board size
        factory.setVarNum(cellCount);
        //Main BDD
        bdd = buildBDD();
        //Placed Queens Array
        queens = new ArrayList<Integer>();
    }

    public BDD buildBDD(){
        //Initialize bdd to one
        BDD bdd = factory.one();
        //Add rules for every cell
        for(int row = 0; row < board.length; row++){
            for(int col = 0; col < board.length; col++){
                //All affected cells for a given queen position
                List<Integer> affected = getAffectedCells(col, row);
                //Add rules for this queen in the big conjunction
                bdd.andWith(buildSingleCellBDD(row * board.length + col, affected));
            }
        }
        //Add the one queen per row rule
        return bdd.andWith(restrictRows());
    }
    public BDD buildSingleCellBDD(int cellNumber, List<Integer> affected){
        BDD c = factory.one();
        //Conjunction of negations for the given cell number
        for(int a : affected){
            c.andWith(factory.nithVar(a));
        }
        //Queen in this cell implies the conjunction of negations
        return factory.ithVar(cellNumber).imp(c);
    }
    public List<Integer> getAffectedCells(int col, int row){
        //Gets all cell numbers that are connected to the given cell
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(getLeftDiagonal(col, row));
        result.addAll(getRightDiagonal(col, row));
        result.addAll(getRemainingColumn(col, row));
        result.addAll(getRemainingRow(col,row));
        return result;
    }
        public List<Integer> getRemainingRow(int col, int row){
        //For every cell on the row, except for the one with the given cell column
        List<Integer> result = new ArrayList<Integer>();
        for(int c = 0; c < board.length; c++){
            if(c != col){
                result.add(row * board.length + c);
            }
        }
        return result;
    }


    public List<Integer> getRemainingColumn(int col, int row){
        //For every cell on the column, except for the one with the given cell row
        List<Integer> result = new ArrayList<Integer>();
        for(int r = 0; r < board.length; r++){
            if(r != row){
                result.add(r * board.length + col);
            }
        }
        return result;
    }

    public List<Integer> getLeftDiagonal(int col, int row){
        List<Integer> result = new ArrayList<Integer>();
        int currentColumn = col;
        int currentRow = row;
        //For the diagonal range except for the given queen location
        while(currentColumn >= 0 && currentRow >= 0){
            if(currentColumn != col && currentRow != row){
                result.add(currentRow * board.length + currentColumn);
            }
            currentColumn--;
            currentRow--;
        }

        currentColumn = col;
        currentRow = row;

        while(currentColumn < board.length && currentRow < board.length){
            if(currentColumn != col && currentRow != row){
                result.add(currentRow * board.length + currentColumn);
            }
            currentColumn++;
            currentRow++;
        }
        return result;
    }
    public List<Integer> getRightDiagonal(int col, int row){
        List<Integer> result = new ArrayList<Integer>();
        int currentColumn = col;
        int currentRow = row;
        //For the diagonal range except for the given queen location
        while(currentColumn < board.length && currentRow >= 0){
            if(currentColumn != col && currentRow != row){
                result.add(currentRow * board.length + currentColumn);
            }
            currentColumn++;
            currentRow--;
        }

        currentColumn = col;
        currentRow = row;

        while(currentColumn >= 0 && currentRow < board.length){
            if(currentColumn != col && currentRow != row){
                result.add(currentRow * board.length + currentColumn);
            }
            currentColumn--;
            currentRow++;
        }
        return result;
    }
    public int[][] getGameBoard() {
        return board;
    }

    public BDD restrictRows(){
        BDD r = factory.one();

        for(int row = 0;  row < board.length; row++){
            BDD rInner = factory.zero();
            for(int col = 0; col < board.length; col++){
                //Disjunction for every cell
                rInner.orWith(factory.ithVar(row*board.length + col));
            }
            //Conjunctions of the rows disjunctions
            r.andWith(rInner);
        }
        return r;
    }
    public BDD queenRestrictions(){
        //Sets the placed queens and restricts their value in the rule bdd
        BDD r = factory.one();
        for(int pos : queens){
            r.andWith(factory.ithVar(pos));
        }
        return bdd.restrict(r);
    }

    public boolean insertQueen(int column, int row) {
        //Returns if taken or unavailable
        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        //Places queen on board
        board[column][row] = 1;


        int cellNum = row * board.length + column;
        queens.add(cellNum);
        //Gets the restricted BDD with the newly added queen
        restricted = queenRestrictions();

        //Iterates every cell, and sets the cell to unavailable
        //if the a queen placed would result in an unsatisfactory solution
        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board.length; c++){
                int cellNumber = r * board.length + c;
                if(restricted.restrict(factory.ithVar(cellNumber)).isZero()){
                    board[c][r] = -1;
                }
            }
        }



        return true;
    }
}
