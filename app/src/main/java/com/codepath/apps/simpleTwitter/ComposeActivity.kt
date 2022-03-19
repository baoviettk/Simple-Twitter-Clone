package com.codepath.apps.simpleTwitter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.red
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.simpleTwitter.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
const val COMPOSE_TAG:String= "ComposeActivity"

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharacterCount:TextView
    lateinit var client: TwitterClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        etCompose= findViewById(R.id.etTweetCompose)
        btnTweet= findViewById(R.id.btnTweet)
        tvCharacterCount=findViewById(R.id.tvCharacterCount)
        client = TwitterApplication.getRestClient(this)
        btnTweet.setOnClickListener{
            val newTweet = etCompose.text.toString()

            if(newTweet.isEmpty()){
                Toast.makeText(this,"Empty text",Toast.LENGTH_SHORT).show()
            }
            else if (newTweet.length>200){
                Toast.makeText(this, "Tweet is too long",Toast.LENGTH_SHORT).show()
            }
            else{
                client.publishTweet(newTweet, object : JsonHttpResponseHandler() {
                    override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
                    ) {
                        Log.e(COMPOSE_TAG, "Fail with status code $statusCode and ", throwable)

                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        etCompose.text.clear()
                        val tweet = Tweet.fromJson(json.jsonObject)
                        val intent= Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                })

            }
        }
        etCompose.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(COMPOSE_TAG, "beforeTextChanged")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i(COMPOSE_TAG, "onTextChanged")

            }

            override fun afterTextChanged(p0: Editable) {
                val characterCounter = p0.length.toString()
                val originalColor = R.color.material_on_surface_stroke
                tvCharacterCount.setText("$characterCounter/280")
                if(p0.length>10){
                    btnTweet.isEnabled = false
                    tvCharacterCount.setTextColor(Color.parseColor("#B00020"))
                }
                else{
                    btnTweet.isEnabled = true
                    tvCharacterCount.setTextColor(Color.parseColor("#000000"))
                }
//                tvCharacterCount.text = p0.toString()
            }

        })
    }
}