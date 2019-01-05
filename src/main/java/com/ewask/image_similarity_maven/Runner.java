package com.ewask.image_similarity_maven;

public class Runner {

    public static void main(String[] args)
    {
        String path = "C:/Users/User/IdeaProjects/ImageSimilarity/Images/";
        String imgName1 = "myszka1";
        String imgName2 = "myszka2";
        Image img1 = new Image(path, imgName1);
        Image img2 = new Image(path, imgName2);
        ImageSimilarity similarity = new ImageSimilarity(img1, img2);
        if(similarity.checkSimilarity(10, 2)) {
            System.out.println("Obrazy są podobne");
        }
        else {
            System.out.println("Obrazy są różne");
        }
    }
}
