package com.cubAIx.WhisperTimeSync;

import java.util.Vector;

public class CubaixAlignerSimple {
	static final boolean _DEBUG = false;

	static double COST_INCREDIBLE = 1000000;
	
	public TokenizedSent syncMarks1to2(TokenizedSent aTS1,TokenizedSent aTS2) throws Exception {
		Vector<Pair> aP12s = align(aTS1, aTS2);
		TokenizedSent aFused = new TokenizedSent(null);
		if(_DEBUG) {
			System.out.println("\nSYNC: \n");
		}
		for(int p = 0;p < aP12s.size();p++) {
			Pair aP12 = aP12s.elementAt(p);
			if(_DEBUG) {
				System.out.println(
						(aP12.t1 == null ? "[✘]" : "["+aP12.t1.token.replaceAll("\r*\n", "\\\\n")+"]"+aP12.t1.kind)
								+ "\t" + (aP12.t1 == null || aP12.t2 == null || !aP12.t1.token.equals(aP12.t2.token) ? "≠":"=") + "\t"
								+(aP12.t2 == null ? "[✘]" : "["+aP12.t2.token.replaceAll("\r*\n", "\\\\n")+"]"+aP12.t2.kind)
						);
			}
			if(aP12.t1 != null && aP12.t1.kind == Token.NSTOKEN_KIND.MARK) {
				aFused.tokens.add(aP12.t1);
			}
			if(aP12.t2 != null) {
				aFused.tokens.add(aP12.t2);
			}
		}
		return aFused;
	}
	
	public Vector<Pair> align(TokenizedSent aTS1,TokenizedSent aTS2) throws Exception {
		int[][] aChoices = new int[aTS1.tokens.size()+1][aTS2.tokens.size()+1];
		double[][] aCosts = new double[aTS1.tokens.size()+1][aTS2.tokens.size()+1];
		for(int x = 0;x<aTS1.tokens.size()+1;x++) {
			aChoices[x][0] = 1;//Left
			aCosts[x][0] = x;
		}
		for(int y = 0;y<aTS2.tokens.size()+1;y++) {
			aChoices[0][y] = 2;//Up
			aCosts[0][y] = y;
		}
		for(int x = 1;x<aTS1.tokens.size()+1;x++) {
			for(int y = 1;y<aTS2.tokens.size()+1;y++) {
				double aCost = cost(aTS1.tokens.elementAt(x-1),aTS2.tokens.elementAt(y-1));
				double aCost0 = aCosts[x-1][y-1]+aCost*0.99;
				double aCost1 = aCosts[x-1][y]+cost(aTS1.tokens.elementAt(x-1));
				double aCost2 = aCosts[x][y-1]+cost(aTS2.tokens.elementAt(y-1));
				if(aCost0 <= aCost1 && aCost0 <= aCost2) {
					aChoices[x][y] = 0;
					aCosts[x][y] = aCost0;
				}
				else if(aCost1 < aCost2) {
					aChoices[x][y] = 1;
					aCosts[x][y] = aCost1;
				}
				else {
					aChoices[x][y] = 2;
					aCosts[x][y] = aCost2;
				}
			}
		}

		int x = aTS1.tokens.size();
		int y = aTS2.tokens.size();
		Vector<Pair> aPs = new Vector<Pair>();
		while(x > 0 || y > 0) {
			Pair aP = new Pair(); 
			//		System.out.println("X="+x+" Y="+y+" Ch="+aChoices[x][y]+" Co="+aCosts[x][y]);
			if(aChoices[x][y] == 0) {
				aP.t1 = aTS1.tokens.elementAt(--x);
				aP.t2 = aTS2.tokens.elementAt(--y);
			}
			else if(aChoices[x][y] == 1) {
				aP.t1 = aTS1.tokens.elementAt(--x);
			}
			else {
				aP.t2 = aTS2.tokens.elementAt(--y);
			}
			aPs.add(aP);
		}
		
		//Reverse order
		Vector<Pair> aPOs = new Vector<Pair>();
		for(int p = aPs.size()-1;p >= 0;p--) {
			aPOs.add(aPs.elementAt(p));
		}
		
		return aPOs;
	}
	
	double cost(Token aT1,Token aT2) {
		if(aT1.kind != aT2.kind) {
			return COST_INCREDIBLE;
		}
		if(aT1.token.equals(aT2.token)) {
			return 0;
		}
		if(aT1.token.equalsIgnoreCase(aT2.token)) {
			return 0.01;
		}
		if(aT1.token.trim().equalsIgnoreCase(aT2.token.trim())) {
			return 0.02;
		}
		if(aT1.token.toLowerCase().startsWith(aT2.token.toLowerCase())
				|| aT1.token.toLowerCase().endsWith(aT2.token.toLowerCase())
				|| aT2.token.toLowerCase().startsWith(aT1.token.toLowerCase())
				|| aT2.token.toLowerCase().endsWith(aT1.token.toLowerCase())) {//Segmentation problem ?
			return 1.0 - 2.0*Math.min(aT1.token.length(),aT2.token.length())/(double)(aT1.token.length()+aT2.token.length());
		}
		return 2.0 - 2.0*Math.min(aT1.token.length(),aT2.token.length())/(double)(aT1.token.length()+aT2.token.length());
	}
	
	double cost(Token aT) {
		if(aT.token.trim().length() == 0) {
			//Blank
			return 0.1;
		}
		return 1.0;
	}

}
