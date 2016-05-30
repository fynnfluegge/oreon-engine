package cdk.main;

import javax.swing.JFrame;

import cdk.tools.terrainEditor.OpenGLEngine;
import cdk.tools.terrainEditor.TerrainEditorInterface;
import simulations.templates.Simulation;

public class MainInterface extends JFrame{

	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private Simulation simulation;
	
	private TerrainEditorInterface terrainEditor;
	
	
	public MainInterface() {
	     initComponents();
	}
                        
	    private void initComponents() {

	        jPanel1 = new javax.swing.JPanel();
	        OpenGLCanvas = new java.awt.Canvas();
	        jMenuBar1 = new javax.swing.JMenuBar();
	        FileMenu = new javax.swing.JMenu();
	        EditMenu = new javax.swing.JMenu();
	        ToolsMenu = new javax.swing.JMenu();
	        TerrainEditorTool = new javax.swing.JMenuItem();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setPreferredSize(new java.awt.Dimension(800, 500));

	        jPanel1.setBackground(new java.awt.Color(30, 30, 30));
	        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addGap(50, 50, 50)
	                .addComponent(OpenGLCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(60, Short.MAX_VALUE))
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addGap(40, 40, 40)
	                .addComponent(OpenGLCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(40, Short.MAX_VALUE))
	        );

	        FileMenu.setText("File");
	        jMenuBar1.add(FileMenu);

	        EditMenu.setText("Edit");
	        jMenuBar1.add(EditMenu);

	        ToolsMenu.setText("Tools");

	        TerrainEditorTool.setText("Terrain Editor");
	        TerrainEditorTool.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                TerrainEditorToolActionPerformed(evt);
	            }
	        });
	        ToolsMenu.add(TerrainEditorTool);

	        jMenuBar1.add(ToolsMenu);

	        setJMenuBar(jMenuBar1);

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
	        
	        this.setLocation(200, 300);
	        
	        terrainEditor = new TerrainEditorInterface();
	       
	        Thread OpenGL = new Thread(new OpenGLEngine(OpenGLCanvas));
	        
	        OpenGL.start();
	        
	        
	    }// </editor-fold>                        

	    private void TerrainEditorToolActionPerformed(java.awt.event.ActionEvent evt) {                                                  
	        
	    	terrainEditor.setLocation(1100, 200);
	    	terrainEditor.setVisible(true);
	    }                                                 

	    /**
	     * @param args the command line arguments
	     */
	    public static void start() {
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
	            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (InstantiationException ex) {
	            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (IllegalAccessException ex) {
	            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
	            java.util.logging.Logger.getLogger(MainInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
	        }
	        //</editor-fold>

	        /* Create and display the form */
	        java.awt.EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                new MainInterface().setVisible(true);
	            }
	        });
	    }

	    // Variables declaration - do not modify                     
	    private javax.swing.JMenu EditMenu;
	    private javax.swing.JMenu FileMenu;
	    private javax.swing.JMenuItem TerrainEditorTool;
	    private javax.swing.JMenu ToolsMenu;
	    private java.awt.Canvas OpenGLCanvas;
	    private javax.swing.JMenuBar jMenuBar1;
	    private javax.swing.JPanel jPanel1;
	    // End of variables declaration   

	public static void main(String[] args) {
		//MainInterface window = new MainInterface();
		MainInterface.start();

	}

}
