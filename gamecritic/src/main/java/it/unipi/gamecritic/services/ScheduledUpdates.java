package it.unipi.gamecritic.services;

import it.unipi.gamecritic.repositories.Game.GameRepository;
import it.unipi.gamecritic.repositories.User.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledUpdates {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledUpdates.class);
    public ScheduledUpdates(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }
    // Every day at 3:00 AM
    @Scheduled(cron = "0 3 * * *")
    public void updateTop3ReviewsForUsersAndGames() {
        logger.info("Updating top 3 reviews for users and games");
        gameRepository.updateTop3ReviewsByLikes();
        userRepository.updateTop3ReviewsByLikes();
    }

}
