import control.Cell;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by lilium on 13/09/27.
 */
public class TestCase extends Application {
    public static void main (String... args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Cell cell = new Cell(0,0,0);
        root.getChildren().add(cell);
        cell.getStyleClass().add("mine_without_flag");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("resources/styleSheets/css.css").toString());
        stage.sizeToScene();
        stage.setScene(scene);
        stage.show();
    }
}