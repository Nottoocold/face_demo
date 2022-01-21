package cn.jiming.jdlearning.test.safehat;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import cn.jiming.jdlearning.safehat.SafetyHatDetection;

/**
 * 安全帽检测测试
 * @author xiaoming
 *
 */
public class SafetyHatDetectionTest {
	
	@Test
	public void test() {
	        
		// 推理测试
		Mat src = imread("img/safehat/2.png");
		SafetyHatDetection.detection(src, true, 0.8);

		// 输出图片
		imwrite("img/build/output/safehat_out1.jpg", src);
	}
	
}
