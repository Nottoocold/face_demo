package cn.jiming.jdlearning.test.glasses;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import cn.jiming.jdlearning.car.CarsObjectDetection;
import cn.jiming.jdlearning.car.CarsObjectDetection2;
import cn.jiming.jdlearning.fire.SmokingDetection;
import cn.jiming.jdlearning.glasses.GlassesDetection;

/**
 * 眼镜检测实测
 * @author xiaoming
 *
 */
public class GlassesTest {
	
	@Test
	public void test() {
		 //推理测试
        Mat src = imread("img/glasses/4.jpg");
        GlassesDetection.detection(src, true, 0.5);
    	
    	//输出图片
        imwrite("img/output/glasses_4.jpg", src);
	}
}
