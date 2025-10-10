# Parameter Loading Verification Guide

## Overview

This document helps you verify that all parameters from your YAML configuration files are being properly loaded by `SPPExperiment.java`.

## Complete List of Loaded Parameters

### From Constants Section
✅ These are loaded from `constants:` section in YAML

| Parameter | Type | Required | Loaded By | Description |
|-----------|------|----------|-----------|-------------|
| `geographicArea` | String | Yes | `loadExperimentConstants()` | Geographic area name (e.g., "MELBOURNE") |
| `locationConfigFile` | String | Yes | `loadExperimentConstants()` | Path to location configuration JSON |
| `resourcesLocationPath` | String | Yes | `loadExperimentConstants()` | Path to edge resources CSV file |
| `usersLocationPath` | String | Yes | `loadExperimentConstants()` | Path to users location CSV file |
| `osmFilePath` | String | No | `loadExperimentConstants()` | Path to OpenStreetMap PBF file |
| `graphHopperFolder` | String | No | `loadExperimentConstants()` | Path to GraphHopper data folder |
| `events` | Map | No | `loadExperimentConstants()` | Event configurations (type, timestamp, parameters) |

**Storage:** Constants are stored in `SPPExperimentConstants` object, accessible via `experimentConstants` field.

### From Experiments Section
✅ These are loaded from each experiment entry in `experiments:` list

| Parameter | Type | Required | Default | Loaded By | Stored In | Description |
|-----------|------|----------|---------|-----------|-----------|-------------|
| `numberOfEdge` | int | Yes | - | `parseExperimentConfigs()` | `SimulationConfig.numberOfEdge` | Number of edge servers |
| `placementLogic` | String/int | Yes | - | `parseExperimentConfigs()` | `SimulationConfig.placementLogic` | Placement algorithm name or code |
| `numberOfApplications` | int | Yes | - | `parseExperimentConfigs()` | `SimulationConfig.numberOfApplications` | Total applications to create |
| `appLoopLength` | int | Yes | - | `parseExperimentConfigs()` | `SimulationConfig.appLoopLength` | Uniform loop length for apps |
| `usersPerType` | Map<String, Integer> | Yes | - | `parseExperimentConfigs()` | `SimulationConfig.usersPerType` | Number of users per type |
| `intervalValues` | Map<String, Integer> | Yes | - | `parseExperimentConfigs()` | `SimulationConfig.intervalValues` | PR generation intervals (seconds) |
| `randomSeeds.experimentSeed` | int | No | 33 | `parseExperimentConfigs()` | `SimulationConfig.experimentSeed` | Overall experiment seed |
| `randomSeeds.locationSeed` | int | No | 42 | `parseExperimentConfigs()` | `SimulationConfig.locationSeed` | Location generation seed |
| `randomSeeds.mobilityStrategySeed` | int | No | 123 | `parseExperimentConfigs()` | `SimulationConfig.mobilityStrategySeed` | Mobility pattern seed |
| `randomSeeds.heuristicSeed` | int | No | 456 | `parseExperimentConfigs()` | `SimulationConfig.heuristicSeed` | Heuristic algorithm seed |
| `placementProcessInterval` | double | No | 60.0 | `parseExperimentConfigs()` | `SimulationConfig.placementProcessInterval` | Placement processing interval (seconds) |
| `areaName` | String | No | null | `parseExperimentConfigs()` | `SimulationConfig.areaName` | Area name metadata |

## Accessing Loaded Parameters

### In SPPExperiment.java

```java
// Access constants
String geographicArea = experimentConstants.getGeographicArea();
String locationConfigFile = experimentConstants.getLocationConfigFile();
Map<String, EventConfig> events = experimentConstants.getEvents();

// Access experiment configuration
SimulationConfig config = configs.get(0); // Get first experiment
int numberOfEdge = config.getNumberOfEdge();
int placementLogic = config.getPlacementLogic();
int numberOfApplications = config.getNumberOfApplications();
int appLoopLength = config.getAppLoopLength();
Map<String, Integer> usersPerType = config.getUsersPerType();
Map<String, Integer> intervalValues = config.getIntervalValues();
Map<String, Double> lambdaValues = config.getLambdaValues(); // Computed from intervalValues
int experimentSeed = config.getExperimentSeed();
int locationSeed = config.getLocationSeed();
int mobilityStrategySeed = config.getMobilityStrategySeed();
int heuristicSeed = config.getHeuristicSeed();
double placementProcessInterval = config.getPlacementProcessInterval();
String areaName = config.getAreaName();
```

## Verification Steps

### 1. Check YAML Structure

Your YAML file must have this structure:

```yaml
constants:
  geographicArea: "MELBOURNE"
  locationConfigFile: "./path/to/config.json"
  resourcesLocationPath: "./path/to/resources.csv"
  usersLocationPath: "./path/to/users.csv"
  # ... other constants

experiments:
  - numberOfEdge: 100
    placementLogic: "ACO"
    numberOfApplications: 1300
    appLoopLength: 1
    # ... other experiment parameters
```

**Common Mistake:** Don't forget the `experiments:` wrapper around your experiment list!

### 2. Verify Console Output

When you run `SPPExperiment`, you should see output like:

```
Loaded experiment constants: SPPExperimentConstants{...}
Loaded 63 configurations from ./dataset/SPPExperimentConfigs.yaml
```

The number of configurations should match your experiment count.

### 3. Check Configuration Loading

Add debug logging to verify parameters are loaded:

```java
// In SPPExperiment.main() after loading configs
for (SimulationConfig config : configs) {
    System.out.println(config.toString());
}
```

