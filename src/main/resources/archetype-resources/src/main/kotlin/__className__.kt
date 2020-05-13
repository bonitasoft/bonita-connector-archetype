package ${package}

import java.util.logging.Logger

import org.bonitasoft.engine.connector.AbstractConnector
import org.bonitasoft.engine.connector.ConnectorValidationException

class ${className} : AbstractConnector {

    constructor() : super()

    val defaultInput = "defaultInput"
    val defaultOutput = "defaultOutput"
    val logger = Logger.getLogger(${className}::class.java.name)

    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/${artifactId}.def)
     * You should: 
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
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

    /**
     * Core method: 
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    override fun executeBusinessLogic() {
        val value = getInputParameter(defaultInput)
        logger.info("Default input: $value")
        setOutputParameter(defaultOutput, "$value - output")
    }
    
    /**
     * [Optional] Open a connection to remote server
     */
    override fun connect(){}
    
    /**
     * [Optional] Close connection to remote server
     */
    override fun disconnect(){}
}