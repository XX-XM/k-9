package app.k9mail.feature.account.setup.domain.input

import app.k9mail.core.common.domain.usecase.validation.ValidationError
import app.k9mail.core.common.domain.usecase.validation.ValidationResult
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import assertk.assertions.prop
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class InputFieldTestData<T>(
    val name: String,
    val initialValue: T,
    val initialValueEmpty: T,
    val initialError: ValidationError?,
    val initialIsValid: Boolean,
    val createInitialInput: (value: T, error: ValidationError?, isValid: Boolean) -> InputField<T>,
    val updatedValue: T,
)

@RunWith(Parameterized::class)
class InputFieldTest(
    val data: InputFieldTestData<Any>,
) {

    @Test
    fun `should set default values`() {
        val initialInput = data.createInitialInput(
            data.initialValueEmpty,
            data.initialError,
            data.initialIsValid,
        )

        assertThat(initialInput).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.initialValueEmpty)
            prop("error") { InputField<*>::error.call(it) }.isNull()
            prop("isValid") { InputField<*>::isValid.call(it) }.isFalse()
        }
    }

    @Test
    fun `should reset error and isValid when value changed`() {
        val initialInput = data.createInitialInput(
            data.initialValue,
            TestValidationError,
            true,
        )

        val result = initialInput.updateValue(data.updatedValue)

        assertThat(result).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.updatedValue)
            prop("error") { InputField<*>::error.call(it) }.isNull()
            prop("isValid") { InputField<*>::isValid.call(it) }.isFalse()
        }
    }

    @Test
    fun `should reset isValid when error set`() {
        val initialInput = data.createInitialInput(
            data.initialValue,
            null,
            true,
        )

        val result = initialInput.updateError(TestValidationError)

        assertThat(result).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.initialValue)
            prop("error") { InputField<*>::error.call(it) }.isEqualTo(TestValidationError)
            prop("isValid") { InputField<*>::isValid.call(it) }.isFalse()
        }
    }

    @Test
    fun `should reset error when valid`() {
        val initialInput = data.createInitialInput(
            data.initialValue,
            TestValidationError,
            false,
        )

        val result = initialInput.updateValidity(isValid = true)

        assertThat(result).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.initialValue)
            prop("error") { InputField<*>::error.call(it) }.isNull()
            prop("isValid") { InputField<*>::isValid.call(it) }.isTrue()
        }
    }

    @Test
    fun `should not reset error when invalid`() {
        val initialInput = data.createInitialInput(
            data.initialValue,
            TestValidationError,
            false,
        )

        val result = initialInput.updateValidity(isValid = false)

        assertThat(result).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.initialValue)
            prop("error") { InputField<*>::error.call(it) }.isEqualTo(TestValidationError)
            prop("isValid") { InputField<*>::isValid.call(it) }.isFalse()
        }
    }

    @Test
    fun `should change error when error changed`() {
        val initialInput = data.createInitialInput(
            data.initialValue,
            TestValidationError,
            false,
        )

        val result = initialInput.updateError(TestValidationError2)

        assertThat(result).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.initialValue)
            prop("error") { InputField<*>::error.call(it) }.isEqualTo(TestValidationError2)
            prop("isValid") { InputField<*>::isValid.call(it) }.isFalse()
        }
    }

    @Test
    fun `should map from success ValidationResult`() {
        val initialInput = data.createInitialInput(
            data.initialValue,
            TestValidationError,
            false,
        )

        val result = initialInput.updateFromValidationResult(ValidationResult.Success)

        assertThat(result).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.initialValue)
            prop("error") { InputField<*>::error.call(it) }.isNull()
            prop("isValid") { InputField<*>::isValid.call(it) }.isTrue()
        }
    }

    @Test
    fun `should map from failure ValidationResult`() {
        val initialInput = data.createInitialInput(
            data.initialValue,
            null,
            true,
        )

        val result = initialInput.updateFromValidationResult(ValidationResult.Failure(TestValidationError))

        assertThat(result).all {
            prop("value") { InputField<*>::value.call(it) }.isEqualTo(data.initialValue)
            prop("error") { InputField<*>::error.call(it) }.isEqualTo(TestValidationError)
            prop("isValid") { InputField<*>::isValid.call(it) }.isFalse()
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<InputFieldTestData<*>> = listOf(
            InputFieldTestData(
                name = "StringInputField",
                createInitialInput = { value, error, isValid -> StringInputField(value, error, isValid) },
                initialValue = "input",
                initialValueEmpty = "",
                initialError = null,
                initialIsValid = false,
                updatedValue = "new value",
            ),
            InputFieldTestData(
                name = "NumberInputField",
                createInitialInput = { value, error, isValid -> NumberInputField(value, error, isValid) },
                initialValue = 123L,
                initialValueEmpty = null,
                initialError = null,
                initialIsValid = false,
                updatedValue = 456L,
            ),
        )
    }

    private object TestValidationError : ValidationError
    private object TestValidationError2 : ValidationError
}
