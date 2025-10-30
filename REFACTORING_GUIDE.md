# Schedule Tool Refactoring Guide

## Overview
This document outlines the refactoring of Schedule Tool from a Java Swing desktop application to a modern web-based application.

## Architecture Changes

### Old Architecture (v4.3)
- **UI**: Java Swing (Desktop)
- **Database**: MSSQL Server
- **Dependencies**: gsutils library (proprietary, no longer available)
- **Build**: Apache Ant + NetBeans
- **Communication**: Custom socket protocol with HDF format

### New Architecture (v5.0)
- **Backend**: Spring Boot REST API + WebSockets
- **Frontend**: HTML/CSS/JavaScript (simple, no build tools)
- **Database**: MariaDB (MySQL-compatible)
- **Build**: Maven
- **Communication**: REST API + WebSockets for real-time updates

## Completed Work

### ✅ Phase 1: Foundation & Utilities
1. **Created Utility Classes** (replacing gsutils):
   - `scheduletool.utils.Logger` - Replaces `gsutils.Debug`
   - `scheduletool.utils.DateTimeUtils` - Replaces `gsutils.DateTimeUtils`
   - `scheduletool.utils.Utils` - Replaces `gsutils.Utils`

2. **Created Database Layer** (replacing MSSQL):
   - `scheduletool.database.DatabaseConnection` - Replaces `gsutils.MSSQL`
   - `scheduletool.database.DatabaseFactory` - Replaces `gsutils.MSSQLfactory`
   - `scheduletool.database.DatabaseConfig` - Configuration holder

3. **Build System**:
   - Created `pom.xml` for Maven build
   - Added Spring Boot dependencies
   - Added MariaDB JDBC driver

4. **Spring Boot Setup**:
   - Created main application class
   - Created application.properties for configuration
   - Set up development and production profiles

## Next Steps

### Phase 2: Data Models (JPA Entities)
Need to recreate all `gsutils.data.*` classes as JPA entities:

**Core Entities:**
- Sport
- League
- League_Team
- League_Player
- League_Team_Player
- League_Equivalent
- Player
- Team
- Category
- Event
- Event_Item
- Event_Time
- Event_Score
- Event_Score_Item
- Event_Venue
- Venue
- Location
- Sportsbook
- Odds_Sportsbook
- Source
- Preset_Message

**Approach:**
1. Create JPA entity classes with proper annotations
2. Define relationships (@OneToMany, @ManyToOne, etc.)
3. Create Spring Data JPA repositories
4. Create DTOs for API responses

### Phase 3: REST API Controllers
Create REST endpoints for all operations:

**Endpoints to Create:**
- `/api/auth/login` - Authentication
- `/api/schedules` - Schedule management
- `/api/categories` - Category CRUD
- `/api/events` - Event CRUD
- `/api/scores` - Score updates
- `/api/lines` - Odds/lines management
- `/api/teams` - Team management
- `/api/players` - Player management
- `/api/pitchers` - Pitcher selection
- `/api/venues` - Venue management
- `/api/messages` - Preset messages

### Phase 4: WebSocket Support
For real-time updates:
- Schedule changes
- Score updates
- Live odds updates

### Phase 5: Frontend (HTML/CSS/JS)
Create simple web pages:

**Pages:**
1. Login page
2. Schedule view (main page)
3. Category management
4. Event add/edit dialog
5. Score update dialog
6. Lines/odds dialog
7. Pitcher selection dialog
8. Team management
9. Message management

**Technology:**
- Plain HTML5
- CSS3 (Bootstrap for styling)
- Vanilla JavaScript (or jQuery for simplicity)
- WebSocket client for real-time updates

### Phase 6: Socket Communication (Optional)
If you need to maintain compatibility with existing socket servers:
- Create WebSocket bridge to legacy socket protocol
- Implement HDF protocol parser/generator
- Handle ScheduleClient and ScoreServerClient functionality

## Database Migration

### MSSQL to MariaDB
You'll need to:

1. **Export existing schema** from MSSQL
2. **Convert to MariaDB** syntax:
   - Change `SYSDATETIMEOFFSET()` to `NOW()`
   - Change `SCOPE_IDENTITY()` to `LAST_INSERT_ID()`
   - Adjust stored procedures if any
   - Convert data types (e.g., `DATETIMEOFFSET` to `TIMESTAMP`)

