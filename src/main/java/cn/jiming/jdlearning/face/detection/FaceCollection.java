package cn.jiming.jdlearning.face.detection;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.Mat;


import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;

/**
 * 人脸地底库提取，并且保存到训练目录中，为facenet模型训练准备
 * @author xiaoming
 */
public class FaceCollection {

	
	/**
	 * 打开本地摄像头采集人脸，采集50张后停止
	 * @param lab_src
	 * @throws Exception
	 */
	public static boolean collectionDnn(String lab_src) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		CanvasFrame canvas = new CanvasFrame("采集人脸");//新建一个窗口
		try {
	        grabber.start();   //开始获取摄像头数据
//	        canvas.setSize(500, 500);
//	        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        canvas.setAlwaysOnTop(true);
	        int i = 0;
	        while(true){
	            if(!canvas.isDisplayable()){//窗口是否关闭
	                grabber.stop();//停止抓取
	                grabber.close();
	                break;
	            }    
	            grabber.flush();
	            Frame frame = grabber.grab();
	            // 将Frame转为Mat
	    		OpenCVFrameConverter.ToMat   matConv = new OpenCVFrameConverter.ToMat();
				Mat mat  = matConv.convertToMat(frame);
				
				//释放
				frame.close();
				matConv.close();
	    		
	    		//人脸定位
				List<Image> faces = OpenCVFaceDetection.detection(mat, true);
	    		
				//保存人脸数据
				Path outputDir = Paths.get(lab_src);
				for(Image face:faces) {
					Path imagePath = outputDir.resolve(i + ".png");
		        	try {
						face.save(Files.newOutputStream(imagePath), "png");
					} catch (IOException e) {
						e.printStackTrace();
					}
		        	i++;
				}
				
	    		
				
				//Mat转Frame
				OpenCVFrameConverter.ToMat  matConv2 = new OpenCVFrameConverter.ToMat();
				Java2DFrameConverter  biConv  = new Java2DFrameConverter();
				Frame frame2 = matConv2.convert(mat);
				
				//释放
				biConv.close();
				matConv2.close();
	    		
				//显示
	            canvas.showImage(frame2);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
	           
	            //释放
	            mat.release();;
	            frame2.close();
	            
	            if(i > 50) {
	            	break;
	            }
	            
	            try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
				}
	        }
	        
