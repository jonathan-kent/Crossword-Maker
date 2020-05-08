from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from time import sleep
import collections

class dictionaryScraper:

    def __init__(self):
        chrome_options = webdriver.ChromeOptions()
        prefs = {"profile.default_content_setting_values.notifications" : 2}
        chrome_options.add_experimental_option("prefs",prefs)
        #add chrome webdriver file path
        self.driver = webdriver.Chrome('C:/Users/Jonathan/bin/chromedriver.exe',chrome_options=chrome_options)
        #select first page of letter to save
        self.driver.get("https://www.dictionary.com/list/a/1")
        sleep(1)

    def get_words(self,file):
        wordList = self.driver.find_element_by_xpath("/html/body/div/div/div/div[2]/div/main/ul")
        buffer = wordList.find_elements_by_tag_name("li")
        for word in buffer:
            try:
                text = word.text
                head, sep, tail = text.partition('|')
                text = head
                text = text.replace(' ', '')
                text = text.replace('-', '')
                if text.isalpha():
                    file.write(text+"\n")
            except:
                print("exception")

    def next_page(self):
        self.driver.execute_script("scroll(0, -1000);")
        self.driver.find_element_by_xpath("/html/body/div/div/div/div[2]/div/main/div/ol/li[13]/a")\
            .click()
        sleep(1)
 
scraper = dictionaryScraper()
#file name
file = open("A_Words.txt","w")
#number of pages
for i in range(1,54):
    scraper.driver.get("https://www.dictionary.com/list/a/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("B_Words.txt","w")
#number of pages
for i in range(1,50):
    scraper.driver.get("https://www.dictionary.com/list/b/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("C_Words.txt","w")
#number of pages
for i in range(1,73):
    scraper.driver.get("https://www.dictionary.com/list/c/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("D_Words.txt","w")
#number of pages
for i in range(1,40):
    scraper.driver.get("https://www.dictionary.com/list/d/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("E_Words.txt","w")
#number of pages
for i in range(1,30):
    scraper.driver.get("https://www.dictionary.com/list/e/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("F_Words.txt","w")
#number of pages
for i in range(1,32):
    scraper.driver.get("https://www.dictionary.com/list/f/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("G_Words.txt","w")
#number of pages
for i in range(1,30):
    scraper.driver.get("https://www.dictionary.com/list/g/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("H_Words.txt","w")
#number of pages
for i in range(1,35):
    scraper.driver.get("https://www.dictionary.com/list/h/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("I_Words.txt","w")
#number of pages
for i in range(1,28):
    scraper.driver.get("https://www.dictionary.com/list/i/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("J_Words.txt","w")
#number of pages
for i in range(1,10):
    scraper.driver.get("https://www.dictionary.com/list/j/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("K_Words.txt","w")
#number of pages
for i in range(1,13):
    scraper.driver.get("https://www.dictionary.com/list/k/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("L_Words.txt","w")
#number of pages
for i in range(1,30):
    scraper.driver.get("https://www.dictionary.com/list/l/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("M_Words.txt","w")
#number of pages
for i in range(1,47):
    scraper.driver.get("https://www.dictionary.com/list/m/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("N_Words.txt","w")
#number of pages
for i in range(1,25):
    scraper.driver.get("https://www.dictionary.com/list/n/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("O_Words.txt","w")
#number of pages
for i in range(1,22):
    scraper.driver.get("https://www.dictionary.com/list/o/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("P_Words.txt","w")
#number of pages
for i in range(1,65):
    scraper.driver.get("https://www.dictionary.com/list/p/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("Q_Words.txt","w")
#number of pages
for i in range(1,5):
    scraper.driver.get("https://www.dictionary.com/list/q/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("R_Words.txt","w")
#number of pages
for i in range(1,35):
    scraper.driver.get("https://www.dictionary.com/list/r/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("S_Words.txt","w")
#number of pages
for i in range(1,84):
    scraper.driver.get("https://www.dictionary.com/list/s/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("T_Words.txt","w")
#number of pages
for i in range(1,41):
    scraper.driver.get("https://www.dictionary.com/list/t/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("U_Words.txt","w")
#number of pages
for i in range(1,19):
    scraper.driver.get("https://www.dictionary.com/list/u/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("V_Words.txt","w")
#number of pages
for i in range(1,14):
    scraper.driver.get("https://www.dictionary.com/list/v/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("W_Words.txt","w")
#number of pages
for i in range(1,21):
    scraper.driver.get("https://www.dictionary.com/list/w/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("X_Words.txt","w")
#number of pages
scraper.driver.get("https://www.dictionary.com/list/x/")
scraper.get_words(file)
file.close()
#file name
file = open("Y_Words.txt","w")
#number of pages
for i in range(1,5):
    scraper.driver.get("https://www.dictionary.com/list/y/"+str(i))
    scraper.get_words(file)
file.close()
#file name
file = open("Z_Words.txt","w")
#number of pages
for i in range(1,4):
    scraper.driver.get("https://www.dictionary.com/list/z/"+str(i))
    scraper.get_words(file)
file.close()
