import unittest


"""These tests are run in order to assess whether the dataset (tweets.csv) is balanced enough to use to continue with our implementation"""
""" The dataset should have at least 2000 tweets (this ensures enough data to run, and enough to categorise)"""
"""For positive and negative sentiments, they should not comprise of a ratio more than 33% of the total tweets, and for neutral it should comprise of more than 33% (for balancing)"""
""" The lowest category count should be above 500 for the dataset to be diverse enough to use"""
"""The purpose of this is for scalability, such that different datasets can be used, but it will be assumed they are called Tweets.csv, and have the same column names and sentiment types"""
class TestData(unittest.TestCase):

    #define class variables for testing - done in custom method below this one - (we need the running objects from backend.py for consistency)
    @classmethod
    def setUpClass(cls):
        pass

    #take sentiment and category counts object for setup - this allows us to define the ratio of each sentiment type, and to get the 
    #cleaned dataset from the TweetData object (which remains constant through the backend)
    #these are methods of the test class
    @classmethod   
    def setup_sentiment_and_category_counts(cls, sentiment_count, category_count, tweet_data):
        #tweetdata should be stored in Tweets.csv
        cls.sentiment_counts = sentiment_count
        cls.category_counts = category_count
        cls.tweets_df = tweet_data

    #test number of tweets in dataset (should be 2000 and more)
    def test_number_of_tweets(self):
        self.assertGreaterEqual(len(self.tweets_df), 2000, "Dataset does not contain enough tweets to continue")
   
    #test categories, take the lowest number and if it is less than 450 tweets (or dataset is under 450), will fail
    #as dataset will not be diverse enough
    def test_categories(self):
        categories = ["Sports", "Politics", "Entertainment", "Other"]
        for category in categories:
            self.category_counts.set_category_type(category)
            category_counted = self.category_counts.get_count()
            self.assertGreaterEqual(category_counted, 450, "Not enough tweets from the smallest category in the dataset")


    #test positive sentiments (less than 34% of total tweets?)
    def test_sentiment_positive(self):
        self.sentiment_counts.set_sentiment_type("positive")
        sentiment_ratio = self.sentiment_counts.get_ratio()
        self.assertLessEqual(sentiment_ratio,0.33, "Positive sentiment - ratio too high for usage")

    #test negative sentiments (less than 34% of total tweets?)
    def test_sentiment_negative(self):
        self.sentiment_counts.set_sentiment_type("negative")
        sentiment_ratio = self.sentiment_counts.get_ratio()
        self.assertLessEqual(sentiment_ratio, 0.33, "Negative sentiment - ratio too high for usage")

    #test neutral sentiments (more than 32% of total tweets?)
    def test_sentiment_neutral(self):
        self.sentiment_counts.set_sentiment_type("neutral")
        sentiment_ratio = self.sentiment_counts.get_ratio()
        self.assertGreaterEqual(sentiment_ratio, 0.33, "Neutral sentiment - ratio too low for usage")


"""When this is run, we will not continue unless conditions are met (dataset is balanced enough to use)"""
""" For running the tests """
class RunTest():
    def __init__(self, CountSentiments, CountCategories, TweetData):
        #set sentiment and category count object from running backend.py instance (to get current flow of objects - TweetData and count sentiments and
        #categories - instead of new random instances)
        sentiment_counts = CountSentiments
        category_counts = CountCategories
        tweet_data = TweetData.tweets_df

        #"setup" the testing class with the running sentiment and category count objects (containing ready tweet data)
        TestData.setup_sentiment_and_category_counts(sentiment_counts, category_counts, tweet_data)

        #load tests
        suite = unittest.TestLoader().loadTestsFromTestCase(TestData)
        
        #failsafe option allows us to ensure implementation will not continue unless conditions met
        runner = unittest.TextTestRunner(failfast=True)
        #run tests
        runner.run(suite)

