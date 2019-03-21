package top.grapedge.ui.base.util;

import com.jhlabs.image.GaussianFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @program: G-ClassManager
 * @description: 图片
 * @author: Grapes
 * @create: 2019-03-05 18:38
 **/
public class ImageUtil {
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static ImageIcon getRoundImageIcon(Image img) {
        try {
            BufferedImage master = toBufferedImage(img);
            int diameter = Math.min(master.getWidth(), master.getHeight());

            // 创建一个圆形遮罩
            BufferedImage mask = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = mask.createGraphics();
            RendererUtil.applyQualityRenderingHints(g2d);
            g2d.fillOval(0, 0, diameter - 1, diameter - 1);
            g2d.dispose();

            // 开始绘制
            BufferedImage masked = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            g2d = masked.createGraphics();
            RendererUtil.applyQualityRenderingHints(g2d);

            int x = (diameter - master.getWidth()) / 2;
            int y = (diameter - master.getHeight()) / 2;

            g2d.drawImage(master, x, y, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
            g2d.drawImage(mask, 0, 0, null);
            g2d.dispose();
            return new ImageIcon(masked);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage createCompatibleImage(int width, int height) {
        return createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    public static BufferedImage createCompatibleImage(int width, int height, int transparency) {
        BufferedImage image = RendererUtil.getGraphicsConfiguration().createCompatibleImage(width, height, transparency);
        image.coerceData(true);
        return image;
    }

    public static BufferedImage createCompatibleImage(BufferedImage image,
                                                      int width, int height) {
        return RendererUtil.getGraphicsConfiguration().createCompatibleImage(width, height, image.getTransparency());
    }

    public static BufferedImage createCompatibleImage(BufferedImage image) {
        return createCompatibleImage(image, image.getWidth(), image.getHeight());
    }

    public static BufferedImage generateBlur(BufferedImage imgSource, int size, Color color, float alpha) {
        GaussianFilter filter = new GaussianFilter(size);

        int imgWidth = imgSource.getWidth();
        int imgHeight = imgSource.getHeight();

        BufferedImage imgBlur = createCompatibleImage(imgWidth, imgHeight);
        Graphics2D g2 = imgBlur.createGraphics();
        RendererUtil.applyQualityRenderingHints(g2);

        g2.drawImage(imgSource, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
        g2.setColor(color);

        g2.fillRect(0, 0, imgSource.getWidth(), imgSource.getHeight());
        g2.dispose();

        imgBlur = filter.filter(imgBlur, null);

        return imgBlur;
    }

    public static BufferedImage generateShadow(BufferedImage imgSource, int size, Color color, float alpha) {
        int imgWidth = imgSource.getWidth() + (size * 2);
        int imgHeight = imgSource.getHeight() + (size * 2);

        BufferedImage imgMask = createCompatibleImage(imgWidth, imgHeight);
        Graphics2D g2 = imgMask.createGraphics();
        RendererUtil.applyQualityRenderingHints(g2);

        int x = Math.round((imgWidth - imgSource.getWidth()) / 2f);
        int y = Math.round((imgHeight - imgSource.getHeight()) / 2f);
        g2.drawImage(imgSource, x, y, null);
        g2.dispose();

        // ---- Blur here ---
        BufferedImage imgGlow = generateBlur(imgMask, (size * 2), color, alpha);

        return imgGlow;
    }

    public static void scaleImage(ImageIcon icon, int maxSize) {
        if (icon.getIconHeight() > maxSize || icon.getIconWidth() > maxSize) {
            Dimension d;
            if (icon.getIconWidth() > icon.getIconHeight()) {
                double per = (double)maxSize / icon.getIconWidth();
                var height = per * icon.getIconHeight();
                d = new Dimension(maxSize, (int)height);
            } else {
                double per = (double)maxSize / icon.getIconHeight();
                var width = per * icon.getIconWidth();
                d = new Dimension((int)width, maxSize);
            }
            icon.setImage(icon.getImage().getScaledInstance(d.width, d.height, Image.SCALE_DEFAULT));
        }
    }
}
