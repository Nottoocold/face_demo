package cn.jiming.jdlearning.face.feature;

import ai.djl.MalformedModelException;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import cn.jiming.jdlearning.face.comparison.FeatureComparison;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 人脸特征提取返回NDArray
 * @author xiaoming
 */
public final class FeatureNDArrayExtraction {

    private static final Logger logger = LoggerFactory.getLogger(FeatureNDArrayExtraction.class);

    private static ZooModel<Image, NDArray> model = null;
    
    private static String model_path = "models/face_feature.zip";
    
    private FeatureNDArrayExtraction() {}


    static {
    	/**
    	 * 初始化模型
    	 */
    	Criteria<Image, NDArray> criteria =
                Criteria.builder()
                        .setTypes(Image.class, NDArray.class)
                        .optModelUrls(model_path)
                        .optModelName("face_feature") // specify model file prefix
                        .optTranslator(new FaceFeatureNDArrayTranslator())
                        .optProgress(new ProgressBar())
                        .optEngine("PyTorch") // Use PyTorch engine
                        .build();
    	try {
			model = ModelZoo.loadModel(criteria);
		} catch (ModelNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 人脸特征提取
     * @param img 人脸区域图,建议w h 等比例的灰度图
     * @return 长度为512的特征一维数组
     * @throws IOException
     * @throws ModelException
     * @throws TranslateException
     */
	public static NDArray faceFeatureExtraction(Image img) {
		img.getWrappedImage();

		Predictor<Image, NDArray> predictor = model.newPredictor();
		NDArray faceFeature = null;
		try {
			faceFeature = predictor.predict(img);
		} catch (TranslateException e) {
			e.printStackTrace();
		}finally {
			//关闭预测者
			predictor.close();
		}
		
		return faceFeature;
	}


}
