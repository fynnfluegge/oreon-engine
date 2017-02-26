package apps.worldgenerator.mainInterface;

import javax.swing.JFrame;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import apps.worldgenerator.db.DB;
import apps.worldgenerator.tools.terrainEditor.TerrainEditorInterface;
import engine.core.CoreEngine;

public class MainInterface extends JFrame{

	private static final long serialVersionUID = 1L;
	
	DB database;
	
	private TerrainEditorInterface terrainEditor;
	
	
	 public MainInterface() {
	        initComponents();
	    }

	    
	    private void initComponents() {

	        jPanel1 = new javax.swing.JPanel();
	        OpenGLCanvas = new java.awt.Canvas();
	        dataVolumeValue = new javax.swing.JLabel();
	        jButtonSave = new javax.swing.JButton();
	        jButtonReload = new javax.swing.JButton();
	        jMenuBar1 = new javax.swing.JMenuBar();
	        FileMenu = new javax.swing.JMenu();
	        EditMenu = new javax.swing.JMenu();
	        ToolsMenu = new javax.swing.JMenu();
	        TerrainEditorTool = new javax.swing.JMenuItem();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setPreferredSize(new java.awt.Dimension(1300, 740));

	        jPanel1.setBackground(new java.awt.Color(30, 30, 30));
	        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

	        dataVolumeValue.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
	        dataVolumeValue.setForeground(new java.awt.Color(102, 255, 102));

	        jButtonSave.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
	        jButtonSave.setText("Save");
	        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                jButtonSaveActionPerformed(evt);
	            }
	        });

	        jButtonReload.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
	        jButtonReload.setText("Reload");
	        jButtonReload.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                try {
						jButtonReloadActionPerformed(evt);
					} catch (LWJGLException e) {
						e.printStackTrace();
					}
	            }
	        });

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(jButtonReload)
	                        .addGap(46, 46, 46)
	                        .addComponent(jButtonSave))
	                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                        .addGroup(jPanel1Layout.createSequentialGroup()
	                            .addGap(886, 886, 886)
	                            .addComponent(dataVolumeValue, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
	                        .addGroup(jPanel1Layout.createSequentialGroup()
	                            .addGap(38, 38, 38)
	                            .addComponent(OpenGLCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE))))
	                .addContainerGap(42, Short.MAX_VALUE))
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addGap(41, 41, 41)
	                .addComponent(OpenGLCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(dataVolumeValue, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(18, 18, 18)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jButtonSave)
	                    .addComponent(jButtonReload))
	                .addGap(32, 32, 32))
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
	        
	        this.setLocation(100, 50);
	        
	        terrainEditor = new TerrainEditorInterface();
	        
	        Thread OpenGL = new Thread(new Engine(OpenGLCanvas));
	        
	        OpenGL.start();
	        
	        
	    }// </editor-fold>                        

	    private void TerrainEditorToolActionPerformed(java.awt.event.ActionEvent evt) {                                                  
	        
	    	terrainEditor.update(LoDChartPanel, tessellationFunctionPanel, dataVolumeValue);
	    	
	    	terrainEditor.setLocation(1000, 100);
	    	terrainEditor.setVisible(true);
	    }     
	    
	    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {                                            
	        DB.getTerrainConfiguration().saveToFile();
	    }                                           

	    private void jButtonReloadActionPerformed(java.awt.event.ActionEvent evt) throws LWJGLException { 
	    	CoreEngine.setShareGLContext(true);
	    	
	    	CoreEngine.getGLContextLock().lock();
	    	try{
	    		while(!CoreEngine.isGlContextfree())
	    		{
	    			try {
	    				CoreEngine.getHoldGLContext().await();
	    			} catch (InterruptedException e) {
	    				e.printStackTrace();
	    			}
	    		}
	    	}
	    	finally{
	    		CoreEngine.getGLContextLock().unlock();
	    	}
	  
	    	Display.makeCurrent();
	    	
	        DB.getTerrainConfiguration().ReloadFractals("./res/editor/terrainEditor/terrainSettings.ter");
	        
	        CoreEngine.getGLContextLock().lock();
	        try{
	        	try {
	        		Display.releaseContext();
	        		CoreEngine.setGlContextfree(false);
	        		CoreEngine.getHoldGLContext().signal();
	        	} catch (LWJGLException e1) {
	        		e1.printStackTrace();
	        	}
	        }
	        finally{
	        	CoreEngine.getGLContextLock().unlock();
	        }
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
	    private javax.swing.JMenuBar jMenuBar1;
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JPanel tessellationFunctionPanel;
	    private javax.swing.JButton jButtonReload;
	    private javax.swing.JButton jButtonSave;
	    // End of variables declaration   

}