3. **Import data** to MariaDB

### Schema Considerations
- Use `TIMESTAMP` with timezone handling in application layer
- Use `BIGINT` for IDs (auto-increment)
- Ensure proper indexes on foreign keys
- Add proper constraints

## Running the Application

### Development Mode
```bash
# Build the project
mvn clean install

# Run with development profile
mvn spring-boot:run

# Or run with production profile
mvn spring-boot:run -Dspring-boot.run.profiles=production
```

### Access
- Backend API: http://localhost:8080/api
- Frontend: http://localhost:8080 (once created)

## Configuration

### Database Setup
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mariadb://YOUR_HOST:3306/YOUR_DATABASE
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Socket Servers (if needed)
```properties
socket.schedule.host=YOUR_SCHEDULE_SERVER
socket.schedule.port=9999
socket.score.host=YOUR_SCORE_SERVER
socket.score.port=9998
```

## File Structure

```
ScheduleTool/
├── pom.xml                                 # Maven build file
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/scheduletool/
│   │   │       ├── ScheduleToolApplication.java
│   │   │       ├── config/              # Spring configuration
│   │   │       ├── controller/          # REST controllers
│   │   │       ├── service/             # Business logic
│   │   │       ├── repository/          # Data access (JPA)
│   │   │       ├── model/               # JPA entities
│   │   │       ├── dto/                 # Data transfer objects
│   │   │       ├── websocket/           # WebSocket handlers
│   │   │       └── socket/              # Legacy socket clients
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-production.properties
│   │       └── static/                  # Frontend files
│   │           ├── index.html
│   │           ├── css/
│   │           └── js/
│   ├── scheduletool/                    # Old Swing code (to be migrated)
│   │   ├── utils/                       # ✅ New utility classes
│   │   └── database/                    # ✅ New database layer
│   └── test/
└── REFACTORING_GUIDE.md                 # This file
```

## Migration Strategy

### Recommended Approach: Incremental Migration

1. **Keep old code** in `src/scheduletool/` for reference
2. **Build new code** in `src/main/java/com/scheduletool/`
3. **Migrate feature by feature**:
   - Start with read-only features (viewing schedules, categories)
   - Then add CRUD operations (add/edit events, categories)
   - Finally, add real-time features (scores, odds updates)
4. **Test thoroughly** at each step
5. **Remove old code** once fully migrated

### Parallel Running (Optional)
You can run both systems in parallel during migration:
- Old Swing app for production use
- New web app for testing and gradual rollout

## Testing

### Unit Tests
Create tests for:
- Service layer business logic
- Repository data access
- Utility functions

### Integration Tests
Test:
- REST API endpoints
- Database operations
- WebSocket connections

### Manual Testing
Test all UI workflows:
- Login
- Schedule viewing
- Event creation/editing
- Score updates
- Odds management

## Deployment

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/schedule-tool-5.0.0.jar --spring.profiles.active=production
```

### Docker (Optional)
Create Dockerfile for containerized deployment.

## Known Issues & Considerations

1. **HDF Protocol**: The old system uses a custom HDF (Hierarchical Data Format) protocol for socket communication. You'll need to decide:
   - Maintain compatibility (implement HDF parser/generator)
   - Replace with JSON/WebSocket
   - Run both in parallel

2. **Timezone Handling**: Old system uses Pacific timezone extensively. Ensure proper timezone conversion in the new system.

3. **Real-time Updates**: Old system uses socket connections. New system should use WebSockets for browser compatibility.

4. **Authentication**: Old system has a simple login. Consider adding proper authentication (JWT, OAuth2, etc.) for the web version.

## Support & Questions

For questions about the refactoring, refer to:
- This guide
- Spring Boot documentation: https://spring.io/projects/spring-boot
- MariaDB documentation: https://mariadb.org/documentation/

## Version History

- **v4.3** - Last Swing version (MSSQL + gsutils)
- **v5.0** - Web version (MariaDB + Spring Boot) - IN PROGRESS

