package cn.jiming.jdlearning.test.coco;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;

import cn.jiming.jdlearning.coco.CoCoObjectDetection;
import cn.jiming.jdlearning.face.FaceManager;
import cn.jiming.jdlearning.face.FaceNet;
import cn.jiming.jdlearning.face.FaceNet.MaxLab;
import cn.jiming.jdlearning.face.detection.OpenCVFaceDetection;
import cn.jiming.jdlearning.face.detection.RetinaFaceDetection;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;
import cn.jiming.jdlearning.fire.FireDetection;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;

import static org.bytedeco.opencv.global.opencv_imgproc.getRectSubPix;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 获取海康摄像头进行火苗识别
 * @author xiaoming
 */
public class ObjectDetectionMainByMP4 {

	private static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	
	private static int w = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 100;
	
	private static int h = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / 2;
	
	
	@Test
	public void test() throws Exception {

		String src = "E:\\word-discern\\train\\多路人体定位数据集\\2.mp4";
		FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(src);
		grabber.setNumBuffers(1);
//		grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
//		grabber.setImageWidth(1280);
//		grabber.setImageHeight(720);
		System.out.println("grabber start");
		grabber.start();
		
		CanvasFrame canvasFrame = new CanvasFrame("摄像机");
		canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvasFrame.setAlwaysOnTop(true);

		while (grabber.grab() != null){
			grabber.flush();
			Frame frame = grabber.grabImage();
			if(frame == null) {
				System.out.println("null");
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				continue;
			}
			
			Mat mat = Java2DFrameUtils.toMat(frame);//converter.convertToMat(frame)
			
			
			//coco数据集对象检测
			CoCoObjectDetection.detection(mat, true, 0.75);
			
			//人脸定位
			List<Image> faces = OpenCVFaceDetection.detection(mat, true);
			
			//Image转Frame
			Frame convertFrame1 = Java2DFrameUtils.toFrame(mat);
			
			canvasFrame.showImage(convertFrame1);
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//释放mat
			mat.close();
		}
	}

	
}
