package control;

import game.Difficulty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Created by lilium on 13/09/26.
 */
public class CustomController {
    private Stage stage;
    private Difficulty difficulty;
    @FXML
    private SpinField rowField;
    @FXML
    private SpinField columnField;
    @FXML
    private SpinField mineField;
    @FXML
    void initialize(){
        ChangeListener<Number> updateMineMax = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                mineField.setMax(rowField.getValue() * columnField.getValue() - 10);
            }
        };
        rowField.valueProperty().addListener(updateMineMax);
        columnField.valueProperty().addListener(updateMineMax);
    }
    @FXML
    void handleCancelAction(){
        stage.close();
    }
    @FXML
    void handleConfirmAction(){
        difficulty.custom(rowField.getValue(),columnField.getValue(),mineField.getValue());
        stage.close();
    }

    public void setStage(Stage customDialog){
        this.stage = customDialog;
    }

    public void setDifficulty(Difficulty custom) {
        this.difficulty = custom;
    }
}
