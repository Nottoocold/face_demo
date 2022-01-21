package cn.jiming.jdlearning.utile;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

import cn.jiming.jdlearning.face.comparison.FeatureComparison;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;

/**
 * 数据集相似度对比
 * 
 * @author xiaoming
 */
public class Comparison {
	private static int size = 100;
	
	private static int dist = 3000000;//一定距离内认为相似

	/**
	 * 对比两数据集相似度
	 * @param feature1
	 * @param feature2
	 * @return
	 */
	public static float calculSimilar(float[] feature1, float[] feature2) {
		//像素对比
//		float size = feature1.length;
//		float count = 0;
//		for (int i = 0; i < size; ++i) {
//			int tem = feature1[i] - feature2[i];
//			if((tem <= dist) && (tem >= -dist)) {
//				count++;
//			}
//			
//		}
//		System.out.println("count=" + count + " size=" + size);
//		return new BigDecimal(count / size).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		//consi对比
		float v = FeatureComparison.calculSimilar(feature1, feature2);
		return v;
	}
	
	/**
	 * 提取mat上固定宽度的特征数组
	 * @param mat
	 * @return
	 */
	public static float[] getData(Mat mat) {
		//像素提取方式
//		Mat o_d = new Mat();
//		resize(mat, o_d, new Size(size, size));
//		BufferedImage b1 = Java2DFrameUtils.toBufferedImage(o_d);
//		int[] data = new int[size * size];
//		b1.getRGB(0, 0, size, size, data, 0, size);
//		
//		o_d.close();
		
		//ObjNet提取方式
		float[] data = FeatureExtraction.faceFeatureExtraction(mat);
		
		return data;
	}
	
	
	/**
	 * 计算两点之间的距离
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getDist(double x1, double y1, double x2, double y2) {
    	double dx = Math.abs(x2 - x1);
    	double dy = Math.abs(y2 - y1);
    	return Math.sqrt(dx * dx + dy * dy);
	}

//	public static void main(String[] args) throws Exception {
//		
//		
//		BufferedImage  m1 = ImageIO.read(new File("build//output//2.png"));
//		BufferedImage  m2 = ImageIO.read(new File("build//output//1.png"));
//		
//		Mat mat1 = Java2DFrameUtils.toMat(m1);
//		Mat mat2 = Java2DFrameUtils.toMat(m2);
//		
//		resize(mat1, mat1, new Size(size, size));
//		resize(mat2, mat2, new Size(size, size));
//		
//		BufferedImage b1 = Java2DFrameUtils.toBufferedImage(mat1);
//		BufferedImage b2 = Java2DFrameUtils.toBufferedImage(mat2);
//		
//		int[] data1 = new int[size * size];
//		b1.getRGB(0, 0, size, size, data1, 0, size);
//		System.out.println(data1[0]);
//		
//		int[] data2 = new int[size * size];
//		b2.getRGB(0, 0, size, size, data2, 0, size);
//		System.out.println(data2[0]);
//		
//		double similar = Comparison.calculSimilar(data1, data2);
//		System.out.println("相似度=" + similar);
//		
//		
//		/**
//		 * 计算两点之间的距离
//		 */
//        double x1 = 0;
//        double y1 = 1;
//        
//        double x2 = 1;
//        double y2 = 0;
//        
//    	double dx=Math.abs(x2 - x1);
//    	double dy=Math.abs(y2 - y1);
//    	double d= Math.sqrt(dx * dx + dy * dy);
//
//        System.out.println(d);
//        
//	}
}
