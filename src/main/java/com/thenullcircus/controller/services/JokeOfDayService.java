package com.thenullcircus.controller.services;

import com.thenullcircus.dao.PostDAO;
import com.thenullcircus.dao.PostDAOImpl;
import com.thenullcircus.model.Post;
import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class JokeOfDayService {

    //Instance Variables
    private final Logger logger = Logger.getLogger(JokeOfDayService.class.getName());
    public PostDAO postDAO;
    private final ScheduledExecutorService scheduledExecutorService;
    @Getter
    private volatile Post cachedJoke;

    //Constructor
    public JokeOfDayService() {
        this.postDAO = new PostDAOImpl();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    //Methods
    public void start(){
        logger.info("[JOKE_SERVICE] Booting JokeOfDayService. Setting refresh rate to 1 HOUR.");
        scheduledExecutorService.scheduleAtFixedRate(
                this::refreshCache,
                0,
                1,
                TimeUnit.HOURS
        );
        logger.info("JokeOfDayService started - refreshing every hour");
    }

    public void stop(){
        logger.info("[JOKE_SERVICE] Shutting down ScheduledExecutorService.");
        scheduledExecutorService.shutdown();
        logger.info("JokeOfDayService stopped ");
    }

    public void refreshCache(){
        logger.fine("[JOKE_SERVICE] Executing scheduled cache refresh...");
        try {
            Post joke = postDAO.findJokeOfTheDay();
            cachedJoke = joke;
            if (joke != null) {
                logger.info("[JOKE_SERVICE] SUCCESS: Joke Of The Day updated. ID: " + joke.getPostId());
            } else {
                logger.info("[JOKE_SERVICE] CACHE EMPTY: No approved posts found in the last 24-hour window.");
            }
        } catch (Exception e) {
            logger.severe("[JOKE_SERVICE] FAILED to refresh cache from DAO: " + e.getMessage());
        }
    }
}