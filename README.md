# iFogSim2 (The New Version)
A Toolkit for Modeling and Simulation of Resource Management Techniques in Internet of Things, Edge and Fog Computing Environments with the following new features:
 * Mobility-support and Migration Management
   * Supporting real mobility datasets
   * Implementing different random mobility models 
 * Microservice Orchestration
 * Dynamic Distributed Clustering
 * Any Combinations of Above-mentioned Features
 * Full Compatibility with the Latest Version of the CloudSim (i.e., [CloudSim 5](https://github.com/Cloudslab/cloudsim/releases)) and [Previous iFogSim Version](https://github.com/Cloudslab/iFogSim1) and Tutorials

iFogSim2 currently encompasses several new usecases such as:
 * Audio Translation Scenario
 * Healthcare Scenario
 * Crowd-sensing Scenario

# How to run iFogSim2 ?

## Getting Started

### Prerequisites

1. **JDK 11**: Ensure you have JDK 11 installed on your machine. You can verify your Java version by running:
   ```bash
   java -version
   ```
   If you don't have JDK 11, download it from [Oracle](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) or use a package manager like Homebrew (macOS) or apt (Linux).

### Installation

2. **Install Dependencies**: Use the Maven wrapper to install all required packages:
   - **On macOS/Linux**:
     ```bash
     ./mvnw clean install
     ```
   - **On Windows**:
     ```bash
     mvnw.cmd clean install
     ```

### Running the Simulation

3. **Run the Main Simulation**: Execute the simulation using the Maven wrapper:
   ```bash
   ./mvnw exec:java 2>&1 | tee output/SPPExperiment.txt
   ```
   
   **Note**: The main class is configured in `pom.xml` to be `org.fog.test.perfeval.SPPExperiment`. If you want to run a different file, you can specify it in the command:
   ```bash
   ./mvnw exec:java -Dexec.mainClass="org.fog.test.perfeval.OnlinePOC" 2>&1 | tee output/OnlinePOC.txt
   ```

## Quick Start
* Eclipse IDE:
  * Create a Java project
  * Inside the project directory, initialize an empty Git repository with the following command:
  ```
  git init
  ```
  * Add the Git repository of iFogSim2 as the `origin` remote:
  ```
  git remote add origin https://github.com/Cloudslab/iFogSim
  ```
  * Pull the contents of the repository to your machine:
  ```
  git pull origin main
  ```
  * Include the JARs to your project  
  * Run the example files (e.g. TranslationServiceFog_Clustering.java, CrowdSensing_Microservices_RandomMobility_Clustering.java) to get started

* IntelliJ IDEA:
  * Clone the iFogSim2 Git repository to desired folder:
  ```
  git clone https://github.com/Cloudslab/iFogSim
  ```
  * Select "project from existing resources" from the "File" drop-down menu
  * Verify the Java version
  * Verify the external libraries in the "JARs" Folder are added to the project
  * Run the example files (e.g. TranslationServiceFog_Clustering.java, CrowdSensing_Microservices_RandomMobility_Clustering.java) to get started

## Advanced Usage with Adapted Components

This repository includes several adapted components for advanced research scenarios:

### 1. SPPExperiment - Main Research Platform

`SPPExperiment.java` is the primary research platform that allows researchers to configure and run complex simulations using YAML configuration files.

#### Key Features:
- **YAML-based Configuration**: Define multiple simulation scenarios in a single YAML file
- **Comprehensive Metrics**: Tracks latency, CPU utilization, energy consumption, memory usage, and failure rates
- **Multiple Placement Algorithms**: Supports ACO, Best-Fit, Closest-Fit, ILP, Max-Fit, and more
- **Dynamic Location Generation**: Option to generate random or grid-based location distributions
- **User Type Support**: Different user types (generic, ambulance, opera, immobile) with distinct behaviors
- **Performance Monitoring**: Real-time memory and execution time tracking

#### Configuration File Structure:
```yaml
- numberOfEdge: 100                    # Number of edge servers
  placementLogic: "ACO"               # Placement algorithm (ACO, BEST_FIT, CLOSEST_FIT, ILP, MAX_FIT)
  numberOfApplications: 1300          # Total applications to create
  appLoopLength: 1                    # Microservices per application
  placementProcessInterval: 4.0       # Placement processing interval (seconds)
  usersPerType:                       # User distribution
    genericUser: 100
    operaUser: 80
    ambulanceUser: 20
  intervalValues:                     # Event generation intervals (seconds)
    genericUser: 300
    ambulanceUser: 180
    operaUser: 600
    immobileUser: 900
  randomSeeds:                        # Reproducible randomness
    experimentSeed: 33
    locationSeed: 42
    mobilityStrategySeed: 123
    heuristicSeed: 456
  areaName: "melbourne"               # Geographic area
```

#### Usage:
1. **Configure your experiment** in `./dataset/SPPExperimentConfigs.yaml`
2. **Run the simulation**:
   ```bash
   java -cp .:jars/* org.fog.test.perfeval.SPPExperiment
   ```
3. **View results** in `./output/MiH_4_0_Melbourne.csv`

#### Available Configuration Files:
- `./dataset/SPPExperimentConfigs.yaml` - Main experiment configurations
- `./dataset/PerformanceEvalConfigsUsers.yaml` - User-focused performance evaluation
- `./dataset/PerformanceEvalConfigsEdges.yaml` - Edge server-focused evaluation

### 2. OnlinePOC - Online Placement Proof of Concept

`OnlinePOC.java` demonstrates online microservice placement with periodic placement requests.

#### Features:
- **Online Placement**: Real-time placement request processing
- **Periodic Generation**: Placement requests generated at configurable intervals
- **Immobile Users**: Focus on stationary user devices
- **Simple Application Model**: 4-microservice application with deterministic flow

#### Usage:
```bash
java -cp .:jars/* org.fog.test.perfeval.OnlinePOC
```

### 3. MobilityPOC - Mobility Framework Proof of Concept

`MobilityPOC.java` demonstrates the mobility framework without placement requests.

#### Features:
- **Pure Mobility**: Focus on device movement without application placement
- **Strategy Pattern**: Flexible mobility strategy implementation
- **Multiple User Types**: Generic, Opera, and Ambulance users with different mobility patterns
- **Location Data Integration**: Uses real Melbourne CBD location data

#### Usage:
```bash
java -cp .:jars/* org.fog.test.mobility.MobilityPOC
```

## Configuration Options

### Dynamic Location Generation
Enable automatic location generation instead of using pre-defined CSV files:

```java
// In SPPExperiment.java
SPPExperiment.setUseDynamicLocations(true);
SPPExperiment.setOutputDirectory("./dataset/generated/");
```

### Placement Algorithms
Available placement algorithms (use string names in YAML):
- `"ACO"` - Ant Colony Optimization
- `"BEST_FIT"` - Best Fit Heuristic
- `"CLOSEST_FIT"` - Closest Fit Heuristic
- `"ILP"` - Integer Linear Programming
- `"MAX_FIT"` - Maximum Fit Heuristic

### User Types
Supported user types with distinct characteristics:
- `genericUser` - Standard mobile users
- `ambulanceUser` - Emergency vehicle users (higher priority)
- `operaUser` - Event-based users (periodic high activity)
- `immobileUser` - Stationary users

## Output and Metrics

### CSV Output Format
The main output file contains comprehensive metrics:
- Simulation index and configuration
- Resource utilization (average and standard deviation)
- Latency metrics (average and standard deviation)
- Failure ratios
- Execution time and memory usage
- Energy consumption (cloud and edge devices)

### Temporary Files
During simulation, temporary files are created in `./temp_metrics/`:
- `sim_X_metrics.csv` - Detailed performance metrics
- `sim_X_failed.csv` - Failed placement request details

## Customization

### Modifying SPPExperiment
You can modify `SPPExperiment.java` to:
- Add new metrics collection
- Implement custom placement algorithms
- Change the application model
- Add new user types
- Modify the simulation flow

### Adding New Configuration Parameters
To add new parameters to the YAML configuration:
1. Update the `SimulationConfig` class
2. Modify the YAML loading logic in `loadConfigurationsFromYaml()`
3. Use the new parameters in your simulation logic

## Troubleshooting

### Common Issues:
1. **Memory Issues**: Large simulations may require increased heap size:
   ```bash
   java -Xmx8g -cp .:jars/* org.fog.test.perfeval.SPPExperiment
   ```

2. **CSV File Not Found**: Ensure location data files exist in `./dataset/`

3. **YAML Parsing Errors**: Check YAML syntax and indentation

4. **Placement Algorithm Not Found**: Verify algorithm names match those in `PlacementLogicFactory`

## References
 * Redowan Mahmud, Samodha Pallewatta, Mohammad Goudarzi, and Rajkumar Buyya, <A href="https://arxiv.org/abs/2109.05636">iFogSim2: An Extended iFogSim Simulator for Mobility, Clustering, and Microservice Management in Edge and Fog Computing Environments</A>, Journal of Systems and Software (JSS), Volume 190, Pages: 1-17, ISSN:0164-1212, Elsevier Press, Amsterdam, The Netherlands, August 2022.
 * Harshit Gupta, Amir Vahid Dastjerdi , Soumya K. Ghosh, and Rajkumar Buyya, <A href="http://www.buyya.com/papers/iFogSim.pdf">iFogSim: A Toolkit for Modeling and Simulation of Resource Management Techniques in Internet of Things, Edge and Fog Computing Environments</A>, Software: Practice and Experience (SPE), Volume 47, Issue 9, Pages: 1275-1296, ISSN: 0038-0644, Wiley Press, New York, USA, September 2017.
 * Redowan Mahmud and Rajkumar Buyya, <A href="http://www.buyya.com/papers/iFogSim-Tut.pdf">Modelling and Simulation of Fog and Edge Computing Environments using iFogSim Toolkit</A>, Fog and Edge Computing: Principles and Paradigms, R. Buyya and S. Srirama (eds), 433-466pp, ISBN: 978-111-95-2498-4, Wiley Press, New York, USA, January 2019.
