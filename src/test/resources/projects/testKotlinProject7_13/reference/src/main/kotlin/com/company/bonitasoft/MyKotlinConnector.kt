package com.company.bonitasoft

import org.bonitasoft.engine.connector.AbstractConnector
import org.bonitasoft.engine.connector.ConnectorValidationException
import java.util.logging.Logger

open class MyKotlinConnector : AbstractConnector {

    val logger = Logger.getLogger(MyKotlinConnector::class.java.name)

    companion object {
        const val DEFAULT_INPUT = "defaultInput"
        const val DEFAULT_OUTPUT = "defaultOutput"
    }

    constructor() : super()

    /**
     * Perform validation on the inputs defined on the connector definition (src/main/resources/connector-kotlin-test.def)
     * You should:
     * - validate that mandatory inputs are presents
     * - validate that the content of the inputs is coherent with your use case (e.g: validate that a date is / isn't in the past ...)
     */
    override fun validateInputParameters() {
        checkMandatoryStringInput(DEFAULT_INPUT)
    }

    fun checkMandatoryStringInput(inputName: String) {
        val value = getInputParameter(inputName)
        if (!(value is String)) {
            throw ConnectorValidationException(this, "'$inputName' parameter must be a String.")
        } else if (value.isEmpty()) {
            throw ConnectorValidationException(this, "Mandatory parameter '$inputName' is missing.")
        }
    }

    fun setInputParameter(name: String, value: Any) {
        super.setInputParameters(mapOf(name to value))
    }

    fun getOutputParameter(name: String): Any? {
        return super.getOutputParameters()[name]
    }

    /**
     * Core method:
     * - Execute all the business logic of your connector using the inputs (connect to an external service, compute some values ...).
     * - Set the output of the connector execution. If outputs are not set, connector fails.
     */
    public override fun executeBusinessLogic() {
        val value = getInputParameter(DEFAULT_INPUT)
        logger.info("Default input: $value")
        setOutputParameter(DEFAULT_OUTPUT, "$value - output")
    }

    /**
     * [Optional] Open a connection to remote server
     */
    override fun connect() {}

    /**
     * [Optional] Close connection to remote server
     */
    override fun disconnect() {}
}