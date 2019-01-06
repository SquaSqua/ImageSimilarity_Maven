package com.ewask.image_similarity_maven;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

import java.util.ArrayList;
import java.util.Random;

class RANSAC {

    private DMatrixRMaj findBestModel(int iterations, ArrayList<Pair> pairs, String transformName) {
        DMatrixRMaj bestModel = null;
        int bestScore = 0;
        for(int i = 0; i < iterations; i++) {
            DMatrixRMaj model = null;
            Random random = new Random();
            if(transformName.equals("Perspective")) {
                model =  derivePerspectiveTransform(
                        pairs.get(random.nextInt(pairs.size())),
                        pairs.get(random.nextInt(pairs.size())),
                        pairs.get(random.nextInt(pairs.size())),
                        pairs.get(random.nextInt(pairs.size()))
                );
            } else if(transformName.equals("Affine")) {
                model =  deriveAffineTransform(
                        pairs.get(random.nextInt(pairs.size())),
                        pairs.get(random.nextInt(pairs.size())),
                        pairs.get(random.nextInt(pairs.size()))
                );
            } else {
                System.out.println("Podaj rodzaj transformaty \"Affine\" lub \"Perspective\"");
                i--;
            }
            if(model != null) {
                int score = 0;
                for(Pair pair : pairs) {
                    KeyPoint counted = countPointFromAModel(pair.getKeyPoint1(), model);
                    double error = countError(counted, pair.getKeyPoint2());
                    if(error < ImageSimilarity.threshold) {
                        score++;
                    }
                }
                if(score > bestScore) {
                    bestModel = model;
                    bestScore = score;
                }
            }
        }
        return bestModel;
    }

    private DMatrixRMaj derivePerspectiveTransform(Pair pair1, Pair pair2, Pair pair3, Pair pair4) {

        double x1a = pair1.getKeyPoint1().x;
        double y1a = pair1.getKeyPoint1().y;

        double x2a = pair2.getKeyPoint1().x;
        double y2a = pair2.getKeyPoint1().y;

        double x3a = pair3.getKeyPoint1().x;
        double y3a = pair3.getKeyPoint1().y;

        double x4a = pair4.getKeyPoint1().x;
        double y4a = pair4.getKeyPoint1().y;

        double x1b = pair1.getKeyPoint2().x;
        double y1b = pair1.getKeyPoint2().y;

        double x2b = pair2.getKeyPoint2().x;
        double y2b = pair2.getKeyPoint2().y;

        double x3b = pair3.getKeyPoint2().x;
        double y3b = pair3.getKeyPoint2().y;

        double x4b = pair4.getKeyPoint2().x;
        double y4b = pair4.getKeyPoint2().y;

        double[][] pointsFromFirstImage = new double[][]{
                {x1a, y1a, 1, 0,   0,   0, -(x1b * x1a), -(x1b * y1a)},
                {x2a, y2a, 1, 0,   0,   0, -(x2b * x2a), -(x2b * y2a)},
                {x3a, y3a, 1, 0,   0,   0, -(x3b * x3a), -(x3b * y3a)},
                {x4a, y4a, 1, 0,   0,   0, -(x4b * x4a), -(x4b * y4a)},
                {0,   0,   0, x1a, y1a, 1, -(y1b * x1a), -(y1b * y1a)},
                {0,   0,   0, x2a, y2a, 1, -(y2b * x2a), -(y2b * y2a)},
                {0,   0,   0, x3a, y3a, 1, -(y3b * x3a), -(y3b * y3a)},
                {0,   0,   0, x4a, y4a, 1, -(y4b * x4a), -(y4b * y4a)}
        };
        DMatrixRMaj matrixOfFirsts = new DMatrixRMaj(pointsFromFirstImage);

        double[][] pointsFromSecondImage = new double[][]{
                {x1b},
                {x2b},
                {x3b},
                {x4b},
                {y1b},
                {y2b},
                {y3b},
                {y4b}
        };
        DMatrixRMaj matrixOfSecond = new DMatrixRMaj(pointsFromSecondImage);

        CommonOps_DDRM.invert(matrixOfFirsts);


        DMatrixRMaj resultMatrix = multiplyMatrices(matrixOfFirsts, matrixOfSecond);

        return new DMatrixRMaj(new double[][] {
                {resultMatrix.get(0,0), resultMatrix.get(1,0), resultMatrix.get(2,0)},
                {resultMatrix.get(3,0), resultMatrix.get(4,0), resultMatrix.get(5,0)},
                {resultMatrix.get(6,0), resultMatrix.get(7,0), 1}
        });
    }

