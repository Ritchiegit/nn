package org.tensorflow.lite.examples.poseestimation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ImageReader
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.tensorflow.lite.examples.poseestimation.camera.CameraSource
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.MoveNet
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.media.MediaExtractor
import android.media.MediaFormat
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import android.widget.VideoView
import org.tensorflow.lite.examples.poseestimation.ml.ModelType
import org.tensorflow.lite.examples.poseestimation.ml.PoseDetector


class ImageAndVideo : AppCompatActivity() {
    companion object{

        private val CHOOSE_PHOTO = 1
        private const val CPU_NUM_THREADS = 4

        private const val LIGHTNING_FILENAME = "movenet_lightning.tflite"
        private const val THUNDER_FILENAME = "movenet_thunder.tflite"

        private var device = Device.CPU



        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480
    }

    private var cameraSource: CameraSource? = null
    private var imageReader: ImageReader? = null
    private var path:String = ""
    private lateinit var imageBitmap: Bitmap
    private lateinit var iv_image:ImageView
    private lateinit var vv_video:VideoView
    private lateinit var poseDetector:PoseDetector
    private lateinit var mediaExtractor:MediaExtractor
    private lateinit var detector: PoseDetector

//    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(surfaceView.context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageandvideo)

//        appContext = InstrumentationRegistry.getInstrumentation().targetContext

        var poseDetector = MoveNet.create(this, device, ModelType.Thunder)


//        cameraSource?.setDetector(poseDetector)
        this.detector = poseDetector

//        imageReader =
//            ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 3)



        val button_image: Button = findViewById(R.id.button_image)
        val button_video: Button = findViewById(R.id.button_video)
//        var iv_image: ImageView = findViewById(R.id.iv_image)
        iv_image = findViewById(R.id.iv_image)
        vv_video = findViewById(R.id.vv_video)


        button_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    ImageAndVideo.CHOOSE_PHOTO
                )
            } else { //??????album?????????
//                val intent = Intent(this@MainActivity, AlbumActivity::class.java)
//                startActivity(intent)
                openAlbum()
            }

        }
//        imageReader?.setOnImageAvailableListener({ reader ->
//            val image = reader.acquireLatestImage()
//
//            if (image != null) {
//                if (!::imageBitmap.isInitialized) {
//
//                    imageBitmap =
//                        Bitmap.createBitmap(
//                            PREVIEW_WIDTH,
//                            PREVIEW_HEIGHT,
//                            Bitmap.Config.ARGB_8888
//                        )
//                }
//                yuvConverter.yuvToRgb(image, imageBitmap)
//                // Create rotated version for portrait display
//                val rotateMatrix = Matrix() // ??????????????????
//                rotateMatrix.postRotate(90.0f)//M' = R(???) * M????????????????????????????????????????????????????????????????????????????????????90???
//
//                val rotatedBitmap = Bitmap.createBitmap(
//
//                    imageBitmap, 0, 0, CameraSource.PREVIEW_WIDTH, CameraSource.PREVIEW_HEIGHT,
//                    rotateMatrix, false
//                )
//                processImage(rotatedBitmap)//?????????????????????????????????????????????
//                image.close()
//            }
//        }, imageReaderHandler)



    }

//    fun create(context: Context): MoveNet{
//        val options = Interpreter.Options()
//        var gpuDelegate: GpuDelegate? = null
//        options.setNumThreads(CPU_NUM_THREADS)
//        return MoveNet( //????????????movenet???
//            Interpreter(
//                FileUtil.loadMappedFile(
//                    //?????????????????????assert????????????????????????????????????????????????????????????????????????
//                    context,
//                    LIGHTNING_FILENAME
//
//                ), options //?????????????????????
//            ),
//            gpuDelegate //?????????gpu?????????????????????null
//        )
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        var iv_image: ImageView = findViewById(R.id.iv_image)
        when (requestCode){
            CHOOSE_PHOTO -> if (resultCode == Activity.RESULT_OK){
                if (Build.VERSION.SDK_INT >= 19){
                    // 4.4?????????????????????????????????????????????
                    if (data != null){

//                      0.???????????????????????????
                        handleImageOnKitKat(data)



//                        val uri: Uri? = data.data


//                        1.?????????uri????????????
//                        iv_image.setImageURI(uri)


//                        val bitmap = BitmapFactory.decodeFile(uri?.path)


//                        2.???uri??????bitmap????????????
//                        iv_image.setImageBitmap(bitmap)

//                        3.?????????????????????????????????bug
//                        if (bitmap != null) {
////                            cameraSource?.processImage_pub(bitmap)
//                            iv_image.setImageBitmap(cameraSource?.processImage_pub(bitmap))
//                        }













                    }

                }else{
                    // 4.4??????????????????????????????????????????
                    if (data != null){

//                        handleImageBeforeKitKat(data)

//                        val uri: Uri? = data.data
//                        iv_image?.setImageURI(uri)

//                        handleImageOnKitKat(data)

                    }

                }
            }
        }
    }



    fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO) //????????????
    }



    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        // ??????Uri???selection??????????????????????????????
        val cursor = contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    private fun displayImage(imagePath: String?) {
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            iv_image.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show()
        }
    }





//    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent) {
        var imagePath: String? = null
        val uri = data.data
        Log.d("TAG", "handleImageOnKitKat: uri is $uri")
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // ?????????document?????????Uri????????????document id??????
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":".toRegex()).toTypedArray()[1] // ????????????????????????id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // ?????????content?????????Uri??????????????????????????????
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // ?????????file?????????Uri?????????????????????????????????
            imagePath = uri.path
        }
        val bitmap: Bitmap? = BitmapFactory.decodeFile(imagePath)


//      ???????????????
        if (bitmap != null) {
//           cameraSource?.processImage_pub(bitmap)
            Log.d("TAG", "--------------------------------------")
//            iv_image.setImageBitmap(cameraSource?.processImage_pub(bitmap,detector))
            this.detector.estimateSinglePose(bitmap)
            this.detector.estimateSinglePose(bitmap)
            this.detector.estimateSinglePose(bitmap)
            val person = this.detector.estimateSinglePose(bitmap)
//            val outbitmap:Bitmap = cameraSource?.visualize_pud(person, bitmap)
            val outputbitmap = VisualizationUtils.drawBodyKeypoints(bitmap, person)
            iv_image.setImageBitmap(outputbitmap)
            Log.d("TAG", "++++++++++++++++++++++++++++++++++++++")
        }

//        ???????????????
//        displayImage(imagePath) // ??????????????????????????????


    }

    private fun handleImageBeforeKitKat(data: Intent) {
        val uri = data.data
        val imagePath = getImagePath(uri, null)
        displayImage(imagePath)
    }


    fun video(){
        mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(path)
        var videoFormat: MediaFormat? = null
        for (i in 0..mediaExtractor.trackCount) {
            val mediaFormat = mediaExtractor.getTrackFormat(i)
            if (mediaFormat.getString(MediaFormat.KEY_MIME)?.contains("video") == true) {
                mediaExtractor.selectTrack(i)
                videoFormat = mediaFormat
                break
            }
        }
        if (videoFormat == null) {
            throw IllegalStateException("video format is null")
        }
    }








}