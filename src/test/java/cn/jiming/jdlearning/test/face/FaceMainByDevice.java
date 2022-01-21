package cn.jiming.jdlearning.test.face;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;

import cn.jiming.jdlearning.face.FaceManager;
import cn.jiming.jdlearning.face.FaceNet;
import cn.jiming.jdlearning.face.FaceNet.MaxLab;
import cn.jiming.jdlearning.face.detection.OpenCVFaceDetection;
import cn.jiming.jdlearning.face.detection.RetinaFaceDetection;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 获取海康摄像头进行人俩识别
 * @author xiaoming
 */
public class FaceMainByDevice {

	private static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	
	private static int w = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 100;
	
	private static int h = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / 2;
	
	@Test
	public static void test() throws Exception {
		FaceManager manager = new FaceManager("models/face_net_0602.xm");

		String devicePath = "rtsp://admin:123456xj@10.14.10.10";

		FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(devicePath);
		grabber.setOption("rtsp_transport", "tcp"); // 使用tcp的方式，不然会丢包很严重
		grabber.setVideoCodecName("h264");
		grabber.setImageWidth(854);
		grabber.setImageHeight(640);
		System.out.println("grabber start");
		grabber.start();

		CanvasFrame canvasFrame = new CanvasFrame("摄像机");
		canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvasFrame.setAlwaysOnTop(true);

		while (true){
			grabber.flush();
			Frame frame = grabber.grabImage();
			Mat mat = Java2DFrameUtils.toMat(frame);//converter.convertToMat(frame);
			
			//异步进行人脸识别，识别完成后回调任务事件
			boolean isRc = manager.asynPredictor(mat, new FaceManager.Callback() {
				
				@Override
				public void run(List<MaxLab> labs) {
					//打印识别结果
					for(MaxLab lab:labs) {
						System.out.println("lab=" + lab.maxLab + "  相似度=" + lab.maxSimilar);
					}
				}
			});
			
			//本次放弃识别,opencv 人脸定位
			OpenCVFaceDetection.detection(mat, true);
			
			//Image转Frame
			Frame convertFrame1 = Java2DFrameUtils.toFrame(mat);

			canvasFrame.showImage(convertFrame1);
			
			//销毁mat
//			mat.release();

		}
	}

	
}
