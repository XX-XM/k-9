package app.k9mail.feature.account.setup.ui

import app.cash.turbine.testIn
import app.k9mail.core.ui.compose.testing.MainDispatcherRule
import app.k9mail.feature.account.setup.ui.AccountSetupContract.Effect.NavigateBack
import app.k9mail.feature.account.setup.ui.AccountSetupContract.Effect.NavigateNext
import app.k9mail.feature.account.setup.ui.AccountSetupContract.SetupStep
import app.k9mail.feature.account.setup.ui.AccountSetupContract.State
import assertk.assertions.assertThatAndTurbinesConsumed
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountSetupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `should forward step state on next event`() = runTest {
        val viewModel = AccountSetupViewModel()
        val stateTurbine = viewModel.state.testIn(backgroundScope)
        val effectTurbine = viewModel.effect.testIn(backgroundScope)
        val turbines = listOf(stateTurbine, effectTurbine)

        // Initial state
        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.AUTO_CONFIG)
        }

        viewModel.event(AccountSetupContract.Event.OnNext)

        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.INCOMING_CONFIG)
        }

        viewModel.event(AccountSetupContract.Event.OnNext)

        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.OUTGOING_CONFIG)
        }

        viewModel.event(AccountSetupContract.Event.OnNext)

        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.OPTIONS)
        }

        viewModel.event(AccountSetupContract.Event.OnNext)

        assertThatAndTurbinesConsumed(
            actual = effectTurbine.awaitItem(),
            turbines = turbines,
        ) {
            isEqualTo(NavigateNext)
        }
    }

    @Test
    fun `should rewind step state on back event`() = runTest {
        val initialState = State(setupStep = SetupStep.OPTIONS)
        val viewModel = AccountSetupViewModel(initialState)
        val stateTurbine = viewModel.state.testIn(backgroundScope)
        val effectTurbine = viewModel.effect.testIn(backgroundScope)
        val turbines = listOf(stateTurbine, effectTurbine)

        // Initial state
        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.OPTIONS)
        }

        viewModel.event(AccountSetupContract.Event.OnBack)

        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.OUTGOING_CONFIG)
        }

        viewModel.event(AccountSetupContract.Event.OnBack)

        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.INCOMING_CONFIG)
        }

        viewModel.event(AccountSetupContract.Event.OnBack)

        assertThatAndTurbinesConsumed(
            actual = stateTurbine.awaitItem(),
            turbines = turbines,
        ) {
            prop(State::setupStep).isEqualTo(SetupStep.AUTO_CONFIG)
        }

        viewModel.event(AccountSetupContract.Event.OnBack)

        assertThatAndTurbinesConsumed(
            actual = effectTurbine.awaitItem(),
            turbines = turbines,
        ) {
            isEqualTo(NavigateBack)
        }
    }
}
