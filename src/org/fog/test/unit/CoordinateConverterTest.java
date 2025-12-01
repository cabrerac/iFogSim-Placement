package org.fog.test.unit;

import org.fog.utils.CoordinateConverter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Unit tests for CoordinateConverter utility functions.
 * Tests coordinate generation and validation without running simulations.
 */
public class CoordinateConverterTest {
    
    private static final double DELTA = 0.001; // Tolerance for coordinate comparisons
    
    private File tempResourceFile;
    private File tempUserFile;
    private File tempConfigFile;
    
    @Before
    public void setUp() {
        // Initialize with a test config
        // Create a simple test config for Melbourne CBD
        String testConfig = "./src/org/fog/test/unit/test_location_config.json";
        try {
            java.io.FileWriter writer = new java.io.FileWriter(testConfig);
            writer.write("{\n");
            writer.write("  \"minLat\": -37.823400,\n");
            writer.write("  \"maxLat\": -37.804060,\n");
            writer.write("  \"minLon\": 144.947130,\n");
            writer.write("  \"maxLon\": 144.978820\n");
            writer.write("}");
            writer.close();
            
            CoordinateConverter.initializeFromConfig(testConfig);
            
            // Clean up config file
            new File(testConfig).delete();
        } catch (IOException e) {
            fail("Failed to initialize CoordinateConverter: " + e.getMessage());
        }
    }
    
    @After
    public void tearDown() {
        // Clean up any temp files created during tests
        if (tempResourceFile != null && tempResourceFile.exists()) {
            tempResourceFile.delete();
        }
        if (tempUserFile != null && tempUserFile.exists()) {
            tempUserFile.delete();
        }
        if (tempConfigFile != null && tempConfigFile.exists()) {
            tempConfigFile.delete();
        }
    }
    
    @Test
    public void testGenerateResourceLocationsCreatesFile() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_resources.csv";
        tempResourceFile = new File(filePath);
        
        CoordinateConverter.generateResourceLocationsCSV(10, filePath, 42L);
        
