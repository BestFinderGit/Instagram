package com.hashim.instagram.data.repository

import com.hashim.instagram.data.model.User
import com.hashim.instagram.data.remote.NetworkService
import com.hashim.instagram.data.remote.Networking
import com.hashim.instagram.data.remote.response.PostListResponse
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostRepositoryTest {

    @Mock
    lateinit var networkService: NetworkService

    lateinit var postRepository: PostRepository

    @Before
    fun setUp(){
        Networking.API_KEY = "FAKE API"
        postRepository = PostRepository(networkService)
    }

    @Test
    fun fetchHomePostList_requestDoHomePostList(){

        val user = User("id","name","email","accessToken","profilePicUrl")

        doReturn(Single.just(PostListResponse("statusCode","message", listOf())))
            .`when`(networkService)
            .doHomePostListCall(
                "firstId",
                "lastId",
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

            postRepository.fetchHomePostList("firstId","lastId",user)

            verify(networkService).doHomePostListCall(
                "firstId",
                "lastId",
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

    }



}