package com.example.coroutinetutorial

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private companion object {

        const val JOB_TIMEOUT = 1900L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            CoroutineScope(IO).launch {
                fakeApiRequest()
            }

        }

    }

    private suspend fun fakeApiRequest() {

        coroutineScope {

            val job = launch {
                try {

                    val result1 = getResult_1_from_Api() // wait until job is done
                    if (result1 == "Result #1") {
                        setTextOnMainThread("Got $result1")
                        val result2 = getResult_2_from_Api()  // wait until job is done

                        if (result2 == "Result #2") {
                            setTextOnMainThread("Got $result2")

                        } else {
                            setTextOnMainThread("Couldn't get Result #2")
                        }

                    } else {
                        setTextOnMainThread("Couldn't get Result #1")

                    }


                } catch (e: CancellationException) {
                    println("debug: CancellationException: ${e.message}")
                    showToast(e.message.toString())

                } finally {
                    println("debug: Finishing job. Cleaning up resources...")
                }

            }
            isJobRunning(job.isActive)

            delay(JOB_TIMEOUT) // wait to see if job completes in this time

            // Cancel Option 1
            job.cancel(CancellationException("Job took longer than $JOB_TIMEOUT")) // cancel if delay time elapses and job has not completed
            job.join() // wait for the cancellation to happen

            delay(1000)

            isJobRunning(job.isActive)
        }


    }

    private fun setNewtext(input: String) {

        val newText = textView.text.toString() + "\n$input"
        textView.text = newText
    }


    private suspend fun setTextOnMainThread(input: String) {

        withContext(Main) {
            setNewtext(input)
        }
    }

    private fun isJobRunning(isActive: Boolean) {

        if (isActive) {
            showToast("Job is Active")
        } else {
            showToast("Job is not Active")
        }

    }

    private suspend fun getResult_1_from_Api(): String {

        delay(1000)
        return "Result #1"

    }

    private suspend fun getResult_2_from_Api(): String {

        delay(1000)
        return "Result #2"
    }


    private fun showToast(msg: String) {
        GlobalScope.launch(Main) {

            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
        }
    }

}
