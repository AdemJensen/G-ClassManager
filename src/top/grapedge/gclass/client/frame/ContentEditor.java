package top.grapedge.gclass.client.frame;

import top.grapedge.gclass.base.MarkDownParser;
import top.grapedge.gclass.client.base.GEditorListener;
import top.grapedge.gclass.server.json.MessageJson;
import top.grapedge.ui.base.GConfig;
import top.grapedge.ui.base.WrapEditorKit;
import top.grapedge.ui.base.util.ColorUtil;
import top.grapedge.ui.component.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

/**
 * @program: G-ClassManager
 * @description: 公告或者投票的编辑器
 * @author: Grapes
 * @create: 2019-03-19 21:43
 **/
public class ContentEditor extends GFrame {
    // 窗体高度
    private int width = 916;
    // 窗体宽度
    private int height = 616;

    private GPanel advicePane;
    public ContentEditor(String confirmText) {
        // 基础属性设置
        setTitle("Editor");
        setUndecorated(true);
        setSize(width, height);
        setLocationRelativeTo(null);
        setBackground(ColorUtil.TRANSPARENT);   // 透明窗体
        initView(confirmText);
        var markdownTip = new MessageJson();
        markdownTip.text = "请使用Markdown语法进行编写，在右侧您可以进行实时预览。";
        createTips(markdownTip);

        setVisible(true);
    }

    private GEditorListener listener;

    public void setEditorListener(GEditorListener listener) {
        this.listener = listener;
    }

    public ContentEditor(String confirm, GFrame frame, List<MessageJson> tips) {
        this(confirm, frame);
        addTips(tips);
    }

    private GFrame parent;
    public ContentEditor(String confirm, GFrame frame) {
        this(confirm);
        parent = frame;
    }

    public void setText(String text) {
        textEditor.setText(text);
    }

    private GTextPane textEditor;
    private void initView(String confirmText) {
        // 阴影大小
        int shadowSize = 8;
        GShadowPane container = new GShadowPane(shadowSize, 10);

        container.setLayout(new BorderLayout());
        setContentPane(container);

        // 关闭按钮
        GButton close = new GButton();
        ImageIcon closeIcon = new ImageIcon("images/close.png");
        closeIcon.setImage(closeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT ));
        close.setIcon(closeIcon);
        close.setRadius(7);
        close.setPreferredSize(new Dimension(30, 30));
        close.setNormalColor(new Color(0xD0D3D4));
        close.addActionListener(e->{
            if (parent != null) parent.setEnabled(true);
            dispose();
        });

        advicePane = new GPanel();
        advicePane.setLayout(new BoxLayout(advicePane, BoxLayout.Y_AXIS));
        advicePane.setBackground(new Color(0xFBFEFE));
        var adviceScrollPane = new GScrollPane(advicePane);
        adviceScrollPane.setPreferredSize(new Dimension(width, 200));
        adviceScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        adviceScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        adviceScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        adviceScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        adviceScrollPane.setBorder(null);
        // TODO

        var editorPane = new GPanel(new BorderLayout());
        editorPane.setBackground(new Color(0xFDFEFE));
        editorPane.setPreferredSize(new Dimension(450, height));
        add(editorPane, BorderLayout.WEST);

        // 设置文本编辑器
        textEditor = new GTextPane();
        textEditor.setFont(GConfig.createFont(15));
        textEditor.setMinimumSize(new Dimension(0, 0));
        textEditor.setBackground(new Color(0xFBFCFC));
        textEditor.setEditorKit(new WrapEditorKit());
        textEditor.setBorder(new EmptyBorder(20, 20, 0, 0));

        var editorScrollPane = new GScrollPane(textEditor);
        editorScrollPane.setPreferredSize(new Dimension(450, 400));
        editorScrollPane.setBorder(null);
        editorScrollPane.setOpaque(false);
        editorScrollPane.setHorizontalScrollBarPolicy(GScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        editorScrollPane.setVerticalScrollBarPolicy(GScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));

        editorPane.add(editorScrollPane, BorderLayout.NORTH);
        editorPane.add(adviceScrollPane, BorderLayout.SOUTH);

        var previewPane = new GPanel(new BorderLayout());
        previewPane.setBackground(new Color(0xFDFEFE));
        previewPane.setPreferredSize(new Dimension(450, height));
        add(previewPane, BorderLayout.EAST);

        // 上面放关闭按钮

        var toolPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        toolPane.add(close);
        toolPane.setOpaque(false);
        previewPane.add(toolPane, BorderLayout.NORTH);
        // 中间是预览网页


        var webview = new GWebView();
        var webScrollPane = new GScrollPane(webview);
        webScrollPane.setPreferredSize(new Dimension(450, height));
        webScrollPane.setBorder(null);
        webScrollPane.setHorizontalScrollBarPolicy(GScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        webScrollPane.setVerticalScrollBarPolicy(GScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        webScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        webScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));

        var markdown = new MarkDownParser();

        textEditor.getStyledDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    parse(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    parse(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void parse(String text) {
                webview.setText(markdown.parse(text));
            }
        });

        previewPane.add(webScrollPane, BorderLayout.CENTER);
        // 下部是确认按钮，用于返回消息向上个窗体
        GPanel confirmPane = new GPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        var confirm = new GButton(confirmText);
        confirm.setNormalColor(new Color(0x626567));
        confirmPane.add(confirm);
        confirm.addActionListener(e-> listener.onEditorConfirm(textEditor.getText()));
        previewPane.add(confirm, BorderLayout.SOUTH);
        ////////////////////////////////////////////
        setVisible(true);
        addDragMoveFunction();
    }

    private int startX, startY;
    private void addDragMoveFunction() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int wx = getLocation().x;
                int wy = getLocation().y;
                setLocation(wx + e.getX() - startX, wy + e.getY() - startY);
            }
        });
    }

    private void addTips(List<MessageJson> list) {
        if (list == null) return;
        for (var i : list) {
            createTips(i);
        }
    }

    private int count = 0;
    private void createTips(MessageJson message) {
        var pane = new GPanel();
        pane.setOpaque(false);
        var textArea = new JTextArea();
        textArea.setFont(GConfig.createFont(13));
        textArea.setLineWrap(true);
        textArea.setPreferredSize(new Dimension(450, 60));
        textArea.setText("建议：" + message.text);
        textArea.setBackground(new Color(count++ % 2 == 1 ? 0xECF0F1 : 0xF0F3F4));
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(10, 10, 5, 5));
        pane.add(textArea);
        advicePane.add(pane);
    }
}
