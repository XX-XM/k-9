package app.k9mail.feature.account.setup.domain.usecase

import app.k9mail.core.common.domain.usecase.validation.ValidationError
import app.k9mail.core.common.domain.usecase.validation.ValidationResult
import app.k9mail.core.common.domain.usecase.validation.ValidationUseCase

class ValidatePort : ValidationUseCase<Long?> {

    override fun execute(input: Long?): ValidationResult {
        return when {
            input == null -> ValidationResult.Failure(ValidatePortError.EmptyPort)
            input < MIN_PORT_NUMBER -> ValidationResult.Failure(ValidatePortError.InvalidPort)
            input > MAX_PORT_NUMBER -> ValidationResult.Failure(ValidatePortError.InvalidPort)
            else -> ValidationResult.Success
        }
    }

    sealed interface ValidatePortError : ValidationError {
        object EmptyPort : ValidatePortError
        object InvalidPort : ValidatePortError
    }

    companion object {
        const val MAX_PORT_NUMBER = 65535
        const val MIN_PORT_NUMBER = 0
    }
}