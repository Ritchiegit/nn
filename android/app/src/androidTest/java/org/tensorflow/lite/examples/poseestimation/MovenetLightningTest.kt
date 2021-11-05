/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.content.Context
import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.Device
import org.tensorflow.lite.examples.poseestimation.ml.ModelType
import org.tensorflow.lite.examples.poseestimation.ml.MoveNet
import org.tensorflow.lite.examples.poseestimation.ml.PoseDetector

@RunWith(AndroidJUnit4::class)
class MovenetLightningTest {

    companion object {
        private const val TEST_INPUT_IMAGE1 = "image1.png"
        private const val TEST_INPUT_IMAGE2 = "image2.jpg"
        private const val ACCEPTABLE_ERROR = 21f//这是什么？设置的不一样
    }

    private lateinit var poseDetector: PoseDetector
    private lateinit var appContext: Context
    private lateinit var expectedDetectionResult: List<Map<BodyPart, PointF>>

    //pose_landmark_truth.csv得修改要增加关节点
    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        poseDetector = MoveNet.create(appContext, Device.CPU, ModelType.Lightning)
        expectedDetectionResult =
            EvaluationUtils.loadCSVAsset("pose_landmark_truth.csv")
    }

    @Test
    fun testPoseEstimationResultWithImage1() {
        val input = EvaluationUtils.loadBitmapAssetByName(TEST_INPUT_IMAGE1)

        // As Movenet use previous frame to optimize detection result, we run it multiple times
        //由于 Movenet 使用前一帧来优化检测结果，因此我们多次运行它
        // using the same image to improve result.
        poseDetector.estimateSinglePose(input)//不一样，posenet没有使用
        poseDetector.estimateSinglePose(input)//不一样，posenet没有使用
        poseDetector.estimateSinglePose(input)//不一样，posenet没有使用
        val person = poseDetector.estimateSinglePose(input)
        EvaluationUtils.assertPoseDetectionResult(
            person,
            expectedDetectionResult[0],
            ACCEPTABLE_ERROR
        )
    }

    @Test
    fun testPoseEstimationResultWithImage2() {
        val input = EvaluationUtils.loadBitmapAssetByName(TEST_INPUT_IMAGE2)

        // As Movenet use previous frame to optimize detection result, we run it multiple times
        // using the same image to improve result.
        poseDetector.estimateSinglePose(input)//不一样，posenet没有使用
        poseDetector.estimateSinglePose(input)//不一样，posenet没有使用
        poseDetector.estimateSinglePose(input)//不一样，posenet没有使用
        val person = poseDetector.estimateSinglePose(input)
        EvaluationUtils.assertPoseDetectionResult(
            person,
            expectedDetectionResult[1],
            ACCEPTABLE_ERROR
        )
    }
}