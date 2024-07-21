package cn.jiming.jdlearning.test.face;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import cn.jiming.jdlearning.face.FaceNet;
import cn.jiming.jdlearning.face.FaceNet.MaxLab;
import cn.jiming.jdlearning.face.detection.OpenCVFaceDetection;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

/**
 * 人脸识别测试
 *
 * @author xiaoming
 */
public class FaceRecognitionTest {

    @Test
    public void convert() throws Exception {
        String facePath = Paths.get("img/face/train/xiaoming/0.png").toAbsolutePath().toString();
        Mat src = imread(facePath);
        List<Mat> faces = OpenCVFaceDetection.detectionToMat(src, true);
        Mat face = faces.get(0);
        imwrite(facePath, face);
    }

    @Test
    public void test() throws Exception {
        /**
         * 人脸识别测试
         */
        FaceNet faceNet = FaceNet.load("models/face_net.xm");
        int labs = faceNet.labs();
        int features = faceNet.features();
        System.out.println("人数=" + labs + " 样本数=" + features);

        Path facePath = Paths.get("img/face/train/xiaoming/0.png");
        Image img = ImageFactory.getInstance().fromFile(facePath);
        float[] feature = FeatureExtraction.faceFeatureExtraction(img);

        //推理是属于哪个lab
        MaxLab maxLab = null;
        for (int i = 0; i < 3; i++) {
            long start = System.currentTimeMillis();
            maxLab = faceNet.predict(feature);
            System.out.println("lab=" + maxLab.maxLab + "  相似度=" + maxLab.maxSimilar + "  耗时=" + (System.currentTimeMillis() - start));

        }

    }

}
