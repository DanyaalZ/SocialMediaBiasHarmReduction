"""Details of user to be used throughout the backend.
This will help with bias creation and measurement, as it gives us values such as:
- User's filter bubble seal status
- User's sentiment preference
- User's category preferences"""
from ConnectandReadDatabase import ConnectandReadDatabase
class UserDetails:
    def __init__(self, username):
        self.username = username
        
        #user filter bubble seal status
        self.filter_bubble_seal = self.get_filter_bubble_seal()

        #user sentiment preference
        self.sentiment_preference = self.get_sentiment_preference()

        #user category preference ratios
        self.sport_category_ratio = None
        self.politics_category_ratio = None
        self.other_category_ratio = None
        self.entertainment_category_ratio = None

        self.get_category_preferences()
        

    #get user sentiment preference
    def get_sentiment_preference(self):
        with ConnectandReadDatabase(self.username) as database_connect:
            return str((database_connect.get_value_from_users("Sentiment_Preference"))[0])
    
    #get user category preference ratios
    def get_category_preferences(self):
        with ConnectandReadDatabase(self.username) as database_connect:
            self.sport_category_ratio = int((database_connect.get_value_from_users("Sports_Preference"))[0])
            self.politics_category_ratio = int((database_connect.get_value_from_users("Politics_Preference"))[0])
            self.other_category_ratio = int((database_connect.get_value_from_users("Other_Preference"))[0])
            self.entertainment_category_ratio = int((database_connect.get_value_from_users("Entertainment_Preference"))[0])
    
    #get user filter bubble seal status (again for category bias)
    def get_filter_bubble_seal(self):
        with ConnectandReadDatabase(self.username) as database_connect:
            return database_connect.get_value_from_users("Filter_Bubble_Seal")