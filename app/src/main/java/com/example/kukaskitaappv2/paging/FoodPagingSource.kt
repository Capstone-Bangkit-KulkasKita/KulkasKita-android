package com.example.kukaskitaappv2.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.source.remote.response.FoodItem
import com.example.kukaskitaappv2.source.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first

//class FoodPagingSource(private val preferences: UserPreferences, private val apiService: ApiService) : PagingSource<Int, FoodItem>() {
//
//    override fun getRefreshKey(state: PagingState<Int, FoodItem>): Int? {
//        return state.anchorPosition?.let {
//            val anchorPage = state.closestPageToPosition(it)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FoodItem> {
//        return try {
//            val position = params.key ?: INITIAL_PAGE_INDEX
//            val token = preferences.getToken().first()
//
//            if (token.isNotEmpty()) {
//                val responseData = apiService.getStories("Bearer $token", position, params.loadSize)
//                if (responseData.isSuccessful) {
//                    Log.d("Story Paging Source", "Load: ${responseData.body()}")
//                    LoadResult.Page(
//                        data = responseData.body()?.listStory ?: emptyList(),
//                        prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
//                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
//                    )
//                } else {
//                    Log.d("Token", "Load Error: $token")
//                    LoadResult.Error(Exception("Failed"))
//                }
//            } else {
//                LoadResult.Error(Exception("Failed"))
//            }
//        } catch (e: Exception) {
//            Log.d("Exception", "Load Error: ${e.message}")
//            return LoadResult.Error(e)
//        }
//    }
//
//    private companion object {
//        const val INITIAL_PAGE_INDEX = 1
//    }
//}