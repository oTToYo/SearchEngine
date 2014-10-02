import java.util.*;
import java.io.*;

public class SearchEngine {
	static final int MAX_HASH=1023;
	StringBuffer[] wdHashBuf = new StringBuffer[MAX_HASH];
	String root,listFName,hashFName;
	RandomAccessFile listFile;
	TreeMap lstHashMap;
	public static void main(String args[]) throws Exception
	{
		
		System.out.println("ddfs");
		SearchEngine engine = new SearchEngine("data/");
		
	}
	public SearchEngine(String rootAd) throws Exception
	{
		root = rootAd;
		File idxDir = new File(root+"idx");
		idxDir.mkdirs();
		listFName = root+"idx\\files.lst";
		hashFName = root+"idx\\files.hash";
		
		File file = new File(listFName);
		if(!file.exists())
			FileIO.txt2File("", listFName, "");
		listFile = new RandomAccessFile(listFName, "rwd");
		listFile.seek(listFile.length());//移到listFile的尾巴
		try 
		{
			  String hashFileText = FileIO.file2Text(hashFName,"");
			  lstHashMap = FileIO.text2Map(hashFileText);
		} 
		catch (Exception e) { lstHashMap = new TreeMap(); } //如果找不到檔案
		
		for (int i=0; i<MAX_HASH; i++)
			wdHashBuf[i] = new StringBuffer(" ");
		
	}
}
