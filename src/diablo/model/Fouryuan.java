package diablo.model;

/**
 * 四元式的数据结构
 * 
 * @author mgy
 * 
 */
public class Fouryuan {
	public String op, arg1, arg2, result;// 四元式的运算符、两个参数、结果
	public static int count = 1;// 记录四元式目前行数
	public int num;// 该四元式的行数

	public Fouryuan(String op, String arg1, String arg2, String result) {
		this.num = count++;
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.result = result;
	}

	public Fouryuan(String op, String arg1, String arg2) {
		this.num = count++;
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.result = num + 3 + "";
	}

	// 四元式的显示方式
	@Override
	public String toString() {
		return num + "  (" + op + "," + arg1 + "," + arg2 + "," + result + ")";
	}

	// 设置跳转行
	public void setResult(String s) {
		this.result = s;
	}

	// 得到当前行数
	public static int getLineNum() {
		return count;
	}

	// 行数清零
	public static void clear() {
		count = 1;
	}
}
