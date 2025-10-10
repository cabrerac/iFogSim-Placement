package org.fog.test.unit;

import org.fog.placement.SPPHeuristic;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for DeviceState resource allocation logic.
 * Tests resource constraints without running any simulation.
 */
public class DeviceStateTest {
    
    private static final double DELTA = 0.001; // Tolerance for double comparisons
    
    private SPPHeuristic.DeviceState device;
    
    @Before
    public void setUp() {
        // Create a device with 1000 MIPS, 2000 MB RAM, 10000 MB storage
        device = new SPPHeuristic.DeviceState(
            1,              // id
            1000.0,         // totalCpu
            2000.0,         // totalRam
            10000.0,        // totalStorage
            1000.0,         // initialCpu (fully available)
            2000.0,         // initialRam (fully available)
            10000.0         // initialStorage (fully available)
        );
    }
    
    @Test
    public void testFreshDeviceCanFitSmallService() {
        // Should fit a small service on fresh device
        assertTrue("Fresh device should fit small service", 
                   device.canFit(100.0, 200.0, 500.0));
    }
    
    @Test
    public void testFreshDeviceCanFitExactCapacity() {
        // Should fit service that exactly matches capacity
        assertTrue("Device should fit service of exact capacity", 
                   device.canFit(1000.0, 2000.0, 10000.0));
    }
    
    @Test
    public void testCannotFitExceedingCpu() {
        assertFalse("Should not fit service exceeding CPU", 
                    device.canFit(1001.0, 100.0, 100.0));
    }
    
    @Test
    public void testCannotFitExceedingRam() {
        assertFalse("Should not fit service exceeding RAM", 
                    device.canFit(100.0, 2001.0, 100.0));
    }
    
    @Test
    public void testCannotFitExceedingStorage() {
        assertFalse("Should not fit service exceeding storage", 
                    device.canFit(100.0, 100.0, 10001.0));
    }
    
    @Test
    public void testAllocationReducesAvailableResources() {
        device.allocate(200.0, 400.0, 1000.0);
        
        assertEquals("CPU should be reduced", 800.0, device.getCPU(), DELTA);
        assertEquals("RAM should be reduced", 1600.0, device.getRAM(), DELTA);
        assertEquals("Storage should be reduced", 9000.0, device.getStorage(), DELTA);
    }
    
    @Test
    public void testCannotFitAfterAllocation() {
        device.allocate(600.0, 1200.0, 5000.0);
        
        // Try to fit another service that would exceed remaining resources
        assertFalse("Should not fit after partial allocation", 
                    device.canFit(500.0, 900.0, 5100.0));
    }
    
    @Test
    public void testCanFitAfterPartialAllocation() {
        device.allocate(400.0, 800.0, 3000.0);
        
        // Should still fit smaller service in remaining space
        assertTrue("Should fit service in remaining space", 
                   device.canFit(300.0, 600.0, 2000.0));
    }
    
    @Test
    public void testMultipleAllocations() {
        device.allocate(100.0, 200.0, 500.0);
        device.allocate(150.0, 300.0, 750.0);
        device.allocate(250.0, 500.0, 1250.0);
        
        // Total allocated: 500 CPU, 1000 RAM, 2500 storage
        assertEquals("CPU after multiple allocations", 500.0, device.getCPU(), DELTA);
        assertEquals("RAM after multiple allocations", 1000.0, device.getRAM(), DELTA);
        assertEquals("Storage after multiple allocations", 7500.0, device.getStorage(), DELTA);
    }
    
    @Test
    public void testDeallocationRestoresResources() {
        device.allocate(300.0, 600.0, 2000.0);
        device.deallocate(300.0, 600.0, 2000.0);
        
        // Should be back to original state
        assertEquals("CPU should be restored", 1000.0, device.getCPU(), DELTA);
        assertEquals("RAM should be restored", 2000.0, device.getRAM(), DELTA);
        assertEquals("Storage should be restored", 10000.0, device.getStorage(), DELTA);
    }
    
    @Test
    public void testPartialDeallocation() {
        device.allocate(500.0, 1000.0, 5000.0);
        device.deallocate(200.0, 400.0, 2000.0);
        
        // Should have original - allocated + deallocated
        assertEquals("CPU after partial deallocation", 700.0, device.getCPU(), DELTA);
        assertEquals("RAM after partial deallocation", 1400.0, device.getRAM(), DELTA);
        assertEquals("Storage after partial deallocation", 7000.0, device.getStorage(), DELTA);
    }
    
    @Test
    public void testUtilizationOnFreshDevice() {
        assertEquals("Fresh device should have 0% CPU utilization", 
                     0.0, device.getCPUUtil(), DELTA);
        assertEquals("Fresh device should have 0% RAM utilization", 
                     0.0, device.getRAMUtil(), DELTA);
    }
    
