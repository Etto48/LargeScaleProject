import argparse
import json
import random
import os
import datetime

def generate_review_comment(reviewer_name, review_rating, average_score, std_score):
    adjectives_agree = ["insightful", "thoughtful", "detailed", "informative"]
    adjectives_disagree = ["biased", "uninsightful", "unthoughtful", "uninformative"]
    verbs_agree = ["agree with", "appreciate", "vibe with", "like"]
    verbs_disagree = ["disagree with", "dislike", "don't vibe with", "don't like", "don't agree with"]
    game_opinion_positive = ["good", "amazing", "innovative", "fun", "lit", "a masterpiece", "a classic"]
    game_opinion_negative = ["bad", "terrible", "boring", "uninspired", "trash", "garbage"]
    game_opinion_neutral = ["okay", "alright", "decent", "average"]

    comment_score = round(random.gauss(average_score, std_score))
    agree = abs(comment_score - review_rating) <= 2
    
    if review_rating < 3:
        opinion = "negative"
    elif review_rating < 8:
        opinion = "mixed"
    else:
        opinion = "positive"
    if comment_score < 3:
        game_opinion = random.choice(game_opinion_negative)
    elif comment_score < 7:
        game_opinion = random.choice(game_opinion_neutral)
    else:
        game_opinion = random.choice(game_opinion_positive)
    
    if agree:
        verbs = verbs_agree
        adjectives = adjectives_agree
    else:
        verbs = verbs_disagree
        adjectives = adjectives_disagree
    
    templates_neutral = [
        f"I {random.choice(verbs)} {reviewer_name}'s {opinion} review. I found it {random.choice(adjectives)}.",
        f"IMO, {reviewer_name}'s {opinion} review was {random.choice(adjectives)}.",
        f"I {random.choice(verbs)} this review. {reviewer_name} was {random.choice(adjectives)}.",
        f"I think that {reviewer_name} has written a {random.choice(adjectives)} review. This game is {game_opinion}.",
        f"I {random.choice(verbs)} {reviewer_name}'s {opinion} review. I think this game is {game_opinion}.",
    ]
    templates_agree = [
        f"Yeah, {reviewer_name}'s right, this game is {game_opinion}.",
        f"{reviewer_name} has a point, this game is {game_opinion}.",
        f"I agree with {reviewer_name}.",
        f"I see that {reviewer_name} agrees with me.",
        f"Yeah, this game is {game_opinion}.",
        f"Yes, this game is {game_opinion}.",
    ]
    templates_disagree = [
        f"No, {reviewer_name}'s wrong, this game is {game_opinion}.",
        f"{reviewer_name} didn't get it, this game is {game_opinion}.",
        f"I disagree with {reviewer_name}.",
        f"This {opinion} review was not so {random.choice(adjectives_agree)}",
        f"Noo, this game is {game_opinion}.",
        f"Nah, this game is {game_opinion}.",
    ]

    templates = templates_neutral + (templates_agree if agree else templates_disagree)
    
    return random.choice(templates)

def random_date(min_date: datetime.datetime) -> str:
    today = datetime.datetime.now()
    delta = today - min_date
    random_delta = random.randint(0, delta.days)
    return (min_date + datetime.timedelta(days=random_delta)).strftime("%Y-%m-%d")

def get_comment_author(usernames: list[str], reviewers: list[str], reviewer_name: str, already_used_names: list[str]) -> str:
    output_user = None
    while output_user is None or output_user == reviewer_name or output_user in already_used_names:
        if random.random() < 0.1:
            output_user = random.choice(usernames)
        else:
            output_user = random.choice(reviewers)
    return output_user

def main(input_path: str, output_path: str):
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    print("Loading dataset info...")
    with open(input_path, "r") as f:
        data = json.load(f)
    usernames = set()
    for game in data:
        for review in game["reviews"]:
            usernames.add(review["author"])
    usernames = list(usernames)
    print("Dataset info loaded, generating comments...")
    for i,game in enumerate(data):
        if i % 10 == 0:
            print(f"{i+1}/{len(data)} ({i/len(data)*100:.02f}%)",end="\033[J\r")
        
        if len(game["reviews"]) == 0:
            continue
        average_score = 0
        for review in game["reviews"]:
            average_score += review["score"]
        
        average_score /= len(game["reviews"])
        std_score = 0
        for review in game["reviews"]:
            std_score += (review["score"] - average_score) ** 2
        std_score /= len(game["reviews"])
        std_score = std_score ** 0.5
        
        reviewers = set()
        for review in game["reviews"]:
            reviewers.add(review["author"])
        reviewers = list(reviewers)
        
        for review in game["reviews"]:
            number_of_comments = random.randint(0, 5)
            
            comments = []
            already_used_names = []
            for _ in range(number_of_comments):
                comment_author = get_comment_author(usernames, reviewers, review["author"], already_used_names)
                comment_quote = generate_review_comment(review["author"], review["score"], average_score, std_score)
                comment_date = random_date(datetime.datetime.strptime(review["date"], "%Y-%m-%d")) 
                comments.append({
                    "author": comment_author,
                    "quote": comment_quote,
                    "date": comment_date,
                })
                already_used_names.append(comment_author)
            review["comments"] = comments
    
    print("Saving dataset...\033[J")
    with open(output_path, "w") as f:
        json.dump(data, f, indent=4)
        
if __name__ == "__main__":
    parser = argparse.ArgumentParser("Generate fake comments")
    parser.add_argument("--input", type=str, required=True, help="Path to the input dataset")
    parser.add_argument("--output", type=str, required=True, help="Path to the output dataset")
    args = parser.parse_args()
    main(args.input, args.output)