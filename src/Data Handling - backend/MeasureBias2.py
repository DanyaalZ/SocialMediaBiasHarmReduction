import json

""" For measuring original and created biases as well as filter bubbles

Measurements will be handled as follows:
Original (completely fair and unbiased - verified by testing class) bias ratios will be recorded using the original dataframe before the bias
algorithm is applied

New (user biased) ratios will be recorded separately - using the dataframe with algorithmic bias applied to it, in the same manner as
the measurement of the original df

Filter bubbles - If the prompt was triggered in the frontend, the filter bubble "seal will be broken", and recorded as such (done in the frontend); upper and lower bounds
used for creating sentiment bias will also be displayed

These will all be compared and measured, and stored in a json file so they can be read by the frontend
at the end for display

SENTIMENT BIAS MEASUREMENTS ARE ROUNDED TO 2DP FOR BETTER READABILITY
CATEGORY BIAS MEASUREMENTS ARE FORMATTED TO 2DP FOR ACCURACY
 """

class MeasureBias2:
    def __init__(self, count_sentiments, count_categories, user_details, create_bias):
        #get classes from running backend
        self.count_sentiments = count_sentiments
        self.count_categories = count_categories
        self.user_details = user_details
        self.create_bias = create_bias

        #df of new tweets (with algorithm applied)
        self.new_df = create_bias.bias_applied_tweet_data

        #original total number of tweets from the dataset - stored in both category and sentiment count classes 
        #no relevance to class used here, just where the total count is stored for ease of access
        self.original_total_tweets = self.count_sentiments.total_tweets

        """sentiment bias measurement variables"""
        #original sentiment ratios AKA GROUND TRUTHS
        #2dp rounded - seemed to have most readable results
        self.original_sentiment_bias_positive_ratio = round(self.get_original_sentiment_bias("positive"),2)
        self.original_sentiment_bias_negative_ratio = round(self.get_original_sentiment_bias("negative"),2)
        self.original_sentiment_bias_neutral_ratio = round(self.get_original_sentiment_bias("neutral"),2)

        #new sentiment ratios - taking the number of each sentiment directly from the new applied df, and then calculating
        #their numbers divided by the total number of tweets in that run of the algorithmic bias
        #2dp rounded
        self.new_sentiment_bias_positive_ratio = round(self.get_new_ratio(len(self.new_df[self.new_df["sentiment"] == "positive"])),2)
        self.new_sentiment_bias_negative_ratio = round(self.get_new_ratio(len(self.new_df[self.new_df["sentiment"] == "negative"])),2)
        self.new_sentiment_bias_neutral_ratio = round(self.get_new_ratio(len(self.new_df[self.new_df["sentiment"] == "neutral"])),2)
    
        """category bias measurement variables"""

        #original category ratios AKA GROUND TRUTHS
        #formatted (not rounded) to 2dp - seemed to have most accurate results
        self.original_category_ratio_sports = "{0:.2f}".format(self.get_original_category_biases("Sports"))
        self.original_category_ratio_entertainment = "{0:.2f}".format(self.get_original_category_biases("Entertainment"))
        self.original_category_ratio_politics = "{0:.2f}".format(self.get_original_category_biases("Politics"))
        self.original_category_ratio_other = "{0:.2f}".format(self.get_original_category_biases("Other"))
    

        #new category ratios (stored in user details class)
        self.new_category_ratio_sports = 0
        self.new_category_raio_politics = 0 
        self.new_category_ratio_entertainment = 0
        self.new_category_ratio_other = 0

        #we get these from the final dataset, which may not have some values there if the user has set them to zero, so
        #check each to make sure user has not set them to 0 (in which case we set it to 0 as we don't want values which don't exist)

        #a 'binary' map of all 0s set (if there is a value to be stored it will just be stored as 1 here)
        #default to 0, check if user has anything above set for each ratio, and then accordingly get new ratios for thoses
        binary_map = {"Sports": 0 if self.user_details.sport_category_ratio == 0 else 1, 
                      "Politics": 0 if self.user_details.politics_category_ratio == 0 else 1, 
                      "Entertainment": 0 if self.user_details.entertainment_category_ratio == 0 else 1, 
                      "Other": 0 if self.user_details.other_category_ratio == 0 else 1}

        #calculate the new ratio for categories which are above 0, all formatted to 2dp
        for category, bin_number in binary_map.items():
            if bin_number == 1:
                if category == "Sports":
                     self.new_category_ratio_sports = "{0:.2f}".format(self.get_new_ratio(len(self.new_df[self.new_df["category"] == "Sports"])))

                elif category == "Politics":
                     self.new_category_raio_politics = "{0:.2f}".format(self.get_new_ratio(len(self.new_df[self.new_df["category"] == "Politics"])))

                elif category == "Entertainment":
                    self.new_category_ratio_entertainment = "{0:.2f}".format(self.get_new_ratio(len(self.new_df[self.new_df["category"] == "Entertainment"])))
                #other
                else:
                    self.new_category_ratio_other = "{0:.2f}".format(self.get_new_ratio(len(self.new_df[self.new_df["category"] == "Other"])))


        """filter bubble measurements"""
        
        #filter bubble seal - did the user select any category to comprise above 45% of total tweets?
        self.filter_bubble_seal = self.user_details.filter_bubble_seal

        #sentiment variation count - what percentage of tweets conformed to the user's sentiment preference?
        #default 0 (assuming neutral is selected, otherwise set to the variation percentage created in CreateBias)
        self.sentiment_variation = 0
        if self.user_details.sentiment_preference != "Neutral":
            self.sentiment_variation = "{0:.2f}".format(self.create_bias.sentiment_variation)

        """Write to json"""
        #write all measurements to json file
        self.write_results_to_json()

    """ Get new bias ratios for sentiments and categories """
    #get new ratios - takes as argument the count of type of bias we are measuring (e.g. sports category)
    #returns ratio of that tweet compared to the new bias applied dataset
    def get_new_ratio(self, new_bias_count):
      biased_tweets_number = len(self.new_df)
      return new_bias_count/biased_tweets_number

    """ Original  biases for sentiment bias preferences """
    #get original sentiment biases from CountSentiments class (unbiased and tested)
    def get_original_sentiment_bias(self, sentiment):
        #ground truths for bias measurements (to compare with)
        #sentiment original ratios
        self.count_sentiments.set_sentiment_type(sentiment)
        return self.count_sentiments.get_ratio()
    
    """ Original  biases for category bias preferences """
    #get original category bias ratios from CountCategories class (unbiased and tested)
    def get_original_category_biases(self, category):
        #ground truths for bias measurements (to compare with)
        #category original ratios
        self.count_categories.set_category_type(category)
        return self.count_categories.get_ratio()
       

    #once all measurements calculated - written to json for frontend to display
    def write_results_to_json(self): 
        #nested dictionaries used in order to clearly show type of measurement

        #filter bubble measurements - with file name to write
        filter_bubble_measurements = {"Filter Bubble Measurements" : {"Sentiment Variation (percentage of tweets following sentiment preference, 0 if neutral)": self.sentiment_variation, "Filter Bubble Seal Status (category ratio above 45%)": self.filter_bubble_seal}}
        filter_file_name = "filter_bubble_measurements"

        #sentiment ratio measurements - with file name to write
        positive_sentiment_measurements = {"Positive Sentiment Ratio Measurements - ": {"Original Positive: ": self.original_sentiment_bias_positive_ratio, "New bias ratio: ": self.new_sentiment_bias_positive_ratio}}
        positive_file_name = "positive_sentiment_measurements"

        negative_sentiment_measurements = {"Negative Sentiment Ratio Measurements - ": {"Original Negative: ": self.original_sentiment_bias_negative_ratio, "New bias ratio: ": self.new_sentiment_bias_negative_ratio}}
        negative_file_name = "negative_sentiment_measurements"

        neutral_sentiment_measurements = {"Neutral Sentiment Ratio Measurements - ": {"Original Neutral: ": self.original_sentiment_bias_neutral_ratio, "New bias ratio: ": self.new_sentiment_bias_neutral_ratio}}
        neutral_file_name = "neutral_sentiment_measurements"

        #category ratio measurements - with file name to write
        sports_category_measurements = {"Sport Category Ratio Measurements - ": {"Original Sports: ": self.original_category_ratio_sports, "New bias ratio: ": self.new_category_ratio_sports}}
        sports_file_name = "sports_category_measurements"

        entertainment_category_measurements = {"Entertainment Category Ratio Measurements - ": {"Original Entertainment: ": self.original_category_ratio_entertainment, "New bias ratio: ": self.new_category_ratio_entertainment}}
        entertainment_file_name = "entertainment_category_measurements"

        politics_category_measurements = {"Politics Category Ratio Measurements - ": {"Original Politics: ": self.original_category_ratio_politics, "New bias ratio: ": self.new_category_raio_politics}}
        politics_file_name = "politics_category_measurements"

        other_category_measurements = {"Other Category Ratio Measurements - ": {"Original Other: ": self.original_category_ratio_other, "New bias ratio: ": self.new_category_ratio_other}}
        other_file_name = "other_category_measurements"     

        files_to_write = [(filter_bubble_measurements, filter_file_name), (positive_sentiment_measurements, positive_file_name), (negative_sentiment_measurements, negative_file_name), (neutral_sentiment_measurements, neutral_file_name), (sports_category_measurements, sports_file_name), (entertainment_category_measurements, entertainment_file_name), (politics_category_measurements, politics_file_name), (other_category_measurements, other_file_name)]

        #write all measurements to separate json files
        for file, file_name in files_to_write:
            with open(f"{file_name}.json", "w") as write:
                json.dump(file, write)

   