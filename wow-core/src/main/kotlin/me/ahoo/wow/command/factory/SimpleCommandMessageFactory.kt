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

package me.ahoo.wow.command.factory

import jakarta.validation.Validator
import me.ahoo.wow.api.command.CommandMessage
import me.ahoo.wow.api.command.validation.CommandValidator
import me.ahoo.wow.command.factory.CommandValidationException.Companion.toCommandValidationException
import me.ahoo.wow.command.toCommandMessage
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class SimpleCommandMessageFactory(
    private val validator: Validator,
    private val commandOptionsExtractorRegistry: CommandOptionsExtractorRegistry
) : CommandMessageFactory {

    @Suppress("TooGenericExceptionCaught")
    override fun <C : Any> create(body: C, options: CommandOptions): Mono<CommandMessage<C>> {
        if (body is CommandValidator) {
            try {
                body.validate()
            } catch (error: Throwable) {
                return error.toMono()
            }
        }
        val constraintViolations = validator.validate(body)
        if (constraintViolations.isNotEmpty()) {
            return constraintViolations.toCommandValidationException(body).toMono()
        }

        val extractor = commandOptionsExtractorRegistry.getExtractor(body.javaClass)
            ?: return body.toCommandMessage(options).toMono()
        return extractor.extract(body, options)
            .map {
                body.toCommandMessage(it)
            }
    }
}
