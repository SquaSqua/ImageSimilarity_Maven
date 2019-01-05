package com.ewask.image_similarity_maven;

import java.util.ArrayList;

class ImageSimilarity {
    private Image img1, img2;
    private ArrayList<Pair> pairs = new ArrayList<Pair>();

    ImageSimilarity(Image img1, Image img2) {
        this.img1 = img1;
        this.img2 = img2;
    }


    boolean checkSimilarity(int neighbourhoodSize, int acceptanceNumber) {
        boolean isSimilar = false;
        ImageDrawer drawer = new ImageDrawer();
        drawer.createMergedImageWithPoints(img1, img2);
        findPairs();
        drawer.createMergedImageWithLines(img1, img2, pairs, "AllPairsLines");
        setNeighbourhood(img1, neighbourhoodSize);
        setNeighbourhood(img2, neighbourhoodSize);
        setPairsNeighbourhood();
        pairs = chooseConsistentNeighbours(acceptanceNumber);
        drawer.createMergedImageWithLines(img1, img2, pairs, "ConsistentPairsLines");


        return isSimilar;
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
