package com.ewask.image_similarity_maven;

public class Runner {

    public static void main(String[] args)
    {
        String path = "Images/";
        String imgName1 = "myszka1";
        String imgName2 = "myszka2";
        Image img1 = new Image(path, imgName1);
        Image img2 = new Image(path, imgName2);
        ImageSimilarity similarity = new ImageSimilarity(img1, img2);
//        similarity.countSimilarPoints(20, 0.1);
        similarity.countSimilarPoints(10, 100, 0.05, "Perspective");
        similarity.countSimilarPoints(10, 100, 0.05, "Affine");
    }
}
