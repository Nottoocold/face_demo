package cn.jiming.jdlearning.test.car;

import java.io.File;

import org.junit.Test;

public class FileNameTest {
	@Test
	public void rename(){
		String root = "E:\\word-discern\\train\\paddle-seg\\PaddleSeg-release-2.2-carids\\data\\carids\\dataset";
		
		File r_p = new File(root); 
		File[] fs = r_p.listFiles();
		for(File f : fs) {
			File[] fs_t = f.listFiles();
			if(fs_t == null || fs_t.length != 4) {
				System.out.println(f.getName() + "   " + fs_t.length);
				f.delete();
			}
			
//			String oleName = f.getName();
//			String newName = oleName.substring(1);
//			System.out.println(newName);
//			
//			File newF = new File(root + "\\" + newName);
//			
//			f.renameTo(newF);
		}
	}
	
}
