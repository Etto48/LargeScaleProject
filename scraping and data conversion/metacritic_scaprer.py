import requests
import argparse
import bs4
import json
import dirtyjson
import os
import rich.progress
import rich.text
import urllib.parse
import unidecode

def get_json_len(data: str) -> int:
    depth = 0
    for i,d in enumerate(data):
        if d == "{" or d == "[":
            depth += 1
        elif d == "}" or d == "]":
            depth -= 1
        if depth == 0:
            skip = i
            break
    return skip

def sanitize_name(name: str) -> str:
    name = unidecode.unidecode(name)
    name = name.replace("&", "and")
    name = name.replace("- ", "")
    #remove every character that is not alphanumeric or whitespace
    name = "".join([x for x in name if x.isalnum() or x == " " or x == "-"])
    name = " ".join(name.split())
    name = name.replace(" ", "-")
    name = name.lower()
    return name

def gen_url(category:str, name: str) -> str:
    return f"https://www.metacritic.com/{category}/{sanitize_name(name)}/user-reviews/"

def gen_search_url(name: str) -> str:
    name = unidecode.unidecode(name)
    name = name.replace("/"," ")
    name = name.replace("&", "and")
    name = name.replace("#", "")
    name = name.replace("%", "")
    name = name.replace("?", "")
    escaped_name = urllib.parse.quote(name)
    return f"https://www.metacritic.com/search/{escaped_name}/?category=13"

def remove_vars(data: str) -> str:
    vars = ["a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","x","y","z"]
    for var in vars:
        data = data.replace(f":{var},",":0,")
        data = data.replace(f":{var}}}",":0}")
        data = data.replace(f":[{var}],",":0,")
        data = data.replace(f":[{var}]}}",":0}")
    return data

def score_difference(name1: str, name2: str) -> float:
    s_name1 = sanitize_name(name1)
    s_name2 = sanitize_name(name2)
    max_len = max(len(s_name1), len(s_name2))
    differences = 0
    for i in range(max_len):
        try:
            if s_name1[i] != s_name2[i]:
                differences += 1
        except IndexError:
            differences += 1
    return differences / max_len

def find_url(category: str, name: str) -> str:
    try:
        request = requests.get(gen_search_url(name), headers={"User-Agent": "Mozilla/5.0"})
        request.raise_for_status()
        soup = bs4.BeautifulSoup(request.text, "html.parser")
        links = soup.find_all("a", class_="c-pageSiteSearch-results-item")
        if len(links) == 0:
            raise ValueError("Could not find url")
        first_link = None
        for link in links:
            link_text:str = link["href"]
            game_name = link.find_all("p")[0].text.strip()
            if link_text.count(f"/{category}/") == 1:
                if score_difference(name, game_name) < 0.2:
                    first_link = link
                    break
        if first_link is None:
            raise ValueError("Could not find url")
        first_link = first_link["href"]
    except Exception as e:
        raise e
    return "https://www.metacritic.com" + first_link

