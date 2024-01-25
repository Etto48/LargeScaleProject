import neo4j as neo
import argparse
import os
import json
import pandas as pd

NEO4J_URI = "bolt://localhost:7687"
NEO4J_AUTH = ()

def clear_tmp_folder(tmp_folder):
    if os.path.exists(tmp_folder):
        for f in os.listdir(tmp_folder):
            os.remove(os.path.join(tmp_folder, f))
        os.rmdir(tmp_folder)

def main(args):
    users = json.load(open(args.users))
    cm_and_admins = json.load(open(args.cm_and_admins))
    games = json.load(open(args.games))
    
    reviews = []
    for game in games:
        for review in game["reviews"]:
            simplified_review = {
                "author": review["author"],
                "game": game["Name"],
            }
            reviews.append(simplified_review)
    simple_users = []
    for user in users:
        simple_user = {
            "usernname": user["username"],
        }
        simple_users.append(simple_user)
    for user in cm_and_admins:
        simple_user = {
            "usernname": user["username"],
        }
        simple_users.append(simple_user)
        
    simple_games = []
    for game in games:
        simple_game = {
            "name": game["Name"],
        }
        simple_games.append(simple_game)
    
    clear_tmp_folder(args.tmp)
    os.makedirs(args.tmp, exist_ok=True)
    
    pd.DataFrame(simple_games).to_csv(os.path.join(args.tmp, "games.csv"), index=False)
    pd.DataFrame(simple_users).to_csv(os.path.join(args.tmp, "users.csv"), index=False)
    pd.DataFrame(reviews).to_csv(os.path.join(args.tmp, "reviews.csv"), index=False)
        
    with neo.GraphDatabase.driver(NEO4J_URI, auth=NEO4J_AUTH) as driver:
        tmp_path = os.path.abspath(args.tmp).replace("\\", "/").replace(" ","%20")
        driver.execute_query(
            """
            match (n) detach delete n
            """
        )
        
        print("Creating indexes...")
        driver.execute_query(
            """
            create index username_index if not exists for (u:User) on (u.username)
            """
        )
        driver.execute_query(
            """
            create index game_index if not exists for (g:Game) on (g.name)
            """
        )
        
        print("Inserting users...")
        driver.execute_query(
            f"""
            load csv with headers from "file:///{tmp_path}/users.csv" as row
            create (u:User {{username: row.usernname}})
            """
        )
        
        print("Inserting games...")
        driver.execute_query(
            f"""
            load csv with headers from "file:///{tmp_path}/games.csv" as row
            create (g:Game {{name: row.name}})
            """
        )
        
        print("Inserting reviews...")
        driver.execute_query(
            f"""
            load csv with headers from "file:///{tmp_path}/reviews.csv" as row
            match (u:User {{username: row.author}})
            match (g:Game {{name: row.game}})
            create (u)-[:WROTE]->(r:Review {{text: row.text, rating: row.rating}})
            create (r)-[:ABOUT]->(g)
            """
        )
        
        print("Clearing tmp folder...")
        clear_tmp_folder(args.tmp)
        
        print("All data inserted correctly!")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Insert data into Neo4j')
    parser.add_argument('--users', type=str, help='File with users', default="./users/users.json")
    parser.add_argument('--cm_and_admins', type=str, help='File with CM and admins', default="./users/cm_and_admins.json")
    parser.add_argument('--games', type=str, help='File with games', default="./games/commented_games.json")
    parser.add_argument('--tmp', type=str, help='tmp folder', default="./tmp")
    args = parser.parse_args()
    main(args)