package top.grapedge.ui.component;

import javax.swing.*;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description: 零边距容器
 * @author: Grapes
 * @create: 2019-03-06 09:11
 **/
public class GContainer extends GPanel {
    public GContainer() {
        super();
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        //boxLayout.
        setLayout(boxLayout);
    }
}
