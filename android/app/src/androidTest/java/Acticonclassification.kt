import kotlin.Throws
import com.opencsv.CSVWriter
import java.io.*
import java.util.*
import java.util.stream.DoubleStream
import kotlin.jvm.JvmStatic

class Acticonclassification {
    //函数----对其躯干
    //读取得到的坐标点的CSV文件(列之间用，分隔)，CSV应该有3列分别是关键点序号，x,y,#,keypoint_id,x,y
    //读取CSV文件
    fun Align_torso(inFile: File?): Array<DoubleArray> {
        //File inFile = new File(readpath);
        val poses = Array(20) { DoubleArray(2) } //一共19个点，每个点有xy坐标
        try {
            val reader = BufferedReader(FileReader(inFile))
            var line: String? = null
            var i = 0
            while (reader.readLine().also { line = it } != null) {
                val item = line!!.split(",").toTypedArray() //用逗号来分开,item应该是有3个变量
                poses[i][0] = java.lang.Double.valueOf(item[1]).toDouble()
                poses[i][1] = java.lang.Double.valueOf(item[2]).toDouble()
                i++
            }
            reader.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //添加脖子和腹部节点，并设置胸部节点为（0，0）
        poses[17][0] = (poses[5][0] + poses[6][0]) / 2
        poses[17][1] = (poses[5][1] + poses[6][1]) / 2
        poses[18][0] = (poses[11][0] + poses[12][0]) / 2
        poses[18][1] = (poses[11][1] + poses[12][1]) / 2
        poses[19][0] = (poses[17][0] + poses[18][0]) / 2
        poses[19][1] = (poses[17][1] + poses[18][1]) / 2
        //LinkedList<Integer> LEFT_LEG=new linkedList<Integer>();
        val RIGHT_LEG = intArrayOf(18, 11, 13, 15)
        val LEFT_LEG = intArrayOf(18, 12, 14, 16)
        val RIGHT_ARM = intArrayOf(18, 17, 5, 7, 9)
        val LEFT_ARM = intArrayOf(18, 17, 6, 8, 10)
        val HEAD = intArrayOf(18, 17, 0)
        val SPINE = intArrayOf(18, 17)
        val RIGHT_ARM_SPINE = intArrayOf(17, 5, 7, 9)
        val LEFT_ARM_SPINE = intArrayOf(17, 6, 8, 10)
        val HEAD_SPINE = intArrayOf(17, 0)
        val poses_new = Array(20) { DoubleArray(2) }
        //求了一下每条线的长度
        //System.out.print("poses",poses);
        for (i in 1..3) {
            poses_new[RIGHT_LEG[i]][0] = poses[RIGHT_LEG[i]][0] - poses[RIGHT_LEG[i - 1]][0]
            poses_new[RIGHT_LEG[i]][1] = poses[RIGHT_LEG[i]][1] - poses[RIGHT_LEG[i - 1]][1]
            poses_new[LEFT_LEG[i]][0] = poses[LEFT_LEG[i]][0] - poses[LEFT_LEG[i - 1]][0]
            poses_new[LEFT_LEG[i]][1] = poses[LEFT_LEG[i]][1] - poses[LEFT_LEG[i - 1]][1]
        }
        for (i in 1..4) {
            poses_new[RIGHT_ARM[i]][0] = poses[RIGHT_ARM[i]][0] - poses[RIGHT_ARM[i - 1]][0]
            poses_new[RIGHT_ARM[i]][1] = poses[RIGHT_ARM[i]][1] - poses[RIGHT_ARM[i - 1]][1]
            poses_new[LEFT_ARM[i]][0] = poses[LEFT_ARM[i]][0] - poses[LEFT_ARM[i - 1]][0]
            poses_new[LEFT_ARM[i]][1] = poses[LEFT_ARM[i]][1] - poses[LEFT_ARM[i - 1]][1]
        }
        val poses_ret = Array(20) { DoubleArray(2) }
        for (i in 1..2) {
            poses_new[HEAD[i]][0] = poses[HEAD[i]][0] - poses[HEAD[i - 1]][0]
            poses_new[HEAD[i]][1] = poses[HEAD[i]][1] - poses[HEAD[i - 1]][1]
            val ratio =
                50 / Math.sqrt(poses_new[17][0] * poses_new[17][0] + poses_new[17][1] * poses_new[17][1])
            for (p in 0..19) {
                for (j in 0..1) {
                    poses_ret[p][j] = poses_new[p][j] * ratio
                }
            }
        }
        for (i in 1..3) {
            poses_ret[RIGHT_LEG[i]][0] = poses_ret[RIGHT_LEG[i]][0] - poses_ret[RIGHT_LEG[i - 1]][0]
            poses_ret[RIGHT_LEG[i]][1] = poses_ret[RIGHT_LEG[i]][1] - poses_ret[RIGHT_LEG[i - 1]][1]
            poses_ret[LEFT_LEG[i]][0] = poses_ret[LEFT_LEG[i]][0] - poses_ret[LEFT_LEG[i - 1]][0]
            poses_ret[LEFT_LEG[i]][1] = poses_ret[LEFT_LEG[i]][1] - poses_ret[LEFT_LEG[i - 1]][1]
        }
        for (i in 1..1) {
            poses_ret[SPINE[i]][0] = poses_ret[SPINE[i]][0] - poses_ret[SPINE[i - 1]][0]
            poses_ret[SPINE[i]][1] = poses_ret[SPINE[i]][1] - poses_ret[SPINE[i - 1]][1]
        }
        for (i in 1..3) {
            poses_ret[RIGHT_ARM_SPINE[i]][0] =
                poses_ret[RIGHT_ARM_SPINE[i]][0] - poses_ret[RIGHT_ARM_SPINE[i - 1]][0]
            poses_ret[RIGHT_ARM_SPINE[i]][1] =
                poses_ret[RIGHT_ARM_SPINE[i]][1] - poses_ret[RIGHT_ARM_SPINE[i - 1]][1]
            poses_ret[LEFT_ARM_SPINE[i]][0] =
                poses_ret[LEFT_ARM_SPINE[i]][0] - poses_ret[LEFT_ARM_SPINE[i - 1]][0]
            poses_ret[LEFT_ARM_SPINE[i]][1] =
                poses_ret[LEFT_ARM_SPINE[i]][1] - poses_ret[LEFT_ARM_SPINE[i - 1]][1]
        }
        for (i in 1..1) {
            poses_ret[HEAD_SPINE[i]][0] =
                poses_ret[HEAD_SPINE[i]][0] - poses_ret[HEAD_SPINE[i - 1]][0]
            poses_ret[HEAD_SPINE[i]][1] =
                poses_ret[HEAD_SPINE[i]][1] - poses_ret[HEAD_SPINE[i - 1]][1]
        }
        //double[][] centers=new double[20][2];
        val p1 = 250.0 - poses[18][0]
        val p2 = 250.0 - poses[18][1]
        for (i in 0..19) {
            poses_ret[i][0] = p1 + poses_ret[i][0]
            poses_ret[i][1] = p2 + poses_ret[i][1]
        }
        poses_ret[19][0] = 0.0
        poses_ret[19][1] = 0.0
        return poses_ret
    }

    //函数---计算两条线之间的夹角
    fun Cal_angle(v1: DoubleArray, v2: DoubleArray): Double {
        val dx1 = v1[0] - v1[2]
        val dy1 = v1[1] - v1[3]
        val dx2 = v2[0] - v2[2]
        val dy2 = v2[1] - v2[3]
        val len12 = Math.sqrt(dx1 * dx1 + dy1 * dy1)
        val len13 = Math.sqrt(dx2 * dx2 + dy2 * dy2)
        val cosangle2: Double = (dx1 * dx2 + dy1 * dy2) / (abs(len12) * abs(len13))
        return Math.acos(round(cosangle2, 2)) * 180 / Math.pi
    }

    //函数---构建角度特征
    fun Build_feature_from_angle(poses_ret: Array<DoubleArray>): DoubleArray {
        val angle_output = DoubleArray(13)
        val angle_list = arrayOf(
            intArrayOf(16, 14, 12),
            intArrayOf(14, 12, 18),
            intArrayOf(15, 13, 11),
            intArrayOf(13, 11, 18),
            intArrayOf(12, 18, 11),
            intArrayOf(10, 8, 6),
            intArrayOf(8, 6, 17),
            intArrayOf(6, 17, 18),
            intArrayOf(9, 7, 5),
            intArrayOf(7, 5, 17),
            intArrayOf(5, 17, 18),
            intArrayOf(12, 18, 17),
            intArrayOf(11, 18, 17)
        )
        for (i in 0..12) {
            val v1 = DoubleArray(4)
            val v2 = DoubleArray(4)
            v1[0] = poses_ret[angle_list[i][0]][0]
            v1[1] = poses_ret[angle_list[i][0]][1]
            v1[2] = poses_ret[angle_list[i][1]][0]
            v1[3] = poses_ret[angle_list[i][1]][1]
            v2[0] = poses_ret[angle_list[i][2]][0]
            v2[1] = poses_ret[angle_list[i][2]][1]
            v2[2] = poses_ret[angle_list[i][1]][0]
            v2[3] = poses_ret[angle_list[i][1]][1]
            angle_output[i] = Cal_angle(v1, v2)
        }
        return angle_output
    }

    //函数---得到水平垂直距离
    fun Distance_Ankle_Wrist(poses_ret: Array<DoubleArray>): DoubleArray {
        val distance_ankle_wrist = DoubleArray(28)
        distance_ankle_wrist[0] = Math.abs(poses_ret[16][0] - poses_ret[10][0])
        distance_ankle_wrist[1] = Math.abs(poses_ret[16][1] - poses_ret[10][1])
        distance_ankle_wrist[2] = Math.abs(poses_ret[15][0] - poses_ret[9][0])
        distance_ankle_wrist[3] = Math.abs(poses_ret[15][1] - poses_ret[9][1])
        distance_ankle_wrist[4] = Math.abs(poses_ret[16][0] - poses_ret[9][0])
        distance_ankle_wrist[5] = Math.abs(poses_ret[16][1] - poses_ret[9][1])
        distance_ankle_wrist[6] = Math.abs(poses_ret[15][0] - poses_ret[10][0])
        distance_ankle_wrist[7] = Math.abs(poses_ret[15][1] - poses_ret[10][1])
        distance_ankle_wrist[8] = Math.abs(poses_ret[10][0] - poses_ret[9][0])
        distance_ankle_wrist[9] = Math.abs(poses_ret[10][1] - poses_ret[9][1])
        distance_ankle_wrist[10] = Math.abs(poses_ret[16][0] - poses_ret[15][0])
        distance_ankle_wrist[11] = Math.abs(poses_ret[16][1] - poses_ret[15][1])
        distance_ankle_wrist[12] = Math.abs(poses_ret[16][0] - poses_ret[18][0])
        distance_ankle_wrist[13] = Math.abs(poses_ret[16][1] - poses_ret[18][1])
        distance_ankle_wrist[14] = Math.abs(poses_ret[15][0] - poses_ret[18][0])
        distance_ankle_wrist[15] = Math.abs(poses_ret[15][1] - poses_ret[18][1])
        distance_ankle_wrist[16] = Math.abs(poses_ret[18][0] - poses_ret[10][0])
        distance_ankle_wrist[17] = Math.abs(poses_ret[18][1] - poses_ret[10][1])
        distance_ankle_wrist[18] = Math.abs(poses_ret[18][0] - poses_ret[15][0])
        distance_ankle_wrist[19] = Math.abs(poses_ret[18][1] - poses_ret[15][1])
        distance_ankle_wrist[20] = Math.abs(poses_ret[13][0] - poses_ret[18][0])
        distance_ankle_wrist[21] = Math.abs(poses_ret[13][1] - poses_ret[18][1])
        distance_ankle_wrist[22] = Math.abs(poses_ret[14][0] - poses_ret[18][0])
        distance_ankle_wrist[23] = Math.abs(poses_ret[14][1] - poses_ret[18][1])
        distance_ankle_wrist[24] = Math.abs(poses_ret[18][0] - poses_ret[7][0])
        distance_ankle_wrist[25] = Math.abs(poses_ret[18][1] - poses_ret[7][1])
        distance_ankle_wrist[26] = Math.abs(poses_ret[18][0] - poses_ret[8][0])
        distance_ankle_wrist[27] = Math.abs(poses_ret[18][1] - poses_ret[8][1])
        return distance_ankle_wrist
    }

    //构建真实的特征值
    fun Build_features_realvalue(
        distance_ankle_wrist: DoubleArray,
        angle_output: DoubleArray
    ): DoubleArray {
        val t = distance_ankle_wrist.size + angle_output.size
        val feature_final = DoubleArray(t)
        for (i in angle_output.indices) {
            feature_final[i] = angle_output[i]
        }
        var j = 0
        var p = angle_output.size
        while (j < distance_ankle_wrist.size) {
            feature_final[p] = distance_ankle_wrist[j]
            p++
            j++
        }
        return feature_final
    }

    //把特征转成01来判断是否在做动作
    fun Angle_distance_toturn_1(
        feature_final1: DoubleArray,
        feature_final2: DoubleArray
    ): IntArray {
        val feature_toturn_1 = IntArray(feature_final1.size)
        for (i in 0..12) {
            if (feature_final2[i] >= feature_final1[i] * 2 / 3 && feature_final2[i] <= feature_final1[i] * (1 + 1 / 3)) feature_toturn_1[i] =
                1 else feature_toturn_1[i] = 0
        }
        val p = 13
        for (j in 0 until feature_final1.size - p) {
            if (feature_final2[p + j] <= 0 && feature_final1[p + j] <= 0 && feature_final2[p + j] <= feature_final1[p + j] * 2 / 3 && feature_final2[p + j] >= feature_final1[p + j] * (1 + 1 / 3)) feature_toturn_1[p + j] =
                1 else if (feature_final2[p + j] >= 0 && feature_final1[p + j] >= 0 && feature_final2[p + j] >= feature_final1[p + j] * 2 / 3 && feature_final2[p + j] <= feature_final1[p + j] * (1 + 1 / 3)) feature_toturn_1[p + j] =
                1 else continue
        }
        return feature_toturn_1
    }

    //输入的是对齐之后的骨架坐标，相当于把特征进行整合然后得到结果
    fun Get_results(aligned_pose: Array<DoubleArray>): List<Double?> {
        val angle = Build_feature_from_angle(aligned_pose)
        val distance_Ankle_Wrist = Distance_Ankle_Wrist(aligned_pose)
        //不用使用Build_features_realvalue这个函数了
        val feature_results = DoubleStream.concat(
            Arrays.stream(angle),
            Arrays.stream(distance_Ankle_Wrist)
        ).toArray()
        //return feature_results;
        //List<Double> feature_results_list=new ArrayList<Double>();
        val feature_results_Double =
            arrayOfNulls<Double>(feature_results.size)
        for (i in feature_results.indices) {
            feature_results_Double[i] = feature_results[i]
        }
        val feature_results_list1 =
            Arrays.asList(*feature_results_Double)
        return ArrayList<Any?>(feature_results_list1)
    }

    //保存训练数据集，每一行对应一个图片并且加入类别
    //先实现一个类别的，给他们加上标签，最后是要保存在一个csv文件中 序号 标签 特征值
    @Throws(IOException::class)
    fun Get_results_labelcsv(
        inputfolderpath: String?,
        outputfilepath: String?,
        label: String?
    ): ArrayList<Array<String?>> {
        val file = File(inputfolderpath)
        val fs = file.listFiles()
        val save_list = ArrayList<Array<String?>>()
        val save_pd = CSVWriter(FileWriter(outputfilepath))
        for (f in fs) {
            //读取每个文件，文件里面是坐标，然后得到特征，保存到csv文件中
            val aligned_pose = Align_torso(f)
            val feature_results_list = Get_results(aligned_pose)
            //feature_results_list.add(0,label);//保存了标签+特征
            //HashMap<String,List<Double>> save_one=new HashMap<String,List<Double>>();
            //List<String> save_one = new ArrayList<String>();
            val save_one = arrayOfNulls<String>(feature_results_list.size + 2) //存一个文件名
            //StringBuilder one_filename=new StringBuilder(f.getName());
            save_one[0] = f.name //保存文件名
            var j = 1
            for (i in feature_results_list) {
                save_one[j] = i.toString()
                j++
            }
            save_one[j] = label //label放在最后了
            save_list.add(save_one)
            //save_pd.writeNext(save_one);
        }
        save_pd.writeAll(save_list)
        save_pd.flush()
        return save_list
    }

    //传入的是四个文件夹，返回的是所有文件夹中所有内容的的一个整体的CSV文件
    @Throws(IOException::class)
    fun Get_results_labelcsv_in_total_folder(
        total_folder: String?,
        output_filepath1: String
    ): ArrayList<Array<String?>> {
        val folder_name: File = file(total_folder)
        val all_category_input_pd = CSVWriter(FileWriter(output_filepath1))
        val fs_folder = folder_name.listFiles()
        val save_allfolder_list = ArrayList<Array<String?>>()
        for (f in fs_folder) {
            val label = f.name.split("-").toTypedArray()[0]
            val save_onefolder_list = Get_results_labelcsv(
                "$fs_folder/$f", "$output_filepath1/$label.csv", label
            )
            save_allfolder_list.addAll(save_onefolder_list)
        }
        all_category_input_pd.writeAll(save_allfolder_list)
        all_category_input_pd.flush()
        return save_allfolder_list
    }

    companion object {
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            // TODO Auto-generated method stub
            //Test for svm_train and svm_predict
            //svm_train:
            //    param: String[], parse result of command line parameter of svm-train
            //    return: String, the directory of modelFile
            //svm_predect:
            //    param: String[], parse result of command line parameter of svm-predict, including the modelfile
            //    return: Double, the accuracy of SVM classification
            val trainArgs = arrayOf("UCI-breast-cancer-tra") //directory of training file
            val modelFile: String = svm_train.main(trainArgs)
            val testArgs = arrayOf(
                "UCI-breast-cancer-test",
                modelFile,
                "uci-breast-cancer-result"
            ) //directory of test file,model file,result file
            val accuracy: Double = svm_predict.main(testArgs)
            println("SVM Classification is done! The accuracy is$accuracy")
            //Test for cross validation
            //String[] crossValidationTrainArgs = {"-v", "10", "UCI-breast-cancer-tra"};// 10 fold cross validation
            //modelFile = svm_train.main(crossValidationTrainArgs);
            //System.out.print("Cross validation is done! The modelFile is " + modelFile);
        }
    }
}