# Debugging FXML Errors

## Common FXML Errors and Solutions

### Error: "Insets is not a valid type"

**Cause:** Missing import statement or JavaFX module path issue

**Solution:**
1. Ensure the FXML file has: `<?import javafx.geometry.Insets?>`
2. Rebuild the project: `mvn clean compile`
3. Check if the file is in `target/classes/fxml/` after build

### Debugging Steps

1. **Check FXML file syntax:**
   ```powershell
   # View the compiled FXML file
   Get-Content target\classes\fxml\dashboard.fxml
   ```

2. **Rebuild cleanly:**
   ```powershell
   mvn clean compile
   ```

3. **Check JavaFX is on classpath:**
   ```powershell
   mvn dependency:tree | Select-String "javafx"
   ```

4. **Enable verbose JavaFX logging:**
   Add to VM options: `-Djavafx.verbose=true`

5. **Check console output:**
   Look for stack traces in the terminal when the error occurs

### Alternative: Use CSS instead of Insets

If Insets continues to cause issues, use CSS padding instead:
- Instead of: `<Insets bottom="10.0" left="20.0" right="20.0" top="10.0" />`
- Use: `style="-fx-padding: 10 20 10 20;"`

### Verify FXML Loading

Add this to your controller to see what's happening:
```java
try {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
    loader.setControllerFactory(applicationContext::getBean);
    System.out.println("Loading FXML from: " + loader.getLocation());
    Parent root = loader.load();
    System.out.println("FXML loaded successfully");
} catch (Exception e) {
    e.printStackTrace();
    // Show detailed error
}
```