	        if(i > 50) {
	        	 return true;
            }else {
            	 return false;//人脸不够50可能被中途关闭
            }
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}finally {
			//结束抓拍
			canvas.setVisible(false);
			try {
				grabber.stop();
				grabber.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 打开本地摄像头采集人脸，采集size张后停止
	 * @param lab_src
	 * @throws Exception
	 */
	public static boolean collectionDnn(String lab_src, int size) {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		CanvasFrame canvas = new CanvasFrame("采集人脸");//新建一个窗口
		try {
	        grabber.start();   //开始获取摄像头数据
//	        canvas.setSize(500, 500);
//	        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        canvas.setAlwaysOnTop(true);
	        int i = 0;
	        while(true){
	            if(!canvas.isDisplayable()){//窗口是否关闭
	                grabber.stop();//停止抓取
	                grabber.close();
	                break;
	            }    
	            grabber.flush();
	            Frame frame = grabber.grab();
	            // 将Frame转为Mat
	    		OpenCVFrameConverter.ToMat   matConv = new OpenCVFrameConverter.ToMat();
				Mat mat  = matConv.convertToMat(frame);
				
				//释放
				frame.close();
				matConv.close();
	    		
	    		//人脸定位
				List<Image> faces = OpenCVFaceDetection.detection(mat, true);
	    		
				//保存人脸数据
				Path outputDir = Paths.get(lab_src);
				for(Image face:faces) {
					Path imagePath = outputDir.resolve(i + ".png");
		        	try {
						face.save(Files.newOutputStream(imagePath), "png");
					} catch (IOException e) {
						e.printStackTrace();
					}
		        	i++;
				}
				
	    		
				
				//Mat转Frame
				OpenCVFrameConverter.ToMat  matConv2 = new OpenCVFrameConverter.ToMat();
				Java2DFrameConverter  biConv  = new Java2DFrameConverter();
				Frame frame2 = matConv2.convert(mat);
				
				//释放
				biConv.close();
				matConv2.close();
	    		
				//显示
	            canvas.showImage(frame2);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
	           
	            //释放
	            mat.release();;
	            frame2.close();
	            
	            if(i > size) {
	            	break;
	            }
	            
	            try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
				}
	        }
	        
	        if(i > size) {
	        	 return true;
            }else {
            	 return false;//人脸不够size可能被中途关闭
            }
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}finally {
			//结束抓拍
			canvas.setVisible(false);
			try {
				grabber.stop();
				grabber.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 从本地摄像头采集人脸集合
	 * @param size 最大采集数量
	 * @return
	 */
	public static List<Image> collection(int size) {
		List<Image> faceList = new ArrayList<Image>();
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		CanvasFrame canvas = new CanvasFrame("采集人脸");//新建一个窗口
		try {
	        grabber.start();   //开始获取摄像头数据
//	        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        canvas.setAlwaysOnTop(true);
	        int i = 0;
	        while(true){
	            if(!canvas.isDisplayable()){//窗口是否关闭
	                grabber.stop();//停止抓取
	                grabber.close();
	                break;
	            }    
	            Frame frame = grabber.grab();
	            // 将Frame转为Mat
	    		OpenCVFrameConverter.ToMat   matConv = new OpenCVFrameConverter.ToMat();
				Mat mat  = matConv.convertToMat(frame);
				
				//释放
				frame.close();
				matConv.close();
	    		
	    		
	    		//mat转换imag
	    		BufferedImage bimg = Java2DFrameUtils.toBufferedImage(mat);
				
				//转DJL img
				Image img = ImageFactory.getInstance().fromImage(bimg);
	    		
	    		
	    		//人脸定位
				List<Image> faces = RetinaFaceDetection.faceDetection(img, true, mat);
				System.out.println("人脸数=" + faces.size());
				
	    		
				//保存人脸数据
				for(Image face:faces) {
					faceList.add(face);
		        	i++;
				}
	    		
				
	    		//Image转Frame
				BufferedImage bi = (BufferedImage)img.getWrappedImage();
				Frame convertFrame1 = Java2DFrameUtils.toFrame(bi);
	    		
				//显示
	            canvas.showImage(convertFrame1);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
	           
	            //释放
	            mat.release();
	            convertFrame1.close();
	            
	            if(i > size) {
	            	break;
	            }
	            
	            try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
				}
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//结束抓拍
			canvas.dispose();
			canvas.setVisible(false);
			try {
				grabber.stop();
				grabber.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return faceList;
	}

	/**
	 * opencv版本提取人脸
	 * @param i
	 * @return
	 */
	public static List<Image> collectionDnn(int size) {
		
		List<Image> faceList = new ArrayList<Image>();
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		CanvasFrame canvas = new CanvasFrame("采集人脸");//新建一个窗口
		try {
	        grabber.start();   //开始获取摄像头数据
//	        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        canvas.setAlwaysOnTop(true);
	        int i = 0;
	        while(true){
	            if(!canvas.isDisplayable()){//窗口是否关闭
	                grabber.stop();//停止抓取
	                grabber.close();
	                break;
	            }  
	            grabber.flush();
	            Frame frame = grabber.grab();
	            // 将Frame转为Mat
	    		OpenCVFrameConverter.ToMat   matConv = new OpenCVFrameConverter.ToMat();
				Mat mat  = matConv.convertToMat(frame);
				
				//释放
				frame.close();
				matConv.close();
	    		
	    		//人脸定位
				List<Image> faces = OpenCVFaceDetection.detection(mat, true);
				
	    		
				//保存人脸数据
				for(Image face:faces) {
					faceList.add(face);
		        	i++;
				}
	    		
				
	    		//Mat转Frame
				// 转换成BufferedImage
				OpenCVFrameConverter.ToMat  matConv2 = new OpenCVFrameConverter.ToMat();
				Java2DFrameConverter  biConv  = new Java2DFrameConverter();
				Frame frame2 = matConv2.convert(mat);
				
				//释放
				biConv.close();
				matConv2.close();
	    		
				//显示
	            canvas.showImage(frame2);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
	           
	            //释放
	            mat.release();
	            frame2.close();
	            
	            if(i > size) {
	            	break;
	            }
	            
	            try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
				}
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//结束抓拍
			canvas.dispose();
			canvas.setVisible(false);
			try {
				grabber.stop();
				grabber.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return faceList;
	}
	

}
