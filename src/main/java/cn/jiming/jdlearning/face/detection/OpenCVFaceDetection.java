package cn.jiming.jdlearning.face.detection;

import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.opencv.global.opencv_dnn;


import org.bytedeco.opencv.opencv_dnn.*;


import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import cn.jiming.jdlearning.Face;

import org.bytedeco.opencv.opencv_core.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class OpenCVFaceDetection {
	private static String prototxt = "models/winder_face_1.pbtxt";
	private static String model = "models/winder_face_1.pb";
	private static Net dnnNet = opencv_dnn.readNetFromTensorflow(model, prototxt);
	private static double threshold = 0.85;
	private static int n = 0;
	
	static {
		//计算后台
	    dnnNet.setPreferableBackend(opencv_dnn.DNN_BACKEND_OPENCV);
		dnnNet.setPreferableTarget(opencv_dnn.DNN_TARGET_CPU);
		
	}
	
	/**
	 * opencv dnn人脸检测
	 */
	public synchronized static List<Mat> detectionToMat(Mat src, boolean isrect) {
		List<Mat> faces = new ArrayList();
		Mat blob = opencv_dnn.blobFromImage(src, 1.0, new Size(300, 300), new Scalar(), true, false, CV_32F);
		
		dnnNet.setInput(blob);
		Mat detection = dnnNet.forward();
		if( detection.empty() ) {
            return faces;
        }
		
		//解析结果  找出具体位置  可能有重复的rect，需要做NMS非最大抑制
		Mat ne = new Mat(new Size(detection.size(3), detection.size(2)), CV_32F, detection.ptr(0, 0));//extract a 2d matrix for 4d output matrix with form of (number of detections x 7)
		

        for (int i = 0; i < ne.rows(); i++) {//iterate to extract elements
        	float lab = ne.ptr(i, 1).getFloat();//srcIndexer.get(i, 1);
            float confidence = ne.ptr(i, 2).getFloat();//srcIndexer.get(i, 2);
            float f1 = ne.ptr(i, 3).getFloat();
            float f2 = ne.ptr(i, 4).getFloat();
            float f3 = ne.ptr(i, 5).getFloat();
            float f4 = ne.ptr(i, 6).getFloat();
            
            if (confidence > threshold) {
                float tx = f1 * src.cols();//top left point's x
                float ty = f2 * src.rows();//top left point's y
                float bx = f3 * src.cols();//bottom right point's x
                float by = f4 * src.rows();//bottom right point's y
                float w = bx - tx;
                float h = by - ty;

                //截取人脸
                try {
                	Mat face = new Mat();
                    float size = w>h?h:w;
                    getRectSubPix(src, new Size((int)size, (int)size), new Point2f((int) (tx + bx)/2, (int) (ty + by)/2), face);
                    
                    faces.add(face);
				} catch (Exception e) {
				}
                
                
                //在mat上画出人脸矩形
				if (isrect) {
					rectangle(src, new Rect(new Point((int) tx, (int) ty), new Point((int) bx, (int) by)),
							new Scalar(0, 255, 0, 0), 2, LINE_8, 0);// print blue rectangle
				}
                
            }
        }
        
        
        detection.close();
        blob.close();
        ne.close();
        
        return faces;
	}
	
	/**
	 * opencv dnn人脸检测
	 */
	public static List<Image> detection(Mat src, boolean isrect) {
		List<Image> faces = new ArrayList();
		Mat blob = opencv_dnn.blobFromImage(src, 1.0, new Size(300, 300), new Scalar(), true, false, CV_32F);
		
		dnnNet.setInput(blob);
		Mat detection = dnnNet.forward();
		if( detection.empty() ) {
            return faces;
        }
		
		//解析结果  找出具体位置  可能有重复的rect，需要做NMS非最大抑制
		Mat ne = new Mat(new Size(detection.size(3), detection.size(2)), CV_32F, detection.ptr(0, 0));//extract a 2d matrix for 4d output matrix with form of (number of detections x 7)
		

        for (int i = 0; i < ne.rows(); i++) {//iterate to extract elements
        	float lab = ne.ptr(i, 1).getFloat();//srcIndexer.get(i, 1);
            float confidence = ne.ptr(i, 2).getFloat();//srcIndexer.get(i, 2);
            float f1 = ne.ptr(i, 3).getFloat();
            float f2 = ne.ptr(i, 4).getFloat();
            float f3 = ne.ptr(i, 5).getFloat();
            float f4 = ne.ptr(i, 6).getFloat();
            
            if (confidence > threshold) {
                float tx = f1 * src.cols();//top left point's x
                float ty = f2 * src.rows();//top left point's y
                float bx = f3 * src.cols();//bottom right point's x
                float by = f4 * src.rows();//bottom right point's y
                float w = bx - tx;
                float h = by - ty;

                //截取人脸
                try {
                	Mat face = new Mat();
                    float size = w>h?w:h;
                    getRectSubPix(src, new Size((int)size, (int)size), new Point2f((int) (tx + bx)/2, (int) (ty + by)/2), face);
                    BufferedImage bimg = Java2DFrameUtils.toBufferedImage(face);
                    
        			//转DJL img
        			Image img = ImageFactory.getInstance().fromImage(bimg);
                    faces.add(img);
				} catch (Exception e) {
				}
                
                
                //在mat上画出人脸矩形
				if (isrect) {
					rectangle(src, new Rect(new Point((int) tx, (int) ty), new Point((int) bx, (int) by)),
							new Scalar(0, 255, 0, 0), 2, LINE_8, 0);// print blue rectangle
				}
                
            }
        }
        
        
        detection.close();
        blob.close();
        ne.close();
        
        return faces;
	}
	
	/**
	 * 
	 * @param src
	 * @param isrect
	 * @return
	 */
	public static List<Face> detection(Mat src) {
		List<Face> faces = new ArrayList();
		Mat blob = opencv_dnn.blobFromImage(src, 1.0, new Size(300, 300), new Scalar(), true, false, CV_32F);
		
		dnnNet.setInput(blob);
		Mat detection = dnnNet.forward();
		if( detection.empty() ) {
            return faces;
        }
		
		//解析结果  找出具体位置  可能有重复的rect，需要做NMS非最大抑制
		Size si = new Size(detection.size(3), detection.size(2));
		Mat ne = new Mat(si, CV_32F, detection.ptr(0, 0));//extract a 2d matrix for 4d output matrix with form of (number of detections x 7)
		
		si.close();

        for (int i = 0; i < ne.rows(); i++) {//iterate to extract elements
        	float lab = ne.ptr(i, 1).getFloat();//srcIndexer.get(i, 1);
            float confidence = ne.ptr(i, 2).getFloat();//srcIndexer.get(i, 2);
            float f1 = ne.ptr(i, 3).getFloat();
            float f2 = ne.ptr(i, 4).getFloat();
            float f3 = ne.ptr(i, 5).getFloat();
            float f4 = ne.ptr(i, 6).getFloat();
            
            if (confidence > threshold) {
                float tx = f1 * src.cols();//top left point's x
                float ty = f2 * src.rows();//top left point's y
                float bx = f3 * src.cols();//bottom right point's x
                float by = f4 * src.rows();//bottom right point's y
                float w = bx - tx;
                float h = by - ty;
                

                //截取人脸
                float size = w>h?w:h;
                Rect faceR = new Rect();
                faceR.width((int)size);
                faceR.height((int)size);
                faceR.x((int) (tx + bx)/2);
                faceR.y((int) (ty + by)/2);
                
                Face face = new Face();
                Mat faceM = new Mat();
                try {
                	Size si2 = new Size(faceR.width(), faceR.height());
                	Point2f p2 = new Point2f(faceR.x(), faceR.y());
                	getRectSubPix(src, si2, p2, faceM);
                	si2.close();
                	p2.close();
                	
                    face.setFace(faceM);
                    face.setFaceRect(faceR);
                    faces.add(face);
				} catch (Exception e) {
					face.close();
					faceM.close();
					faceR.close();
				}
                
                
            }
        }
        
        
        detection.close();
        blob.close();
        ne.close();
        
        return faces;
	}



}
