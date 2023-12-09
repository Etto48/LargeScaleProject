import argparse
import json
import random
import datetime

MEAN = 7.01
STD = 2.17

with open("random_review_dictionary.json", "r") as f:
    RANDOM_REVIEW_DICTIONARY = json.load(f)

def random_author() -> str:
    length = random.randint(4, 10)
    vowels = 'aeiou'
    consonants = 'bcdfghjklmnpqrstvwxyz'
    
    def generate_syllable():
        return random.choice(consonants) + random.choice(vowels)
    
    username = ''.join(generate_syllable() for _ in range(length // 2))
    
    has_a_number = random.random()
    if has_a_number < 0.5:
        username += str(random.randint(0, 9))
        if has_a_number < 0.25:
            username += str(random.randint(0, 9))
    
    return username.capitalize()

def extract_from_list(l: list, count: int) -> list:
    l_copy = l.copy()
    random.shuffle(l_copy)
    return l_copy[:count]
    
def random_quote(score: int) -> str:
    length = random.randint(1, 3)
    comment: list[str] = []
    if score < 3:
        comment += extract_from_list(RANDOM_REVIEW_DICTIONARY["negative"], length)
    elif score < 5:
        comment += extract_from_list(RANDOM_REVIEW_DICTIONARY["negative"], length)
        comment += [random.choice(RANDOM_REVIEW_DICTIONARY["neutral"])]
    elif score < 8:
        comment += extract_from_list(RANDOM_REVIEW_DICTIONARY["positive"], length)
        comment += [random.choice(RANDOM_REVIEW_DICTIONARY["neutral"])]
    else:
        comment = extract_from_list(RANDOM_REVIEW_DICTIONARY["positive"], length)
    has_typo = random.random() < 0.2
    comment: str = ' '.join([c.capitalize() for c in comment])
    
    if has_typo:
        for _ in range(random.randint(1, 3)):
            typo_index = random.randint(0, len(comment) - 1)
            if comment[typo_index].isalpha():
                comment = comment[:typo_index] + random.choice('abcdefghijklmnopqrstuvwxyz') + comment[typo_index + 1:]
    return comment

def random_date(min_date: datetime.datetime) -> str:
    today = datetime.datetime.now()
    delta = today - min_date
    random_delta = random.randint(0, delta.days)
    return (min_date + datetime.timedelta(days=random_delta)).strftime("%Y-%m-%d")

def custom_gaussian() -> int:
    distribution = [
        0.020236374457954256, 
        0.014284499617379475, 
        0.021086642292322082, 
        0.03180001700535669, 
        0.05484227531672477, 
        0.0893631493920585, 
        0.14480061219284074, 
        0.21596802992942776, 
        0.2216648244196922, 
        0.11708188079244962, 
        0.0688716945837939
    ]

    return random.choices(range(0, 11), weights=distribution)[0]

def random_comment(min_date) -> dict:
    score = custom_gaussian()
    quote = random_quote(score)
    author = random_author()
    date = random_date(min_date)
    return {
        "score": score,
        "quote": quote,
        "author": author,
        "date": date,
        "source": "random"
    }
    
def parse_date(date: str) -> datetime.datetime:
    date = date.split(" on ")[0]
    date = date.replace("1st", "1")
    date = date.replace("2nd", "2")
    date = date.replace("3rd", "3")
    date = date.replace("1th", "1")
    date = date.replace("2th", "2")
    date = date.replace("3th", "3")
    date = date.replace("4th", "4")
    date = date.replace("5th", "5")
    date = date.replace("6th", "6")
    date = date.replace("7th", "7")
    date = date.replace("8th", "8")
    date = date.replace("9th", "9")
    date = date.replace("0th", "0")
    try:
        ret = datetime.datetime.strptime(date, "%B %d, %Y")
    except ValueError as e:
        try:
            ret = datetime.datetime.strptime(date, "%B %Y")
        except ValueError as e:
            try:
                ret = datetime.datetime.strptime(date, "%Y")
            except ValueError as e:
                ret = datetime.datetime.strptime("January 1, 1986", "%B %d, %Y")
    return ret

def main(input_path: str, output_path: str, seed: str | None):
    if seed is not None:
        random.seed(seed)
    with open(input_path, "r") as f:
        data = json.load(f)
    for game in data:
        min_date = parse_date(game["Released"])
        if len(game["reviews"]) == 0:
            if random.random() < 0.65:
                game["reviews"] = [random_comment(min_date) for _ in range(random.randint(1, 5))]
                avg_score = 0
                for review in game["reviews"]:
                    avg_score += review["score"]
                avg_score /= len(game["reviews"])
                game["user_review"] = avg_score
        else:
            for review in game["reviews"]:
                review["source"] = "user"
                
    with open(output_path, "w") as f:
        json.dump(data, f, indent=4)

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Generate fake reviews")
    parser.add_argument("--input", type=str, required=True, help="Path to the input dataset")
    parser.add_argument("--output", type=str, required=True, help="Path to the output dataset")
    parser.add_argument("--seed", type=str, required=False, help="Seed for the random generator")
    args = parser.parse_args()
    main(args.input, args.output, args.seed)