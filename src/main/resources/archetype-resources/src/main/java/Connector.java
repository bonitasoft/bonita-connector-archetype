package ${groupId};

import java.util.Map;

import org.bonitasoft.engine.connector.AbstractConnector;
import org.bonitasoft.engine.connector.ConnectorException;
import org.bonitasoft.engine.connector.ConnectorValidationException;

public class Connector extends AbstractConnector {

    static final String DEFAULT_INPUT = "defaultInput";
    static final String DEFAULT_OUTPUT = "defaultOutput";

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

    @Override
    protected void executeBusinessLogic() throws ConnectorException {
        System.out.println(String.format("Default input: %s", getInputParameter(DEFAULT_INPUT)));
        setOutputParameter(DEFAULT_OUTPUT, String.format("%s - output", getInputParameter(DEFAULT_INPUT)));
    }

}
