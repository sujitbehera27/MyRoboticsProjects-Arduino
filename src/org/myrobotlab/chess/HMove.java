package org.myrobotlab.chess;

//
//  Move.java
//
//  Created by Peter Hunter on Mon Dec 31 2001.
//  Copyright (c) 2001 Peter Hunter. All rights reserved.
//
public final class HMove extends org.op.chess.Move implements Comparable, Constants {
	public final int promote;
	public final int bits;
	final char pieceLetter;
	int score = 0;

	public HMove(int from, int to, int promote, int bits, char pieceLetter) {
		super(from, to);
		this.promote = promote;
		this.bits = bits;
		this.pieceLetter = pieceLetter;
	}

	int getScore() {
		return score;
	}

	void setScore(int i) {
		score = i;
	}

	public int hashCode() {
		return from + (to << 8) + (promote << 16);
	}

	public boolean equals(Object o) {
		HMove m = (HMove) o;
		return (m.from == from && m.to == to && m.promote == promote);
	}

	/**
	 * Compares this move to another move. The implementation is strictly
	 * defective - it ought to do something other than throw a
	 * ClassCastException if o is not a Move, but for optimization reasons it
	 * doesn't. This class is only partially ordered. That is, the ordering is
	 * incompatible with equals.
	 */

	public int compareTo(Object o) {
		HMove m = (HMove) o;
		int mScore = m.getScore();
		return mScore - score; // Can't overflow so this should work.
	}

	public String toString() {
		char c;
		StringBuffer sb = new StringBuffer();

		if ((bits & 32) != 0) {
			switch (promote) {
			case KNIGHT:
				c = 'n';
				break;
			case BISHOP:
				c = 'b';
				break;
			case ROOK:
				c = 'r';
				break;
			default:
				c = 'q';
				break;
			}
			sb.append((char) (getFromCol() + 'a'));
			sb.append(8 - getFromRow());
			sb.append("-");
			sb.append((char) (getToCol() + 'a'));
			sb.append(8 - getToRow());
			sb.append(c);
		} else {
			if (pieceLetter != 'P')
				sb.append((char) pieceLetter);
			sb.append((char) (getFromCol() + 'a'));
			sb.append(8 - getFromRow());
			sb.append("-");
			sb.append((char) (getToCol() + 'a'));
			sb.append(8 - getToRow());
		}
		return sb.toString();
	}
}