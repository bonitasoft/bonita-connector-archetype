package com.company.bonitasoft;

import org.bonitasoft.engine.connector.ConnectorValidationException

import spock.lang.Specification

class ConnectorTest extends Specification {
    
    def should_throw_exception_if_mandatory_input_is_missing() {
        given: "A connector without input"
        def connector = new Connector()
        
        when: "Validating inputs"
        connector.validateInputParameters()
        
        then: "ConnectorValidationException is thrown"
        thrown ConnectorValidationException
    }
    
    def should_throw_exception_if_mandatory_input_is_empty() {
        given: "A connector without an empty input"
        def connector = new Connector()
        connector.setInputParameters(["defaultInput":""])
        
        when: "Validating inputs"
        connector.validateInputParameters()
        
        then: "ConnectorValidationException is thrown"
        thrown ConnectorValidationException
    }
    
    def should_throw_exception_if_mandatory_input_is_not_a_string() {
        given: "A connector without an integer input"
        def connector = new Connector()
        connector.setInputParameters(["defaultInput":38])
        
        when: "Validating inputs"
        connector.validateInputParameters()
        
        then: "ConnectorValidationException is thrown"
        thrown ConnectorValidationException
    }
    
    def should_accept_valid_input() {
        given: "A connector with a valid input"
        def connector = new Connector()
        connector.setInputParameters(["defaultInput":"valid"])
        
        when: "Validating inputs"
        connector.validateInputParameters()
        
        then: "No exception is thrown"
    }
    
}