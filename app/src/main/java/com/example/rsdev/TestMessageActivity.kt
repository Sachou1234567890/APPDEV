
package com.example.rsdev

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rsdev.data.Message
import com.example.rsdev.SentMessageAdapter

class TestMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_message)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val messages = listOf(
            Message("Hello", "12:00 PM", "John"),
            Message("How are you?", "12:30 PM", "Jane"),
            Message("I'm good, thanks!", "1:00 PM", "John"),
            Message("Let's meet up later?", "2:00 PM", "Jane"),
            Message("Sure, what time?", "2:30 PM", "John"),
            Message("How about 6 PM?", "3:00 PM", "Jane"),
            Message("Sounds good to me!", "3:30 PM", "John"),
            Message("See you then!", "4:00 PM", "Jane"),
            Message("Great meeting you!", "5:00 PM", "John"),
            Message("Likewise!", "5:30 PM", "Jane"),
            Message("Don't forget our meeting tomorrow", "6:00 PM", "John"),
            Message("I won't, thanks for the reminder!", "6:30 PM", "Jane"),
            Message("Can you send me that report?", "7:00 PM", "John"),
            Message("Sure, I'll send it to you now", "7:30 PM", "Jane"),
            Message("Thanks, I received it", "8:00 PM", "John"),
            Message("You're welcome!", "8:30 PM", "Jane"),
            Message("Goodnight!", "9:00 PM", "John"),
            Message("Sweet dreams!", "9:30 PM", "Jane")
        )


        // Define the layout manager
        val layoutManager = LinearLayoutManager(this)

        // Define the adapter
        val adapter = SentMessageAdapter(messages)

        // Attach the adapter and layout manager to the RecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}
