import sys

from Requirements import Requirements
#check requirements.txt and install needed packages if necessary
requirements = Requirements()     


from HandleData import TweetData 
from TestData import RunTest
from CreateBias import CreateBias
from MeasureBias2 import MeasureBias2
from CountSentimentsandCategories import CountSentiments, CountCategories
from UserDetails import UserDetails



"""Culmination of all included algorithms, run in succession to each other to form one backend - takes user as a string
argument so that java can parse the current user in for the database to work with.

The main aim is to take a number of tweets to display to the user in their content feed in the frontend, with their bias preferences
applied and filter bubbles dodged as much as possible. It also provides measurements for the user to check bias level in the frontend."""

#this should be the main run python file for the backend:
#get username variable from java frontend for sql queries 
if __name__ == "__main__":
    if len(sys.argv) > 1:
        username = sys.argv[1]

#user object to store user details (for database usage and calculations)
user = UserDetails(username)

#handle the data - this validates and cleans the dataset so we can sample tweets and create a level of bias based on user preference
#default path of data assumed to be Tweets.csv
#it returns a dataframe called tweets_df, and categorises the tweets into four categories - sports, politics, entertainment, other
#it contains all the tweets from the original provided dataset, and will have its number reduced later in a final dataset, which
handle = TweetData("Data/Tweets.csv")

#maintain one instance of sentiment and category ratio count through the backend, to be used for various purposes such as
#for testing and measurement - count the original number of tweets with different sentiments and categories so we can work out how many to use
#also allows us to check suitability of dataset (when we run tests)
count_sentiment = CountSentiments(handle)
count_category = CountCategories(handle)

#test sentiments to ensure dataset is balanced enough to use for the bias algorithm (a balanced ratio of sentiments and categories)
#this class also uses the CountSentiments and Categories class in order to ascertain the ratio of each sentiment and category to test suitability
#if tests fail the backend cannot continue and will exit
test = RunTest(count_sentiment, count_category, handle)

#create biases - takes username from frontend to ascertain user bias preferences to create a user-made bias in the feed, which 
#is then reflected in the frontend (content feed)
#also takes the tweetdata (handle), which again must remain constant
bias = CreateBias(user, handle, count_category)

#finally, measure the biases and filter bubbles compared to ground truths (ratio of original, unbiased data), and store in database
#for frontend to access and display to user
#will use count sentiment and count category to get original ratios of tweets for different sentiments and categories
measure = MeasureBias2(count_sentiment, count_category, user, bias)

#log that everything is done
print("Done!")
