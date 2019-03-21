package top.grapedge.ui.component;

import top.grapedge.gclass.base.GClass;
import top.grapedge.ui.base.GConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-21 08:56
 **/
public class GWebView extends JEditorPane {
    public GWebView() {
        setContentType("text/html");
        setEditorKit(GClass.getHTMLEditorKit());
        setEditable(false);
        setBorder(new EmptyBorder(10, 10, 0, 0));
        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        setFont(GConfig.createFont(13));
        addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
