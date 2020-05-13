package com.example.coroutinetutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            CoroutineScope(IO).launch {

                fakeApiRequest()
            }

        }

    }

    private fun setNewText(input: String) {
        val newText = textView.text.toString() + "\n$input"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(message: String) {
        withContext(Main) {

            setNewText(message)
        }
    }


    /*suspend fun fakeApiRequest() {

        val result1 = getresult_1_FromApi()
        setTextOnMainThread(result1)

        val result2 = getresult_2_FromApi()
        setTextOnMainThread(result2)


    }*/

    private suspend fun fakeApiRequest() {

        logThread("fakeApiRequest")

        val result1 = getresult_1_FromApi() // wait until job is done

        if (result1 == "Result #1") {

            setTextOnMainThread("Got $result1")

            val result2 = getresult_2_FromApi() // wait until job is done

            if (result2 == "Result #2") {
                setTextOnMainThread("Got $result2")
            } else {
                setTextOnMainThread("Couldn't get Result #2")
            }
        } else {
            setTextOnMainThread("Couldn't get Result #1")
        }
    }

    suspend fun getresult_1_FromApi(): String {
        logThread("getresult_1_FromApi")
        delay(1000)
        return "Result #1"
    }

    suspend fun getresult_2_FromApi(): String {
        logThread("getresult_2_FromApi")
        delay(1000)
        return "Result #2"
    }


    /*fun showToast(message: String) {

        GlobalScope.launch(Main) {

            Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
        }

    }*/

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}
