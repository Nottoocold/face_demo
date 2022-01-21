package cn.jiming.jdlearning.action;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;

/**
 * 人体动作姿态检测
 * @author xiaoming
 */
public class HumanActionDetection {
	private static ZooModel<Image, NDArray> action_model = null;
	
	static {
		String modelUrl = "models/checkpoint_iter_370000.onnx";
		Criteria<Image, NDArray> criteria = Criteria.builder()
		        .setTypes(Image.class, NDArray.class)
		        .optModelUrls(modelUrl)
		        .optModelName("checkpoint_iter_370000")
		        .optTranslator(new HumanActionDetectionTranslator())
		        .optEngine("PyTorch") // use Pytorch engine by default
		        .build();
		try {
			action_model = ModelZoo.loadModel(criteria);
			System.out.println(action_model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		Path human_p = Paths.get("img/1.jpg");
        Image img = ImageFactory.getInstance().fromFile(human_p);
        
		Predictor<Image, NDArray> predictor = action_model.newPredictor();
		predictor.predict(img);
		
		
	}
}
