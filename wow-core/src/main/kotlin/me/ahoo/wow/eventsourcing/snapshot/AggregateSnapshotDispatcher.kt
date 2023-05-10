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

package me.ahoo.wow.eventsourcing.snapshot

import me.ahoo.wow.api.modeling.NamedAggregate
import me.ahoo.wow.event.EventStreamExchange
import me.ahoo.wow.messaging.dispatcher.AggregateGroupKey
import me.ahoo.wow.messaging.dispatcher.AggregateGroupKey.Companion.asGroupKey
import me.ahoo.wow.messaging.dispatcher.AggregateMessageDispatcher
import me.ahoo.wow.messaging.dispatcher.MessageParallelism
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler

class AggregateSnapshotDispatcher(
    private val snapshotHandler: SnapshotHandler,
    override val namedAggregate: NamedAggregate,
    override val name: String =
        "${namedAggregate.aggregateName}-${AggregateSnapshotDispatcher::class.simpleName!!}",
    override val parallelism: MessageParallelism = MessageParallelism.DEFAULT,
    override val scheduler: Scheduler,
    override val messageFlux: Flux<EventStreamExchange>,
) : AggregateMessageDispatcher<EventStreamExchange>() {

    override fun handleExchange(exchange: EventStreamExchange): Mono<Void> {
        return snapshotHandler.handle(exchange)
    }

    override fun EventStreamExchange.asGroupKey(): AggregateGroupKey {
        return message.asGroupKey(parallelism.group)
    }
}
