package com.example.coroutinetutorial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

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

    /**
     * Comparison between async/await and job/launch patterns.
     * Major difference is async/await can return a value wrapped in a Deferred type.
     */

    private suspend fun fakeApiRequest() {

        withContext(IO) {

            val executionTime = measureTimeMillis {

                /*//Classic job/launch
                var result1 = ""
                val job1= launch {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    result1 = getResult_1_from_Api()
                }
                job1.join()

               var result2 = ""
               val job2 = launch {

                   println("debug: launchinf job2: ${Thread.currentThread().name}")
                   result2 = getResult_2_from_Api(result1)
               }*/

                //Using async/await   (preferred type)

                val result1 = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")

                    getResult_1_from_Api()
                }.await()  //wait for the result 1

                println("Got result1: $result1")

                val result2 = async {

                    println("debug: launching job1: ${Thread.currentThread().name}")

                    getResult_2_from_Api(result1)
                }.await()
                println("Got result2: $result2")

            }
            println("job1 and job2 are complete. It took $executionTime ms")
            setTextOnMainThread("job1 and job2 are complete. It took $executionTime ms")
        }
    }

    private fun setNewText(input: String) {

        val newText = textView.text.toString() + "\n$input"
        textView.text = newText
    }


    private suspend fun setTextOnMainThread(input: String) {

        withContext(Main) {
            setNewText(input)
        }
    }

    /*private fun isJobRunning(isActive: Boolean) {

        if (isActive) {
            showToast("Job is Active")
        } else {
            showToast("Job is not Active")
        }

    }*/

    private suspend fun getResult_1_from_Api(): String {

        delay(1000)
        return "Result #1"

    }

    private suspend fun getResult_2_from_Api(result1: String): String {

        delay(1700)
        if (result1 == "Result #1") {
            return "Result #2"

        }
        return "Result #1 was incorrect"
    }


    private fun showToast(msg: String) {
        GlobalScope.launch(Main) {

            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
        }
    }

}
