import requests
import json
import time
from bs4 import BeautifulSoup
import re
import string
g = ""
doc = []
data = [{}]
baseURL = "https://www.mobygames.com/game/sort:moby_score/page:"
pageStart = 1016
howmanypages = 183
gamename = ""


def sanitize_filename(name):
    valid_chars = "-_.() %s%s" % (string.ascii_letters, string.digits)
    return ''.join(c if c in valid_chars else '_' for c in name)

toIgnore = ['Credits','Players','Ranking','Collected By','Business Model','Moby Score']
try:
    for i in range(howmanypages):
        print(i+pageStart)
        with open("./id_jsons/"+str(i+pageStart)+".json") as f:
            lines = f.readlines()

        links = re.findall('https://www.mobygames.com/game(.*?)"', lines[0])


        #pageN = pageStart + i
        #print("page number: "+str(pageN))
        #pageURL = baseURL + str(pageN) + "/";
        #print(pageURL)
        #page = session.get(pageURL)

        #soup = BeautifulSoup(page.content, "html.parser")
        #print(page.text)
        #x = re.findall('https://www.mobygames.com/game(.*?)"', str(soup))
        #for j in range(24):
            #x.pop(0)
        for game in links:

            URL = 'https://www.mobygames.com/game' + game
            print(URL)
            gamepage = requests.get(URL)
            gamesoup = BeautifulSoup(gamepage.content, "html.parser")
            # res = gamesoup.find_all("a")
            gamename = gamesoup.find('h1')
            if gamename is None:
                print("skip")
                continue
            else:
                gamename = gamename.text.strip()
            data[0]["Name"] = gamename
            res1 = gamesoup.find_all(class_='metadata')
            for r in res1:

                for dt in r.find_all('dt'):
                    if dt.text in toIgnore:
                        continue
                    dd = dt.findNext('dd')
                    if dt.text == "Released":
                        a = dd.find_all('a')
                        released = ""
                        for idx, x in enumerate(a):
                            clean = x.text.strip()
                            released = released + clean
                            if idx == 0:
                                released = released + " on "
                        data[0][dt.text] = released
                    elif "Releases by Date" in dt.text:
                        arr = []
                        a = dd.find_all('a')
                        comb = ""
                        for idx, x in enumerate(a):
                            clean = x.text.strip()
                            comb = comb + clean
                            if idx % 2 == 1:
                                arr.append(comb)
                                comb = ""
                            else:
                                comb = comb + ", "
                        data[0][dt.text] = arr
                    else:
                        a = dd.find_all('a')
                        if len(a) > 1:
                            arr = []
                            for idx, x in enumerate(a):
                                arr.append(x.text)
                            data[0][dt.text] = arr
                        elif a:
                            data[0][dt.text] = a[0].text
            res1 = gamesoup.find(id="description-text")
            clean = res1.text.strip()
            data[0]['Description'] = clean
            img = gamesoup.find(alt="box cover")
            if img is not None:
                data[0]['img'] = img['src']

            sanitized_gamename = sanitize_filename(gamename)
            strname = f"./game_jsons/{sanitized_gamename}.json"
            with open(strname, "w") as file:
                json.dump(data, file)
            #doc = doc + data
            data = [{}]
except Exception as e:
    e_type, e_object, e_traceback = sys.exc_info()

    e_filename = os.path.split(
        e_traceback.tb_frame.f_code.co_filename
    )[1]

    e_message = str(e)

    e_line_number = e_traceback.tb_lineno

    print(f'exception type: {e_type}')

    print(f'exception filename: {e_filename}')

    print(f'exception line number: {e_line_number}')

    print(f'exception message: {e_message}')
    print("Exception occurred, saving if possible")
    print(e)
    if gamename:
        sanitized_gamename = sanitize_filename(gamename)
        with open(f"./game_jsons/{sanitized_gamename}.json", "w") as file:
            json.dump(data, file)
#with open("./game_jsons/games.json", "w") as file:
    #json.dump(doc, file)


#URL = "https://www.mobygames.com/game/47486/starcraft-ii-wings-of-liberty/"
#page = requests.get(URL)

#soup = BeautifulSoup(page.content, "html.parser")





#for strong_tag in attr:
 #   print(strong_tag.text)
#for tag in val:
#    print(tag.text)


#if res1 is None:
#    print("inef")
#print(type(res1))