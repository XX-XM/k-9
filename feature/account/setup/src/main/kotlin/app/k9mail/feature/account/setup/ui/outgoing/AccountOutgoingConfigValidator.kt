package app.k9mail.feature.account.setup.ui.outgoing

import app.k9mail.core.common.domain.usecase.validation.ValidationResult
import app.k9mail.feature.account.setup.domain.usecase.ValidatePassword
import app.k9mail.feature.account.setup.domain.usecase.ValidatePort
import app.k9mail.feature.account.setup.domain.usecase.ValidateServer
import app.k9mail.feature.account.setup.domain.usecase.ValidateUsername

class AccountOutgoingConfigValidator(
    private val serverValidator: ValidateServer = ValidateServer(),
    private val portValidator: ValidatePort = ValidatePort(),
    private val usernameValidator: ValidateUsername = ValidateUsername(),
    private val passwordValidator: ValidatePassword = ValidatePassword(),
) : AccountOutgoingConfigContract.Validator {
    override fun validateServer(server: String): ValidationResult {
        return serverValidator.execute(server)
    }

    override fun validatePort(port: Long?): ValidationResult {
        return portValidator.execute(port)
    }

    override fun validateUsername(username: String): ValidationResult {
        return usernameValidator.execute(username)
    }

    override fun validatePassword(password: String): ValidationResult {
        return passwordValidator.execute(password)
    }
}
