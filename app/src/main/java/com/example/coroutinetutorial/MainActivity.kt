package com.example.coroutinetutorial

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private companion object{

        const val PROGRESS_MAX = 100
        const val PROGRESS_START = 0
        const val JOB_TIME = 4000
    }

    private lateinit var job: CompletableJob


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener{

            if(!::job.isInitialized){

                initJob()
            }
            progressBar.startJobOrCancel()

        }

    }


    private fun ProgressBar.startJobOrCancel(){

        if(this.progress > 0){
            showToast("$job is already active.. cancelling...")
            resetJob()
        }else{

            button.text = "Cancel Job"

            CoroutineScope(IO + job).launch {

                showToast("coroutine ${this} is activated with job ${job}.")

                for(i in PROGRESS_START.. PROGRESS_MAX){

                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i

                }
                updateJobCompleteTextView("Job is complete!")

            }
        }

    }

    private fun updateJobCompleteTextView(text: String){
        GlobalScope.launch (Main){
            textView.text = text
        }
    }

    private fun resetJob() {

        if(job.isCompleted || job.isActive){

            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()    //Here we have to initialize our job again because we cannot use the job that has been cancelled
    }


    private fun initJob(){

        button.text = "Click"
        textView.text = ""
        job = Job()

        job.invokeOnCompletion {

            it?.message.let {

                var msg = it

                if(msg.isNullOrBlank()){
                    msg = "Unknown Error occured"
                }
                Log.e("Appdebug", "$job was cancelled. Reason: ${msg}")
                showToast(msg)
            }
        }

        progressBar.max = PROGRESS_MAX
        progressBar.progress = PROGRESS_START
    }

    private fun showToast(msg: String) {
        GlobalScope.launch(Main) {

            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}
