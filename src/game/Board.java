package game;

import control.Cell;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.ArrayList;

/**
 * Created by lilium on 13/09/25.
 */
public class Board extends GridPane {
    private ArrayList<Cell> cells = new ArrayList<Cell>();
    private Cell[][] cellArray;
    private int row;
    private int column;
    public Board(Difficulty diff){
        row = diff.getRow();
        column = diff.getColumn();
        cellArray = new Cell[row][column];
        getStyleClass().add("boardPane");

        initBlocks();

    }
    public ArrayList<Cell> getCells(){
        return cells;
    }

    private void initBlocks() {
        for(int row = 0;row < this.row;row++) {
            for(int column = 0;column < this.column;column++){
                int blockID = row * this.column + column;
                Cell cell = new Cell(row,column,blockID);
                cells.add(cell);
                add(cell,column,row);
            }
        }
    }

    public void resize(){
        // resize stage and center to screen
        try{
            Window window = getScene().getWindow();

            autosize();
            window.sizeToScene();
            //window.centerOnScreen();
        } catch (NullPointerException e){

        }
    }
}
