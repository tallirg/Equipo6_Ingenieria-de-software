/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pizzeria;

import control.NotificacionesJpaController;
import control.PagosJpaController;
import control.PedidosJpaController;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.Ingrediente;
import modelo.Inventario;
import modelo.Notificaciones;
import modelo.PedidoProducto;
import modelo.Pedidos;
import modelo.ProductoIngrediente;
import modelo.Usuario;


/**
 *
 * @author noobe
 */
public class Pagos extends javax.swing.JFrame {
String num1;
private Pedidos pedidoActual;
private Usuario usuarioActual;
private Icon imagen;
private boolean estado = false;
    /**
     * Creates new form Pagos
     */
    public Pagos(Pedidos pedido, Usuario usuario) {
        initComponents();
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Regresar al menú principal
                MenuPrincipal menu = new MenuPrincipal(usuarioActual, null, pedidoActual);
                menu.setVisible(true);
                dispose(); // Cierra sólo esta ventana
            }
        });
        
        this.usuarioActual = usuario;
        this.pedidoActual = pedido;
        
        lblPreciot.setText("Su total es de: " + pedidoActual.getTotal());
        
        JComponent[] campos = {nomTitular, NoTarjeta, mes, anio, cvv};

        for (int i = 0; i < campos.length; i++) {
            int indice = i; // necesario para el lambda
            campos[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (indice < campos.length - 1) {
                            campos[indice + 1].requestFocus(); // siguiente componente
                        }
                        else {
                            pagar();
                        }
                    }
                }
            });
        }
        
        JComponent[] campos2 = {correo,password};

        for (int i = 0; i < campos2.length; i++) {
            int indice = i; // necesario para el lambda
            campos2[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (indice < campos2.length - 1) {
                            campos2[indice + 1].requestFocus(); // siguiente componente
                        } else {
                            pagar();
                        }
                    }
                }
            });
        }
        
        JComponent[] campos3 = {nomTitularM,NumeroMesa,txtFldMonto};

        for (int i = 0; i < campos3.length; i++) {
            int indice = i; // necesario para el lambda
            campos3[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (indice < campos3.length - 1) {
                            campos3[indice + 1].requestFocus(); // siguiente componente
                        } 
                        else if (indice < campos3.length - 1) {
                            if (!txtFldMonto.getText().isEmpty()) {
                                try {
                                    BigDecimal montoCliente = new BigDecimal(txtFldMonto.getText());
                                    BigDecimal cambio = montoCliente.subtract(pedidoActual.getTotal());
                                    if(cambio.compareTo(BigDecimal.ZERO) < 0){JOptionPane.showMessageDialog(null, "Monto inválido");}
                                    else{lblCambio.setText("Cambio: " + cambio.toString());}
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null, "Monto inválido");
                                }
                            }
                        }
                        else {
                            pagar();
                        }
                    }
                }
            });
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pagos = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnPagar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        lblPreciot = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        nomTitular = new javax.swing.JTextField();
        NoTarjeta = new javax.swing.JTextField();
        cvv = new javax.swing.JTextField();
        txtTitular = new javax.swing.JLabel();
        txtNoTarjeta = new javax.swing.JLabel();
        txtFechaVencimiento = new javax.swing.JLabel();
        mes = new javax.swing.JComboBox<>();
        txtNoTarjeta2 = new javax.swing.JLabel();
        anio = new javax.swing.JComboBox<>();
        txtCvv = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtCorreo = new javax.swing.JLabel();
        correo = new javax.swing.JTextField();
        txtPassword = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        jPanel5 = new javax.swing.JPanel();
        txtTitularS = new javax.swing.JLabel();
        nomTitularM = new javax.swing.JTextField();
        txtNumMesa = new javax.swing.JLabel();
        NumeroMesa = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFldMonto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblCambio = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Impact", 3, 24)); // NOI18N
        jLabel1.setText("Pagar");

        jLabel2.setFont(new java.awt.Font("Gadugi", 1, 14)); // NOI18N
        jLabel2.setText("Seleccione su forma de pago: ");

        btnPagar.setBackground(new java.awt.Color(0, 0, 0));
        btnPagar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnPagar.setForeground(new java.awt.Color(255, 255, 255));
        btnPagar.setText("Pagar");
        btnPagar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        btnSalir.setText("Cancelar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        lblPreciot.setText("Su total es:");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        nomTitular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomTitularActionPerformed(evt);
            }
        });
        nomTitular.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nomTitularKeyTyped(evt);
            }
        });

        NoTarjeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoTarjetaActionPerformed(evt);
            }
        });
        NoTarjeta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                NoTarjetaKeyTyped(evt);
            }
        });

        cvv.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cvvKeyTyped(evt);
            }
        });

        txtTitular.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtTitular.setText("Titular");

        txtNoTarjeta.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtNoTarjeta.setText("No. Tarjeta");

        txtFechaVencimiento.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtFechaVencimiento.setText("Fecha de vencimiento");

        mes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));

        txtNoTarjeta2.setFont(new java.awt.Font("Gadugi", 0, 24)); // NOI18N
        txtNoTarjeta2.setText("/");

        anio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035" }));

        txtCvv.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtCvv.setText("CVV");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel4.setText("Ingrese sus datos: ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(mes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(txtNoTarjeta2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(anio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(nomTitular)
                        .addComponent(NoTarjeta)
                        .addComponent(txtTitular, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtNoTarjeta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFechaVencimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                        .addComponent(txtCvv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cvv, javax.swing.GroupLayout.Alignment.TRAILING)))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel4)
                .addGap(10, 10, 10)
                .addComponent(txtTitular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nomTitular, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNoTarjeta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NoTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFechaVencimiento)
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mes, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNoTarjeta2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(anio, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCvv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cvv, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Tarjeta", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        txtCorreo.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtCorreo.setText("Correo electronico");

        correo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                correoActionPerformed(evt);
            }
        });

        txtPassword.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtPassword.setText("Contraseña");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel5.setText("Ingrese sus datos: ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(txtPassword)
                    .addComponent(correo)
                    .addComponent(txtCorreo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(txtCorreo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(correo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(159, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("PayPal", new javax.swing.ImageIcon(getClass().getResource("/imagenes/Paypal_Icon.png")), jPanel3); // NOI18N

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        txtTitularS.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtTitularS.setText("Titular");

        nomTitularM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomTitularMActionPerformed(evt);
            }
        });
        nomTitularM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nomTitularMKeyTyped(evt);
            }
        });

        txtNumMesa.setFont(new java.awt.Font("Gadugi", 0, 14)); // NOI18N
        txtNumMesa.setText("Numero de mesa");

        NumeroMesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NumeroMesaActionPerformed(evt);
            }
        });
        NumeroMesa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                NumeroMesaKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setText("Ingrese sus datos: ");

        txtFldMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFldMontoKeyTyped(evt);
            }
        });

        jLabel3.setText("Monto recibido");

        lblCambio.setText("Cambio: ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addComponent(txtNumMesa)
                    .addComponent(nomTitularM)
                    .addComponent(txtTitularS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NumeroMesa, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                    .addComponent(txtFldMonto)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCambio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(txtTitularS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nomTitularM, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNumMesa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NumeroMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFldMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(lblCambio)
                .addGap(52, 52, 52))
        );

        jTabbedPane1.addTab("Sucursal", new javax.swing.ImageIcon(getClass().getResource("/imagenes/Paypal_Icon.png")), jPanel5); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 22, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(lblPreciot, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(121, 121, 121)
                        .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPreciot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPagar)
                    .addComponent(btnSalir))
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
        pagar();
    }//GEN-LAST:event_btnPagarActionPerformed

    private void pagar(){
        int index = jTabbedPane1.getSelectedIndex();
    String formaPago = "";

    switch (index) {
        case 0:
            formaPago = "Tarjeta";
            break;
        case 1:
            formaPago = "PayPal";
            break;
        case 2:
            formaPago = "Efectivo";
            break;
        default:
            JOptionPane.showMessageDialog(this, "Seleccione un método de pago");
            return;
    }

    
    String titularT = nomTitular.getText().trim();
    String numTarjeta = NoTarjeta.getText().trim();
    String fecha = mes.getSelectedItem().toString().trim() 
             + "/" 
             + anio.getSelectedItem().toString().trim();
    String CVV = cvv.getText().trim();
    
    String correoPaypal = correo.getText().trim();
    String contra = password.getText();
    
    String titularM = nomTitularM.getText();
    String numMesa = NumeroMesa.getText();

    // 🛡️ VALIDACIONES
    if (formaPago.equals("Tarjeta")) {
        if (titularT.isEmpty() || numTarjeta.isEmpty() || fecha.isEmpty() || CVV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellene todos los campos antes de continuar");
            return;
        }

        if (numTarjeta.length() < 13) {
            JOptionPane.showMessageDialog(this, "La tarjeta debe tener al menos 13 digitos.");
            return;
        }
        
        if (CVV.length() < 3) {
            JOptionPane.showMessageDialog(this, "El cvv debe tener al menos 3 digitos.");
            return;
        }
        
        if (!fecha.matches("(0[1-9]|1[0-2])/\\d{2,4}")) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener formato MM/AA o MM/AAAA");
            return;
        }
        

        // Validar que la tarjeta no esté vencida
        try {
            String[] partes = fecha.split("/");
            int mes = Integer.parseInt(partes[0]);
            int anio = Integer.parseInt(partes[1]);
            if (anio < 100) anio += 2000; // convertir AA a AAAA

            java.time.YearMonth fechaExp = java.time.YearMonth.of(anio, mes);
            java.time.YearMonth ahora = java.time.YearMonth.now();

            if (fechaExp.isBefore(ahora)) {
                JOptionPane.showMessageDialog(this, "La tarjeta está vencida");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido");
            return;
        }
    }

    if (formaPago.equalsIgnoreCase("PayPal")) {

        if (correoPaypal.isEmpty() || contra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellene todos los campos antes de continuar");
            return;
        }
        
        if (!correoPaypal.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        JOptionPane.showMessageDialog(this, "Por favor, ingrese un correo electrónico válido.");
        return;
        }

        // Validar correo electrónico básico
        /**if (!correoPaypal.trim().matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "Ingrese un correo electrónico válido");
            return;
        }*/
        
    }

    if (formaPago.equals("Efectivo")) {
        if (titularM.isEmpty() || numMesa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellene todos los campos antes de continuar");
            return;
        }
    }

    
    try {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
    PedidosJpaController ultimopedido = new PedidosJpaController(emf);
    NotificacionesJpaController noti = new NotificacionesJpaController(emf);
    PagosJpaController pagocontrol = new PagosJpaController(emf);

    Pedidos pedido = ultimopedido.findPedidos(pedidoActual.getIdPedido());

    if (pedido != null && "Pendiente de Pago".equals(pedido.getEstado())) {

        // 1. Cambiar estado del pedido
        pedido.setEstado("Pagado");
        ultimopedido.edit(pedido);

        // 2. Registrar notificación
        Notificaciones notifi = new Notificaciones();
        notifi.setIdUsuario(pedido.getIdUsuario());
        notifi.setMensaje("Tu pedido " + pedido.getIdPedido() + " ha sido pagado mediante " + formaPago + ".");
        noti.create(notifi);

        // 3. REGISTRAR EL PAGO EN LA TABLA PAGOS
                modelo.Pagos pago = new modelo.Pagos();
        pago.setIdPedido(pedido);  
        pago.setMetodoPago(formaPago);
        pago.setEstadoPago("Aprobado");
        pago.setFechaPago(new java.util.Date());
        pago.setTotalPagado(pedido.getTotal());

        pagocontrol.create(pago);
        
        // 5. DESCONTAR INGREDIENTES DE INVENTARIO
try {
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();

    // Obtener TODOS los productos del pedido
    Query q = em.createQuery(
        "SELECT pp FROM PedidoProducto pp WHERE pp.idPedido.idPedido = :idPedido"
    );
    q.setParameter("idPedido", pedidoActual.getIdPedido());

    List<PedidoProducto> listaPP = q.getResultList();

    // Por cada producto del pedido...
    for (PedidoProducto pp : listaPP) {

        int idProducto = pp.getIdProducto().getIdProducto();
        int cantidadPedida = pp.getCantidad(); // cuántas pizzas pidió

        // Obtener ingredientes necesarios para ese producto (receta)
        Query q2 = em.createQuery(
            "SELECT pi FROM ProductoIngrediente pi WHERE pi.idProducto.idProducto = :idProd"
        );
        q2.setParameter("idProd", idProducto);

        List<ProductoIngrediente> receta = q2.getResultList();

        // Descontar ingredientes
        for (ProductoIngrediente pi : receta) {

            Ingrediente ingr = pi.getIdIngrediente();
            /**double cantNecesaria = pi.getCantidadNecesaria();
            Cantidad total a rebajar
            double cantidadFinal = cantNecesaria * cantidadPedida;*/

            // Actualizar inventario
            double cantidadActual = pi.getCantidadNecesaria().doubleValue();
            double nuevaCantidad = ingr.getCantidad()-cantidadActual;
            if (nuevaCantidad < 0) nuevaCantidad = 0; // evitar números negativos

            ingr.setCantidad((int)nuevaCantidad);
            em.merge(ingr);
        }
            Query q3 = em.createQuery(
            "SELECT i FROM Inventario i WHERE i.idProducto.idProducto = :idProd"
        );
        q3.setParameter("idProd", idProducto);

        List<Inventario> inventarios = q3.getResultList();

        if (!inventarios.isEmpty()) {

            Inventario inv = inventarios.get(0);

            int cantidadActualProd = inv.getCantidad();
            int nuevaCantidadProd = cantidadActualProd - cantidadPedida;

            if (nuevaCantidadProd < 0) nuevaCantidadProd = 0;

            inv.setCantidad(nuevaCantidadProd);


            em.merge(inv);
        }
    }

    em.getTransaction().commit();
    em.close();

    System.out.println("Inventario actualizado correctamente.");

} catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error al actualizar inventario: " + ex.getMessage());
}

        
        // 4. Confirmación
        JOptionPane.showMessageDialog(this, "El pago se realizó correctamente");

        // Volver al menú
        MenuPrincipal salir = new MenuPrincipal(usuarioActual, imagen, null);
        salir.setVisible(true);
        this.dispose();

    } else {
        JOptionPane.showMessageDialog(this, "No se pudo procesar el pago");
    }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al procesar el pago: " + e.getMessage());
    }
    }
    
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
     MenuPrincipal salir = new MenuPrincipal(usuarioActual, imagen, pedidoActual);
     salir.setVisible(true);
     this.dispose();
        
