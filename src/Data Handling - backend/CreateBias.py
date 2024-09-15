import random
import pandas as pd
import math

""" This is the driving force of the implementation - the creation of the bias. It will handle bias - set by the user with a strong 
level of freedom of choice as to exactly what they see and will control filter bubbles. 

Biases will be handled as follows:
Firstly, we will take 100 tweets from our dataset, which will allow us to accurately factor in the percentage of each ratio 
(sentiment, category bias). 

Sentiment algorithm - calculate the amount of tweets for each sentiment based on user preference (neutral will allow all tweets to be distributed evenly); 
depending on user's sentiment preference, (except neutral), between 35% and 60% of tweets will be of that sentiment (randomised by filter bubble control algorithm),
and the remaining amount will be even spread between the other two.

Category refactorisation algorithm - refactor the ratio of tweets by category user preference, again first calculate the amount of tweets
required for each category.

Finally, we apply filter all tweets in our dataset, sampling randomly the numbers we calculated for each category first, and then from
that returned dataframe doing the same for each sentiment, (i.e. filtering for the correct amount), and then concatenating that a final
df before writing it to the content table.
"""

from ConnectandReadDatabase import ConnectandReadDatabase

class CreateBias():
    #user taken from front end so we can take user bias preferences and apply them to tweets
    def __init__(self, UserDetails, TweetData, CountCategories):
        self.user = UserDetails

        #get all tweets
        self.tweet_data = TweetData.tweets_df
        self.category_count = CountCategories

        #this df will contain the bias applied tweet data 
        self.bias_applied_tweet_data = None

        #number of tweets to take from tweets_df for bias applied tweet data scaled down by around 30
        #this is the TOTAL number of tweets we will display on the content feed 
        self.no_applied_tweets = self.calculate_no_tweets()

        #run sentiment bias calculation - which will determine the number of tweets per sentiment
        self.no_positive_tweets = None
        self.no_negative_tweets = None
        self.no_neutral_tweets = None
        self.sentiment_variation = self.control_filter_bubbles_sentiments()


        self.sentiment_bias_calculator()

        #run category bias - which will determine the number of tweets per category
        self.no_sports_tweets = None
        self.no_politics_tweets = None
        self.no_entertainment_tweets = None
        self.no_other_tweets = None

        self.category_bias_calculator()

        #apply the bias
        self.apply_bias()

        #write results to content table
        self.write_bias_to_db()

    """ Total number of tweets presented to the user:
    Originally scaled down to fit the lowest category, but the most efficient method seems to be simply returning 100
    tweets, which fits directly to the percentage the user enters. This may be limited in terms of available category nums
    in the dataset, but will ensure that at least 5 sets of tweets for the lowest category are shown (min 500 for lowest category
    checked in TestBias)
    """
    def calculate_no_tweets(self):
        return 100
    
    """ Calculating Sentiment bias (number of each tweets for sentiment needed) """
    #apply the bias and filter bubble control to the user's preferred sentiment bias
    def sentiment_bias_calculator(self):
        #user has a preference for positive sentiment
        if self.user.sentiment_preference == "Positive":
            #select no. tweets based on randomised range - will always be more than 34% of tweets
            self.no_positive_tweets = round(self.sentiment_variation*self.no_applied_tweets)
            
            #the rest of the number of tweets will be the total number in the applied df - positive, and halved and rounded
            #rounded 
            self.no_negative_tweets = round(self.no_applied_tweets - self.no_positive_tweets)/2
            
            self.no_neutral_tweets = round(self.no_applied_tweets - self.no_positive_tweets)/2

        #user has a preference for negative sentiment
        elif self.user.sentiment_preference == "Negative":
            #select no. tweets based on randomised  range - will always be more than 34% of tweets
            self.no_negative_tweets = round(self.sentiment_variation*self.no_applied_tweets)
            
            #the rest of the number of tweets will be the total number in the applied df - positive, and halved and rounded
            #rounded 
            self.no_positive_tweets = round(self.no_applied_tweets - self.no_negative_tweets)/2
            
            self.no_neutral_tweets = round(self.no_applied_tweets - self.no_negative_tweets)/2

        #neutral case - user has a preference for neutral sentiment
        else:
            #amount of tweets to split sentiments into
            neutral_divisor = self.no_applied_tweets/3

            #apply equal amount of each, with no worry of filter bubbles so no random variation needed
            self.no_positive_tweets = round(neutral_divisor)
            self.no_negative_tweets = round(neutral_divisor)
            self.no_neutral_tweets = round(neutral_divisor)
    
    #generate the percentage of tweets which should conform to the user's preference of sentiment
    #will comprise between 35% and 60% of tweets (for positive or negative sentiment preference)
    def control_filter_bubbles_sentiments(self):
        #chosen sentiment
        self.upper_bound = 0.6
        self.lower_bound = 0.34

        #use uniform as we are working with floats
        ratio_of_tweets_for_sentiment = random.uniform(self.lower_bound, self.upper_bound)

        return ratio_of_tweets_for_sentiment
    
    #filter bubbles controlled by user warning on frontend
    def category_bias_calculator(self):
        #user category bias ratio preferences used here for calculation
        #divided by 100 to accurately take the ratio and apply it to 100 tweets
        user_sports_ratio =  self.user.sport_category_ratio/100
        user_politics_ratio = self.user.politics_category_ratio/100
        user_entertainment_ratio = self.user.entertainment_category_ratio/100
        user_other_ratio = self.user.other_category_ratio/100

        #calculate based on given ratio how many of the tweets should be of each category
        self.no_sports_tweets = int(user_sports_ratio*self.no_applied_tweets)
        self.no_politics_tweets = int(user_politics_ratio*self.no_applied_tweets)
        self.no_entertainment_tweets = int(user_entertainment_ratio*self.no_applied_tweets)
        self.no_other_tweets = int(user_other_ratio*self.no_applied_tweets)

    """ Apply bias - taking a random amount of tweets from the original tweet_df, conforming to bias numbers for both types of bias
    This gives us the final df that we need, and will be taken by the frontend to display content. We apply the category bias first, 
    filtering tweets by respective categories, sampling a random amount of each of those tweets and then
    finally applying the sentiment bias the same way, with replacement (duplicates) incase there are not enough tweets for
    a category of that sentiment at the given time."""
    def apply_bias(self):
        
        """Category bias application """
        #sample tweets into different dfs based on categories for the number of each that we need (calculated in self.no_x_tweets)
        sports_tweets = self.tweet_data[self.tweet_data["category"] == "Sports"].sample(n=self.no_sports_tweets, replace=True)
        politics_tweets = self.tweet_data[self.tweet_data["category"] == "Politics"].sample(n=self.no_politics_tweets, replace=True)
        entertainment_tweets = self.tweet_data[self.tweet_data["category"] == "Entertainment"].sample(n=self.no_entertainment_tweets, replace=True)
        other_tweets = self.tweet_data[self.tweet_data["category"] == "Other"].sample(n=self.no_other_tweets, replace=True)

        #combine the categorised tweet dfs to get the total number of categorised tweets we need (addition of all categorised tweets)
        combined_selected_tweets = pd.concat([sports_tweets, politics_tweets, entertainment_tweets, other_tweets])

        """Sentiment bias application - IMPORTANT TO NOTE: REPLACEMENT IS USED IN CASE THERE ARE NOT ENOUGH FILTERED TWEETS TO MATCH
        no_applied_tweets"""
        #sample tweets into different dfs based on sentiments for the number of each that we need (calculated in self.no_x_tweets)
        #make sure number of each sentiment is an integer, as it seems to have an error where it is not viewed as an int unless converted
        positive_tweets = combined_selected_tweets[combined_selected_tweets["sentiment"] == "positive"].sample(n=int(self.no_positive_tweets), replace=True)
        negative_tweets = combined_selected_tweets[combined_selected_tweets["sentiment"] == "negative"].sample(n=int(self.no_negative_tweets), replace=True)
        neutral_tweets = combined_selected_tweets[combined_selected_tweets["sentiment"] == "neutral"].sample(n=int(self.no_neutral_tweets), replace=True)

        #combine tweets after applying sentiment bias
        self.bias_applied_tweet_data = pd.concat([positive_tweets, negative_tweets, neutral_tweets])
            
    """ Finally, once bias applied, write the new values to the db (content table) to present the freshly bias applied content to the user"""
    def write_bias_to_db(self):
        with ConnectandReadDatabase(None) as database_connect:
            database_connect.write_to_content(self.bias_applied_tweet_data)