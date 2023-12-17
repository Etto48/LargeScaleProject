# Collections

## DocumentDB

### Videogames (Document)

- Name
- Released
- Publishers
- Developers
- Genre
- ...
- Average score
- Number of reviews
- Top 3 reviews (by likes)
- Review list (with only id score and date)

### Reviews (Document)

- GameId
- Score
- Quote
- Author
- Date
- Number of likes

### Comments (Document)

- ReviewId (only for base comments)
- Quote
- Author
- Date
- Responses (recursive)

### Users (Document)

- Username
- Password (hashed)
- Email
- Image data (optional)
- Role (Admin, Company Manager, Reviewer)
- Top 3 reviews (by likes) (only for reviewers)

### Companies (Document)

- Name
- Overview
- Top 3 games (by average score)

## GraphDB

### Entities

#### Users (Graph)

- Username

#### Games (Graph)

- Name

#### Genres (Graph)

- Name

#### Reviews (Graph)

- Id

### Relationships

#### Follows

User -> User

#### Liked (Game)

User -> Game

#### Liked (Review)

User -> Review

#### Posted (?)

User -> Review

- Date

#### BelongsTo

Game -> Genre
