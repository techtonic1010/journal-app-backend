# 📔 Journal App - Full Stack Application

A modern, feature-rich journal application built with Spring Boot backend and vanilla JavaScript frontend. This application enables users to create, manage, and analyze their daily journal entries with sentiment tracking, email notifications, and advanced search capabilities.

## 🌟 Features

### Core Functionality
- **User Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (USER, ADMIN)
  - Secure password encryption
  - Session management

- **Journal Entry Management**
  - Create, read, update, and delete journal entries
  - Rich text support for journal content
  - Date and timestamp tracking
  - Sentiment analysis for entries

- **Advanced Features**
  - 🔍 **Smart Search & Filtering** - Filter entries by date range, sentiment, and keywords
  - 📊 **Sentiment Analysis** - Automatic emotion detection using Kafka-based event processing
  - 📧 **Email Notifications** - Scheduled email reminders and updates
  - 💾 **Redis Caching** - High-performance data caching layer
  - 📖 **Pagination** - Efficient handling of large datasets
  - 🌤️ **Weather Integration** - Track weather conditions with your entries
  - 📅 **Scheduled Tasks** - Automated background jobs for maintenance

- **Administrative Features**
  - User management
  - System health monitoring
  - Configuration management
  - Admin-only endpoints

### Technical Highlights
- RESTful API design
- MongoDB for data persistence
- Redis for caching
- Apache Kafka for event streaming
- SpringDoc OpenAPI/Swagger documentation
- Comprehensive exception handling
- Structured logging
- CORS configuration for frontend integration

---

## 🏗️ Architecture

```
journal_entries/
├── journal-app-backend/     # Spring Boot backend
├── journal-app-frontend/    # Vanilla JS frontend
└── logs/                    # Application logs
```

### Tech Stack

#### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** MongoDB
- **Cache:** Redis
- **Message Broker:** Apache Kafka 3.7.0
- **Security:** Spring Security + JWT (JJWT 0.12.5)
- **Documentation:** SpringDoc OpenAPI 2.5.0
- **Build Tool:** Maven
- **Email:** Spring Mail (SMTP)

#### Frontend
- **HTML5**
- **CSS3** (Modern responsive design)
- **Vanilla JavaScript** (ES6+)
- **No frameworks** - lightweight and fast

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+**
- **MongoDB** (local or cloud instance)
- **Redis** (for caching)
- **Apache Kafka** (for sentiment analysis events) - Optional
- **Node.js** (optional, for frontend dev server)
- **Git**

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd journal_entries
```

### 2. Backend Setup

#### Configure Environment Variables

Create a `.env` file in the `journal-app-backend` directory:

```env
# MongoDB Configuration
MONGO_URI=mongodb://localhost:27017/journalapp

# Redis Configuration (if using)
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your-secret-key-here-make-it-long-and-secure

# Email Configuration
GMAIL_USERNAME=your-email@gmail.com
GMAIL_PASSWORD=your-app-password

# Kafka Configuration (if using)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_API_KEY=your-kafka-api-key
KAFKA_API_SECRET=your-kafka-api-secret

# Weather API (if using)
WEATHER_API_KEY=your-weather-api-key
```

#### Install Dependencies & Build

```bash
cd journal-app-backend
./mvnw clean install
```

Or on Windows:
```bash
mvnw.cmd clean install
```

#### Run the Backend

**Development Mode:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production Mode:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

The backend will start at `http://localhost:8080`

#### Verify Backend is Running

```bash
curl http://localhost:8080/public/health-check
```

### 3. Frontend Setup

#### Configure API Endpoint

Edit `journal-app-frontend/app.js` and update the API base URL if needed:

```javascript
const API_BASE_URL = 'http://localhost:8080';
```

#### Run the Frontend

**Option 1: Using Python HTTP Server**
```bash
cd journal-app-frontend
python3 -m http.server 3000
```

**Option 2: Using Node.js HTTP Server**
```bash
cd journal-app-frontend
npx http-server -p 3000
```

**Option 3: Using Live Server (VS Code)**
- Install "Live Server" extension
- Right-click `index.html` → "Open with Live Server"

Access the application at `http://localhost:3000`

---

## 🔧 Configuration

### Application Profiles

The backend supports multiple profiles:

- **dev** - Development environment (detailed logging, local services)
- **prod** - Production environment (optimized settings, cloud services)

Configure in `application.properties`:
```properties
spring.profiles.active=dev
```

### Key Configuration Files

- `application.properties` - Main configuration
- `application-dev.yml` - Development settings
- `application-prod.yml` - Production settings
- `logback-spring.xml` - Logging configuration

---

## 📚 API Documentation

Once the backend is running, access the interactive API documentation:

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Main Endpoints

#### Public Endpoints (No authentication required)
```
POST   /public/signup          - Create new user account
POST   /public/login           - Authenticate user
GET    /public/health-check    - Check API health
```

#### User Endpoints (Authentication required)
```
GET    /journal                - Get all journal entries (with pagination & filters)
POST   /journal                - Create new journal entry
GET    /journal/id/{id}        - Get specific journal entry
PUT    /journal/id/{id}        - Update journal entry
DELETE /journal/id/{id}        - Delete journal entry
GET    /user/ping              - Test user authentication
GET    /user/greet             - Get personalized greeting
```

#### Admin Endpoints (Admin role required)
```
GET    /admin                  - Get all users
POST   /admin                  - Create admin user
```

### Query Parameters for Filtering

```
GET /journal?page=0&size=10&sentiment=HAPPY&startDate=2024-01-01&endDate=2024-12-31&keyword=vacation
```

---

## 🗂️ Project Structure

