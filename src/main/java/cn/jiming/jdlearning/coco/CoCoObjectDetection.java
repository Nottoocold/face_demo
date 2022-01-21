package cn.jiming.jdlearning.coco;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Point2f;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import cn.jiming.jdlearning.utile.Comparison;
import cn.jiming.jdlearning.Object;
import cn.jiming.jdlearning.ObjectType;

import ai.djl.Application;
import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.translator.YoloTranslator;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.modality.cv.translator.YoloV5Translator.YoloOutputType;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;
import static org.bytedeco.opencv.global.opencv_imgproc.LINE_8;
import static org.bytedeco.opencv.global.opencv_imgproc.getRectSubPix;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

/**
 * coco数据yolov5 对象检测
 * @author xiaoming
 */
public class CoCoObjectDetection {
	private static String model_path = "models/coco.torchscript.pt";
	
	private static ZooModel<Image, DetectedObjects> model = null;
	
	static {
	       Translator<Image, DetectedObjects> translator = YoloV5Translator.builder().optSynsetArtifactName("coco.names").build();
	        Criteria<Image, DetectedObjects> criteria =
	                Criteria.builder()
	                        .setTypes(Image.class, DetectedObjects.class)
	                        .optModelUrls(model_path)
	                        .optModelName("coco.torchscript.pt")
	                        .optTranslator(translator)
	                        .optEngine("PyTorch")
	                        .build();
	        
	        try {
		        model = ModelZoo.loadModel(criteria);
		        System.out.println(model);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * coco数据集对象识别 接收640*640的图片
	 * @param img
	 * @param isDraw
	 * @return
	 */
	public static List<Object> detection(Mat img, boolean isDraw, double thresh) {
		List<Object> obs = new ArrayList<Object>();
		if(img == null || img.empty()) {
			return obs;
		}
		
		
		Mat src = new Mat();
		Size size = new Size(640, 640);
		resize(img, src, size);
		size.close();
		
		// 转换成BufferedImage
		OpenCVFrameConverter.ToMat matConv = new OpenCVFrameConverter.ToMat();
		Java2DFrameConverter biConv = new Java2DFrameConverter();
		Frame frame = matConv.convert(src);
		BufferedImage bimg = biConv.getBufferedImage(frame);

		// 释放
		frame.close();
		biConv.close();
		matConv.close();
		
		//转换Image
		Image djl_img = ImageFactory.getInstance().fromImage(bimg);
		
		//构建预测者
		Predictor<Image, DetectedObjects> predictor = model.newPredictor();
		
		try {
			//检测出全部对象
			DetectedObjects objects = predictor.predict(djl_img);
		    
			
			for (DetectedObject obj : objects.<DetectedObject>items()) {
				if (thresh < obj.getProbability()) {
					BoundingBox bbox = obj.getBoundingBox();
					Rectangle rectangle = bbox.getBounds();
					
					//coco对象
					ObjectType type = null;
					try {
						type = ObjectType.valueOf(obj.getClassName().trim().replace(" ", "").toUpperCase());
					} catch (Exception e) {
					}
					
					//只识别人
					if(type != null && ObjectType.PERSON == type) {
						Object o = new Object();
						o.setType(type);
						o.setProbability((float)obj.getProbability());
						o.setH((int) (rectangle.getHeight() * img.arrayHeight() / 640));
						o.setW((int) (rectangle.getWidth() * img.arrayWidth() / 640));
						o.setX((int) (rectangle.getX() * img.arrayWidth() / 640));//左上角的点
						o.setY((int) (rectangle.getY() * img.arrayHeight() / 640));//左上角的点
						
						//截取对象
		                Mat mat = new Mat();
		                Size  si = new Size(o.getW(), o.getH());
		                Point2f p = new Point2f(o.getX() + o.getW()/2, o.getY() + o.getH()/2);
						getRectSubPix(img, si, p, mat);
						o.setMat(mat);
						o.setData(Comparison.getData(mat));
						
						p.close();
						si.close();
						
						if(isDraw) {
							//在原图上画box
							String text = String.format("%s: %.2f", "xiaoming", obj.getProbability());
							
							Rect rect = new Rect();
							rect.x(o.getX());
							rect.y(o.getY());
							rect.width(o.getW());
							rect.height(o.getH());
							// 画框
							rectangle(img, rect, new Scalar(0, 255, 0, 0), 2, LINE_8, 0);// print blue rectangle
							// 画名字
							Point pt = new Point(rect.x(), rect.y());
							putText(img, text, pt, FONT_HERSHEY_PLAIN, 2, new Scalar(0, 0, 255, 0));
							
							pt.close();
							rect.close();
						}
						
						//保存对象
						obs.add(o);
					}
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		src.close();
		predictor.close();
		return obs;
	}
	
}
