package com.company.bonitasoft

import org.junit.Test
import org.bonitasoft.engine.connector.ConnectorValidationException

class MyKotlinConnectorTest {

    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_missing() {
        val connector = MyKotlinConnector()
        connector.validateInputParameters()
    }
    
    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_empty() {
        val connector = MyKotlinConnector()
        connector.setInputParameters(mapOf("defaultInput" to ""))
        connector.validateInputParameters()
    }
    
    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_not_a_string() {
        val connector = MyKotlinConnector()
        connector.setInputParameters(mapOf("defaultInput" to 38))
        connector.validateInputParameters()
    }
    
    @Test
    fun should_create_output_for_valid_input() {
        val connector = MyKotlinConnector()
        connector.setInputParameters(mapOf("defaultInput" to "valid"))
        val output = connector.execute()
        assert(output["defaultOutput"] == "valid - output") { "" }
    }
}