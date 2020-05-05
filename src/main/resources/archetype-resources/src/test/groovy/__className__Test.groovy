package ${package}

import org.bonitasoft.engine.connector.ConnectorValidationException

import spock.lang.Specification

class ${className}Test extends Specification {

    def should_throw_exception_if_mandatory_input_is_missing() {
        given: 'A connector without input'
        def connector = new ${className}()

        when: 'Validating inputs'
        connector.validateInputParameters()

        then: 'ConnectorValidationException is thrown'
        thrown ConnectorValidationException
    }

    def should_throw_exception_if_mandatory_input_is_empty() {
        given: 'A connector without an empty input'
        def connector = new ${className}()
        connector.setInputParameters(['defaultInput':''])

        when: 'Validating inputs'
        connector.validateInputParameters()

        then: 'ConnectorValidationException is thrown'
        thrown ConnectorValidationException
    }

    def should_throw_exception_if_mandatory_input_is_not_a_string() {
        given: 'A connector without an integer input'
        def connector = new ${className}()
        connector.setInputParameters(['defaultInput':38])

        when: 'Validating inputs'
        connector.validateInputParameters()

        then: 'ConnectorValidationException is thrown'
        thrown ConnectorValidationException
    }

    def should_create_output_for_valid_input() {
        given: 'A connector with a valid input'
        def connector = new ${className}()
        connector.setInputParameters(['defaultInput':'valid'])

        when: 'Executing connector'
        def outputs = connector.execute()

        then: 'Output is created'
        assert outputs['defaultOutput'] == 'valid - output'
    }
}