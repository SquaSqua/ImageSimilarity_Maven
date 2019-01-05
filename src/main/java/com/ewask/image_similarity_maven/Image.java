package com.ewask.image_similarity_maven;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Image {
    private static final String keyPointExtension = ".png.haraff.sift";
    private String pathBase, imagineName;
    private String filePath;
    private ArrayList<KeyPoint> keyPoints;


    Image(String pathBase, String imageName) {
        this.pathBase = pathBase;
        this.imagineName = imageName;
        filePath = pathBase + imageName + ".png";
        String keyPointsFilePath = pathBase + "KeyPoints/" + imageName + keyPointExtension;
        keyPoints = createPoints(keyPointsFilePath);
    }

    private ArrayList<KeyPoint> createPoints(String keyPointsFile) {
        ArrayList<KeyPoint> keyPoints = new ArrayList<KeyPoint>();
        try {
            FileReader fileReader = new FileReader(keyPointsFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int numberOfFeatures = Integer.parseInt(bufferedReader.readLine());
            int numberOfRows = Integer.parseInt(bufferedReader.readLine());
            String currentLine = bufferedReader.readLine();
            for(int i = 0; i < numberOfRows && currentLine != null; i++) {
                keyPoints.add(new KeyPoint(currentLine, i));
                currentLine = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyPoints;
    }

    String getFilePath() {
        return filePath;
    }

    ArrayList<KeyPoint> getKeyPoints() {
        return keyPoints;
    }

    String getPathBase() {
        return pathBase;
    }

    String getImageName() {
        return imagineName;
    }
}
