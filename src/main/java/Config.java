
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.buttons.animated.EffectButton;
import com.buttons.round.NButton;
import com.comboBox.comboSuggestion.ComboBoxSuggestion;
import com.dialog.confirm.MessageDialog;
import com.dialog.confirm.MessageDialog.MessageType;
import com.textField.text.NewTextField;

import mthos.JMthos;

@SuppressWarnings("all")

public class Config extends JPanel {

	private NewTextField textField;

	private ComboBoxSuggestion<String> perfil;

	private ComboBoxSuggestion<String> comboBox_1_1;

	private int indicePerfil;

	private ArrayList<String> restricciones;

	private ComboBoxSuggestion<String> comboBox_1_1_1;

	public Config(String ruta) {

		comboBox_1_1_1 = new ComboBoxSuggestion<String>();

		restricciones = new ArrayList<>();

		comboBox_1_1 = new ComboBoxSuggestion<>();

		perfil = new ComboBoxSuggestion<>();

		perfil.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {

				if (comboBox_1_1_1.getItemCount() == 0) {

					ArrayList<String> restriccionesAndId = JMthos.selectSqlite(ruta, "SELECT VALOR FROM RESTRICCIONES");

					for (int i = 0; i < restriccionesAndId.size(); i++) {

						comboBox_1_1_1.addItem(restriccionesAndId.get(i));

					}

				}

				indicePerfil = Integer.parseInt(JMthos
						.selectSqlite(ruta,
								"SELECT ID FROM PERFILES WHERE NOMBRE='" + perfil.getSelectedItem().toString() + "'")
						.get(0));

				ArrayList<String> restriccionesAndId = JMthos.selectSqlite(ruta,
						"SELECT R.ID,R.VALOR FROM RESTRICCIONES R JOIN PR ON R.ID=PR.RESTRICCION JOIN PERFILES P ON P.ID=PR.PERFIL WHERE P.ID="
								+ indicePerfil);

				comboBox_1_1.removeAllItems();

				for (int i = 0; i < restriccionesAndId.size(); i++) {

					if (i % 2 == 0) {

						restricciones.add(restriccionesAndId.get(i));

					}

					else {

						comboBox_1_1.addItem(restriccionesAndId.get(i));

					}

				}

			}

		});

		setBackground(Color.WHITE);

		setLayout(null);

		NButton btnNewButton = new NButton("-");

		btnNewButton.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				try {

					if (comboBox_1_1.getItemCount() > 0) {

						MessageDialog dialogo = new MessageDialog(0, 0, null, null, "",
								"¿Realmente desea borrar la restriccion seleccionada?");

						if (dialogo.getMessageType().equals(MessageType.OK)) {

							JMthos.runSqlite(ruta,
									"DELETE FROM PR WHERE PERFIL=" + indicePerfil + " AND RESTRICCION="
											+ "(SELECT ID FROM RESTRICCIONES WHERE VALOR=\""
											+ comboBox_1_1.getSelectedItem().toString() + "\")");

							comboBox_1_1.removeItemAt(comboBox_1_1.getSelectedIndex());

						}

					}

					else if (comboBox_1_1_1.getItemCount() == 0) {

						JMthos.runSqlite(ruta, "DELETE FROM PR");

						JMthos.runSqlite(ruta, "DELETE FROM RESTRICCIONES");

					}

				}

				catch (Exception e1) {

				}

			}

		});

		btnNewButton.setBackground(SystemColor.controlHighlight);

		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));

		btnNewButton.setBounds(145, 143, 102, 38);

		add(btnNewButton);

		NButton btnNewButton_1 = new NButton("+");

		btnNewButton_1.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {

				textField.setText(textField.getText().trim());

				if (!textField.getText().isEmpty() && perfil.getItemCount() > 0 &&

						estaProvinciaEnPerfil(ruta, perfil.getSelectedItem().toString(), textField.getText()) == 0) {

					ArrayList<String> lista = new ArrayList<>();

					lista.add("COLUMNA");

					lista.add("VALOR");

					ArrayList<String> valores = new ArrayList<>();

					boolean existeRestriccion = false;

					if (JMthos
							.selectSqlite(ruta,
									"SELECT COUNT(ID) FROM RESTRICCIONES WHERE VALOR=\"" + textField.getText() + "\"")
							.get(0).equals("0")) {

						existeRestriccion = true;

						valores.add("PROVINCIA");

						valores.add(textField.getText());

						JMthos.insertSQLite(ruta, "RESTRICCIONES", lista, valores);

					}

					lista.clear();

					lista.add("PERFIL");

					lista.add("RESTRICCION");

					valores.clear();

					valores.add(Integer.toString(indicePerfil));

					valores.add(JMthos.selectSqlite(ruta, "SELECT MAX(ID) FROM RESTRICCIONES").get(0));

					try {

						JMthos.insertSQLite(ruta, "PR", lista, valores);

					}

					catch (Exception e1) {

					}

					if (existeRestriccion) {

						comboBox_1_1_1.addItem(textField.getText());

					}

					comboBox_1_1.addItem(textField.getText());

				}

			}

		});

		btnNewButton_1.setBackground(SystemColor.controlHighlight);

		btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 20));

		btnNewButton_1.setBounds(7, 143, 128, 38);

		add(btnNewButton_1);

		perfil.setFont(new Font("Tahoma", Font.PLAIN, 20));

		perfil.setBounds(7, 204, 374, 58);

		JMthos.agregarAComboBox(perfil, JMthos.selectSqlite(ruta, "SELECT NOMBRE FROM PERFILES"));

		add(perfil);

		JLabel lblNewLabel_1 = new JLabel("Todos los emails menos la provincia");

		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);

		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));

		lblNewLabel_1.setBounds(6, 0, 769, 55);

		add(lblNewLabel_1);

		JPanel panel = new JPanel();

		panel.setBorder(new LineBorder(new Color(0, 0, 0)));

		panel.setBounds(6, 65, 769, 56);

		add(panel);

		panel.setLayout(new GridLayout(0, 1, 0, 0));

		textField = new NewTextField();

		textField.setLabel("PROVINCIA");

		textField.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(textField);

		textField.setColumns(10);

		comboBox_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));

		comboBox_1_1.setBounds(7, 293, 768, 56);

		add(comboBox_1_1);

		NButton btnAll = new NButton("- All");
		btnAll.setText("Borrar todo");

		btnAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					if (comboBox_1_1.getItemCount() > 0) {

						MessageDialog dialogo = new MessageDialog(0, 0, null, null, "",
								"¿Desea borrar todas las restricciones para este perfil?");

						if (dialogo.getMessageType().equals(MessageType.OK)) {

							JMthos.runSqlite(ruta, "DELETE FROM PR WHERE PERFIL=" + indicePerfil);

							comboBox_1_1.removeAllItems();

						}

					}

					else if (comboBox_1_1_1.getItemCount() == 0) {

						JMthos.runSqlite(ruta, "DELETE FROM PR");

						JMthos.runSqlite(ruta, "DELETE FROM RESTRICCIONES");

					}

				}

				catch (Exception e1) {

				}

			}

		});

		btnAll.setFont(new Font("Tahoma", Font.PLAIN, 20));

		btnAll.setBackground(SystemColor.controlHighlight);

		btnAll.setBounds(257, 143, 124, 38);

		add(btnAll);

		comboBox_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));

		comboBox_1_1_1.setBounds(401, 205, 374, 56);

		add(comboBox_1_1_1);

		NButton btnNewButton_2 = new NButton("-");

		btnNewButton_2.setText("Borrar restriccion");

		btnNewButton_2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					if (comboBox_1_1_1.getItemCount() > 0) {

						MessageDialog dialogo = new MessageDialog(0, 0, null, null, "",
								"¿Desea borrar la restriccion de la base de datos?");

						Object objetoBorrar = comboBox_1_1_1.getSelectedItem().toString();

						if (dialogo.getMessageType().equals(MessageType.OK)) {

							JMthos.runSqlite(ruta,
									"DELETE FROM PR WHERE RESTRICCION=(SELECT ID FROM RESTRICCIONES WHERE VALOR=\""
											+ comboBox_1_1_1.getSelectedItem().toString() + "\")");

							JMthos.runSqlite(ruta, "DELETE FROM RESTRICCIONES WHERE VALOR=\""
									+ comboBox_1_1_1.getSelectedItem().toString() + "\")");

							comboBox_1_1.removeItem(objetoBorrar);

							comboBox_1_1_1.removeItem(objetoBorrar);

						}

					}

					else if (comboBox_1_1.getItemCount() == 0) {

						JMthos.runSqlite(ruta, "DELETE FROM PR");

						JMthos.runSqlite(ruta, "DELETE FROM RESTRICCIONES");

					}

				}

				catch (Exception e1) {

				}

			}

		});

		btnNewButton_2.setFont(new Font("Tahoma", Font.PLAIN, 20));

		btnNewButton_2.setBackground(SystemColor.controlHighlight);

		btnNewButton_2.setBounds(567, 143, 208, 38);

		add(btnNewButton_2);

		EffectButton btnNewButton_3 = new EffectButton("-");

		btnNewButton_3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					textField.setText(comboBox_1_1_1.getSelectedItem().toString());

				}

				catch (Exception e1) {

				}

			}

		});

		btnNewButton_3.setIcon(new ImageIcon(Config.class.getResource("/image/up.png")));

		btnNewButton_3.setText("");

		btnNewButton_3.setShadowColor(Color.WHITE);

		btnNewButton_3.setFont(new Font("Tahoma", Font.PLAIN, 20));

		btnNewButton_3.setBackground(SystemColor.controlHighlight);

		btnNewButton_3.setBounds(412, 143, 128, 38);

		add(btnNewButton_3);

	}

	int estaProvinciaEnPerfil(String ruta, String perfil, String value) {

		return Integer.parseInt(JMthos.selectSqlite(ruta,
				"SELECT COUNT(P.ID) FROM PERFILES P JOIN PR PR ON PR.PERFIL=P.ID JOIN RESTRICCIONES R ON R.ID=PR.RESTRICCION WHERE P.ID="
						+ indicePerfil + " AND R.COLUMNA='PROVINCIA' AND R.VALOR='" + value + "'")
				.get(0)) == 0 ? 0 : 1;

	}
}
