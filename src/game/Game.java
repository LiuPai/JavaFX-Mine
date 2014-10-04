package game;

import control.Cell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by lilium on 13/09/23.
 */

/**
 *
 */
public class Game {
    // game result
    private boolean winLose;
    // is cheated
    //TODO : add cheat cheaker
    private boolean cheat;
    // is blind game
    //TODO : add blind mode
    private boolean blind;
    private int row;
    private int column;
    private int mine;
    // Thread safe lists
    private CopyOnWriteArrayList<Cell> cells;
    private CopyOnWriteArrayList<Cell> shuffledCells;
    private List<Cell> safeCells;
    private CopyOnWriteArrayList<Cell> mineCells = new CopyOnWriteArrayList<Cell>();
    private CopyOnWriteArrayList<Cell> openedCells = new CopyOnWriteArrayList<Cell>();
    // onChange properties
    private BooleanProperty onGame = new SimpleBooleanProperty(this, "onGame", false);
    private IntegerProperty flagCounter = new SimpleIntegerProperty(this, "flagCounter",0);
    // game time
    private LocalDateTime gameStartTime;
    private LocalDateTime gameStopTime;

    /**
     * Initialize game.
     *
     * @param difficulty Game difficulty.
     * @param cells     cells.
     */
    public Game(Difficulty difficulty, ArrayList<Cell> cells) {
        row = difficulty.getRow();
        column = difficulty.getColumn();
        mine = difficulty.getMines();
        flagCounter.setValue(mine);
        this.cells = new CopyOnWriteArrayList<Cell>(cells);
        //shuffle cells
        this.shuffledCells = (CopyOnWriteArrayList<Cell>) this.cells.clone();
        Collections.shuffle(shuffledCells, new Random(0));

        this.safeCells = (CopyOnWriteArrayList<Cell>) this.cells.clone();

        initMineBlocks();

        initMineCounts();

        initEventHandler();

    }

    // init mine cells
    private void initMineBlocks() {
        shuffledCells.parallelStream()
                .limit(mine)
                .forEach(block -> {
                    block.setMine(true);
                    mineCells.add(block);
                    safeCells.remove(block);
                });
    }

    // init mine counts
    private void initMineCounts() {
        mineCells.parallelStream().forEach(mineBlock -> updateMineCounts(mineBlock, true));
    }