### Backend Structure

```
journal-app-backend/
├── src/main/java/
│   ├── cache/              # Caching logic (Redis)
│   ├── config/             # Configuration classes
│   │   ├── Security.java   # Security & JWT config
│   │   ├── RedisConfig.java
│   │   ├── CorsConfig.java
│   │   ├── producer.java   # Kafka producer
│   │   └── consumer.java   # Kafka consumer
│   ├── dto/                # Data Transfer Objects
│   ├── entities/           # MongoDB entities
│   │   ├── User.java
│   │   ├── JournalEntry.java
│   │   └── weather.java
│   ├── JournalAppController/  # REST Controllers
│   │   ├── publicController.java
│   │   ├── userContorller.java
│   │   ├── adminController.java
│   │   └── JournalApplication_v2.java
│   ├── jwtfilter/          # JWT authentication filter
│   ├── repositary/         # MongoDB repositories
│   ├── service/            # Business logic layer
│   ├── scheduler/          # Scheduled tasks
│   ├── Sentiment/          # Sentiment analysis
│   ├── exception/          # Exception handlers
│   └── utils/              # Utility classes
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── logback-spring.xml
└── pom.xml
```

### Frontend Structure

```
journal-app-frontend/
├── index.html       # Main HTML file
├── app.js          # JavaScript logic
├── styles.css      # Styling
└── README.md       # Frontend documentation
```

---

## 🧪 Testing

### Run Backend Tests

```bash
cd journal-app-backend
./mvnw test
```

### Test Coverage

The project includes:
- Unit tests for services
- Repository tests
- Integration tests

---

## 🔒 Security

- **Authentication:** JWT tokens with configurable expiration
- **Password Security:** BCrypt hashing
- **CORS:** Configured for frontend integration
- **SQL Injection:** Protected by MongoDB parameterized queries
- **XSS Protection:** Input validation and sanitization
- **HTTPS:** Recommended for production deployment

---

## 📊 Monitoring & Logging

### Logs Location
- Development: `journal-app-backend/logs/`
- Application logs: Configured via `logback-spring.xml`
- Access logs: Tomcat access logs enabled in dev profile

### Health Monitoring
```bash
GET /public/health-check
```

### Redis Testing
```bash
GET /journal/test-redis
GET /public/test-redis
```

---

## 🚀 Deployment

### Backend Deployment

#### Building for Production

```bash
cd journal-app-backend
./mvnw clean package -DskipTests
```

The JAR file will be created in `target/journalapp-1.0-SNAPSHOT.jar`

#### Running the Production Build

```bash
java -jar -Dspring.profiles.active=prod target/journalapp-1.0-SNAPSHOT.jar
```

#### Docker Deployment (Optional)

Create a `Dockerfile` in `journal-app-backend`:

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/journalapp-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t journal-app .
docker run -p 8080:8080 --env-file .env journal-app
```

### Frontend Deployment

The frontend can be deployed to:
- **GitHub Pages**
- **Netlify**
- **Vercel**
- **AWS S3 + CloudFront**
- Any static hosting service

Remember to update `API_BASE_URL` in `app.js` to your production backend URL.

---

## 🛠️ Development

### Hot Reload (Backend)

Using Spring Boot DevTools (add to `pom.xml` if not present):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Frontend Development

For live reload, use Live Server or any development server with watch mode.

### Code Style

- **Backend:** Follow Java naming conventions and Spring Boot best practices
- **Frontend:** Use ESLint and Prettier for consistent formatting

---

## 🐛 Troubleshooting

### Common Issues

#### MongoDB Connection Error
```
Error: Unable to connect to MongoDB
```
**Solution:** Ensure MongoDB is running and `MONGO_URI` is correct.

```bash
# Check MongoDB status
sudo systemctl status mongodb

# Start MongoDB
sudo systemctl start mongodb
```

#### Redis Connection Error
```
Error: Unable to connect to Redis
```
**Solution:** Ensure Redis is running.

```bash
# Check Redis
redis-cli ping
# Should return: PONG

# Start Redis
sudo systemctl start redis
```

#### Port Already in Use
```
Error: Port 8080 is already in use
```
**Solution:** Kill the process or change the port in `application.properties`:
```properties
server.port=8081
```

#### JWT Token Expired
**Solution:** Login again to get a new token.

#### CORS Issues
**Solution:** Verify `CorsConfig.java` includes your frontend URL.

---

## 📈 Future Enhancements

- [ ] Mobile application (React Native/Flutter)
- [ ] Voice-to-text journal entries
- [ ] Image/photo attachments
- [ ] Social features (share entries with friends)
- [ ] Advanced analytics dashboard
- [ ] Export journal as PDF/DOCX
- [ ] Multi-language support
- [ ] Dark mode theme
- [ ] Offline support with PWA
- [ ] AI-powered writing suggestions
- [ ] Calendar view for entries
- [ ] Tags and categories

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Write clear, descriptive commit messages
- Add unit tests for new features
- Update documentation as needed
- Follow existing code style and conventions

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Authors

**Parth Ratnaparkhi**

---

## 🙏 Acknowledgments

- Spring Boot Team for the excellent framework
- MongoDB for flexible data storage
- Redis for high-performance caching
- Apache Kafka for event streaming
- The open-source community

---

## 📧 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Contact: [Your Email]

---

## 🔗 Useful Links

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Redis Documentation](https://redis.io/documentation)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [JWT Introduction](https://jwt.io/introduction)

---

<div align="center">
  <p>Built with ❤️ using Spring Boot and JavaScript</p>
  <p>⭐ Star this repo if you find it helpful!</p>
</div>
