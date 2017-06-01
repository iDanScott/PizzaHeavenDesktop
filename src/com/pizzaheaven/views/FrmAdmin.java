/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pizzaheaven.views;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import java.util.Timer;
import pizzaheaven.controllers.DrinksController;
import pizzaheaven.controllers.OrderController;
import pizzaheaven.controllers.OrderStatusController;
import pizzaheaven.models.Session;
import pizzaheaven.controllers.PizzaController;
import pizzaheaven.controllers.SideController;
import pizzaheaven.controllers.StaffController;
import pizzaheaven.controllers.ToppingsController;
import pizzaheaven.helpers.StringHelpers;
import pizzaheaven.models.Drink;
import pizzaheaven.models.Order;
import pizzaheaven.models.OrderStatus;
import pizzaheaven.models.Pizza;
import pizzaheaven.models.Side;
import pizzaheaven.models.Staff;
import pizzaheaven.models.Topping;
import pizzaheaven.security.Encryptor;

/**
 *
 * @author dan
 */
public class FrmAdmin extends javax.swing.JFrame {
    private boolean isRobotPressing = false;
    Timer timer;

    private void updateTime() {
        while(true) {
            try {
                Date now = new Date();
                this.lblTime.setText("<html>Today is: " 
                + now.getDate() + "/" + (now.getMonth() + 1) + "/" + (now.getYear() + 1900)
                + " <br />Current Time: " + (now.getHours()) + ":" + now.getMinutes()+"</html>");
                double moneyTaken = 0d;
                for (Order order : ((OrderController)Session.get().getController("OrderController")).get()) {
                    if (order.getOrderID().equals("259")) {
                        Date orderDate = StringHelpers.orderToDate(order.getOrderDateTime());
                        if (orderDate.getDate() == new Date().getDate() && orderDate.getMonth() == new Date().getMonth() 
                            && orderDate.getYear() == new Date().getYear()) {
                            moneyTaken += Double.parseDouble(order.getOrderCost());
                            lblTakings.setText("Today's takings: £" + moneyTaken);
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("[ERR] " + e.getMessage());
            }
        }
    }
    
    /**
     * Creates new form FrmAdmin
     */
    public FrmAdmin() {
        initComponents();
        Thread timerThread = new Thread(this::updateTime);
        timerThread.start();
        lblCurrentUser.setText("Welcome Back: " + Session.get().getStaff().getFirstName());
        Image currentImage = ((ImageIcon)lblIcon.getIcon()).getImage();
        Image image = currentImage.getScaledInstance((int)((double)lblIcon.getHeight() / 0.34189723320158102766798418972332d), lblIcon.getHeight(), Image.SCALE_SMOOTH);
        lblIcon.setIcon(new ImageIcon(image));
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        cboFilterOrderStatus.setModel(
                new JComboBox(((OrderStatusController)Session.get().getController("OrderStatusController")).get()).getModel());
        cboFilterOrderStatus.addItem("");
        updatePizzaTable();
        JPopupMenu popup = new JPopupMenu();
        JMenuItem deleteMenuItem = new JMenuItem("Delete Pizza...");
        deleteMenuItem.addActionListener((ActionEvent e) -> {
            btnDeletePizza.doClick();
        });
        JMenuItem editMenuItem = new JMenuItem("Edit Pizza...");
        editMenuItem.addActionListener((ActionEvent e) -> { 
            btnEditPizza.doClick();
        });
        deleteMenuItem.setMnemonic(KeyEvent.VK_P);
        editMenuItem.setMnemonic(KeyEvent.VK_P);
        popup.add(editMenuItem);
        popup.add(deleteMenuItem);
        tblPizzas.setComponentPopupMenu(popup);
        
        updateDrinksTable();
        popup = new JPopupMenu();
        deleteMenuItem = new JMenuItem("Delete Drink...");
        deleteMenuItem.addActionListener((ActionEvent e) -> {
            btnDeleteDrink.doClick();
        });
        editMenuItem = new JMenuItem("Edit Drink...");
        editMenuItem.addActionListener((ActionEvent e) -> { 
            btnEditDrink.doClick();
        });
        deleteMenuItem.setMnemonic(KeyEvent.VK_P);
        editMenuItem.setMnemonic(KeyEvent.VK_P);
        popup.add(editMenuItem);
        popup.add(deleteMenuItem);
        tblDrinks.setComponentPopupMenu(popup);
        
        updateSidesTable();
        popup = new JPopupMenu();
        deleteMenuItem = new JMenuItem("Delete Side...");
        deleteMenuItem.addActionListener((ActionEvent e) -> {
            btnDeleteSide.doClick();
        });
        editMenuItem = new JMenuItem("Edit Side...");
        editMenuItem.addActionListener((ActionEvent e) -> { 
            btnEditSide.doClick();
        });
        deleteMenuItem.setMnemonic(KeyEvent.VK_P);
        editMenuItem.setMnemonic(KeyEvent.VK_P);
        popup.add(editMenuItem);
        popup.add(deleteMenuItem);
        tblSides.setComponentPopupMenu(popup);
        
        updateStaffTable();
        
        updateOrdersTable();
        
        updateToppingsTable();
        popup = new JPopupMenu();
        deleteMenuItem = new JMenuItem("Delete Topping...");
        deleteMenuItem.addActionListener((ActionEvent e) -> {
            btnDeleteTopping.doClick();
        });
        editMenuItem = new JMenuItem("Edit Topping...");
        editMenuItem.addActionListener((ActionEvent e) -> { 
            btnEditTopping.doClick();
        });
        deleteMenuItem.setMnemonic(KeyEvent.VK_P);
        editMenuItem.setMnemonic(KeyEvent.VK_P);
        popup.add(editMenuItem);
        popup.add(deleteMenuItem);
        tblToppings.setComponentPopupMenu(popup);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Drinks Functions">  
    private void updateDrinksTable() {
        Drink[] drinks = ((DrinksController)Session.get().getController("DrinksController")).get(true);
        Vector model = new Vector();
        Vector headers = new Vector();
        headers.add("Name");
        headers.add("Description");
        headers.add("Small Size");
        headers.add("Small Price");
        headers.add("Medium Size");
        headers.add("Medium Price");
        headers.add("Large Size");
        headers.add("Large Price");
        headers.add("Image Name");
        for (Drink drink : drinks) {
            Vector row = new Vector();
            row.add(drink.getName());
            row.add(drink.getDescription());
            row.add(drink.getSmallSize());
            row.add(Double.valueOf(drink.getSmallPrice()));
            row.add(drink.getMediumSize());
            row.add(Double.valueOf(drink.getMediumPrice()));
            row.add(drink.getLargeSize());
            row.add(Double.valueOf(drink.getLargePrice()));
            row.add(drink.getImage());
            model.add(row);
        }
        JTable table = new JTable(model, headers);
        tblDrinks.setModel(table.getModel());
        tblDrinks.setDefaultEditor(Object.class, null);
    }
    
    private Drink getSelectedDrink() {
        String drinkName = tblDrinks.getValueAt(tblDrinks.getSelectedRow(), 0).toString();
        return ((DrinksController)Session.get().getController("DrinksController")).get(drinkName);
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Pizza Functions">  
    private void updatePizzaTable() {
        Pizza[] pizzas = ((PizzaController)Session.get().getController("PizzaController")).get(true);
        Vector model = new Vector();
        Vector headers = new Vector();
        headers.add("Name"); 
        headers.add("Description");
        headers.add("Small Price");
        headers.add("Medium Price");
        headers.add("Large Price");
        headers.add("Heat Rating");
        headers.add("Image Name");
        for (Pizza pizza : pizzas) {
            Vector row = new Vector();
            row.add(pizza.getName());
            row.add(pizza.getDescription());
            row.add(Double.valueOf(pizza.getSmallPrice()));
            row.add(Double.valueOf(pizza.getMediumPrice()));
            row.add(Double.valueOf(pizza.getLargePrice()));
            row.add(Double.valueOf(pizza.getHeatRating()));
            row.add(pizza.getImage());
            model.add(row);
        }
        JTable table = new JTable(model, headers);
        tblPizzas.setModel(table.getModel());
        tblPizzas.setDefaultEditor(Object.class, null);
    }
    
    private void btnAddPizzaActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
        FrmPizza frmPizza = new FrmPizza();
        frmPizza.setLocationRelativeTo(null);
        frmPizza.setVisible(true);
        frmPizza.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                updatePizzaTable();
            }
        });
    }                                           

    private void btnEditPizzaActionPerformed(java.awt.event.ActionEvent evt) {                                             
        // TODO add your handling code here:
        if (tblPizzas.getSelectedRow() == -1 || getSelectedPizza() == null){
            JOptionPane.showMessageDialog(this, "No pizza is selected", "Selection Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
        FrmPizza frmPizza = new FrmPizza(getSelectedPizza());
        frmPizza.setLocationRelativeTo(null);
        frmPizza.setVisible(true);
        frmPizza.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                updatePizzaTable();
            }
        });
        }
    }                                            

    private void btnDeletePizzaActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:
        if (getSelectedPizza() != null) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete " + getSelectedPizza().getName() + "?", "Confirm Deletion", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ((PizzaController)Session.get().getController("PizzaController")).remove(getSelectedPizza());
                updatePizzaTable();
            }
        }
    }                                              

    private Pizza getSelectedPizza() {
        String pizzaName = tblPizzas.getValueAt(tblPizzas.getSelectedRow(), 0).toString();
        return ((PizzaController)Session.get().getController("PizzaController")).get(pizzaName);
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Sides Functions">  
    private void updateSidesTable() {
        Side[] sides = ((SideController)Session.get().getController("SideController")).get(true);
        Vector model = new Vector();
        Vector headers = new Vector();
        headers.add("Name");
        headers.add("Description");
        headers.add("Price");
        headers.add("Heat Rating");
        headers.add("Image Name");
        for (Side side : sides) {
            Vector row = new Vector();
            row.add(side.getName());
            row.add(side.getDescription());
            row.add(Double.valueOf(side.getPrice()));
            row.add(Double.valueOf(side.getHeatRating()));
            row.add(side.getImage());
            model.add(row);
        }
        JTable table = new JTable(model, headers);
        tblSides.setModel(table.getModel());
        tblSides.setDefaultEditor(Object.class, null);
    }
    
    private Side getSelectedSide() {
        String sideName = tblSides.getValueAt(tblSides.getSelectedRow(), 0).toString();
        return ((SideController)Session.get().getController("SideController")).get(sideName);
    }
    //</editor-fold>
    
    private Topping getSelectedTopping() {
        String toppingName = tblToppings.getValueAt(tblToppings.getSelectedRow(), 0).toString();
        return ((ToppingsController)Session.get().getController("ToppingsController")).get(toppingName);
    }
    
    private void updateToppingsTable() {
        Topping[] toppingList = ((ToppingsController)Session.get().getController("ToppingsController")).get();
        Vector model = new Vector();
        Vector headers = new Vector();
        headers.add("Topping Name");
        headers.add("Topping Description");
        headers.add("Topping Price");
        headers.add("Heat Rating");
        headers.add("Topping Image");
        for (Topping topping : toppingList) {
            Vector row = new Vector();
            row.add(topping.getName());
            row.add(topping.getDescription());
            row.add(Double.valueOf(topping.getPrice()));
            row.add(Double.valueOf(topping.getHeatRating()));
            row.add(topping.getImage());
            model.add(row);
        }
        JTable table = new JTable(model, headers);
        tblToppings.setModel(table.getModel());
        tblToppings.setDefaultEditor(Object.class, null);
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="Orders Functions"> 
    
    private Order getSelectedOrder() {
        String orderID = tblOrders.getValueAt(tblOrders.getSelectedRow(), 0).toString();
        return ((OrderController)Session.get().getController("OrderController")).get(orderID);
    }
    
    private void updateOrdersTable() {
        Order[] ordersList = ((OrderController)Session.get().getController("OrderController")).get();
        Vector model = new Vector();
        Vector headers = new Vector();
        headers.add("Order ID");
        headers.add("Customer ID");
        headers.add("Order Date");
        headers.add("Delivery Date");
        headers.add("Status");
        for (Order order : ordersList) {
            Vector row = new Vector();
            row.add(Integer.valueOf(order.getOrderID()));
            row.add(Integer.valueOf(order.getCustomerID()));
            row.add(order.getOrderDateTime());
            row.add(order.getDeliveryDateTime());
            row.add(order.getStatus());
            model.add(row);
        }
        JTable table = new JTable(model, headers);
        tblOrders.setModel(table.getModel());
        tblOrders.setDefaultEditor(Object.class, null);
    }
    
    private void filterOrdersTable(String filterBy) {
        Vector model = new Vector();
        Vector headers = new Vector();
        headers.add("Order ID");
        headers.add("Customer ID");
        headers.add("Order Date");
        headers.add("Delivery Date");
        headers.add("Status");
        Order[] ordersList = ((OrderController)Session.get().getController("OrderController")).get();
        switch (filterBy) {
            case "Status":
                if (cboFilterOrderStatus.getItemCount() > 0 && (Object)cboFilterOrderStatus.getItemAt(0) instanceof OrderStatus || 
                    cboFilterOrderStatus.getSelectedItem().toString().equals("")) {
                    for (Order order : ordersList) {
                        if (cboFilterOrderStatus.getSelectedItem().toString().equals("") || 
                           (order.getStatus() != null && order.getStatus().equals(((OrderStatus)(Object)cboFilterOrderStatus.getSelectedItem()).getStatus()))) {
                            Vector row = new Vector();
                            row.add(Integer.valueOf(order.getOrderID()));
                            row.add(Integer.valueOf(order.getCustomerID()));
                            row.add(order.getOrderDateTime());
                            row.add(order.getDeliveryDateTime());
                            row.add(order.getStatus());
                            model.add(row);
                        }
                    }
                }
                break;
        }
        JTable table = new JTable(model, headers);
        tblOrders.setModel(table.getModel());
        tblOrders.setDefaultEditor(Object.class, null);
    }
    //</editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="Staff Functions"> 
    private void updateStaffTable() {
        Staff[] staffList = ((StaffController)Session.get().getController("StaffController")).get();
        Vector model = new Vector();
        Vector headers = new Vector();
        headers.add("Staff ID");
        headers.add("First Name");
        headers.add("Surname");
        headers.add("Email");
        headers.add("Dob");
        headers.add("Phone Number");
        headers.add("Address Line One");
        headers.add("Address Line Two");
        headers.add("City");
        headers.add("County");
        headers.add("Postode");
        headers.add("Company Position");
        headers.add("Start Date");
        for (Staff staff : staffList) {
            if (staff.getFirstName() == null) continue;
            if (staff.getFirstName().contains("=")) 
                staff = (Staff)Encryptor.decrypt(staff);
            if (staff.getEmployed() != null && staff.getEmployed().equals("1")) {
                Vector row = new Vector();
                row.add(Integer.parseInt(staff.getStaffID()));
                row.add(staff.getFirstName());
                row.add(staff.getSurname());
                row.add(staff.getEmail());
                row.add(staff.getDob().split("T")[0]);
                row.add(staff.getPhoneNumber());
                row.add(staff.getAddressLineOne());
                row.add(staff.getAddressLineTwo());
                row.add(staff.getCity());
                row.add(staff.getCounty());
                row.add(staff.getPostCode());
                row.add(staff.getCompanyPosition());
                row.add(staff.getStartDate().split("T")[0]);
                model.add(row);
            }
        }
        JTable table = new JTable(model, headers);
        tblStaff.setModel(table.getModel());
        tblStaff.setDefaultEditor(Object.class, null);
    }
    
    private Staff getSelectedStaff() {
        String staffID = tblStaff.getValueAt(tblStaff.getSelectedRow(), 0).toString();
        return ((StaffController)Session.get().getController("StaffController")).get(staffID);
    }
    // </editor-fold>
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnlMenu = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        scrollPizzas = new javax.swing.JScrollPane();
        pnlPizzas = new javax.swing.JPanel();
        btnAddPizza = new javax.swing.JButton();
        btnEditPizza = new javax.swing.JButton();
        btnDeletePizza = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblPizzas = new javax.swing.JTable();
        scrollDrinks = new javax.swing.JScrollPane();
        pnlDrinks = new javax.swing.JPanel();
        btnAddDrink = new javax.swing.JButton();
        btnEditDrink = new javax.swing.JButton();
        btnDeleteDrink = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblDrinks = new javax.swing.JTable();
        scrollSides = new javax.swing.JScrollPane();
        pnlSides = new javax.swing.JPanel();
        btnAddSide = new javax.swing.JButton();
        btnEditSide = new javax.swing.JButton();
        btnDeleteSide = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblSides = new javax.swing.JTable();
        pnlStaff = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStaff = new javax.swing.JTable();
        btnAddStaff = new javax.swing.JButton();
        btnDeleteStaff = new javax.swing.JButton();
        btnEditStaff = new javax.swing.JButton();
        pnlOrders = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cboFilterOrderStatus = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnViewOrder = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblOrders = new javax.swing.JTable();
        pnlMisc = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        scrollToppings = new javax.swing.JScrollPane();
        pnlToppings = new javax.swing.JPanel();
        btnAddTopping = new javax.swing.JButton();
        btnEditTopping = new javax.swing.JButton();
        btnDeleteTopping = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblToppings = new javax.swing.JTable();
        lblIcon = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblCurrentUser = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        lblTakings = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pizza Heaven - Manager Application");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel3.setLayout(new java.awt.GridBagLayout());

        scrollPizzas.setMaximumSize(new java.awt.Dimension(500, 200));
        scrollPizzas.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollPizzas.setPreferredSize(new java.awt.Dimension(500, 200));

        pnlPizzas.setMaximumSize(new java.awt.Dimension(500, 200));
        pnlPizzas.setMinimumSize(new java.awt.Dimension(0, 0));
        pnlPizzas.setPreferredSize(new java.awt.Dimension(500, 200));

        btnAddPizza.setBackground(new java.awt.Color(35, 141, 0));
        btnAddPizza.setForeground(new java.awt.Color(245, 245, 245));
        btnAddPizza.setText("Add Pizza");
        btnAddPizza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPizzaActionPerformed(evt);
            }
        });

        btnEditPizza.setBackground(new java.awt.Color(200, 80, 0));
        btnEditPizza.setForeground(new java.awt.Color(245, 245, 245));
        btnEditPizza.setText("Edit Pizza");
        btnEditPizza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditPizzaActionPerformed(evt);
            }
        });

        btnDeletePizza.setBackground(new java.awt.Color(154, 0, 0));
        btnDeletePizza.setForeground(new java.awt.Color(245, 245, 245));
        btnDeletePizza.setText("Delete Pizza");
        btnDeletePizza.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletePizzaActionPerformed(evt);
            }
        });

        tblPizzas.setAutoCreateRowSorter(true);
        tblPizzas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblPizzas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPizzasMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tblPizzas);

        javax.swing.GroupLayout pnlPizzasLayout = new javax.swing.GroupLayout(pnlPizzas);
        pnlPizzas.setLayout(pnlPizzasLayout);
        pnlPizzasLayout.setHorizontalGroup(
            pnlPizzasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPizzasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPizzasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDeletePizza, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditPizza, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddPizza, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlPizzasLayout.setVerticalGroup(
            pnlPizzasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPizzasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPizzasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlPizzasLayout.createSequentialGroup()
                        .addComponent(btnAddPizza, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditPizza, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeletePizza, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        scrollPizzas.setViewportView(pnlPizzas);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 16;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(scrollPizzas, gridBagConstraints);

        scrollDrinks.setMaximumSize(new java.awt.Dimension(500, 200));
        scrollDrinks.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollDrinks.setPreferredSize(new java.awt.Dimension(500, 200));

        pnlDrinks.setMaximumSize(new java.awt.Dimension(500, 200));
        pnlDrinks.setPreferredSize(new java.awt.Dimension(500, 200));

        btnAddDrink.setBackground(new java.awt.Color(35, 141, 0));
        btnAddDrink.setForeground(new java.awt.Color(245, 245, 245));
        btnAddDrink.setText("Add Drink");
        btnAddDrink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddDrinkActionPerformed(evt);
            }
        });

        btnEditDrink.setBackground(new java.awt.Color(200, 80, 0));
        btnEditDrink.setForeground(new java.awt.Color(245, 245, 245));
        btnEditDrink.setText("Edit Drink");
        btnEditDrink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditDrinkActionPerformed(evt);
            }
        });

        btnDeleteDrink.setBackground(new java.awt.Color(154, 0, 0));
        btnDeleteDrink.setForeground(new java.awt.Color(245, 245, 245));
        btnDeleteDrink.setText("Delete Drink");
        btnDeleteDrink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteDrinkActionPerformed(evt);
            }
        });

        tblDrinks.setAutoCreateRowSorter(true);
        tblDrinks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblDrinks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDrinksMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblDrinks);

        javax.swing.GroupLayout pnlDrinksLayout = new javax.swing.GroupLayout(pnlDrinks);
        pnlDrinks.setLayout(pnlDrinksLayout);
        pnlDrinksLayout.setHorizontalGroup(
            pnlDrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDrinksLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDeleteDrink, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditDrink, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddDrink, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlDrinksLayout.setVerticalGroup(
            pnlDrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDrinksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDrinksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                    .addGroup(pnlDrinksLayout.createSequentialGroup()
                        .addComponent(btnAddDrink, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditDrink, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteDrink, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        scrollDrinks.setViewportView(pnlDrinks);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 16;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(scrollDrinks, gridBagConstraints);

        scrollSides.setMaximumSize(new java.awt.Dimension(500, 200));
        scrollSides.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollSides.setPreferredSize(new java.awt.Dimension(500, 200));

        pnlSides.setMaximumSize(new java.awt.Dimension(500, 200));
        pnlSides.setPreferredSize(new java.awt.Dimension(500, 200));

        btnAddSide.setBackground(new java.awt.Color(35, 141, 0));
        btnAddSide.setForeground(new java.awt.Color(245, 245, 245));
        btnAddSide.setText("Add Side");
        btnAddSide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSideActionPerformed(evt);
            }
        });

        btnEditSide.setBackground(new java.awt.Color(200, 80, 0));
        btnEditSide.setForeground(new java.awt.Color(245, 245, 245));
        btnEditSide.setText("Edit Side");
        btnEditSide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditSideActionPerformed(evt);
            }
        });

        btnDeleteSide.setBackground(new java.awt.Color(154, 0, 0));
        btnDeleteSide.setForeground(new java.awt.Color(245, 245, 245));
        btnDeleteSide.setText("Delete Side");
        btnDeleteSide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteSideActionPerformed(evt);
            }
        });

        tblSides.setAutoCreateRowSorter(true);
        tblSides.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSides.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSidesMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(tblSides);

        javax.swing.GroupLayout pnlSidesLayout = new javax.swing.GroupLayout(pnlSides);
        pnlSides.setLayout(pnlSidesLayout);
        pnlSidesLayout.setHorizontalGroup(
            pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDeleteSide, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditSide, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddSide, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSidesLayout.setVerticalGroup(
            pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSidesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlSidesLayout.createSequentialGroup()
                        .addComponent(btnAddSide, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditSide, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteSide, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        scrollSides.setViewportView(pnlSides);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 16;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(scrollSides, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel3);

        javax.swing.GroupLayout pnlMenuLayout = new javax.swing.GroupLayout(pnlMenu);
        pnlMenu.setLayout(pnlMenuLayout);
        pnlMenuLayout.setHorizontalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        pnlMenuLayout.setVerticalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );

        jTabbedPane1.addTab("Menu", pnlMenu);

        tblStaff.setAutoCreateRowSorter(true);
        tblStaff.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblStaff.setPreferredSize(null);
        tblStaff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStaffMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblStaff);

        btnAddStaff.setBackground(new java.awt.Color(35, 141, 0));
        btnAddStaff.setForeground(new java.awt.Color(245, 245, 245));
        btnAddStaff.setText("<html><center>Add Staff<br />Member</center></html>");
        btnAddStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStaffActionPerformed(evt);
            }
        });

        btnDeleteStaff.setBackground(new java.awt.Color(154, 0, 0));
        btnDeleteStaff.setForeground(new java.awt.Color(245, 245, 245));
        btnDeleteStaff.setText("<html><center>Delete Staff<br />Member</center></html>");
        btnDeleteStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteStaffActionPerformed(evt);
            }
        });

        btnEditStaff.setBackground(new java.awt.Color(200, 80, 0));
        btnEditStaff.setForeground(new java.awt.Color(245, 245, 245));
        btnEditStaff.setText("<html><center>Edit Staff <br />Member</center></html>");
        btnEditStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditStaffActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlStaffLayout = new javax.swing.GroupLayout(pnlStaff);
        pnlStaff.setLayout(pnlStaffLayout);
        pnlStaffLayout.setHorizontalGroup(
            pnlStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStaffLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlStaffLayout.createSequentialGroup()
                        .addComponent(btnAddStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1139, Short.MAX_VALUE)))
        );
        pnlStaffLayout.setVerticalGroup(
            pnlStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStaffLayout.createSequentialGroup()
                .addGroup(pnlStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEditStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlStaffLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAddStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDeleteStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Staff", pnlStaff);

        jLabel2.setText("Order Status");

        cboFilterOrderStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboFilterOrderStatus.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboFilterOrderStatusItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Filtering Options");

        jLabel3.setText("<html>There would be more filtering options here if we had the time :)</html>");

        btnViewOrder.setText("View Order");
        btnViewOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewOrderActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboFilterOrderStatus, 0, 172, Short.MAX_VALUE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(btnViewOrder))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cboFilterOrderStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnViewOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(441, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        tblOrders.setAutoCreateRowSorter(true);
        tblOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblOrders);

        jSplitPane1.setRightComponent(jScrollPane4);

        javax.swing.GroupLayout pnlOrdersLayout = new javax.swing.GroupLayout(pnlOrders);
        pnlOrders.setLayout(pnlOrdersLayout);
        pnlOrdersLayout.setHorizontalGroup(
            pnlOrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1149, Short.MAX_VALUE)
        );
        pnlOrdersLayout.setVerticalGroup(
            pnlOrdersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        jTabbedPane1.addTab("Orders", pnlOrders);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        scrollToppings.setMaximumSize(new java.awt.Dimension(500, 200));
        scrollToppings.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollToppings.setPreferredSize(new java.awt.Dimension(500, 200));

        pnlToppings.setMaximumSize(new java.awt.Dimension(500, 200));
        pnlToppings.setPreferredSize(new java.awt.Dimension(500, 200));

        btnAddTopping.setBackground(new java.awt.Color(35, 141, 0));
        btnAddTopping.setForeground(new java.awt.Color(245, 245, 245));
        btnAddTopping.setText("Add Topping");
        btnAddTopping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToppingActionPerformed(evt);
            }
        });

        btnEditTopping.setBackground(new java.awt.Color(200, 80, 0));
        btnEditTopping.setForeground(new java.awt.Color(245, 245, 245));
        btnEditTopping.setText("Edit Topping");
        btnEditTopping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditToppingActionPerformed(evt);
            }
        });

        btnDeleteTopping.setBackground(new java.awt.Color(154, 0, 0));
        btnDeleteTopping.setForeground(new java.awt.Color(245, 245, 245));
        btnDeleteTopping.setText("Delete Topping");
        btnDeleteTopping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteToppingActionPerformed(evt);
            }
        });

        tblToppings.setAutoCreateRowSorter(true);
        tblToppings.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblToppings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblToppingsMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tblToppings);

        javax.swing.GroupLayout pnlToppingsLayout = new javax.swing.GroupLayout(pnlToppings);
        pnlToppings.setLayout(pnlToppingsLayout);
        pnlToppingsLayout.setHorizontalGroup(
            pnlToppingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToppingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 989, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addGroup(pnlToppingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDeleteTopping, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEditTopping, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddTopping, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlToppingsLayout.setVerticalGroup(
            pnlToppingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlToppingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlToppingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlToppingsLayout.createSequentialGroup()
                        .addComponent(btnAddTopping, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditTopping, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteTopping, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        scrollToppings.setViewportView(pnlToppings);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 16;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(scrollToppings, gridBagConstraints);

        jScrollPane3.setViewportView(jPanel4);

        javax.swing.GroupLayout pnlMiscLayout = new javax.swing.GroupLayout(pnlMisc);
        pnlMisc.setLayout(pnlMiscLayout);
        pnlMiscLayout.setHorizontalGroup(
            pnlMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        pnlMiscLayout.setVerticalGroup(
            pnlMiscLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );

        jTabbedPane1.addTab("Custom Pizza", pnlMisc);

        lblIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pizzaheaven/images/PizzaHeavenLogo1.png"))); // NOI18N
        lblIcon.setMaximumSize(new java.awt.Dimension(10000, 10000));
        lblIcon.setPreferredSize(null);

        lblCurrentUser.setText("Welcome back:");

        lblTime.setText("jLabel3");

        lblTakings.setText("Todays Takings: £");

        btnLogout.setBackground(new java.awt.Color(0, 153, 255));
        btnLogout.setForeground(new java.awt.Color(245, 245, 245));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnLogout))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblTakings)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblCurrentUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                        .addComponent(lblTime)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTime)
                    .addComponent(lblCurrentUser))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTakings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblPizzasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPizzasMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            btnEditPizza.doClick();
        }
        
                                      
    }//GEN-LAST:event_tblPizzasMouseClicked

    private void btnAddStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStaffActionPerformed
        // TODO add your handling code here:
        FrmStaff frmStaff = new FrmStaff();
        frmStaff.setLocationRelativeTo(null);
        frmStaff.setVisible(true);
        frmStaff.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                updateStaffTable();
            }
        });
    }//GEN-LAST:event_btnAddStaffActionPerformed

    private void btnDeleteStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteStaffActionPerformed
        // TODO add your handling code here:
        if (tblStaff.getSelectedRow() == -1 || getSelectedStaff() == null){
            JOptionPane.showMessageDialog(this, "No staff member is selected", "Selection Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Staff staffToDelete = getSelectedStaff();
            if (staffToDelete.getFirstName().contains("=")) staffToDelete = (Staff)Encryptor.decrypt(staffToDelete);
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete " + staffToDelete.toString() + "?", "Confirm Selection", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                staffToDelete.setEmployed("0");
                int apiResult = ((StaffController)Session.get().getController("StaffController")).update(staffToDelete);
                System.out.println("[INFO] API Result: " + apiResult);
                updateStaffTable();
            } 
        }
    }//GEN-LAST:event_btnDeleteStaffActionPerformed

    private void btnEditStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditStaffActionPerformed
        // TODO add your handling code here:
        if (tblStaff.getSelectedRow() == -1 || getSelectedStaff() == null){
            JOptionPane.showMessageDialog(this, "No staff member is selected", "Selection Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            FrmStaff frmStaff = new FrmStaff(getSelectedStaff());
            frmStaff.setLocationRelativeTo(null);
            frmStaff.setVisible(true);
            frmStaff.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent evt) {
                    updateStaffTable();
                }
            });
            
        }
        
    }//GEN-LAST:event_btnEditStaffActionPerformed

    private void cboFilterOrderStatusItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboFilterOrderStatusItemStateChanged
        // TODO add your handling code here:
        filterOrdersTable("Status");
    }//GEN-LAST:event_cboFilterOrderStatusItemStateChanged

    private void tblDrinksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDrinksMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            btnEditDrink.doClick();
        }
    }//GEN-LAST:event_tblDrinksMouseClicked

    private void tblSidesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSidesMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            btnEditSide.doClick();
        }
    }//GEN-LAST:event_tblSidesMouseClicked

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        /*Image currentImage = ((ImageIcon)lblIcon.getIcon()).getImage();
        double widthToHeightScale = (double)lblIcon.getHeight() / (double)lblIcon.getWidth();
        int height = ((Double)(this.getWidth() * widthToHeightScale)).intValue();
        int width = this.getWidth();
        Image image = currentImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        lblIcon.setIcon(new ImageIcon(image));*/
    }//GEN-LAST:event_formComponentResized

    private void btnAddDrinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddDrinkActionPerformed
        // TODO add your handling code here:
        FrmDrink frmDrink = new FrmDrink();
        frmDrink.setLocationRelativeTo(null);
        frmDrink.setVisible(true);
        frmDrink.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                updateDrinksTable();
            }
        });
    }//GEN-LAST:event_btnAddDrinkActionPerformed

    private void btnEditDrinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditDrinkActionPerformed
        // TODO add your handling code here:
         if (tblDrinks.getSelectedRow() == -1 || getSelectedDrink() == null){
            JOptionPane.showMessageDialog(this, "No Drink is selected", "Selection Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
        FrmDrink frmDrink = new FrmDrink(getSelectedDrink());
        frmDrink.setLocationRelativeTo(null);
        frmDrink.setVisible(true);
        frmDrink.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                updateDrinksTable();
            }
        });
         }
    }//GEN-LAST:event_btnEditDrinkActionPerformed

    private void btnDeleteDrinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteDrinkActionPerformed
        // TODO add your handling code here:
        if (getSelectedDrink() != null) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete " + getSelectedDrink().getName() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ((DrinksController)Session.get().getController("DrinksController")).remove(getSelectedDrink());
                updateDrinksTable();
            }
        }
    }//GEN-LAST:event_btnDeleteDrinkActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        Session.get().dispose();
        this.dispose();
        FrmLogin frmLogin = new FrmLogin();
        frmLogin.setLocationRelativeTo(null);
        frmLogin.setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void tblStaffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStaffMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            btnEditStaff.doClick();
        }
    }//GEN-LAST:event_tblStaffMouseClicked

    private void btnDeleteSideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteSideActionPerformed
        // TODO add your handling code here:
        if (getSelectedSide() != null) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete " + getSelectedSide().getName() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ((SideController)Session.get().getController("SideController")).remove(getSelectedSide());
                updateSidesTable();
            }
        }
    }//GEN-LAST:event_btnDeleteSideActionPerformed

    private void btnAddSideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSideActionPerformed
        // TODO add your handling code here:
        FrmSide frmSide = new FrmSide();
        frmSide.setLocationRelativeTo(null);
        frmSide.setVisible(true);
        frmSide.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                updateSidesTable();
            }
        });
    }//GEN-LAST:event_btnAddSideActionPerformed

    private void btnEditSideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditSideActionPerformed
        // TODO add your handling code here:
         if (tblSides.getSelectedRow() == -1 || getSelectedSide() == null){
            JOptionPane.showMessageDialog(this, "No side is selected", "Selection Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
        FrmSide frmSide = new FrmSide(getSelectedSide());
        frmSide.setLocationRelativeTo(null);
        frmSide.setVisible(true);
        frmSide.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                updateSidesTable();
            }
        });
         }
        
    }//GEN-LAST:event_btnEditSideActionPerformed

    private void btnAddToppingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToppingActionPerformed
        // TODO add your handling code here:
        FrmTopping frmTopping = new FrmTopping();
        frmTopping.setLocationRelativeTo(null);
        frmTopping.setVisible(true);
        frmTopping.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                updateToppingsTable();
            }
        });
    }//GEN-LAST:event_btnAddToppingActionPerformed

    private void btnEditToppingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditToppingActionPerformed
        // TODO add your handling code here:
        if (tblToppings.getSelectedRow() == -1 || getSelectedTopping() == null){
            JOptionPane.showMessageDialog(this, "No topping is selected", "Selection Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
        FrmTopping frmTopping = new FrmTopping(getSelectedTopping());
        frmTopping.setLocationRelativeTo(null);
        frmTopping.setVisible(true);
        frmTopping.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                updateToppingsTable();
            }
        });
         }
    }//GEN-LAST:event_btnEditToppingActionPerformed

    private void btnDeleteToppingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteToppingActionPerformed
        // TODO add your handling code here:
        if (getSelectedTopping() != null) {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete " + getSelectedTopping().getName() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                ((ToppingsController)Session.get().getController("ToppingsController")).remove(getSelectedTopping());
                updateToppingsTable();
            }
        }
    }//GEN-LAST:event_btnDeleteToppingActionPerformed

    private void tblToppingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblToppingsMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {
            btnEditTopping.doClick();
        }
    }//GEN-LAST:event_tblToppingsMouseClicked

    private void btnViewOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewOrderActionPerformed
        // TODO add your handling code here:
        if (tblOrders.getSelectedRow() == -1 || getSelectedOrder() == null){
            JOptionPane.showMessageDialog(this, "No order is selected", "Selection Error", JOptionPane.INFORMATION_MESSAGE);
        } else {
            FrmOrder frmOrder = new FrmOrder(getSelectedOrder());
            frmOrder.setLocationRelativeTo(null);
            frmOrder.setVisible(true);
            frmOrder.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent evt) {
                    updateOrdersTable();
                }
            });
         }
    }//GEN-LAST:event_btnViewOrderActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAdmin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDrink;
    private javax.swing.JButton btnAddPizza;
    private javax.swing.JButton btnAddSide;
    private javax.swing.JButton btnAddStaff;
    private javax.swing.JButton btnAddTopping;
    private javax.swing.JButton btnDeleteDrink;
    private javax.swing.JButton btnDeletePizza;
    private javax.swing.JButton btnDeleteSide;
    private javax.swing.JButton btnDeleteStaff;
    private javax.swing.JButton btnDeleteTopping;
    private javax.swing.JButton btnEditDrink;
    private javax.swing.JButton btnEditPizza;
    private javax.swing.JButton btnEditSide;
    private javax.swing.JButton btnEditStaff;
    private javax.swing.JButton btnEditTopping;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnViewOrder;
    private javax.swing.JComboBox<String> cboFilterOrderStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCurrentUser;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblTakings;
    private javax.swing.JLabel lblTime;
    private javax.swing.JPanel pnlDrinks;
    private javax.swing.JPanel pnlMenu;
    private javax.swing.JPanel pnlMisc;
    private javax.swing.JPanel pnlOrders;
    private javax.swing.JPanel pnlPizzas;
    private javax.swing.JPanel pnlSides;
    private javax.swing.JPanel pnlStaff;
    private javax.swing.JPanel pnlToppings;
    private javax.swing.JScrollPane scrollDrinks;
    private javax.swing.JScrollPane scrollPizzas;
    private javax.swing.JScrollPane scrollSides;
    private javax.swing.JScrollPane scrollToppings;
    private javax.swing.JTable tblDrinks;
    private javax.swing.JTable tblOrders;
    private javax.swing.JTable tblPizzas;
    private javax.swing.JTable tblSides;
    private javax.swing.JTable tblStaff;
    private javax.swing.JTable tblToppings;
    // End of variables declaration//GEN-END:variables
}
