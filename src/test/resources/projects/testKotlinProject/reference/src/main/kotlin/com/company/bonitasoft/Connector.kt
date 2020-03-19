package com.company.bonitasoft

import org.bonitasoft.engine.connector.AbstractConnector
import org.bonitasoft.engine.connector.ConnectorValidationException

class Connector : AbstractConnector {

    constructor() : super()

    val defaultInput = "defaultInput"
    val defaultOutput = "defaultOutput"

    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/connectorKotlin.def)
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
        println("Default input: $value")
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