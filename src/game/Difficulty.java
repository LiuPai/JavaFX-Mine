package game;

/**
 * Created by lilium on 13/09/25.
 */
public enum Difficulty {
    EASY (8,8,10),
    MEDIUM (16,16,40),
    HARD (16,30,99),
    Custom (0,0,0);
    private int row;
    private int column;
    private int mines;

    Difficulty(int row,int column,int mines){
        this.row = row;
        this.column = column;
        this.mines = mines;
    }
    public void custom(int row, int column, int mines){
        this.row = row;
        this.column = column;
        this.mines = mines;
    }
    public int getRow(){
        return row;
    }
    public int getColumn(){
        return column;
    }
    public int getMines(){
        return mines;
    }
}
