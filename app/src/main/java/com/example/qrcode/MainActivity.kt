package com.example.qrcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.qrcode.databinding.ActivityMainBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class MainActivity : AppCompatActivity() {
    private var cameraSource: CameraSource? = null
    private var cameraView: SurfaceView? = null
    private var token = ""
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        cameraView = binding.cameraView
        initQR()
    }

    fun initQR(){
        val qrdetector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, qrdetector).setRequestedPreviewSize(1600, 1024).setAutoFocusEnabled(true).build()

        cameraView?.holder?.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                if(ActivityCompat.checkSelfPermission(this@MainActivity,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    Log.d("PermisoCamara", "No hay permiso")
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),1)
                    }
                    return
                } else {
                    Log.d("PermisoCamara", "Hay permiso")
                    cameraSource?.start(cameraView?.holder)
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                cameraSource?.stop()
            }
        })

        qrdetector.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {
                //
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes= detections?.detectedItems
                if(barcodes?.size() ?: 0>0){
                    token = barcodes.valueAt(0).displayValue.toString()
                    Log.d("token", token)
                    binding.tvMsg.setText(token)
                }
            }
        })
    }
}