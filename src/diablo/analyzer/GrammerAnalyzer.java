package diablo.analyzer;

import java.util.ArrayList;

import diablo.model.Fouryuan;
import diablo.model.Item;

/**
 * 语法和语义分析器
 * 
 * @author mgy
 * 
 */
public class GrammerAnalyzer {
	private ArrayList<Item> items;// 词法分析的结果集
	private ArrayList<Fouryuan> fouryuans;// 四元式集合
	private ArrayList<String> consts, vars;// 常量和变量的集合
	private ArrayList<String> errors;// 错误的集合
	private ItemFactory itemFactory;// Item工厂
	private int count = 0;// 作为生成的t变量的下标

	public GrammerAnalyzer(ArrayList<Item> items) {
		this.items = items;
		itemFactory = new ItemFactory(items);
		fouryuans = new ArrayList<Fouryuan>();
		consts = new ArrayList<String>();
		vars = new ArrayList<String>();
		errors = new ArrayList<String>();
	}

	// 分析
	public ArrayList analysis() {
		block();
		if (next() != null && next().value.equals(".")) {
			getNext();
			System.out.println("语法分析结束");
		} else {
			errors.add(getItem().rownum + "行:缺少 .");
		}
		// 如果分析的结果有错误，返回errors
		if (errors.isEmpty())
			return fouryuans;
		// 如果没有错误，返回四元式的集合
		else
			return errors;
	}

	// 分程序
	private void block() {
		while (next() != null) {
			Item item = next();
			if (item.value.equals("const")) {
				constPart();
			} else if (item.value.equals("var")) {
				varPart();
			} else if (item.value.equals("procedure")) {
				procePart();
			} else {
				break;
			}
		}
		if (next() != null)
			statement();
	}

	// 语句
	private void statement() {
		Item item = next();// 预读一位
		if (item == null)
			return;
		if (item.isIdentifier()) {
			assignment();
		} else if (item.value.equals("if")) {
			conditionstate();
		} else if (item.value.equals("begin")) {
			compound();
		} else if (item.value.equals("while")) {
			loop();
		} else if (item.value.equals("read")) {
			read();
		} else if (item.value.equals("write")) {
			write();
		} else {
			return;
		}
	}

	// 条件语句
	private void conditionstate() {
		getNext();
		condition();
		fouryuans.add(new Fouryuan("=", "false", "_", "t" + count++));
		Fouryuan fy = new Fouryuan("jp", "_", "_");// 条件错误的跳转语句
		fouryuans.add(fy);
		if (next() != null && next().value.equals("then")) {
			getNext();
			statement();
			int i = getLineNum();
			fy.setResult(i + "");// 在此应用了回填拉链技术
		} else {
			errors.add(getItem().rownum + "行:缺少then");
		}
	}

	// 条件
	private void condition() {
		Item item = next();
		if (item != null
				&& (item.isAdd() || item.isIdentifier() || item.isNum() || item.value
						.equals(")"))) {
			String arg1 = expression();
			if (next() != null && next().isRelational()) {
				String op = "j" + getNext().value;
				String arg2 = expression();
				fouryuans.add(new Fouryuan(op, arg1, arg2));// 条件正确的跳转语句
			} else {
				errors.add(getItem().rownum + "行:缺少关系运算符");
			}
		} else {
			errors.add(getItem().rownum + "行:条件语句出错");
		}
	}

	// 写语句
	private void write() {
		getNext();
		if (next() != null && next().value.equals("(")) {
			getNext();
			expression();
			while (next() != null && next().value.equals(",")) {
				getNext();
				expression();
			}
			if (next() != null && next().value.equals(")")) {
				getNext();
			} else {
				errors.add(getItem().rownum + "行:缺少 )");
			}
		} else {
			errors.add(getItem().rownum + "行:缺少 (");
		}
	}

	// 读语句
	private void read() {
		getNext();
		if (next() != null && next().value.equals("(")) {
			getNext();
			identity();
			while (next() != null && next().value.equals(",")) {
				getNext();
				identity();
			}
			if (next() != null && next().value.equals(")")) {
				getNext();
			} else {
				errors.add(getItem().rownum + "行:缺少 )");
			}
		} else {
			errors.add(getItem().rownum + "行:缺少 )");
		}
	}

	// 当型循环语句
	private void loop() {
		getNext();
		condition();
		fouryuans.add(new Fouryuan("=", "false", "_", "t" + count++));
		Fouryuan fy = new Fouryuan("jp", "_", "_");// 条件错误的跳转语句
		int j = fy.num - 2;
		fouryuans.add(fy);
		if (next() != null && next().value.equals("do")) {
			getNext();
			statement();
			fouryuans.add(new Fouryuan("jp", "_", "_", j + ""));
			int i = getLineNum();
			fy.setResult(i + "");// 在此应用了回填拉链技术
		} else {
			errors.add(getItem().rownum + "行:缺少 do");
		}
	}

	// 复合语句
	private void compound() {
		getNext();
		statement();
		while (next() != null && next().value.equals(";")) {
			getNext();
			statement();
		}
		if (next() != null && next().value.equals("end")) {
			getNext();
		} else {
			errors.add(getItem().rownum + "行:缺少end");
		}
	}

