package ${package};

import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ${className}Test {

    ${className} connector;

    @BeforeEach
    void setUp() {
        connector = new ${className}();
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_missing() {
        assertThrows(ConnectorValidationException.class, () ->
                connector.validateInputParameters()
        );
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_empty() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(${className}.DEFAULT_INPUT, "");
        connector.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                connector.validateInputParameters()
        );
    }

    @Test
    void should_throw_exception_if_mandatory_input_is_not_a_string() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(${className}.DEFAULT_INPUT, 38);
        connector.setInputParameters(parameters);
        assertThrows(ConnectorValidationException.class, () ->
                connector.validateInputParameters()
        );
    }

    @Test
    void should_create_output_for_valid_input() throws ConnectorException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(${className}.DEFAULT_INPUT, "valid");
        connector.setInputParameters(parameters);
        Map<String, Object> outputs = connector.execute();
        assertThat(outputs.get("defaultOutput")).isEqualTo("valid - output");
    }

}