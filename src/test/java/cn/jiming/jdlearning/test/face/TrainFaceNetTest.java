package cn.jiming.jdlearning.test.face;

import cn.jiming.jdlearning.face.train.FaceClassificationTrain;
import org.junit.Test;
/**
 * 训练facenet
 * @author xiaoming
 */
public class TrainFaceNetTest {

	@Test
	public void train() {
		String trainPath = "img/face/train";
		String faceNetPath = "models/face_net.xm";
		
		//开始训练
		System.out.println("***** 开始训练 ***** ");
		FaceClassificationTrain.training(null , trainPath, faceNetPath);
		System.out.println("***** 训练完成 ***** ");
	}
}
