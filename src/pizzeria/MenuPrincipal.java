/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pizzeria;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme;
import control.PagosJpaController;
import control.PedidoProductoJpaController;
import control.PedidosJpaController;
import control.ProductoJpaController;
import control.UsuarioJpaController;
import extras.Seguridad;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import modelo.Pedidos; 
import modelo.Producto;
import modelo.Usuario;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import modelo.Ingrediente;
import modelo.Inventario;
import modelo.PedidoProducto;
import modelo.Pagos;
import modelo.Rol;
import pizzeria.PrePedido;
/**
 *
 * @author noobe
 */
public class MenuPrincipal extends javax.swing.JFrame {
int BotonPulsado;
private Usuario usuarioActual;
private Pedidos pedidoActual;

    /**
     * Creates new form MenuPrincipal
     */
    public MenuPrincipal(Usuario usuario, Icon imagen, Pedidos pedido) {
        initComponents();
        jPanel2.removeAll();  // Limpia lo que el diseñador puso
    jPanel2.setLayout(new BorderLayout()); 
    jPanel2.add(jTabbedPanelPrincipal, BorderLayout.CENTER);

    jPanel2.revalidate();
    jPanel2.repaint();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                try {
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
                    PedidosJpaController controlador = new PedidosJpaController(emf);

                    // 🔥 Si el pedido sigue en estado Creado → Cancelarlo
                    if (pedidoActual != null && "Creado".equals(pedidoActual.getEstado())) {
                        pedidoActual.setEstado("Cancelado");
                        controlador.edit(pedidoActual); // ← Actualiza en BD
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Regresar al login
                Ingreso_U salir = new Ingreso_U();
                salir.setVisible(true);

                dispose(); // Cierra esta ventana
            }
        });

        
        System.out.println("initComponents ejecutado");
        this.usuarioActual = usuario;
        
        if (pedido != null) {
            System.out.println("Pedido recibido en el constructor: " + pedido.getIdPedido());
            this.pedidoActual = pedido;
        } else {
            // Si viene null, entonces sí buscamos en BD
            this.pedidoActual = obtenerPedidoActivo(usuario);
            System.out.println("Pedido cargado desde BD: " + 
                              (pedidoActual != null ? pedidoActual.getIdPedido() : "Ninguno"));
        }
        
        String nombreCompleto = usuario.getNombre();
        String primerNombre = nombreCompleto.split(" ")[0];

        lblBienvenida.setText("Bienvenido " + primerNombre + ", ¿Cuál será tu elección el día de hoy?");

        lblBienvenida.setHorizontalAlignment(lblBienvenida.CENTER);
        lblBienvenida.setVerticalAlignment(lblBienvenida.CENTER);
        
        lblPromos.setText("Bienvenido " + usuario.getNombre() + " Disfruta de nuestros promos");
        lblPromos.setHorizontalAlignment(lblPromos.CENTER);
        lblPromos.setVerticalAlignment(lblPromos.CENTER);
        
        btnPagar.setVisible(false);
        btnTerminar.setVisible(false);
        btnCancelarP.setVisible(false);
        addProducto.setVisible(false);
        addPromo.setVisible(false);
        btnPedido.setVisible(true);
        
        
    // Primero ocultamos TODO y luego activamos lo que corresponda
        
       RolTipo rolEnum = RolTipo.valueOf(usuarioActual.getIdRol().getNombre().toUpperCase());
       configurarAccesosPorRol(rolEnum);
        
        //temas();
        configurarInterfaz();
        this.setLocationRelativeTo(null);
        this.pack();
        
        tblProductos.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    if (tblProductos.isEditing()) {
                        tblProductos.getCellEditor().stopCellEditing();
                    }

                    btnActualizar2ActionPerformed(null); // actualiza BD

