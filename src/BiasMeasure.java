/* This class entails the measure and display of biases ascertained in other aspects of the implementation, including user category 
 * preferences and sentiment preferences - these are displayed as numerical values for the user, and are one of the greatest and most
 * crucial aspects of the implementation - allowing us to infer as to whether it has successfully achieved its aim of bias mitigation 
 * or not, based upon a "ground truth" (an initial value we can compare the bias with);
 * Filter bubble measurement, an integral part of the implementation, will also be shown here.
 * 
 * Measures are taken from backend (initialised in MeasureBias.py), read from the output json files (using org.json external library)
 */

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BiasMeasure extends GUI
{
    private User user;
    private DatabaseConnection db = new DatabaseConnection();

    //window for bias measures, size 1500x400 using (inheriting) class GUI
    private JFrame window = createWindow("Bias Measures", 1550, 400);
    private JPanel hold;

    //display text for biases - can be updated

    //categories
    private JLabel sportsBiasText;
    private JLabel politicsBiasText;
    private JLabel entertainmentBiasText;
    private JLabel otherBiasText;

    //filter bubble statuses (for sentiment - variation percentage, category - filter bubble seal)
    private JLabel filterBubbleText;

    //sentiments
    private JLabel positiveBiasText;
    private JLabel negativeBiasText;
    private JLabel neutralBiasText;

    public BiasMeasure(User user) 
    {
        this.user = user;
    }

    //to get the jsons we need (output from measurebias.py) from the backend folder as strings
    private String getJsonToString(String path)
    {
        try 
	    {
            //file will be small, so a bytearray will work efficiently
            String jsonString = new String(Files.readAllBytes(Paths.get(path)));
            return jsonString;
        } 

        catch (Exception e) 
	    {
            e.printStackTrace();
            return null;
        }
    }

    /*get measures for display, depending on input (category, sentiment), will fetch the given json file as a string*/

    private String getCategoryMeasure(String categoryType)
    {
        switch(categoryType)
        {
            case "Sports":
                return getJsonToString("Data Handling - backend/sports_category_measurements.json");
            
            case "Politics":
                return getJsonToString("Data Handling - backend/politics_category_measurements.json");
            
            case "Entertainment":
                return getJsonToString("Data Handling - backend/entertainment_category_measurements.json");
            
            case "Other":
                return getJsonToString("Data Handling - backend/other_category_measurements.json");
        }
        return null;
    }

    private String getSentimentMeasure(String sentimentType)
    {
        switch(sentimentType)
        {
            case "Positive":
                return getJsonToString("Data Handling - backend/positive_sentiment_measurements.json");
            
            case "Negative":
                return getJsonToString("Data Handling - backend/negative_sentiment_measurements.json");
            
            case "Neutral":
                return getJsonToString("Data Handling - backend/neutral_sentiment_measurements.json");
        }
        return null;
    }

    //for filter bubble measurements (sentiment variation and filter bubble seal)
    private String getFilterBubbleMeasure()
    {
        return getJsonToString("Data Handling - backend/filter_bubble_measurements.json");
    }

    //display window for biases
    public void biasWindow()
    {
        this.hold = createPanel(this.window);
        //display category bias measures
        addText(window, hold, "Category bias measures:", 5, 0, 20);
        this.sportsBiasText = addText(window, hold, this.getCategoryMeasure("Sports"), 10, 20, 15);
        this.politicsBiasText = addText(window, hold, this.getCategoryMeasure("Politics"), 10, 50, 15);
        this.entertainmentBiasText = addText(window, hold, this.getCategoryMeasure("Entertainment"), 10, 80, 15);
        this.otherBiasText = addText(window, hold, this.getCategoryMeasure("Other"), 10, 110, 15);

        //display sentiment bias measures
        addText(window, hold, "Sentiment bias measures:", 5, 170, 20);
        this.positiveBiasText = addText(this.window, hold, this.getSentimentMeasure("Positive"), 10, 190, 15);
        this.positiveBiasText = addText(this.window, hold, this.getSentimentMeasure("Negative"), 10, 220, 15);
        this.positiveBiasText = addText(this.window, hold, this.getSentimentMeasure("Neutral"), 10, 250, 15);

        //filter bubble measures
        addText(window, hold, "Filter bubble measures:", 5, 280, 17);
        this.filterBubbleText = addText(this.window, hold, this.getFilterBubbleMeasure(), 10, 300, 15);

        showWindow(this.window, hold);
    }
}
