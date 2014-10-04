import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle i18nBundle = ResourceBundle.getBundle("resources.lang.Bundle");
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/main.fxml"),i18nBundle);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("resources/styleSheets/css.css").toString());
        primaryStage.setTitle("JMine");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
