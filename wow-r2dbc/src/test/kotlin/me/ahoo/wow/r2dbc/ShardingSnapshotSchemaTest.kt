/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License,Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.wow.r2dbc

import me.ahoo.cosid.sharding.ModCycle
import me.ahoo.wow.modeling.MaterializedNamedAggregate
import me.ahoo.wow.modeling.asAggregateId
import me.ahoo.wow.sharding.CosIdAggregateIdSharding
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ShardingSnapshotSchemaTest {

    private val namedAggregate = MaterializedNamedAggregate("test", "ShardingEventStreamSchemaTest")
    private val snapshotSharding =
        CosIdAggregateIdSharding(
            mapOf(namedAggregate to ModCycle(4, "test_snapshot_")),
        )
    private val snapshotSchema = ShardingSnapshotSchema(snapshotSharding)

    @Test
    fun load() {
        assertThat(
            snapshotSchema.load(namedAggregate.asAggregateId("0TEC7cEx0001001")),
            equalTo("select * from test_snapshot_1 where aggregate_id=? order by version desc limit 1"),
        )
    }

    @Test
    fun loadVersion() {
        assertThat(
            snapshotSchema.loadByVersion(namedAggregate.asAggregateId("0TEC7cEx0001002")),
            equalTo("select * from test_snapshot_2 where aggregate_id=? and version=?"),
        )
    }

    @Test
    fun save() {
        assertThat(
            snapshotSchema.save(namedAggregate.asAggregateId("0TEC7cEx0001003")),
            equalTo(
                """
     replace into test_snapshot_3
     (aggregate_id,tenant_id,version,state_type,state,event_id,first_event_time,event_time,snapshot_time,deleted)
     values 
     (?,?,?,?,?,?,?,?,?,?)
     """.trim(),
            ),
        )
    }
}
