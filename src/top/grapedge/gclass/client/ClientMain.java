package top.grapedge.gclass.client;

import top.grapedge.gclass.client.base.GChatClient;
import top.grapedge.gclass.client.base.GFileClient;
import top.grapedge.gclass.client.base.GPost;
import top.grapedge.gclass.client.frame.LoginFrame;
import top.grapedge.ui.component.GImage;
import top.grapedge.ui.component.GMessageDialog;
import top.grapedge.ui.component.GWindow;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) {
        try {
            new ClientMain();
        } catch (IOException e) {
            e.printStackTrace();
            new GMessageDialog((Frame) null, "连接服务器失败", "哎鸭鸭");
            System.exit(0);
        }
        EventQueue.invokeLater(LoginFrame::new);
    }

    private ClientMain() throws IOException {
        // 显示闪屏界面
        showSplash();
        new GPost();
        new GFileClient();
        new GChatClient();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showSplash() {
        int showMillis = 0;
        int width = 400, height = 250;
        GWindow splash = new GWindow();
        GImage screen = new GImage(new ImageIcon("images/splash.png"));
        screen.setPreferredSize(new Dimension(width, height));
        splash.getContentPane().add(
                screen
        );
        splash.setSize(new Dimension(width, height));
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);
        try {
            Thread.sleep(showMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        splash.setVisible(false);
        splash.dispose();
    }

}