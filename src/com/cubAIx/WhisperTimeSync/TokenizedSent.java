package com.cubAIx.WhisperTimeSync;

import java.util.Vector;

public class TokenizedSent {
	public String text = null;
	public Vector<Token> tokens = new Vector<Token>();
	
	public TokenizedSent(String aTxt) {
		text = aTxt;
	}
}
