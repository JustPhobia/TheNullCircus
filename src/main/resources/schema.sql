CREATE
DATABASE  IF NOT EXISTS `thenullcircus` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE
`thenullcircus`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: thenullcircus
-- ------------------------------------------------------
-- Server version	8.4.7
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
--
-- Table structure for table `posts`
--
DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts`
(
    `postId`      char(36)     NOT NULL,
    `body`        varchar(400) NOT NULL,
    `userId`      char(36)              DEFAULT NULL,
    `timestamp`   datetime     NOT NULL,
    `upvotes`     int          NOT NULL DEFAULT 0,
    `downvotes`   int          NOT NULL DEFAULT 0,
    `comments`    varchar(255)          DEFAULT NULL,
    `status`      enum('pending','approved','rejected') NOT NULL DEFAULT 'pending',
    `moderatedBy` char(36)              DEFAULT NULL,
    PRIMARY KEY (`postId`),
    UNIQUE KEY `postId_UNIQUE` (`postId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users`
(
    `userId`     char(36)     NOT NULL,
    `name`       varchar(65)  NOT NULL,
    `surname`    varchar(65)  NOT NULL,
    `username`   varchar(30)  NOT NULL,
    `password`   varchar(255) NOT NULL,
    `email`      varchar(255) NOT NULL,
    `gender`     enum('MALE', 'FEMALE', 'NON_BINARY', 'OTHER') NOT NULL,
    `clown`      tinyint DEFAULT NULL,
    `ringleader` tinyint DEFAULT NULL,
    PRIMARY KEY (`userId`),
    UNIQUE KEY `userId_UNIQUE` (`userId`),
    UNIQUE KEY `username_UNIQUE` (`username`),
    UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `votes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `votes` (
    `voteId`  char(36) NOT NULL,
    `postId`  char(36) NOT NULL,
    `userId`  char(36) NOT NULL,
    `type`    enum('UPVOTE','DOWNVOTE') NOT NULL,
    PRIMARY KEY (`voteId`),
    UNIQUE KEY `user_post_UNIQUE` (`postId`, `userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `role_requests` (
    `requestId`   char(36)     NOT NULL,
    `userId`      char(36)     NOT NULL,
    `requestedRole` enum('CLOWN','RINGLEADER') NOT NULL,
    `reason` VARCHAR(120) NOT NULL,
    `status`      enum('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    `ringleaderId`  char(36)     DEFAULT NULL,
    PRIMARY KEY (`requestId`),
    UNIQUE KEY `requestId_UNIQUE` (`requestId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
-- Dump completed on 2026-05-06  9:23:24