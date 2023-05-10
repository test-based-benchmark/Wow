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

package me.ahoo.wow.infra.prepare

import me.ahoo.wow.api.naming.Named
import me.ahoo.wow.infra.prepare.PreparedValue.Companion.asForever
import reactor.core.publisher.Mono

interface PrepareKey<V : Any> : Named {

    fun prepare(key: String, value: V): Mono<Boolean> {
        return prepare(key, value.asForever())
    }

    fun prepare(key: String, value: PreparedValue<V>): Mono<Boolean>

    fun get(key: String): Mono<V> {
        return getValue(key)
            .filter { !it.isExpired }
            .map { it.value }
    }

    fun getValue(key: String): Mono<PreparedValue<V>>

    fun rollback(key: String): Mono<Boolean>

    /**
     * Rollback only if both key and value match
     */
    fun rollback(key: String, value: V): Mono<Boolean>

    fun reprepare(key: String, oldValue: V, newValue: V): Mono<Boolean> {
        return reprepare(key, oldValue, newValue.asForever())
    }

    fun reprepare(key: String, oldValue: V, newValue: PreparedValue<V>): Mono<Boolean>

    fun reprepare(key: String, value: V): Mono<Boolean> {
        return reprepare(key, value.asForever())
    }

    fun reprepare(key: String, value: PreparedValue<V>): Mono<Boolean>

    fun <R> usingPrepare(key: String, value: V, block: (Boolean) -> Mono<R>): Mono<R> {
        return usingPrepare(key, value.asForever(), block)
    }

    fun <R> usingPrepare(key: String, value: PreparedValue<V>, block: (Boolean) -> Mono<R>): Mono<R> {
        return prepare(key, value)
            .flatMap { prepared ->
                block(prepared).onErrorResume {
                    val errorMono = Mono.error<R>(it)
                    if (!prepared) {
                        return@onErrorResume errorMono
                    }
                    rollback(key, value.value)
                        .then(errorMono)
                }
            }
    }
}
