import java.util.*;
import java.io.*;

public class FileIO {

	
	public static void txt2File(String txt,String path,String encode) throws Exception//output file
	{
		FileOutputStream out = new FileOutputStream(path);
		Writer wr;
		
		if(encode==null) // 不使用編碼
			wr = new OutputStreamWriter(out);
		else
			wr = new OutputStreamWriter(out,encode);
		wr.write(replace(txt, "\n", "\r\n"));
	    wr.close();
	}
	public static String replace(String str,String iPat,String oPat)
	{
		if(iPat.length()==0) return str; //沒有要取代的pattern
		if(str.indexOf(str)<0) return str; //str中找不到要取代pattern
		
		StringBuffer outStr = new StringBuffer();
		int start = 0,next;
		
		while((next = str.indexOf(iPat,start))>=0) //尋找文中所有要取代的pattern
		{
			outStr.append(str.substring(start, next));
		    outStr.append(oPat);
		    start = next + iPat.length();	
		}
		
		outStr.append(str.substring(start));
		return outStr.toString();
		
	}
	public static String file2Text(String fName,String encode) throws Exception
	{
		FileInputStream fin= new FileInputStream(fName);
	    
		InputStreamReader isr;
		if(encode == null)
			isr = new InputStreamReader(fin);
		else
			isr = new InputStreamReader(fin, encode);
		
		final int  BUFSIZE = 4096;
		BufferedReader reader=new BufferedReader(isr, BUFSIZE);
	    StringBuffer outText = new StringBuffer();
	    do{
	    	char[] buf = new char[BUFSIZE];
	        int len = reader.read(buf, 0, BUFSIZE);
	        if (len > 0) // 檢查是否讀到檔尾
	        {
	          String bufStr = new String(buf, 0, len);
	          outText.append(bufStr);
	        }
	        if (len < BUFSIZE) break;
	      } while (true);
	    
	      reader.close();
	      return replace(outText.toString(), "\r", "");
	
	}
	public static TreeMap text2Map(String txt)
	{
		TreeMap map = new TreeMap();
	    String[] lines = txt.split("\n");
	    for (int i=0; i<lines.length; i++)
	    {
	      String[] tokens=lines[i].split("=");
	      if (tokens.length >= 2)
	        map.put(tokens[0].trim(), tokens[1].trim()); //首尾去除空白並存入map
	    }
	    return map;
		
	}
	public static String map2text(Map map) 
	{
	    StringBuffer rzStr = new StringBuffer();
	    Object[] keys = map.keySet().toArray();
	    Object[] values = map.values().toArray();
	    for (int i=0; i<keys.length; i++) 
	      rzStr.append(keys[i]+"="+values[i]+"\n");
	    return rzStr.toString();
	}  
}
