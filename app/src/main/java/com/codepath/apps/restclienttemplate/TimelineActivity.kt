package com.codepath.apps.restclienttemplate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

const val TAG= "TimelineActivity"
class TimelineActivity : AppCompatActivity() {
    lateinit var client: TwitterClient
    lateinit var rvTweet: RecyclerView
    lateinit var tweetsAdapter: TweetsAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    var tweets= ArrayList<Tweet>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client= TwitterApplication.getRestClient(this)

        rvTweet=findViewById(R.id.rvTweet)
        swipeContainer = findViewById(R.id.swipeContainer)
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
        rvTweet.layoutManager = LinearLayoutManager(this)
        rvTweet.adapter=tweetsAdapter

        polulateTimeline()

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
                    tweets.addAll(newTweets)
                    tweetsAdapter.addAll(tweets)
                    tweetsAdapter.notifyDataSetChanged()
                    swipeContainer.setRefreshing(false)
                } catch (e:JSONException){
                    Log.e(TAG, "JSON Exception $e")
                }
            }

        })
    }
}