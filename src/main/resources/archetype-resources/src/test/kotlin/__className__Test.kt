package ${package}

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.bonitasoft.engine.connector.ConnectorValidationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ${className}Test {

    lateinit var connector: ${className}

    @BeforeEach
    fun setUp() {
        connector = ${className}();
    }

    @Test
    fun `should throw exception if mandatory input is missing`() {
        assertThatThrownBy { connector.validateInputParameters() }
                .isExactlyInstanceOf(ConnectorValidationException::class.java)
    }

    @Test
    fun `should throw exception if mandatory input is empty`() {
        // Given
        connector.setInputParameter(${className}.DEFAULT_INPUT, "")

        // When
        assertThatThrownBy { connector.validateInputParameters() }
                // Then
                .isExactlyInstanceOf(ConnectorValidationException::class.java)
    }

    @Test
    fun `should throw exception if mandatory input is not a string`() {
        // Given
        connector.setInputParameter(${className}.DEFAULT_INPUT, 38);

        // When
        assertThatThrownBy { connector.validateInputParameters() }
                // Then
                .isExactlyInstanceOf(ConnectorValidationException::class.java)
    }

    @Test
    fun `should create output for valid input`() {
        // Given
        connector.setInputParameter(${className}.DEFAULT_INPUT, "valid");

        // When
        connector.executeBusinessLogic();

        // Then
        val output = connector.getOutputParameter(${className}.DEFAULT_OUTPUT);
        assertThat(output).isEqualTo("valid - output");
    }
}