package it.unipi.gamecritic.repositories.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.unipi.gamecritic.Util;
import it.unipi.gamecritic.entities.user.User;

@Component
public class UserAsyncRepository {
    private final UserRepositoryNeo4J userRepositoryNeo4J;
    private static final Logger logger = LoggerFactory.getLogger(UserAsyncRepository.class);

    public UserAsyncRepository(UserRepositoryNeo4J userRepositoryNeo4J) {
        this.userRepositoryNeo4J = userRepositoryNeo4J;
    }

    @Async
    public void completeInsertIfNotExists(User user)
    {
        if(!Util.retryFor(() -> {
            userRepositoryNeo4J.insertUserIfNotExists(user.username);
        }))
        {
            logger.error("Error while inserting user into Neo4J");
        }
    } 
}
