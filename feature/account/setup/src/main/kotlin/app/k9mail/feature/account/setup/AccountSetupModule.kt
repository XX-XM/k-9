package app.k9mail.feature.account.setup

import app.k9mail.feature.account.setup.ui.AccountSetupViewModel
import app.k9mail.feature.account.setup.ui.options.AccountOptionsContract
import app.k9mail.feature.account.setup.ui.options.AccountOptionsValidator
import app.k9mail.feature.account.setup.ui.options.AccountOptionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val featureAccountSetupModule: Module = module {
    factory<AccountOptionsContract.Validator> { AccountOptionsValidator() }

    viewModel { AccountSetupViewModel() }
    viewModel {
        AccountOptionsViewModel(
            validator = get(),
        )
    }
}
