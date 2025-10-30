# Quick Start Guide - Schedule Tool v5.0

## What Has Been Done

### ✅ Completed
1. **Utility Classes Created** - Replaced gsutils dependencies
   - `Logger.java` - Logging/debugging
   - `DateTimeUtils.java` - Date/time operations with Pacific timezone support
   - `Utils.java` - General utilities (parsing, file I/O, etc.)

2. **Database Layer Created** - MariaDB support
   - `DatabaseConnection.java` - Connection management
   - `DatabaseFactory.java` - Factory pattern for DB access
   - `DatabaseConfig.java` - Configuration holder

3. **Spring Boot Setup**
   - Maven `pom.xml` with all dependencies
   - Main application class
   - Configuration files (dev & production)

4. **Example Implementation** - Sport entity (full CRUD)
   - JPA Entity: `Sport.java`
   - Repository: `SportRepository.java`
   - Service: `SportService.java`
   - REST Controller: `SportController.java`

5. **Frontend Example**
   - HTML page with navigation
   - CSS styling
   - JavaScript for API calls
   - Sports management UI (working example)

## Prerequisites

### Required Software
1. **Java 11 or higher**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **MariaDB 10.5+**
   - Install MariaDB server
   - Create database: `schedule_tool`

### Database Setup

```sql
-- Create database
CREATE DATABASE schedule_tool CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional)
CREATE USER 'schedule_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON schedule_tool.* TO 'schedule_user'@'localhost';
FLUSH PRIVILEGES;

-- Create Sport table (example)
USE schedule_tool;

CREATE TABLE Sport (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO Sport (name, abbreviation, active) VALUES
('Basketball', 'NBA', TRUE),
('Football', 'NFL', TRUE),
('Baseball', 'MLB', TRUE),
('Hockey', 'NHL', TRUE);
```

## Running the Application

### Step 1: Configure Database
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/schedule_tool
spring.datasource.username=root
spring.datasource.password=your_password
```

### Step 2: Build the Project
```bash
mvn clean install
```

### Step 3: Run the Application
```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/schedule-tool-5.0.0.jar
```

### Step 4: Access the Application
- **Frontend**: http://localhost:8080
- **API**: http://localhost:8080/api/sports

## Testing the API

### Using cURL

```bash
# Get all sports
curl http://localhost:8080/api/sports

# Get sport by ID
curl http://localhost:8080/api/sports/1

# Create new sport
curl -X POST http://localhost:8080/api/sports \
  -H "Content-Type: application/json" \
  -d '{"name":"Soccer","abbreviation":"MLS","active":true}'

# Update sport
curl -X PUT http://localhost:8080/api/sports/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Basketball","abbreviation":"NBA","active":true}'

