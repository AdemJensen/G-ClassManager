package top.grapedge.ui.component;

import javax.swing.*;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-05 19:26
 **/
public class GFrame extends JFrame {
    public GFrame() {
        //setLocationRelativeTo(null);
    }

    public GFrame(String title) {
        super(title);
    }

    public GFrame(int width, int height) {
        setSize(width, height);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        setLocationRelativeTo(null);
    }

    public void setSize(Dimension d, boolean center) {
        if (center) setSize(d);
        else super.setSize(d);
    }

}
