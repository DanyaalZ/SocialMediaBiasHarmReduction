import pandas as pd


"""Class to handle tweet data (for readability and to create an understandable flow in subsequent algorithms and classes)"""
""" This is the algorithm which further cleans the dataset, and readies it - takes path of file and reads as panda df"""
""" Written for scalability, but it is assumed the dataset contains a column called text and sentiment - and will check for this """
class TweetData:
    def __init__(self, path):
        #read tweets into pandas df for processing based on provided path
        self.tweets_df = pd.read_csv(path)

        #validate the data before using it to ensure it contains the columns and sentiments we need
        self.validate_data()

        #on initialisation immediately clean text, such that we can access it
        #for each row on text (content) column in df, apply the clean function on it and store in a column called cleaned_text
        self.tweets_df["text"] = self.tweets_df["text"].apply(self.clean_content)

        #categorise tweets - store in a column in the df called category
        self.tweets_df["category"] = self.tweets_df["text"].apply(self.categorise_tweets)

        #get only the needed columns from tweets_df (ID of each tweet (for testing and identifying by frontend),text, sentiment, category)
        self.tweets_df = pd.DataFrame({"textID":self.tweets_df["textID"],"text": self.tweets_df["text"], "sentiment": self.tweets_df["sentiment"], "category": self.tweets_df["category"]})

    #df is already cleaned, but I want to clean it further (of URLs and cursing) to provide better, cleaner data to use
    #clean the text from the df (tweet contents)
    def clean_content(self, text):
        #provided given text is string
        if isinstance(text,str):
            #find and remove urls using python regular expression, replace with white text
            #find and remove words with any asterisks (cursing)
            #find and remove any instance of @ or _ (twitter usernames)
            if "http" in text or "*" in text or "@" in text or "_" in text:
                text = "REMOVED TWEET"
        return text
    
    #ensure the data used (tweets_df) contains the needed columns
    #check for text and sentiment columns, and sentiment columns should contain only positive, negative and neutral as sentiments
    def validate_data(self):
         #check for textID column
         if 'textID' not in self.tweets_df.columns:
            raise ValueError("Error - required column textID is missing from dataset")
         
         #check for text column
         if 'text' not in self.tweets_df.columns:
            raise ValueError("Error - required column text is missing from dataset")
         
         #check for sentiment column
         if 'sentiment' not in self.tweets_df.columns:
            raise ValueError("Error - required column sentiment is missing from dataset")
         
         #sentiment columns should contain only positive, negative and neutral as sentiments, we check for them here
         allowed_sentiments = ["positive", "negative", "neutral"]
         found_sentiments = []
         
         #check for sentiments which should not be in the dataset
         for sentiment in self.tweets_df["sentiment"]:
             if sentiment not in allowed_sentiments:
                 raise ValueError(f"Error - {sentiment} is not allowed in the dataset")
             
             #check to make sure all allowed sentiments are present
             if sentiment not in found_sentiments:
                 found_sentiments.append(sentiment)

         #check if all allowed sentiments are present
         #set used to allow any order
         if not set(found_sentiments) == set(allowed_sentiments):
             raise ValueError("Error - sentiments are missing from dataset")
    
    """ Categorise the tweets into three main categories for the user to choose bias towards - sports, entertainment and politics; the text
    column will remain, containing all tweets for user to browse. This will use a variety of keywords stored in each respective array, and
    if a keyword is found for a specific category, the tweet will be based in that category"""
    def categorise_tweets(self, text):
            #make tweets lowercase so key words can be ascertained based on criteria
            #research if isinstance(text,str for this )
            #IMPORTANT - account for floats and ints as well, if not a string, placed in other - use isinstance to determine type
            if isinstance(text,str):
                text = text.lower()
            
                #if any of the key words from the following arrays are found, that category will be selected
                #sports
                sport_words = ["football", "soccer", "cricket", "bowling", "pool", "snooker", "basketball", "baseball", "hockey", "swimming", "olympics", "mma", "ufc", "wwe", "boxing", "sport", "sports", "the game", "nfl", "nba", "mlb", "premier league", "worldcup", "lakers", "yankees", "eagles", "broncos", "raiders", "messi", "lebron", "jordan", "goal", "touchdown", "ball", "barcelona", "madrid", "manchester united", "chelsea", "milan", "liverpool", "arsenal", "patriots", "packers", "steelers", "giants", "cowboys", "spurs", "bulls","lakers", "red sox", "dodgers", "cubs", "chicago cubs", "ronaldo", "medal", "finals", "playoffs", "championship", "gym", "weight", "bench press", "dead lift", "squat", "quarter back", "pitcher", "easports", "espn", "ultras", "f1", "grand prix", "madden", "2k", "yanks", "race", "team", "teams", "arena", "nike", "adidas", "reebok", "steroids", "testosterone"]
                if any(sport in text for sport in sport_words):
                    return "Sports"

                #entertainment
                entertainment_words = ["tv ", "movie", "watch", "movies", "film", "cinema", "dvd", "netflix", "streaming", "show", "shows", "actor", "actors", "actress", "director", "television", "gaming", "playstation", "xbox", "sony", "microsoft", "game", "twitter", "facebook", "spotify", "hulu", "disney", "marvel", "batman", "photo", "computer", "tweet", "3g", "eat ", "food", "nicotine", "car ", "season", "episode", "drive", "wolverine", "hulk", "iron man", "spiderman", "bug ", "animal", "animals", "family", "party", "celebrity", "celebrities", "twitter", "read", "smoke", "book"]
                if any(entertainment in text for entertainment in entertainment_words):
                    return "Entertainment"
                
                #politics
                politics_words = ["politics", "political", "politician", "white house", "parliament", "pentagon", "government", "house of parliament", "obama", "barack obama", "president", "prime minister", "minister", "military", "law ", "immigration", "climate change", "conservative", "conservatism", "liberal", "liberalism", "communism", "reform", "obamacare", "tax", "taxes", "tax reform", "foreign", "foreign policy", "USA", "united states", "uk", "united kingdom", "diplomatic", "mexico", "middle east", "nato", "war ", "united nations", "trade", "david cameron", "joe biden", "edward snowden", "julian assange", "debt", "patriotic", "london", "olympics", "labour party", "election", "vote", "republican", "embassy", "republic", "fbi", "mi5", "cia", "mi6", "nsa", "diversity"]
                if any(politics in text for politics in politics_words):
                    return "Politics"
                
                #if no keywords are found, tweet will be placed in other category
                else:
                    return "Other"
            #if text not a string, will also be placed in other category
            else:
                return "Other"
        
            

