package org.fog.test.unit;

import org.fog.placement.PlacementLogicFactory;
import org.fog.placement.MicroservicePlacementLogic;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for PlacementLogicFactory.
 * Tests configuration parsing and parameter validation without running simulations.
 */
public class PlacementLogicFactoryTest {
    
    private PlacementLogicFactory factory = new PlacementLogicFactory();
    
    @Test
    public void testGetPlacementLogicCodeACO() {
        int code = PlacementLogicFactory.getPlacementLogicCode("ACO");
        assertEquals("ACO should map to correct code", 13, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeBestFit() {
        int code = PlacementLogicFactory.getPlacementLogicCode("BEST_FIT");
        assertEquals("BEST_FIT should map to correct code", 7, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeClosestFit() {
        int code = PlacementLogicFactory.getPlacementLogicCode("CLOSEST_FIT");
        assertEquals("CLOSEST_FIT should map to correct code", 8, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeMaxFit() {
        int code = PlacementLogicFactory.getPlacementLogicCode("MAX_FIT");
        assertEquals("MAX_FIT should map to correct code", 9, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeILP() {
        int code = PlacementLogicFactory.getPlacementLogicCode("ILP");
        assertEquals("ILP should map to correct code", 14, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeRandom() {
        int code = PlacementLogicFactory.getPlacementLogicCode("RANDOM");
        assertEquals("RANDOM should map to correct code", 10, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeCaseInsensitive() {
        int code1 = PlacementLogicFactory.getPlacementLogicCode("aco");
        int code2 = PlacementLogicFactory.getPlacementLogicCode("ACO");
        int code3 = PlacementLogicFactory.getPlacementLogicCode("AcO");
        
        assertEquals("Should be case insensitive", code1, code2);
        assertEquals("Should be case insensitive", code2, code3);
    }
    
    @Test
    public void testGetPlacementLogicCodeInvalidReturnsNegative() {
        int code = PlacementLogicFactory.getPlacementLogicCode("INVALID_ALGORITHM");
        assertEquals("Invalid algorithm should return -1", -1, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeEmptyString() {
        int code = PlacementLogicFactory.getPlacementLogicCode("");
        assertEquals("Empty string should return -1", -1, code);
    }
    
    @Test
    public void testGetPlacementLogicCodeNull() {
        try {
            int code = PlacementLogicFactory.getPlacementLogicCode(null);
            // If it doesn't throw, should return -1
            assertEquals("Null should return -1", -1, code);
        } catch (NullPointerException e) {
            // Also acceptable - null handling
            assertTrue("Null handling is acceptable", true);
        }
    }
    
    @Test
    public void testGetPlacementLogicNameFromCode() {
        String name = PlacementLogicFactory.getPlacementLogicName(13);
        assertEquals("Code 13 should map to ACO", "ACO", name);
    }
    
    @Test
    public void testGetPlacementLogicNameBestFit() {
        String name = PlacementLogicFactory.getPlacementLogicName(7);
        assertEquals("Code 7 should map to BEST_FIT", "BEST_FIT", name);
    }
    
    @Test
    public void testGetPlacementLogicNameInvalidCode() {
        String name = PlacementLogicFactory.getPlacementLogicName(999);
        assertNull("Invalid code should return null", name);
    }
    
    @Test
    public void testGetPlacementLogicNameNegativeCode() {
        String name = PlacementLogicFactory.getPlacementLogicName(-1);
        assertNull("Negative code should return null", name);
    }
    
    @Test
    public void testRoundTripStringToIntToString() {
        String original = "ACO";
        int code = PlacementLogicFactory.getPlacementLogicCode(original);
        String result = PlacementLogicFactory.getPlacementLogicName(code);
        
        assertEquals("Round trip should preserve value", original, result);
    }
    
    @Test
    public void testRoundTripIntToStringToInt() {
        int original = 13; // ACO
        String name = PlacementLogicFactory.getPlacementLogicName(original);
        int result = PlacementLogicFactory.getPlacementLogicCode(name);
        
        assertEquals("Round trip should preserve value", original, result);
    }
    
    @Test
    public void testGetPlacementLogicByStringACO() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic("ACO", 1);
        
        assertNotNull("ACO logic should be created", logic);
        assertEquals("Should have correct name", "ACO", logic.getName());
    }
    
    @Test
    public void testGetPlacementLogicByStringBestFit() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic("BEST_FIT", 1);
        
        assertNotNull("BEST_FIT logic should be created", logic);
        assertEquals("Should have correct name", "BestFit", logic.getName());
    }
    
    @Test
    public void testGetPlacementLogicByStringClosestFit() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic("CLOSEST_FIT", 1);
        
        assertNotNull("CLOSEST_FIT logic should be created", logic);
        assertEquals("Should have correct name", "ClosestFit", logic.getName());
    }
    
    @Test
    public void testGetPlacementLogicByStringMaxFit() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic("MAX_FIT", 1);
        
        assertNotNull("MAX_FIT logic should be created", logic);
        assertEquals("Should have correct name", "MaxFit", logic.getName());
    }
    
    @Test
    public void testGetPlacementLogicByStringILP() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic("ILP", 1);
        
        assertNotNull("ILP logic should be created", logic);
        assertEquals("Should have correct name", "ILP", logic.getName());
    }
    
    @Test
    public void testGetPlacementLogicByStringInvalid() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic("INVALID", 1);
        
        assertNull("Invalid string should return null", logic);
    }
    
    @Test
    public void testGetPlacementLogicByIntACO() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic(13, 1);
        
        assertNotNull("ACO logic should be created", logic);
        assertEquals("Should have correct name", "ACO", logic.getName());
    }
    
    @Test
    public void testGetPlacementLogicByIntBestFit() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic(7, 1);
        
        assertNotNull("BEST_FIT logic should be created", logic);
        assertEquals("Should have correct name", "BestFit", logic.getName());
    }
    
    @Test
    public void testGetPlacementLogicByIntInvalid() {
        MicroservicePlacementLogic logic = factory.getPlacementLogic(999, 1);
        
        assertNull("Invalid int should return null", logic);
    }
    
    @Test
    public void testAllDefinedConstantsHaveMapping() {
        // Test that all defined constants can be retrieved
        int[] codes = {7, 8, 9, 10, 11, 12, 13, 14}; // All heuristic codes
        
        for (int code : codes) {
            String name = PlacementLogicFactory.getPlacementLogicName(code);
            assertNotNull("Code " + code + " should have a name mapping", name);
            
            int retrievedCode = PlacementLogicFactory.getPlacementLogicCode(name);
            assertEquals("Name should map back to same code", code, retrievedCode);
        }
    }
    
    @Test
    public void testPlacementLogicWithDifferentFonIds() {
        MicroservicePlacementLogic logic1 = factory.getPlacementLogic("ACO", 1);
        MicroservicePlacementLogic logic2 = factory.getPlacementLogic("ACO", 2);
        
        assertNotNull("Logic with fonId 1 should be created", logic1);
        assertNotNull("Logic with fonId 2 should be created", logic2);
        assertNotSame("Different fonIds should create different instances", logic1, logic2);
    }
    
    @Test
    public void testConstantsMatchExpectedValues() {
        // Verify the actual constant values haven't changed
        assertEquals("BEST_FIT constant", 7, PlacementLogicFactory.BEST_FIT);
        assertEquals("CLOSEST_FIT constant", 8, PlacementLogicFactory.CLOSEST_FIT);
        assertEquals("MAX_FIT constant", 9, PlacementLogicFactory.MAX_FIT);
        assertEquals("RANDOM constant", 10, PlacementLogicFactory.RANDOM);
        assertEquals("MULTI_OPT constant", 11, PlacementLogicFactory.MULTI_OPT);
        assertEquals("SIMULATED_ANNEALING constant", 12, PlacementLogicFactory.SIMULATED_ANNEALING);
        assertEquals("ACO constant", 13, PlacementLogicFactory.ACO);
        assertEquals("ILP constant", 14, PlacementLogicFactory.ILP);
    }
}

