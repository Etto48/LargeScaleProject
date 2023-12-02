import os
import json

input_directory = "./game_jsons"
output_file = "./combined_games.json"

combined_data = []

for filename in os.listdir(input_directory):
    if filename.endswith(".json"):
        file_path = os.path.join(input_directory, filename)
        with open(file_path, "r") as file:
            data = json.load(file)
            combined_data.extend(data)

with open(output_file, "w") as output_file:
    json.dump(combined_data, output_file, indent=2)

print(f"Combined data saved to {output_file}")