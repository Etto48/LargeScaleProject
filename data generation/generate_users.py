import json
import random
import hashlib
import base64
import argparse
import os
from io import BytesIO
from PIL import Image

# info:
# -username
# -email
# -password_hash
# -image_data
# -top_reviews (empty)


def gen_mail(username):
    providers = ["gmail", "yahoo", "outlook", "hotmail"]
    return f"{username}@{random.choice(providers)}.com"

def gen_password_hash(username: str):
    h = hashlib.sha256(username.encode()).digest()
    h = base64.b64encode(h).decode()
    return h

def gen_image_data(username, path, colors = 2, cells = 16, size = 256):
    if not os.path.exists(f"{path}/{username}.png"):
        image = Image.new("RGB", (size,size))
        pixel_size = size//cells
        color_1 = random.randint(0, 0xffffff)
        color_2 = random.randint(0, 0xffffff)
        white = 0xffffff
        colors = [color_1, color_2, white]
        for i in range(size//pixel_size//2):
            for j in range(size//pixel_size):
                color = colors[random.randint(0,2)]
                for x in range(pixel_size):
                    for y in range(pixel_size):
                        image.putpixel((i*pixel_size+x, j*pixel_size+y), color)
                        image.putpixel((size-(i+1)*pixel_size+x, j*pixel_size+y), color)
        image.save(f"{path}/{username}.png")
    with open(f"{path}/{username}.png", "rb") as f:
        return base64.b64encode(f.read()).decode()

def main():
    args = parser.parse_args()
    print("Loading data...")
    with open(args.input, "r") as f:
        data = json.load(f)
    usernames = set()
    for game in data:
        for review in game["reviews"]:
            usernames.add(review["author"])
    print("Generating users...")
    os.makedirs(args.image_cache, exist_ok=True)
    users = []
    for i,username in enumerate(usernames):
        if i % 10 == 0:
            print(f"Generating user {i+1}/{len(usernames)} ({i/len(usernames)*100:.02f}%)...",end="\033[J\r")
        user = {}
        user["username"] = username
        user["email"] = gen_mail(username)
        user["password_hash"] = gen_password_hash(str(username))
        user["image"] = gen_image_data(username, args.image_cache)
        user["top_reviews"] = []
        users.append(user)
    
    print(f"Saving users to {args.output}...")
    os.makedirs(os.path.dirname(args.output), exist_ok=True)
    with open(args.output, "w") as f:
        json.dump(users, f, indent=4)

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", type=str, help="Path to the input file")
    parser.add_argument("--output", type=str, help="Path to the output file")
    parser.add_argument("--image-cache", type=str, help="Path to the image cache")
    main()