package ${groupId};

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector extends AbstractConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class.getName());
    
    static final String DEFAULT_INPUT = "defaultInput";
    
    
    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        checkMandatoryStringInput(DEFAULT_INPUT);
    }
    
    protected void checkMandatoryStringInput(String inputName) throws ConnectorValidationException {
        try {
            String value = (String) getInputParameter(inputName);
            if (value == null || value.isEmpty()) {
                throw new ConnectorValidationException(this, String.format("Mandatory parameter '%s' is missing.", inputName));
            }
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(this, String.format("'%s' parameter must be a String", inputName));
        }
    }
    
    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        LOGGER.info(String.format("Default input: %s", getInputParameter(DEFAULT_INPUT)));
    }

}
