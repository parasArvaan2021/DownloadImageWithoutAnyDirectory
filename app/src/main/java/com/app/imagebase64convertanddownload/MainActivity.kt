package com.app.imagebase64convertanddownload


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {


    private val URLS = arrayOf(
        stringToURL("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg"),
        stringToURL("https://i.redd.it/v1fvin01ynv51.jpg"),
        stringToURL("https://helpx.adobe.com/content/dam/help/en/photoshop/using/convert-color-image-black-white/jcr_content/main-pars/before_and_after/image-before/Landscape-Color.jpg"),
        stringToURL("https://im0-tub-ru.yandex.net/i?id=84dbd50839c3d640ebfc0de20994c30d&n=27&h=480&w=480")
// and so on
    )
    private lateinit var mImageView: ImageView
    private lateinit var mProgressDialog: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mProgressDialog = findViewById(R.id.mProgressDialog)

        mImageView = findViewById(R.id.image)

        findViewById<Button>(R.id.button).setOnClickListener {
            download()
        }

    }

    private fun stringToURL(urlString: String?): URL? {
        try {
            return URL(urlString)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }



    fun download() {

        val executor: ExecutorService = Executors.newSingleThreadExecutor();
        val  handler: Handler = Handler(Looper.getMainLooper());
        mProgressDialog.visibility= View.VISIBLE
        executor.execute {

            val count =URLS.size
            var connection: HttpURLConnection? = null
            val bitmaps: MutableList<Bitmap> = ArrayList()

            for(i in 0 until count){
                val currentUrl=URLS[i]
                try {
                    connection = currentUrl?.openConnection() as HttpURLConnection
                    connection.connect()
                    val inputStream = connection.inputStream
                    val bufferedInputStream = BufferedInputStream(inputStream)
                    val bmp = BitmapFactory.decodeStream(bufferedInputStream)

                    bitmaps.add(bmp)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Log.i("TAG", "doInBackground: $bitmaps")
            handler.post {
                mProgressDialog.visibility= View.GONE
                val bitmapString=getImageUriFromBitmap(this,bitmaps[0])
                mImageView.setImageURI(bitmapString)

            }
        }
    }


    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }



}