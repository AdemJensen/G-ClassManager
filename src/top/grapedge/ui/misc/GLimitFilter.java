package top.grapedge.ui.misc;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @program: G-ClassManager
 * @description:
 * @author: Grapes
 * @create: 2019-03-07 19:11
 **/
public class GLimitFilter extends PlainDocument {
    private int limit;

    public int getLimit() {
        return limit;
    }

    public GLimitFilter(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (getLength() < getLimit())
            super.insertString(offs, str, a);
    }
}
