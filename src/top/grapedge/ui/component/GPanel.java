package top.grapedge.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-05 18:55
 **/
public class GPanel extends JPanel {

    public GPanel() {
        ((FlowLayout)getLayout()).setVgap(0);
        ((FlowLayout)getLayout()).setHgap(0);
    }

    public GPanel(LayoutManager layoutManager) {
        this();
        setLayout(layoutManager);
    }
}
