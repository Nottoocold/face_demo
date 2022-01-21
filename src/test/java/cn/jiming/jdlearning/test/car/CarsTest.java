package cn.jiming.jdlearning.test.car;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import cn.jiming.jdlearning.car.CarsObjectDetection;
import cn.jiming.jdlearning.car.CarsObjectDetection2;
import cn.jiming.jdlearning.fire.SmokingDetection;

/**
 * 车辆检测实测
 * @author xiaoming
 *
 */
public class CarsTest {
	
	@Test
	public void test() {
		 //推理测试
        Mat src = imread("img/coco/2.jpg");
        CarsObjectDetection2.detection(src, true, 0.5);
    	
    	//输出图片
        imwrite("img/build/output/car_2.jpg", src);
	}
}
