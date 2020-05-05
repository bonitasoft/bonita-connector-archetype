package ${package}

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

class ${className} extends AbstractConnector {
    
    def defaultInput = "defaultInput"
    def defaultOutput = "defaultOutput"
    
    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/${connectorName}.def)
     * You should:
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
    @Override
    def void validateInputParameters() throws ConnectorValidationException {
        checkMandatoryStringInput(defaultInput)
    }
    
    def checkMandatoryStringInput(inputName) throws ConnectorValidationException {
        def value = getInputParameter(inputName)
        if (value in String) {
            if (!value) {
                throw new ConnectorValidationException(this, "Mandatory parameter '$inputName' is missing.")
            }
        } else {
            throw new ConnectorValidationException(this, "'$inputName' parameter must be a String")
        }
    }
    
    /**
     * Core method:
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    @Override
    def void executeBusinessLogic() throws ConnectorException {
        def defaultInput = getInputParameter(defaultInput)
        println "Default input: $defaultInput"
        setOutputParameter(defaultOutput, "$defaultInput - output".toString())
    }
    
    /**
     * [Optional] Open a connection to remote server
     */
    @Override
    def void connect() throws ConnectorException{}

    /**
     * [Optional] Close connection to remote server
     */
    @Override
    def void disconnect() throws ConnectorException{}
}