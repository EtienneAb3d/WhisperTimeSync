package com.cubAIx.WhisperTimeSync;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizerSimple {
	static final String PUNCT_ONLY_STRING_RE = "[\r\n\t	  ,  ​،؟;:.!?¡¿。：？！；؟!؛،.ـ()\\[\\]{}<>\"'‘’`´«»‹›„“”*/+=|\\‒–—―‑€$£§%#@&°。：？！；，、（）-]+";

	public TokenizedSent tokenizeXmlSimple(String aTxt) throws Exception {
		return tokenizeXmlSimple(aTxt, PUNCT_ONLY_STRING_RE);
	}
	public TokenizedSent tokenizeXmlSimple(String aTxt,String aSeparatorChars) throws Exception {
		TokenizedSent aTS = new TokenizedSent(aTxt);
		Pattern aPatten = Pattern.compile("<[^>]*>");
		Matcher aMatcher = aPatten.matcher(aTS.text);
		Vector<String> aParts = new Vector<String>();
		int aPos = 0;
		while(aMatcher.find()) {
			if(aMatcher.start() > aPos) {
				aParts.add(aTS.text.substring(aPos, aMatcher.start()));
				aPos = aMatcher.start();
			}
			aParts.add(transcodeFromEntities(aTS.text.substring(aMatcher.start(),aMatcher.end())));
			aPos = aMatcher.end();
		}
		if(aPos < aTS.text.length()) {
			aParts.add(transcodeFromEntities(aTS.text.substring(aPos)));
		}
		int aTokPos = 0;
		String aSubTok = "";
		aPos = 0;
		for(String aP : aParts) {
			if(aP.startsWith("<")) {
				Token aT = new Token();
				aT.token = aP;
				aT.kind = Token.NSTOKEN_KIND.MARK;
				aT.charPos = aPos;
				aT.tokPos = aTokPos;
				aTS.tokens.add(aT);
				aPos += aT.token.length();
				aTokPos++;
				continue;
			}
			if(aSeparatorChars == null) {
				for(char aC : aP.toCharArray()) {
					String aTok = ""+aC;
					if(aTok.matches(PUNCT_ONLY_STRING_RE)) {
						Token aT = new Token();
						aT.token = aTok;
						aT.kind = Token.NSTOKEN_KIND.PUNCT;
						aT.charPos = aPos;
						aT.tokPos = aTokPos;
						aTS.tokens.add(aT);
						aPos += aT.token.length();
						aTokPos++;
						continue;
					}
					else {
						Token aT = new Token();
						aT.token = aTok;
						aT.kind = Token.NSTOKEN_KIND.WORD;
						aT.charPos = aPos;
						aT.tokPos = aTokPos;
						aTS.tokens.add(aT);
						aPos += aT.token.length();
						aTokPos++;
						continue;
					}
				}
			}
			else{
				StringTokenizer aTokenizer = new StringTokenizer(aP,aSeparatorChars,true);
				while(aTokenizer.hasMoreTokens()) {
					String aTok =  (String)aTokenizer.nextElement();
					if(aTok.matches(PUNCT_ONLY_STRING_RE)) {
						Token aT = new Token();
						aT.token = aTok;
						aT.kind = Token.NSTOKEN_KIND.PUNCT;
						aT.charPos = aPos;
						aT.tokPos = aTokPos;
						aTS.tokens.add(aT);
						aPos += aT.token.length();
						aTokPos++;
						continue;
					}
					else {
						Token aT = new Token();
						aT.token = aTok;
						aT.kind = Token.NSTOKEN_KIND.WORD;
						aT.charPos = aPos;
						aT.tokPos = aTokPos;
						aTS.tokens.add(aT);
						aPos += aT.token.length();
						aTokPos++;
						continue;
					}
				}
			}
		}
		return aTS;
	}
	
	/**
	 * @param aStrIn
	 * @return
	 */
	public static String transcodeFromHTMLSafe(String aStrIn) {
		return aStrIn.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&amp;","&");
	}

	/**
	 * @param aStrIn
	 * @return
	 */
	public static String transcodeFromEntities(String aStrIn) {
		//Search for all possible entities
		String aStrOut = transcodeFromHTMLSafe(aStrIn);
		String aEntity;
		char aChar;
		int aPos = 0,aPosStart;
		//EM 08/08/2006, bug : check for end of string !
		//EM 16/06/2006, bug : aStrOut in place of aStrIn
		while(aPos < aStrOut.length() && (aPos = aStrOut.indexOf("&",aPos)) >= 0){
			//Ok "&" found
			aPosStart = aPos;
			aPos++;
			if(aPos >= aStrOut.length() || aStrOut.charAt(aPos) != '#'){
				//Not a suitable entity
				continue;
			}
			aPos++;
			while(aPos < aStrOut.length() && ("" + aStrOut.charAt(aPos)).matches("[0-9]")){
				aPos++;
			}
			if(aPos >= aStrOut.length() || aStrOut.charAt(aPos) != ';'){
				//Not a suitable entity
				continue;
			}
			//Ok found one !
			aPos++;
			aEntity = aStrOut.substring(aPosStart,aPos);
			aChar = (char) (Integer.parseInt(aEntity.substring(2, aEntity.length() - 1)) & 0xFFFF);
			aStrOut = aStrOut.replaceAll(aEntity,"" + aChar);
			aPos = aPosStart + 1;
		}
		return aStrOut;
	}

	public static void main(String[] args) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

}
