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
     * async() is a blocking call (similar to the job pattern with job.join())
     *  NOTES:
     *  1) IF you don't call await(), it does not wait for the result
     *  2) Calling await() on both these Deffered values will EXECUTE THEM IN PARALLEL. But the RESULTS won't
     *     be published until the last result is available (in this case that's result2)
     *
     */

    private suspend fun fakeApiRequest() {


        withContext(IO) {

            val executionTime = measureTimeMillis {

                val result1: Deferred<String> = async {
                    showToast("debug: launching job1: ${Thread.currentThread().name}")
                    getResult_1_from_Api()
                }

                val result2 = async {
                    showToast("debug: launching job2: ${Thread.currentThread().name}")
                    getResult_2_from_Api()
                }

                setTextOnMainThread("Got ${result1.await()}")  //we will have to call await here to get the results
                setTextOnMainThread("Got ${result2.await()}")


            }
            println("debug: job1 and job2 are complete. It took $executionTime ms")
            setTextOnMainThread("\njob1 and job2 are completed. It took $executionTime ms")
        }
    }


    /*//using job/launch pattern
    private fun fakeApiRequest(){


        val startTime = System.currentTimeMillis()
        val parentJob =  CoroutineScope(IO).launch {

            val job1 = launch {

                val time1 = measureTimeMillis {
                    println("launching job1 in thread: ${Thread.currentThread().name}")
                    val result1 = getResult_1_from_Api()
                    setTextOnMainThread("Got ${result1}")
                }
                showToast("job 1 took $time1 ms")
            }

            val job2 = launch {
                val time2 = measureTimeMillis {

                    println("launching job2 in thread: ${Thread.currentThread().name}")
                    val result2 = getResult_2_from_Api()
                    setTextOnMainThread("Got ${result2}")
                }
                showToast("job 2 took $time2 ms")
            }
        }
        parentJob.invokeOnCompletion {
            println("debug: job1 and job2 are complete. It took ${System.currentTimeMillis() - startTime} ms")
            showToast("\njob1 and job2 are completed. It took ${System.currentTimeMillis() - startTime} ms")
        }
    }*/

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

    private suspend fun getResult_2_from_Api(): String {

        delay(1700)

        return "Result #2"
    }


    private fun showToast(msg: String) {
        GlobalScope.launch(Main) {

            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
        }
    }

}
