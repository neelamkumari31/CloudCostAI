## 🛠️ Complete Local Installation & Deployment Guide

Follow these steps in order to spin up the entire full-stack event-driven pipeline on your local machine.

### Prerequisites
Ensure you have the following installed on your machine:
* **Java 21 JDK** or higher
* **Node.js** (v18 or higher) & **npm**
* **Docker Desktop** (or a local installation of PostgreSQL and Apache Kafka)

---

### Step 1: Initialize Infrastructure Containers (Kafka & Postgres)
The application expects an active Apache Kafka event broker and a PostgreSQL instance running locally. 

If you are using Docker, create a `docker-compose.yml` file in your root folder and launch the ecosystem:
```cmd
docker-compose up -d
```
*   **Database Requirement:** Ensure a database named `cloudcost` exists. The credentials should match `username: postgres` and `password: postgres` (or your configured settings inside `application.properties`).
*   **Kafka Requirement:** Ensure a broker is active on port `9092` with an established topic named `cloud-metrics`.

---

### Step 2: Secure Backend Configuration
Navigate to your resources directory (`backend/src/main/resources/`) and create a private properties file to house your secure keys. This file is ignored by Git and will not leak to production.

```properties
# Create a file named: application-local.properties
spring.ai.google.api-key=YOUR_ACTUAL_GEMINI_API_KEY
spring.mail.username=YOUR_GMAIL_ACCOUNT@gmail.com
spring.mail.password=YOUR_16_CHARACTER_GMAIL_APP_PASSWORD
```

---

### Step 3: Launch the Core Application Instances

To run this pipeline completely, you must open **three separate terminal tabs** to keep the decoupled processes alive side-by-side:

#### Terminal 1: Start the Spring Boot Backend Engine
This launches the REST endpoints, binds the Hibernate ORM layer, connects to PostgreSQL, and activates the live Gemini AI service loop.
```cmd
cd backend
.\mvnw.cmd spring-boot:run
```

#### Terminal 2: Start the Continuous Data Stream Simulator
This executes the scheduled cron-producer loop that generates random telemetry scores (simulating heavy enterprise cluster tracking data) and feeds them directly into your Kafka topic.
```cmd
cd backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--spring.main.web-application-type=none"
```
*(Note: If your data producer logic is stored inside an independent project file or subclass, run its matching main Java class executor instead).*

#### Terminal 3: Start the React Frontend UI Dashboard
This compiles your frontend components through the high-speed Vite server engine and exposes your visual FinOps workspace.
```cmd
cd frontend
npm install
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
npm run dev
```

---

### Step 4: Verify the End-to-End Pipeline
1. Open your web browser tab to **`http://localhost:5173/`** to load the visual monitoring interface.
2. Watch your Backend Terminal log stream. Once every minute, an underutilized server metric will trigger, call Gemini over the web, commit a secure token rows to your database, and fire a styled notification message.
3. Open your mailbox—you will find an interactive **Approve & Execute** button email. 
4. Click the button from either your email inbox client or your live dashboard webpage grid table to execute the script and watch your financial metrics update live!


