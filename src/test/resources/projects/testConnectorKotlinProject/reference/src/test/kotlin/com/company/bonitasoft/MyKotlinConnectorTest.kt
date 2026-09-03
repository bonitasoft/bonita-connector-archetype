package com.company.bonitasoft

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.bonitasoft.engine.connector.ConnectorValidationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MyKotlinConnectorTest {

    lateinit var connector: MyKotlinConnector

    @BeforeEach
    fun setUp() {
        connector = MyKotlinConnector();
    }

    @Test
    fun `should throw exception if mandatory input is missing`() {
        assertThatThrownBy { connector.validateInputParameters() }
                .isExactlyInstanceOf(ConnectorValidationException::class.java)
    }

    @Test
    fun `should throw exception if mandatory input is empty`() {
        // Given
        connector.setInputParameter(MyKotlinConnector.DEFAULT_INPUT, "")

        // When
        assertThatThrownBy { connector.validateInputParameters() }
                // Then
                .isExactlyInstanceOf(ConnectorValidationException::class.java)
    }

    @Test
    fun `should throw exception if mandatory input is not a string`() {
        // Given
        connector.setInputParameter(MyKotlinConnector.DEFAULT_INPUT, 38);

        // When
        assertThatThrownBy { connector.validateInputParameters() }
                // Then
                .isExactlyInstanceOf(ConnectorValidationException::class.java)
    }

    @Test
    fun `should create output for valid input`() {
        // Given
        connector.setInputParameter(MyKotlinConnector.DEFAULT_INPUT, "valid");

        // When
        connector.executeBusinessLogic();

        // Then
        val output = connector.getOutputParameter(MyKotlinConnector.DEFAULT_OUTPUT);
        assertThat(output).isEqualTo("valid - output");
    }
}