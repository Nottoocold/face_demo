package cn.jiming.jdlearning.face.detection;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 人脸检测模型
 */
public final class RetinaFaceDetection {

    private static final Logger logger = LoggerFactory.getLogger(RetinaFaceDetection.class);
    
    private static ZooModel<Image, DetectedObjects> model = null;
    
    private static String model_path = "models/retinaface.zip";
    
    private static double confThresh = 0.85f;
    

    private RetinaFaceDetection() {}


    static {
    	/**
    	 * 初始化人脸检测模型
    	 */
        double nmsThresh = 0.45f;
        double[] variance = {0.1f, 0.2f};
        int topK = 5000;
        int[][] scales = {{16, 32}, {64, 128}, {256, 512}};
        int[] steps = {8, 16, 32};
        FaceDetectionTranslator translator =
                new FaceDetectionTranslator(confThresh, nmsThresh, variance, topK, scales, steps);

        Criteria<Image, DetectedObjects> criteria =
                Criteria.builder()
                        .setTypes(Image.class, DetectedObjects.class)
                        .optModelUrls(model_path)
                        // Load model from local file, e.g:
                        .optModelName("retinaface") // specify model file prefix
                        .optTranslator(translator)
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
     * 人脸检测
     * @param img
     * @return 返回人脸矩形，高宽等比例的截图
     */
    public static List<Image> faceDetection(Image img, boolean isrect, Mat mat_img) {
    	List<Image> faces = new ArrayList();
		Predictor<Image, DetectedObjects> predictor = model.newPredictor();
		try {
			DetectedObjects detection = predictor.predict(img);
			
			for(int i = 0; i < detection.getNumberOfObjects(); i++) {
				DetectedObject box = detection.item(i);
				double similar = box.getProbability();
//				System.out.println("检测到人脸，相似度=" + similar);
				double x = img.getWidth() * box.getBoundingBox().getBounds().getX();
				double y = img.getHeight() * box.getBoundingBox().getBounds().getY();
				double w = img.getWidth() * box.getBoundingBox().getBounds().getWidth();
				double h = img.getHeight() * box.getBoundingBox().getBounds().getHeight();
				
				if(w != h) {
					//需要做等比例
					if(h < w) {
						double xd = (h - w) / 2;
						w = h;
						x = x - xd;
					}else {
						double yd = w - h;
						h = w;
						y = y - yd;
					}
				}
				
				
				//截取人脸区域
				if(mat_img != null) {
					//mat上截取人脸
	                Mat face = new Mat();
	                double size = w>h?w:h;
	                getRectSubPix(mat_img, new Size((int)size, (int)size), new Point2f((int) (x + w/2), (int) (y + h/2)), face);
	                BufferedImage bimg = Java2DFrameUtils.toBufferedImage(face);
	    			//转DJL img
	    			Image face_img = ImageFactory.getInstance().fromImage(bimg);
	                faces.add(face_img);
	                face.close();
				}else {
					try {
						Image face = img.getSubimage((int)x, (int)y, (int)w, (int)h);
						faces.add(face);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
			
			//最后在画人脸矩形
			if(isrect) {
				img.drawBoundingBoxes(detection);
			}
		} catch (TranslateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//关闭预测者
		predictor.close();
		
		return faces;
    }
    
    
    /**
     * 人脸检测
     * @param img
     * @return 返回人脸矩形，高宽等比例的截图
     */
    public static List<Mat> faceDetection(Mat mat_img) {
    	// 转换成BufferedImage
    	BufferedImage bimg = Java2DFrameUtils.toBufferedImage(mat_img);

    	// 转换Image
    	Image djl_img = ImageFactory.getInstance().fromImage(bimg);
    	
    	List<Mat> faces = new ArrayList();
		Predictor<Image, DetectedObjects> predictor = model.newPredictor();
		try {
			DetectedObjects detection = predictor.predict(djl_img);
			
			for(int i = 0; i < detection.getNumberOfObjects(); i++) {
				DetectedObject box = detection.item(i);
				double similar = box.getProbability();
				double x = djl_img.getWidth() * box.getBoundingBox().getBounds().getX();
				double y = djl_img.getHeight() * box.getBoundingBox().getBounds().getY();
				double w = djl_img.getWidth() * box.getBoundingBox().getBounds().getWidth();
				double h = djl_img.getHeight() * box.getBoundingBox().getBounds().getHeight();
				
				if(w != h) {
					//需要做等比例
					if(h > w) {
						double xd = (h - w) / 2;
						w = h;
						x = x - xd;
					}else {
						double yd = w - h;
						h = w;
						y = y - yd;
					}
				}
				
				
				//截取人脸区域
				if(mat_img != null) {
					//mat上截取人脸
	                Mat face = new Mat();
	                double size = w>h?w:h;
	                getRectSubPix(mat_img, new Size((int)size, (int)size), new Point2f((int) (x + w/2), (int) (y + h/2)), face);
	                faces.add(face);
				}
				
			}
			
		} catch (TranslateException e) {
			e.printStackTrace();
		}
		
		//关闭预测者
		predictor.close();
		
		return faces;
    }

}
