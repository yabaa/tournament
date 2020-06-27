package com.yabaa.tournament.container

import org.junit.Assert.assertTrue
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.testcontainers.containers.MongoDBContainer

class TestcontainersTests {
    companion object {
        private val instance: MongoDBContainer = MongoDBContainer()
            .withExposedPorts(27017)

        @BeforeClass
        @JvmStatic
        internal fun beforeAll() {
            instance.start()
        }

        @AfterClass
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }
    }

    @Test
    fun test() {
        assertTrue(instance.isRunning)
    }

}
