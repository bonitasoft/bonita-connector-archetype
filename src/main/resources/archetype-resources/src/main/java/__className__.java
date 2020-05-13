package ${package};

import java.util.logging.Logger;

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public class ${className} extends AbstractConnector {

    private static final Logger LOGGER = Logger.getLogger(${className}.class.getName());

    static final String DEFAULT_INPUT = "defaultInput";
    static final String DEFAULT_OUTPUT = "defaultOutput";

    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/${artifactId}.def)
     * You should: 
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        checkMandatoryStringInput(DEFAULT_INPUT);
    }

    protected void checkMandatoryStringInput(String inputName) throws ConnectorValidationException {
        try {
            String value = (String) getInputParameter(inputName);
            if (value == null || value.isEmpty()) {
                throw new ConnectorValidationException(this,
                        String.format("Mandatory parameter '%s' is missing.", inputName));
            }
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(this, String.format("'%s' parameter must be a String", inputName));
        }
    }

    /**
     * Core method: 
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        LOGGER.info(String.format("Default input: %s", getInputParameter(DEFAULT_INPUT)));
        setOutputParameter(DEFAULT_OUTPUT, String.format("%s - output", getInputParameter(DEFAULT_INPUT)));
    }
    
    /**
     * [Optional] Open a connection to remote server
     */
    @Override
    public void connect() throws ConnectorException{}

    /**
     * [Optional] Close connection to remote server
     */
    @Override
    public void disconnect() throws ConnectorException{}
}
