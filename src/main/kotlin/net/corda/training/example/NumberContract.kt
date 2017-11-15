package net.corda.training.example

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class NumberContract : Contract {
    // contract id was omitted...
    interface Commands : CommandData {
        class Create : TypeOnlyCommandData(), Commands
        class Add : TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<NumberContract.Commands>()
        when (command.value) {
            is Commands.Create -> {
                requireThat {
                    "There are no inputs" using (tx.inputs.isEmpty())
                    "There is only one output" using (tx.outputs.size == 1)

                    val out = tx.outputsOfType<NumberState>().single()
                    "Number must be positive" using (out.number > 0)
                    "The participants are distinct" using (out.alice != out.bob)

                    val participantKeys = out.participants.map { it.owningKey }
                    "All participants must be signers" using
                            (command.signers.containsAll(participantKeys))
                }
            }
            is Commands.Add -> {
                requireThat {
                    "There is only one input" using (tx.inputs.size == 1)
                    "There is only one output" using (tx.outputs.size == 1)

                    val input = tx.inputsOfType<NumberState>().single()
                    val out = tx.outputsOfType<NumberState>().single()
                    "Amount added is >0" using (input.number < out.number)
                    "The participants are distinct" using (out.alice != out.bob)

                    val participantKeys = out.participants.map { it.owningKey }
                    "All participants must be signers" using
                            (command.signers.containsAll(participantKeys))
                }
            }
            else ->
                throw IllegalArgumentException("Unknown command $command")
        }
    }
}