package control;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Created by lilium on 13/09/22.
 */

/**
 *
 */
public class Cell extends Control implements Skinnable, Styleable {

    private boolean isOpen = false;
    private boolean isMine = false;
    private boolean isFlagged = false;
    private boolean isExploded = false;
    private int surroundMineCount = 0;
    private int id;
    private int row;
    private int column;

    /**
     * Initialize Cell
     *
     * @param row    row number
     * @param column column number
     * @param id     assigned id
     */
    public Cell(int row, int column, int id) {
        this.row = row;
        this.column = column;
        this.id = id;
        setMinSize(16, 16);
    }




    /**
     * Gets block's id.
     *
     * @return block's id.
     */
    public int getID() {
        return id;
    }

    /**
     * Gets block's row number.
     *
     * @return block's row number.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets block's column number.
     *
     * @return block's column number.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets if block is opened.
     *
     * @return block's open state.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Gets if block is mine.
     *
     * @return block's mine state.
     */
    public boolean isMine() {
        return isMine;
    }

    /**
     * Sets block's mine state.
     *
     * @param state add or remove mine.
     */
    public void setMine(boolean state) {
        surroundMineCount = state ? -1 : 0;
        isMine = state;
    }

    /**
     * Gets if block been flagged.
     *
     * @return block's flag state.
     */
    public boolean isFlagged() {
        return isFlagged;
    }

    /**
     * Gets if block already exploded.
     *
     * @return block's explode state.
     */
    public boolean isExploded() {
        return isExploded;
    }

    /**
     * Gets if block is empty.
     *
     * @return block's empty state.
     */
    public boolean isEmpty() {
        return (surroundMineCount == 0);
    }

    /**
     * Gets surround mine block count.
     *
     * @return How many mines around this block.
     */
    public int getSurroundMineCount() {
        return surroundMineCount;
    }

    /**
     * Sets surround mine number.
     *
     * @param mineCount surround mine number.
     */
    public void setSurroundMineCount(int mineCount) {
        surroundMineCount = mineCount;
    }

    /**
     * Sets this block been flagged.
     */
    public void setFlagged() {
        if (!isOpen) {
            if (!isFlagged) {
                isFlagged = true;
                getStyleClass().add("flag");
            } else {
                isFlagged = false;
                getStyleClass().remove("flag");
            }
        }
    }

    /**
     * Update surround mine number.
     *
     * @param add add or sub surround mine number.
     */
    public void updateSurroundMineCount(boolean add) {
        if (add) {
            surroundMineCount++;
        } else {
            surroundMineCount--;
        }
    }

    /**
     * Open this block.
     *
     * @return Result of this opened block.
     */
    public Result open() {
        //Don't open flagged block.
        if (isFlagged) {
            return Result.FLAGGED;
        } else {
            isOpen = true;
            if (isMine) {
                isExploded = true;
                explode();
                //getStyleClass().add("exploded");
                return Result.EXPLODED;
            } else if (surroundMineCount == 0) {
                getStyleClass().add("empty");
                return Result.EMPTY;
            } else {
                displayMineCount();
                return Result.NUMBERED;
            }
        }
    }

    /**
     * Shows block contain after game over.
     *
     * @param onGame Is game over.
     */
    public void showMine(boolean onGame) {
        if (!isExploded) {
            String style = isMine ? (isFlagged ? (onGame ? "mine_with_flag" : "mine_with_flag_M") :
                    (onGame ? "mine_without_flag" : "mine_without_flag_M")) : (isFlagged ? "flag_wrong" : "");
            if (!style.equals(""))
                getStyleClass().add(style);
        }
    }

    public void explode() {
        Timeline timeline = new Timeline();
        Image explodeSpriteSheet = new Image(Cell.class.getResource("/resources/images/explodeSpriteSheet.png").toString());
        ImageView iv = new ImageView(explodeSpriteSheet);
        toFront();
        int step = 256;
        int row = 6;
        int column = 8;
        iv.setX(-(step / 2));
        iv.setY(-(step / 2));
        //setGraphic(iv);
        for (int y = 0; y < row; y++) {
            for (int x = 0; x < column; x++) {
                Rectangle2D viewport = new Rectangle2D(x * step, y * step, step, step);
                iv.setViewport(viewport);
                KeyValue keyValue = new KeyValue(iv.viewportProperty(), viewport);
                KeyFrame keyFrame = new KeyFrame(new Duration((y * column + x) * 30), keyValue);
                if (y == 4 && x == 5) {
                    keyFrame = new KeyFrame(new Duration((y * column + x) * 30), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            getStyleClass().add("exploded");
                        }
                    }, keyValue);
                }
                timeline.getKeyFrames().add(keyFrame);
            }
        }
        timeline.play();
    }

    private void displayMineCount() {
        getStyleClass().add("number");
        Color numberColor;
        switch (surroundMineCount) {
            case 1:
                numberColor = Color.BLUE;
                break;
            case 2:
                numberColor = Color.GREEN;
                break;
            case 3:
                numberColor = Color.RED;
                break;
            case 4:
                numberColor = Color.DARKBLUE;
                break;
            case 5:
                numberColor = Color.DARKRED;
                break;
            case 6:
                numberColor = Color.CYAN;
                break;
            case 7:
                numberColor = Color.PINK;
                break;
            case 8:
                numberColor = Color.PURPLE;
                break;
            default:
                numberColor = Color.BLACK;
                break;
        }
        //setTextFill(numberColor);
        //setText(String.valueOf(surroundMineCount));
    }



    public static enum State {
        COVERED, OPENED, DEPRESSED
    }

    /**
     *
     */
    public static enum Result {
        EMPTY, NUMBERED, EXPLODED, FLAGGED
    }
}
