package com.example

import com.example.engine.FormulaEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testFormulaEngine_standardMultiplication() {
        val result = FormulaEngine.evaluate("(CGPA * 9.5)", 9.25)
        assertTrue(result.isSuccess)
        assertEquals(87.875, result.getOrNull()!!, 0.001)
    }

    @Test
    fun testFormulaEngine_linearFormula() {
        val result = FormulaEngine.evaluate("(CGPA * 7.1) + 12", 9.0)
        assertTrue(result.isSuccess)
        assertEquals(75.9, result.getOrNull()!!, 0.001)
    }

    @Test
    fun testFormulaEngine_simpleMultiplier() {
        val result = FormulaEngine.evaluate("CGPA * 10", 8.8)
        assertTrue(result.isSuccess)
        assertEquals(88.0, result.getOrNull()!!, 0.001)
    }

    @Test
    fun testFormulaEngine_offsetFormula() {
        val result = FormulaEngine.evaluate("(CGPA * 10) + 5", 9.0)
        assertTrue(result.isSuccess)
        assertEquals(95.0, result.getOrNull()!!, 0.001)
    }

    @Test
    fun testFormulaEngine_divisionByZeroSafeHandling() {
        val result = FormulaEngine.evaluate("CGPA / 0", 9.0)
        assertTrue(result.isFailure)
    }
}
