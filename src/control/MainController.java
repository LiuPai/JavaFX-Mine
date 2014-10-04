package control;

import game.Board;
import game.Difficulty;
import game.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    private Difficulty difficulty = Difficulty.EASY;
    private Board board;
    private Game game;
    private Timeline mainTimeline,secondTimeline;
    private DigitBoard secondsDisplay,flagDisplay;
    private Button mainButton;

    @FXML
    private ResourceBundle resources;
    @FXML
    private VBox mainPane;
    @FXML
    private BorderPane infoPane;
    @FXML
    private BorderPane boardPane;
    @FXML
    void esayGame(){
        difficulty = Difficulty.EASY;
        initGame();
    }
    @FXML
    void mediumGame(){
        difficulty = Difficulty.MEDIUM;
        initGame();
    }
    @FXML
    void hardGame(){
        difficulty = Difficulty.HARD;
        initGame();
    }
    @FXML
    void customGame() throws IOException {
        Difficulty custom = Difficulty.Custom;
        //init custom stage
        Stage customDialog = customStage(custom);
        customDialog.showAndWait();
        if(custom.getRow() != 0){
            difficulty = custom;
            initGame();
        }
    }


    @FXML
    void showMines() {
        game.showMines();
    }

    private void initGame(){

        if (game!=null){
            game.gameStop();
        }
        mainButton.getStyleClass().removeAll("lose", "wait");
        mainButton.getStyleClass().add("fine");
        initMainTimeline();
        secondsDisplay.displayNumber(0);
        flagDisplay.displayNumber(difficulty.getMines());
        board = new Board(difficulty);
        board.setEffect(new DropShadow());
        boardPane.setCenter(board);
        board.resize();
        game = new Game(difficulty,board.getCells());
        boardPane.addEventFilter(MouseEvent.MOUSE_PRESSED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.isPrimaryButtonDown()){
                    Cell cell = null;
                    try{
                        cell = (Cell)mouseEvent.getPickResult().getIntersectedNode();
                    } catch (ClassCastException  e){

                    }
                    if(cell !=null && !cell.isOpen()){
                        mainButton.getStyleClass().removeAll("fine", "lose");
                        mainButton.getStyleClass().add("wait");
                    }
                }
            }
        });
        boardPane.addEventFilter(MouseEvent.MOUSE_RELEASED,new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mainButton.getStyleClass().remove("wait");
                mainButton.getStyleClass().add("fine");
            }
        });
        game.onGameProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if(newValue == true){
                    mainTimeline.play();
                } else {
                    mainTimeline.stop();
                    if(secondTimeline != null){
                        secondTimeline.stop();
                    }
                    mainButton.getStyleClass().removeAll("fine","wait");
                    mainButton.getStyleClass().add("lose");
                }
            }
        });
        game.flagCounterProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                flagDisplay.displayNumber((int)newValue);
            }
        });
    }
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize fxml components
        assert mainPane != null : "fx:id=\"mainPane\" was not injected: check your FXML file 'Mines.fxml'.";
        assert infoPane != null : "fx:id=\"infoPane\" was not injected: check your FXML file 'Mines.fxml'.";
        secondsDisplay = new DigitBoard(3,0.3);
        flagDisplay = new DigitBoard(2,0.3);
        mainButton = createMainButton();
        infoPane.setLeft(secondsDisplay);
        infoPane.setRight(flagDisplay);
        infoPane.setCenter(mainButton);
        difficulty = Difficulty.HARD;
        initGame();

    }
    private Stage customStage(Difficulty difficulty) throws IOException {
        Stage customDialog = new Stage(StageStyle.UTILITY);
        ResourceBundle i18nBundle = ResourceBundle.getBundle("resources.lang.Bundle");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/custom.fxml"),i18nBundle);
        loader.load();
        Parent customPane = loader.getRoot();
        CustomController controller = loader.getController();
        controller.setStage(customDialog);
        controller.setDifficulty(difficulty);
        customDialog.setScene(new Scene(customPane));
        customDialog.initOwner(mainPane.getScene().getWindow());
        customDialog.initModality(Modality.WINDOW_MODAL);
        customDialog.setTitle(i18nBundle.getString("custom"));
        customDialog.setResizable(false);
        return customDialog;
    }
    private void initMainTimeline() {
        mainTimeline = new Timeline();
        mainTimeline.getKeyFrames().add(
                new KeyFrame(
                        new Duration(1000 - (System.currentTimeMillis()%1000)),
                        new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent event) {
                                if (secondTimeline != null) {
                                    secondTimeline.stop();
                                }
                                secondTimeline = new Timeline();
                                secondTimeline.setCycleCount(Timeline.INDEFINITE);
                                secondTimeline.getKeyFrames().add(
                                        new KeyFrame(Duration.millis(250),
                                                new EventHandler<ActionEvent>() {
                                                    @Override public void handle(ActionEvent event) {
                                                        try{
                                                        int gameSeconds = game.getGameSeconds();
                                                        secondsDisplay.displayNumber(gameSeconds);
                                                        } catch (NullPointerException e){

                                                        }
                                                    }
                                                }
                                        )
                                );
                                secondTimeline.play();
                            }
                        }
                )
        );

    }

    private Button createMainButton(){
        Button mainButton = new Button();
        mainButton.setMinWidth(30);
        mainButton.setMinHeight(30);
        mainButton.getStyleClass().add("fine");

        mainButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("entered");
                initGame();
            }
        });
        return mainButton;
    }
}
