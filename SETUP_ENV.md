# Environment Variables Setup Guide

This guide explains how to set up environment variables for the Hotel Management Platform.

## Quick Start (Windows PowerShell)

1. Create a `.env` file in the project root:

```powershell
# Copy the example file
Copy-Item .env.example .env

# Edit .env with your actual values
notepad .env
```

2. Set the variables in your current PowerShell session:

```powershell
# Load .env file (if you have a script to do this)
# Or set them manually:
$env:DB_HOST = "localhost"
$env:DB_PORT = "5432"
$env:DB_NAME = "hotel_db"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "your_actual_password"
$env:JWT_SECRET = "your-secret-key-minimum-32-characters-long-for-security"
$env:APP_PORT = "8080"
$env:SPRING_PROFILES_ACTIVE = "dev"
```

3. Run the application:

```powershell
mvn spring-boot:run
```

## Method 1: Windows PowerShell (Current Session)

Set variables for the current PowerShell session:

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "5432"
$env:DB_NAME = "hotel_db"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "your_password_here"
$env:JWT_SECRET = "your-secret-key-minimum-32-characters-long-for-security"
$env:APP_PORT = "8080"
$env:SPRING_PROFILES_ACTIVE = "dev"
```

**Note**: These only last for the current PowerShell session. Close the terminal and they're gone.

## Method 2: Windows Command Prompt (CMD)

Set variables for the current CMD session:

```cmd
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=hotel_db
set DB_USER=postgres
set DB_PASSWORD=your_password_here
set JWT_SECRET=your-secret-key-minimum-32-characters-long-for-security
set APP_PORT=8080
set SPRING_PROFILES_ACTIVE=dev
```

## Method 3: Windows System Environment Variables (Permanent)

1. Press `Win + R`, type `sysdm.cpl`, press Enter
2. Go to "Advanced" tab → Click "Environment Variables"
3. Under "User variables" or "System variables", click "New"
4. Add each variable:
   - Variable name: `DB_HOST`, Variable value: `localhost`
   - Variable name: `DB_PORT`, Variable value: `5432`
   - Variable name: `DB_NAME`, Variable value: `hotel_db`
   - Variable name: `DB_USER`, Variable value: `postgres`
   - Variable name: `DB_PASSWORD`, Variable value: `your_password_here`
   - Variable name: `JWT_SECRET`, Variable value: `your-secret-key-minimum-32-characters-long-for-security`
   - Variable name: `APP_PORT`, Variable value: `8080`
   - Variable name: `SPRING_PROFILES_ACTIVE`, Variable value: `dev`
5. Click OK on all dialogs
6. **Restart your terminal/IDE** for changes to take effect

## Method 4: Using a PowerShell Script

Create a file `set-env.ps1` in the project root:

```powershell
# set-env.ps1
$env:DB_HOST = "localhost"
$env:DB_PORT = "5432"
$env:DB_NAME = "hotel_db"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "your_password_here"
$env:JWT_SECRET = "your-secret-key-minimum-32-characters-long-for-security"
$env:APP_PORT = "8080"
$env:SPRING_PROFILES_ACTIVE = "dev"

Write-Host "Environment variables set!" -ForegroundColor Green
```

Then run it before starting the app:

```powershell
. .\set-env.ps1
mvn spring-boot:run
```

## Method 5: Linux/Mac (Bash)

### Option A: Export in current session

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=hotel_db
export DB_USER=postgres
export DB_PASSWORD=your_password_here
export JWT_SECRET=your-secret-key-minimum-32-characters-long-for-security
export APP_PORT=8080
export SPRING_PROFILES_ACTIVE=dev
```

### Option B: Create .env file and source it

1. Create `.env` file:
```bash
cat > .env << EOF
DB_HOST=localhost
DB_PORT=5432
DB_NAME=hotel_db
DB_USER=postgres
DB_PASSWORD=your_password_here
JWT_SECRET=your-secret-key-minimum-32-characters-long-for-security
APP_PORT=8080
SPRING_PROFILES_ACTIVE=dev
EOF
```

