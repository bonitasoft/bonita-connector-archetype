package ${package}

import org.junit.Test
import org.bonitasoft.engine.connector.ConnectorValidationException

class ${className}Test {

    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_missing() {
        val connector = ${className}()
        connector.validateInputParameters()
    }
    
    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_empty() {
        val connector = ${className}()
        connector.setInputParameters(mapOf("defaultInput" to ""))
        connector.validateInputParameters()
    }
    
    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_not_a_string() {
        val connector = ${className}()
        connector.setInputParameters(mapOf("defaultInput" to 38))
        connector.validateInputParameters()
    }
    
    @Test
    fun should_create_output_for_valid_input() {
        val connector = ${className}()
        connector.setInputParameters(mapOf("defaultInput" to "valid"))
        val output = connector.execute()
        assert(output["defaultOutput"] == "valid - output") { "" }
    }
}