    // init event handler for each block
    private void initEventHandler() {
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Cell cell = (Cell) mouseEvent.getSource();
                if (getOnGame() == false && mouseEvent.isPrimaryButtonDown()) {
                    gameBegin(cell);
                }
                if (!cell.isOpen()) {
                    if (mouseEvent.isPrimaryButtonDown()) {
                        openBlock(cell);
                    } else if (!blind && mouseEvent.isSecondaryButtonDown() && flagCounter.getValue() >= 0) {
                        if(flagCounter.getValue() > 0){
                            flagBlock(cell);
                        }else if(flagCounter.getValue() == 0 && cell.isFlagged()){
                            flagBlock(cell);
                        }

                    }
                } else if (!blind && !cell.isEmpty() && mouseEvent.isPrimaryButtonDown() && mouseEvent.isSecondaryButtonDown()) {
                    if(mouseEvent.isStillSincePress()){
                        //TODO: add auto open cell hint
                    }
                    autoOpen(cell);
                }
                if (getOnGame() && openedCells.size() == safeCells.size()) {
                    gameWin();
                }
            }
        };
        cells.parallelStream().forEach(block -> block.setOnMousePressed(eventHandler));
    }

    private void flagBlock(Cell cell) {
        cell.setFlagged();
        int currentLeftFlags = cell.isFlagged()?flagCounter.getValue()-1:flagCounter.getValue()+1;
        flagCounter.setValue(currentLeftFlags);
    }

    // game begin
    private void gameBegin(Cell firstCell) {
        // exchange mine block
        if (firstCell.isMine()) {
            // remove old mine
            firstCell.setMine(false);
            mineCells.remove(firstCell);
            safeCells.add(firstCell);
            // add new mine
            Cell newMine = shuffledCells.get(mine);
            newMine.setMine(true);
            mineCells.add(newMine);
            safeCells.remove(newMine);
            safeCells = safeCells.stream().sorted().collect(Collectors.toList());
            // update mine counts
            updateMineCounts(firstCell, false);
            updateMineCounts(newMine, true);
            countSurroundMines(firstCell);
        }
        setOnGame(true);
        //play timeline
        gameStartTime = LocalDateTime.now(ZoneId.systemDefault());
        //open first block
        openBlock(firstCell);
        //set game on play
    }



    private void openBlock(Cell cell) {
        Cell.Result result = cell.open();
        if (!openedCells.contains(cell))
            openedCells.add(cell);
        if (result == Cell.Result.EMPTY) {
            emptyOpen(cell);
        } else if (result == Cell.Result.EXPLODED) {
            gameOver();
        }
    }

    private void emptyOpen(Cell cell) {
        surroundBlocks(cell).stream()
                .filter(surroundBlock -> !surroundBlock.isOpen())
                .forEachOrdered(surroundBlock -> openBlock(surroundBlock));
    }

    private void autoOpen(Cell cell) {
        ArrayList<Cell> surroundCells = surroundBlocks(cell);
        List<Cell> flaggedCell =
                surroundCells.stream()
                        .filter(b -> !b.isOpen())
                        .filter(b -> b.isFlagged())
                        .collect(Collectors.toList());
        if (flaggedCell.size() == cell.getSurroundMineCount()) {
            surroundCells.stream()
                    .filter(b -> !b.isOpen())
                    .filter(b -> !b.isFlagged())
                    .forEach(b -> openBlock(b));
        }
    }

    private void gameOver() {
        setOnGame(false);
        gameStop();
        winLose = false;
        System.out.println(getGameTime());
    }

    public void gameStop() {
        gameStopTime = LocalDateTime.now(ZoneId.systemDefault());
        cells.forEach(b -> {
            b.setOnMousePressed(null);
            b.showMine(onGame.getValue());
        });
    }

    /**
     * Display which block is a mine.
     */
    public void showMines(){
        mineCells.forEach(b -> b.showMine(false));
    }

    private void gameWin() {
        setOnGame(false);
        gameStop();
        winLose = true;
    }

    private void countSurroundMines(Cell cell) {
        cell.setSurroundMineCount(0);
        surroundBlocks(cell).stream()
                .filter(b -> b.isMine())
                .forEach(b -> cell.updateSurroundMineCount(true));
    }

    private void updateMineCounts(Cell cell, boolean add) {
        surroundBlocks(cell).stream()
                .filter(b -> !b.isMine())
                .forEach(b -> b.updateSurroundMineCount(add));
    }

    // return surround cells based on id generation and serial of cells.
    private ArrayList<Cell> surroundBlocks(Cell cell) {
        ArrayList<Cell> surroundCells = new ArrayList<Cell>();
        int blockRow = cell.getRow();
        int blockColumn = cell.getColumn();
        for (int i = blockRow - 1; i < blockRow + 2; i++) {
            for (int j = blockColumn - 1; j < blockColumn + 2; j++) {
                int id = countID(i, j);
                if (id != -1 && id != cell.getID()) {
                    surroundCells.add(cells.get(id));
                }
            }
        }
        return surroundCells;
    }

    private int countID(int row, int column) {
        if (row >= 0 && row < this.row && column >= 0 && column < this.column) {
            return row * this.column + column;
        } else {
            return -1;
        }
    }

    public BooleanProperty onGameProperty() {
        return onGame;
    }
    public IntegerProperty flagCounterProperty(){
        return flagCounter;
    }
    public boolean getOnGame() {
        return onGameProperty().get();
    }

    public void setOnGame(boolean onGame) {
        onGameProperty().set(onGame);
    }

    public boolean getWinLose() {
        return winLose;
    }
    public int getGameSeconds(){
        java.time.Duration gameSeconds = java.time.Duration.between(gameStartTime,LocalDateTime.now());
        return (int)gameSeconds.toMillis()/1000;
    }
    public long getGameTime(){
        java.time.Duration gameTime = java.time.Duration.between(gameStartTime,gameStopTime);
        return gameTime.toMillis();
    }
    public int get3BV(){

        return 0;
    }
}
