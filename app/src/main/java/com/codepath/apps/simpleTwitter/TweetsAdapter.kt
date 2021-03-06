package com.codepath.apps.simpleTwitter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.simpleTwitter.models.Tweet

class TweetsAdapter(val tweets: ArrayList<Tweet>):RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsAdapter.ViewHolder {
        val context = parent.context
        val inflate = LayoutInflater.from(context)

        //Inflate our item layout
        val view= inflate.inflate(R.layout.item_tweet, parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {
        val tweet = tweets.get(position)
        bind(holder, tweet)
    }

    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }
    private fun bind(holder: TweetsAdapter.ViewHolder, tweet: Tweet) {
        holder.tvUsername.text= tweet.user?.name
        holder.tvTweetBody.text= tweet.body
        holder.tvTimeStamp.text= tweet.timeStamp
        Log.i("TweetsAdapter", tweet.timeStamp)
        Glide.with(holder.itemView).load(tweet.user?.publicImageUrl).into(holder.ivProfileImage)
    }


    override fun getItemCount(): Int {
        return tweets.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ivProfileImage= itemView.findViewById<ImageView>(R.id.ivProfileImage)
        val tvUsername= itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweetBody= itemView.findViewById<TextView>(R.id.tvTweetBody)
        val tvTimeStamp = itemView.findViewById<TextView>(R.id.tvTimeStamp)
    }
}