package diablo.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import diablo.model.Item;

/**
 * 词法分析器
 * 
 * @author mgy
 * 
 */
public class MorphologyAnalyzer {

	// private String[] keywords = { "abstract", "assert", "boolean", "break",
	// "byte", "case", "catch", "char", "class", "continue", "default",
	// "do", "double", "else", "extends", "false", "final", "float",
	// "finally", "for", "if", "implements", "import", "instanceof",
	// "int", "interface", "long", "native", "new", "null", "package",
	// "private", "protected", "public", "return", "short", "static",
	// "super", "switch", "synchronized", "this", "throw", "throws",
	// "transient", "true", "try", "void", "volatie", "while" };
	//
	// private String[] signs = { ".", "[", "]", "(", ")", "++", "--", "+", "-",
	// "~", "!", "*", "/", "%", "<<", ">>", "<", "<=", ">", ">=",
	// "==", "!=", "&", "^", "|", "||", "&&", "?", ":", "+=", "-=", "*=",
	// "/=", "%=", "&=", "^=", "|=", ";", "," };

	// 关键字集
	private String[] keywords = { "begin", "call", "const", "do", "end", "if",
			"procedure", "read", "then", "var", "while", "write" };

	// 符号集
	private String[] signs = { "+", "-", "*", "/", "=", "#", "<", "<=", ">",
			">=", "(", ")", ",", ".", ";", ":=", };

	private ArrayList<Item> items;// 词法分析的结果集
	private BufferedReader reader;// 要分析的输入流
	private CharFactory charFactory;// 字符工厂
	private static StringBuilder sb;// 组合字符

	public MorphologyAnalyzer(BufferedReader reader) {
		this.reader = reader;
		this.charFactory = new CharFactory(reader);
		sb = new StringBuilder();
		items = new ArrayList<Item>();
	}

	// 分析，返回结果集
	public ArrayList<Item> analysis() {
		Character c = charFactory.getNextChar();
		while (c != null) {
			if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
				letter(c);
			} else if (c >= '0' && c <= '9') {
				num(c, 0);
			} else {
				sign(c);
			}
			c = charFactory.getNextChar();
		}
		return items;
	}

	// 对标识符进行分析
	void letter(Character c) {
		int row = charFactory.getRowcount();
		sb.append(c);
		c = charFactory.getNextChar();
		while (c != null
				&& (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0'
						&& c <= '9' || c == '_')) {
			sb.append(c);
			c = charFactory.getNextChar();
		}
		String s = sb.toString();
		if (isKeyword(s)) {
			items.add(new Item("keyword", s, row));
		} else {
			items.add(new Item("identifier", s, row));
		}
		sb = new StringBuilder();
		if (c == null)
			return;
		else if (c >= '0' && c <= '9') {
			num(c, 0);
		} else {
			sign(c);
		}
	}

	// 对无符号实数进行分析
	void num(Character c, int flag) {
		int row = charFactory.getRowcount();
		sb.append(c);
		c = charFactory.getNextChar();
		while (c != null && (c >= '0' && c <= '9' || c == '.' && flag == 0)) {
			if (c == '.') {
				flag = 1;
				sb.append(c);
				c = charFactory.getNextChar();
				if (c != null && !(c >= '0' && c <= '9'))
					sb.append('0');
				continue;
			}
			sb.append(c);
			c = charFactory.getNextChar();
		}
		String s = sb.toString();
		items.add(new Item("number", s, row));
		sb = new StringBuilder();
		if (c == null)
			return;
		else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
			letter(c);
		} else {
			sign(c);
		}
	}

	// 对符号进行分析
	void sign(Character c) {
		int row = charFactory.getRowcount();
		if (c == ' ')
			c = charFactory.getNextChar();
		else if (c == ',' || c == ';' || c == '(' || c == ')' || c == '#'
				|| c == '=' || c == '.') {
			sb.append(c);
			items.add(new Item("sign", sb.toString(), row));
			c = charFactory.getNextChar();
			sb = new StringBuilder();
		} else if (c == '<') {
			sb.append(c);
			c = charFactory.getNextChar();
			if (c == '=') {
				sb.append(c);
				c = charFactory.getNextChar();
			}
			items.add(new Item("sign", sb.toString(), row));
			sb = new StringBuilder();
		} else if (c == '>') {
			sb.append(c);
			c = charFactory.getNextChar();
			if (c == '=') {
				sb.append(c);
				c = charFactory.getNextChar();
			}
			items.add(new Item("sign", sb.toString(), row));
			sb = new StringBuilder();
		} else if (c == '-') {
			sb.append(c);
			c = charFactory.getNextChar();
			items.add(new Item("sign", sb.toString(), row));
			sb = new StringBuilder();
		} else if (c == '+') {
			sb.append(c);
			c = charFactory.getNextChar();
			items.add(new Item("sign", sb.toString(), row));
			sb = new StringBuilder();
		} else if (c == '*') {
			sb.append(c);
			c = charFactory.getNextChar();
			items.add(new Item("sign", sb.toString(), row));
			sb = new StringBuilder();
		} else if (c == '/') {
			sb.append(c);
			c = charFactory.getNextChar();
			items.add(new Item("sign", sb.toString(), row));
			sb = new StringBuilder();
		} else if (c == ':') {
			sb.append(c);
			c = charFactory.getNextChar();
			if (c == '=') {
				sb.append(c);
				c = charFactory.getNextChar();
			}
			items.add(new Item("sign", sb.toString(), row));
			sb = new StringBuilder();
		} else {
			items.add(new Item("error", c.toString(), row));
			c = charFactory.getNextChar();
		}
		if (c == null)
			return;
		else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
			letter(c);
		} else if (c >= '0' && c <= '9') {
			num(c, 0);
		} else {
			sign(c);
		}
	}

	// 是否是关键字
	boolean isKeyword(String s) {
		for (String keyword : keywords) {
			if (s.equals(keyword))
				return true;
		}
		return false;
	}

	// 是否是符号
	boolean isSign(String s) {
		for (String sign : signs) {
			if (s.equals(sign))
				return true;
		}
		return false;
	}
}
