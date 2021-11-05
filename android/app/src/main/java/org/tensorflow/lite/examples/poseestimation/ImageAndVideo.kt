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
            } else { //打开album的界面
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
//                val rotateMatrix = Matrix() // 创建单位矩阵
//                rotateMatrix.postRotate(90.0f)//M' = R(度) * M，后乘即左乘一个旋转矩阵，默认围绕原点（屏幕左上角）旋转90度
//
//                val rotatedBitmap = Bitmap.createBitmap(
//
//                    imageBitmap, 0, 0, CameraSource.PREVIEW_WIDTH, CameraSource.PREVIEW_HEIGHT,
//                    rotateMatrix, false
//                )
//                processImage(rotatedBitmap)//这一步里应该有在图像中绘制骨架
//                image.close()
//            }
//        }, imageReaderHandler)



    }

//    fun create(context: Context): MoveNet{
//        val options = Interpreter.Options()
//        var gpuDelegate: GpuDelegate? = null
//        options.setNumThreads(CPU_NUM_THREADS)
//        return MoveNet( //返回一个movenet类
//            Interpreter(
//                FileUtil.loadMappedFile(
//                    //通过内存映射从assert文件夹加载模型文件，返回一个已加载的内存映射文件
//                    context,
//                    LIGHTNING_FILENAME
//
//                ), options //上面设置的参数
//            ),
//            gpuDelegate //不设置gpu加速，此参数为null
//        )
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        var iv_image: ImageView = findViewById(R.id.iv_image)
        when (requestCode){
            CHOOSE_PHOTO -> if (resultCode == Activity.RESULT_OK){
                if (Build.VERSION.SDK_INT >= 19){
                    // 4.4及以上系统使用这个方法处理图片
                    if (data != null){

//                      0.直接调用此函数显示
                        handleImageOnKitKat(data)



//                        val uri: Uri? = data.data


//                        1.直接用uri显示图片
//                        iv_image.setImageURI(uri)


//                        val bitmap = BitmapFactory.decodeFile(uri?.path)


//                        2.用uri转成bitmap显示图片
//                        iv_image.setImageBitmap(bitmap)

//                        3.加入姿态识别部分（还有bug
//                        if (bitmap != null) {
////                            cameraSource?.processImage_pub(bitmap)
//                            iv_image.setImageBitmap(cameraSource?.processImage_pub(bitmap))
//                        }













                    }

                }else{
                    // 4.4以下系统使用这个方法处理图片
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
        startActivityForResult(intent, CHOOSE_PHOTO) //打开相册
    }



    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        // 通过Uri和selection来获取真实的图片路径
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
            // 如果是document类型的Uri，则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":".toRegex()).toTypedArray()[1] // 解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.path
        }
        val bitmap: Bitmap? = BitmapFactory.decodeFile(imagePath)


//      带姿态识别
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

//        只显示图片
//        displayImage(imagePath) // 根据图片路径显示图片


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