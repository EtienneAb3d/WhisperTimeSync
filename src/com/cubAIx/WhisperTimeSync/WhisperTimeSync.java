package com.cubAIx.WhisperTimeSync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class WhisperTimeSync {
	static final boolean _DEBUG_INOUT = false;
	static final boolean _DEBUG_ALIGN = false;

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
			aSB.append(aLine);
		}
		aBR.close();
		return aSB.toString();
	}
	
	String toXml(String aSrt) {
		return ("\n"+aSrt.replaceAll("\r*\n", "\n"))
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				.replaceAll("\n([0-9]+)\n([0-9]+:[0-9]+:[0-9]+[,.][0-9]+ --&gt; [0-9]+:[0-9]+:[0-9]+[,.][0-9]+)\n"
						, "<time id='$1' stamp='$2'/>")
				.replaceAll("\n([0-9]+:[0-9]+:[0-9]+[,.][0-9]+ --&gt; [0-9]+:[0-9]+:[0-9]+[,.][0-9]+)\n"
						, "<time id='' stamp='$1'/>")
				.replaceAll("[ ]+", " ")
				.replaceAll("[\n]+", "\n");
	}
	
	public void processFile(String aPathSRT,String aPathTxt,String aLng) throws Exception {
		String aSrt = load(aPathSRT);
		String aTxt = load(aPathTxt);
		String aOut = processString(aSrt,aTxt,aLng);
		System.out.println("\n"
				+ "Output ("+aPathTxt+".srt"+ "):");
		BufferedWriter aBW = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(aPathTxt+".srt")
				,"UTF8"));
		aBW.write(aOut);
		aBW.flush();
		aBW.close();
	}
		
	public String processString(String aSRT,String aTxt,String aLng) throws Exception {
		if(_DEBUG_INOUT) {
			System.out.println("\nSRT: \n"+aSRT);
			System.out.println("\nTXT: \n"+aTxt);
		}
		String aSrtXml = toXml(aSRT);
		String aTxtXml = toXml(aTxt);
		if(_DEBUG_INOUT) {
			System.out.println("\nSRTXML: \n"+aSrtXml);
			System.out.println("\nTXTXML: \n"+aTxtXml);
		}

		String aCutOnRE = aLng.matches("(ja|zh|ko)")?null:"[ \n]";

		TokenizerSimple aTokenizer = new TokenizerSimple();
		TokenizedSent aSrtTS = aTokenizer.tokenizeXmlSimple(aSrtXml,aCutOnRE); 
		TokenizedSent aTxtTS = aTokenizer.tokenizeXmlSimple(aTxtXml,aCutOnRE);

		CubaixAlignerSimple aAligner = new CubaixAlignerSimple();
		TokenizedSent aSyncTS = aAligner.syncMarks1to2(aSrtTS, aTxtTS);

		StringBuffer aOut = new StringBuffer();
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
					aOut.append(aPhrase);
					if(_DEBUG_ALIGN) {
						System.out.print(aPhrase);
					}
					aWaiting = new StringBuffer();
				}
				aOut.append(aId+"\n"+aStamp+"\n");
				if(_DEBUG_ALIGN) {
					System.out.print(aId+"\n"+aStamp+"\n");
				}
				continue;
			}
			aWaiting.append(aT.token);
		}
		if(aWaiting.length() > 0) {
			String aPhrase = aWaiting.toString()
					.replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">")
					.trim()+"\n\n";
			aOut.append(aPhrase);
			if(_DEBUG_ALIGN) {
				System.out.print(aPhrase);
			}
		}
		
		return aOut.toString();
	}
	
	public static void main(String[] args) {
		try {
			new WhisperTimeSync().processFile(args[0], args[1], args[2]);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
