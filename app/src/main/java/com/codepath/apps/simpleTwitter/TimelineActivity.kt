package com.codepath.apps.simpleTwitter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.simpleTwitter.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.codepath.recyclerviewlab.EndlessRecyclerViewScrollListener
import okhttp3.Headers
import org.json.JSONException

const val TAG= "TimelineActivity"
const val REQUEST_CODE = 16
class TimelineActivity : AppCompatActivity() {
    lateinit var client: TwitterClient
    lateinit var rvTweet: RecyclerView
    lateinit var tweetsAdapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var scrollListener:EndlessRecyclerViewScrollListener
    private lateinit var progressSpinner: ContentLoadingProgressBar

    var lastTweetId: Long=0
    var tweets= ArrayList<Tweet>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client= TwitterApplication.getRestClient(this)

        rvTweet=findViewById(R.id.rvTweet)
        swipeContainer = findViewById(R.id.swipeContainer)
        progressSpinner =findViewById(R.id.progress)

        swipeContainer.setOnRefreshListener{
            Log.i(TAG, "Refreshing the screen")
            polulateTimeline()
        }

        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

        tweetsAdapter = TweetsAdapter(tweets)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        polulateTimeline()
        scrollListener =object: EndlessRecyclerViewScrollListener(layoutManager){
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                loadNextDataFromApi(lastTweetId)
            }
        }
        scrollListener.resetState()
        rvTweet.layoutManager =layoutManager
        rvTweet.adapter=tweetsAdapter
        rvTweet.addItemDecoration(
            DividerItemDecoration(
                baseContext,
                layoutManager.orientation
            )
        )
        rvTweet.addOnScrollListener(scrollListener)



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.compose){
            intent= Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            val tweet= data?.getParcelableExtra<Tweet>("tweet")
            if (tweet != null) {
                tweets.add(0,tweet)
                tweetsAdapter.notifyItemInserted(0)
                rvTweet.smoothScrollToPosition(0)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadNextDataFromApi(lastTweetId: Long) {
            client.getNextPageOfTweets(object :JsonHttpResponseHandler(){
                override fun onFailure(
                    statusCode: Int,
                    headers: Headers?,
                    response: String?,
                    throwable: Throwable?
                ) {
                    Log.e(TAG, "loadNextDataFromApi failed with code $statusCode")
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                    val jsonArray = json.jsonArray
                    try {
                        Log.i(TAG, "loadNextDataFromApi success")
                        val newTweets = Tweet.fromJsonArray(jsonArray)
                        tweets.addAll(newTweets)
                        tweetsAdapter.notifyDataSetChanged()
                        val lastTweetJson = jsonArray.getJSONObject(jsonArray.length())
                        this@TimelineActivity.lastTweetId = lastTweetJson.getLong("id")


                    } catch (e:JSONException){
                        Log.e(TAG, "JSON Exception $e")
                    }
                }
            }, lastTweetId)
    }

    fun polulateTimeline(){
        client.getHomeTimeline(object :JsonHttpResponseHandler(){
            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                Log.e(TAG, "StatusCode is $statusCode")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                val jsonArray = json.jsonArray
                try {
                    tweetsAdapter.clear()
                    val newTweets = Tweet.fromJsonArray(jsonArray)
                    this@TimelineActivity.tweets.addAll(newTweets)
                    tweetsAdapter.addAll(tweets)
                    tweetsAdapter.notifyDataSetChanged()
                    swipeContainer.setRefreshing(false)
                    val lastTweetJson = jsonArray.getJSONObject(jsonArray.length() - 1)
                    lastTweetId= lastTweetJson.getLong("id")
                } catch (e:JSONException){
                    Log.e(TAG, "JSON Exception $e")
                }
            }

        })
    }
}