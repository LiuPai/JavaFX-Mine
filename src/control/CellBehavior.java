package control;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;

import java.util.List;

/**
 * Created by lilium on 13/10/13.
 */
public class CellBehavior<C extends Cell> extends BehaviorBase<C>{
    public CellBehavior(C c, List<KeyBinding> keyBindings) {
        super(c, keyBindings);
    }
}
