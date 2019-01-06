package com.ewask.image_similarity_maven;

import java.util.ArrayList;

class ImageSimilarity {
    private Image img1, img2;
    private ArrayList<Pair> pairs = new ArrayList<Pair>();
    static double threshold;

    ImageSimilarity(Image img1, Image img2) {
        this.img1 = img1;
        this.img2 = img2;
    }


    int countSimilarPoints(int neighbourhoodSize, double acceptancePercentage) {
        return checkSimilarity(neighbourhoodSize, acceptancePercentage);
    }

    int countSimilarPoints(int neighbourhoodSize, int iterations, double threshold, String transformName) {
        ImageSimilarity.threshold = threshold;
        alwaysDo(neighbourhoodSize);
        RANSAC ransac = new RANSAC();
        ArrayList<Pair> chosenPairs = ransac.choosePairsWithRANSAC(iterations, pairs, transformName);
        ImageDrawer.createMergedImageWithLines(img1, img2, chosenPairs, "RANSAC");
        return chosenPairs.size();
    }

    private void alwaysDo(int neighbourhoodSize) {
        ImageDrawer.createMergedImageWithPoints(img1, img2);
        findPairs();
        ImageDrawer.createMergedImageWithLines(img1, img2, pairs, "AllPairsLines");
        setNeighbourhood(img1, neighbourhoodSize);
        setNeighbourhood(img2, neighbourhoodSize);
        setPairsNeighbourhood();
    }

    private int checkSimilarity(int neighbourhoodSize, double acceptancePercentage) {
        alwaysDo(neighbourhoodSize);
        int acceptanceNumber = (int)(neighbourhoodSize * acceptancePercentage);
        ArrayList<Pair> chosenPairs = chooseConsistentNeighbours(acceptanceNumber);
        ImageDrawer.createMergedImageWithLines(img1, img2, chosenPairs, "ConsistentPairsLines");

        return pairs.size();
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
    }

    private ArrayList<Pair> chooseConsistentNeighbours(int acceptanceNumber) {
        ArrayList<Pair> acceptedPairs = new ArrayList<Pair>();
        for(Pair pair : pairs) {
            if(pair.getNeighbouringPairs().size() >= acceptanceNumber) {
                acceptedPairs.add(pair);
            }
        }
        return acceptedPairs;
    }


}
