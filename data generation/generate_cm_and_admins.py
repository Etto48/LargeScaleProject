import json
import argparse
import random
from generate_users import gen_mail, gen_password_hash, gen_image_data

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

def minify_username(company_name: str) -> str:
    username = company_name.lower()
    # remove non-alphanumeric characters
    username = "".join([c for c in username if c.isalnum()])
    username = username.split(" ")[0]
    return username
    
def gen_admin_username() -> str:
    return "gc_" + random_author()
    
def gen_admin(username: str) -> dict:
    return {
        "username": username,
        "email": gen_mail(username),
        "password_hash": gen_password_hash(username),
        "image": gen_image_data(username, "images/image_cache"),
        "is_admin": True
    }

def gen_company_manager(company_name) -> dict:
    username = minify_username(company_name)
    return {
        "username": username,
        "email": gen_mail(username),
        "password_hash": gen_password_hash(username),
        "image": gen_image_data(username, "images/image_cache"),
        "company_name": company_name
    }

def main(args):
    print("Loading data...")
    with open(args.companies, "r") as f:
        companies = json.load(f)
    print("Generating company managers...")
    managers = []
    manager_usernames = set()
    for i,company in enumerate(companies):
        if random.random() < 0.2:
            print(f"Generating company manager {i+1}/{len(companies)} ({i/len(companies)*100:.02f}%)...",end="\033[J\r")
            company_manager = gen_company_manager(company["Name"])
            if company_manager["username"] not in manager_usernames:
                managers.append(company_manager)
                manager_usernames.add(company_manager["username"])
    
    print()
    print("Generating admins...")
    admins = []
    admin_usernames = set()
    for i in range(10):
        admin_username = gen_admin_username()
        if admin_username not in admin_usernames:
            admins.append(gen_admin(admin_username))
            admin_usernames.add(admin_username)
    
    print(f"Saving users to {args.output}...")
    json.dump(managers+admins, open(args.output, "w"), indent=4)
    


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--companies", type=str, help="Path to the companies file")
    parser.add_argument("--output", type=str, help="Path to the output file")
    args = parser.parse_args()
    main(args)