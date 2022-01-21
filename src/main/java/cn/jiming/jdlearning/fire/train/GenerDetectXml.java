package cn.jiming.jdlearning.fire.train;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import org.bytedeco.opencv.opencv_core.Mat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.jiming.jdlearning.ObjRect;
import cn.jiming.jdlearning.fire.FireDetection;
import cn.jiming.jdlearning.fire.SmokingDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 自动生成车牌字符对象检测的xml数据文件
 * @author xiaoming
 *
 */
public class GenerDetectXml {

	public static void main(String[] args) {
		
//		copyIds("E:\\word-discern\\train\\车牌字符定位\\others", "E:\\word-discern\\train\\carids\\0");
		
		
//		toGenerXmlByFire();
		
		
		toGenerXmlBySmoking();
		
		
	}

	
	private static void copyIds(String a, String b) {
		File af = new File(a);
		File[] ids = af.listFiles();
		
		int i = 20000;
		for(File id:ids) {
			
			File bf = new File(b + "\\" + i + ".jpg");
			copy(id, bf);
			
			i++;
			System.out.println(i);
		}
		
	}


	private static void toGenerXmlByFire() {
		String idPath = "E:\\daming\\git-repository\\fire-recognition\\yolov5-fire\\paper_data\\fire5\\images";
		File idf = new File(idPath);
		File[] ids = idf.listFiles();
		
		int i = 0;
		for(File id:ids) {
			//获取Mat
			Mat src = imread(id.getAbsolutePath());
			
			//火苗定位
			List<ObjRect> rects = FireDetection.detectRects(src, true, 0.4);
			
			//判断是否是成功定位？1个rect以上
			if(rects.size() >= 1) {
				//成功的定位字符,生成xml
//				generXmlByFire(id, src, rects);
				
				//文件复制去img路径
//				File imgF = new File("E:\\daming\\git-repository\\fire-recognition\\yolov5-fire\\paper_data\\tem\\" + id.getName());
//				copy(id, imgF);
				
				imwrite("E:\\daming\\git-repository\\fire-recognition\\yolov5-fire\\paper_data\\tem\\" + id.getName(), src);
				
			}else {
				//失败的定位
//				File badF = new File("E:\\daming\\git-repository\\fire-recognition\\yolov5-fire\\paper_data\\error\\" + id.getName());
//				copy(id, badF);
			}
			
			src.close();
			i++;
			System.out.println(i);
		}
		
	}
	
	
	private static void toGenerXmlBySmoking() {
		String idPath = "E:\\daming\\git-repository\\fire-recognition\\yolov5-smoke\\paper_data\\img";
		File idf = new File(idPath);
		File[] ids = idf.listFiles();
		
		int i = 0;
		for(File id:ids) {
			//获取Mat
			Mat src = imread(id.getAbsolutePath());
			
			//抽烟定位
			List<ObjRect> rects = SmokingDetection.detectRects(src, true, 0.2);
			
			//判断是否是成功定位？1个rect以上
			if(rects.size() >= 1) {
				//成功的定位字符,生成xml
				generXmlBySmoke(id, src, rects);
				
				//文件复制去img路径
				File imgF = new File("E:\\daming\\git-repository\\fire-recognition\\yolov5-smoke\\paper_data\\images\\" + id.getName());
				copy(id, imgF);
				
				imwrite("E:\\daming\\git-repository\\fire-recognition\\yolov5-smoke\\paper_data\\out\\" + id.getName(), src);
			}else {
				//失败的定位
				File badF = new File("E:\\daming\\git-repository\\fire-recognition\\yolov5-smoke\\paper_data\\error\\" + id.getName());
				copy(id, badF);
			}
			
			src.close();
			i++;
			System.out.println(i);
			
			
		}
		
	}
	
	
	private static void generXmlBySmoke(File id, Mat src, List<ObjRect> rects) {

		String xmls = "E:\\daming\\git-repository\\fire-recognition\\yolov5-smoke\\paper_data\\Annotations";
		String tarImgPath = "E:\\daming\\git-repository\\fire-recognition\\yolov5-smoke\\paper_data\\images";

		try {
			// 生成 xml
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder
					.parse("E:\\word-discern\\train\\tensorflow\\chinese_license_plate_generator\\num.xml");

			String name = id.getName().substring(0, id.getName().lastIndexOf("."));
			String xml_path = xmls + "\\" + name + ".xml";

			// 图片大小设置
			Node width = document.getElementsByTagName("width").item(0);
			width.setTextContent(src.arrayWidth() + "");
			Node height = document.getElementsByTagName("height").item(0);
			height.setTextContent(src.arrayHeight() + "");

			// 图片名设置
			Node filename = document.getElementsByTagName("filename").item(0);
			filename.setTextContent(id.getName());
			// 图片实际路径设置
			Node path = document.getElementsByTagName("path").item(0);
			path.setTextContent(tarImgPath + "\\" + id.getName());

			// 清空字符的位置
			NodeList object_list = document.getElementsByTagName("object");
			for (int n = 0; n < object_list.getLength(); n++) {
				Node objectN = object_list.item(n);
				if (objectN != null) {
					document.getDocumentElement().removeChild(objectN);
				}

			}

			for (int i = 0; i < rects.size(); i++) {
				ObjRect rect = rects.get(i);

				// 编辑xml
				/**
				 * <object> <name>fire</name> <pose>Unspecified</pose> <truncated>1</truncated>
				 * <difficult>0</difficult> <bndbox> <xmin>1</xmin> <ymin>1</ymin>
				 * <xmax>18</xmax> <ymax>36</ymax> </bndbox> </object>
				 */
				Element nameE = document.createElement("name");
				nameE.setTextContent("smoke");
				Element poseE = document.createElement("pose");
				poseE.setTextContent("Unspecified");
				Element truncatedE = document.createElement("truncated");
				truncatedE.setTextContent("1");
				Element difficultE = document.createElement("difficult");
				difficultE.setTextContent("0");

				// <bndbox>
				Element bndboxE = document.createElement("bndbox");
				Element xminE = document.createElement("xmin");
				xminE.setTextContent(rect.getMinX() + "");
				Element yminE = document.createElement("ymin");
				yminE.setTextContent(rect.getMinY() + "");
				Element xmaxE = document.createElement("xmax");
				xmaxE.setTextContent(rect.getMaxX() + "");
				Element ymaxE = document.createElement("ymax");
				ymaxE.setTextContent(rect.getMaxY() + "");

				bndboxE.appendChild(xminE);
				bndboxE.appendChild(yminE);
				bndboxE.appendChild(xmaxE);
				bndboxE.appendChild(ymaxE);

				Element object = document.createElement("object");
				object.appendChild(nameE);
				object.appendChild(poseE);
				object.appendChild(truncatedE);
				object.appendChild(difficultE);
				object.appendChild(bndboxE);

				document.getDocumentElement().appendChild(object);

			}

			// 输出xml
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult consoleResult = new StreamResult(new FileOutputStream(new File(xml_path)));// new
																									// FileOutputStream(f.getPath())
			transformer.transform(domSource, consoleResult);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	private static void generXmlByFire(File id, Mat src, List<ObjRect> rects) {

		String xmls = "E:\\daming\\git-repository\\fire-recognition\\yolov5-fire\\paper_data\\Annotations";
		String tarImgPath = "E:\\daming\\git-repository\\fire-recognition\\yolov5-fire\\paper_data\\images";

		try {
			// 生成 xml
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder
					.parse("E:\\word-discern\\train\\tensorflow\\chinese_license_plate_generator\\num.xml");

			String name = id.getName().substring(0, id.getName().lastIndexOf("."));
			String xml_path = xmls + "\\" + name + ".xml";

			// 图片大小设置
			Node width = document.getElementsByTagName("width").item(0);
			width.setTextContent(src.arrayWidth() + "");
			Node height = document.getElementsByTagName("height").item(0);
			height.setTextContent(src.arrayHeight() + "");

			// 图片名设置
			Node filename = document.getElementsByTagName("filename").item(0);
			filename.setTextContent(id.getName());
			// 图片实际路径设置
			Node path = document.getElementsByTagName("path").item(0);
			path.setTextContent(tarImgPath + "\\" + id.getName());

			// 清空字符的位置
			NodeList object_list = document.getElementsByTagName("object");
			for (int n = 0; n < object_list.getLength(); n++) {
				Node objectN = object_list.item(n);
				if (objectN != null) {
					document.getDocumentElement().removeChild(objectN);
				}

			}

			for (int i = 0; i < rects.size(); i++) {
				ObjRect rect = rects.get(i);

				// 编辑xml
				/**
				 * <object> <name>fire</name> <pose>Unspecified</pose> <truncated>1</truncated>
				 * <difficult>0</difficult> <bndbox> <xmin>1</xmin> <ymin>1</ymin>
				 * <xmax>18</xmax> <ymax>36</ymax> </bndbox> </object>
				 */
				Element nameE = document.createElement("name");
				nameE.setTextContent("fire");
				Element poseE = document.createElement("pose");
				poseE.setTextContent("Unspecified");
				Element truncatedE = document.createElement("truncated");
				truncatedE.setTextContent("1");
				Element difficultE = document.createElement("difficult");
				difficultE.setTextContent("0");

				// <bndbox>
				Element bndboxE = document.createElement("bndbox");
				Element xminE = document.createElement("xmin");
				xminE.setTextContent(rect.getMinX() + "");
				Element yminE = document.createElement("ymin");
				yminE.setTextContent(rect.getMinY() + "");
				Element xmaxE = document.createElement("xmax");
				xmaxE.setTextContent(rect.getMaxX() + "");
				Element ymaxE = document.createElement("ymax");
				ymaxE.setTextContent(rect.getMaxY() + "");

				bndboxE.appendChild(xminE);
				bndboxE.appendChild(yminE);
				bndboxE.appendChild(xmaxE);
				bndboxE.appendChild(ymaxE);

				Element object = document.createElement("object");
				object.appendChild(nameE);
				object.appendChild(poseE);
				object.appendChild(truncatedE);
				object.appendChild(difficultE);
				object.appendChild(bndboxE);

				document.getDocumentElement().appendChild(object);

			}

			// 输出xml
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult consoleResult = new StreamResult(new FileOutputStream(new File(xml_path)));// new
																									// FileOutputStream(f.getPath())
			transformer.transform(domSource, consoleResult);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private static void copy(File srcI, File toI) {
		FileInputStream in = null;
		FileOutputStream ou = null;
		try {
			in = new FileInputStream(srcI);
			ou = new FileOutputStream(toI);
			
			byte[] b = new byte[1024];
			int end = -1;
			while((end = in.read(b)) != -1) {
				ou.write(b, 0, end);
				ou.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if(ou != null) {
				try {
					ou.close();
				} catch (IOException e) {
				}
			}
		}
		
		
	}
}
