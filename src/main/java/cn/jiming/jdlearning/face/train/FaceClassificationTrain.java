package cn.jiming.jdlearning.face.train;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import cn.jiming.jdlearning.face.FaceNet;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;

/**
 * 人脸分类训练,得出facenet模型网络
 * @author xiaoming
 */
public class FaceClassificationTrain {
	
//	private static ExecutorService pool = Executors.newFixedThreadPool(20);
	
	/**
	 * 训练
	 * @param model 可以在此模型文件基础上迭代训练
	 * @param trainPath 人脸训练数据根文件夹
	 * @param faceNetPath 训练完成后的face net存储文件路径
	 */
	public synchronized static void training(String model, String trainPath, String faceNetPath) {
		FaceNet faceNet = null;
		if(model == null) {
			faceNet = new FaceNet();
		}else {
			faceNet = FaceNet.load(model);
		}
		
		dataSet(trainPath, faceNet);
		
		//等待线程池任务完成后再保存网络
//		pool.shutdown();//有序关闭，等待所有任务完成后关闭线程池
//		while(!pool.isTerminated()) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		//保存网络模型
		faceNet.saveNet(faceNetPath);
	}



	private static void dataSet(String trainPath, FaceNet faceNet) {
		int i = 0;
		File data = new File(trainPath);
		if(data.isDirectory()) {
			File[] labs = data.listFiles();
			if(labs != null && labs.length > 0) {
				for(File lab:labs) {
					if(lab.isDirectory()) {
						i++;
						String labName = lab.getName();
						System.out.println("labName=" + labName + "  i=" + i);
						loadData(faceNet, lab, labName);
						
//						pool.execute(new Runnable() {
//							
//							@Override
//							public void run() {
//								loadData(faceNet, lab, labName);
//							}
//						});
						
					}
				}
			}else {
				new TrainFaceClassificationException("Path:" + trainPath + " is not empty.");
			}
		}else {
			new TrainFaceClassificationException("Path:" + trainPath + " is not data directory.");
		}
	}



	private static void loadData(FaceNet faceNet, File lab, String labName) {
		File[] faces = lab.listFiles();
		if(faces != null && faces.length > 0) {
			for(File faceF:faces) {
				Path facePath = Paths.get(faceF.getAbsolutePath());
				try {
					Image img = ImageFactory.getInstance().fromFile(facePath);
					float[] feature = FeatureExtraction.faceFeatureExtraction(img);
					faceNet.addFeature(labName, feature);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	
	
}
