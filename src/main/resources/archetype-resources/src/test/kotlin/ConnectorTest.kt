package ${groupId}

import org.junit.Test
import org.bonitasoft.engine.connector.ConnectorValidationException

class ConnectorTest {

    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_missing() {
        val connector = Connector()
        connector.validateInputParameters()
    }
    
    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_empty() {
        val connector = Connector()
        connector.setInputParameters(mapOf("defaultInput" to ""))
        connector.validateInputParameters()
    }
    
    @Test(expected = ConnectorValidationException::class)
    fun should_throw_exception_if_mandatory_input_is_not_a_string() {
        val connector = Connector()
        connector.setInputParameters(mapOf("defaultInput" to 38))
        connector.validateInputParameters()
    }
    
    @Test
    fun should_create_output_for_valid_input() {
        val connector = Connector()
        connector.setInputParameters(mapOf("defaultInput" to "valid"))
        val output = connector.execute()
        assert(output["defaultOutput"] == "valid - output") { "" }
    }
}