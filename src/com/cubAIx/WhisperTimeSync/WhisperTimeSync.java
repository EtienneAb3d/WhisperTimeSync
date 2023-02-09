package com.cubAIx.WhisperTimeSync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class WhisperTimeSync {

	public WhisperTimeSync() {
	}

	String load(String aPath) throws Exception {
		StringBuffer aSB = new StringBuffer();
		BufferedReader aBR = new BufferedReader(
					new InputStreamReader(new FileInputStream(aPath)
					,"UTF8"));
		String aLine = null;
		while((aLine = aBR.readLine()) != null) {
			if(aSB.length() > 0) {
				aSB.append("\n");
			}
			aSB.append(aLine.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		}
		aBR.close();
		return aSB.toString()
				.replaceAll("([0-9]+)\n([0-9]+:[0-9]+:[0-9]+,[0-9]+ --&gt; [0-9]+:[0-9]+:[0-9]+,[0-9]+)\n"
				, "<time id='$1' stamp='$2'/>")
				.replaceAll("[ \n]+", " ");
	}
	
	void process(String aPathSRT,String aPathTxt) throws Exception {
		String aSrtXml = load(aPathSRT);
		System.out.println("SRT: "+aSrtXml);
		String aTxtXml = load(aPathTxt);
		System.out.println("TXT: "+aTxtXml);
		
		TokenizerSimple aTokenizer = new TokenizerSimple();
		TokenizedSent aSrtTS = aTokenizer.tokenizeXmlSimple(aSrtXml," "); 
		TokenizedSent aTxtTS = aTokenizer.tokenizeXmlSimple(aTxtXml," ");
		
		CubaixAlignerSimple aAligner = new CubaixAlignerSimple();
		TokenizedSent aSyncTS = aAligner.syncMarks1to2(aSrtTS, aTxtTS);
		
		BufferedWriter aBW = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(aPathTxt+".srt")
				,"UTF8"));
		
		System.out.println("Output ("+aPathTxt+".srt"+ "):");
		
		StringBuffer aWaiting = new StringBuffer();
		for(Token aT : aSyncTS.tokens) {
			if(aT.kind == Token.NSTOKEN_KIND.MARK) {
				String aId = aT.getAttr("id");
				String aStamp = aT.getAttr("stamp");
				if(aWaiting.length() > 0) {
					String aPhrase = aWaiting.toString()
							.replaceAll("&lt;", "<")
							.replaceAll("&gt;", ">")
							.trim()+"\n\n";
					aBW.write(aPhrase);
					System.out.print(aPhrase);
					aWaiting = new StringBuffer();
				}
				aBW.write(aId+"\n"+aStamp+"\n");
				System.out.print(aId+"\n"+aStamp+"\n");
				continue;
			}
			aWaiting.append(aT.token);
		}
		if(aWaiting.length() > 0) {
			String aPhrase = aWaiting.toString()
					.replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">")
					.trim()+"\n\n";
			aBW.write(aPhrase);
			System.out.print(aPhrase);
}
		aBW.flush();
		aBW.close();
	}
	
	public static void main(String[] args) {
		try {
			new WhisperTimeSync().process(args[0], args[1]);
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