    @Test
    public void testUtilizationAfterAllocation() {
        device.allocate(500.0, 1000.0, 5000.0);
        
        // 500 out of 1000 = 50% CPU utilization
        assertEquals("Should have 50% CPU utilization", 
                     0.5, device.getCPUUtil(), 0.01);
        // 1000 out of 2000 = 50% RAM utilization
        assertEquals("Should have 50% RAM utilization", 
                     0.5, device.getRAMUtil(), 0.01);
    }
    
    @Test
    public void testUtilizationAtFullCapacity() {
        device.allocate(1000.0, 2000.0, 10000.0);
        
        assertEquals("Should have 100% CPU utilization", 
                     1.0, device.getCPUUtil(), DELTA);
        assertEquals("Should have 100% RAM utilization", 
                     1.0, device.getRAMUtil(), DELTA);
    }
    
    @Test
    public void testGetId() {
        assertEquals("Should return correct device ID", 1, device.getId());
    }
    
    @Test
    public void testCopyConstructor() {
        device.allocate(300.0, 600.0, 2000.0);
        
        SPPHeuristic.DeviceState copy = new SPPHeuristic.DeviceState(device);
        
        assertEquals("Copy should have same ID", device.getId(), copy.getId());
        assertEquals("Copy should have same CPU", device.getCPU(), copy.getCPU(), DELTA);
        assertEquals("Copy should have same RAM", device.getRAM(), copy.getRAM(), DELTA);
        assertEquals("Copy should have same storage", device.getStorage(), copy.getStorage(), DELTA);
    }
    
    @Test
    public void testCopyConstructorIsIndependent() {
        SPPHeuristic.DeviceState copy = new SPPHeuristic.DeviceState(device);
        
        // Modify the copy
        copy.allocate(100.0, 200.0, 500.0);
        
        // Original should be unchanged
        assertEquals("Original CPU should be unchanged", 1000.0, device.getCPU(), DELTA);
        assertEquals("Original RAM should be unchanged", 2000.0, device.getRAM(), DELTA);
        
        // Copy should be changed
        assertEquals("Copy CPU should be changed", 900.0, copy.getCPU(), DELTA);
        assertEquals("Copy RAM should be changed", 1800.0, copy.getRAM(), DELTA);
    }
    
    @Test
    public void testCompareToByUtilization() {
        SPPHeuristic.DeviceState device1 = new SPPHeuristic.DeviceState(
            1, 1000.0, 2000.0, 10000.0, 1000.0, 2000.0, 10000.0);
        SPPHeuristic.DeviceState device2 = new SPPHeuristic.DeviceState(
            2, 1000.0, 2000.0, 10000.0, 1000.0, 2000.0, 10000.0);
        
        // device1: 30% utilized
        device1.allocate(300.0, 600.0, 3000.0);
        // device2: 50% utilized
        device2.allocate(500.0, 1000.0, 5000.0);
        
        // device1 should come before device2 (lower utilization first)
        assertTrue("Lower utilization device should sort first", 
                   device1.compareTo(device2) < 0);
        assertTrue("Higher utilization device should sort last", 
                   device2.compareTo(device1) > 0);
    }
    
    @Test
    public void testCompareToEqualUtilization() {
        SPPHeuristic.DeviceState device1 = new SPPHeuristic.DeviceState(
            1, 1000.0, 2000.0, 10000.0, 1000.0, 2000.0, 10000.0);
        SPPHeuristic.DeviceState device2 = new SPPHeuristic.DeviceState(
            2, 1000.0, 2000.0, 10000.0, 1000.0, 2000.0, 10000.0);
        
        // Both 50% utilized
        device1.allocate(500.0, 1000.0, 5000.0);
        device2.allocate(500.0, 1000.0, 5000.0);
        
        // Should sort by ID when utilization is equal
        assertTrue("Lower ID should sort first when utilization equal", 
                   device1.compareTo(device2) < 0);
    }
    
    @Test
    public void testZeroResourceAllocation() {
        // Test allocating zero resources (edge case)
        device.allocate(0.0, 0.0, 0.0);
        
        assertEquals("Zero allocation should not change CPU", 
                     1000.0, device.getCPU(), DELTA);
        assertEquals("Zero allocation should not change RAM", 
                     2000.0, device.getRAM(), DELTA);
        assertEquals("Zero allocation should not change storage", 
                     10000.0, device.getStorage(), DELTA);
    }
    
    @Test
    public void testCanFitZeroResources() {
        // Even a fully loaded device should be able to "fit" zero resources
        device.allocate(1000.0, 2000.0, 10000.0); // Fully loaded
        
        assertTrue("Should be able to fit zero resources", 
                   device.canFit(0.0, 0.0, 0.0));
    }
}

