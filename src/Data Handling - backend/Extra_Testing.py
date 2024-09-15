import matplotlib.pyplot as plt
import numpy as np

import random

from CreateBias import CreateBias
from MeasureBias2 import MeasureBias2
from HandleData import TweetData
from CountSentimentsandCategories import CountCategories, CountSentiments

"""This python file does not use 'testing' in the traditional sense of testCases, rather it creates 30 users with randomised (fair) traits, measures
the accuracy of their bias and plots it onto a graph.

It will compare a measurement of exactly what the user 'chose' (randomly selected), compared to what was actually measured (how closely the algorithm
distributed the tweets (sentiments, categories) according to the user's choices."""

#a class for simulated users here, with the same name as the original to allow us to create a bias
#it will give a random sentiment preference, and randomised category preferences
#omits filter bubble seal, as this can be easily 
class UserDetails:
    def __init__(self, username):
        self.username = username

        #set the sentiment preference and category ratios through randomisation
        self.sentiment_preference = self.create_sentiment_preference()
        
        self.sport_category_ratio = None
        self.politics_category_ratio = None
        self.other_category_ratio = None
        self.entertainment_category_ratio = None
        

        #needed for measure bias object
        self.filter_bubble_seal = None

        #fill in the category ratios (randomised)
        self.create_category_ratios()

    #select a sentiment preference with equal chance
    #neutral ommitted as this will always cause the same value
    def create_sentiment_preference(self):
        possible_sentiments = ["Positive", "Negative"]
        chosen_sentiment = random.choice(possible_sentiments)

        return chosen_sentiment
    
    def create_category_ratios(self):
        #get a total to ensure it is not exceeded (adding up to 100)
        total = 100

        #sports ratio
        self.sport_category_ratio = random.randint(0,total)
        total -= self.sport_category_ratio

        #politics
        self.politics_category_ratio = random.randint(0,total)
        total-= self.politics_category_ratio

        #entertainment
        self.entertainment_category_ratio = random.randint(0,total)
        total -= self.entertainment_category_ratio
        
        #rest goes into other
        self.other_category_ratio = total

        #ensure they add up to 100
        if self.sport_category_ratio+self.politics_category_ratio+self.entertainment_category_ratio+self.other_category_ratio != 100:
            self.create_category_ratios()

