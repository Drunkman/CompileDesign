package diablo.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import diablo.analyzer.GrammerAnalyzer;
import diablo.analyzer.MorphologyAnalyzer;
import diablo.model.Fouryuan;
import diablo.model.Item;

/**
 * 主界面（包括菜单项、编辑框、按钮组件、结果框等）
 * 
 * @author mgy
 * 
 */
public class MainView extends JFrame implements ActionListener, FocusListener {

	private JPanel panel;
	private JTextArea text_edit, text_result;
	private JButton button_morphology, button_grammer_semantic, button_run;
	private JScrollPane scrollPane, scrollPane1;
	private File file = null;
	private ArrayList<Item> items;
	private ArrayList list;

	public MainView() {
		super("编译原理课程设计");

		setResizable(false);// 使界面大小不能改变
		setBounds(250, 0, 800, 700);// 设置位置和代码
		setDefaultCloseOperation(EXIT_ON_CLOSE);// 设置点击退出
		panel = new JPanel(null);// 布局方式为绝对布局

		// 菜单栏
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		String[] menustr = { "文件", "帮助" };// 两个菜单项
		JMenu[] menu = new JMenu[menustr.length];
		for (int i = 0; i < menu.length; i++) {
			menu[i] = new JMenu(menustr[i]);
			menuBar.add(menu[i]);
		}

		// 文件操作
		JMenuItem menuitem_open = new JMenuItem("打开");
		menuitem_open.addActionListener(this);
		menu[0].add(menuitem_open);
		JMenuItem menuitem_save = new JMenuItem("保存");
		menuitem_save.addActionListener(this);
		menu[0].add(menuitem_save);
		JMenuItem menuitem_othersave = new JMenuItem("另存为");
		menuitem_othersave.addActionListener(this);
		menu[0].add(menuitem_othersave);
		JMenuItem menuitem_exit = new JMenuItem("关闭文件");
		menuitem_exit.addActionListener(this);
		menu[0].add(menuitem_exit);

		// 帮助操作
		JMenuItem menuitem_about = new JMenuItem("关于");
		menuitem_about.addActionListener(this);
		menu[1].add(menuitem_about);
		JMenuItem menuitem_state = new JMenuItem("使用说明");
		menuitem_state.addActionListener(this);
		menu[1].add(menuitem_state);

		// 编辑框
		text_edit = new JTextArea("Source:");
		text_edit.addFocusListener(this);
		scrollPane = new JScrollPane(text_edit);
		scrollPane.setBounds(5, 10, 770, 280);
		Border border = new LineBorder(Color.BLACK, 1);
		scrollPane.setBorder(border);
		panel.add(scrollPane);

		// 按钮组件
		JPanel panel_button = new JPanel();
		panel_button.setBounds(5, 300, 770, 25);
		GridLayout layout_panel_button = new GridLayout(1, 3);
		layout_panel_button.setHgap(5);
		layout_panel_button.setVgap(5);
		panel_button.setLayout(layout_panel_button);

		// 词法分析按钮
		button_morphology = new JButton("词法分析");
		button_morphology.addActionListener(this);
		panel_button.add(button_morphology);

		// 语法和语义分析按钮
		button_grammer_semantic = new JButton("语法和语义分析");
		button_grammer_semantic.addActionListener(this);
		panel_button.add(button_grammer_semantic);

		// 运行按钮
		button_run = new JButton("运行");
		button_run.addActionListener(this);
		panel_button.add(button_run);

		panel.add(panel_button);

		// 结果框
		text_result = new JTextArea("Result:");
		text_result.setEditable(false);
		scrollPane1 = new JScrollPane(text_result);
		scrollPane1.setBounds(5, 335, 770, 290);
		scrollPane1.setBorder(border);
		panel.add(scrollPane1);

		add(panel);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 对菜单项的操作
		if (e.getSource() instanceof JMenuItem) {
			if (e.getActionCommand() == "打开") {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(fileChooser);
				file = fileChooser.getSelectedFile();
				if (file == null)
					return;
				this.setTitle(file.getPath());
				try {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					String s = null;
					text_edit.setText("");
					while ((s = reader.readLine()) != null) {
						text_edit.append(s + "\n");
					}
					reader.close();
				} catch (Exception e1) {
					text_result.setText("打开时发生错误！");
				}
			} else if (e.getActionCommand() == "保存") {
				save();
			} else if (e.getActionCommand() == "另存为") {
				othersave();
			} else if (e.getActionCommand() == "关闭文件") {
				if (file != null) {
					file = null;
					this.setTitle("编译原理课程设计");
				}
				text_edit.setText("");
			} else if (e.getActionCommand() == "关于") {
				JOptionPane.showMessageDialog(this,
						"2015年 编译原理课程设计\n组员：马高宇、方源\n如有问题，请联系18840863562", "关于",
						JOptionPane.PLAIN_MESSAGE);
			}
		}

		// 对按钮的操作
		else if (e.getSource() instanceof JButton) {
			JOptionPane.showMessageDialog(this, "文件将被保存", "编译原理课程设计",
					JOptionPane.CANCEL_OPTION);
			save();
			BufferedReader reader = null;
			try {
				if (file == null)
					return;
				reader = new BufferedReader(new FileReader(file));
			} catch (Exception e1) {
				text_result.setText("数据输出发生错误");
			}
			if (e.getSource() == button_morphology) {
				if (reader != null)
					items = new MorphologyAnalyzer(reader).analysis();
				else
					text_result.setText("数据输出发生错误");
				show(items);
			} else if (e.getSource() == button_grammer_semantic) {
				if (reader != null)
					items = new MorphologyAnalyzer(reader).analysis();
				else
					text_result.setText("数据输出发生错误");
				if (items != null) {
					list = new GrammerAnalyzer(items).analysis();
					text_result.setText("");
					int i = 0;
					for (Object object : list) {
						text_result.append(object + "\n");
					}
					Fouryuan.clear();
				}
			} else if (e.getSource() == button_run) {
				System.out.println("运行");
			}

		}
	}

	// 分析结果的输出
	private void show(ArrayList<Item> items2) {
		StringBuilder sb = new StringBuilder();
		int row = 1;
		for (Item item : items) {
			if (item.rownum == row) {
				sb.append(item + "\t");
			} else {
				sb.append("\n" + item + "\t");
				row = item.rownum;
			}
		}

		text_result.setText(sb.toString());
	}

	// 鼠标进入文本框的效果
	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == text_edit) {
			if (text_edit.getText().equals("Source:")) {
				text_edit.setText("");
			}
		}
	}

	// 鼠标退入文本框的效果
	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == text_edit && file == null) {
			if (text_edit.getText().equals("")) {
				text_edit.setText("Source:");
			}
		}
	}

	// 文件保存
	public void save() {
		if (file == null) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.showSaveDialog(fileChooser);
			file = fileChooser.getSelectedFile();
			if (file == null)
				return;
			this.setTitle(file.getPath());
		}
		try {
			if (text_edit.getText().equals("Source:")) {
				text_edit.setText("");
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String s = text_edit.getText();
			writer.write(s);
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			text_result.setText("保存时发生错误！");
		}
	}

	// 文件另存为
	public void othersave() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.showSaveDialog(fileChooser);
		file = fileChooser.getSelectedFile();
		if (file == null)
			return;
		this.setTitle(file.getPath());
		try {
			if (text_edit.getText().equals("Source:")) {
				text_edit.setText("");
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			String s = text_edit.getText();
			writer.write(s);
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			text_result.setText("保存时发生错误！");
		}
	}
}
