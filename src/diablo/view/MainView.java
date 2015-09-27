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
 * �����棨�����˵���༭�򡢰�ť����������ȣ�
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
		super("����ԭ��γ����");

		setResizable(false);// ʹ�����С���ܸı�
		setBounds(250, 0, 800, 700);// ����λ�úʹ���
		setDefaultCloseOperation(EXIT_ON_CLOSE);// ���õ���˳�
		panel = new JPanel(null);// ���ַ�ʽΪ���Բ���

		// �˵���
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		String[] menustr = { "�ļ�", "����" };// �����˵���
		JMenu[] menu = new JMenu[menustr.length];
		for (int i = 0; i < menu.length; i++) {
			menu[i] = new JMenu(menustr[i]);
			menuBar.add(menu[i]);
		}

		// �ļ�����
		JMenuItem menuitem_open = new JMenuItem("��");
		menuitem_open.addActionListener(this);
		menu[0].add(menuitem_open);
		JMenuItem menuitem_save = new JMenuItem("����");
		menuitem_save.addActionListener(this);
		menu[0].add(menuitem_save);
		JMenuItem menuitem_othersave = new JMenuItem("���Ϊ");
		menuitem_othersave.addActionListener(this);
		menu[0].add(menuitem_othersave);
		JMenuItem menuitem_exit = new JMenuItem("�ر��ļ�");
		menuitem_exit.addActionListener(this);
		menu[0].add(menuitem_exit);

		// ��������
		JMenuItem menuitem_about = new JMenuItem("����");
		menuitem_about.addActionListener(this);
		menu[1].add(menuitem_about);
		JMenuItem menuitem_state = new JMenuItem("ʹ��˵��");
		menuitem_state.addActionListener(this);
		menu[1].add(menuitem_state);

		// �༭��
		text_edit = new JTextArea("Source:");
		text_edit.addFocusListener(this);
		scrollPane = new JScrollPane(text_edit);
		scrollPane.setBounds(5, 10, 770, 280);
		Border border = new LineBorder(Color.BLACK, 1);
		scrollPane.setBorder(border);
		panel.add(scrollPane);

		// ��ť���
		JPanel panel_button = new JPanel();
		panel_button.setBounds(5, 300, 770, 25);
		GridLayout layout_panel_button = new GridLayout(1, 3);
		layout_panel_button.setHgap(5);
		layout_panel_button.setVgap(5);
		panel_button.setLayout(layout_panel_button);

		// �ʷ�������ť
		button_morphology = new JButton("�ʷ�����");
		button_morphology.addActionListener(this);
		panel_button.add(button_morphology);

		// �﷨�����������ť
		button_grammer_semantic = new JButton("�﷨���������");
		button_grammer_semantic.addActionListener(this);
		panel_button.add(button_grammer_semantic);

		// ���а�ť
		button_run = new JButton("����");
		button_run.addActionListener(this);
		panel_button.add(button_run);

		panel.add(panel_button);

		// �����
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
		// �Բ˵���Ĳ���
		if (e.getSource() instanceof JMenuItem) {
			if (e.getActionCommand() == "��") {
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
					text_result.setText("��ʱ��������");
				}
			} else if (e.getActionCommand() == "����") {
				save();
			} else if (e.getActionCommand() == "���Ϊ") {
				othersave();
			} else if (e.getActionCommand() == "�ر��ļ�") {
				if (file != null) {
					file = null;
					this.setTitle("����ԭ��γ����");
				}
				text_edit.setText("");
			} else if (e.getActionCommand() == "����") {
				JOptionPane.showMessageDialog(this,
						"2015�� ����ԭ��γ����\n��Ա��������Դ\n�������⣬����ϵ18840863562", "����",
						JOptionPane.PLAIN_MESSAGE);
			}
		}

		// �԰�ť�Ĳ���
		else if (e.getSource() instanceof JButton) {
			JOptionPane.showMessageDialog(this, "�ļ���������", "����ԭ��γ����",
					JOptionPane.CANCEL_OPTION);
			save();
			BufferedReader reader = null;
			try {
				if (file == null)
					return;
				reader = new BufferedReader(new FileReader(file));
			} catch (Exception e1) {
				text_result.setText("���������������");
			}
			if (e.getSource() == button_morphology) {
				if (reader != null)
					items = new MorphologyAnalyzer(reader).analysis();
				else
					text_result.setText("���������������");
				show(items);
			} else if (e.getSource() == button_grammer_semantic) {
				if (reader != null)
					items = new MorphologyAnalyzer(reader).analysis();
				else
					text_result.setText("���������������");
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
				System.out.println("����");
			}

		}
	}

	// ������������
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

	// �������ı����Ч��
	@Override
	public void focusGained(FocusEvent e) {
		if (e.getSource() == text_edit) {
			if (text_edit.getText().equals("Source:")) {
				text_edit.setText("");
			}
		}
	}

	// ��������ı����Ч��
	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == text_edit && file == null) {
			if (text_edit.getText().equals("")) {
				text_edit.setText("Source:");
			}
		}
	}

	// �ļ�����
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
			text_result.setText("����ʱ��������");
		}
	}

	// �ļ����Ϊ
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
			text_result.setText("����ʱ��������");
		}
	}
}
