package control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.effect.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Created by lilium on 13/10/10.
 */
public class DigitBoard extends HBox {
    private int length;
    private double scale;

    private int max;

    private Digit[] digits;

    public DigitBoard(int length,double scale){
        this.length = length;
        this.scale = scale;
        initialize();
    }
    private void initialize(){
        setPadding(new Insets(0,5,5,0));
        setEffect(new DropShadow());
        max = (int)Math.pow(10,length) -1;
        digits = new Digit[length];
        for(int i =0;i < length;i++){
            Glow onEffect = new Glow(10);
            onEffect.setInput(new InnerShadow(5,Color.BLACK));
            digits[i] = new Digit(Color.RED,Color.DARKRED,onEffect,new ColorInput(0,0,0,0,Color.TRANSPARENT) ,scale);
            digits[i].setTranslateX( i * scale * 10);
        }
        getChildren().addAll(digits);
        setMinWidth(length * scale * 54 + (length - 1) * scale * 10);
    }

    public void displayNumber(int number) {
        if(number > max){
            number = max;
        }
        int tempRemainder = number;
        for (int i = length - 1; i >= 0;i--){
            int currentPos = (int)Math.pow(10,i);
            int quotient = tempRemainder/currentPos;
            tempRemainder = tempRemainder % currentPos;
            digits[length - 1 - i].showNumber(quotient);
        }
    }

}
