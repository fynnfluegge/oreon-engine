package cdk.main;

import javax.swing.JFrame;

import cdk.database.DataBase;
import cdk.tools.terrainEditor.OpenGLEngine;
import cdk.tools.terrainEditor.TerrainEditorInterface;

public class MainInterface extends JFrame{

	private static final long serialVersionUID = 1L;
	
	DataBase database;
	
	private TerrainEditorInterface terrainEditor;
	
	
	public MainInterface() {
	     initComponents();
	}
                        
	    private void initComponents() {

	    	jPanel1 = new javax.swing.JPanel();
	        OpenGLCanvas = new java.awt.Canvas();
	        tessellationFunctionPanel = new javax.swing.JPanel();
	        LoDChartPanel = new javax.swing.JPanel();
	        dataVolumetxt = new javax.swing.JLabel();
	        dataVolumeValue = new javax.swing.JLabel();
	        mbtxt = new javax.swing.JLabel();
	        jMenuBar1 = new javax.swing.JMenuBar();
	        FileMenu = new javax.swing.JMenu();
	        EditMenu = new javax.swing.JMenu();
	        ToolsMenu = new javax.swing.JMenu();
	        TerrainEditorTool = new javax.swing.JMenuItem();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setPreferredSize(new java.awt.Dimension(900, 700));

	        jPanel1.setBackground(new java.awt.Color(30, 30, 30));
	        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

	        tessellationFunctionPanel.setLayout(new java.awt.BorderLayout());

	        LoDChartPanel.setLayout(new java.awt.BorderLayout());

	        dataVolumetxt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
	        dataVolumetxt.setForeground(new java.awt.Color(51, 255, 51));
	        dataVolumetxt.setText(" GPU memory usage");

	        dataVolumeValue.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
	        dataVolumeValue.setForeground(new java.awt.Color(102, 255, 102));

	        mbtxt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
	        mbtxt.setForeground(new java.awt.Color(102, 255, 102));
	        mbtxt.setText("mb");

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
	                .addGap(39, 39, 39)
	                .addComponent(OpenGLCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(39, 39, 39)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addComponent(dataVolumetxt)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                        .addComponent(dataVolumeValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(mbtxt))
	                    .addComponent(tessellationFunctionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
	                    .addComponent(LoDChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
	                .addGap(42, 42, 42))
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addGap(41, 41, 41)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                    .addComponent(OpenGLCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                            .addComponent(dataVolumeValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                            .addComponent(dataVolumetxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                            .addComponent(mbtxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                        .addGap(40, 40, 40)
	                        .addComponent(LoDChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(tessellationFunctionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap(90, Short.MAX_VALUE))
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
	            .addGroup(layout.createSequentialGroup()
	                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(0, 49, Short.MAX_VALUE))
	        );

	        pack();
	        
	        this.setLocation(200, 300);
	        
	        terrainEditor = new TerrainEditorInterface();
	        
	        Thread OpenGL = new Thread(new OpenGLEngine(OpenGLCanvas));
	        
	        OpenGL.start();
	        
	        
	    }// </editor-fold>                        

	    private void TerrainEditorToolActionPerformed(java.awt.event.ActionEvent evt) {                                                  
	        
	    	terrainEditor.update(LoDChartPanel, tessellationFunctionPanel, dataVolumeValue);
	    	
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
	    private javax.swing.JPanel LoDChartPanel;
	    private java.awt.Canvas OpenGLCanvas;
	    private javax.swing.JMenuItem TerrainEditorTool;
	    private javax.swing.JMenu ToolsMenu;
	    private javax.swing.JLabel dataVolumeValue;
	    private javax.swing.JLabel dataVolumetxt;
	    private javax.swing.JMenuBar jMenuBar1;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JLabel mbtxt;
	    private javax.swing.JPanel tessellationFunctionPanel;
	    // End of variables declaration   

	public static void main(String[] args) {
		//MainInterface window = new MainInterface();
		MainInterface.start();

	}

}