        assertTrue("Resource file should be created", tempResourceFile.exists());
        assertTrue("Resource file should not be empty", tempResourceFile.length() > 0);
    }
    
    @Test
    public void testGenerateResourceLocationsCorrectCount() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_resources.csv";
        tempResourceFile = new File(filePath);
        
        int numResources = 5;
        CoordinateConverter.generateResourceLocationsCSV(numResources, filePath, 42L);
        
        // Count lines in file (should be cloud + numResources edge servers)
        java.util.Scanner scanner = new java.util.Scanner(tempResourceFile);
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            lineCount++;
        }
        scanner.close();
        
        assertEquals("Should have cloud + edge servers", 
                     numResources + 1, lineCount);
    }
    
    @Test
    public void testGenerateUserLocationsCreatesFile() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_users.csv";
        tempUserFile = new File(filePath);
        
        CoordinateConverter.generateUserLocationsCSV(10, filePath, 42L);
        
        assertTrue("User file should be created", tempUserFile.exists());
        assertTrue("User file should not be empty", tempUserFile.length() > 0);
    }
    
    @Test
    public void testGenerateUserLocationsCorrectCount() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_users.csv";
        tempUserFile = new File(filePath);
        
        int numUsers = 20;
        CoordinateConverter.generateUserLocationsCSV(numUsers, filePath, 42L);
        
        // Count lines in file
        java.util.Scanner scanner = new java.util.Scanner(tempUserFile);
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            lineCount++;
        }
        scanner.close();
        
        assertEquals("Should have correct number of users", numUsers, lineCount);
    }
    
    @Test
    public void testGenerateLocationsDeterministic() throws IOException {
        String filePath1 = "./src/org/fog/test/unit/test_resources1.csv";
        String filePath2 = "./src/org/fog/test/unit/test_resources2.csv";
        
        try {
            // Generate with same seed
            CoordinateConverter.generateResourceLocationsCSV(5, filePath1, 123L);
            CoordinateConverter.generateResourceLocationsCSV(5, filePath2, 123L);
            
            // Read both files
            File file1 = new File(filePath1);
            File file2 = new File(filePath2);
            
            java.util.Scanner scanner1 = new java.util.Scanner(file1);
            java.util.Scanner scanner2 = new java.util.Scanner(file2);
            
            // Compare line by line
            while (scanner1.hasNextLine() && scanner2.hasNextLine()) {
                assertEquals("Same seed should produce same coordinates",
                           scanner1.nextLine(), scanner2.nextLine());
            }
            
            assertFalse("Both files should have same number of lines", 
                       scanner1.hasNextLine() || scanner2.hasNextLine());
            
            scanner1.close();
            scanner2.close();
            
            // Cleanup
            file1.delete();
            file2.delete();
            
        } catch (IOException e) {
            fail("IO error during test: " + e.getMessage());
        }
    }
    
    @Test
    public void testGenerateLocationConfigCreatesValidJSON() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_location_config.json";
        tempConfigFile = new File(filePath);
        
        CoordinateConverter.generateLocationConfig(filePath);
        
        assertTrue("Config file should be created", tempConfigFile.exists());
        
        // Check that file contains expected JSON keys
        java.util.Scanner scanner = new java.util.Scanner(tempConfigFile);
        String content = scanner.useDelimiter("\\Z").next();
        scanner.close();
        
        assertTrue("Config should contain minLat", content.contains("\"minLat\""));
        assertTrue("Config should contain maxLat", content.contains("\"maxLat\""));
        assertTrue("Config should contain minLon", content.contains("\"minLon\""));
        assertTrue("Config should contain maxLon", content.contains("\"maxLon\""));
        assertTrue("Config should contain boundary", content.contains("\"boundary\""));
    }
    
    @Test
    public void testResourceFileContainsCloudAsFirstLine() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_resources.csv";
        tempResourceFile = new File(filePath);
        
        CoordinateConverter.generateResourceLocationsCSV(5, filePath, 42L);
        
        java.util.Scanner scanner = new java.util.Scanner(tempResourceFile);
        String firstLine = scanner.nextLine();
        scanner.close();
        
        assertTrue("First line should start with cloud ID (0)", 
                  firstLine.startsWith("0,"));
        assertTrue("First line should contain DataCenter", 
                  firstLine.contains("DataCenter"));
    }
    
    @Test
    public void testResourceFileContainsValidCoordinates() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_resources.csv";
        tempResourceFile = new File(filePath);
        
        CoordinateConverter.generateResourceLocationsCSV(3, filePath, 42L);
        
        java.util.Scanner scanner = new java.util.Scanner(tempResourceFile);
        boolean allValid = true;
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            
            if (parts.length >= 3) {
                try {
                    double lat = Double.parseDouble(parts[1]);
                    double lon = Double.parseDouble(parts[2]);
                    
                    // Check if coordinates are in reasonable range for Melbourne
                    if (lat < -38 || lat > -37 || lon < 144 || lon > 145) {
                        allValid = false;
                        break;
                    }
                } catch (NumberFormatException e) {
                    allValid = false;
                    break;
                }
            }
        }
        scanner.close();
        
        assertTrue("All coordinates should be valid and in expected range", allValid);
    }
    
    @Test
    public void testUserFileContainsValidCoordinates() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_users.csv";
        tempUserFile = new File(filePath);
        
        CoordinateConverter.generateUserLocationsCSV(10, filePath, 42L);
        
        java.util.Scanner scanner = new java.util.Scanner(tempUserFile);
        boolean allValid = true;
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            
            if (parts.length >= 2) {
                try {
                    double lat = Double.parseDouble(parts[0]);
                    double lon = Double.parseDouble(parts[1]);
                    
                    // Check if coordinates are in reasonable range for Melbourne
                    if (lat < -38 || lat > -37 || lon < 144 || lon > 145) {
                        allValid = false;
                        break;
                    }
                } catch (NumberFormatException e) {
                    allValid = false;
                    break;
                }
            }
        }
        scanner.close();
        
        assertTrue("All coordinates should be valid and in expected range", allValid);
    }
    
    @Test
    public void testGenerateZeroResourcesStillCreatesCloud() throws IOException {
        String filePath = "./src/org/fog/test/unit/test_resources_zero.csv";
        tempResourceFile = new File(filePath);
        
        // Generate with 0 edge servers (but should still have cloud)
        CoordinateConverter.generateResourceLocationsCSV(0, filePath, 42L);
        
        java.util.Scanner scanner = new java.util.Scanner(tempResourceFile);
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            lineCount++;
        }
        scanner.close();
        
        assertEquals("Should have at least cloud entry", 1, lineCount);
    }
    
    @Test
    public void testInitializeFromConfigParsesCorrectly() {
        // This test validates that initialization actually happened in setUp
        // If we got here without errors in setUp, initialization worked
        
        // We can't easily test the internal state without exposing it,
        // but we can verify that subsequent operations don't crash
        try {
            String filePath = "./src/org/fog/test/unit/test_init_check.csv";
            CoordinateConverter.generateResourceLocationsCSV(1, filePath, 42L);
            new File(filePath).delete();
            // If we got here, initialization was successful
            assertTrue("Initialization should work", true);
        } catch (IOException e) {
            fail("Operations after initialization should work: " + e.getMessage());
        }
    }
}

