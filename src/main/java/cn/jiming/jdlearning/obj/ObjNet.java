package cn.jiming.jdlearning.obj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jiming.jdlearning.face.comparison.FeatureComparison;

import ai.djl.modality.cv.Image;

/**
 * 对象分类网络
 * @author xiaoming
 */
public class ObjNet  implements Serializable{
private static final long serialVersionUID = 8778867996150023000L;
	
	private int feature_count = 0;
	
	/**
	 * 缓冲着所有分类lab的特征数据
	 */
	private HashMap<String, List<float[]>> models = new HashMap<String, List<float[]>>();
	
	/**
	 * 添加一个特征值
	 * @param lab
	 * @param feature
	 */
	public synchronized void addFeature(String lab, float[] feature) {
		List<float[]> features = models.get(lab);
		if(features == null) {
			features = new ArrayList<float[]>();
			models.put(lab, features);
		}
		
		feature_count++;
		features.add(feature);
	}
	
	/**
	 * 推理获取最大的lab和最大相似度
	 * @param feature
	 * @return
	 */
	public MaxLab predict(float[] feature) {
		MaxLab maxLab = new MaxLab();
		models.forEach((lab, features)->{
			//在改lab下全部对比一遍
			float maxSimilar = 0;
			if(features != null && features.size() > 0) {
				int min = 20;//最多比较10个样本，如果都小于0.85的不用再比较当前lab
				if(features.size() < min) {
					min = features.size();
				}
				
				for(int i = 0; i < features.size(); i++) {
					float[] tem = features.get(i);
					float temSimilar = FeatureComparison.calculSimilar(feature, tem);
					if(temSimilar > maxSimilar) {
						maxSimilar = temSimilar;
					}
				}
			}
			
			//本次lab对比是否是最大值？
			if(maxSimilar > maxLab.maxSimilar) {
				maxLab.maxSimilar = maxSimilar;
				maxLab.maxLab = lab;
			}
		});
		
		return maxLab;
	}
	
	/**
	 * 加载磁盘上的网络对象
	 * @param name
	 * @return
	 */
	public static ObjNet load(String name) {
		File file = new File(name);
        ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(file));
			ObjNet net = (ObjNet)ois.readObject();
			
			return net;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
		return null;
	}
	
	/**
	 * 保存网络到磁盘上
	 * @param name
	 */
	public void saveNet(String name) {
		ObjectOutputStream oos = null;
        try {
        	oos = new ObjectOutputStream(new FileOutputStream(name));
			oos.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @author xiaoming
	 */
	public class MaxLab {
		public String maxLab = null; //最大相似的标签
		public float maxSimilar = 0; //最大的相似度比率 1==100%
	}


	/**
	 * 网络标签总数
	 * @return
	 */
	public int labs() {
		return models.size();
	}

	/**
	 * 样本总数
	 * @return
	 */
	public int features() {
		return feature_count;
	}
}
