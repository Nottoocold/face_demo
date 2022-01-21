package cn.jiming.jdlearning.test.face;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import cn.jiming.jdlearning.face.comparison.FeatureComparison;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

/**
 * 特征相似度对比测试
 * @author xiaoming
 *
 */
public class FeatureComparisonTest {
	/**
	 * 两数组对比相似度
	 */
	@Test
	public void dataTest() {
    	float[] d1 = new float[]{1F, 2F, 3F};
    	float[] d2 = new float[]{10F, 20F, 2F};
    	
    	//NDArray 测试
    	NDManager ndManager = NDManager.newBaseManager();
    	NDArray f1 = ndManager.create(d1);
    	NDArray f2 = ndManager.create(d2);
    	
    	float similar1 = FeatureComparison.ndarrayCalculSimilar(f1, f2);
    	ndManager.close();
    	System.out.println("NDArray相似度=" + similar1);
    	
    	
    	//cpu版本测试
    	float similar = FeatureComparison.calculSimilar(d1, d2);
    	System.out.println("cpu相似度=" + similar);
    	
	}
	
	/**
	 * 人脸特征提取后对比相速度
	 */
	@Test
	public void faceFeatureTest() {
		try {
			Path imageFile1 = Paths.get("img/face/train/xiaoming/0.png");
			Image img1 = ImageFactory.getInstance().fromFile(imageFile1);
			Path imageFile2 = Paths.get("img/face/train/xiaoming/1.png");
			Image img2 = ImageFactory.getInstance().fromFile(imageFile2);

			float[] feature1 = FeatureExtraction.faceFeatureExtraction(img1);
			float[] feature2 = FeatureExtraction.faceFeatureExtraction(img2);

			System.out.println(Float.toString(FeatureComparison.calculSimilar(feature1, feature2)));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