Expected output format:
```
numberOfEdge: 100, numberOfApplications: 1300, appLoopLength: 1, numberOfUser: 200, 
placementLogic: 13, placementProcessInterval: 4.0, experimentSeed: 33, locationSeed: 42, 
mobilityStrategySeed: 123, heuristicSeed: 456, areaName: melbourne
```

### 4. Validate Placement Logic Mapping

Placement logic names are mapped to integer codes in `PlacementLogicFactory`:

| String Name | Integer Code | Algorithm |
|-------------|--------------|-----------|
| `"ACO"` | 13 | Ant Colony Optimization |
| `"BEST_FIT"` | 7 | Best Fit |
| `"CLOSEST_FIT"` | 8 | Closest Fit |
| `"ILP"` | 14 | Integer Linear Programming |
| `"MAX_FIT"` | 9 | Maximum Fit |
| `"MULTI_OPT"` | 11 | Multi-Objective Optimization |
| `"SIMULATED_ANNEALING"` | 12 | Simulated Annealing |

If you see "Unknown placement logic name" errors, check that your string names match exactly (case-sensitive).

## Common Loading Issues

### Issue 1: "YAML file must contain a 'constants' section"
**Cause:** Missing `constants:` section at top of file  
**Fix:** Add required constants section with all required fields

### Issue 2: "YAML file must contain an 'experiments' section"
**Cause:** Experiments are at root level instead of under `experiments:` key  
**Fix:** Wrap your experiment list with `experiments:` key

### Issue 3: Parameters showing as null or default values
**Cause:** Parameter name mismatch or wrong nesting level  
**Fix:** Ensure parameter names match exactly (case-sensitive) and are at correct indentation level

### Issue 4: "Configuration missing required fields, skipping"
**Cause:** Missing `numberOfApplications` or `appLoopLength`  
**Fix:** Ensure both fields are present in each experiment

### Issue 5: Anchor not resolved
**Cause:** Anchor used before it's defined, or name mismatch  
**Fix:** Define all anchors before `experiments:` section, check spelling matches exactly

## Parameter Usage Flow

```
YAML File
    ↓
SPPExperiment.loadConfigurationsFromYaml()
    ↓
├─→ loadExperimentConstants() → SPPExperimentConstants
│       ├─→ geographicArea
│       ├─→ locationConfigFile
│       ├─→ resourcesLocationPath
│       ├─→ usersLocationPath
│       ├─→ osmFilePath
│       ├─→ graphHopperFolder
│       └─→ events
│
└─→ parseExperimentConfigs() → List<SimulationConfig>
        └─→ For each experiment:
            ├─→ numberOfEdge
            ├─→ placementLogic
            ├─→ numberOfApplications
            ├─→ appLoopLength
            ├─→ usersPerType
            ├─→ intervalValues
            ├─→ randomSeeds
            ├─→ placementProcessInterval
            └─→ areaName
```

## Computed Parameters

Some parameters are computed from loaded values:

### numberOfUser
**Computed from:** `usersPerType`  
**Computation:** Sum of all values in `usersPerType` map  
**Example:**
```yaml
usersPerType:
  genericUser: 100
  operaUser: 80
  ambulanceUser: 20
# Computed: numberOfUser = 200
```

### lambdaValues
**Computed from:** `intervalValues`  
**Computation:** For each user type: `lambda = 1.0 / interval`  
**Example:**
```yaml
intervalValues:
  genericUser: 300  # lambda = 1/300 = 0.00333...
  ambulanceUser: 180  # lambda = 1/180 = 0.00555...
```
**Access via:** `config.getLambdaValues()`

## Testing Your Configuration

Create a simple test to verify all parameters are loaded:

```java
public static void testConfigurationLoading() {
    List<SimulationConfig> configs = loadConfigurationsFromYaml();
    
    assert configs.size() > 0 : "No configurations loaded";
    
    SimulationConfig firstConfig = configs.get(0);
    assert firstConfig.getNumberOfEdge() > 0 : "numberOfEdge not loaded";
    assert firstConfig.getNumberOfApplications() > 0 : "numberOfApplications not loaded";
    assert firstConfig.getAppLoopLength() > 0 : "appLoopLength not loaded";
    assert !firstConfig.getUsersPerType().isEmpty() : "usersPerType not loaded";
    assert !firstConfig.getIntervalValues().isEmpty() : "intervalValues not loaded";
    assert firstConfig.getPlacementProcessInterval() > 0 : "placementProcessInterval not loaded";
    
    System.out.println("✓ All required parameters loaded successfully");
    System.out.println("  - Number of configurations: " + configs.size());
    System.out.println("  - First config: " + firstConfig);
}
```

## Summary Checklist

Before running your experiments, verify:

- [ ] YAML file has `constants:` section with all required fields
- [ ] YAML file has `experiments:` section wrapping experiment list
- [ ] All YAML anchors are defined before `experiments:` section
- [ ] Each experiment has all required parameters
- [ ] Placement logic names match valid options
- [ ] File paths in constants section are valid
- [ ] Console shows "Loaded N configurations" matching expected count
- [ ] Configuration `toString()` output shows all expected values
- [ ] No error messages about missing fields or unknown placement logic

## Need Help?

If parameters are still not loading correctly:

1. Check the Java source code:
   - `SPPExperiment.loadConfigurationsFromYaml()` - Main loading method
   - `SPPExperiment.parseExperimentConfigs()` - Parses individual experiments
   - `SimulationConfig` constructors - See all supported parameters

2. Enable debug output by uncommenting `System.out.println()` statements in:
   - Line 569: Interval values loading
   - Line 598: Placement process interval loading
   - Lines 620-621: Configuration loading summary

3. Verify YAML syntax with an online YAML validator

4. Compare your YAML structure with the examples in `dataset/SPPExperimentConfigs.yaml`

