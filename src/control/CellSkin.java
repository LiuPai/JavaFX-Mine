package control;

import com.sun.javafx.scene.control.behavior.KeyBinding;
import javafx.scene.control.SkinBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lilium on 13/10/13.
 */
public class CellSkin extends SkinBase<C extends CellBase> {
    protected static final List<KeyBinding> BLOCK_BINDINGS = new ArrayList();
    public CellSkin(CellBase cell) {
        super(cell);
    }
}
