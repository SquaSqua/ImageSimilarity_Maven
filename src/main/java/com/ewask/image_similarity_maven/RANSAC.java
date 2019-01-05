package com.ewask.image_similarity_maven;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public class RANSAC {
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
                {y3b},
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
        DMatrixRMaj result = new DMatrixRMaj();

        for(int row = 0; row < m1.numRows; row++) {
            for(int col = 0; col < m1.numCols; col++) {
                result.set(row, 0, result.get(row, 0) + (m1.get(row, col) * m2.get(col, 0)));
            }
        }

        return result;
    }

    private KeyPoint countPointWithAffineTransform(KeyPoint pointFromFirstImage, DMatrixRMaj affineTransform) {
        DMatrixRMaj matrixFromPoint = new DMatrixRMaj(new double[][] {{pointFromFirstImage.x}, {pointFromFirstImage.y}, {1}});
        DMatrixRMaj resultMatrix = multiplyMatrices(affineTransform, matrixFromPoint);
        return new KeyPoint(resultMatrix.get(0,0), resultMatrix.get(1,0));
    }
}
