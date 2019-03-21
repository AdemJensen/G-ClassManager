package top.grapedge.ui.component;

import top.grapedge.ui.base.util.RendererUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @program: G-Chat
 * @description: 低多边形控件
 * @author: Grapes
 * @create: 2019-01-25 19:14
 **/
public class GLowPoly extends JPanel implements ActionListener {

    private int cWidth, cHeight, triWidth, triHeight;

    private Vertex points[][];

    private TColor colors[][];

    private Vector2D light, lightSpeed = new Vector2D((int) (Math.random() * 5+1), (int) (Math.random() * 2+1));

    private int interval = 16;
    private int time = 0;

    public GLowPoly(int width, int height, int triWidth, int triHeight) {
        this.cWidth = width;
        this.cHeight = height;
        this.triWidth = triWidth;
        this.triHeight = triHeight;
        // 初始化网格生成
        initView();
        light = new Vector2D((int)(Math.random() * cWidth), (int)(Math.random() * cHeight));
        // 动画更新
        new Timer(interval, this).start();
    }

    private void initView() {
        int row = cHeight / triHeight + 5, col = cWidth / triWidth + 5;
        points = new Vertex[row][col];
        colors = new TColor[row][col];
        // 初始化各个点
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                points[i][j] = new Vertex(new Vector2D(j * triWidth - triWidth * 3 + (int)(Math.random() * triWidth / 3), i * triHeight - triHeight * 2 + (int)(Math.random() * triHeight / 3)));
                colors[i][j] = new TColor();
            }
        }
    }

    private void setBright() {
        for (Vertex[] point : points) {
            for (Vertex aPoint : point) {
                aPoint.light = 1.0 - (Math.sqrt((light.x - aPoint.point.x) * (light.x - aPoint.point.x) +
                        (light.y - aPoint.point.y) * (light.y - aPoint.point.y)) / 100.0);

                if (aPoint.light < 0.0) aPoint.light = 0.0;
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(cWidth, cHeight);
    }

    private double sin01(int t, int T) {
        return Math.abs(Math.sin(t * (2 * Math.PI) / T));
    }

    // 移动一个点
    private void movePoint(Vertex vertex) {
        Vector2D ori = vertex.original;
        Vector2D tar = vertex.target;
        vertex.point = new Vector2D((int) (ori.x + (tar.x - ori.x) * sin01(time + vertex.moveTime, 30000)), ori.y + (int)((tar.y - ori.y) * sin01(time + vertex.moveTime, 30000)));
    }

    // 移动一个点
    private void moveColor(TColor tc, double light) {
        tc.up = moveColor(tc.oriUp, tc.tarUp, tc.moveTime + time, light);
        tc.down = moveColor(tc.oriDown, tc.tarDown, tc.moveTime + time, light);
    }

    private Color moveColor(Color ori, Color tar, int t, double light) {
        int or = ori.getRed(), tr = tar.getRed();
        double per = sin01(t, 10000);
        double r = (or + (tr - or) * per);
        if (light > 0) {
            double add = (130 - r) * light;
            if (r != 0) {
                r += add;
            }
        }
        return new Color((int)r, (int)r, (int)r);
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        RendererUtil.applyQualityRenderingHints(g2d);

        for (var i : points) {
            for (var v : i) {
                movePoint(v);
            }
        }

        light.x += lightSpeed.x;
        light.y += lightSpeed.y;
        setBright();
        if (light.x > cWidth) {
            light.x = cWidth;
            lightSpeed.x = -(int)(Math.random() * 5 + 1);
        }
        if (light.x < 0) {
            light.x = 0;
            lightSpeed.x = (int)(Math.random() * 5 + 1);
        }
        if (light.y > cHeight) {
            light.y = cHeight;
            lightSpeed.y = -(int)(Math.random() * 2 + 1);
        }
        if (light.y < 0) {
            light.y = 0;
            lightSpeed.y = (int)(Math.random() * 2 + 1);
        }
        //System.out.println(light.x + "," + light.y);
        for (int i = 0; i < colors.length - 1; i++) {
            for (int j = 0; j < colors[i].length - 1; j++) {
                moveColor(colors[i][j], points[i][j].light);
            }
        }

        for (int i = 0; i < points.length - 1; i++) {
            for (int j = 0; j < points[i].length - 1; j++) {
                drawTriangle(points[i][j], points[i][j + 1], points[i + 1][j], colors[i][j].up, g2d);
                drawTriangle(points[i][j + 1], points[i + 1][j], points[i + 1][j + 1], colors[i][j].down, g2d);
            }
        }

    }

    // 绘制三角形
    private void drawTriangle(Vertex a, Vertex b, Vertex c, Color color, Graphics g) {
        int x[] = {a.point.x, b.point.x, c.point.x };
        int y[] = {a.point.y, b.point.y, c.point.y };
        g.setColor(color);
        g.drawPolygon(x, y, 3);
        g.fillPolygon(x, y, 3);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 更新时间
        time += interval;
        if (getParent() != null) {
            getParent().repaint();
        } else {
            repaint();
        }
    }

    // 用于记录各个点的信息
    private class Vector2D {
        int x;
        int y;

        Vector2D(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Vertex {
        Vector2D point;
        Vector2D original;
        Vector2D target;
        double light = 0f;
        int moveTime = (int) (Math.random() * 15000);

        Vertex(Vector2D pos) {
            this.point = pos;
            this.original = pos;
            this.target = new Vector2D(pos.x + (int) (Math.random() * triWidth * 1.7),  pos.y + (int)(Math.random() * triHeight * 0.15));
        }
    }

    private class TColor {
        Color up, down;
        Color oriUp, oriDown;
        Color tarUp, tarDown;
        int moveTime = (int) (Math.random() * 15000);
        TColor() {
            this.up = getRandomColor();
            this.down = getRandomColor();
            this.oriUp = up;
            this.oriDown = down;
            this.tarUp = getRandomRGBLight();
            this.tarDown = getRandomRGBLight();
        }

        private Color getRandomColor() {
            var randomNum = Math.random();
            randomNum = randomNum / 2 + 0.5;
            var r = (int)(randomNum * 50);
            return new Color(r, r, r);
        }

        private Color getRandomRGBLight() {
            var randomNum = Math.random();
            randomNum = randomNum / 2 + 0.5;
            var r = 40 + (int)(randomNum * 30);
            return new Color(r, r, r);
        }
    }


}
