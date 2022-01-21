package cn.jiming.jdlearning.test.face;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;

import cn.jiming.jdlearning.face.comparison.FeatureComparison;
import cn.jiming.jdlearning.face.detection.OpenCVFaceDetection;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;

public class LivingDetection {
	private static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
	
	private static int w = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 100;
	
	private static int h = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height / 2;
	
	public static void main(String[] args) throws Exception {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();   //开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);
        
        List<Mat> livings = new ArrayList<>();
		while(true){
			if(!canvas.isDisplayable()){//窗口是否关闭
                grabber.stop();//停止抓取
                break;
            }    
            Frame frame = grabber.grabFrame();
            // 将Frame转为Mat
    		Mat mat = converter.convertToMat(frame);
			
			List<Mat> faces = OpenCVFaceDetection.detectionToMat(mat, true);
			if(faces != null && faces.size() > 0) {
				livings.add(faces.get(0));
				System.out.println(livings.size());
				//输出图片
		        imwrite("D:\\test\\"+livings.size()+".jpg", faces.get(0));
			}
			
			
			
			
			Frame convertFrame1 = Java2DFrameUtils.toFrame(mat);
			canvas.showImage(convertFrame1);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
            mat.release();
			
			Thread.sleep(100);
			if(livings.size() >= 20) {
				 grabber.stop();//停止抓取
	             break;
			}
		}
		
		System.out.println("开始living 检测");
		List<float[]> datas = new ArrayList<>();
		for(Mat face:livings) {
			float[] feature = FeatureExtraction.faceFeatureExtraction(face);
			datas.add(feature);
		}
		
		System.out.println("开始living 对比");
		double v = living(datas);
		System.out.println("living=" + v);
	}

	private static double living(List<float[]> datas) {
		double v = 0;
		int n = 1;
		for(int i = 0; i < datas.size(); i++) {
			float[] d1 = datas.get(i);
			for(int j = i + 1; j < datas.size(); j++) {
				float[] d2 = datas.get(j);
				
				v = v + FeatureComparison.calculSimilar(d1, d2);
				n++;
			}
			
		}
		
		
		return v / n;
	}

}
