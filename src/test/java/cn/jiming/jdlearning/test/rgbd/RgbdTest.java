package cn.jiming.jdlearning.test.rgbd;


import cn.jiming.jdlearning.fire.FireDetection;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.UMat;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class RgbdTest {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// cv
		Mat src = imread("E:\\2d-3d\\sample01.jpg");
		
		System.out.println(src.depth());
		System.out.println(src.channels());
		System.out.println(src.arrayDepth());
		System.out.println(src.arrayChannels());
		
		System.out.println("*********");
		
		NDManager manager = NDManager.newBaseManager();
		Path path = Path.of("E:\\2d-3d\\sample01.jpg");
		Image img = ImageFactory.getInstance().fromFile(path);
		NDArray nd = img.toNDArray(manager);
		System.out.println(nd);
		
		BytePointer p = src.ptr(0, 0, 3);
		
		
	}

}
