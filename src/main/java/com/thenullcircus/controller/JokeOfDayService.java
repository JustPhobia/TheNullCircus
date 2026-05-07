package com.thenullcircus.controller;

import com.thenullcircus.dao.PostDAO;
import com.thenullcircus.dao.PostDAOImpl;
import com.thenullcircus.model.Post;
import lombok.Getter;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JokeOfDayService {

    //Instance Variables
    private Logger logger = Logger.getLogger(JokeOfDayService.class.getName());
    public PostDAO postDAO;
    private ScheduledExecutorService scheduledExecutorService;
    @Getter
    private volatile Post cachedJoke;

    //Constructor
    public JokeOfDayService() {
        this.postDAO = new PostDAOImpl();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    //Methods
    public void start(){
        scheduledExecutorService.scheduleAtFixedRate(
                this::refreshCache,
                0,
                1,
                TimeUnit.HOURS
        );
        logger.info("JokeOfDayService started - refreshing every hour");
    }

    public void stop(){
        scheduledExecutorService.shutdown();
        logger.info("JokeOfDayService stopped ");
    }

    public void refreshCache(){
        try {
            Post joke = postDAO.findJokeOfTheDay();
            cachedJoke = joke;
            if (joke != null) {
                logger.info("Joke Of The Day refreshed: " + joke.getPostId());
            }else {
                logger.info("No approved posts in the last 24 hours.");
            }
        } catch (Exception e) {
            logger.severe("Failed to refresh Joke Of The Day: " + e.getMessage());
        }
    }


}
