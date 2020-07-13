package utilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//le ce218 sample code has arrived (Provided by Dr Dimitri Ognibene)

//edited slightly by me

public class ImageManager {

    // this may need modifying
    public final static String path = "/ThingsThatAreNotCode/images/";
    public final static String ext = ".png";

    public static Map<String, Image> images = new HashMap<>();

    public static Image getImage(String s) { return images.get(s); }

    public static Image loadImage(String fname) throws IOException {
        BufferedImage img = loadBufferedImage(fname);
        images.put(fname, img);
        return img;
    }

    //easier than using loadImage to just cast the Image back to a BufferedImage, y'know?
    public static BufferedImage loadBufferedImage(String fname) throws IOException{
        //return ImageIO.read(new File(path + fname + ext));
        return ImageIO.read(ImageManager.class.getResource(path + fname + ext));
        //Thank you, JB Nizet! (https://stackoverflow.com/a/8362018)
    }

    public static Image loadImage(String imName, String fname) throws IOException {
        BufferedImage img = loadBufferedImage(fname);
        images.put(imName, img);
        return img;
    }

    public static void loadImages(String[] fNames) throws IOException {
        for (String s : fNames) {
            loadImage(s);
        }
    }

    public static void loadImages(Iterable<String> fNames) throws IOException {
        for (String s : fNames) {
            loadImage(s);
        }
    }

}