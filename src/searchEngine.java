import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class SearchEngine {
	static final int MAX_HASH=1023;
	StringBuffer[] wdHashBuf = new StringBuffer[MAX_HASH];
	String root,listFName,hashFName;
	RandomAccessFile listFile;
	TreeMap lstHashMap;
	static int encodeBase = 16;
	public static void main(String args[]) throws Exception
	{
		
		System.out.println("d");
		SearchEngine engine = new SearchEngine("data/");
		engine.setIndex("","");
		engine.flush();
		
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
			FileIO.txt2File("", listFName,null);
		listFile = new RandomAccessFile(listFName, "rwd");
		listFile.seek(listFile.length());//移到listFile的尾巴
		try 
		{
			  String hashFileText = FileIO.file2Text(hashFName,null);
			  lstHashMap = FileIO.text2Map(hashFileText);
		} 
		catch (Exception e) { lstHashMap = new TreeMap(); } //如果找不到檔案
		
		for (int i=0; i<MAX_HASH; i++)
			wdHashBuf[i] = new StringBuffer(" ");
		
	}
	
	int fCnt=0;
	public void setIndex(String dPath,String filter) throws Exception
	{
		File dir = new File(root+dPath);
		String[] files = dir.list();
		for (int i=0; i<files.length; i++)
		{
			File file = new File(root+dPath+files[i]);
			if(file.isDirectory())
			{
				if (!files[i].equals("idx"))
					setIndex(dPath+files[i]+"\\", filter);
			}
			else
			{
				if (lstHashMap.get(file2Id(dPath+files[i])) != null) continue;
				fCnt++;
				int fileOffset = writeList(dPath+files[i]);
				String text = FileIO.file2Text(root+dPath+files[i],null);
				indexFile(fileOffset,text, dPath);
			}
		}
		
		
		
	}
	protected String file2Id(String s) {
		 int hashCode = Math.abs(s.hashCode());
		 return Integer.toString(hashCode, encodeBase);
	}
	private int writeList(String fileName) throws IOException
	{
		int fileOffset = (int)listFile.length();
		byte[] bytes = (fileName+"\r\n").getBytes();
		listFile.write(bytes);
		lstHashMap.put(file2Id(fileName), fileOffset+"");
		return fileOffset;
		
	}
	
	static final String[] patterns = 
	{
		/*english word*/"\\w+", 
		/*1 character*/"[^\\s\\w\\p{Punct}]",
		/*2 character*/"[^\\s\\w\\p{Punct}]{2,2}"
	};
	private void indexFile(int fileOffset, String text, String dPath) throws Exception 
	{
		text = text.replace('_', ' ');
		TreeMap wordMap = new TreeMap();
		for (int i=0; i<text.length(); )
		{
			String word = null;
			for (int pi=0; pi<patterns.length; pi++) 
			{
				String token = matchAt(text, i, patterns[pi]);
				if (token == null) continue; 
				
				word = token.toLowerCase();
				
				if (wordMap.get(word) == null) 
				{
					wordMap.put(word, new Integer(1));
					putHit(word, fileOffset);
				}
			}
			if (word == null) i++; 
			else i+=word.length();
		}
	}
	
	private String matchAt(String pText, int pFrom, String pPattern)
	{
		Pattern p = Pattern.compile(pPattern, Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
	    Matcher m = p.matcher(pText.substring(pFrom));
	    if (m.lookingAt()) return m.group(0);
	    return null;
	}
	
	int BIG_ENOUGH = 1024;
	public void putHit(String pWord, int pOffset) throws Exception {
		int hashId = word2hash(pWord);
		StringBuffer buf = wdHashBuf[hashId];
		String code = int2str(pOffset);
		if (buf.indexOf(" "+pWord+"="+code+" ") >= 0) return; // duplicate : the offset has showed before.
		buf.append(pWord+"="+code+" ");
		if (buf.length() > BIG_ENOUGH) saveHit(hashId); // write to disk when the buf is big enough.
	}
	
	public void saveHit(int hashId) throws Exception {
		StringBuffer buf = wdHashBuf[hashId];
		if (buf.toString().trim().length() == 0) return;
		appendFile(" "+buf.toString().trim(), root+"idx\\"+int2str(hashId)+".idx");
		buf.setLength(1); // the first is space.
	}
	static int word2hash(String pWord) {
		return Math.abs(pWord.hashCode())%MAX_HASH;
	}
	
	static String int2str(int n) {
		return Integer.toString(n, encodeBase);
	}
	public static void appendFile(String pStr, String outFile) throws Exception {
	  	FileWriter writer=new FileWriter(outFile, true);
		writer.write(pStr);
		writer.close();
  	}
	public void flush() throws Exception {
		String mapText = FileIO.map2text(lstHashMap);
		FileIO.txt2File(mapText, hashFName,null);
		for (int i=0; i<MAX_HASH; i++)
			saveHit(i);
	}
}
