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

import jakarta.validation.ConstraintViolation
import me.ahoo.wow.api.exception.BindingError
import me.ahoo.wow.api.exception.ErrorInfo
import me.ahoo.wow.exception.ErrorCodes.COMMAND_VALIDATION
import me.ahoo.wow.exception.WowException

class CommandValidationException(
    val command: Any,
    errorMsg: String = "Command validation failed.",
    cause: Throwable? = null,
    bindingErrors: List<BindingError> = emptyList(),
) : WowException(
    errorCode = COMMAND_VALIDATION,
    errorMsg = errorMsg,
    cause = cause,
    bindingErrors = bindingErrors
),
    ErrorInfo {

    companion object {

        internal fun Set<ConstraintViolation<*>>.toBindingErrors(): List<BindingError> {
            return this.map {
                BindingError(it.propertyPath.toString(), it.message)
            }
        }

        fun <T> Set<ConstraintViolation<T>>.toCommandValidationException(
            command: Any,
            errorMsg: String = "Command validation failed."
        ): CommandValidationException {
            return CommandValidationException(command, errorMsg = errorMsg, bindingErrors = toBindingErrors())
        }
    }
}
