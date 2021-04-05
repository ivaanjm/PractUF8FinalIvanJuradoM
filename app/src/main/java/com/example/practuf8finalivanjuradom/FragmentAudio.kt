package com.example.practuf8finalivanjuradom

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.practuf8finalivanjuradom.databinding.FragmentAudioBinding
import java.io.File
import java.io.IOException

class FragmentAudio : Fragment() {
private lateinit var binding : FragmentAudioBinding
private lateinit var ruta : File
private var mediaRecorder: MediaRecorder? = null
private var output: String? = null
private var dir: File = File(Environment.getExternalStorageDirectory().absolutePath + "/soundrecorder/")
private var state: Boolean = false
private var recordingStopped: Boolean = false

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    ruta = getOutputDirectory()
    return inflater.inflate(R.layout.fragment_audio, container, false)


}

@RequiresApi(Build.VERSION_CODES.O)
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    checkpermison(view)
    binding = FragmentAudioBinding.bind(view)

    try{
        // create a File object for the parent directory
        val recorderDirectory = getOutputDirectory()
        // have the object build the directory structure, if needed.
        recorderDirectory.mkdirs()
    }catch (e: IOException){
        Toast.makeText(context, "Error ioexception ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
    dir = getOutputDirectory()
    if(dir.exists()){
        val count = dir.listFiles().size
        output = getOutputDirectory().absolutePath + "nombre.mp3"
    }

    mediaRecorder = MediaRecorder()

    mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
    mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
    mediaRecorder?.setOutputFile(output)

    binding.btnRecord.setOnClickListener{
        checkpermison(view)
        startRecording()
    }

    binding.btnPause.setOnClickListener {
        pauseRecording()
    }

    binding.btnPlay.setOnClickListener {
        pauseRecording()
    }


    binding.btnStop.setOnClickListener {
        stopRecording()

    }

}

private fun startRecording() {
    try {
        mediaRecorder?.prepare()
        mediaRecorder?.start()
        state = true
        Toast.makeText(context, "Recording started!", Toast.LENGTH_SHORT).show()
    } catch (e: IllegalStateException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun stopRecording(){
    if(state){
        mediaRecorder?.stop()
        mediaRecorder?.release()
        Toast.makeText(context, "Recording stopped!", Toast.LENGTH_SHORT).show()
        state = false
    }else{
        Toast.makeText(context, "You are not recording right now!", Toast.LENGTH_SHORT).show()
    }
}

@SuppressLint("RestrictedApi", "SetTextI18n")
@TargetApi(Build.VERSION_CODES.N)
private fun pauseRecording() {
    if(state) {
        if(!recordingStopped){
            Toast.makeText(context,"Paused!", Toast.LENGTH_SHORT).show()
            mediaRecorder?.pause()
            recordingStopped = true
            binding.btnPlay.visibility = View.VISIBLE
            binding.btnPause.visibility = View.INVISIBLE
        }else{
            binding.btnPlay.visibility = View.INVISIBLE
            binding.btnPause.visibility = View.VISIBLE
            resumeRecording()
        }
    }
}

@SuppressLint("RestrictedApi", "SetTextI18n")
@TargetApi(Build.VERSION_CODES.N)
private fun resumeRecording() {
    Toast.makeText(context,"Resume!", Toast.LENGTH_SHORT).show()
    mediaRecorder?.resume()
    recordingStopped = false
}


private fun checkpermison(view: View) {
    if (ActivityCompat.checkSelfPermission(
            view.context,
            android.Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            view.context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        val permissions = arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(requireActivity(), permissions, 0)

    } else {
        Log.d("infO", "PERMISSION GRANTED")
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun getOutputDirectory(): File {
    val mediaDir = activity?.externalMediaDirs?.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else activity?.filesDir!!
}
}