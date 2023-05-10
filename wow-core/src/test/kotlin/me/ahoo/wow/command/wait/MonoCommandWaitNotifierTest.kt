/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.wow.command.wait

import me.ahoo.wow.api.command.CommandMessage
import me.ahoo.wow.command.MockCreateCommand
import me.ahoo.wow.command.SimpleServerCommandExchange
import me.ahoo.wow.command.asCommandMessage
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

internal class MonoCommandWaitNotifierTest {

    @Test
    fun notifyAndForgetWrapNotInjectWaitStrategy() {
        val commandWaitNotifier = LocalCommandWaitNotifier(SimpleWaitStrategyRegistrar)
        Mono.empty<Void>()
            .thenNotifyAndForget(
                commandWaitNotifier,
                CommandStage.SENT,
                SimpleServerCommandExchange(MockCreateCommand("").asCommandMessage())
            )
            .test()
            .verifyComplete()
    }

    @Test
    fun notifyAndForgetWrap() {
        val command = MockCreateCommand("").asCommandMessage()
            .injectWaitStrategy<CommandMessage<MockCreateCommand>>("", CommandStage.PROCESSED)
        val commandWaitNotifier = LocalCommandWaitNotifier(SimpleWaitStrategyRegistrar)
        Mono.empty<Void>()
            .thenNotifyAndForget(commandWaitNotifier, CommandStage.SENT, SimpleServerCommandExchange(command))
            .test()
            .verifyComplete()
    }

    @Test
    fun notifyAndForgetWrapError() {
        val command = MockCreateCommand("").asCommandMessage()
            .injectWaitStrategy<CommandMessage<MockCreateCommand>>("", CommandStage.PROCESSED)
        val commandWaitNotifier = LocalCommandWaitNotifier(SimpleWaitStrategyRegistrar)
        RuntimeException("error")
            .toMono<Void>()
            .thenNotifyAndForget(commandWaitNotifier, CommandStage.SENT, SimpleServerCommandExchange(command))
            .test()
            .verifyError(RuntimeException::class.java)
    }

    @Test
    fun notifyAndForgetWrapAndStageIsEarly() {
        val command = MockCreateCommand("").asCommandMessage()
            .injectWaitStrategy<CommandMessage<MockCreateCommand>>("", CommandStage.SENT)
        val commandWaitNotifier = LocalCommandWaitNotifier(SimpleWaitStrategyRegistrar)
        Mono.empty<Void>()
            .thenNotifyAndForget(commandWaitNotifier, CommandStage.PROCESSED, SimpleServerCommandExchange(command))
            .test()
            .verifyComplete()
    }
}