# Delete sport
curl -X DELETE http://localhost:8080/api/sports/1
```

### Using Browser
1. Open http://localhost:8080
2. Click "Sports" in navigation
3. Click "Refresh Sports" to load data
4. Click "Add Sport" to create new sport
5. Use Edit/Delete buttons to modify sports

## Next Steps

### Immediate Tasks

1. **Create Remaining Entities**
   Follow the Sport example to create:
   - League
   - Team
   - Player
   - Event
   - Category
   - Score
   - Venue
   - etc.

2. **Implement Relationships**
   Add JPA relationships between entities:
   ```java
   @ManyToOne
   @JoinColumn(name = "league_id")
   private League league;
   
   @OneToMany(mappedBy = "league")
   private List<Team> teams;
   ```

3. **Create More Controllers**
   - ScheduleController
   - CategoryController
   - EventController
   - ScoreController
   - etc.

4. **Build Frontend Pages**
   - Schedule view
   - Category management
   - Event creation/editing
   - Score updates
   - Lines/odds management

5. **Add Authentication**
   - Implement login endpoint
   - Add JWT or session-based auth
   - Secure API endpoints

6. **WebSocket Support**
   - Real-time score updates
   - Live schedule changes
   - Odds updates

### Migration from Old System

1. **Export Data from MSSQL**
   ```sql
   -- Export to CSV or use migration tool
   ```

2. **Convert Schema**
   - MSSQL → MariaDB syntax
   - Data type conversions
   - Stored procedures → Java code

3. **Import Data to MariaDB**
   ```bash
   mysql -u root -p schedule_tool < data_export.sql
   ```

4. **Test Data Integrity**
   - Verify all records migrated
   - Check relationships
   - Validate data types

## Project Structure

```
ScheduleTool/
├── pom.xml                          # Maven configuration
├── REFACTORING_GUIDE.md             # Detailed refactoring guide
├── QUICK_START.md                   # This file
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/scheduletool/
│   │   │       ├── ScheduleToolApplication.java    # Main class
│   │   │       ├── controller/                     # REST controllers
│   │   │       │   └── SportController.java
│   │   │       ├── service/                        # Business logic
│   │   │       │   └── SportService.java
│   │   │       ├── repository/                     # Data access
│   │   │       │   └── SportRepository.java
│   │   │       └── model/                          # JPA entities
│   │   │           └── Sport.java
│   │   └── resources/
│   │       ├── application.properties              # Configuration
│   │       ├── application-production.properties
│   │       └── static/                             # Frontend files
│   │           ├── index.html
│   │           ├── css/
│   │           │   └── style.css
│   │           └── js/
│   │               └── app.js
│   └── scheduletool/                # Old Swing code (reference)
│       ├── utils/                   # ✅ New utilities
│       └── database/                # ✅ New database layer
```

## Common Issues

### Issue: Port 8080 already in use
**Solution**: Change port in `application.properties`
```properties
server.port=8081
```

### Issue: Database connection failed
**Solution**: 
1. Check MariaDB is running: `systemctl status mariadb`
2. Verify credentials in `application.properties`
3. Check database exists: `SHOW DATABASES;`

### Issue: Maven build fails
**Solution**:
1. Clean Maven cache: `mvn clean`
2. Update dependencies: `mvn dependency:resolve`
3. Check Java version: `java -version` (must be 11+)

### Issue: Frontend not loading
**Solution**:
1. Check files are in `src/main/resources/static/`
2. Rebuild project: `mvn clean install`
3. Clear browser cache

## Development Tips

### Hot Reload
Spring Boot DevTools enables automatic restart on code changes.
Just save your Java files and the app will restart.

### Logging
Check logs in console or configure file logging:
```properties
logging.file.name=logs/schedule-tool.log
```

### Database Changes
After modifying entities, you may need to update the database schema.
For development, you can use:
```properties
spring.jpa.hibernate.ddl-auto=update
```
⚠️ **Never use this in production!**

### API Testing
Use tools like:
- Postman
- Insomnia
- Browser DevTools
- cURL

## Getting Help

1. Check `REFACTORING_GUIDE.md` for detailed architecture info
2. Review Spring Boot docs: https://spring.io/projects/spring-boot
3. Check MariaDB docs: https://mariadb.org/documentation/
4. Look at the Sport example for patterns to follow

## Production Deployment

### Build for Production
```bash
mvn clean package -DskipTests
```

### Run with Production Profile
```bash
java -jar target/schedule-tool-5.0.0.jar --spring.profiles.active=production
```

### Environment Variables
```bash
export SPRING_DATASOURCE_URL=jdbc:mariadb://prod-host:3306/schedule_tool
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
java -jar target/schedule-tool-5.0.0.jar
```

## Success Criteria

You'll know the refactoring is successful when:
- ✅ Application starts without errors
- ✅ Can access frontend at http://localhost:8080
- ✅ Can view sports list
- ✅ Can create/edit/delete sports
- ✅ Database operations work correctly
- ✅ No gsutils dependencies remain
- ✅ All features from old system are replicated

## Timeline Estimate

- **Phase 1** (Utilities & DB): ✅ COMPLETE
- **Phase 2** (Data Models): 2-3 days
- **Phase 3** (REST APIs): 3-5 days
- **Phase 4** (Frontend): 5-7 days
- **Phase 5** (WebSockets): 2-3 days
- **Phase 6** (Testing): 3-5 days
- **Total**: 15-23 days (3-4 weeks)

Good luck with your refactoring! 🚀

