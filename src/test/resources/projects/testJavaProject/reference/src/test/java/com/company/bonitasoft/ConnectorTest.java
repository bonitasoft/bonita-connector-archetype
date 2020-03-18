package com.company.bonitasoft;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.junit.Test;

public class ConnectorTest {

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_missing() throws ConnectorValidationException {
        Connector connector = new Connector();
        connector.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_empty() throws ConnectorValidationException {
        Connector connector = new Connector();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Connector.DEFAULT_INPUT, "");
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Test(expected = ConnectorValidationException.class)
    public void should_throw_exception_if_mandatory_input_is_not_a_string() throws ConnectorValidationException {
        Connector connector = new Connector();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Connector.DEFAULT_INPUT, 38);
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

    @Test
    public void should_accept_valid_input() throws ConnectorValidationException {
        Connector connector = new Connector();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(Connector.DEFAULT_INPUT, "valid");
        connector.setInputParameters(parameters);
        connector.validateInputParameters();
    }

}