package com.codepath.apps.simpleTwitter.models

import android.os.Parcelable
import com.codepath.apps.simpleTwitter.TimeFormatter
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
class Tweet(
    var body: String = "",
    var createdAt: String = "",
    var timeStamp: String = "",
    var user: User? = null
) : Parcelable {
    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            tweet.timeStamp = getFormattedTimestamp(tweet.createdAt)
            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }

        fun getFormattedTimestamp(string: String): String {
            return TimeFormatter.getTimeDifference(string)
        }
    }
}