class PlotTest:
    def __init__(self):
        #30 users with randomised variables
        self.all_users = self.create_users()

        #create sample data (data as normal)
        #ALL THE SAME DATA WILL BE SAMPLED FROM, FOR FAIRNESS
        self.sample_data = TweetData("Data/tweets.csv")

        #count categories needed for CreateBias
        self.category_count = CountCategories(self.sample_data)

        #count sentiments needed for MeasureBias
        self.sentiment_count = CountSentiments(self.sample_data)

        #creating bias for each user
        self.user_biases = self.create_biases()

        #measuring bias for each user
        self.measured_biases = self.measure_biases()

        
        """Variables needed for plotting - stored in arrays to get for each user"""
        #measurement of user chosen variables
        self.user_sentiment_variation = [0 for i in range(30)]
        self.user_sports = [0 for i in range(30)]
        self.user_politics = [0 for i in range(30)]
        self.user_entertainment = [0 for i in range(30)]
        self.user_other = [0 for i in range(30)]
        #fill each array
        self.get_user_chosen_biases()

        #measurement of algorithm's run of the chosen variables
        self.algorithm_sentiment_variation = [0 for i in range(30)]
        self.algorithm_sports = [0 for i in range(30)]
        self.algorithm_politics = [0 for i in range(30)]
        self.algorithm_entertainment = [0 for i in range(30)]
        self.algorithm_other = [0 for i in range(30)]
        
        #fill each array
        self.get_algorithm_created_biases()

        """Plotting on graph"""
        self.plot_and_save_graph("Sentiment Variation",self.user_sentiment_variation, self.algorithm_sentiment_variation)
        self.plot_and_save_graph("Sports Category",self.user_sports, self.algorithm_sports)
        self.plot_and_save_graph("Politics Category",self.user_politics, self.algorithm_politics)
        self.plot_and_save_graph("Entertainment Category",self.user_entertainment, self.algorithm_entertainment)
        self.plot_and_save_graph("Other Category",self.user_other, self.algorithm_other)
        

    """Creating users with randomised values """
    #a loop for to create each user (of which there will be 30)
    def create_users(self, creation_loop_position=0):
        #array for each user 
        all_user_values = [0 for i in range(30)]

        #loop to create each user
        for i in range(1,31):
            #username will be user + position in loop (1-30)
            username = "User " + str(i)
            new_user = UserDetails(username)

            #store user in array
            all_user_values[creation_loop_position] = new_user
            creation_loop_position += 1

        return all_user_values

    """Creating bias for each user"""
    def create_biases(self):
        #create the bias for each user and store in a separate array
        user_biases = []

        for i in range(len(self.all_users)):
            user_biases.append(CreateBias(self.all_users[i], self.sample_data, self.category_count))

        return user_biases
    
    """Measuring bias for each user"""
    def measure_biases(self):
        #measure bias for each user
        #stored in its own array
        user_measured_biases = []

        for i in range(len(self.user_biases)):
            user_measured_biases.append(MeasureBias2(self.sentiment_count, self.category_count, self.all_users[i], self.user_biases[i]))
        
        return user_measured_biases

    #get chosen biases for all users - position in array will match user
    #categories are divided by 100 to match the format of algorithm created biases
    def get_user_chosen_biases(self):
        for i in range(len(self.all_users)):
            #sentiment variation
            self.user_sentiment_variation[i] = float("{0:.2f}".format((self.user_biases[i]).sentiment_variation))
                
            #categories
            self.user_sports[i] = ((self.all_users[i]).sport_category_ratio)/100
            self.user_politics[i] = ((self.all_users[i]).politics_category_ratio)/100
            self.user_entertainment[i] = ((self.all_users[i]).entertainment_category_ratio)/100
            self.user_other[i] = ((self.all_users[i]).other_category_ratio)/100
    
    #get measurements of algorithm created biases to compare with user chosen ones
    def get_algorithm_created_biases(self):
        for i in range(len(self.all_users)):
                #sentiment variation
                self.algorithm_sentiment_variation[i] = float((self.measured_biases[i]).sentiment_variation)
                    
                #categories
                self.algorithm_sports[i] = float((self.measured_biases[i]).new_category_ratio_sports)
                self.algorithm_politics[i] = float((self.measured_biases[i]).new_category_raio_politics)
                self.algorithm_entertainment[i] = float((self.measured_biases[i]).new_category_ratio_entertainment)
                self.algorithm_other[i] = float((self.measured_biases[i]).new_category_ratio_other)

    """ (Bar) Plot the graph for the given type (e.g. sentiment variation), take user and algorithm data for that type and plot against each other"""
    def plot_and_save_graph(self, type_name, user_data, algorithm_data):
        #usernames for x axis
        users = [user.username for user in self.all_users]

        plt.figure(figsize=[15, 8])

        bar_width = 0.35 

        #bar positions (1 and 2)
        r1 = range(len(users)) 
        r2 = [x + bar_width for x in r1]  

        #two bars - one for user chosen data, one for algorithm generated
        plt.bar(r1, user_data, color="blue", width=bar_width, label="User Chosen")
        plt.bar(r2, algorithm_data, color="green", width=bar_width, label="Algorithm Generated")

        #x axis
        plt.xlabel("User", fontweight="bold")
        #centre labels between the bars
        plt.xticks([r + bar_width / 2 for r in range(len(users))], users, rotation=90) 

        #y axis
        #y ticks have been increased for better readability
        plt.yticks(np.arange(0, 1.1, 0.05)) 
        plt.ylabel(type_name)

        #title of given plot
        plt.title(f"User vs Algorithm: {type_name} Preference")

        #legend to show which bar is which
        plt.legend()
        plt.tight_layout()  

        #save figure and close plt for next iteration
        plt.savefig(f"{type_name}_graph.png")  
        plt.close()  


"""run tests and plot"""
#run tests and plot
plot_test = PlotTest()
