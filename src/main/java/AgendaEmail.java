import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

import javax.swing.ImageIcon;
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
import com.dialog.confirm.MessageDialog;
import com.dialog.confirm.MessageDialog.MessageType;
import com.dialog.popup.MaterialJOptionPane;
import com.dialog.popup.VentanaEmergente;
import com.draganddrop.DragAndDrop;
import com.draganddrop.UtilDragAndDrop;
import com.jicons.Ojo;
import com.jicons.Reload;
import com.layout.MaterialPanelLayout;
import com.message.alerts.PopupAlerts;
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

	private JLabel update;

	private int identificador;

	private void verDatosSeleccionados() {

		try {

			ArrayList<String> datosLista = JMthos.selectSqlite(rutaDb,
					"SELECT ID,EMAILS,ASUNTO,MENSAJE FROM PERFILES WHERE NOMBRE='" + combo.getSelectedItem().toString()
							+ "'");

			textField.setText(datosLista.get(1));

			textField_1.setText(datosLista.get(2));

			panel3.setText(datosLista.get(3));

			identificador = Integer.parseInt(datosLista.get(0));

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

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

	private static int findLocalidadColumnIndex(XSSFSheet sheet) {

		Row headerRow = sheet.getRow(0);

		if (headerRow == null) {

			return -1;

		}

		for (Cell cell : headerRow) {

			if (cell.getCellType() == CellType.STRING
					&& "Localidad".equalsIgnoreCase(cell.getStringCellValue().trim())) {

				return cell.getColumnIndex();

			}

		}

		return -1;

	}

	public static Set<String> readEmailsFromExcel(String filePath, ArrayList<String> lista) {

		Set<String> emailSet = new HashSet<>();

		try (FileInputStream fis = new FileInputStream(filePath); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			XSSFSheet sheet = workbook.getSheetAt(0);

			int emailColumnIndex = findEmailColumnIndex(sheet, 0);

			Cell emailCell;

			String email;

			boolean empty = false;

			if (lista == null) {

				empty = true;

			}

			else {

				empty = lista.isEmpty();

			}

			Cell localidadCell;

			String localidad;

			int localidadColumnIndex = 0;

			if (!empty) {

				localidadColumnIndex = findLocalidadColumnIndex(sheet);

			}

			for (Row row : sheet) {

				emailCell = row.getCell(emailColumnIndex);

				if (empty) {

					if (emailCell != null && emailCell.getCellType() == CellType.STRING) {

						email = emailCell.getStringCellValue().trim();

						if (!email.isEmpty() && !email.equals("E-mail")) {

							emailSet.add(email);

						}

					}

				}

				else {

					emailCell = row.getCell(emailColumnIndex);

					localidadCell = row.getCell(localidadColumnIndex);

					if (emailCell != null && emailCell.getCellType() == CellType.STRING) {

						email = emailCell.getStringCellValue().trim();

						if (localidadCell != null && localidadCell.getCellType() == CellType.STRING) {

							localidad = localidadCell.getStringCellValue().trim();

						}

						else {

							localidad = "";

						}

						if (!email.isEmpty() && !email.equals("E-mail") && !lista.contains(email)
								&& !lista.contains(localidad)) {

							emailSet.add(email);

						}

					}

				}

			}

		}

		catch (IOException e) {

		}

		return emailSet;

	}

	private static int findEmailColumnIndex(XSSFSheet sheet, int headerRowIndex) {

		Row headerRow = sheet.getRow(headerRowIndex);

		if (headerRow != null) {

			for (int i = 0; i <= 10; i++) {

				Cell headerCell = headerRow.getCell(i);

				if (headerCell != null && headerCell.getCellType() == CellType.STRING) {

					if (headerCell.getStringCellValue().trim().equalsIgnoreCase("E-mail")) {

						return i;
					}

				}

			}

		}

		return -1;

	}

	public AgendaEmail() {

		identificador = -1;

		try {

			rutaDb = JMthos.rutaActual() + "db" + JMthos.saberSeparador() + "db.db";

		}

		catch (IOException e) {

		}

		textField_1 = new NTextField();

		textField = new NTextField();

		update = new JLabel("");

		combo = new ComboBoxSuggestion<String>();

		panel3 = new CopyTextArea(false, false, false, false);

		update.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				String dato1 = textField.getText().trim();

				String dato2 = textField_1.getText().trim();

				String dato3 = panel3.getText().trim();

				if (!dato1.isEmpty() && !dato2.isEmpty() && !dato3.isEmpty()) {

					ArrayList<String> lista = new ArrayList<>();

					lista.add("EMAILS");

					lista.add("ASUNTO");

					lista.add("MENSAJE");

					ArrayList<String> valores = new ArrayList<>();

					valores.add(dato1);

					valores.add(dato2);

					valores.add(dato3);

					JMthos.updateSqlite(rutaDb, "PERFILES", lista, valores, "ID=" + identificador);

					new PopupAlerts(600, 300).mensaje("Perfil actualizado correctamente");

				}

			}

		});

		combo.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {

				verDatosSeleccionados();

			}

		});

		combo.setFont(new Font("Dialog", Font.PLAIN, 30));

		ArrayList<String> datosLista = JMthos.selectSqlite(rutaDb, "SELECT NOMBRE FROM PERFILES");

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

						MessageDialog dialogo = new MessageDialog(0, 0, null, null, "",
								"¿Realmente desea borrar el perfil seleccionado?");

						if (dialogo.getMessageType().equals(MessageType.OK)) {

							valorSeleccionado = combo.getSelectedItem().toString();

							combo.removeItemAt(combo.getSelectedIndex());

							JMthos.deleteSQLite(rutaDb, "PERFILES", "NOMBRE", valorSeleccionado);

							if (numeroCombo == 1) {

								JMthos.deleteAllFromTableSqlite(rutaDb, "PERFILES");

							}

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

				verDatosSeleccionados();

			}

		});

		reload.setBackground(Color.WHITE);

		reload.setIcon(new Ojo());

		reload.setShadowColor(Color.WHITE);

		frame = this;

		lista = new ArrayList<>();

		textField_1.setHeaderText("Asunto");

		textField_1.setColumns(10);

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

		panel1.setBounds(0, 0, 639, 107);

		panel2 = new JPanel();

		panel2.setBackground(Color.WHITE);

		panel2.setBounds(32, 142, 607, 107);

		panel3.setBorder(null);

		panel3.setBounds(0, 313, 813, 198);

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
								title, label, "Aceptar", "Cancelar",
								new ImageIcon(AgendaEmail.class.getResource("/image/dato.png")), callback);

					}

				}

				catch (Exception e1) {

				}

			}

		});

		add.setShadowColor(Color.WHITE);

		add.setBounds(649, 20, 154, 44);

		addComponentListener(new ComponentAdapter() {

			@Override

			public void componentResized(ComponentEvent e) {

				int ancho = getWidth();

				int alto = getHeight();

				panel1.setBounds(0, 0, ancho, Math.round(alto * 0.2f));

				panelTest.setBounds(panelTest.getX(), 0, Math.round(ancho * 0.53f), Math.round(alto * 0.15f));

				panel2.setBounds(0, Math.round(alto * 0.2f), ancho, Math.round(alto * 0.2f));

				panel3.setBounds(0, Math.round(alto * 0.4f), Math.round(ancho * 0.98f), Math.round(alto * 0.3f));

				perfiles.setBounds(5, Math.round(alto * 0.72f), Math.round(ancho * 0.7f), Math.round(alto * 0.17f));

				add.setBounds(Math.round(ancho * 0.7f) + 5, Math.round(alto * 0.725f), Math.round(ancho * 0.06f),
						Math.round(alto * 0.17f) - 10);

				reload.setBounds(Math.round(ancho * 0.75f), Math.round(alto * 0.7f), Math.round(ancho * 0.1f),
						Math.round(alto * 0.2f));

				update.setBounds(Math.round(ancho * 0.84f), Math.round(alto * 0.755f), Math.round(ancho * 0.06f),
						Math.round(alto * 0.1f));

				delete.setBounds(Math.round(ancho * 0.92f), Math.round(alto * 0.725f), Math.round(ancho * 0.06f),
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

			drag.setBounds(155, 10, 199, 84);

			new UtilDragAndDrop(drag, drag.dragBorder, true, new UtilDragAndDrop.Listener() {

				@Override

				public void filesDropped(java.io.File[] archivos) {

					Set<String> lista = new HashSet<>();

					ArrayList<String> datos = null;

					if (combo.getItemCount() > 0) {

						datos = new ArrayList<>();

						datos = JMthos.selectSqlite(rutaDb,
								"SELECT R.VALOR FROM RESTRICCIONES R JOIN PR ON R.ID=PR.RESTRICCION JOIN PERFILES P ON P.ID=PR.PERFIL WHERE P.ID="
										+ identificador);

					}

					for (File f : archivos) {

						lista.addAll(readEmailsFromExcel(f.getAbsolutePath(), datos));

					}

					StringBuilder text = new StringBuilder();

					String coma = "";

					if (!textField.getText().isEmpty()) {

						coma = ",";

					}

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

			lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 20));

			lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);

			panel1.add(lblNewLabel_3);

			panel1.add(drag);

			panelTest.setBounds(370, 0, 184, 87);

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

			lblNewLabel_2.setBounds(10, 35, 75, 90);

			panel2.add(lblNewLabel_2);

			lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 20));

			lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);

			MaterialPanelLayout panelAsunto = new MaterialPanelLayout(lista, porcentajes, false);

			panelAsunto.setBounds(110, 10, 700, 97);

			panel2.add(panelAsunto);

			getContentPane().add(perfiles);

			getContentPane().add(add);

			delete.setBounds(649, 64, 154, 50);

			getContentPane().add(delete);

			reload.setBounds(649, 129, 154, 44);

			getContentPane().add(reload);

			update.setBackground(Color.WHITE);

			update.setIcon(new Reload());

			update.setBounds(649, 183, 154, 55);

			getContentPane().add(update);

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
								0, 0, true);

					}

					catch (Exception e1) {

					}

				}

			});

			JMenuItem mntmNewMenuItem_1 = new JMenuItem("Config");

			mntmNewMenuItem_1.setBackground(Color.WHITE);

			mntmNewMenuItem_1.addMouseListener(new MouseAdapter() {

				@Override

				public void mousePressed(MouseEvent e) {

					try {

						Config config = new Config(rutaDb);

						VentanaEmergente ventana = new VentanaEmergente(frame, config, "Configuración", 800, 450, false,
								1, new ImageIcon(AgendaEmail.class.getResource("/image/config.png")));

					}

					catch (Exception e1) {

					}

				}

			});

			mntmNewMenuItem_1.setFont(new Font("Segoe UI", Font.PLAIN, 20));

			menuBar.add(mntmNewMenuItem_1);

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

		setSize(new Dimension(827, 684));

		setLocationRelativeTo(null);

	}

	public void actionPerformed(ActionEvent arg0) {

	}

	public void stateChanged(ChangeEvent e) {

	}

}
