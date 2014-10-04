package control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Created by lilium on 13/10/14.
 */
public class CellBase extends Control {
    private static final PseudoClass ARMED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("armed");

    private ReadOnlyBooleanWrapper armed = new ReadOnlyBooleanWrapper() {
        protected void invalidated() {
            CellBase.this.pseudoClassStateChanged(CellBase.ARMED_PSEUDOCLASS_STATE, get());
        }

        public Object getBean() {
            return CellBase.this;
        }

        public String getName() {
            return "armed";
        }
    };
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase() {
        protected void invalidated() {
            CellBase.this.setEventHandler(ActionEvent.ACTION, (EventHandler) get());
        }

        public Object getBean() {
            return CellBase.this;
        }

        public String getName() {
            return "onAction";
        }
    };
    public final ReadOnlyBooleanProperty armedProperty() {
        return this.armed.getReadOnlyProperty();
    }

    public final boolean isArmed() {
        return armedProperty().get();
    }

    private void setArmed(boolean paramBoolean) {
        this.armed.set(paramBoolean);
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return this.onAction;
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return (EventHandler) onActionProperty().get();
    }

    public final void setOnAction(EventHandler<ActionEvent> paramEventHandler) {
        onActionProperty().set(paramEventHandler);
    }

    public void arm() {
        setArmed(true);
    }

    public void disarm() {
        setArmed(false);
    }

    public void fire() {
        if (!isDisabled())
            fireEvent(new ActionEvent());
    }

    protected Skin<?> createDefaultSkin() {
        return new CellSkin(this);
    }
}
