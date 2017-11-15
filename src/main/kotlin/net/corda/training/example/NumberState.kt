package net.corda.training.example

import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

data class NumberState(val number: Int,
                       val alice: Party,
                       val bob: Party,
                       override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState {
    val contract = NumberContract()
    override val participants get() = listOf(alice, bob)
}