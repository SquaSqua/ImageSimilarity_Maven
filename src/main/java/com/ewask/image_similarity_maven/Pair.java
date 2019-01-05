package com.ewask.image_similarity_maven;

import java.util.ArrayList;

class Pair {
    private KeyPoint keyPoint1;
    private KeyPoint keyPoint2;
    private ArrayList<Pair> neighbouringPairs;

    Pair(KeyPoint keyPoint1, KeyPoint keyPoint2) {
        this.keyPoint1 = keyPoint1;
        this.keyPoint2 = keyPoint2;
        neighbouringPairs = new ArrayList<Pair>();
    }

    void findNeighbouringPairs(ArrayList<Pair> allPairs) {
        for(Pair pair : allPairs) {
            if(this.keyPoint1.getNeighbourhood().contains(pair.getKeyPoint1())
                    && this.keyPoint2.getNeighbourhood().contains(pair.getKeyPoint2())) {
                neighbouringPairs.add(pair);
            }
        }
    }

    KeyPoint getKeyPoint1() {
        return keyPoint1;
    }

    KeyPoint getKeyPoint2() {
        return keyPoint2;
    }

    ArrayList<Pair> getNeighbouringPairs() {
        return neighbouringPairs;
    }
}
