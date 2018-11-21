package address;

import java.io.File;
import java.util.Arrays;

public class Test2 {

	public static void main (String[] args) {
		
		File file = new File("files");
		System.out.println(file.exists());
		
		String str = "1 1 1";
		String[] strSplit = new String[3];
		strSplit = str.split(" ");
		System.out.println(Arrays.toString(strSplit));
		
	}
	// /Users/snit71/Google Drive/Year 13/Y12_13 Lessons/Y12_13 CS HL/Internal Assessment/Ingrid_Tsang_IA/CSPastPapersApp/src/address
}
