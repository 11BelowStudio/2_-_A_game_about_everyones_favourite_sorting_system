package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TextAssetReader {

    final static String path = "/ThingsThatAreNotCode/text/";
    final static String extension = ".txt";

    private final static ArrayList<String> OPENING_TEXT = fileToStringArrayList("IntroCrawlText");
    private final static ArrayList<String> CREDITS_TEXT = fileToStringArrayList("credits");

    private static ArrayList<String> fileToStringArrayList(String filename){

        //if it wasn't obvious, yes, this was repurposed from the HighScoreHandler
        ArrayList<String> output = new ArrayList<>();
        try{
            InputStream in = TextAssetReader.class.getResourceAsStream(path + filename + extension);
            //Thanks, Drew MacInnis! (https://stackoverflow.com/a/20389418)
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String currentString;
            //pretty much setting up the stuff for reading the file
            //until the end of the file is reached, it will add the current string to the file (even the empty ones)
            while ((currentString = br.readLine())!=null) {
                output.add(currentString);
            }
            br.close(); //closes the bufferedReader
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static ArrayList<String> getOpeningText() {return OPENING_TEXT;}
    public static ArrayList<String> getCreditsText() {return CREDITS_TEXT;}
}
