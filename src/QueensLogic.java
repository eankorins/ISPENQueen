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
    BDD rule = factory.one();
    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.board = new int[x][y];
        this.cellCount = x * y;
        factory.setVarNum(cellCount);
        buildBDD();
    }

    public BDD buildBDD(){
        BDD t = factory.one();
        for(int row = 0; row < board.length; row++){
            for(int col = 0; col <  board.length; col++){
                BDD node = t.andWith(factory.ithVar(row * board.length + col));
                List<Integer> affected = getAffectedCells(col,row);
                for(int i : affected){
                    node.andWith(factory.nithVar(i));
                }
            }
        }
        rule.andWith(t);
        return null;
    }
    public List<Integer> getRemainingRow(int col, int row){
        List<Integer> result = new ArrayList<Integer>();
        for(int c = 0; c < board.length; c++){
            if(c != col){
                result.add(row * board.length + c);
            }
        }
        return result;
    }

    public List<Integer> getAffectedCells(int col, int row){
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(getLeftDiagonal(col, row));
        result.addAll(getRightDiagonal(col, row));
        result.addAll(getRemainingColumn(col, row));
        result.addAll(getRemainingRow(col,row));
        return result;
    }
    public List<Integer> getRemainingColumn(int col, int row){
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

    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }
        board[column][row] = 1;

        int cellNum = row * board.length + column;
        rule.restrict(factory.ithVar(cellNum));
        System.out.println("node count" + rule.nodeCount());

        for(int r = 0; r < board.length; r++){
            for(int c = 0; c < board.length; c++){
                int cellNumber = r * board.length + c;
                if(rule.restrict(factory.ithVar(cellNumber)).isZero()){
                    board[c][r] = -1;
                }
            }
        }



        return true;
    }
}
