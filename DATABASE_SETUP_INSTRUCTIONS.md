# Database Setup Instructions for HeidiSQL

## Step 1: Open HeidiSQL and Connect to MariaDB

1. Open HeidiSQL
2. Connect to your MariaDB server (usually localhost)
3. Use your root credentials

## Step 2: Run the Schema Script

1. In HeidiSQL, click on **File → Load SQL file...**
2. Navigate to: `c:\Users\slalani\Documents\GitHub\ScheduleTool\database\schema.sql`
3. Click **Open**
4. Click the **Execute** button (or press F9)

This will:
- Create the `schedule_tool` database
- Create all necessary tables
- Insert sample data

## Step 3: Verify Database Creation

After running the script, you should see:
- A new database called `schedule_tool` in the left panel
- Multiple tables under it (Sport, League, Team, Event, etc.)
- Sample data in the Sport table

## Step 4: Update Application Configuration

1. Open `src/main/resources/application.properties`
2. Update the database password:
   ```properties
   spring.datasource.password=YOUR_MARIADB_PASSWORD
   ```

## Alternative: Quick Setup via HeidiSQL Query Tab

If you prefer, you can also copy and paste the SQL directly:

1. In HeidiSQL, click the **Query** tab
2. Copy the entire contents of `database/schema.sql`
3. Paste into the query window
4. Click **Execute** (F9)

## Verify Connection

To test if everything is working:

```sql
-- Run this in HeidiSQL Query tab
USE schedule_tool;
SELECT * FROM Sport;
```

You should see 5 sports (Basketball, Football, Baseball, Hockey, Soccer).

## Troubleshooting

### Issue: Database already exists
If you get an error that the database already exists, you can either:
1. Drop it first: `DROP DATABASE IF EXISTS schedule_tool;`
2. Or just run the CREATE TABLE statements individually

### Issue: Permission denied
Make sure you're connected as root or a user with CREATE DATABASE privileges.

### Issue: Can't connect from Spring Boot
1. Verify MariaDB is running (check HeidiSQL connection)
2. Check the port (default is 3306)
3. Verify username/password in `application.properties`
4. Make sure the database `schedule_tool` exists

## Next Steps

Once the database is set up:
1. Update the password in `application.properties`
2. Run the Spring Boot application: `mvn spring-boot:run`
3. Access the web UI at: http://localhost:8080

