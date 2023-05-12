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

package me.ahoo.wow.spring.boot.starter.metrics

import me.ahoo.wow.metrics.Metrics.metrizable
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.Ordered

class MetricsBeanPostProcessor : BeanPostProcessor, Ordered {
    companion object {
        private val log = LoggerFactory.getLogger(MetricsBeanPostProcessor::class.java)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        val metrizableBean = bean.metrizable()
        if (metrizableBean !== bean && log.isInfoEnabled) {
            log.info("Metrizable bean [{}] [{}] -> [{}]", beanName, bean.javaClass.name, metrizableBean.javaClass.name)
        }
        return metrizableBean
    }

    override fun getOrder(): Int {
        return Ordered.LOWEST_PRECEDENCE
    }
}