2. Source it before running:
```bash
source .env
mvn spring-boot:run
```

### Option C: Add to ~/.bashrc or ~/.zshrc (permanent)

```bash
echo 'export DB_HOST=localhost' >> ~/.bashrc
echo 'export DB_PORT=5432' >> ~/.bashrc
echo 'export DB_NAME=hotel_db' >> ~/.bashrc
echo 'export DB_USER=postgres' >> ~/.bashrc
echo 'export DB_PASSWORD=your_password_here' >> ~/.bashrc
echo 'export JWT_SECRET=your-secret-key-minimum-32-characters-long-for-security' >> ~/.bashrc
echo 'export APP_PORT=8080' >> ~/.bashrc
echo 'export SPRING_PROFILES_ACTIVE=dev' >> ~/.bashrc

# Reload shell
source ~/.bashrc
```

## Method 6: Using IntelliJ IDEA / Eclipse

### IntelliJ IDEA:
1. Go to Run → Edit Configurations
2. Select your Spring Boot run configuration
3. Under "Environment variables", click the folder icon
4. Add each variable:
   - `DB_HOST=localhost`
   - `DB_PORT=5432`
   - `DB_NAME=hotel_db`
   - `DB_USER=postgres`
   - `DB_PASSWORD=your_password_here`
   - `JWT_SECRET=your-secret-key-minimum-32-characters-long-for-security`
   - `APP_PORT=8080`
   - `SPRING_PROFILES_ACTIVE=dev`

### Eclipse:
1. Right-click project → Run As → Run Configurations
2. Select your Spring Boot App
3. Go to "Environment" tab
4. Add each variable using "New" button

## Method 7: Using application.properties (Alternative)

If you prefer not to use environment variables, you can directly edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hotel_db
    username: postgres
    password: your_password_here

app:
  jwt:
    secret: your-secret-key-minimum-32-characters-long-for-security
```

**⚠️ Warning**: Never commit actual credentials to version control!

## Generating a Secure JWT_SECRET

For production, generate a secure random secret:

### Windows PowerShell:
```powershell
-join ((48..57) + (65..90) + (97..122) | Get-Random -Count 32 | ForEach-Object {[char]$_})
```

### Linux/Mac:
```bash
openssl rand -base64 32
```

Or use an online generator: https://www.random.org/strings/

## Verifying Environment Variables

### Windows PowerShell:
```powershell
$env:DB_HOST
$env:JWT_SECRET
```

### Windows CMD:
```cmd
echo %DB_HOST%
echo %JWT_SECRET%
```

### Linux/Mac:
```bash
echo $DB_HOST
echo $JWT_SECRET
```

## Troubleshooting

### Variables not being picked up?
1. Make sure you've restarted your terminal/IDE after setting system variables
2. Check that variable names match exactly (case-sensitive on Linux/Mac)
3. Verify no typos in variable names
4. For Spring Boot, ensure variables are set before running `mvn spring-boot:run`

### Database connection fails?
1. Verify PostgreSQL is running: `psql -U postgres -c "SELECT version();"`
2. Check database exists: `psql -U postgres -l`
3. Verify credentials are correct
4. Check firewall settings if connecting to remote database

### JWT authentication fails?
1. Ensure `JWT_SECRET` is at least 32 characters
2. Use the same secret for generating and validating tokens
3. Check token expiration (default: 24 hours)

## Security Best Practices

1. **Never commit `.env` files** - Add to `.gitignore`
2. **Use different secrets for dev/staging/production**
3. **Rotate JWT_SECRET periodically in production**
4. **Use strong passwords for database**
5. **Restrict database access** - Only allow connections from application server

## Example .gitignore entry

Make sure `.env` is in your `.gitignore`:

```
.env
.env.local
.env.*.local
```