                    e.consume(); // evita salto de línea
                }
            }
        });
        tblIngredientes1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    if (tblIngredientes1.isEditing()) {
                        tblIngredientes1.getCellEditor().stopCellEditing();
                    }

                    btnActualizar1ActionPerformed(null); // actualiza BD

                    e.consume(); // evita salto de línea
                }
            }
        });
        JComponent[] campos = {txtNombre1, txtTelefono1, jcbRol, txtDireccion1, txtCorreo1, txtContraseña1, txtConfirmarContra1};

        for(int i = 0; i < campos.length; i++){
            int indice = i; // necesario para el lambda
            campos[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent evt) {
                    if(evt.getKeyCode() == KeyEvent.VK_ENTER){
                        if(indice < campos.length - 1){
                            campos[indice + 1].requestFocus(); // siguiente campo
                        } else {
                            btnRegistrar1ActionPerformed(null);
                        }
                    }
                }
            });
        }
    }

    /**private void temas(){
        try{
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch(Exception e){
            e.printStackTrace();
        }
    }**/
    
    private void configurarAccesosPorRol(RolTipo rol) {
    jTabbedPanelPrincipal.removeAll();

    switch (rol) {
        case ADMINISTRADOR:
            jTabbedPanelPrincipal.addTab("Productos", jPanelProductos);
            jTabbedPanelPrincipal.addTab("Promos", jPanelPromos);
            jTabbedPanelPrincipal.addTab("Almacén", jTabbedAlmacen);
            jTabbedPanelPrincipal.addTab("Pedidos", jPanelPedidos);
            jTabbedPanelPrincipal.addTab("Caja", jPanelCaja);
            jTabbedPanelPrincipal.addTab("Añadir Empleado", jPanelAddEmpleado);
            addProducto.setVisible(true);
            addPromo.setVisible(true);
            break;

        case CAJERO:
            jTabbedPanelPrincipal.addTab("Pedidos", jPanelPedidos);
            jTabbedPanelPrincipal.addTab("Caja", jPanelCaja);
            break;

        case COCINERO:
        case REPARTIDOR:
            jTabbedPanelPrincipal.addTab("Pedidos", jPanelPedidos);
            break;

        case ALMACEN:
            jTabbedPanelPrincipal.addTab("Almacén", jTabbedAlmacen);
            jTabbedPanelPrincipal.addTab("Productos", jPanelProductos);
            jTabbedPanelPrincipal.addTab("Promos", jPanelPromos);
            addProducto.setVisible(true);
            addPromo.setVisible(true);
            break;

        case CLIENTE:
            jTabbedPanelPrincipal.addTab("Productos", jPanelProductos);
            jTabbedPanelPrincipal.addTab("Promos", jPanelPromos);
            jTabbedPanelPrincipal.addTab("Pedidos", jPanelPedidos);
            break;
    }
    }

    private void quitarPestaña(JTabbedPane tabs, Component panel) {
    int index = tabs.indexOfComponent(panel);
    if (index != -1) {
        tabs.remove(index);
    }
}
    
    private Pedidos obtenerPedidoActivo(Usuario usuario) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
    PedidosJpaController control = new PedidosJpaController(emf);

    // Obtener solo los pedidos del usuario actual
    List<Pedidos> lista = control.findPedidosEntities()
                                 .stream()
                                 .filter(p -> p.getIdUsuario().getIdUsuario() == usuario.getIdUsuario())
                                 .collect(Collectors.toList());
    // Buscar si tiene uno en estado "Creado"
    for (Pedidos p : lista) {
        if ("Creado".equalsIgnoreCase(p.getEstado())) {
            return p; // este es su pedido activo
        }
    }

    return null; // si no hay "Creado", significa que puede iniciar uno nuevo
}


    private void configurarInterfaz() {

        if (pedidoActual == null) {
            // No hay pedido
            btnPedido.setEnabled(true);
            btnTerminar.setVisible(false);
            btnCancelarP.setVisible(false);
            btnPagar.setVisible(false);
            return;
        }

        String estado = pedidoActual.getEstado();

        switch (estado) {

            case "Creado":
                // Pedido recién creado, aún se pueden agregar productos
                btnPedido.setEnabled(false);
                btnTerminar.setVisible(true);
                btnCancelarP.setVisible(true);
                btnPagar.setVisible(false);
                break;

            case "Pendiente de Pago":
                // Pedido ya terminado, falta pagar
                btnPedido.setEnabled(false);
                btnTerminar.setEnabled(false);
                btnCancelarP.setVisible(true); // Si quieres permitir cancelación
                btnPagar.setVisible(true);
                break;

            default:
                // Otros estados (Pagado, Cancelado, etc.)
                btnPedido.setEnabled(true);
                btnTerminar.setVisible(false);
                btnCancelarP.setVisible(false);
                btnPagar.setVisible(false);
                break;
        }
    }

    private void enlistarProductos() {
    try {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        PedidoProductoJpaController control = new PedidoProductoJpaController(emf);

        // Obtener productos del pedido actual
        List<PedidoProducto> productosDelPedido = new ArrayList<>();
        for (PedidoProducto pp : control.findPedidoProductoEntities()) {
            if (pp.getIdPedido().equals(pedidoActual)) {
                productosDelPedido.add(pp);
            }
        }

        DefaultTableModel modelo = (DefaultTableModel) CcontenidoP.getModel();

        // Limpiar la tabla
        modelo.setRowCount(0);

        // Formato moneda
        NumberFormat mx = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

        // Llenar tabla
        for (PedidoProducto pp : productosDelPedido) {

            // Obtener valores
            String nombre = pp.getIdProducto().getNombre();
            int cantidad = pp.getCantidad();
            double precio = pp.getIdProducto().getPrecio().doubleValue(); // precio unitario
            double subtotal = pp.getSubtotal().doubleValue(); // precio*cantidad ya calculado

            modelo.addRow(new Object[]{
                nombre,                     // Producto
                cantidad,                   // Cant
                mx.format(precio),          // Precio unitario
                mx.format(subtotal)         // Subtotal
            });
        }

        // Alinear columnas numéricas a la derecha
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);

        CcontenidoP.getColumnModel().getColumn(1).setCellRenderer(derecha); // Cant
        CcontenidoP.getColumnModel().getColumn(2).setCellRenderer(derecha); // Precio
        CcontenidoP.getColumnModel().getColumn(3).setCellRenderer(derecha); // Subtotal

    } catch (Exception e) {
        e.printStackTrace();
    }
}


    
    private void enlistarPagos() {
    try {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        PagosJpaController control = new PagosJpaController(emf);

        // Obtener todos los pagos
        List<Pagos> todos = control.findPagosEntities();

        // Filtrar solo los pagos que pertenecen al pedido actual
        List<Pagos> pagos = new ArrayList<>();
        for (Pagos p : todos) {
            if (p.getIdPedido() != null && p.getIdPedido().equals(pedidoActual)) {
                pagos.add(p);
            }
        }


    } catch (Exception e) {
        e.printStackTrace();
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

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        historial = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPanelPrincipal = new javax.swing.JTabbedPane();
        jPanelProductos = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnHawa = new javax.swing.JButton();
        Mexicana = new javax.swing.JButton();
        btnPeperoni = new javax.swing.JButton();
        Queso4 = new javax.swing.JButton();
        veggie = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        pepsi = new javax.swing.JButton();
        SevenUp = new javax.swing.JButton();
        Lipton = new javax.swing.JButton();
        Epura = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        Cheesy = new javax.swing.JButton();
        Papotas = new javax.swing.JButton();
        Alitas = new javax.swing.JButton();
        Bonelees = new javax.swing.JButton();
        btnPedido = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnTerminar = new javax.swing.JButton();
        btnPagar = new javax.swing.JButton();
        btnCarrito = new javax.swing.JLabel();
        btnCancelarP = new javax.swing.JButton();
        addProducto = new javax.swing.JButton();
        jPanelPromos = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnCanela = new javax.swing.JButton();
        btnBra = new javax.swing.JButton();
        btnVol = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnCM = new javax.swing.JButton();
        btnCD = new javax.swing.JButton();
        btnCG = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        btnGO = new javax.swing.JButton();
        btnIndi = new javax.swing.JButton();
        lblPromos = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        addPromo = new javax.swing.JButton();
        jPanelPedidos = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btPagados = new javax.swing.JRadioButton();
        btPendientes = new javax.swing.JRadioButton();
        btCreados = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        historialpedidos = new javax.swing.JTable();
        btCancelados = new javax.swing.JRadioButton();
        jTabbedAlmacen = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        btnActualizar1 = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        disponibleIngd = new javax.swing.JRadioButton();
        agotadoIngd = new javax.swing.JRadioButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblIngredientes1 = new javax.swing.JTable();
        btnAddIngrediente1 = new javax.swing.JButton();
        todosIngd = new javax.swing.JRadioButton();
        jPanel10 = new javax.swing.JPanel();
        btnActualizar2 = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        btnAddIngrediente2 = new javax.swing.JButton();
        disponibleProdct = new javax.swing.JRadioButton();
        todosProdct = new javax.swing.JRadioButton();
        agotadoProdct = new javax.swing.JRadioButton();
        jPanelCaja = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        CcontenidoP = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lblPreciot = new javax.swing.JLabel();
        btnPagarCaja = new javax.swing.JButton();
        jPanelAddEmpleado = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        txtDireccion1 = new javax.swing.JTextField();
        txtCorreo1 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        btnRegistrar1 = new javax.swing.JButton();
        jLabel50 = new javax.swing.JLabel();
        txtContraseña1 = new javax.swing.JPasswordField();
        jLabel51 = new javax.swing.JLabel();
        txtConfirmarContra1 = new javax.swing.JPasswordField();
        jcbRol = new javax.swing.JComboBox<>();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        txtNombre1 = new javax.swing.JTextField();
        txtTelefono1 = new javax.swing.JTextField();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");
        jMenuItem2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jTabbedPanelPrincipal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPanelPrincipalStateChanged(evt);
            }
        });

        jPanelProductos.setBackground(new java.awt.Color(255, 255, 255));
        jPanelProductos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanelProductos.setMaximumSize(new java.awt.Dimension(40000, 40000));

        lblBienvenida.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 18)); // NOI18N
        lblBienvenida.setText("Bienvenido              ¿Cual sera tu eleccion el dia de hoy?");

        jLabel2.setFont(new java.awt.Font("Gadugi", 3, 14)); // NOI18N
        jLabel2.setText("Pizza'z clasicas");

        btnHawa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/hawaiana (1).png"))); // NOI18N
        btnHawa.setToolTipText("");
        btnHawa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHawaActionPerformed(evt);
            }
        });

        Mexicana.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Mexicana-1.png"))); // NOI18N
        Mexicana.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MexicanaActionPerformed(evt);
            }
        });

        btnPeperoni.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Pepperoni-Pizza.png"))); // NOI18N
        btnPeperoni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPeperoniActionPerformed(evt);
            }
        });

        Queso4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pizza-4-quesos-g-1.png"))); // NOI18N
        Queso4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Queso4ActionPerformed(evt);
            }
        });

        veggie.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pizza-mia-veggie-2-low.png"))); // NOI18N
        veggie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                veggieActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Gadugi", 3, 14)); // NOI18N
        jLabel3.setText("Bebidas");

        pepsi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/Pepsi-Logo-PNG-HD.png"))); // NOI18N
        pepsi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pepsiActionPerformed(evt);
            }
        });

        SevenUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/7up-PNG-Photo.png"))); // NOI18N
        SevenUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SevenUpActionPerformed(evt);
            }
        });

        Lipton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/lipton.png"))); // NOI18N
        Lipton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LiptonActionPerformed(evt);
            }
        });

        Epura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/epura2.png"))); // NOI18N
        Epura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EpuraActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Gadugi", 3, 14)); // NOI18N
        jLabel4.setText("Extras");

        Cheesy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/png-transparent-cheese-bread.png"))); // NOI18N
        Cheesy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheesyActionPerformed(evt);
            }
        });

        Papotas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/entradas_papotas.png"))); // NOI18N
        Papotas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PapotasActionPerformed(evt);
            }
        });

        Alitas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pngtree-chicken-wings-png-image_13353320.png"))); // NOI18N
        Alitas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlitasActionPerformed(evt);
            }
        });

        Bonelees.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/boneles.png"))); // NOI18N
        Bonelees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BoneleesActionPerformed(evt);
            }
        });

        btnPedido.setText("Iniciar Pedido");
        btnPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedidoActionPerformed(evt);
            }
        });

        jLabel1.setText("Hawaiana");

        jLabel9.setText("Mexicana");

        jLabel10.setText("Peperoni");

        jLabel11.setText("4 Quesos");

        jLabel12.setText("Veggie");

        jLabel13.setText("Pepsi 600ml");

        jLabel14.setText("7UP 600ml");

        jLabel15.setText("Lipton Limon 600ml");

        jLabel16.setText("Epura 600ml");

        jLabel17.setText("Cheesy Bread");

        jLabel18.setText("Papotas");

        jLabel19.setText("Alitas");

        jLabel20.setText("Bonelees");

        btnTerminar.setText("Terminar Pedido");
        btnTerminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTerminarActionPerformed(evt);
            }
        });

        btnPagar.setText("Pagar");
        btnPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarActionPerformed(evt);
            }
        });

        btnCarrito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/icono-de-la-tienda-web.png"))); // NOI18N
        btnCarrito.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCarritoMouseClicked(evt);
            }
        });

        btnCancelarP.setText("Cancelar Pedido");
        btnCancelarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarPActionPerformed(evt);
            }
        });

        addProducto.setText("Añadir Producto");
        addProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelProductosLayout = new javax.swing.GroupLayout(jPanelProductos);
        jPanelProductos.setLayout(jPanelProductosLayout);
        jPanelProductosLayout.setHorizontalGroup(
            jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProductosLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Alitas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(50, 50, 50)
                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Cheesy, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(250, 250, 250)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblBienvenida, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelProductosLayout.createSequentialGroup()
                            .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btnHawa, javax.swing.GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE)
                                            .addComponent(pepsi, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(50, 50, 50)
                                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btnPeperoni, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(Lipton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(50, 50, 50)
                            .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(Queso4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(SevenUp, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Bonelees, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(50, 50, 50)
                            .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Mexicana, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Epura, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(Papotas, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(50, 50, 50)
                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(veggie, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPedido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTerminar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelarP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addProducto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPagar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCarrito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(48, 48, 48))
        );
        jPanelProductosLayout.setVerticalGroup(
            jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProductosLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBienvenida))
                .addGap(9, 9, 9)
                .addComponent(jLabel2)
                .addGap(20, 20, 20)
                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                        .addComponent(Queso4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelProductosLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel12))
                            .addGroup(jPanelProductosLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnHawa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(veggie, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnPeperoni, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel10)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelProductosLayout.createSequentialGroup()
                        .addComponent(Mexicana, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addGap(1, 1, 1)))
                .addGap(40, 40, 40)
                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                        .addComponent(btnPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)
                        .addComponent(btnCancelarP, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelProductosLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(20, 20, 20)
                                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pepsi, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Epura, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanelProductosLayout.createSequentialGroup()
                                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SevenUp, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Lipton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(1, 1, 1)))))
                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel15))
                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelProductosLayout.createSequentialGroup()
                                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel16)
                                        .addComponent(jLabel14)))
                                .addGap(40, 40, 40)
                                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(24, 24, 24)
                                        .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Cheesy, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Bonelees, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Alitas, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Papotas, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanelProductosLayout.createSequentialGroup()
                                        .addComponent(btnTerminar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(55, 55, 55)
                                        .addComponent(btnPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(8, 8, 8)
                                .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelProductosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel19)
                                        .addComponent(jLabel17))
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel18))))))
                .addGap(73, 73, 73))
        );

        jTabbedPanelPrincipal.addTab("Menu", new javax.swing.ImageIcon(getClass().getResource("/imagenes/menu (1).png")), jPanelProductos); // NOI18N
        jPanelProductos.getAccessibleContext().setAccessibleName("");

        jPanelPromos.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Gadugi", 3, 14)); // NOI18N
        jLabel5.setText("Seleccionado por nosotros");

        btnCanela.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/canelabaitz.png"))); // NOI18N
        btnCanela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCanelaActionPerformed(evt);
            }
        });

        btnBra.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pngtree-chocolate-brownie-png-png-image_13321549.png"))); // NOI18N
        btnBra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBraActionPerformed(evt);
            }
        });

        btnVol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/volcan-de-chocolate.jpg"))); // NOI18N
        btnVol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Gadugi", 3, 14)); // NOI18N
        jLabel6.setText("Combos");

        btnCM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/combo mediano.png"))); // NOI18N
        btnCM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCMActionPerformed(evt);
            }
        });

        btnCD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dominator.png"))); // NOI18N
        btnCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCDActionPerformed(evt);
            }
        });

        btnCG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/grande.png"))); // NOI18N
        btnCG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCGActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Gadugi", 3, 14)); // NOI18N
        jLabel7.setText("Descuentos");

        btnGO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/2 grande.png"))); // NOI18N
        btnGO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGOActionPerformed(evt);
            }
        });

        btnIndi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/individual.png"))); // NOI18N
        btnIndi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIndiActionPerformed(evt);
            }
        });

        lblPromos.setFont(new java.awt.Font("Arial Rounded MT Bold", 1, 18)); // NOI18N
        lblPromos.setText("d");

        jLabel21.setText("Canela Baitz");

        jLabel22.setText("Brownie");

        jLabel23.setText("Volcan de Chocolate");

        jLabel24.setText("Combo Mediano");

        jLabel25.setText("Combo Dominator");

        jLabel26.setText("Combo Grande");

        jLabel27.setText("2 Pizzas Grandes Originales");

        jLabel28.setText("Combo Individual");

        addPromo.setText("Añadir Promoción");
        addPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPromoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPromosLayout = new javax.swing.GroupLayout(jPanelPromos);
        jPanelPromos.setLayout(jPanelPromosLayout);
        jPanelPromosLayout.setHorizontalGroup(
            jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPromosLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelPromosLayout.createSequentialGroup()
                        .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnGO, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCanela, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCM, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnIndi, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCD, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBra, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnVol, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCG, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPromosLayout.createSequentialGroup()
                .addContainerGap(332, Short.MAX_VALUE)
                .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPromosLayout.createSequentialGroup()
                        .addComponent(addPromo)
                        .addGap(150, 150, 150))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPromosLayout.createSequentialGroup()
                        .addComponent(lblPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(331, Short.MAX_VALUE))))
        );
        jPanelPromosLayout.setVerticalGroup(
            jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPromosLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblPromos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addGap(20, 20, 20)
                .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelPromosLayout.createSequentialGroup()
                        .addComponent(btnCanela, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21))
                    .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelPromosLayout.createSequentialGroup()
                            .addComponent(btnVol, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel23))
                        .addGroup(jPanelPromosLayout.createSequentialGroup()
                            .addComponent(btnBra, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel22))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(20, 20, 20)
                .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCG, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnCM, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGO, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnIndi, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPromosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addGap(62, 62, 62)
                .addComponent(addPromo, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        jTabbedPanelPrincipal.addTab("Promociones", new javax.swing.ImageIcon(getClass().getResource("/imagenes/cupon-de-descuento (1).png")), jPanelPromos); // NOI18N

        jPanelPedidos.setBackground(new java.awt.Color(255, 255, 255));
        jPanelPedidos.setToolTipText("Pedidos");

        jLabel8.setFont(new java.awt.Font("Impact", 3, 36)); // NOI18N
        jLabel8.setText("Historial de pedidos");

        historial.add(btPagados);
        btPagados.setText("Pagados");
        btPagados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btPagadosMouseClicked(evt);
            }
        });
        btPagados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPagadosActionPerformed(evt);
            }
        });

        historial.add(btPendientes);
        btPendientes.setText("Pendientes");
        btPendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btPendientesMouseClicked(evt);
            }
        });
        btPendientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPendientesActionPerformed(evt);
            }
        });

        historial.add(btCreados);
        btCreados.setText("Creados");
        btCreados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btCreadosMouseClicked(evt);
            }
        });
        btCreados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCreadosActionPerformed(evt);
            }
        });

        historialpedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id pedido", "Fecha", "Estado", "Total"
            }
        ));
        historialpedidos.setRowHeight(25);
        historialpedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                historialpedidosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(historialpedidos);

        historial.add(btCancelados);
        btCancelados.setText("Cancelados");
        btCancelados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btCanceladosMouseClicked(evt);
            }
        });
        btCancelados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCanceladosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPedidosLayout = new javax.swing.GroupLayout(jPanelPedidos);
        jPanelPedidos.setLayout(jPanelPedidosLayout);
        jPanelPedidosLayout.setHorizontalGroup(
            jPanelPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPedidosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(286, 286, 286))
            .addGroup(jPanelPedidosLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanelPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPedidosLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 794, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(324, Short.MAX_VALUE))
                    .addGroup(jPanelPedidosLayout.createSequentialGroup()
                        .addComponent(btPagados, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(109, 109, 109)
                        .addComponent(btPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btCreados, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(132, 132, 132)
                        .addComponent(btCancelados, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(110, 110, 110))))
        );
        jPanelPedidosLayout.setVerticalGroup(
            jPanelPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPedidosLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel8)
                .addGap(55, 55, 55)
                .addGroup(jPanelPedidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btCreados)
                    .addComponent(btPendientes)
                    .addComponent(btPagados)
                    .addComponent(btCancelados))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(265, Short.MAX_VALUE))
        );

        jTabbedPanelPrincipal.addTab("Pedidos", new javax.swing.ImageIcon("C:\\Users\\Talli\\Downloads\\clipboard_4130098 (1).png"), jPanelPedidos); // NOI18N

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setToolTipText("Pedidos");

        btnActualizar1.setText("Actualizar");
        btnActualizar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizar1ActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Impact", 3, 36)); // NOI18N
        jLabel34.setText("Ingredientes");

        historial.add(disponibleIngd);
        disponibleIngd.setText("Disponible");
        disponibleIngd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                disponibleIngdMouseClicked(evt);
            }
        });
        disponibleIngd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disponibleIngdActionPerformed(evt);
            }
        });

        historial.add(agotadoIngd);
        agotadoIngd.setText("Agotado");
        agotadoIngd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                agotadoIngdMouseClicked(evt);
            }
        });
        agotadoIngd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agotadoIngdActionPerformed(evt);
            }
        });

        tblIngredientes1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Producto", "Estado", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblIngredientes1.setRowHeight(25);
        jScrollPane5.setViewportView(tblIngredientes1);

        btnAddIngrediente1.setText("Añadir Ingrediente");
        btnAddIngrediente1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddIngrediente1ActionPerformed(evt);
            }
        });

        historial.add(todosIngd);
        todosIngd.setText("Todos");
        todosIngd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                todosIngdMouseClicked(evt);
            }
        });
        todosIngd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todosIngdActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 864, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(btnActualizar1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(604, 604, 604)
                                .addComponent(btnAddIngrediente1))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(disponibleIngd, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(188, 188, 188)
                                .addComponent(agotadoIngd, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(188, 188, 188)
                                .addComponent(todosIngd, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(367, 367, 367)
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(249, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34)
                .addGap(41, 41, 41)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(disponibleIngd)
                    .addComponent(agotadoIngd)
                    .addComponent(todosIngd))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddIngrediente1)
                    .addComponent(btnActualizar1))
                .addGap(24, 24, 24))
        );

        jTabbedAlmacen.addTab("Ingredientes", jPanel9);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setToolTipText("Pedidos");

        btnActualizar2.setText("Actualizar");
        btnActualizar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizar2ActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Impact", 3, 36)); // NOI18N
        jLabel35.setText("Productos");

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Producto", "Estado", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setRowHeight(25);
        jScrollPane6.setViewportView(tblProductos);

        btnAddIngrediente2.setText("Añadir Ingrediente");
        btnAddIngrediente2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddIngrediente2ActionPerformed(evt);
            }
        });

        historial.add(disponibleProdct);
        disponibleProdct.setText("Disponible");
        disponibleProdct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                disponibleProdctMouseClicked(evt);
            }
        });
        disponibleProdct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disponibleProdctActionPerformed(evt);
            }
        });

        historial.add(todosProdct);
        todosProdct.setText("Todos");
        todosProdct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                todosProdctMouseClicked(evt);
            }
        });
        todosProdct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todosProdctActionPerformed(evt);
            }
        });

        historial.add(agotadoProdct);
        agotadoProdct.setText("Agotado");
        agotadoProdct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                agotadoProdctMouseClicked(evt);
            }
        });
        agotadoProdct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agotadoProdctActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(347, 347, 347))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 864, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(btnActualizar2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(604, 604, 604)
                                .addComponent(btnAddIngrediente2))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(disponibleProdct, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(197, 197, 197)
                        .addComponent(agotadoProdct, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(197, 197, 197)
                        .addComponent(todosProdct, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(249, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addGap(36, 36, 36)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(disponibleProdct)
                    .addComponent(agotadoProdct)
                    .addComponent(todosProdct))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 185, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddIngrediente2)
                    .addComponent(btnActualizar2))
                .addGap(24, 24, 24))
        );

        jTabbedAlmacen.addTab("Productos", jPanel10);

        jTabbedPanelPrincipal.addTab("Almacen", new javax.swing.ImageIcon(getClass().getResource("/imagenes/almacen.png")), jTabbedAlmacen); // NOI18N

        jPanelCaja.setBackground(new java.awt.Color(255, 255, 255));

        CcontenidoP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Producto", "Cantidad", "Precio", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(CcontenidoP);

        jLabel31.setFont(new java.awt.Font("Impact", 3, 36)); // NOI18N
        jLabel31.setText("Detalles del pedido");

        jButton1.setText("Cancelar");

        lblPreciot.setText("Su total es:");

        btnPagarCaja.setText("Pagar");
        btnPagarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagarCajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCajaLayout = new javax.swing.GroupLayout(jPanelCaja);
        jPanelCaja.setLayout(jPanelCajaLayout);
        jPanelCajaLayout.setHorizontalGroup(
            jPanelCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCajaLayout.createSequentialGroup()
                .addGroup(jPanelCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCajaLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanelCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 888, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelCajaLayout.createSequentialGroup()
                                .addGroup(jPanelCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(btnPagarCaja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblPreciot, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                                .addGap(532, 532, 532)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanelCajaLayout.createSequentialGroup()
                        .addGap(291, 291, 291)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(261, Short.MAX_VALUE))
        );
        jPanelCajaLayout.setVerticalGroup(
            jPanelCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCajaLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel31)
                .addGap(42, 42, 42)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPreciot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 235, Short.MAX_VALUE)
                .addGroup(jPanelCajaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPagarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66))
        );

        jTabbedPanelPrincipal.addTab("Caja", new javax.swing.ImageIcon("C:\\Users\\Talli\\Downloads\\caja-registradora-removebg-preview (1).png"), jPanelCaja); // NOI18N

        jPanelAddEmpleado.setBackground(new java.awt.Color(255, 255, 255));

        jLabel43.setFont(new java.awt.Font("Impact", 2, 24)); // NOI18N
        jLabel43.setText("Crea la cuenta del empleado");

        txtCorreo1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCorreo1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtCorreo1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtCorreo1MouseExited(evt);
            }
        });

        jLabel45.setText("Nombre");

        jLabel46.setText("Telefono");

        jLabel47.setText("Direccion");

        jLabel48.setText("Correo");

        jLabel49.setText("Contraseña");

        btnRegistrar1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRegistrar1.setText("Registrarse");
        btnRegistrar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRegistrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrar1ActionPerformed(evt);
            }
        });

        jLabel50.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pitzas.jpg"))); // NOI18N
        jLabel50.setText("jLabel8");

        txtContraseña1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtContraseña1MouseClicked(evt);
            }
        });
        txtContraseña1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContraseña1ActionPerformed(evt);
            }
        });
        txtContraseña1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtContraseña1KeyPressed(evt);
            }
        });

        jLabel51.setText("Confirma contraseña");

        txtConfirmarContra1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtConfirmarContra1MouseClicked(evt);
            }
        });
        txtConfirmarContra1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtConfirmarContra1KeyPressed(evt);
            }
        });

        jcbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un rol", "Administrador", "Cajero", "Cocinero", "Repartidor", "Almacen" }));

        jLabel52.setText("Rol");

        jLabel53.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/pitzas.jpg"))); // NOI18N
        jLabel53.setText("jLabel8");

        txtNombre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombre1ActionPerformed(evt);
            }
        });
        txtNombre1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombre1KeyTyped(evt);
            }
        });

        txtTelefono1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefono1KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanelAddEmpleadoLayout = new javax.swing.GroupLayout(jPanelAddEmpleado);
        jPanelAddEmpleado.setLayout(jPanelAddEmpleadoLayout);
        jPanelAddEmpleadoLayout.setHorizontalGroup(
            jPanelAddEmpleadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAddEmpleadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                .addGroup(jPanelAddEmpleadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelAddEmpleadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRegistrar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtConfirmarContra1)
                        .addComponent(txtContraseña1)
                        .addComponent(txtCorreo1)
                        .addComponent(txtDireccion1)
                        .addComponent(jcbRol, 0, 300, Short.MAX_VALUE)
                        .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtNombre1)
                        .addComponent(txtTelefono1))
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelAddEmpleadoLayout.setVerticalGroup(
            jPanelAddEmpleadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAddEmpleadoLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel43)
                .addGap(44, 44, 44)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTelefono1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbRol, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDireccion1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCorreo1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtContraseña1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtConfirmarContra1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 195, Short.MAX_VALUE)
                .addComponent(btnRegistrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelAddEmpleadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanelAddEmpleadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPanelPrincipal.addTab("Añadir Empleado", jPanelAddEmpleado);

        jPanel2.add(jTabbedPanelPrincipal, java.awt.BorderLayout.CENTER);
        jTabbedPanelPrincipal.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btPendientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPendientesActionPerformed
        listarPedidosPorEstado("Pendiente de Pago");
    }//GEN-LAST:event_btPendientesActionPerformed

    private void btnPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedidoActionPerformed
    try {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        PedidosJpaController pedidosJpaController = new PedidosJpaController(emf);
//        btnTerminar.setVisible(true);
        // Paso 1: Verificar si ya hay un pedido "Creado" para el usuario actual
        List<Pedidos> listaPedidos = pedidosJpaController.findPedidosEntities();
        
                long activos = listaPedidos.stream()
                .filter(p -> p.getIdUsuario().equals(usuarioActual) &&
                             "Creado".equalsIgnoreCase(p.getEstado()))
                .count();

        if (activos > 1) {
            JOptionPane.showMessageDialog(this,
                "Este usuario tiene más de un pedido activo.\nFavor de revisar.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        boolean tienePedidoEnProceso = false;

        for (Pedidos p : listaPedidos) {
            if (p.getIdUsuario().equals(usuarioActual) && "Creado".equalsIgnoreCase(p.getEstado())) {
                tienePedidoEnProceso = true;
                break;
            }
        }

        if (tienePedidoEnProceso) {
            JOptionPane.showMessageDialog(this, "Ya tienes un pedido en proceso. Debes terminarlo antes de crear uno nuevo.");
            return;
        }

        // Paso 2: Crear nuevo pedido
        Pedidos nuevoPedido = new Pedidos();
        nuevoPedido.setIdUsuario(usuarioActual);
        nuevoPedido.setFecha(new Date());
        nuevoPedido.setEstado("Creado");
        nuevoPedido.setTotal(BigDecimal.ZERO);

        pedidosJpaController.create(nuevoPedido);
        
        JOptionPane.showMessageDialog(this, "Se creó el pedido correctamente");
        pedidoActual = nuevoPedido;
        btnTerminar.setVisible(true);
        btnCancelarP.setVisible(true);
        
    } catch(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al crear el pedido.");
    }
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPedidoActionPerformed

    private void BoneleesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BoneleesActionPerformed
        // TODO add your handling code here:
         int idProducto = 119;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = Bonelees.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_BoneleesActionPerformed

    private void PapotasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PapotasActionPerformed
        // TODO add your handling code here:
         int idProducto = 50;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = Papotas.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_PapotasActionPerformed

    private void CheesyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheesyActionPerformed
        // TODO add your handling code here:
         int idProducto = 49;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = Cheesy.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_CheesyActionPerformed

    private void pepsiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pepsiActionPerformed
        // TODO add your handling code here:
         int idProducto = 74;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = pepsi.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_pepsiActionPerformed

    private void Queso4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Queso4ActionPerformed
     int idProducto = 32;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = Queso4.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_Queso4ActionPerformed

    private void btnPeperoniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPeperoniActionPerformed
     int idProducto = 31;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnPeperoni.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
 
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPeperoniActionPerformed

    private void btnHawaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHawaActionPerformed
        int idProducto = 29;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnHawa.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHawaActionPerformed

    private void AlitasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlitasActionPerformed
        // TODO add your handling code here:
         int idProducto = 51;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = Alitas.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_AlitasActionPerformed

    private void MexicanaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MexicanaActionPerformed
     int idProducto = 30;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = Mexicana.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_MexicanaActionPerformed

    private void veggieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_veggieActionPerformed
     int idProducto = 33;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = veggie.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_veggieActionPerformed

    private void SevenUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SevenUpActionPerformed
        // TODO add your handling code here:
         int idProducto = 75;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = SevenUp.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_SevenUpActionPerformed

    private void LiptonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LiptonActionPerformed
        // TODO add your handling code here:
         int idProducto = 76;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = Lipton.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_LiptonActionPerformed

    private void EpuraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EpuraActionPerformed
        // TODO add your handling code here:
         int idProducto = 77;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = new ImageIcon(getClass().getResource("/imagenes/epura.png"));

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_EpuraActionPerformed

    private void btnCanelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCanelaActionPerformed
     int idProducto = 64;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnCanela.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCanelaActionPerformed

    private void btnBraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBraActionPerformed
        // TODO add your handling code here:
         int idProducto = 65;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnBra.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_btnBraActionPerformed

    private void btnVolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolActionPerformed
        // TODO add your handling code here:
         int idProducto = 66;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnVol.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
        
    }//GEN-LAST:event_btnVolActionPerformed

    private void btnCMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCMActionPerformed
        // TODO add your handling code here:
         int idProducto = 90;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnCM.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_btnCMActionPerformed

    private void btnCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCDActionPerformed
        // TODO add your handling code here:
         int idProducto = 91;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnCD.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_btnCDActionPerformed

    private void btnCGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCGActionPerformed
        // TODO add your handling code here:
         int idProducto = 92;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnCG.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_btnCGActionPerformed

    private void btnGOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGOActionPerformed
        // TODO add your handling code here:
         int idProducto = 93;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnGO.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_btnGOActionPerformed

    private void btnIndiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIndiActionPerformed
        // TODO add your handling code here:
         int idProducto = 89;

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        ProductoJpaController productoController = new ProductoJpaController(emf);
        Producto producto = productoController.findProducto(idProducto);

        Icon icone = btnIndi.getIcon();

        if (producto != null) {
            PrePedido next = new PrePedido(producto, icone, usuarioActual, pedidoActual);
            next.setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "no se pudo :v");
        }
    }//GEN-LAST:event_btnIndiActionPerformed

    private void btnTerminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerminarActionPerformed
    try {
        
        if (pedidoActual.getTotal() == null |pedidoActual.getTotal().compareTo(BigDecimal.ZERO)==0){
        JOptionPane.showMessageDialog(this, "Su pedido esta vacio. Por favor agregue productos antes de terminar.");
        return;
    }
        
        pedidoActual.setEstado("Pendiente de Pago");
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        PedidosJpaController pedido = new PedidosJpaController(emf);
        pedidoActual = pedido.findPedidos(pedidoActual.getIdPedido());
        //PedidosJpaController pedido = new PedidosJpaController(Persistence.createEntityManagerFactory("PizzeriaPU"));
        //pedido.edit(pedidoActual);
        if (pedidoActual == null){
        JOptionPane.showMessageDialog(this, "El pedido fue eliminado o no existe");
        return;
        }
       
        pedidoActual.setEstado("Pendiente de Pago");
        pedido.edit(pedidoActual);
        
        JOptionPane.showMessageDialog(this, "Armaste tu pedido, preparate para pagar");
        btnPagar.setVisible(true);
        btnPedido.setEnabled(false);
        btnTerminar.setEnabled(false);
    } catch (Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "No se pudoooooo");
    }

        // TODO add your handling code here:
    }//GEN-LAST:event_btnTerminarActionPerformed

    private void btnPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarActionPerformed
    pizzeria.Pagos ultimo = new pizzeria.Pagos(pedidoActual, usuarioActual);
    ultimo.setVisible(true);
    this.dispose();
    
    //temas();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPagarActionPerformed

    private void btPagadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btPagadosMouseClicked
        
    }//GEN-LAST:event_btPagadosMouseClicked

    private void btPendientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btPendientesMouseClicked
        
    }//GEN-LAST:event_btPendientesMouseClicked

    private void btCreadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btCreadosMouseClicked
        
    }//GEN-LAST:event_btCreadosMouseClicked

    private void btnCarritoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCarritoMouseClicked
        Carrito carritos = new Carrito(usuarioActual, pedidoActual);
        carritos.setVisible(true);
        this.dispose();
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCarritoMouseClicked

    private void btnCancelarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarPActionPerformed
        EntityManagerFactory emf = null;
