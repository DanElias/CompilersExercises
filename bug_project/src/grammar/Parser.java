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
		System.out.println("ACCEPTED");
	}

	private void statementSequence() throws SyntaxError, IOException {
		if (token.getTag() == Tag.VAR || token.getTag() == Tag.ID || token.getTag() == Tag.PRINT  ||
		    token.getTag() == Tag.WHILE || token.getTag() == (int) '{' || token.getTag() == Tag.IF) {
			sentence();
			statementSequence();
		} else {
			//do nothing
		}
	}

	private void sentence() throws SyntaxError, IOException {
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
			//sentence();
			displayList();
			check((int) ')');
			check((int) ';');
		} else if (token.getTag() == Tag.WHILE) {
			check(Tag.WHILE);
			check((int) '(');
			eBool();
			check((int) ')');
			sentence();
		} else if (token.getTag() == (int) '{') {
			check((int) '{');
			// sentence();
			statementSequence();
			check((int) '}');
		} else if (token.getTag() == Tag.IF) {
			check(Tag.IF);
			check((int) '(');
			eBool();
			check((int) ')');
			sentence();
			restIf();
		}
	}

	private void restIf() throws SyntaxError, IOException {
		if (token.getTag() == Tag.ELSE) {
			check(Tag.ELSE);
			sentence();
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
		else {
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
			} else if (token.getTag() == '>') {
				check((int) '>');
				expression();
			} else if (token.getTag() == '<') {
				check((int) '<');
				expression();
			} else { // Not a expected value, throw error
				throw new SyntaxError();
			}
		}
	}

	private void displayList() throws SyntaxError, IOException {
		try {
			element();
			elementSequence();
		} catch (Exception e) {
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
