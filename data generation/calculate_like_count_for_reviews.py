import pandas as pd
import argparse
import json
import os

def int_to_id(x: int) -> str:
    ret = hex(x)[2:]
    ret = '0' * (24 - len(ret)) + ret
    return ret

def main(args):
    like_dict = {}
    print("Reading likes...")
    df = pd.read_csv(args.likes)
    for i in range(len(df)):
        if(i % 100 == 0):
            print(f"Processing {i+1}/{len(df)} {i/len(df)*100:.01f}%", end="\033[J\r")
        review_id = int_to_id(df.iloc[i]['reviewId'])
        if review_id not in like_dict:
            like_dict[review_id] = 1
        else:
            like_dict[review_id] += 1
    print()
    os.makedirs(os.path.dirname(args.output), exist_ok=True)
    with open(args.output, 'w') as f:
        json.dump(like_dict, f, indent=4)
    print(f"Done! {len(like_dict)} reviews have been processed.")

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--likes', type=str, default="dataset/likes/likes.csv")
    parser.add_argument('--output', type=str, default='dataset/reviews/reviews_info.json')
    args = parser.parse_args()
    main(args)