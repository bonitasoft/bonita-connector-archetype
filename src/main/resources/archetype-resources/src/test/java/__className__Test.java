package ${package};

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.Test;

public class ${className}Test {

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_missing() throws ConnectorValidationException {
        ${className} connector = new ${className}();
        connector.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_empty() throws ConnectorValidationException {
        ${className} connector = new ${className}();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(${className}.DEFAULT_INPUT, "");
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_not_a_string() throws ConnectorValidationException {
        ${className} connector = new ${className}();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(${className}.DEFAULT_INPUT, 38);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Test
    public void should_create_output_for_valid_input() throws ConnectorException {
        ${className} connector = new ${className}();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(${className}.DEFAULT_INPUT, "valid");
        connector.setInputParameters(parameters);
        Map<String, Object> outputs = connector.execute();
        assertTrue("Expected 'valid - output' as output.",
                Objects.equals(outputs.get("defaultOutput"), "valid - output"));
    }

}