package top.grapedge.gclass.client.ui;

import top.grapedge.ui.base.util.ImageUtil;
import top.grapedge.ui.component.GGroupButton;
import top.grapedge.ui.component.GPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @program: G-ClassManager
 * @description: 用于创建组件
 * @author: Grapes
 * @create: 2019-03-12 21:14
 **/
public class ChatBar {
    private int type = 0;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private String id;

    public String getId() {
        return id;
    }

    private GPanel myGroup;
    private GGroupButton myGroupButton;
    public void addActionListener(ActionListener listener) {
        myGroupButton.addActionListener(listener);
    }

    private String title;
    public String getTitle() {
        return title;
    }
    public ChatBar(String id, String title, ImageIcon avatar, int type, int asize, int gwidth, int gheight) {
        this.id = id;
        this.title = title;
        myGroup = new GPanel();
        myGroup.setPreferredSize(new Dimension(gwidth, gheight));
        myGroup.setMaximumSize(new Dimension(gwidth, gheight));
        myGroupButton = new GGroupButton(title);
        myGroupButton.setHorizontalAlignment(SwingConstants.LEFT);
        myGroupButton.setNormalColor(new Color(0xFBFCFC));
        myGroupButton.setHoverColor(new Color(0xFDFEFE));
        myGroupButton.setPressColor(new Color(0xF4F6F7));
        myGroupButton.setTextColor(new Color(0x424949));
        this.type = type;
        if (avatar == null) avatar = ImageUtil.getRoundImageIcon(new ImageIcon("images/avatar.jpg").getImage());
        avatar.setImage(avatar.getImage().getScaledInstance(asize, asize, Image.SCALE_DEFAULT));
        avatar = ImageUtil.getRoundImageIcon(avatar.getImage());
        myGroupButton.setIcon(avatar);
        myGroupButton.setPreferredSize(new Dimension(gwidth, gheight));
        myGroup.add(myGroupButton);
    }

    public GPanel getPane() {
        return  myGroup;
    }

    public GGroupButton getArea() {
        return myGroupButton;
    }

    public void showCount(boolean show) {
        myGroupButton.setShowCount(show);
    }
    public boolean isShowCount() {
        return myGroupButton.isShowCount();
    }
}
