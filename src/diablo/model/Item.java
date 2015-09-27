package diablo.model;

/**
 * 词法分析的数据结构
 * 
 * @author mgy
 * 
 */
public class Item {
	public String type, value;// 所分析词条的类型和值
	public int rownum;// 所分析词条的所在行数

	public Item(String type, String value, int rownum) {
		this.type = type;
		this.value = value;
		this.rownum = rownum;
	}

	// 是否是标识符
	public boolean isIdentifier() {
		return type.equals("identifier");
	}

	// 是否是数字
	public boolean isNum() {
		return type.equals("number");
	}

	// 是否是关系运算符
	public boolean isRelational() {
		return value.equals("=") || value.equals("#") || value.equals("<")
				|| value.equals("<=") || value.equals(">")
				|| value.equals(">=");
	}

	// 是否是加法运算符
	public boolean isAdd() {
		return value.equals("+") || value.equals("-");
	}

	// 是否是乘法运算符
	public boolean isMul() {
		return value.equals("*") || value.equals("/");
	}

	// 每一项的输出方式
	@Override
	public String toString() {
		return "<" + type + ",\"" + value + "\">";
	}

}
