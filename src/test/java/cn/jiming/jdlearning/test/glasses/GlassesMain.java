package cn.jiming.jdlearning.test.glasses;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.Test;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import cn.jiming.jdlearning.face.FaceNet;
import cn.jiming.jdlearning.face.FaceNet.MaxLab;
import cn.jiming.jdlearning.face.detection.OpenCVFaceDetection;
import cn.jiming.jdlearning.face.detection.RetinaFaceDetection;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;
import cn.jiming.jdlearning.glasses.GlassesDetection;
/**
 * 眼镜识别测试窗口（适合在自带有摄像头的电脑上）
 * @author xiaoming
 */
public class GlassesMain {
	private static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	
	private static int w = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 100;
	
	private static int h = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / 2;
	
	@Test
	public void test() throws Exception {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();   //开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);
        while(true){
            if(!canvas.isDisplayable()){//窗口是否关闭
                grabber.stop();//停止抓取
                System.exit(2);//退出
                break;
            }    
            Frame frame = grabber.grabFrame();
            // 将Frame转为Mat
    		Mat mat = converter.convertToMat(frame);
    		
    		
    		//眼镜定位
    		List<cn.jiming.jdlearning.Object> glassesList = GlassesDetection.detection(mat, true, 0.5);
    		
    		
			
    		//Image转Frame
			Frame convertFrame1 = Java2DFrameUtils.toFrame(mat);
    		
            canvas.showImage(convertFrame1);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
            mat.release();
            
        }

		
		
	}

}
