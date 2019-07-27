package com.corda.samples.cordasample.flow

import co.paralleluniverse.fibers.Suspendable
import com.corda.samples.cordasample.contract.EntityContract
import com.corda.samples.cordasample.contract.TransactionContract
import com.corda.samples.cordasample.model.EntityModel
import com.corda.samples.cordasample.model.TransactionModel
import com.corda.samples.cordasample.schema.EntitySchemaV1
import com.corda.samples.cordasample.schema.TransactionSchemaV1
import com.corda.samples.cordasample.state.EntityState
import com.corda.samples.cordasample.state.TransactionState
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.Builder.equal
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import java.time.Instant

/*
*
* Executando o flow na linha de comando
* flow start CreateTransactionFlow name: "Joao", document: "123.123.123-12"
*
* Consultando os estados criados:
* run vaultQuery contractStateType: com.corda.samples.cordasample.state.TransactionState
*
* */

object CreateTransaction {

    @InitiatingFlow
    @StartableByRPC
    class CreateTransactionFlow(
        val origin: String,
        val destiny: String,
        val amount: Double
    ): FlowLogic<SignedTransaction>() {

        companion object {

            object INITIALISING : ProgressTracker.Step("Inicializando")
            object BUILDING : ProgressTracker.Step("Construindo")
            object SIGNING : ProgressTracker.Step("Assinando")
            object PART_SIGNING : ProgressTracker.Step("Obtendo assinatura das partes") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING : ProgressTracker.Step("Finalizando") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                INITIALISING,
                BUILDING,
                SIGNING,
                PART_SIGNING,
                FINALISING
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): SignedTransaction {

            progressTracker.currentStep = INITIALISING

            // localizando a pessoa
            val indexOrigin = EntitySchemaV1.PersistentEntity::document.equal(origin)
            val indexDestiny = EntitySchemaV1.PersistentEntity::document.equal(destiny)

            val criteriaOrigin = QueryCriteria.VaultCustomQueryCriteria(expression = indexOrigin)
            val criteriaDestiny = QueryCriteria.VaultCustomQueryCriteria(expression = indexDestiny)

            val originList = serviceHub.vaultService.queryBy<EntityState>(criteriaOrigin).states
            val destinyList = serviceHub.vaultService.queryBy<EntityState>(criteriaDestiny).states

            var originStateRef = originList.single()
            var destinyStateRef = destinyList.single()

            if (originList.size != 1) {
                throw Exception("Origem deve ser apenas um")
            }
            if (originList.size != 1) {
                throw Exception("Destino deve ser apenas um")
            }

            var originNewAmount = originStateRef.state.data.entity.amount - amount;
            var destinyNewAmount = destinyStateRef.state.data.entity.amount + amount;

            // criando um modelo de pessoa
            val transaction = TransactionModel(Instant.now(), originStateRef.state.data.entity, destinyStateRef.state.data.entity, amount)
            val originModel = EntityModel(ourIdentity, Instant.now(), originStateRef.state.data.entity.name, originStateRef.state.data.entity.document, originNewAmount)
            val destinyModel = EntityModel(ourIdentity, Instant.now(), destinyStateRef.state.data.entity.name, destinyStateRef.state.data.entity.document, destinyNewAmount)

            // criando um estado de pessoa
            val transactionState = TransactionState(transaction)
            val originState = EntityState(originModel)
            val destinyState = EntityState(destinyModel)

            // criando o command para validação do contract
            val txCommand = Command(
                TransactionContract.Commands.CreateTransaction(),
                transactionState.participants.map { it.owningKey }
            )

            val txOriginUpdateCommand = Command(
                EntityContract.Commands.UpdateEntity(),
                originState.participants.map { it.owningKey }
            )

            progressTracker.currentStep = BUILDING

            // criando a transação e validando

            val notary = serviceHub.networkMapCache.notaryIdentities.firstOrNull() ?: throw FlowException("Notary not found")

            val txBuilder = TransactionBuilder(notary)
                .addCommand(txCommand)
                .addCommand(txOriginUpdateCommand)
                .addInputState(originStateRef)
                .addInputState(destinyStateRef)
                .addOutputState(transactionState, TransactionContract::class.java.canonicalName)
                .addOutputState(originState, EntityContract::class.java.canonicalName)
                .addOutputState(destinyState, EntityContract::class.java.canonicalName)

            txBuilder.verify(serviceHub)

            /*
            * Exemplo de comunicação com outro nó
            *
            * ### criando uma sessao com a outra parte - nodeB é do tipo Party
            * val sessionParty = initiateFlow(nodeB)
            *
            * ### Enviando um texto e recebendo um texto, o texto recebido estará em doc
            * val packNameTo: UntrustworthyData<String> = sessionTo.sendAndReceive<String>("documento")
            * val doc: String = packNameTo.unwrap { data -> data }
            *
            * */

            progressTracker.currentStep = SIGNING

            // assinando a transação
            val partSignedTx = serviceHub.signInitialTransaction(txBuilder, ourIdentity.owningKey)

            progressTracker.currentStep = PART_SIGNING

            /*
            *
            * ### abaixo "initiateFlow(nodeB" pode ser trocado por sessionParty (criado um pouco acima no código)
            * val session = listOf(nodeB).toSet().map { initiateFlow(nodeB) }
            * val signedTx = subFlow(CollectSignaturesFlow(partSignedTx, session, PART_SIGNING.childProgressTracker()))
            *
            * ### no retorno desse método troca o parâmetro partSignedTx por signedTx (que estará assinado por todos)
            *
            * */

            progressTracker.currentStep = FINALISING

            // finalizando
            return subFlow(
                FinalityFlow(partSignedTx,
                    FINALISING.childProgressTracker()
                )
            )
        }


    }


    /*
    * Quando precisa da assinatura de um outro nó, ou qualquer outra comunicação,
    * esse será o fluxo executado pela outra parte
    * */
    @InitiatedBy(CreateTransactionFlow::class)
    class PartyFlow(val partyFlow: FlowSession) : FlowLogic<SignedTransaction>() {

        companion object {

            object INITIALISING : ProgressTracker.Step("Inicializando destino")
            object BUILDING : ProgressTracker.Step("Construindo destino")
            object SIGNING : ProgressTracker.Step("Assinando destino")
            object PART_SIGNING : ProgressTracker.Step("Obtendo assinatura das partes origem") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING : ProgressTracker.Step("Finalizando destino") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                INITIALISING,
                BUILDING,
                SIGNING,
                PART_SIGNING,
                FINALISING
            )
        }

        override val progressTracker = tracker()

        @Suspendable
        override fun call(): SignedTransaction {


            /*
            * ################################################################################################
            * FLUXO 1 - Obtém e responde um texto
            * ################################################################################################
            * */

            // recebe um texto
            val doc: String = partyFlow.receive<String>().unwrap { data -> data }

            // responde com um texto
            partyFlow.send("document")

            /*
            * ################################################################################################
            * FLUXO 2 - Assina a transação
            * ################################################################################################
            * */

            // assina a transação principal
            val signTransactionFlow = object : SignTransactionFlow(partyFlow) {

                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    "Documento inválido" using (doc.isNotEmpty())
                }
            }

            // responde com a assinatura
            return subFlow(signTransactionFlow)


        }
    }


}