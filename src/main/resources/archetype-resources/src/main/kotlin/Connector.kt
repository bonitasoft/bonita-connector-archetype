package ${groupId}

import org.bonitasoft.engine.connector.AbstractConnector
import org.bonitasoft.engine.connector.ConnectorValidationException
import org.slf4j.LoggerFactory;

class Connector : AbstractConnector {

    constructor() : super()

    val logger = LoggerFactory.getLogger(Connector::class.java.name)
    val defaultInput = "defaultInput"

    override fun validateInputParameters() {
        checkMandatoryStringInput(defaultInput)
    }

    fun checkMandatoryStringInput(inputName: String) {
        val value = getInputParameter(inputName)
        if (!(value is String)) {
            throw ConnectorValidationException(this, "'$inputName' parameter must be a String.")
        } else if (value.isNullOrEmpty()) {
            throw ConnectorValidationException(this, "Mandatory parameter '$inputName' is missing.")
        }
    }

    override fun executeBusinessLogic() {
        val value = getInputParameter(defaultInput)
        logger.info("Default input: $value")
    }
}