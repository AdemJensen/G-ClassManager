package top.grapedge.ui.misc;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-07 16:16
 **/
public class GNumberFilter extends GLimitFilter {

    public GNumberFilter(int limit) {
        super(limit);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str.charAt(0) >= '0' && str.charAt(0) <= '9') {
            super.insertString(offs, str, a);
        }
    }
}