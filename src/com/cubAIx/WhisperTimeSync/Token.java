package com.cubAIx.WhisperTimeSync;

public class Token {
	public enum NSTOKEN_KIND {UNDEF,MARK,WORD,PUNCT};

	public NSTOKEN_KIND kind = NSTOKEN_KIND.UNDEF;
	public String token = null;
	
	public String getAttr(String aAttr) {
		if(kind != NSTOKEN_KIND.MARK) {
			return null;
		}
		int aBeg = token.indexOf(aAttr+"='");
		if(aBeg < 0) {
			return null;
		}
		aBeg += aAttr.length()+2;
		int aEnd = token.indexOf("'",aBeg);
		if(aEnd < 0) {
			return null;
		}
		return token.substring(aBeg,aEnd);
	}
}
