package com.company.bonitasoft;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.Test;

public class MyJavaConnectorTest {

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_missing() throws ConnectorValidationException {
        MyJavaConnector connector = new MyJavaConnector();
        connector.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_empty() throws ConnectorValidationException {
        MyJavaConnector connector = new MyJavaConnector();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MyJavaConnector.DEFAULT_INPUT, "");
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_not_a_string() throws ConnectorValidationException {
        MyJavaConnector connector = new MyJavaConnector();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MyJavaConnector.DEFAULT_INPUT, 38);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Test
    public void should_create_output_for_valid_input() throws ConnectorException {
        MyJavaConnector connector = new MyJavaConnector();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MyJavaConnector.DEFAULT_INPUT, "valid");
        connector.setInputParameters(parameters);
        Map<String, Object> outputs = connector.execute();
        assertTrue("Expected 'valid - output' as output.",
                Objects.equals(outputs.get("defaultOutput"), "valid - output"));
    }

}