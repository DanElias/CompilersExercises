package grammar;

import java.io.FileInputStream;
import java.io.IOException;

import errors.SyntaxError;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;

public class Parser {
	private Lexer lexer;
	private Token token;

	public Parser (FileInputStream input) {
		lexer = new Lexer(input);
	}

	private void check(int tag) throws SyntaxError, IOException {
		if (token.getTag() == tag) {
			token = lexer.scan();
		} else {
			throw new SyntaxError();
		}
	}

	public void analyze() throws SyntaxError, IOException {
		token = lexer.scan();
		statementSequence();
		if (token.getTag() == Tag.EOF) { 
			System.out.println("ACCEPTED");
		} else {
			throw new SyntaxError();
		}
	}

	private void statementSequence() throws SyntaxError, IOException {
		if (token.getTag() == Tag.VAR || token.getTag() == Tag.ID || token.getTag() == Tag.PRINT  ||
		    token.getTag() == Tag.WHILE || token.getTag() == (int) '{' || token.getTag() == Tag.IF || 
		    token.getTag() == Tag.HOME || token.getTag() == Tag.MOVE ||
		    token.getTag() == Tag.PENUP || token.getTag() == Tag.PENDOWN || 
		    token.getTag() == Tag.FORWARD || token.getTag() == Tag.BACKWARD || 
		    token.getTag() == Tag.RIGHT || token.getTag() == Tag.LEFT ) {
		statement();
		statementSequence();
		} else {
			//do nothing
		}
	}

	private void statement() throws SyntaxError, IOException {
		if (token.getTag() == Tag.VAR) {
			check(Tag.VAR);
			check(Tag.ID);
			check((int) ';');
		} else if (token.getTag() == Tag.ID) {
			check(Tag.ID);
			check((int) '=');
			expression();
			check((int) ';');
		} else if (token.getTag() == Tag.PRINT) {
			check(Tag.PRINT);
			check((int) '(');
			displayList();
			check((int) ')');
			check((int) ';');
		} else if (token.getTag() == Tag.WHILE) {
			check(Tag.WHILE);
			check((int) '(');
			eBool();
			check((int) ')');
			statement();
		} else if (token.getTag() == (int) '{') {
			check((int) '{');
			statementSequence();
			check((int) '}');
		} else if (token.getTag() == Tag.IF) {
			check(Tag.IF);
			check((int) '(');
			eBool();
			check((int) ')');
			statement();
			restIf();
		} else if (token.getTag() == Tag.HOME) {
			check(Tag.HOME);
			check((int) ';');
		} else if (token.getTag() == Tag.MOVE) {
			check(Tag.MOVE);
			expression();
			check((int) ',');
			expression();
			check((int) ';');
		} else if (token.getTag() == Tag.PENUP) {
			check(Tag.PENUP);
			check((int) ';');
		} else if (token.getTag() == Tag.PENDOWN) {
			check(Tag.PENDOWN);
			check((int) ';');
		} else if (token.getTag() == Tag.FORWARD) {
			check(Tag.FORWARD);
			expression();
			check((int) ';');
		} else if (token.getTag() == Tag.BACKWARD) {
			check(Tag.BACKWARD);
			expression();
			check((int) ';');
		} else if (token.getTag() == Tag.RIGHT) {
			check(Tag.RIGHT);
			expression();
			check((int) ';');
		} else if (token.getTag() == Tag.LEFT) {
			check(Tag.LEFT);
			expression();
			check((int) ';');
		} else {
			throw new SyntaxError();
		}
	}

	private void restIf() throws SyntaxError, IOException {
		if (token.getTag() == Tag.ELSE) {
			check(Tag.ELSE);
			statement();
		} else {
			// do nothing
		}
	}

	private void expression() throws SyntaxError, IOException {
		term();
		ePrime();
	}

	private void ePrime() throws SyntaxError, IOException {
		if (token.getTag() == ((int) '+')) {
			check((int) '+');
			term();
			ePrime();
		} else if (token.getTag() == ((int) '-')) {
			check((int) '-');
			term();
			ePrime();
		} else {
			// do nothing
		}
	}

	private void term() throws SyntaxError, IOException {
		factor();
		tPrime();
	}

	private void tPrime() throws SyntaxError, IOException {
		if (token.getTag() == ((int) '*')) {
			check((int) '*');
			factor();
			tPrime();
		} else if (token.getTag() == ((int) '/')) {
			check((int) '/');
			factor();
			tPrime();
		} else {
			// do nothing
		}
	}

	private void factor() throws SyntaxError, IOException {
		if (token.getTag() == Tag.ID) {
			check(Tag.ID);
		} else if (token.getTag() == Tag.NUMBER) {
			check(Tag.NUMBER);
		} else if (token.getTag() == Tag.STRING) {
			check(Tag.STRING);
		} else if (token.getTag() == ((int) '(')) {
			check((int) '(');
			expression();
			check((int) ')');
		} else {
			throw new SyntaxError();
		}
	}

	private void eBool() throws SyntaxError, IOException {
		if (token.getTag() == Tag.TRUE) {
			check(Tag.TRUE);
		} else if (token.getTag() == Tag.FALSE) {
			check(Tag.FALSE);
		} 
		else if (token.getTag() == Tag.NUMBER || token.getTag() == (int)'(' || token.getTag() == Tag.ID || token.getTag() == Tag.STRING ){
			expression(); // First is expression
			if (token.getTag() == Tag.EQ) {
				check(Tag.EQ);
				expression();
			} else if (token.getTag() == Tag.GEQ) {
				check(Tag.GEQ);
				expression();
			} else if (token.getTag() == Tag.LEQ) {
				check(Tag.LEQ);
				expression();
			} else if (token.getTag() == Tag.NEQ) {
				check(Tag.NEQ);
				expression();
			} else if (token.getTag() == (int)'>') {
				check((int) '>');
				expression();
			} else if (token.getTag() == (int)'<') {
				check((int) '<');
				expression();
			} 
		} else { // Not a expected value, throw error
			throw new SyntaxError();
		}
	}

	private void displayList() throws SyntaxError, IOException {
		if (token.getTag() == Tag.NUMBER ||  token.getTag() == Tag.ID || token.getTag() == Tag.STRING ) {
			element();
			elementSequence();
		} else {
			// do noting, epsilon
		}
		
	}

	private void element() throws SyntaxError, IOException {
		if (token.getTag() == Tag.ID) {
			check(Tag.ID);
		} else if (token.getTag() == Tag.NUMBER) {
			check(Tag.NUMBER);
		} else if (token.getTag() == Tag.STRING) {
			check(Tag.STRING);
		} else {
			throw new SyntaxError();
		}
	}

	private void elementSequence() throws SyntaxError, IOException {
		if (token.getTag() == ',') {
			check((int) ',');
			element();
			elementSequence();
		} else {
			// do nothing 
		}
	}
}
