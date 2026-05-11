# 🎪 The Null Circus

**A High-Performance Networked Joke-Sharing Ecosystem**

---

### "Where the logic is tight, but the jokes are loose."

Built with a custom JSON-over-TCP protocol and a robust, threaded Swing architecture.

[Explore Docs]() • [View Features]() • [Setup Guide]()

---

## 👥 The Creative Crew

| Name | Primary Focus |
| --- | --- |
| **Jarryd** | Backend Architecture & Logic |
| **James** | UI/UX & Theming |
| **Christan** | Network Protocols & Deployment |

---

## 🎭 Role-Based Permissions

The system dynamically adapts the interface and capabilities based on the authenticated user:

* **Audience** (`MEMBER`)
> The critics. Can browse the feed and cast real-time upvotes/downvotes.


* **Clown** (`CLOWN`)
> The talent. Can create new jokes and access a private dashboard to track post performance.


* **Ringleader** (`MODERATOR`)
> The management. Full moderation powers to approve/reject pending jokes and role upgrade requests.



---

## 🛠️ Key Features

* **Real-Time Threaded Feed:** Non-blocking `SwingWorker` implementations ensure a butter-smooth UI while fetching data.
* **Smart Refresh Engine:** A custom `refreshCurrentPanel` system that re-syncs state without requiring a full re-login.
* **Joke of the Day (JOTD):** A scheduled server-side service that identifies the highest-rated content every 24 hours.
* **Surgical Logging:** A custom tag-based logging engine (`[UI_NAV]`, `[DB_INIT]`, `[VOTE_ACTION]`) for professional-grade debugging.
* **Connection Pooling:** Powered by **HikariCP** for enterprise-level database performance.

---

## 📂 Project Structure

```text
src/main/java/com/thenullcircus/
├── model/       # Domain Entities (User, Post, Vote) & Enums
├── dao/         # Data Access Layer (HikariCP / SQL logic)
├── network/     # Custom TCP Client & Protocol Handlers
├── controller/  # Server-side Handlers & Background Services
├── view/        # Swing UI Components & Layout Management
└── util/        # Logging Engine, Session Persistence, & Theme Config

```

---

## 🚀 Getting Started

### 1. Database Initialization

Execute the SQL schema found in `src/main/resources/schema.sql`. This will set up the `thenullcircus` database and prepare the tables.

### 2. Configuration

Create a `.env` file in the project root to handle sensitive credentials:

```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/thenullcircus
DB_USER=your_user
DB_PASSWORD=your_password

# Network Configuration
SERVER_HOST=localhost
SERVER_PORT=1234

```

### 3. Execution

1. **Launch the Server:** `com.thenullcircus.controller.server.ServerMain`
2. **Launch the Client:** `com.thenullcircus.view.App`

---

## 🛡️ Technical Implementation Notes

> [!IMPORTANT]
> **Logging Protocol:** All logs are persisted to `logs/thenullcircuslog`. When troubleshooting, look for specific bracketed tags like `[FATAL]` or `[NAV_ROUTER]` to pinpoint the failure origin within the multi-threaded environment.

---
