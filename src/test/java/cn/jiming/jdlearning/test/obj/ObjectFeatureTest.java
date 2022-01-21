package cn.jiming.jdlearning.test.obj;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import java.io.File;

import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.Test;

import cn.jiming.jdlearning.face.comparison.FeatureComparison;
import cn.jiming.jdlearning.face.feature.FeatureExtraction;
import cn.jiming.jdlearning.obj.ObjNet;
import cn.jiming.jdlearning.obj.ObjNet.MaxLab;


public class ObjectFeatureTest {
    
	@Test
	public void test() {
//		Mat src1 = imread("img/obj/1.jpg");
//		Mat src2 = imread("img/obj/3.jpg");
//		
//		float[] f1 = FeatureExtraction.faceFeatureExtraction(src1);
//		float[] f2 = FeatureExtraction.faceFeatureExtraction(src2);
//		
//		
//		//推理
//		ObjNet objNet = ObjNet.load("models/mnist.xm");
//		MaxLab maxLab = objNet.predict(f1);
//		
//		System.out.println("相似度=" + maxLab.maxSimilar + "  lab=" + maxLab.maxLab);
	}
	
	
	@Test
	public void test2() {
//		String root = "E:\\word-discern\\train\\test_img";
//		ObjNet objNet = ObjNet.load("models/mnist.xm");
//		
//		Count count = new Count();
//		
//		File rootP = new File(root);
//		if(rootP.isDirectory()) {
//			File[] fs = rootP.listFiles();
//			if(fs != null && fs.length > 0) {
//				for(File testf:fs) {
//					String lab = testf.getName();
//					System.out.println("Start test lab:" + lab);
//					
//					testLab(objNet, root, lab, count);
//					
//					System.out.println("End test lab:" + lab);
//				}
//			}
//			
//		}
//		
//		
//		/**
//		 * 统计结果
//		 */
//		double size = count.count_e + count.count_r;
//		double val = count.count_r / size;
//		System.out.println("******** 最终结果 ********");
//		System.out.println("准确率：" + val + "  正确:" + count.count_r + "  错误:" + count.count_e);
//		System.out.println("******** 最终结果 ********");
	}


	private void testLab(ObjNet objNet, String root, String lab, Count count) {
		/**
		 * 测试
		 */
		if(lab == null) {
			lab = "5";
		}
		
		File testP = new File(root + "\\" + lab);
		
		
		int r = 0;
		int e = 0;
		if(testP.isDirectory()) {
			File[] fs = testP.listFiles();
			if(fs != null && fs.length > 0) {
				for(File f:fs) {
					Mat src = imread(f.getAbsolutePath());
					if(src != null && !src.empty()) {
						float[] ft = FeatureExtraction.faceFeatureExtraction(src);
						MaxLab maxLab = objNet.predict(ft);
						if(lab.equals(maxLab.maxLab)) {
							//正确
							r++;
						}else {
							//错误
							e++;
//							System.out.println("错误：lab:" + maxLab.maxLab + "  val:" + maxLab.maxSimilar + "  src:" + f.getAbsolutePath());
						}
						
					}
				}
			}
		}
		
		/**
		 * 统计结果
		 */
		count.count_r = count.count_r + r;
		count.count_e = count.count_e + e;
		double size = r + e;
		double val = r / size;
		System.out.println("准确率：" + val + "  正确:" + r + "  错误:" + e + "  lab:" + lab);
	}
	
	class Count{
		Integer count_r = 0;
		Integer count_e = 0;
	}
}
