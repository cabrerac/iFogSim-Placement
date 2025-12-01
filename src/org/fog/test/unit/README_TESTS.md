# Unit Tests for iFogSim2

This directory contains **standalone unit tests** that validate core functionality **without running any simulations**.

## What These Tests Cover

### 1. **LocationTest.java** - Geographic & Mathematical Functions
Tests the `Location` class for:
- ✅ **Haversine distance calculations** (real-world distances between GPS coordinates)
- ✅ **Point-in-polygon detection** (boundary checking)
- ✅ **Bearing calculations** (directional navigation)
- ✅ **Movement operations** (`movedTowards`)
- ✅ **Random location generation** (deterministic with seeds)
- ✅ **Coordinate validation**

**Why this matters:** Bugs in distance/location calculations silently corrupt ALL placement decisions and latency measurements throughout the entire simulation.

### 2. **DeviceStateTest.java** - Resource Allocation Logic
Tests the `DeviceState` class for:
- ✅ **Resource constraint checking** (`canFit`)
- ✅ **Allocation and deallocation** of CPU, RAM, storage
- ✅ **Resource utilization calculations**
- ✅ **Copy constructor** (critical for ACO/SA algorithms)
- ✅ **Comparison/sorting logic** (used by Best-Fit)
- ✅ **Edge cases** (zero resources, exact capacity, over-allocation)

**Why this matters:** Resource management bugs lead to over-subscription or failed placements that won't be discovered until hours into a simulation run.

### 3. **CoordinateConverterTest.java** - Data Generation Utilities
Tests the `CoordinateConverter` class for:
- ✅ **CSV file generation** (resources and users)
- ✅ **Coordinate validity** (within expected bounds)
- ✅ **Deterministic generation** (same seed → same output)
- ✅ **File format correctness**
- ✅ **JSON configuration generation**

**Why this matters:** Invalid input data corrupts experiments and makes results non-reproducible.

### 4. **PlacementLogicFactoryTest.java** - Configuration Parsing
Tests the `PlacementLogicFactory` class for:
- ✅ **String to algorithm code mapping** ("ACO" → 13)
- ✅ **Case-insensitive parsing**
- ✅ **Invalid algorithm detection**
- ✅ **Round-trip conversions** (code ↔ string)
- ✅ **Factory instantiation** of placement algorithms

**Why this matters:** A typo in YAML configuration could run an 8-hour experiment with the wrong algorithm, and you wouldn't know until examining results.

## What These Tests DON'T Do

❌ **Run simulations** - All tests complete in milliseconds  
❌ **Test CloudSim internals** - Only tests iFogSim-specific code  
❌ **Compare algorithm performance** - That's what experiments do  
❌ **Test end-to-end scenarios** - Focused on individual components  

## Running the Tests

### Option 1: Using Maven (Recommended)

```bash
# From project root
mvn test

# Run specific test class
mvn test -Dtest=LocationTest

# Run with verbose output
mvn test -X
```

### Option 2: Using IDE

**IntelliJ IDEA:**
1. Right-click on `src/org/fog/test/unit/` folder
2. Select "Run 'Tests in unit'"

**Eclipse:**
1. Right-click on test file
2. Select "Run As" → "JUnit Test"

### Option 3: Run Individual Test Files

```bash
# Compile first
javac -cp ".:jars/*" src/org/fog/test/unit/LocationTest.java

# Run with JUnit
java -cp ".:jars/*:junit-4.13.2.jar:hamcrest-core-1.3.jar" \
    org.junit.runner.JUnitCore org.fog.test.unit.LocationTest
```

## Expected Output

All tests should pass:
```
Running org.fog.test.unit.LocationTest
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0

Running org.fog.test.unit.DeviceStateTest
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0

Running org.fog.test.unit.CoordinateConverterTest
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0

Running org.fog.test.unit.PlacementLogicFactoryTest
Tests run: 28, Failures: 0, Errors: 0, Skipped: 0

Total: 85 tests, 0 failures
Time: ~2 seconds
```

## Test Execution Time

All tests combined run in **< 3 seconds** on typical hardware.

Individual test classes:
- `LocationTest`: ~500ms
- `DeviceStateTest`: ~200ms  
- `CoordinateConverterTest`: ~800ms (file I/O)
- `PlacementLogicFactoryTest`: ~400ms

## When to Run These Tests

✅ **Before running expensive simulations** - Catch bugs early  
✅ **After modifying core algorithms** - Prevent regressions  
✅ **When changing coordinate/resource logic** - Validate correctness  
✅ **Before committing code** - Basic sanity check  
✅ **After pulling updates** - Verify nothing broke  

## Adding New Tests

When adding new test methods:

1. **Use descriptive names**: `testHaversineDistanceKnownLocations()`
2. **Test one thing**: Each test should verify a single behavior
3. **Include edge cases**: Zero values, boundary conditions, null inputs
4. **No simulation dependencies**: Tests must be fast and standalone
5. **Use appropriate assertions**: `assertEquals`, `assertTrue`, `assertNotNull`

Example template:
```java
@Test
public void testDescriptiveName() {
    // Arrange: Set up test data
    Location loc = new Location(-37.8136, 144.9631, -1);
    
    // Act: Execute the function
    double result = loc.calculateDistance(loc);
    
    // Assert: Verify the result
    assertEquals("Distance to self should be 0", 0.0, result, 0.001);
}
```

## Troubleshooting

**Problem:** Tests fail with "ClassNotFoundException"  
**Solution:** Ensure JUnit is in your classpath and Maven dependencies are installed

**Problem:** Tests fail with file I/O errors  
**Solution:** Check write permissions in `src/org/fog/test/unit/` directory

**Problem:** Location tests fail with coordinate out of bounds  
**Solution:** Check that `Config` class is properly initialized with Melbourne boundaries

**Problem:** PlacementLogicFactory tests fail  
**Solution:** Verify all placement algorithm classes exist in `org.fog.placement` package

## Test Coverage

Current test coverage for tested classes:
- `Location`: ~75% of public methods
- `DeviceState`: ~90% of public methods
- `CoordinateConverter`: ~60% of public methods
- `PlacementLogicFactory`: ~95% of public methods

**Not tested** (would require simulation):
- Event scheduling logic
- Network communication
- Tuple routing
- Energy consumption tracking
- Full algorithm execution (ACO iterations, etc.)

These are appropriately tested through the full simulation experiments.

## Contributing Tests

When contributing new utility functions or algorithms to iFogSim2:
1. Write standalone unit tests if the function is testable in isolation
2. Document what aspects can't be unit tested (require simulation)
3. Keep tests fast (< 1 second per test class)
4. Don't mock CloudSim - if you need CloudSim, it's not a unit test

## References

- JUnit 4 Documentation: https://junit.org/junit4/
- Maven Surefire Plugin: https://maven.apache.org/surefire/maven-surefire-plugin/

