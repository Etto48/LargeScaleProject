import requests
import json
from bs4 import BeautifulSoup
import re
import string
doc = []
data = [{}]
baseURL = "https://www.mobygames.com/company/page:"
pageN = 0
pageURL = baseURL + str(pageN) + "/"


def sanitize_filename(name):
    valid_chars = "-_.() %s%s" % (string.ascii_letters, string.digits)
    return ''.join(c if c in valid_chars else '_' for c in name)

howmanypages = 200
try:
    for i in range(howmanypages):
        print(i)
        page = requests.get(pageURL)
        soup = BeautifulSoup(page.content, "html.parser")
        x = re.findall('https://www.mobygames.com/company(.*?)"', str(soup))
        for j in range(3):
            x.pop(0)
        for company in x:
            URL = 'https://www.mobygames.com/company' + company
            print(URL)
            companypage = requests.get(URL)
            companysoup = BeautifulSoup(companypage.content, "html.parser")
            img = companysoup.find(class_="img-fluid img-max-h300")
            if img is not None:
                data[0]['imglink'] = img['src']
            companyname = companysoup.find('h1').text.strip()
            data[0]['Name'] = companyname
            overview = companysoup.find(id="description-text")
            if overview is not None:
                ov = overview.text.strip()
                data[0]['Overview'] = ov
            sanitized_companyname = sanitize_filename(companyname)
            strname = f"./game_jsons/{sanitized_companyname}.json"
            with open("./company_jsons/"+companyname+".json", "w") as file:
                json.dump(data, file)
            #doc = doc + data
            data = [{}]
except Exception as e:
    print("Exception occurred, saving if possible")
    print(e)
    if companyname:
        sanitized_companyname = sanitize_filename(companyname)
        with open(f"./game_jsons/{sanitized_companyname}.json", "w") as file:
            json.dump(data, file)
#with open("./company_jsons/companies.json", "w") as file:
 #   json.dump(doc, file)