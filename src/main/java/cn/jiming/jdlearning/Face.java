package cn.jiming.jdlearning;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

public class Face {
	private Mat face;//如果是人体的话，可能检测出人脸
	
	private Rect faceRect;//人脸框
	
	private float[] faceFeature;//人脸特征

	public Mat getFace() {
		return face;
	}

	public void setFace(Mat face) {
		this.face = face;
	}

	public Rect getFaceRect() {
		return faceRect;
	}

	public void setFaceRect(Rect faceRect) {
		this.faceRect = faceRect;
	}

	public float[] getFaceFeature() {
		return faceFeature;
	}

	public void setFaceFeature(float[] faceFeature) {
		this.faceFeature = faceFeature;
	}
	
	/**
	 * 释放mat资源
	 */
	public void close() {
		if(this.face != null) {
			try {
				this.face.close();
			} catch (Exception e) {
			}
		}
		if(this.faceRect != null) {
			try {
				this.faceRect.close();
			} catch (Exception e) {
			}
		}
	}
}
