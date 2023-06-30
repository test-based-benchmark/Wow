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

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.1.1"))
    api(platform("me.ahoo.cosid:cosid-bom:2.2.1"))
    api(platform("io.opentelemetry:opentelemetry-bom:1.27.0"))
    api(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:1.27.0"))
    api(platform("io.r2dbc:r2dbc-bom:Borca-RELEASE"))
    api(platform("org.testcontainers:testcontainers-bom:1.18.3"))
    constraints {
        api("com.google.devtools.ksp:symbol-processing-api:1.8.22-1.0.11")
        api("com.google.devtools.ksp:symbol-processing:1.8.22-1.0.11")
        api("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
        api("com.google.guava:guava:32.1.1-jre")
        api("io.opentelemetry:opentelemetry-semconv:1.22.0-alpha")
        api("org.springdoc:springdoc-openapi-starter-webflux-api:2.1.0")
        api("org.springdoc:springdoc-openapi-starter-webflux-ui:2.1.0")
        api("org.hamcrest:hamcrest:2.2")
        api("io.mockk:mockk:1.13.5")
        api("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
    }
}
