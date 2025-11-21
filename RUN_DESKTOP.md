# Running the Desktop GUI Application

## Quick Start (One Command!)

**Windows PowerShell:**
```powershell
.\run-desktop.ps1
```

**Linux/Mac:**
```bash
chmod +x run-desktop.sh
./run-desktop.sh
```

That's it! The script automatically:
1. Loads your environment variables from `set-env.ps1` (or `set-env.sh`)
2. Checks for Maven
3. Launches the desktop application

## Alternative: Manual Steps (if needed)

If you prefer to run steps manually:

1. **Set environment variables:**
   ```powershell
   . .\set-env.ps1
   ```

2. **Run the desktop application:**
   ```powershell
   mvn exec:java -Dexec.mainClass="com.hotel.desktop.DesktopLauncher"
   ```

## Login Credentials

Default users (from seed data):

| Role | Email | Password |
|------|-------|----------|
| ADMIN | admin@example.com | admin123 |
| FRONTDESK | frontdesk@example.com | frontdesk123 |
| HOUSEKEEPING | housekeeping@example.com | housekeeping123 |

## Desktop Application Features

### Login Screen
- Enter email and password
- Click "Login" or press Enter

### Dashboard
- Role-based menu navigation
- Welcome message with user name and role
- Access to features based on user role

### Available Features by Role

#### All Roles
- **Reservations**: View and create reservations

#### FRONTDESK & ADMIN
- **Stays**: Check-in and check-out guests
- **Rooms**: View room status
- **Reports**: View daily reports

#### HOUSEKEEPING & ADMIN
- **Housekeeping**: Manage housekeeping tasks
- **Rooms**: Update room status

#### ADMIN Only
- **Admin**: View audit logs
- **Rooms**: Create new rooms

## Troubleshooting

### Application doesn't start
- Make sure environment variables are set (run `.\set-env.ps1`)
- Check that database is accessible
- Verify Java 17+ is installed

### Login fails
- Verify database connection
- Check that seed data was loaded (migrations ran)
- Try default admin credentials

### GUI doesn't appear
- Check console for errors
- Verify JavaFX dependencies are downloaded
- Try running with: `mvn clean compile exec:java -Dexec.mainClass="com.hotel.desktop.DesktopLauncher"`

## Building a Standalone Executable

To create a standalone JAR with all dependencies:

```bash
mvn clean package
java -jar target/hotel-management-platform-1.1.0.jar
```

Note: For a true native desktop application, you may need to use JavaFX packaging tools like jpackage (Java 14+) or create an installer.

