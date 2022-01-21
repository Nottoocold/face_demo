package cn.jiming.jdlearning.test.fire;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import cn.jiming.jdlearning.fire.SmokingDetection;

/**
 * 抽烟检测实测
 * @author xiaoming
 *
 */
public class SmokingTest {
	
	@Test
	public void test() {
		 //推理测试
        Mat src = imread("img/smoking/1.jpg");
        SmokingDetection.detection(src, true, 0.8);
    	
    	//输出图片
        imwrite("img/build/output/smoking_out1.jpg", src);
	}
}
