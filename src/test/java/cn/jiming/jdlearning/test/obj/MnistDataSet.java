package cn.jiming.jdlearning.test.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.junit.Test;


public class MnistDataSet {

	@Test
	public void test() throws Exception {
		String tar = "test";
		
		FileReader reader = new FileReader("E:\\word-discern\\train\\手写文字数据集\\mnist\\mnist_dataset\\"+tar+"_labs.txt");
		BufferedReader br = new BufferedReader(reader);
		String root = "E:\\word-discern\\train\\手写文字数据集\\mnist\\mnist_dataset\\"+tar+"_img";
		
		//开始读文件
		String line = "";
		int i = 0;
		while((line = br.readLine()) != null) {
			String[] names = line.split(" ");
			if(names.length == 5) {
				String name = names[0];
				String lab = names[4];
				
				File lanF = new File(root + "\\" + lab);
				if(!lanF.exists()) {
					lanF.mkdir();
				}
				
				//源文件
				i++;
				System.out.println(i);
				File src = new File("E:\\word-discern\\train\\手写文字数据集\\mnist\\mnist_dataset\\"+tar+"\\" + name + ".png");
				if(src.exists()) {
					//复制
				    copy(src.getAbsolutePath(), root + "\\" + lab + "\\" + src.getName());
				}
				
			}
			
		}
		
		
	}

	private void copy(String src, String to) throws Exception {
		FileInputStream fi = new FileInputStream(src);
		FileOutputStream fo = new FileOutputStream(to);
		
		int end = -1;
		byte[] b = new byte[1024];
		while((end = fi.read(b)) != -1) {
			fo.write(b, 0, end);
			fo.flush();
		}
		
		fi.close();
		fo.close();
	}

}
