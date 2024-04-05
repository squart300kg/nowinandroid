/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.testing.data.newsResourcesTestData
import com.google.samples.apps.nowinandroid.core.testing.data.topicsTestData
import com.google.samples.apps.nowinandroid.core.testing.repository.TestSearchContentsRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit test for [GetSearchContentsCountUseCase]
 */
class GetSearchContentsCountUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchContentsRepository = TestSearchContentsRepository()

    private val useCase = GetSearchContentsCountUseCase(
        searchContentsRepository,
    )

    @Test
    fun whenTopicsAreAdded_searchContentCountIncreasesByTheTopics() = runTest {
        // Obtain a stream of search count.
        val searchContentsCount = useCase()

        // Check that initial search count is zero.
        assertEquals(
            searchContentsCount.first(),
            0,
        )

        // add topics.
        searchContentsRepository.addTopics(topicsTestData)

        // Check whether search count is increased by the newly added topic counts.
        assertEquals(
            searchContentsCount.first(),
            topicsTestData.size,
        )
    }

    @Test
    fun whenNewsResourcesAreAdded_searchContentCountIncreasesByTheNewsResources() = runTest {
        // Obtain a stream of search count.
        val searchContentsCount = useCase()

        // Check that initial search count is zero.
        assertEquals(
            searchContentsCount.first(),
            0,
        )

        // add topics.
        searchContentsRepository.addNewsResources(newsResourcesTestData)

        // Check whether search count is increased by the newly news resource counts.
        assertEquals(
            searchContentsCount.first(),
            newsResourcesTestData.size,
        )
    }
}
