package com.ewask.image_similarity_maven;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
class ImageDrawer {

    private static BufferedImage joinBufferedImages(BufferedImage img1, BufferedImage img2) {

        int wid = img1.getWidth() + img2.getWidth();
        int height = Math.max(img1.getHeight(),img2.getHeight());
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth(), 0);
        g2.dispose();
        return newImage;
    }

    private static BufferedImage drawKeyPoints(BufferedImage img, ArrayList<KeyPoint> keyPoints) {

        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        g2.drawImage(img, null, 0, 0);
        g2.setPaint(Color.CYAN);
        for (KeyPoint keyPoint : keyPoints) {
            g2.fillOval(keyPoint.x.intValue(), keyPoint.y.intValue(), 15,15);
        }

        g2.dispose();
        return newImage;
    }

    static void createMergedImageWithPoints(Image image1, Image image2) {
        try {
            BufferedImage img1 = ImageIO.read(new File(image1.getFilePath()));
            BufferedImage img2 = ImageIO.read(new File(image2.getFilePath()));
            BufferedImage colouredImg1 = drawKeyPoints(img1, image1.getKeyPoints());
            BufferedImage colouredImg2 = drawKeyPoints(img2, image2.getKeyPoints());
            BufferedImage joinedImg = joinBufferedImages(colouredImg1, colouredImg2);
            ImageIO.write(joinedImg, "png", new File(image1.getPathBase()
                    + "Results/" + image1.getImageName() + image2.getImageName() + "Points.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void createMergedImageWithLines(Image image1, Image image2, ArrayList<Pair> pairs, String fileName) {
        try {
            BufferedImage img1 = ImageIO.read(new File(image1.getFilePath()));
            BufferedImage img2 = ImageIO.read(new File(image2.getFilePath()));
            BufferedImage imageWithLines = drawPairsLines(joinBufferedImages(img1,img2), image1, pairs);
            ImageIO.write(imageWithLines, "png", new File(image1.getPathBase()
                    + "Results/" + image1.getImageName() + image2.getImageName() + fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static BufferedImage drawPairsLines(BufferedImage mergedImage, Image leftImage, ArrayList<Pair> pairs) {
        BufferedImage newImage = new BufferedImage(mergedImage.getWidth(), mergedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        try {
            BufferedImage leftImgBuffered = ImageIO.read(new File(leftImage.getFilePath()));
            Graphics2D g2 = newImage.createGraphics();
            g2.drawImage(mergedImage, null, 0, 0);
            for(Pair pair : pairs) {
                g2.setPaint(randomColor());
                g2.drawLine(pair.getKeyPoint1().x.intValue(), pair.getKeyPoint1().y.intValue(),
                        leftImgBuffered.getWidth() + pair.getKeyPoint2().x.intValue(), pair.getKeyPoint2().y.intValue());
            }
            g2.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImage;
    }

    private static Color randomColor()
    {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new Color(red, green, blue);
    }
}