package cn.jiming.jdlearning.face.comparison;

import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.TranslateException;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 欧式距离比较,得出百分比相似度
 * @author xiaoming
 */
public final class FeatureComparison {

    private static final Logger logger = LoggerFactory.getLogger(FeatureComparison.class);

    private FeatureComparison() {}

    /**
     * cosin相似度比较(cpu版本)
     * @param feature1
     * @param feature2
     * @return 返回相似度比例
     */
    public static float calculSimilar(float[] feature1, float[] feature2) {
        float ret = 0.0f;
        float mod1 = 0.0f;
        float mod2 = 0.0f;
        int length = feature1.length;
        for (int i = 0; i < length; ++i) {
            ret += feature1[i] * feature2[i];
            mod1 += feature1[i] * feature1[i];
            mod2 += feature2[i] * feature2[i];
        }
        return (float) ((ret / Math.sqrt(mod1) / Math.sqrt(mod2) + 1) / 2.0f);
    }
    
    /**
     * cosin相似度比较(NDArray版本)
     * @param feature1
     * @param feature2
     * @return
     */
    public static float ndarrayCalculSimilar(NDArray feature1, NDArray feature2) {
    	NDArray ret_nd = feature1.mul(feature2);
    	NDArray mod1_nd = feature1.mul(feature1);
    	NDArray mod2_nd = feature2.mul(feature2);
    	
    	float ret = ret_nd.sum().toFloatArray()[0];
    	float mod1 = mod1_nd.sum().toFloatArray()[0];
        float mod2 = mod2_nd.sum().toFloatArray()[0];
        
    	return (float) ((ret / Math.sqrt(mod1) / Math.sqrt(mod2) + 1) / 2.0f);
    }
    

}