EntityManager em = null;

try {
    emf = Persistence.createEntityManagerFactory("PizzeriaPU");
    em = emf.createEntityManager();

    // Buscar el pedido activo del usuario actual
    TypedQuery<Pedidos> query = em.createQuery(
        "SELECT p FROM Pedidos p WHERE p.idUsuario.idUsuario = :idUsuario " +
        "AND (p.estado = 'Creado' OR p.estado = 'Pendiente de Pago')", Pedidos.class);
    query.setParameter("idUsuario", usuarioActual.getIdUsuario());

    List<Pedidos> resultados = query.getResultList();

    if (resultados.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No tienes pedidos activos que cancelar");
        return;
    }

    Pedidos existente = resultados.get(0);

    // Aseguramos que el pedido esté en el contexto actual de persistencia
    
    /**if (pedidoDB == null) {
        JOptionPane.showMessageDialog(this, "El pedido ya no existe en la base de datos");
        em.getTransaction().rollback();
        return;
    }*/

    em.getTransaction().begin();

Pedidos pedidoDB = em.find(Pedidos.class, existente.getIdPedido());
pedidoDB.setEstado("Cancelado");

em.flush();
em.getTransaction().commit();

System.out.println("Commit realizado exitosamente, estado pedido actualizado a Cancelado");

// Actualiza la variable local si deseas
pedidoActual = pedidoDB;

JOptionPane.showMessageDialog(this, "El pedido fue cancelado exitosamente");

    // Actualizar interfaz
    pedidoActual = null; 
    btnTerminar.setVisible(false);
    btnCancelarP.setVisible(false);
    btnPagar.setVisible(false);
    btnTerminar.setEnabled(true);
    btnPedido.setEnabled(true);

} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error al cancelar pedido: " + e.getMessage());
    if (em != null && em.getTransaction().isActive()) {
        em.getTransaction().rollback();
    }
} finally {
    try {
        if (em != null && em.isOpen()) em.close();
    } catch (Exception ignore) {}

    try {
        if (emf != null && emf.isOpen()) emf.close();
    } catch (Exception ignore) {}
}

    }//GEN-LAST:event_btnCancelarPActionPerformed

    private void btCanceladosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btCanceladosMouseClicked
    
    }//GEN-LAST:event_btCanceladosMouseClicked

    private void addProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductoActionPerformed
        Productos abrir = new Productos(usuarioActual, pedidoActual);
        abrir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addProductoActionPerformed

    private void btPagadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPagadosActionPerformed
        listarPedidosPorEstado("Pagado");
    }//GEN-LAST:event_btPagadosActionPerformed

    public void abrirVentanaConPanel(JPanel panel, String titulo) {
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        this.dispose();
    }

    
    private void btCreadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCreadosActionPerformed
         listarPedidosPorEstado("Creado");
    }//GEN-LAST:event_btCreadosActionPerformed

    private void btCanceladosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCanceladosActionPerformed
        listarPedidosPorEstado("Cancelado");
    }//GEN-LAST:event_btCanceladosActionPerformed

    private void listarPedidosPorEstado(String estado) {
    DefaultTableModel modelo = (DefaultTableModel) historialpedidos.getModel();

    String rol = usuarioActual.getIdRol().getNombre();
    boolean esPendientePago = estado.equalsIgnoreCase("Pendiente de pago");

    // --- Remover todas las columnas dinámicas si existen ---
    removerColumna(modelo, "Cliente");
    removerColumna(modelo, "Seleccionar");
    removerColumna(modelo, "Pagar");

    // --- Agregar columnas SOLO si estamos en "Pendiente de pago" ---
    if (esPendientePago) {

        // ADMIN & CAJERO & COCINERO & REPARTIDOR -> ven CLIENTE
        if (rol.equalsIgnoreCase("Administrador")
         || rol.equalsIgnoreCase("Cajero")
         || rol.equalsIgnoreCase("Cocinero")
         || rol.equalsIgnoreCase("Repartidor")) {

            modelo.addColumn("Cliente");
        }

        // ADMIN & CAJERO -> ven SELECCIONAR
        if (rol.equalsIgnoreCase("Administrador") || rol.equalsIgnoreCase("Cajero")) {
            modelo.addColumn("Seleccionar");
        }

        // CLIENTE -> solo él ve PAGAR
        if (rol.equalsIgnoreCase("Cliente")) {
            modelo.addColumn("Pagar");
        }
    }

    modelo.setRowCount(0);

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
    EntityManager em = emf.createEntityManager();
    SimpleDateFormat formato = new SimpleDateFormat("dd-MMM-yyyy", new java.util.Locale("es","ES"));

    try {
        List<Pedidos> lista;

        // El cliente solo ve sus propios pedidos
        if (rol.equalsIgnoreCase("Cliente")) {
            lista = em.createQuery(
                "SELECT p FROM Pedidos p WHERE p.estado = :estado AND p.idUsuario.idUsuario = :id",
                Pedidos.class
            )
            .setParameter("estado", estado)
            .setParameter("id", usuarioActual.getIdUsuario())
            .getResultList();

        } else {
            // Todos los demás roles ven todos los pedidos
            lista = em.createQuery(
                "SELECT p FROM Pedidos p WHERE p.estado = :estado",
                Pedidos.class
            )
            .setParameter("estado", estado)
            .getResultList();
        }

        for (Pedidos ped : lista) {

            Object[] fila = {
                ped.getIdPedido(),
                formato.format(ped.getFecha()),
                ped.getEstado(),
                ped.getTotal()
            };

            // Solo en Pendiente de pago agregamos dinámicos
            if (esPendientePago) {

                // Mostrar CLIENTE para admin/cajero/cocinero/repartidor
                if (rol.equalsIgnoreCase("Administrador")
                 || rol.equalsIgnoreCase("Cajero")
                 || rol.equalsIgnoreCase("Cocinero")
                 || rol.equalsIgnoreCase("Repartidor")) {

                    fila = agregarA(fila, ped.getIdUsuario().getNombre());
                }

                // ADMIN y CAJERO -> SELECCIONAR
                if (rol.equalsIgnoreCase("Administrador") || rol.equalsIgnoreCase("Cajero")) {
                    fila = agregarA(fila, "Seleccionar");
                }

                // CLIENTE -> PAGAR
                if (rol.equalsIgnoreCase("Cliente")) {
                    fila = agregarA(fila, "Pagar");
                }
            }

            modelo.addRow(fila);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al cargar pedidos: " + e.getMessage());
    } finally {
        em.close();
        emf.close();
    }
}

// --- FUNCIONES AUXILIARES ---
private void removerColumna(DefaultTableModel modelo, String nombre) {
    try {
        int col = modelo.findColumn(nombre);
        if (col != -1) modelo.setColumnCount(modelo.getColumnCount() - 1);
    } catch (Exception ignored) {}
}

private Object[] agregarA(Object[] arr, Object valor) {
    Object[] nuevo = new Object[arr.length + 1];
    System.arraycopy(arr, 0, nuevo, 0, arr.length);
    nuevo[arr.length] = valor;
    return nuevo;
}

  
    private void addPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPromoActionPerformed
        Promociones abrir = new Promociones(usuarioActual, pedidoActual);
        abrir.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_addPromoActionPerformed

    private void jTabbedPanelPrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPanelPrincipalStateChanged
        int index = jTabbedPanelPrincipal.getSelectedIndex();
        if (index == 4) {  // Cambia el número al índice correcto
            enlistarProductos();
        }
    }//GEN-LAST:event_jTabbedPanelPrincipalStateChanged

    private void historialpedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historialpedidosMouseClicked
        int fila = historialpedidos.getSelectedRow();
        int col = historialpedidos.getSelectedColumn();

        if (fila < 0 || col < 0) return;

        String nombreColumna = historialpedidos.getColumnName(col);

        if (nombreColumna.equalsIgnoreCase("Pagar")) {
            int idPedido = Integer.parseInt(
                historialpedidos.getValueAt(fila, 0).toString()
            );
            Pedidos pedido = obtenerPedidoPorId(idPedido); // método que haremos
            pizzeria.Pagos pagar = new pizzeria.Pagos(pedido, usuarioActual);
            pagar.setVisible(true);
            this.dispose();
        }
        if (nombreColumna.equalsIgnoreCase("Seleccionar")) {
            int idPedido = Integer.parseInt(
                historialpedidos.getValueAt(fila, 0).toString()
            );
            pedidoActual = obtenerPedidoPorId(idPedido);
            JOptionPane.showMessageDialog(this, "Seleccionaste el pedido: " + pedidoActual.getIdPedido());
            enlistarProductos();
            lblPreciot.setText("Su total es de: " + pedidoActual.getTotal());
        }
    }//GEN-LAST:event_historialpedidosMouseClicked

    private Pedidos obtenerPedidoPorId(int idPedido) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        EntityManager em = emf.createEntityManager();
        Pedidos pedido = null;

        try {
            pedido = em.find(Pedidos.class, idPedido);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error obteniendo pedido: " + e.getMessage());
        } finally {
            em.close();
            emf.close();
        }

        return pedido;
    }

    private void btnActualizar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizar1ActionPerformed
        if (tblIngredientes1.isEditing()) {
            tblIngredientes1.getCellEditor().stopCellEditing();
        }
        int fila = tblIngredientes1.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un ingrediente para actualizar");
            return;
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
        EntityManager em = emf.createEntityManager();

        try {
            // 1️⃣ Obtener valores de la tabla
            String codigo = tblIngredientes1.getValueAt(fila, 0).toString();
            String nombre = tblIngredientes1.getValueAt(fila, 1).toString();

            // Cantidad está en la columna 3 como me mostraste
            String valorCantidad = tblIngredientes1.getValueAt(fila, 3).toString().trim();


            int cantidad;

            if (valorCantidad.isEmpty() || !valorCantidad.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.");
                return;
            }

            cantidad = Integer.parseInt(valorCantidad);


            em.getTransaction().begin();

            // 2️⃣ UPDATE manual → SOLO nombre y cantidad
            Query query = em.createQuery(
                "UPDATE Ingrediente i SET i.nombre = :nombre, i.cantidad = :cantidad " +
                "WHERE i.codigo = :codigo"
            );

            query.setParameter("nombre", nombre);
            query.setParameter("cantidad", cantidad);
            query.setParameter("codigo", codigo);

            int actualizados = query.executeUpdate(); // Ejecuta el UPDATE

            em.getTransaction().commit();

            if (actualizados > 0) {
                JOptionPane.showMessageDialog(this, "Ingrediente actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el ingrediente a actualizar.");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }//GEN-LAST:event_btnActualizar1ActionPerformed

    private void disponibleIngdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disponibleIngdMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_disponibleIngdMouseClicked

    private void disponibleIngdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disponibleIngdActionPerformed
        if (disponibleIngd.isSelected()) {
            cargarDatos(tblIngredientes1, "ingrediente", "Disponible");
        }
    }//GEN-LAST:event_disponibleIngdActionPerformed

    private void agotadoIngdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_agotadoIngdMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_agotadoIngdMouseClicked

    private void agotadoIngdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agotadoIngdActionPerformed
        if (agotadoIngd.isSelected()) {
            cargarDatos(tblIngredientes1, "ingrediente", "Agotado");
        }
    }//GEN-LAST:event_agotadoIngdActionPerformed

    private void btnAddIngrediente1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddIngrediente1ActionPerformed
        Ingredientes ventana = new Ingredientes(usuarioActual, pedidoActual);
        ventana.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAddIngrediente1ActionPerformed

    private void todosIngdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_todosIngdMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_todosIngdMouseClicked

    private void todosIngdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todosIngdActionPerformed
        if (todosIngd.isSelected()) {
            cargarDatos(tblIngredientes1, "ingrediente", "Todos");
        }
    }//GEN-LAST:event_todosIngdActionPerformed

    private void btnActualizar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizar2ActionPerformed
        if (tblProductos.isEditing()) {
            tblProductos.getCellEditor().stopCellEditing();
        }
        
        int fila = tblProductos.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un producto para actualizar");
        return;
    }

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
    EntityManager em = emf.createEntityManager();

    try {
        // 1️⃣ Obtener valores de la tabla
        String codigo = tblProductos.getValueAt(fila, 0).toString();
        String nombre = tblProductos.getValueAt(fila, 1).toString();
        String estado = tblProductos.getValueAt(fila, 2).toString();
        String valorCantidad = tblProductos.getValueAt(fila, 3).toString().trim();

            int cantidad;

            if (valorCantidad.isEmpty() || !valorCantidad.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.");
                return;
            }

            cantidad = Integer.parseInt(valorCantidad);
        // 2️⃣ Buscar producto por código
   
        
         List<Producto> lista = em.createQuery(
        "SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
        .setParameter("codigo", codigo)
        .getResultList();

if (lista.isEmpty()) {
    JOptionPane.showMessageDialog(this, "No existe el producto con ese código.");
    return;
}

Producto producto = lista.get(0);


        // 3️⃣ Buscar inventario asociado
        Inventario inventario = em.createQuery(
            "SELECT i FROM Inventario i WHERE i.idProducto.idProducto = :id",
            Inventario.class)
            .setParameter("id", producto.getIdProducto())
            .getSingleResult();

        em.getTransaction().begin();

        // 4️⃣ Actualizar Producto
        producto.setNombre(nombre);

        // 5️⃣ Actualizar Inventario
        inventario.setCantidad(cantidad);

        em.merge(producto);
        em.merge(inventario);

        em.getTransaction().commit();

        JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");

    } catch (Exception e) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage());
        e.printStackTrace();
    } finally {
        em.close();
        emf.close();
    }
    }//GEN-LAST:event_btnActualizar2ActionPerformed

    private void btnAddIngrediente2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddIngrediente2ActionPerformed
        Productos ventana = new Productos(usuarioActual, pedidoActual);
        ventana.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnAddIngrediente2ActionPerformed

    private void disponibleProdctMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disponibleProdctMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_disponibleProdctMouseClicked

    private void disponibleProdctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disponibleProdctActionPerformed
    if (disponibleProdct.isSelected()) {
        cargarDatos(tblProductos, "producto", "Disponible");
    }
    }//GEN-LAST:event_disponibleProdctActionPerformed

    private void todosProdctMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_todosProdctMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_todosProdctMouseClicked

    private void todosProdctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todosProdctActionPerformed
        if (todosProdct.isSelected()) {
            cargarDatos(tblProductos, "producto", "Todos");
    }
    }//GEN-LAST:event_todosProdctActionPerformed

    private void agotadoProdctMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_agotadoProdctMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_agotadoProdctMouseClicked

    

    
    private void agotadoProdctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agotadoProdctActionPerformed
        if (agotadoProdct.isSelected()) {
            cargarDatos(tblProductos, "producto", "Agotado");
    }
    }//GEN-LAST:event_agotadoProdctActionPerformed

    private void txtCorreo1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCorreo1MouseClicked
        txtCorreo1.setText("");
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreo1MouseClicked

    private void txtCorreo1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCorreo1MouseEntered

        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreo1MouseEntered

    private void txtCorreo1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCorreo1MouseExited

        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreo1MouseExited

    private void btnRegistrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrar1ActionPerformed
            // Obtener datos del formulario
    String nombre = txtNombre1.getText().trim();
    String telefono = txtTelefono1.getText().trim();
    String direccion = txtDireccion1.getText().trim();
    String correo = txtCorreo1.getText().trim();
    String password = txtContraseña1.getText().trim();
    String confirPassword = txtConfirmarContra1.getText().trim();

    // Obtener rol seleccionado del combo
    String rolSeleccionado = jcbRol.getSelectedItem().toString();

    // 🔹 Validar que no haya campos vacíos
    if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || correo.isEmpty() 
            || password.isEmpty() || confirPassword.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, rellene todos los campos antes de continuar.");
        return;
    }

    // 🔹 Validar selección del rol
    if (rolSeleccionado.equals("Seleccione un rol")) {
        JOptionPane.showMessageDialog(this, "Debe seleccionar un rol.");
        return;
    }

    // 🔹 Mapear rol → ID
    int idRol = obtenerIdRol(rolSeleccionado);

    // 🔹 Validar que la conversión fue correcta
    if (idRol == -1) {
        JOptionPane.showMessageDialog(this, "Rol inválido.");
        return;
    }

    // 🔹 Validar longitud mínima del nombre
    if (nombre.length() < 3) {
        JOptionPane.showMessageDialog(this, "El nombre debe tener al menos 3 caracteres.");
        return;
    }

    // 🔹 Validar longitud mínima de la dirección
    if (direccion.length() < 5) {
        JOptionPane.showMessageDialog(this, "La dirección debe tener al menos 5 caracteres.");
        return;
    }

    // 🔹 Validar formato del correo electrónico
    if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
        JOptionPane.showMessageDialog(this, "Por favor, ingrese un correo electrónico válido.");
        return;
    }

    // 🔹 Validar contraseña (mínimo 8 caracteres, al menos una letra y un número)
    if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
        JOptionPane.showMessageDialog(this,
                "La contraseña debe tener al menos 8 caracteres, incluir una letra y un número.");
        return;
    }

    // 🔹 Validar confirmación de contraseña
    if (!password.equals(confirPassword)) {
        JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
        return;
    }

    // ------------------------------------------
    //  VERIFICAR SI YA EXISTE UN CORREO
    // ------------------------------------------
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
    EntityManager em = emf.createEntityManager();
    Usuario sihay = null;

    try {
        sihay = em.createQuery("SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class)
                .setParameter("correo", correo)
                .getSingleResult();
    } catch (NoResultException ex) {
        // Si no encuentra, es válido continuar
    } finally {
        em.close();
    }

    if (sihay != null) {
        JOptionPane.showMessageDialog(this, "Este correo ya está registrado. Intente con otro.");
        return;
    }

    // ------------------------------------------
    // GUARDAR USUARIO
    // ------------------------------------------
    String passwordEncriptada = Seguridad.encriptarPassword(password);

    Usuario nuevoUsuario = new Usuario();
    nuevoUsuario.setNombre(nombre);
    nuevoUsuario.setTelefono(telefono);
    nuevoUsuario.setDireccion(direccion);
    nuevoUsuario.setCorreo(correo);
    nuevoUsuario.setContraseña(passwordEncriptada);

    // 👇 AQUI SE INSERTA EL ROL CORRESPONDIENTE
    nuevoUsuario.setIdRol(new Rol(idRol));

    UsuarioJpaController usuariosController = new UsuarioJpaController(emf);

    try {
        usuariosController.create(nuevoUsuario);
        JOptionPane.showMessageDialog(null, "Usuario registrado exitosamente.");

        Ingreso_U inicio = new Ingreso_U();
        inicio.setVisible(true);
        this.dispose();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "No se pudo registrar el usuario. Intente nuevamente.");
    }
    }//GEN-LAST:event_btnRegistrar1ActionPerformed

    private int obtenerIdRol(String rol) {
        switch (rol) {
            case "Administrador": return 1;
            case "Cajero":        return 2;
            case "Cocinero":      return 3;
            case "Repartidor":    return 4;
            case "Almacen":       return 5;
            default:              return -1;
        }
    }

    
    private void txtContraseña1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtContraseña1MouseClicked
        txtContraseña1.setText("");

        // TODO add your handling code here:
    }//GEN-LAST:event_txtContraseña1MouseClicked

    private void txtContraseña1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContraseña1KeyPressed
        txtContraseña1.setEchoChar('*');
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContraseña1KeyPressed

    private void txtConfirmarContra1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtConfirmarContra1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtConfirmarContra1MouseClicked

    private void txtConfirmarContra1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtConfirmarContra1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtConfirmarContra1KeyPressed

    private void txtContraseña1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContraseña1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContraseña1ActionPerformed

    private void txtNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombre1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombre1ActionPerformed

    private void txtNombre1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombre1KeyTyped
        if(!(Character.isLetter(evt.getKeyChar())) && !(evt.getKeyChar() == KeyEvent.VK_SPACE)){
                evt.consume();
            }  
        if (txtNombre1.getText().length() > 100) {
            evt.consume();
        }
    }//GEN-LAST:event_txtNombre1KeyTyped

    private void txtTelefono1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefono1KeyTyped
        char c = evt.getKeyChar();

        // Evita que se escriban letras
        if (!Character.isDigit(c)) {
            evt.consume();
            return;
        }

        // Evita más de 10 dígitos
        if (txtTelefono1.getText().length() >= 10) {
            evt.consume();
        }
    }//GEN-LAST:event_txtTelefono1KeyTyped

    private void btnPagarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagarCajaActionPerformed
        pizzeria.Pagos pagar = new pizzeria.Pagos(pedidoActual, usuarioActual);
        pagar.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPagarCajaActionPerformed

    private void cargarDatos(JTable tabla, String tipo, String filtro) {

    aplicarRenderizadoStock(tabla);
    aplicarEditorCantidad(tabla);

    DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
    modelo.setRowCount(0);

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("PizzeriaPU");
    EntityManager em = emf.createEntityManager();

    boolean hayBajoStock = false;

    try {
        List<?> lista;

        // ------- INGREDIENTES -------
        if (tipo.equals("ingrediente")) {

            if (filtro.equals("Todos")) {
                lista = em.createQuery("SELECT i FROM Ingrediente i", Ingrediente.class)
                          .getResultList();
            } else {
                lista = em.createQuery("SELECT i FROM Ingrediente i WHERE i.estado = :estado", Ingrediente.class)
                          .setParameter("estado", filtro)
                          .getResultList();
            }

            for (Object o : lista) {
                Ingrediente ing = (Ingrediente) o;

                if (ing.getCantidad() <= 10 && ing.getCantidad() > 0)
                    hayBajoStock = true;

                modelo.addRow(new Object[]{
                        ing.getCodigo(),
                        ing.getNombre(),
                        ing.getEstado(),
                        ing.getCantidad()
                });
            }
        }

        // ------- PRODUCTOS -------
else if (tipo.equals("producto")) {

    String baseSQL =
        "SELECT p.codigo, p.nombre, i.estado, i.cantidad " +
        "FROM Inventario i " +
        "JOIN i.idProducto p ";

    if (!filtro.equals("Todos")) {
        baseSQL += "WHERE i.estado = :estado";
    }

    // Reemplaza 'var' por TypedQuery<Object[]>
    javax.persistence.TypedQuery<Object[]> query = em.createQuery(baseSQL, Object[].class);

    if (!filtro.equals("Todos")) {
        query.setParameter("estado", filtro);
    }

    List<Object[]> listaProd = query.getResultList();

    for (Object[] filaSQL : listaProd) {

        String codigo = filaSQL[0] == null ? "" : filaSQL[0].toString();
        String nombre = filaSQL[1] == null ? "" : filaSQL[1].toString();
        String estado = filaSQL[2] == null ? "" : filaSQL[2].toString();

        int cantidad = 0;
        Object val = filaSQL[3];

        if (val != null) {
            String txt = val.toString().trim();
            if (txt.matches("\\d+")) {
                cantidad = Integer.parseInt(txt);
            }
        }

        if (cantidad <= 10 && cantidad > 0)
            hayBajoStock = true;

        modelo.addRow(new Object[]{codigo, nombre, estado, cantidad});
    }

}

        // ---- ALERTA ----
        if (hayBajoStock) {
            JOptionPane.showMessageDialog(
                    this,
                    tipo.equals("ingrediente") ?
                        "Algunos ingredientes están por agotarse.\nFavor de rellenar el almacén."
                        :
                        "Algunos productos están por agotarse.\nFavor de reabastecer el inventario.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
    } finally {
        em.close();
        emf.close();
    }
}

    
    private void aplicarRenderizadoStock(JTable table) {
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // 🔒 Validación de seguridad para evitar errores
            if (row < 0 || row >= table.getRowCount() ||
                table.getColumnCount() <= 3) {

                // Retornar tal cual si la tabla aún no está lista
                return c;
            }

            int cantidad = 0;

            try {
                Object obj = table.getValueAt(row, 3);
                if (obj != null) {
                    cantidad = Integer.parseInt(obj.toString());
                }
            } catch (Exception ex) {
                cantidad = 0;
            }

            // 🔥 Aplicar colores solo si la tabla está estable
            if (cantidad <= 10) {
                c.setBackground(Color.RED);
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    });
}

        private void aplicarEditorCantidad(JTable tabla) {
    tabla.getColumnModel().getColumn(3).setCellEditor(crearEditorEnteros());
}

    private TableCellEditor crearEditorEnteros() {
    JTextField txt = new JTextField();

    // Filtro para aceptar solo dígitos
    ((AbstractDocument) txt.getDocument()).setDocumentFilter(new DocumentFilter() {
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text != null && text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    });

    return new DefaultCellEditor(txt);
}


    
    
    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Alitas;
    private javax.swing.JButton Bonelees;
    private javax.swing.JTable CcontenidoP;
    private javax.swing.JButton Cheesy;
    private javax.swing.JButton Epura;
    private javax.swing.JButton Lipton;
    private javax.swing.JButton Mexicana;
    private javax.swing.JButton Papotas;
    private javax.swing.JButton Queso4;
    private javax.swing.JButton SevenUp;
    private javax.swing.JButton addProducto;
    private javax.swing.JButton addPromo;
    private javax.swing.JRadioButton agotadoIngd;
    private javax.swing.JRadioButton agotadoProdct;
    private javax.swing.JRadioButton btCancelados;
    private javax.swing.JRadioButton btCreados;
    private javax.swing.JRadioButton btPagados;
    private javax.swing.JRadioButton btPendientes;
    private javax.swing.JButton btnActualizar1;
    private javax.swing.JButton btnActualizar2;
    private javax.swing.JButton btnAddIngrediente1;
    private javax.swing.JButton btnAddIngrediente2;
    private javax.swing.JButton btnBra;
    private javax.swing.JButton btnCD;
    private javax.swing.JButton btnCG;
    private javax.swing.JButton btnCM;
    private javax.swing.JButton btnCancelarP;
    private javax.swing.JButton btnCanela;
    private javax.swing.JLabel btnCarrito;
    private javax.swing.JButton btnGO;
    private javax.swing.JButton btnHawa;
    private javax.swing.JButton btnIndi;
    private javax.swing.JButton btnPagar;
    private javax.swing.JButton btnPagarCaja;
    private javax.swing.JButton btnPedido;
    private javax.swing.JButton btnPeperoni;
    private javax.swing.JButton btnRegistrar1;
    private javax.swing.JButton btnTerminar;
    private javax.swing.JButton btnVol;
    private javax.swing.JRadioButton disponibleIngd;
    private javax.swing.JRadioButton disponibleProdct;
    private javax.swing.ButtonGroup historial;
    private javax.swing.JTable historialpedidos;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelAddEmpleado;
    private javax.swing.JPanel jPanelCaja;
    private javax.swing.JPanel jPanelPedidos;
    private javax.swing.JPanel jPanelProductos;
    private javax.swing.JPanel jPanelPromos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedAlmacen;
    private javax.swing.JTabbedPane jTabbedPanelPrincipal;
    private javax.swing.JComboBox<String> jcbRol;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblPreciot;
    private javax.swing.JLabel lblPromos;
    private javax.swing.JButton pepsi;
    private javax.swing.JTable tblIngredientes1;
    private javax.swing.JTable tblProductos;
    private javax.swing.JRadioButton todosIngd;
    private javax.swing.JRadioButton todosProdct;
    private javax.swing.JPasswordField txtConfirmarContra1;
    private javax.swing.JPasswordField txtContraseña1;
    private javax.swing.JTextField txtCorreo1;
    private javax.swing.JTextField txtDireccion1;
    private javax.swing.JTextField txtNombre1;
    private javax.swing.JTextField txtTelefono1;
    private javax.swing.JButton veggie;
    // End of variables declaration//GEN-END:variables

    

}
