# GameCritic

## Initial Setup

To make #[insert_into_neo4j.py](insert_into_neo4j.py) work, you need to comment out the following line in neo4j.conf:

- If you have the community server

  ```conf
  server.directories.import=import
  ```

- If you have the desktop version

  ```conf
  dbms.directories.import=import
  ```
