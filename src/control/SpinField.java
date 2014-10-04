package control;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * Created by lilium on 13/09/29.
 */
public class SpinField extends HBox {
    private IntegerProperty max = new SimpleIntegerProperty(this,"max",0);
    private IntegerProperty min = new SimpleIntegerProperty(this,"min",0);
    private IntegerProperty defaultValue = new SimpleIntegerProperty(this,"defaultValue",0);
    private IntegerProperty value = new SimpleIntegerProperty(this,"value",0);
    private TextField field = new TextField();
    private Button adder = new Button("+");
    private Button suber = new Button("-");
    private Timeline idlerTimeline;
    private AnimationTimer timer;
    public SpinField(){
        getChildren().addAll(field, suber, adder);
        initEventHandler();
        initListener();
    }

    private void initListener() {
        field.textProperty().addListener(new ChangeListener<String>() {
            private boolean ignore;
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (ignore || newValue.length() == 0) {
                    return;
                }
                int value;
                int max = getMax();
                int min = getMin();
                try {
                    value = Integer.valueOf(newValue);
                    /* reset value inside boundary */
                    if (value > max) {
                        value = max;
                    } else if (value < min) {
                        value = min;
                    }
                    /* update value and display text */
                    ignore = true;
                    setValue(value);
                    ignore = false;
                } catch (NumberFormatException e) {
                    /* accept a single '-' */
                    if (!(field.getText().length() == 1 && field.getText().charAt(0) == '-')) {
                        /* drop this change */
                        ignore = true;
                        field.setText(String.valueOf(getValue()));
                        ignore = false;
                    }
                }
            }
        });
        valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if(newValue.intValue() >= getMax()){
                    adder.setDisable(true);
                } else if(newValue.intValue() <= getMin()){
                    suber.setDisable(true);
                } else {
                    adder.setDisable(false);
                    suber.setDisable(false);
                }
                field.setText(newValue.toString());
            }
        });
        field.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                field.setText(String.valueOf(getValue()));
            }
        });
    }

    private void initEventHandler() {
        field.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                field.setText(String.valueOf(getValue()));
                actionEvent.consume();
            }
        });
        adder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setValue(getValue() + 1);
                actionEvent.consume();
            }
        });
        adder.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                idlerTimeline = new Timeline(new KeyFrame(new Duration(1000),new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        timer = new AnimationTimer() {
                            @Override
                            public void handle(long l) {
                                setValue(getValue() + 1);
                                if(getValue()>=getMax()){
                                    timer.stop();
                                }
                            }
                        };
                        timer.start();
                    }
                }));
                idlerTimeline.play();
            }
        });
        adder.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (idlerTimeline != null && timer != null) {
                    idlerTimeline.stop();
                    timer.stop();
                }
            }
        });
        suber.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setValue(getValue() - 1);
                actionEvent.consume();
            }
        });
        suber.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                idlerTimeline = new Timeline(new KeyFrame(new Duration(1000),new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        timer = new AnimationTimer() {
                            @Override
                            public void handle(long l) {
                                setValue(getValue() - 1);
                                if(getValue()<=getMin()){
                                    timer.stop();
                                }
                            }
                        };
                        timer.start();
                    }
                },null));
                idlerTimeline.play();
            }
        });
        suber.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (idlerTimeline != null && timer != null) {
                    idlerTimeline.stop();
                    timer.stop();
                }
            }
        });

    }

    /**
     * The top boundary property.
     * @return The top boundary property.
     */
    public IntegerProperty maxProperty(){
        return max;
    }
    /**
     * The bottom boundary property.
     * @return The bottom boundary property.
     */
    public IntegerProperty minProperty(){
        return min;
    }
    /**
     * The default value property.
     * @return The default value property.
     */
    public IntegerProperty defaultValueProperty(){
        return defaultValue;
    }
    /**
     * The value property.
     * @return The value property.
     */
    public IntegerProperty valueProperty(){
        return value;
    }

    /**
     * Gets the top boundary of the field.
     * @return top boundary of the field.
     */
    public int getMax(){
        return max.get();
    }
    /**
     * Gets the bottom boundary of the field.
     * @return the bottom boundary of the field.
     */
    public int getMin(){
        return min.get();
    }
    /**
     * Gets the default value of the field.
     * @return the default value.
     */
    public int getDefaultValue(){
        return defaultValue.get();
    }
    /**
     * Gets the value of the field.
     * @return the value of the field.
     */
    public int getValue(){
        return value.get();
    }

    /**
     * Sets the top boundary.
     * @param max The top boundary.
     */
    public void setMax(int max){
        this.max.set(max);
        if(getValue()>getMax()){
            setValue(getMax());
        }
    }
    /**
     * Sets the bottom boundary.
     * @param min The bottom boundary.
     */
    public void setMin(int min){
        this.min.set(min);
        if(getValue()<getMin()){
            setValue(getMin());
        }
    }
    /**
     * Sets the default value.
     * @param defaultValue The default value.
     */
    public void setDefaultValue(int defaultValue){
        this.defaultValue.set(defaultValue);
        setValue(defaultValue);
    }
    /**
     * Sets the value.
     * @param value The value.
     */
    public void setValue(int value){
        this.value.set(value);
        field.setText(String.valueOf(value));
    }

}
