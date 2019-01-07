package com.ewask.image_similarity_maven;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class ImageSimilarity {
    private Image img1, img2;
    private ArrayList<Pair> pairs = new ArrayList<Pair>();
    static double threshold;
    static double slip;

    ImageSimilarity(Image img1, Image img2) {
        this.img1 = img1;
        this.img2 = img2;
    }


    void countSimilarPoints(int neighbourhoodSize, double acceptancePercentage) {
        pairs.clear();
        checkSimilarity(neighbourhoodSize, acceptancePercentage);
    }

    void countSimilarPoints(int neighbourhoodSize, int iterations, double threshold, String transformName) {
        pairs.clear();
        try {
            BufferedImage image1 = ImageIO.read(new File(img1.getFilePath()));
            ImageSimilarity.threshold = threshold * image1.getWidth();
            slip = threshold;
        } catch (IOException e) {
            e.printStackTrace();
        }

        alwaysDo(neighbourhoodSize);
        RANSAC ransac = new RANSAC();
        ArrayList<Pair> chosenPairs = ransac.choosePairsWithRANSAC(iterations, pairs, transformName);
        ImageDrawer.createMergedImageWithLines(img1, img2, chosenPairs, "RANSAC" + transformName);
        System.out.println("RANSAC " + transformName + "; Liczba wybranych par: " + chosenPairs.size());
        System.out.println("Próg akceptowalnego błędu: " + ImageSimilarity.slip);
    }

    private void alwaysDo(int neighbourhoodSize) {
        ImageDrawer.createMergedImageWithPoints(img1, img2);
        findPairs();
        ImageDrawer.createMergedImageWithLines(img1, img2, pairs, "AllPairsLines");
        setNeighbourhood(img1, neighbourhoodSize);
        setNeighbourhood(img2, neighbourhoodSize);
        setPairsNeighbourhood();
    }

    private void checkSimilarity(int neighbourhoodSize, double acceptancePercentage) {
        alwaysDo(neighbourhoodSize);
        int acceptanceNumber = (int)(neighbourhoodSize * acceptancePercentage);
        ArrayList<Pair> chosenPairs = chooseConsistentNeighbours(acceptanceNumber);
        ImageDrawer.createMergedImageWithLines(img1, img2, chosenPairs, "ConsistentPairsLines");
    }

    private void setNeighbourhood(Image img, int neighbourhoodSize) {
        for(KeyPoint keyPoint : img.getKeyPoints()) {
            keyPoint.findNeighbourhood(img, neighbourhoodSize);
        }
    }

    private void setPairsNeighbourhood() {
        for(Pair pair : pairs) {
            pair.findNeighbouringPairs(pairs);
        }
    }

    private void findPairs() {
        for(int i = 0; i < img1.getKeyPoints().size(); i++) {
            KeyPoint matchForA = img1.getKeyPoints().get(i).findMatch(img2);
            KeyPoint matchForB = matchForA.findMatch(img1);
            if(matchForB == img1.getKeyPoints().get(i)) {
                pairs.add(new Pair(matchForB, matchForA));
            }
        }
        System.out.println("Liczba par: " + pairs.size());
    }

    private ArrayList<Pair> chooseConsistentNeighbours(int acceptanceNumber) {
        ArrayList<Pair> acceptedPairs = new ArrayList<Pair>();
        for(Pair pair : pairs) {
            if(pair.getNeighbouringPairs().size() >= acceptanceNumber) {
                acceptedPairs.add(pair);
            }
        }
        System.out.println("Liczba spójnych par: " + acceptedPairs.size());
        return acceptedPairs;
    }


}
