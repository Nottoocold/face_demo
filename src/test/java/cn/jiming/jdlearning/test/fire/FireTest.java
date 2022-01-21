package cn.jiming.jdlearning.test.fire;

import org.junit.Test;

import cn.jiming.jdlearning.fire.FireDetection;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

/**
 * 火苗烟雾检测测试
 * @author xiaoming
 *
 */
public class FireTest {
	
	@Test
	public void test() {
		
		// 推理测试
		Mat src = imread("img/fire/fire_1.jpg");
		FireDetection.detection(src, true, 0.2);

		// 输出图片
		imwrite("img/build/output/fire_out.jpg", src);
	}
}
