package cn.jiming.jdlearning;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;


public class Object {
	private int id = -1;//物体对象的id
	
	private ObjectType type = ObjectType.FIRE;//火苗或者烟雾
	
	private float probability = 0;//相似度
	
	private Mat mat;//该对象的截图
	
	private float[] data;//对象截图的特征数组,用于判断对象和上一贞对象的关系
	
	private Face face;//人体对象上可能有人脸
	
	private Rect smokingRect;//吸烟框
	
	private boolean safeHat = false;//人体安全帽，对象是人体的话要么是true或者false
	
	private boolean smoking = false;//人体抽烟，默认是不吸烟的
	
	private int x = 0;//中心点x
	
	private int y = 0;//中心点y
	
	private int w = 0;//矩形宽度
	
	private int h = 0;//矩形高度
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ObjectType getType() {
		return type;
	}

	public void setType(ObjectType type) {
		this.type = type;
	}

	public float getProbability() {
		return probability;
	}

	public void setProbability(float probability) {
		this.probability = probability;
	}

	public Mat getMat() {
		return mat;
	}

	public void setMat(Mat mat) {
		this.mat = mat;
	}

	public float[] getData() {
		return data;
	}

	public void setData(float[] data) {
		this.data = data;
	}

	public Face getFace() {
		return face;
	}

	public void setFace(Face face) {
		this.face = face;
	}

	public Rect getSmokingRect() {
		return smokingRect;
	}

	public void setSmokingRect(Rect smokingRect) {
		this.smokingRect = smokingRect;
	}

	public boolean isSafeHat() {
		return safeHat;
	}

	public void setSafeHat(boolean safeHat) {
		this.safeHat = safeHat;
	}

	public boolean isSmoking() {
		return smoking;
	}

	public void setSmoking(boolean smoking) {
		this.smoking = smoking;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	/**
	 * 释放mat资源
	 */
	public void close() {
		if(this.mat != null) {
			try {
				this.mat.close();
			} catch (Exception e) {
			}
		}
		if(this.face != null) {
			try {
				this.face.close();
			} catch (Exception e) {
			}
		}
	}
	
}
