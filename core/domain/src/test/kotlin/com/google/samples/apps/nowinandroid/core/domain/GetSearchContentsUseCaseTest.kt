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
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.emptyUserData
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit test for [GetSearchContentsUseCase]
 */
class GetSearchContentsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchContentsRepository = TestSearchContentsRepository()
    private val userDataRepository = TestUserDataRepository()

    private val useCase = GetSearchContentsUseCase(
        searchContentsRepository,
        userDataRepository,
    )

    @Test
    fun whenReceivingSearchContents_allSearchContentsContainSearchQuery() = runTest {
        // Obtain a stream of userSearchResult with search query.
        val searchQuery = "Compose"
        val userSearchResult = useCase(searchQuery)

        // Set user data and search contents.
        userDataRepository.setUserData(emptyUserData)
        searchContentsRepository.addTopics(topicsTestData)
        searchContentsRepository.addNewsResources(newsResourcesTestData)

        // Receive user search result containing topics and news resources
        userSearchResult.first().run {
            // Check that filtered topics all have the search query.
            assertEquals(
                topics,
                topics.filter { searchQuery in it.topic.name || searchQuery in it.topic.shortDescription || searchQuery in it.topic.longDescription },
            )
            // Check that filtered news resources all have the search query.
            assertEquals(
                newsResources,
                newsResources.filter { searchQuery in it.content || searchQuery in it.title },
            )
        }
    }

    @Test
    fun whenReceivingSearchContents_allSearchContentsReflectUserFollowStatus() = runTest {
        // Obtain a stream of userSearchResult with search query.
        val searchQuery = "Compose"
        val userSearchResult = useCase(searchQuery)

        // Set user data with follow status and search contents.
        userDataRepository.setUserData(
            emptyUserData.copy(
                bookmarkedNewsResources = setOf(newsResourcesTestData[0].id),
                viewedNewsResources = setOf(newsResourcesTestData[0].id),
                followedTopics = setOf(topicsTestData[1].id),
            ),
        )
        searchContentsRepository.addTopics(topicsTestData)
        searchContentsRepository.addNewsResources(newsResourcesTestData)

        userSearchResult.first().run {
            // Check that search contents are reflected with user follow status data.
            assertTrue(
                newsResources.first().isSaved &&
                    newsResources.first().hasBeenViewed &&
                    topics.first().isFollowed,
            )
        }
    }
}