	// 赋值语句
	private void assignment() {
		String result = getNext().value;
		if (isConst(result)) {
			errors.add(getItem().rownum + "行:常量不能再定义");
		}
		if (!isVar(result)) {
			errors.add(getItem().rownum + "行:无此变量");
		}
		if (next() != null && next().value.equals(":=")) {
			String op = getNext().value;
			String arg1 = expression();
			fouryuans.add(new Fouryuan(op, arg1, "_", result));// 赋值语句的四元式
		} else {
			errors.add(getItem().rownum + "行:缺少 :=");
		}
	}

	// 表达式，返回表达式最后的结果
	private String expression() {
		String sign = "";
		if (next() != null && next().isAdd()) {
			sign = getNext().value;
		}
		String arg1 = nape();
		while (next() != null && next().isAdd()) {
			String op = getNext().value;
			String arg2 = nape();
			String result = "t" + count++;
			fouryuans.add(new Fouryuan(op, arg1, arg2, result));
			arg1 = result;
		}
		return arg1;
	}

	// 项
	private String nape() {
		String arg1 = factor();
		while (next() != null && next().isMul()) {
			String op = getNext().value;
			String arg2 = factor();
			String result = "t" + count++;
			fouryuans.add(new Fouryuan(op, arg1, arg2, result));
			arg1 = result;
		}
		return arg1;
	}

	// 因子
	private String factor() {
		Item item = next();
		if (item != null && item.isIdentifier()) {
			return getNext().value;
		} else if (item != null && item.isNum()) {
			return getNext().value;
		} else if (item != null && item.value.equals("(")) {
			getNext();
			String s = expression();
			if (next() != null && next().value.equals(")")) {
				getNext();
			} else {
				errors.add(getItem().rownum + "行:缺少 )");
			}
			return s;
		} else {
			errors.add(getItem().rownum + "行:因子出错");
			return null;
		}
	}

	// 过程说明部分
	private void procePart() {
		processPre();
		block();
		if (next() != null && next().value.equals(";")) {
			getNext();
			if (next() != null && next().value.equals("procedure")) {
				getNext();
				procePart();
			}
		} else {
			errors.add(getItem().rownum + "行:缺少‘;’");
		}
	}

	// 过程首部
	private void processPre() {
		if (next() != null && next().value.equals("procedure")) {
			getNext();
			if (next() != null && next().isIdentifier()) {
				getNext();
				if (next() != null && next().value.equals(";")) {
					getNext();
					return;
				} else {
					errors.add(getItem().rownum + "行:缺少 ;");
				}
			} else {
				errors.add(getItem().rownum + "行:缺少标识符");
			}
		} else {
			errors.add(getItem().rownum + "行:缺少 procedure");
		}
	}

	// 变量说明部分
	private void varPart() {
		getNext();
		String s = identity();
		if (s == null)
			return;
		if (!isVar(s) && !isConst(s)) {
			vars.add(s);
		} else {
			errors.add(getItem().rownum + "行:重复定义变量");
		}
		while (next() != null && next().value.equals(",")) {
			getNext();
			String ss = identity();
			if (ss == null)
				return;
			if (!isVar(ss) && !isConst(ss)) {
				vars.add(ss);
			} else {
				errors.add(getItem().rownum + "行:重复定义同名变量");
			}
		}
		if (next() != null && next().value.equals(";")) {
			getNext();
		} else {
			errors.add(getItem().rownum + "行:缺少 ;");
		}
	}

	// 常量说明部分
	private void constPart() {
		getNext();
		constDefinition();
		while (next() != null && next().value.equals(",")) {
			getNext();
			constDefinition();
		}
		if (next() != null && next().value.equals(";")) {
			getNext();
		} else {
			errors.add(getItem().rownum + "行:缺少‘;’");
		}
	}

	// 常量定义
	private String constDefinition() {
		String op = null, arg1 = null, result = null;
		if (next() != null && next().isIdentifier()) {
			result = getNext().value;
			if (!isConst(result) && !isVar(result)) {
				consts.add(result);
			} else {
				errors.add(getItem().rownum + "行:常量不能再定义");
			}
		} else {
			errors.add(getItem().rownum + "行:常量定义错误");
		}
		if (next() != null && next().value.equals("=")) {
			op = getNext().value;
		} else {
			errors.add(getItem().rownum + "行:常量定义错误");
		}
		if (next() != null && next().isNum()) {
			arg1 = getNext().value;
		} else {
			errors.add(getItem().rownum + "行:常量定义错误");
		}
		if (op != null && arg1 != null && result != null) {
			fouryuans.add(new Fouryuan(op, arg1, "_", result));
			return result;
		} else {
			return null;
		}
	}

	// 标识符
	private String identity() {
		if (next() != null && next().isIdentifier()) {
			return getNext().value;
		} else {
			errors.add(getItem().rownum + "行:缺少 标识符");
			return null;
		}
	}

	// 获取下一个item
	private Item getNext() {
		return itemFactory.getNextItem();
	}

	// 预读下一个item
	private Item next() {
		return itemFactory.next();
	}

	// 获得当前Item
	private Item getItem() {
		return itemFactory.getItem();
	}

	// 是否已经存在于变量表中
	private boolean isVar(String s) {
		for (String var : vars) {
			if (var.equals(s))
				return true;
		}
		return false;
	}

	// 是否已经存在于常量表中
	private boolean isConst(String s) {
		for (String cons : consts) {
			if (cons.equals(s))
				return true;
		}
		return false;
	}

	// 当前四元式的编号
	private int getLineNum() {
		return Fouryuan.getLineNum();
	}
}
