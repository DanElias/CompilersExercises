package lexer;

import java.io.*;
import java.util.Hashtable;

public class Lexer {
	private char peek;
	private Hashtable<String, Token> words = new Hashtable<String, Token>();
	private InputStream input;
	public static int line = 1;

	private void addReserveWord(Word w) {
		words.put(w.getLexeme(), w);
	}

	public Lexer(InputStream input) {
		this.peek = ' ';

		this.input = input;
		addReserveWord(new Word("var", Tag.VAR));
		addReserveWord(new Word("print", Tag.PRINT));
		addReserveWord(new Word("while", Tag.WHILE));
		addReserveWord(new Word("if", Tag.IF));
		addReserveWord(new Word("else", Tag.ELSE));
		addReserveWord(new Word("home", Tag.HOME));
		addReserveWord(new Word("move", Tag.MOVE));
		addReserveWord(new Word("penup", Tag.PENUP));
		addReserveWord(new Word("pendown", Tag.PENDOWN));
		addReserveWord(new Word("forward", Tag.FORWARD));
		addReserveWord(new Word("fd", Tag.FORWARD));
		addReserveWord(new Word("backward", Tag.BACKWARD));
		addReserveWord(new Word("bk", Tag.BACKWARD));
		addReserveWord(new Word("right", Tag.RIGHT));
		addReserveWord(new Word("rt", Tag.RIGHT));
		addReserveWord(new Word("left", Tag.LEFT));
		addReserveWord(new Word("lt", Tag.LEFT));
	}

	private void readch() throws IOException {
		peek = (char) input.read();
	}

	private boolean readch(char c) throws IOException {
		readch();
		if (peek != c)
			return false;

		peek = ' ';
		return true;
	}

	public Token scan() throws IOException {
		for ( ; ; readch() ) {
			if (peek == ' ' || peek == '\t' || peek == '\r') {
				continue;
			} else if (peek == '\n') {
				line = line + 1;
			} else if (peek == '/') {
				if (readch('/')) { // comment
					do {
						readch();
					} while ( peek != '\n' && peek != '\r' && (int)peek != Tag.EOF);
					readch();
				} else {
					return new Token('/');
				}
			} else {
				break;
			}
		}

		switch(peek) {
			case '=' : if (readch('=')) return Word.Eq; else return new Token('=');
			case '!' : if (readch('=')) return Word.Neq;
			case '<' : if (readch('=')) return Word.Leq; else return new Token('<');
			case '>' : if (readch('=')) return Word.Geq; else return new Token('>');
			case '#' : if (readch('t')) return Word.True;
					   else if (readch('f')) return Word.False;
					   else return new Token('#');
		}

		if (peek == '"') {
			String val = "";
			do {
				val = val + peek;
				readch();
			} while ( peek != '"' );
			val = val + peek;
			readch();
			return new Text(val);
		}

		

		if (Character.isDigit(peek)) {
			int v = 0;
			do {
				v = (10 * v) + Character.digit(peek, 10);
				readch();
			} while ( Character.isDigit(peek) );
			return new Number(v);
		}

		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			do {
				b.append(Character.toLowerCase(peek));
				readch();
			} while ( Character.isLetterOrDigit(peek) ) ;
			String s = b.toString();
			Word w = (Word) words.get(s);
			if (w != null)
				return w;

			w = new Word(s, Tag.ID);
			words.put(s, w);
			return w;
		}

		Token tok = new Token(peek); peek = ' ';
		return tok;
	}
}
