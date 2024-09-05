import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.about.Sobre;
import com.buttons.animated.EffectButton;
import com.comboBox.comboSuggestion.ComboBoxSuggestion;
import com.copy.textarea.CopyTextArea;
import com.dialog.popup.MaterialJOptionPane;
import com.draganddrop.DragAndDrop;
import com.draganddrop.UtilDragAndDrop;
import com.jicons.Ojo;
import com.layout.MaterialPanelLayout;
import com.panels.others.CopyPanel;
import com.textField.text.NTextField;

import mthos.JMthos;

@SuppressWarnings("all")

public class AgendaEmail extends javax.swing.JFrame {

	private NTextField textField;

	private DragAndDrop drag;

	private NTextField textField_1;

	private ArrayList<JComponent> lista;

	private ArrayList<Integer> porcentajes;

	private JPanel panel1;

	private JPanel panel2;

	private CopyTextArea panel3;

	private JFrame frame;

	private ComboBoxSuggestion<String> combo;

	private EffectButton delete;

	private EffectButton reload;

	private String rutaDb;

	public static void insertSQLite(String db, String table, List<String> columns, List<String> values) {

		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + db);

				PreparedStatement pstmt = conn
						.prepareStatement("INSERT INTO " + table + " (" + String.join(",", columns) + ") VALUES ("
								+ values.stream().map(v -> "?").collect(Collectors.joining(",")) + ")")) {

			for (int i = 0; i < values.size(); i++) {

				pstmt.setString(i + 1, values.get(i));

			}

			pstmt.executeUpdate();

		}

		catch (SQLException e) {

			e.printStackTrace();

		}

	}

	public static Set<String> readEmailsFromExcel(String filePath) {

		Set<String> emailSet = new HashSet<>();

		try (FileInputStream fis = new FileInputStream(filePath); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheetAt(0);

			for (Row row : sheet) {

				Cell emailCell = row.getCell(10);

				if (emailCell != null && emailCell.getCellType() == CellType.STRING) {

					String email = emailCell.getStringCellValue().trim();

					if (!email.isEmpty() && !email.equals("E-mail")) {

						emailSet.add(email);

					}

				}

			}

		}

		catch (IOException e) {

		}

		return emailSet;

	}

	public AgendaEmail() {

		try {

			rutaDb = JMthos.rutaActual() + "db" + JMthos.saberSeparador() + "db.db";

		}

		catch (IOException e) {

		}

		combo = new ComboBoxSuggestion<String>();

		ArrayList<String> columnsLista = new ArrayList<>();

		columnsLista.add("NOMBRE");

		ArrayList<String> datosLista = JMthos.selectSQlite(rutaDb, "SELECT NOMBRE FROM PERFILES", columnsLista);

		for (String valor : datosLista) {

			combo.addItem(valor);

		}

		delete = new EffectButton("-");

		delete.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				String valorSeleccionado = "";

				try {

					int numeroCombo = combo.getItemCount();

					if (numeroCombo > 0) {

						valorSeleccionado = combo.getSelectedItem().toString();

						combo.removeItemAt(combo.getSelectedIndex());

						JMthos.deleteSQLite(rutaDb, "PERFILES", "NOMBRE", valorSeleccionado);

						if (numeroCombo == 1) {

							JMthos.deleteAllFromTableSqlite(rutaDb, "PERFILES");

						}

					}

				}

				catch (Exception e1) {

					e1.printStackTrace();

				}

			}

		});

		delete.setShadowColor(Color.WHITE);

		reload = new EffectButton("");

		reload.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {
				try {
					ArrayList<String> columnsLista = new ArrayList<>();

					columnsLista.add("EMAILS");

					columnsLista.add("ASUNTO");

					columnsLista.add("MENSAJE");

					ArrayList<String> datosLista = JMthos.selectSQlite(rutaDb,
							"SELECT EMAILS,ASUNTO,MENSAJE FROM PERFILES WHERE NOMBRE='"
									+ combo.getSelectedItem().toString() + "'",
							columnsLista);

					textField.setText(datosLista.get(0));

					textField_1.setText(datosLista.get(1));

					panel3.setText(datosLista.get(2));

				}

				catch (Exception e1) {

				}

			}

		});

		reload.setBackground(Color.WHITE);

		reload.setIcon(new Ojo());

		reload.setShadowColor(Color.WHITE);

		frame = this;

		lista = new ArrayList<>();

		textField_1 = new NTextField();

		textField_1.setHeaderText("Asunto");

		textField_1.setColumns(10);

		textField = new NTextField();

		textField.setHeaderText("Destinatarios (poner en Cco)");

		getContentPane().setLayout(null);

		lista.add(textField);

		CopyPanel copy = new CopyPanel(textField.getText());

		lista.add(copy);

		porcentajes = new ArrayList<>();

		porcentajes.add(90);

		porcentajes.add(10);

		MaterialPanelLayout panelTest = new MaterialPanelLayout(lista, null, false);

		CopyPanel copy2 = new CopyPanel(textField_1.getText());

		panel1 = new JPanel();

		panel1.setBounds(0, 10, 639, 107);

		panel2 = new JPanel();

		panel2.setBackground(Color.WHITE);

		panel2.setBounds(32, 142, 607, 107);

		panel3 = new CopyTextArea(false, false, false, false);

		panel3.setBounds(0, 271, 813, 221);

		JPanel perfiles = new JPanel();

		perfiles.setLayout(new GridLayout());

		perfiles.add(combo);

		EffectButton add = new EffectButton("+");

		add.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				try {

					String dato1 = textField.getText().trim();

					String dato2 = textField_1.getText().trim();

					String dato3 = panel3.getText().trim();

					if (!dato1.isEmpty() && !dato2.isEmpty() && !dato3.isEmpty()) {

						NTextField textField3 = new NTextField();

						textField3.setHorizontalAlignment(SwingConstants.CENTER);

						EffectButton buttonOk = new EffectButton();

						buttonOk.setBackground(Color.WHITE);

						buttonOk.setShadowColor(Color.WHITE);

						EffectButton buttonCancel = new EffectButton();

						buttonCancel.setBackground(Color.WHITE);

						buttonCancel.setShadowColor(Color.WHITE);

						String title = "Entrada de Datos";

						String label = "                    Introduce el nombre";

						Runnable callback = () -> {

							String texto = textField3.getText().trim();

							if (!texto.isEmpty()) {

								combo.addItem(texto);

								ArrayList<String> lista = new ArrayList<>();

								lista.add("NOMBRE");

								lista.add("EMAILS");

								lista.add("ASUNTO");

								lista.add("MENSAJE");

								ArrayList<String> valores = new ArrayList<>();

								valores.add(texto);

								valores.add(dato1);

								valores.add(dato2);

								valores.add(dato3);

								JMthos.insertSQLite(rutaDb, "PERFILES", lista, valores);

							}

						};

						MaterialJOptionPane.showCustomInputDialog(null, textField3, buttonOk, buttonCancel, 400, 200,
								title, label, "Aceptar", "Cancelar", null, callback);

					}

				}

				catch (Exception e1) {

				}

			}

		});

		add.setShadowColor(Color.WHITE);

		add.setBounds(649, 10, 154, 44);

		addComponentListener(new ComponentAdapter() {

			@Override

			public void componentResized(ComponentEvent e) {

				int ancho = getWidth();

				int alto = getHeight();

				panel1.setBounds(0, 0, ancho, Math.round(alto * 0.2f));

				panelTest.setBounds(panelTest.getX(), 0, Math.round(ancho * 0.53f), Math.round(alto * 0.2f));

				panel2.setBounds(0, Math.round(alto * 0.2f), ancho, Math.round(alto * 0.2f));

				panel3.setBounds(0, Math.round(alto * 0.4f), Math.round(ancho * 0.98f), Math.round(alto * 0.3f));

				perfiles.setBounds(0, Math.round(alto * 0.7f), Math.round(ancho * 0.7f), Math.round(alto * 0.17f));

				add.setBounds(Math.round(ancho * 0.7f) + 5, Math.round(alto * 0.705f) + 5, Math.round(ancho * 0.1f),
						Math.round(alto * 0.17f) - 10);

				reload.setBounds(Math.round(ancho * 0.8f), Math.round(alto * 0.705f) + 5, Math.round(ancho * 0.1f),
						Math.round(alto * 0.17f) - 10);

				delete.setBounds(Math.round(ancho * 0.9f), Math.round(alto * 0.705f) + 5, Math.round(ancho * 0.1f),
						Math.round(alto * 0.17f) - 10);

			}

		});

		try {

			getContentPane().setBackground(Color.WHITE);

			setIconImage(Toolkit.getDefaultToolkit().getImage(AgendaEmail.class.getResource("/image/emailaaa.png")));

			setTitle("AgendaEmail");

			copy2.getButton().addMouseListener(new MouseAdapter() {

				@Override

				public void mousePressed(MouseEvent e) {

					JMthos.copy(textField_1.getText());

				}

			});

			getContentPane().add(panel3);

			drag = new DragAndDrop("Suelte aqui xlsx");

			drag.setBounds(161, 10, 199, 84);

			new UtilDragAndDrop(drag, drag.dragBorder, true, new UtilDragAndDrop.Listener() {

				@Override

				public void filesDropped(java.io.File[] archivos) {

					Set<String> lista = new HashSet<>();

					for (File f : archivos) {

						lista.addAll(readEmailsFromExcel(f.getAbsolutePath()));

					}

					StringBuilder text = new StringBuilder();

					String coma = "";

					for (String email : lista) {

						text.append(coma).append(email);

						coma = ",";
					}

					textField.setText(text.toString());

				}

			});

			drag.setForeground(Color.GRAY);

			drag.setFont(new Font("Dialog", Font.PLAIN, 22));

			getContentPane().add(panel1);

			panel1.setBackground(Color.WHITE);

			panel1.setLayout(null);

			JLabel lblNewLabel_3 = new JLabel("Destinatarios");

			lblNewLabel_3.setBounds(10, 1, 141, 93);

			lblNewLabel_3.setForeground(Color.BLACK);

			lblNewLabel_3.setBackground(Color.WHITE);

			lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 25));

			lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);

			panel1.add(lblNewLabel_3);

			panel1.add(drag);

			panelTest.setBounds(370, 10, 184, 87);

			panel1.add(panelTest);

			lista = new ArrayList<>();

			textField.setColumns(10);

			lista.add(textField_1);

			lista.add(copy2);

			porcentajes = new ArrayList<>();

			porcentajes.add(90);

			porcentajes.add(10);

			copy.getButton().addMouseListener(new MouseAdapter() {

				@Override

				public void mousePressed(MouseEvent e) {

					JMthos.copy(textField.getText());

				}

			});

			getContentPane().add(panel2);

			panel2.setLayout(null);

			JLabel lblNewLabel_2 = new JLabel("Asunto");

			lblNewLabel_2.setBounds(10, 10, 75, 87);

			panel2.add(lblNewLabel_2);

			lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 25));

			lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);

			MaterialPanelLayout panelAsunto = new MaterialPanelLayout(lista, porcentajes, false);

			panelAsunto.setBounds(110, 10, 700, 84);

			panel2.add(panelAsunto);

			getContentPane().add(perfiles);

			getContentPane().add(add);

			delete.setBounds(649, 64, 154, 50);
			getContentPane().add(delete);

			reload.setBounds(649, 129, 154, 44);
			getContentPane().add(reload);

			JMenuBar menuBar = new JMenuBar();
			menuBar.setBackground(Color.WHITE);

			setJMenuBar(menuBar);

			JMenuItem mntmNewMenuItem = new JMenuItem("Sobre");
			mntmNewMenuItem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
			mntmNewMenuItem.setBackground(Color.WHITE);

			mntmNewMenuItem.addMouseListener(new MouseAdapter() {

				@Override

				public void mousePressed(MouseEvent e) {

					try {

						new Sobre("Programa creado por", "Ramón Jesús Gómez Carmona", "ramonjgomezcarmona@gmail.com", 0,
								0, 0);
					}

					catch (Exception e1) {

					}

				}

			});

			menuBar.add(mntmNewMenuItem);

			initComponents();

			setVisible(true);

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static void main(String[] args) {

		try {

			new AgendaEmail().setVisible(true);

		}

		catch (Exception e) {

		}

	}

	public void initComponents() throws IOException {

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

		setResizable(false);

		setSize(new Dimension(827, 555));

		setLocationRelativeTo(null);

	}

	public void actionPerformed(ActionEvent arg0) {

	}

	public void stateChanged(ChangeEvent e) {

	}
}
