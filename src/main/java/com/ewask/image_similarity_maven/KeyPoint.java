package com.ewask.image_similarity_maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class KeyPoint {
    private int index;
    Double x, y;
    private Double[] features;
    private ArrayList<KeyPoint> neighbourhood;

    KeyPoint(String line, int index) {
        this.index = index;
        neighbourhood = new ArrayList<KeyPoint>();
        String[] numbersInLine = line.split(" ");
        int numberOfFeatures = numbersInLine.length - 5;
        x = Double.parseDouble(numbersInLine[0]);
        y = Double.parseDouble(numbersInLine[1]);
        Double a = Double.parseDouble(numbersInLine[2]);
        Double b = Double.parseDouble(numbersInLine[3]);
        Double c = Double.parseDouble(numbersInLine[4]);

        features = new Double[numberOfFeatures];
        for(int i = 5; i < numbersInLine.length; i++) {
            features[i - 5] = Double.parseDouble(numbersInLine[i]);
        }
    }

    KeyPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    KeyPoint findMatch(Image imgFromSecond) {
        double minDifference = Double.MAX_VALUE;
        KeyPoint mostSimilar = null;
        for(KeyPoint keyPointFromSecond : imgFromSecond.getKeyPoints()) {
            double difference = countDifference(keyPointFromSecond);
            if(minDifference > difference) {
                minDifference = difference;
                mostSimilar = keyPointFromSecond;
            }
        }
        return mostSimilar;
    }

    private double countDifference(KeyPoint keyPoint2) {

        double sum = 0;
        for(int i = 0; i < features.length; i++) {
            sum += Math.pow(features[i] - keyPoint2.getFeatures()[i], 2);
        }
        return Math.sqrt(sum);
    }

    private Double[] getFeatures() {
        return features;
    }

    ArrayList<KeyPoint> getNeighbourhood() {
        return neighbourhood;
    }

    void findNeighbourhood(Image image, int neighbourhoodSize) {//to by się dało przez Collections.max z usuwaniem :/ ale trudno

        HashMap<Double, KeyPoint> neighbours = new HashMap<Double, KeyPoint>();
        double biggestDistanceInNeighbours = Double.MIN_VALUE;
        for(KeyPoint keyPoint : image.getKeyPoints()) {
            double distance = countDifference(keyPoint);
            if(distance > 0) {
                if(neighbours.size() < neighbourhoodSize) {
                    if(biggestDistanceInNeighbours < distance) {
                        biggestDistanceInNeighbours = distance;
                    }
                    neighbours.put(distance, keyPoint);
                }
                else {
                    if(biggestDistanceInNeighbours > distance) {
                        neighbours.remove(biggestDistanceInNeighbours);
                        if(!neighbours.containsKey(biggestDistanceInNeighbours)) {
                            biggestDistanceInNeighbours = Collections.max(neighbours.keySet());
                        }
                        neighbours.put(distance, keyPoint);

                    }
                }
            }
            neighbourhood.clear();
            neighbourhood.addAll(neighbours.values());
        }
    }
}
