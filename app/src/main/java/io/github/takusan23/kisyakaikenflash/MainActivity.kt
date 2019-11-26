package io.github.takusan23.kisyakaikenflash

import android.Manifest
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.getSystemService
import android.hardware.camera2.CameraManager
import androidx.core.app.ComponentActivity.ExtraData
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.KeyEvent
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : AppCompatActivity() {

    //RuntimePermissionの値
    val request = 845

    //光っているか。trueなら点灯。falseは消灯
    var isLight = false

    var timer = Timer()

    //カメラ関係
    lateinit var cameraManager:CameraManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //カメラの権限があるか確認
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            //ひつようなやーつ
            lightInit()

            //自動で連続やるやつ
            initAutoFlash()
        }
        else{
            // 拒否していた場合 リクエストする
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA), request)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == request){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //権限確保
                Toast.makeText(this,"権限が与えられました。",Toast.LENGTH_SHORT).show()

                //ひつようなやーつ
                lightInit()

                //自動で連続やるやつ
                initAutoFlash()

            }else{
                Toast.makeText(this,"権限が付与されませんでした。何もできません。",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAutoFlash() {
        val sleepMs = 50L
        val list = cameraManager.cameraIdList
        flash_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                timer = Timer()
                timer.schedule(timerTask {

                    cameraManager.setTorchMode(list[0], true)
                    Thread.sleep(sleepMs)
                    cameraManager.setTorchMode(list[0], false)
                    Thread.sleep(sleepMs)

                    cameraManager.setTorchMode(list[0], true)
                    Thread.sleep(sleepMs)
                    cameraManager.setTorchMode(list[0], false)
                    Thread.sleep(sleepMs)

                    cameraManager.setTorchMode(list[0], true)
                    Thread.sleep(sleepMs)
                    cameraManager.setTorchMode(list[0], false)
                    Thread.sleep(sleepMs)

                },100,1000)
            }else{
                timer.cancel()
                cameraManager.setTorchMode(list[0], false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    fun lightInit(){
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        //ボタン押したとき
        flash_button.setOnClickListener {
            flashhhhhh()
        }

        //状態を受け取る
        cameraManager.registerTorchCallback(object: CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)
                if(enabled){
                    flash_button.text = "点灯中"
                }else{
                    flash_button.text = "消灯中"
                }
            }
        },null)
    }

    fun flashhhhhh(){
        val list = cameraManager.cameraIdList
        if(isLight){
            cameraManager.setTorchMode(list[0], false)
        }else{
            cameraManager.setTorchMode(list[0], true)
        }
        //反転
        isLight = !isLight
    }

    //物理キーで操作可能に
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val list = cameraManager.cameraIdList
        if (event?.action == KeyEvent.ACTION_UP) {
            when (event?.keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    flashhhhhh()
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    flashhhhhh()
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

}
