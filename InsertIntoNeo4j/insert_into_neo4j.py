import neo4j as neo
import argparse
import os
import json
import pandas as pd

def clear_tmp_folder(tmp_folder):
    if os.path.exists(tmp_folder):
        for f in os.listdir(tmp_folder):
            os.remove(os.path.join(tmp_folder, f))
        os.rmdir(tmp_folder)
        
def prepare_path(path) -> str:
    return os.path.abspath(path).replace("\\", "/").replace(" ","%20")

def id_to_hex_str(id: int) -> str:
    ret = hex(id)[2:]
    return "0" * (24 - len(ret)) + ret

def copy_file_to_server(file_path: str, server_path: str, server_addr: str, server_user: str):
    os.system(f"scp {file_path} {server_user}@{server_addr}:{server_path}")

def move_files(src: str, remote: bool, ):
    if remote:
        for file in os.listdir(src):
            copy_file_to_server(os.path.join(src, file), "/var/lib/neo4j/import", "10.1.1.71", "root")
            

def main(args):
    if args.remote:
        NEO4J_URI = "bolt://10.1.1.71:7687"
    else:
        NEO4J_URI = "bolt://localhost:7687"
    NEO4J_AUTH = ()
    if (args.auth):
        NEO4J_AUTH = (input("Neo4j username: "), input("Neo4j password: "))
    users = json.load(open(args.users))
    cm_and_admins = json.load(open(args.cm_and_admins))
    games = json.load(open(args.games))
    likes = pd.read_csv(args.likes)
    likes["reviewId"] = likes["reviewId"].map(id_to_hex_str)
    
    reviews = []
    review_id = 1
    for game in games:
        for review in game["reviews"]:
            simplified_review = {
                "author": review["author"],
                "score": max(min(int(review["score"]),10),1),
                "game": game["Name"],
                "id": id_to_hex_str(review_id),
            }
            review_id += 1
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
    pd.read_csv(args.follows).to_csv(os.path.join(args.tmp, "follows.csv"), index=False)
    likes.to_csv(os.path.join(args.tmp, "likes.csv"), index=False)
    
    move_files(args.tmp, args.remote)
        
    with neo.GraphDatabase.driver(NEO4J_URI, auth=NEO4J_AUTH) as driver:
        tmp_path = prepare_path(args.tmp)
        add_path = ""
        if args.use_full_path:
            if args.remote:
                add_path = "/var/lib/neo4j/import/"
            else:
                add_path = f"{tmp_path}/"
        
        
        with driver.session() as session:
        
            session.run(
                f"""
                match (n) 
                call {{
                    with n
                    detach delete n
                }} in transactions of {args.batch_size} rows
                """
            )
            
            print("Creating indexes...")
            session.run(
                """
                drop index username_index if exists
                """
            )
            session.run(
                """
                drop index game_index if exists
                """
            )
            session.run(
                """
                drop index review_index if exists
                """
            )
            session.run(
                """
                create index username_index if not exists for (u:User) on (u.username)
                """
            ).consume()
            session.run(
                """
                create index game_index if not exists for (g:Game) on (g.name)
                """
            )
            session.run(
                """
                create index review_index if not exists for (r:Review) on (r.reviewId)
                """
            )
            
            
            print("Inserting users...")
            session.run(
                f"""
                load csv with headers from "file:///{add_path}users.csv" as row
                call {{
                    with row
                    create (u:User {{username: row.usernname}})
                }} in transactions of {args.batch_size} rows
                """
            )
            
            print("Inserting games...")
            session.run(
                f"""
                load csv with headers from "file:///{add_path}games.csv" as row
                call {{
                    with row
                    create (g:Game {{name: row.name}})
                }} in transactions of {args.batch_size} rows
                """
            )
            
            print("Inserting reviews...")
            session.run(
                f"""
                load csv with headers from "file:///{add_path}reviews.csv" as row
                call {{
                    with row
                    match (u:User {{username: row.author}})
                    match (g:Game {{name: row.game}})
                    create (u)-[:WROTE]->(r:Review {{reviewId: row.id, score: toInteger(row.score)}})
                    create (r)-[:ABOUT]->(g)
                }} in transactions of {args.batch_size} rows
                """
            )
            
            print("Inserting likes...")
            session.run(
                f"""
                load csv with headers from "file:///{add_path}likes.csv" as row
                call {{
                    with row
                    match (u:User {{username: row.name}})
                    match (r:Review {{reviewId: row.reviewId}})
                    create (u)-[:LIKED]->(r)
                }} in transactions of {args.batch_size} rows
                """
            )
            
            print("Inserting follows...")
            session.run(
                f"""
                load csv with headers from "file:///{add_path}follows.csv" as row
                call {{
                    with row
                    match (follower:User {{username: row.follower}})
                    match (followed:User {{username: row.followed}})
                    create (follower)-[:FOLLOWS]->(followed)
                }} in transactions of {args.batch_size} rows
                """
            )
            
        
        print("Clearing tmp folder...")
        clear_tmp_folder(args.tmp)
        
        print("All data inserted correctly!")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Insert data into Neo4j')
    parser.add_argument('--users', type=str, help='File with users', default="./dataset/users/users.json")
    parser.add_argument('--cm_and_admins', type=str, help='File with CM and admins', default="./dataset/users/cm_and_admins.json")
    parser.add_argument('--games', type=str, help='File with games', default="./dataset/games/commented_games.json")
    parser.add_argument('--likes', type=str, help='File with likes', default="./dataset/likes/likes.csv")
    parser.add_argument('--follows', type=str, help='File with follows', default="./dataset/follows/follows.csv")
    parser.add_argument('--tmp', type=str, help='tmp folder', default="./tmp")
    parser.add_argument('--auth', action='store_true', help='Use authentication', default=False)
    parser.add_argument('--batch-size', type=int, help='Batch size', default=1000)
    parser.add_argument('--remote', action='store_true', help='Use remote server', default=False)
    parser.add_argument('--use-full-path', type=bool, help='Use full path, use this when the import path is disabled in the neo4j config', default=True)
    args = parser.parse_args()
    main(args)