# Git Setup and GitHub Sync Guide

## Prerequisites

1. **Install Git** (if not already installed):
   - Download from: https://git-scm.com/download/win
   - Or use: `winget install Git.Git` (Windows Package Manager)

2. **Verify Git installation**:
   ```powershell
   git --version
   ```

## Step-by-Step Setup

### 1. Initialize Git Repository

Open PowerShell in your project directory and run:

```powershell
cd "D:\Java Code\Java Group Project\05. Fifth Iteration"
git init
```

### 2. Configure Git (if not already done globally)

```powershell
git config user.name "Your Name"
git config user.email "your.email@example.com"
```

### 3. Add All Files to Git

```powershell
git add .
```

### 4. Create Initial Commit

```powershell
git commit -m "Initial commit: Hotel Management Platform MVP with Desktop GUI"
```

### 5. Add GitHub Remote

Replace `YOUR_USERNAME` and `YOUR_REPO_NAME` with your actual GitHub repository details:

```powershell
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
```

Or if using SSH:
```powershell
git remote add origin git@github.com:YOUR_USERNAME/YOUR_REPO_NAME.git
```

### 6. Push to GitHub

```powershell
git branch -M main
git push -u origin main
```

## Future Updates

After making changes, sync with GitHub:

```powershell
git add .
git commit -m "Description of your changes"
git push
```

## Important Notes

- The `.gitignore` file has been created to exclude:
  - Compiled files (`target/` directory)
  - IDE files (`.idea/`, `.vscode/`, etc.)
  - Environment files (`.env`, `set-env.ps1`, `set-env.sh`)
  - Log files and temporary files

- **Never commit sensitive information** like:
  - Database passwords
  - JWT secrets
  - API keys
  - Personal credentials

- The `set-env.ps1` file is in `.gitignore` to protect your database credentials.

