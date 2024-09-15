"""This class allows us to get the ratio of each sentiment/category (how much it comprises of total tweets), and the number of total tweets"""
"""This data is useful for testing as to whether the dataset is balanced enough for usage (as in TestSentimentsandCategories.py), and for getting values for the bias algorithm"""
""" NOTE - Ratio Counts only used for original dataset created by handledata, the later dataset created and counted by bias will be measured
separately in othe classes."""

""" Count sentiments class """
class CountSentiments():
    def __init__(self, TweetData):
        self.TweetData = TweetData.tweets_df
        self.total_tweets = len(self.TweetData)
        self.sentiment_type = None

    #set sentiment type for testing
    def set_sentiment_type(self, sentiment_type):
        self.sentiment_type = sentiment_type
        #sentiment count for a type is taken by the count of tweetdata where sentiment is equal to the type provided (positive, negative or neutral)
        self.sentiment_count = len(self.TweetData[self.TweetData["sentiment"] == self.sentiment_type])

    #get ratio compared to total tweets for testing and measurement 
    def get_ratio(self):
        return (self.sentiment_count/self.total_tweets) 
    
""" Count categories class - counts the number of tweets per category, works by having the category
in question input as an argument into the setter function, then the count for that category or
lowest category can be calculated. """
class CountCategories:
     def __init__(self, TweetData):
        self.TweetData = TweetData.tweets_df
        self.total_tweets = len(self.TweetData)
        self.category_type = ""
        self.category_count = None
        
     #set category type for testing - this also automatically counts the number of tweets for that category
     def set_category_type(self, new_category_type):
        self.category_type = new_category_type
        #category count for a type is taken by the count of tweetdata where category is equal to the type provided (politics, sports, entertainment, other)
        self.category_count = len(self.TweetData[self.TweetData["category"] == self.category_type])

    #get count of category to total tweets for testing and measurement 
     def get_count(self):
        return (self.category_count)
     
     #get ratio compared to total tweets for testing and measurement 
     def get_ratio(self):
        return (self.category_count/self.total_tweets)

     #get the name of the category with the lowest number of tweets
     def get_lowest_category(self):
        categories = ["Sports", "Politics", "Entertainment", "Other"]
        lowest_count = None
        lowest_category = None

        for category in categories:
            self.set_category_type(category)

            if lowest_count == None or self.category_count < lowest_count:
                lowest_count = self.category_count
                lowest_category = category
        return lowest_category
     