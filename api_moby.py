import requests
import json
import time
from bs4 import BeautifulSoup
import re
import string

baseURL = "https://api.mobygames.com/v1/games?api_key=moby_uMb5V1NHUrLUvJv9I10v13nYfPB&offset="
start = 62100
offset = 100


for i in range(120000-start):
    print("offset is: "+ str(start + (offset * (i + 1))))
    URL = baseURL + str(start+(offset*(i+1)))
    print(URL)
    jjson = requests.get(URL)
    text = jjson.text
    text = text.strip()
    if "moby_url" not in text:
        print("exceeded")
        break
    with open("./id_jsons/"+str(int(start/100)+i)+".json", "w") as file:
        file.write(text)