    private DMatrixRMaj deriveAffineTransform(Pair pair1, Pair pair2, Pair pair3) {
        double x1a = pair1.getKeyPoint1().x;
        double y1a = pair1.getKeyPoint1().y;

        double x2a = pair2.getKeyPoint1().x;
        double y2a = pair2.getKeyPoint1().y;

        double x3a = pair3.getKeyPoint1().x;
        double y3a = pair3.getKeyPoint1().y;

        double x1b = pair1.getKeyPoint2().x;
        double y1b = pair1.getKeyPoint2().y;

        double x2b = pair2.getKeyPoint2().x;
        double y2b = pair2.getKeyPoint2().y;

        double x3b = pair3.getKeyPoint2().x;
        double y3b = pair3.getKeyPoint2().y;

        double[][] pointsFromFirstImage = new double[][]{
                {x1a, y1a, 1, 0,   0,   0},
                {x2a, y2a, 1, 0,   0,   0},
                {x3a, y3a, 1, 0,   0,   0},
                {0,   0,   0, x1a, y1a, 1},
                {0,   0,   0, x2a, y2a, 1},
                {0,   0,   0, x3a, y3a, 1}
        };
        DMatrixRMaj matrixOfFirsts = new DMatrixRMaj(pointsFromFirstImage);

        double[][] pointsFromSecondImage = new double[][]{
                {x1b},
                {x2b},
                {x3b},
                {y1b},
                {y2b},
                {y3b}
        };
        DMatrixRMaj matrixOfSecond = new DMatrixRMaj(pointsFromSecondImage);

        CommonOps_DDRM.invert(matrixOfFirsts);


        DMatrixRMaj resultMatrix = multiplyMatrices(matrixOfFirsts, matrixOfSecond);

        return new DMatrixRMaj(new double[][] {
                {resultMatrix.get(0,0), resultMatrix.get(1,0), resultMatrix.get(2,0)},
                {resultMatrix.get(3,0), resultMatrix.get(4,0), resultMatrix.get(5,0)},
                {0, 0, 1}
        });
    }

    private DMatrixRMaj multiplyMatrices(DMatrixRMaj m1, DMatrixRMaj m2) {
        DMatrixRMaj result = new DMatrixRMaj(m2.numRows, m2.numCols);
        for(int row = 0; row < m1.numRows; row++) {
            double partialResult = 0;
            for(int col = 0; col < m1.numCols; col++) {
                partialResult += m1.get(row, col) * m2.get(col, 0);
            }
            result.set(row, 0, partialResult);
        }

        return result;
    }

    private KeyPoint countPointFromAModel(KeyPoint pointFromFirstImage, DMatrixRMaj model) {
        DMatrixRMaj matrixFromPoint = new DMatrixRMaj(new double[][] {{pointFromFirstImage.x}, {pointFromFirstImage.y}, {1}});
        DMatrixRMaj resultMatrix = multiplyMatrices(model, matrixFromPoint);
        return new KeyPoint(resultMatrix.get(0,0) / resultMatrix.get(2,0),
                resultMatrix.get(1,0) / resultMatrix.get(2,0));
    }

    private double countError(KeyPoint point1, KeyPoint point2) {
        return Math.sqrt(Math.pow((point1.x - point2.x), 2) + Math.pow((point1.y - point2.y), 2));
    }

    ArrayList<Pair> choosePairsWithRANSAC(int iterations, ArrayList<Pair> pairs, String transformName) {
        DMatrixRMaj model = findBestModel(iterations, pairs, transformName);
        ArrayList<Pair> chosen = new ArrayList<Pair>();
        KeyPoint counted;
        for(Pair pair : pairs) {
            counted = countPointFromAModel(pair.getKeyPoint1(), model);
            double error = countError(counted, pair.getKeyPoint2());
            if(error < ImageSimilarity.threshold) {
                chosen.add(pair);
            }
        }
        return chosen;
    }
}
