# The Null Circus 🎪

A networked joke-sharing platform built with Java, MySQL, and Swing.
Users can register, post jokes, vote on them, and moderate content
through a role-based system.

## Team
- Jarryd
- James
- Christan

---

## Roles
- **Audience** — view and vote on jokes
- **Clown** — create and post jokes
- **Ringleader** — moderate and approve/reject posts

---

## Tech Stack
- Java 17
- MySQL 8.4
- Swing (UI)
- JDBC (database connectivity)
- Jackson (JSON / networking)
- Dotenv (credential management)
- Lombok (boilerplate reduction)

---

## Setup

### Prerequisites
- Java 17
- MySQL 8.4
- Maven

### Database
1. Open MySQL and run the schema:
src/main/resources/schema.sql
2. This will create the `thenullcircus` database and all tables.

### Environment Variables
Create a `.env` file in the project root — this file is git-ignored
and must never be committed:
DB_URL=jdbc:mysql://localhost:3306/thenullcircus
DB_USER=your_username
DB_PASSWORD=your_password

### Running the Server
Run the server on the designated machine (Christan's laptop):
com.thenullcircus.controller.server.ServerMain

### Connecting a Client
Each team member runs the client on their own machine:
com.thenullcircus.view.App

---

## Project Structure
src/main/java/com/thenullcircus/
├── model/          — POJOs and enums (User, Post, Role, Status, Gender)
├── dao/            — Database access objects
├── controller/     — Business logic and services
├── view/           — Swing UI panels
└── util/           — DatabaseConnection, LoggerUtil

---

## Features
- User registration and login
- Role-based navigation (Audience / Clown / Ringleader)
- Joke feed with upvote and downvote
- Moderation queue for Ringleaders
- Joke of the Day — highest rated joke in the last 24 hours
- Account upgrade and downgrade requests