def scrape(category:str, name: str, tmp: str | None) -> list[(str,str)]:
    file_name = f"{sanitize_name(name)}.json"
    if tmp is not None:
        os.makedirs(tmp, exist_ok=True)
        if os.path.exists(os.path.join(tmp, file_name)):
            with open(os.path.join(tmp, file_name), "r") as f:
                return json.load(f)
    url = gen_url(category, name)
    request = requests.get(url, headers={"User-Agent": "Mozilla/5.0"})
    if request.status_code == 404:
        try:
            new_url = find_url(category,name)
        except ValueError as e:
            if str(e) == "Could not find url":
                if tmp is not None:
                    with open(os.path.join(tmp, file_name), "w") as f:
                        json.dump([], f, indent=4)
                return []
            else:
                raise e
            
            
        request = requests.get(new_url, headers={"User-Agent": "Mozilla/5.0"})
        request.raise_for_status()
    else:
        request.raise_for_status()
    soup = bs4.BeautifulSoup(request.text, "html.parser")
    scripts = soup.find_all("script")
    data = None
    for script in scripts:
        if "window.__NUXT__=(function(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y)" in str(script):
            data = str(script)
        if "window.__NUXT__=(function(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z)" in str(script):
            data = str(script)
        if "window.__NUXT__=(function(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x)" in str(script):
            data = str(script)
        if "window.__NUXT__=(function(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w)" in str(script):
            data = str(script)
    if data is None:
        os.makedirs("error", exist_ok=True)
        with open(os.path.join("error", f"{sanitize_name(name)}.html"), "wb") as f:
            f.write(request.text.encode("utf-8"))
        raise ValueError("Could not find data")
    
    data_start_index = data.find(".components=")
    data = data[data_start_index + len(".components="):]
    
    data = remove_vars(data)
    
    try:
        json_data = dirtyjson.loads(data)
    except dirtyjson.Error as e:
        with open("error.txt", "wb") as f:
            f.write(data.encode("utf-8"))
        raise e
    
    try:
        raw_reviews = json_data[-1]["items"]
    except KeyError:
        raw_reviews = json_data[-1]["item"]["default"]
    clean_reviews = []
    for review in raw_reviews:
        try:
            quote = review["quote"]
            try:
                score = int(review["score"])
            except ValueError as e:
                if score == "f":
                    score = 0
                else:
                    raise e
            author = review["author"]
            date = review["date"]
            clean_reviews.append({"quote": quote, "score": score, "author": author, "date": date})
        except KeyError:
            pass
    
    if tmp is not None:
        with open(os.path.join(tmp, file_name), "w") as f:
            json.dump(clean_reviews, f, indent=4)
    
    return clean_reviews
        
def main(args):
    assert args.category in ["game", "movie", "music"]
    with open(args.dataset, "r") as f:
        data = json.load(f)
    names: list[str] = [x["Name"] for x in data]
    with rich.progress.Progress(
            *rich.progress.Progress.get_default_columns(),
            rich.progress.MofNCompleteColumn(),
            rich.progress.TextColumn("[bold blue]{task.fields[filename]}", justify="right"),
        ) as progress:
        with open("error.log", "wb") as error_log:
            task = progress.add_task("Scraping", total=len(names), filename="")
            for i,name in enumerate(names):
                max_name_len = 30
                short_name_len = len(name) if len(name) < max_name_len else max_name_len - len("...")
                short_name = name[:short_name_len] + ("..." if len(name) > short_name_len else "")
                progress.update(task, filename=short_name)
                name = name.strip()
                reviews = []
                try:
                    reviews = scrape(args.category, name, args.tmp)
                except Exception as e:
                    if type(e) == dirtyjson.Error:
                        text = f"Fatal error while parsing \"{name}\": {e}"
                        error_log.writelines([text.encode("utf-8")])
                        progress.console.print(rich.text.Text.styled(text,"red"))
                        exit(1)
                    if type(e) == requests.exceptions.HTTPError and e.response.status_code == 404:
                        text = f"Fatal error while searching \"{name}\": {e}"
                        error_log.writelines([text.encode("utf-8")])
                        progress.console.print(rich.text.Text.styled(text,"red"))
                        exit(1)
                    else:
                        text = f"Failed to scrape \"{name}\" ({sanitize_name(name)}): {e}"
                        error_log.writelines([text.encode("utf-8")])
                        progress.console.print(rich.text.Text.styled(text,"red"))
                progress.update(task, advance=1)
                s = 0
                for review in reviews:
                    s += review['score']
                if len(reviews) == 0:
                    score = None
                    progress.console.print(rich.text.Text.styled(f"No reviews found for \"{name}\" ({sanitize_name(name)})","yellow"))
                else:
                    score = s / len(reviews)
                    progress.console.print(rich.text.Text.styled(f"Found {len(reviews)} reviews for \"{name}\" ({sanitize_name(name)})","green"))
                if score is not None:
                    data[i]["user_review"] = score
                else:
                    data[i]["user_review"] = None
                data[i]["reviews"] = reviews

    progress.refresh()
    with open(args.dataset, "w") as f:
        json.dump(data, f, indent=4)
    print(f"\033[32mSaved to {args.dataset}\033[0m")
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Scrape metacritic")
    parser.add_argument("--dataset", "-d", type=str, help="Dataset json file", required=True)
    parser.add_argument("--tmp", "-t", type=str, help="Cache file directory")
    parser.add_argument("--category", "-c", type=str, help="Category to scrape, one of \"game\", \"movie\", \"music\"", required=True)
    args = parser.parse_args()
    main(args)