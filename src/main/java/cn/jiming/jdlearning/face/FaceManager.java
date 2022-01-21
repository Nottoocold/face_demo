package cn.jiming.jdlearning.face;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;

import cn.jiming.jdlearning.face.detection.RetinaFaceDetection;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;

/**
 * 异步进行人脸识别，单线程循环执行，每次只接受一张img，识别完成后进行销毁，等待主线程提交下一张img
 * @author xiaoming
 */
public class FaceManager {
	private ExecutorService task_pool = Executors.newSingleThreadExecutor();
	
	private FaceNet faceNet = null;
	
	private boolean recogined = true;//已经识别完成，等待下一张img进行识别
	
	/**
	 * 指定模型文件构建face net
	 * @param faceNetModel
	 */
	public FaceManager(String faceNetModel) {
		faceNet = FaceNet.load(faceNetModel);
	}
	
	/**
	 * 单线程异步识别，识别结束后回调函数事件
	 * @param mat
	 * @param task
	 * @return true表示本次识别mat，false表示放弃本次识别
	 */
	public synchronized boolean asynPredictor(Mat src, Callback task) {
		List<FaceNet.MaxLab> labs = new ArrayList();
		if(!recogined) {
			//本次放弃识别，因为有在途识别任务
			return false;
		}
		
		task_pool.execute(new Runnable() {
			
			@Override
			public void run() {
				//开始识别
				Mat mat = src.clone();
				BufferedImage bimg = Java2DFrameUtils.toBufferedImage(mat);
				Image img = ImageFactory.getInstance().fromImage(bimg);
				recogined = false;
				
				//人脸定位
				List<Image> faces = RetinaFaceDetection.faceDetection(img, true, mat);//RetinaFace
				
				//特征提取
				if (faces != null && faces.size() > 0) {
					for (Image face : faces) {
						float[] f = FeatureExtraction.faceFeatureExtraction(face);
						if (f != null) {
							//lab推理
							FaceNet.MaxLab maxLab = faceNet.predict(f);
							maxLab.face = face;
							
							labs.add(maxLab);
						}
					}
				}
				
				//识别完成
				recogined = true;
				//回调任务
				if(task != null) {
					task.run(labs);
				}
				
				//释放资源
				mat.close();
			}
		});

		//本次执行识别
		return true;
	}
	
	/**
	 * 识别回调
	 * @author xiaoming
	 */
	public interface Callback{
		public void run(List<FaceNet.MaxLab> labs);
	}
}