// TODO add your handling code here:
    }//GEN-LAST:event_btnSalirActionPerformed

    private void NoTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoTarjetaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoTarjetaActionPerformed

    private void nomTitularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomTitularActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomTitularActionPerformed

    private void correoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_correoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_correoActionPerformed

    private void nomTitularMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomTitularMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomTitularMActionPerformed

    private void NumeroMesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NumeroMesaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NumeroMesaActionPerformed

    private void NoTarjetaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoTarjetaKeyTyped
        char c = evt.getKeyChar();

        // Evita que se escriban letras
        if (!Character.isDigit(c)) {
            evt.consume();
            return;
        }

        // Evita más de 10 dígitos
        if (NoTarjeta.getText().length() >= 16) {
            evt.consume();
        }
    }//GEN-LAST:event_NoTarjetaKeyTyped

    private void cvvKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cvvKeyTyped
        char c = evt.getKeyChar();

        // Evita que se escriban letras
        if (!Character.isDigit(c)) {
            evt.consume();
            return;
        }

        // Evita más de 10 dígitos
        if (cvv.getText().length() >= 4) {
            evt.consume();
        }
    }//GEN-LAST:event_cvvKeyTyped

    private void NumeroMesaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NumeroMesaKeyTyped
        char c = evt.getKeyChar();

        // Evita que se escriban letras
        if (!Character.isDigit(c)) {
            evt.consume();
            return;
        }

        // Evita más de 10 dígitos
        if (NumeroMesa.getText().length() >= 2) {
            evt.consume();
        }
    }//GEN-LAST:event_NumeroMesaKeyTyped

    private void nomTitularKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nomTitularKeyTyped
        if(!(Character.isLetter(evt.getKeyChar())) && !(evt.getKeyChar() == KeyEvent.VK_SPACE)){
            evt.consume();
        }  
        if (nomTitular.getText().length() > 100) {
            evt.consume();
        }
    }//GEN-LAST:event_nomTitularKeyTyped

    private void nomTitularMKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nomTitularMKeyTyped
        if(!(Character.isLetter(evt.getKeyChar())) && !(evt.getKeyChar() == KeyEvent.VK_SPACE)){
            evt.consume();
        }  
        if (nomTitularM.getText().length() > 100) {
            evt.consume();
        }
    }//GEN-LAST:event_nomTitularMKeyTyped

    private void txtFldMontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFldMontoKeyTyped
        char c = evt.getKeyChar();
        String texto = txtFldMonto.getText();

        // Permitir solo dígitos y un punto
        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
            return;
        }

        // Evitar que el primer caracter sea un punto
        if (texto.isEmpty() && c == '.') {
            evt.consume();
            return;
        }

        // Evitar que escriban más de un punto decimal
        if (c == '.' && texto.contains(".")) {
            evt.consume();
            return;
        }

        // (Opcional) Limitar a dos decimales:
        if (texto.contains(".")) {
            int indexPunto = texto.indexOf(".");
            int decimales = texto.length() - indexPunto - 1;

            if (decimales >= 2) {  // Cambia a 3, 4, etc. si deseas más decimales
                evt.consume();
                return;
            }
        }

        // (Opcional) Limitar longitud total
        if (texto.length() >= 10) { // cambia a lo que necesites
            evt.consume();
        }
    }//GEN-LAST:event_txtFldMontoKeyTyped

 
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField NoTarjeta;
    private javax.swing.JTextField NumeroMesa;
    private javax.swing.JComboBox<String> anio;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JTextField correo;
    private javax.swing.JTextField cvv;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCambio;
    private javax.swing.JLabel lblPreciot;
    private javax.swing.JComboBox<String> mes;
    private javax.swing.JTextField nomTitular;
    private javax.swing.JTextField nomTitularM;
    private javax.swing.ButtonGroup pagos;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel txtCorreo;
    private javax.swing.JLabel txtCvv;
    private javax.swing.JLabel txtFechaVencimiento;
    private javax.swing.JTextField txtFldMonto;
    private javax.swing.JLabel txtNoTarjeta;
    private javax.swing.JLabel txtNoTarjeta2;
    private javax.swing.JLabel txtNumMesa;
    private javax.swing.JLabel txtPassword;
    private javax.swing.JLabel txtTitular;
    private javax.swing.JLabel txtTitularS;
    // End of variables declaration//GEN-END:variables
}
