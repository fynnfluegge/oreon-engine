package apps.worldgenerator.tools.terrainEditor;

import javax.swing.JFrame;

import apps.worldgenerator.db.DB;


public class TerrainEditorInterface extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static boolean init;
	
	
	/**
     * Creates new form TerrainEditorTool
     */
    public TerrainEditorInterface() {
        initComponents();
    }

                            
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        LoDTessPanel1 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        TessellationFactor_Slider1 = new javax.swing.JSlider();
        TessellationSlope_Slider1 = new javax.swing.JSlider();
        TessellationShift_Slider1 = new javax.swing.JSlider();
        DetailRange_Slider1 = new javax.swing.JSlider();
        LoD1Range_Slider1 = new javax.swing.JSlider();
        LoD2Range_Slider1 = new javax.swing.JSlider();
        LoD3Range_Slider1 = new javax.swing.JSlider();
        LoD4Range_Slider1 = new javax.swing.JSlider();
        LoD5Range_Slider1 = new javax.swing.JSlider();
        LoD6Range_Slider1 = new javax.swing.JSlider();
        LoD7Range_Slider1 = new javax.swing.JSlider();
        LoD8Range_Slider1 = new javax.swing.JSlider();
        TessellationFactor_Value1 = new javax.swing.JLabel();
        DetailRange_Value1 = new javax.swing.JLabel();
        TessellationSlope_Value1 = new javax.swing.JLabel();
        TessellationShift_Value1 = new javax.swing.JLabel();
        LoD1Range_Value1 = new javax.swing.JLabel();
        LoD2Range_Value1 = new javax.swing.JLabel();
        LoD3Range_Value1 = new javax.swing.JLabel();
        LoD4Range_Value1 = new javax.swing.JLabel();
        LoD5Range_Value1 = new javax.swing.JLabel();
        LoD6Range_Value1 = new javax.swing.JLabel();
        LoD7Range_Value1 = new javax.swing.JLabel();
        LoD8Range_Value1 = new javax.swing.JLabel();
        FractalsPanel1 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jSliderFractalStrength0 = new javax.swing.JSlider();
        jSliderFractalStrength1 = new javax.swing.JSlider();
        jSliderFractalStrength2 = new javax.swing.JSlider();
        jSliderFractalStrength3 = new javax.swing.JSlider();
        jSliderFractalStrength4 = new javax.swing.JSlider();
        jSliderFractalStrength5 = new javax.swing.JSlider();
        jSliderFractalStrength7 = new javax.swing.JSlider();
        jSliderFractalStrength6 = new javax.swing.JSlider();
        jSliderFractalStrength8 = new javax.swing.JSlider();
        jSliderFractalStrength9 = new javax.swing.JSlider();
        jLabelFractalStrength0 = new javax.swing.JLabel();
        jLabelFractalStrength1 = new javax.swing.JLabel();
        jLabelFractalStrength2 = new javax.swing.JLabel();
        jLabelFractalStrength3 = new javax.swing.JLabel();
        jLabelFractalStrength4 = new javax.swing.JLabel();
        jLabelFractalStrength5 = new javax.swing.JLabel();
        jLabelFractalStrength6 = new javax.swing.JLabel();
        jLabelFractalStrength7 = new javax.swing.JLabel();
        jLabelFractalStrength8 = new javax.swing.JLabel();
        jLabelFractalStrength9 = new javax.swing.JLabel();
        
        TessellationFactor_Slider1.setMaximum(5000);
        TessellationSlope_Slider1.setMaximum(200);
        TessellationSlope_Slider1.setMinimum(-200);
        TessellationShift_Slider1.setMaximum(1000);
        TessellationShift_Slider1.setMinimum(-1000);
        jSliderFractalStrength0.setMaximum(300);
        jSliderFractalStrength1.setMaximum(200);
        jSliderFractalStrength2.setMaximum(100);
        jSliderFractalStrength3.setMaximum(100);
        jSliderFractalStrength4.setMaximum(20);
        jSliderFractalStrength5.setMaximum(10);
        jSliderFractalStrength6.setMaximum(10);
        jSliderFractalStrength7.setMaximum(200);
        jSliderFractalStrength8.setMaximum(200);
        jSliderFractalStrength9.setMaximum(200);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        LoDTessPanel1.setBackground(new java.awt.Color(38, 35, 35));

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(125, 255, 76));
        jLabel45.setText("LoD and Tessellation Settings");

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(125, 255, 76));
        jLabel46.setText("Tessellation Factor");

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(125, 255, 76));
        jLabel47.setText("Tessellation Slope");

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(125, 255, 76));
        jLabel48.setText("Tessellation Shift");

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(125, 255, 76));
        jLabel49.setText("Detail Range");

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(125, 255, 76));
        jLabel50.setText("LoD1 Range");

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(125, 255, 76));
        jLabel51.setText("LoD2 Range");

        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(125, 255, 76));
        jLabel52.setText("LoD3 Range");

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(125, 255, 76));
        jLabel53.setText("LoD4 Range");

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(125, 255, 76));
        jLabel54.setText("LoD5 Range");

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(125, 255, 76));
        jLabel55.setText("LoD6 Range");

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(125, 255, 76));
        jLabel56.setText("LoD7 Range");

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(125, 255, 76));
        jLabel57.setText("LoD8 Range");

        TessellationFactor_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TessellationFactor_Slider1StateChanged(evt);
            }
        });

        TessellationSlope_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TessellationSlope_Slider1StateChanged(evt);
            }
        });

        TessellationShift_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TessellationShift_Slider1StateChanged(evt);
            }
        });

        DetailRange_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                DetailRange_Slider1StateChanged(evt);
            }
        });

        LoD1Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD1Range_Slider1StateChanged(evt);
            }
        });

        LoD2Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD2Range_Slider1StateChanged(evt);
            }
        });

        LoD3Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD3Range_Slider1StateChanged(evt);
            }
        });

        LoD4Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD4Range_Slider1StateChanged(evt);
            }
        });

        LoD5Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD5Range_Slider1StateChanged(evt);
            }
        });

        LoD6Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD6Range_Slider1StateChanged(evt);
            }
        });

        LoD7Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD7Range_Slider1StateChanged(evt);
            }
        });

        LoD8Range_Slider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                LoD8Range_Slider1StateChanged(evt);
            }
        });

        TessellationFactor_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TessellationFactor_Value1.setForeground(new java.awt.Color(255, 255, 255));

        DetailRange_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        DetailRange_Value1.setForeground(new java.awt.Color(255, 255, 255));

        TessellationSlope_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TessellationSlope_Value1.setForeground(new java.awt.Color(255, 255, 255));

        TessellationShift_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TessellationShift_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD1Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD1Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD2Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD2Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD3Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD3Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD4Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD4Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD5Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD5Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD6Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD6Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD7Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD7Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        LoD8Range_Value1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        LoD8Range_Value1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout LoDTessPanel1Layout = new javax.swing.GroupLayout(LoDTessPanel1);
        LoDTessPanel1.setLayout(LoDTessPanel1Layout);
        LoDTessPanel1Layout.setHorizontalGroup(
            LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LoDTessPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(TessellationShift_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TessellationShift_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(DetailRange_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DetailRange_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD1Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD1Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD2Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD2Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD3Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD3Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD4Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD4Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD5Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD5Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD6Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD6Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD7Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD7Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(LoD8Range_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(LoD8Range_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(TessellationFactor_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TessellationFactor_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(TessellationSlope_Slider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TessellationSlope_Value1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel45)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LoDTessPanel1Layout.setVerticalGroup(
            LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel45)
                .addGap(50, 50, 50)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TessellationFactor_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(TessellationFactor_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TessellationSlope_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(TessellationSlope_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TessellationShift_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(TessellationShift_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(DetailRange_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(DetailRange_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LoD1Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD1Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LoD2Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD2Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LoD3Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD3Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LoD4Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD4Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LoD5Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD5Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LoD6Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD6Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LoD7Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD7Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(LoD8Range_Slider1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LoD8Range_Value1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("LoD & Tessellation", LoDTessPanel1);

        FractalsPanel1.setBackground(new java.awt.Color(38, 35, 35));

        jLabel58.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(63, 255, 63));
        jLabel58.setText("Fractals Settings");

        jLabel59.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(125, 255, 76));
        jLabel59.setText("Fractal stage 0");

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(125, 255, 76));
        jLabel60.setText("strength");

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(125, 255, 76));
        jLabel62.setText("Fractal stage 1");

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(125, 255, 76));
        jLabel63.setText("strength");

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(125, 255, 76));
        jLabel65.setText("Fractal stage 2");

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(125, 255, 76));
        jLabel66.setText("strength");

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(125, 255, 76));
        jLabel68.setText("Fractal stage 5");

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(125, 255, 76));
        jLabel69.setText("strength");

        jLabel71.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(125, 255, 76));
        jLabel71.setText("Fractal stage 4");

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(125, 255, 76));
        jLabel72.setText("strength");

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(125, 255, 76));
        jLabel74.setText("strength");

        jLabel76.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(125, 255, 76));
        jLabel76.setText("Fractal stage 3");

        jLabel77.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(125, 255, 76));
        jLabel77.setText("Fractal stage 7");

        jLabel78.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel78.setForeground(new java.awt.Color(125, 255, 76));
        jLabel78.setText("Fractal stage 6");

        jLabel79.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(125, 255, 76));
        jLabel79.setText("strength");

        jLabel80.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel80.setForeground(new java.awt.Color(125, 255, 76));
        jLabel80.setText("strength");

        jLabel83.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel83.setForeground(new java.awt.Color(125, 255, 76));
        jLabel83.setText("Fractal stage 9");

        jLabel84.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel84.setForeground(new java.awt.Color(125, 255, 76));
        jLabel84.setText("Fractal stage 8");

        jLabel85.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(125, 255, 76));
        jLabel85.setText("strength");

        jLabel86.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(125, 255, 76));
        jLabel86.setText("strength");

        jSliderFractalStrength0.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength0StateChanged(evt);
            }
        });

        jSliderFractalStrength1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength1StateChanged(evt);
            }
        });

        jSliderFractalStrength2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength2StateChanged(evt);
            }
        });

        jSliderFractalStrength3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength3StateChanged(evt);
            }
        });

        jSliderFractalStrength4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength4StateChanged(evt);
            }
        });

        jSliderFractalStrength5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength5StateChanged(evt);
            }
        });

        jSliderFractalStrength7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength7StateChanged(evt);
            }
        });

        jSliderFractalStrength6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength6StateChanged(evt);
            }
        });

        jSliderFractalStrength8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength8StateChanged(evt);
            }
        });

        jSliderFractalStrength9.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderFractalStrength9StateChanged(evt);
            }
        });

        jLabelFractalStrength0.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength1.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength2.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength3.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength4.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength5.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength6.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength7.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength8.setForeground(new java.awt.Color(51, 255, 51));

        jLabelFractalStrength9.setForeground(new java.awt.Color(51, 255, 51));

        javax.swing.GroupLayout FractalsPanel1Layout = new javax.swing.GroupLayout(FractalsPanel1);
        FractalsPanel1.setLayout(FractalsPanel1Layout);
        FractalsPanel1Layout.setHorizontalGroup(
            FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addGap(128, 128, 128)
                                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderFractalStrength0, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelFractalStrength0, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel84, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel77, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, FractalsPanel1Layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jSliderFractalStrength6, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                    .addComponent(jSliderFractalStrength7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelFractalStrength6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabelFractalStrength7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSliderFractalStrength5, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSliderFractalStrength8, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSliderFractalStrength9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelFractalStrength5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabelFractalStrength9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabelFractalStrength8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderFractalStrength2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelFractalStrength2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSliderFractalStrength4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(28, 28, 28)
                                    .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSliderFractalStrength3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(28, 28, 28)
                                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSliderFractalStrength1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel58)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFractalStrength1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelFractalStrength3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(194, 194, 194)
                        .addComponent(jLabelFractalStrength4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        FractalsPanel1Layout.setVerticalGroup(
            FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel58)
                .addGap(18, 18, 18)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel60))
                    .addComponent(jSliderFractalStrength0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel63))
                    .addComponent(jSliderFractalStrength1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel66))
                    .addComponent(jSliderFractalStrength2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel74))
                    .addComponent(jSliderFractalStrength3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel72))
                    .addComponent(jSliderFractalStrength4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel69))
                    .addComponent(jSliderFractalStrength5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel79))
                    .addComponent(jSliderFractalStrength6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(11, 11, 11)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel80))
                    .addComponent(jSliderFractalStrength7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel85))
                    .addComponent(jSliderFractalStrength8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel86))
                    .addComponent(jSliderFractalStrength9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelFractalStrength9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(179, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Fractals", FractalsPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>                        

    private void TessellationFactor_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {     
        DB.getTerrainConfiguration().setTessellationFactor(this.TessellationFactor_Slider1.getValue());
        this.TessellationFactor_Value1.setText(String.valueOf(this.TessellationFactor_Slider1.getValue()));
        
    }                                                       

    private void TessellationSlope_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                                       
        DB.getTerrainConfiguration().setTessellationSlope(this.TessellationSlope_Slider1.getValue()/100f);
        this.TessellationSlope_Value1.setText(String.valueOf(this.TessellationSlope_Slider1.getValue()/100f));
        
    }                                                      

    private void TessellationShift_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {    
    	 DB.getTerrainConfiguration().setTessellationShift(this.TessellationShift_Slider1.getValue()/1000f);
        this.TessellationShift_Value1.setText(String.valueOf(this.TessellationShift_Slider1.getValue()/1000f));
        
    }                                                      

    private void DetailRange_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {   
    	DB.getTerrainConfiguration().setDetailRange(this.DetailRange_Slider1.getValue());
        this.DetailRange_Value1.setText(Integer.toString(this.DetailRange_Slider1.getValue()));
    }                                                

    private void LoD1Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {   
    	DB.getTerrainConfiguration().setLod1_range(this.LoD1Range_Slider1.getValue());
        this.LoD1Range_Value1.setText(Integer.toString(this.LoD1Range_Slider1.getValue()));
        
        if (init){
        	
        	if (this.LoD1Range_Slider1.getValue() > 2*(DB.getTerrainConfiguration().getLod_range()[0] - DB.getTerrainConfiguration().getLod_morphing_area()[0])){
        		this.LoD2Range_Slider1.setMaximum(this.LoD1Range_Slider1.getValue() - 
        				2 * (this.LoD1Range_Slider1.getValue() - DB.getTerrainConfiguration().getLod_morphing_area()[0]));
        	}
        }
    }                                              

    private void LoD2Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {  
    	DB.getTerrainConfiguration().setLod2_range(this.LoD2Range_Slider1.getValue());
        this.LoD2Range_Value1.setText(Integer.toString(this.LoD2Range_Slider1.getValue()));
        
        if (init){
        	if(this.LoD2Range_Slider1.getValue() > 0){
        		this.LoD1Range_Slider1.setMinimum(this.LoD2Range_Slider1.getValue() + 
        				2 * (DB.getTerrainConfiguration().getLod_range()[0] - DB.getTerrainConfiguration().getLod_morphing_area()[0]));
        	}
        	if(this.LoD2Range_Slider1.getValue() == 0)
        		this.LoD1Range_Slider1.setMinimum(0);
        	
        	if (this.LoD2Range_Slider1.getValue() > 2*(DB.getTerrainConfiguration().getLod_range()[1] - DB.getTerrainConfiguration().getLod_morphing_area()[1])){
        		this.LoD3Range_Slider1.setMaximum(this.LoD2Range_Slider1.getValue() - 
        				2 * (this.LoD2Range_Slider1.getValue() - DB.getTerrainConfiguration().getLod_morphing_area()[1]));
        	}
        }    
    }                                              

    private void LoD3Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {   
    	DB.getTerrainConfiguration().setLod3_range(this.LoD3Range_Slider1.getValue());
    	this.LoD3Range_Value1.setText(Integer.toString(this.LoD3Range_Slider1.getValue()));
    	
    	if (init){
    		if (this.LoD3Range_Slider1.getValue() > 0){
    			this.LoD2Range_Slider1.setMinimum(this.LoD3Range_Slider1.getValue() + 
    					2 * (DB.getTerrainConfiguration().getLod_range()[1] - DB.getTerrainConfiguration().getLod_morphing_area()[1]));
    		}
    		if(this.LoD3Range_Slider1.getValue() == 0)
        		this.LoD2Range_Slider1.setMinimum(0);
    		
    		if (this.LoD3Range_Slider1.getValue() > 2*(DB.getTerrainConfiguration().getLod_range()[2] - DB.getTerrainConfiguration().getLod_morphing_area()[2])){
    			this.LoD4Range_Slider1.setMaximum(this.LoD3Range_Slider1.getValue() - 
    					2 * (this.LoD3Range_Slider1.getValue() - DB.getTerrainConfiguration().getLod_morphing_area()[2]));
    		}
        }    
    }                                              

    private void LoD4Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {    
    	DB.getTerrainConfiguration().setLod4_range(this.LoD4Range_Slider1.getValue());
    	this.LoD4Range_Value1.setText(Integer.toString(this.LoD4Range_Slider1.getValue()));
    	
    	if (init){
    		if (this.LoD4Range_Slider1.getValue() > 0){
    			this.LoD3Range_Slider1.setMinimum(this.LoD4Range_Slider1.getValue() + 
    					2 * (DB.getTerrainConfiguration().getLod_range()[2] - DB.getTerrainConfiguration().getLod_morphing_area()[2]));
    		}
    		if(this.LoD4Range_Slider1.getValue() == 0)
        		this.LoD3Range_Slider1.setMinimum(0);
    		
    		if (this.LoD4Range_Slider1.getValue() > 2*(DB.getTerrainConfiguration().getLod_range()[3] - DB.getTerrainConfiguration().getLod_morphing_area()[3])){
    			this.LoD5Range_Slider1.setMaximum(this.LoD4Range_Slider1.getValue() - 
    					2 * (this.LoD4Range_Slider1.getValue() - DB.getTerrainConfiguration().getLod_morphing_area()[3]));
    		}
        }    
    }                                              

    private void LoD5Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {   
    	DB.getTerrainConfiguration().setLod5_range(this.LoD5Range_Slider1.getValue());
    	this.LoD5Range_Value1.setText(Integer.toString(this.LoD5Range_Slider1.getValue()));
    	
    	if (init){
    		if (DB.getTerrainConfiguration().getLod_range()[4] > 0){
    			this.LoD4Range_Slider1.setMinimum(this.LoD5Range_Slider1.getValue() + 
        			2 * (DB.getTerrainConfiguration().getLod_range()[3] - DB.getTerrainConfiguration().getLod_morphing_area()[3]));
    		}
    		if(this.LoD5Range_Slider1.getValue() == 0)
        		this.LoD4Range_Slider1.setMinimum(0);
    		
    		if (this.LoD5Range_Slider1.getValue() > 2*(DB.getTerrainConfiguration().getLod_range()[4] - DB.getTerrainConfiguration().getLod_morphing_area()[4])){
        	this.LoD6Range_Slider1.setMaximum(this.LoD5Range_Slider1.getValue() - 
    			2 * (this.LoD5Range_Slider1.getValue() - DB.getTerrainConfiguration().getLod_morphing_area()[4]));
    		}
        } 
    }                                              

    private void LoD6Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {  
    	DB.getTerrainConfiguration().setLod6_range(this.LoD6Range_Slider1.getValue());
    	this.LoD6Range_Value1.setText(Integer.toString(this.LoD6Range_Slider1.getValue()));
    	
    	if (init){
    		if (DB.getTerrainConfiguration().getLod_range()[5] > 0){
    			this.LoD5Range_Slider1.setMinimum(this.LoD6Range_Slider1.getValue() + 
        			2 * (DB.getTerrainConfiguration().getLod_range()[4] - DB.getTerrainConfiguration().getLod_morphing_area()[4]));
    		}
    		if(this.LoD6Range_Slider1.getValue() == 0)
        		this.LoD5Range_Slider1.setMinimum(0);
    		
    		if (this.LoD6Range_Slider1.getValue() > 2*(DB.getTerrainConfiguration().getLod_range()[5] - DB.getTerrainConfiguration().getLod_morphing_area()[5])){
        	this.LoD7Range_Slider1.setMaximum(this.LoD6Range_Slider1.getValue() - 
    			2 * (this.LoD6Range_Slider1.getValue() - DB.getTerrainConfiguration().getLod_morphing_area()[5]));
    		}
        } 
    }                                              

    private void LoD7Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) { 

    	DB.getTerrainConfiguration().setLod7_range(this.LoD7Range_Slider1.getValue());
    	this.LoD7Range_Value1.setText(Integer.toString(this.LoD7Range_Slider1.getValue()));
    	
    	if (init){
    		if (DB.getTerrainConfiguration().getLod_range()[6] > 0){
    			this.LoD6Range_Slider1.setMinimum(this.LoD7Range_Slider1.getValue() + 
        			2 * (DB.getTerrainConfiguration().getLod_range()[5] - DB.getTerrainConfiguration().getLod_morphing_area()[5]));
    		}
    		if(this.LoD7Range_Slider1.getValue() == 0)
        		this.LoD6Range_Slider1.setMinimum(0);
    		
    		if (this.LoD7Range_Slider1.getValue() > 2*(DB.getTerrainConfiguration().getLod_range()[6] - DB.getTerrainConfiguration().getLod_morphing_area()[6])){
        	this.LoD8Range_Slider1.setMaximum(this.LoD7Range_Slider1.getValue() - 
    			2 * (this.LoD7Range_Slider1.getValue() - DB.getTerrainConfiguration().getLod_morphing_area()[6]));
    		}
    	}
    }                                              

    private void LoD8Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {  
    	DB.getTerrainConfiguration().setLod8_range(this.LoD8Range_Slider1.getValue());
    	this.LoD8Range_Value1.setText(Integer.toString(this.LoD8Range_Slider1.getValue()));
    	
    	if (init){
    		if (DB.getTerrainConfiguration().getLod_range()[7] > 0){
    		this.LoD7Range_Slider1.setMinimum(this.LoD8Range_Slider1.getValue() + 
        			2 * (DB.getTerrainConfiguration().getLod_range()[6] - DB.getTerrainConfiguration().getLod_morphing_area()[6]));
    		}
    		if(this.LoD8Range_Slider1.getValue() == 0)
        		this.LoD7Range_Slider1.setMinimum(0);
    	}
    }                                              

    private void jSliderFractalStrength0StateChanged(javax.swing.event.ChangeEvent evt) {  
    	jLabelFractalStrength0.setText(Float.toString(jSliderFractalStrength0.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(0).setStrength(jSliderFractalStrength0.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength1StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength1.setText(Float.toString(jSliderFractalStrength1.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(1).setStrength(jSliderFractalStrength1.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength2StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength2.setText(Float.toString(jSliderFractalStrength2.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(2).setStrength(jSliderFractalStrength2.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength3StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength3.setText(Float.toString(jSliderFractalStrength3.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(3).setStrength(jSliderFractalStrength3.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength4StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength4.setText(Float.toString(jSliderFractalStrength4.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(4).setStrength(jSliderFractalStrength4.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength5StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength5.setText(Float.toString(jSliderFractalStrength5.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(5).setStrength(jSliderFractalStrength5.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength6StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength6.setText(Float.toString(jSliderFractalStrength6.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(6).setStrength(jSliderFractalStrength6.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength7StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength7.setText(Float.toString(jSliderFractalStrength7.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(7).setStrength(jSliderFractalStrength7.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength8StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength8.setText(Float.toString(jSliderFractalStrength8.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(8).setStrength(jSliderFractalStrength8.getValue()/100f);
    }                                                    

    private void jSliderFractalStrength9StateChanged(javax.swing.event.ChangeEvent evt) {                                                     
    	jLabelFractalStrength9.setText(Float.toString(jSliderFractalStrength9.getValue()/100f));
    	DB.getTerrainConfiguration().getFractals().get(9).setStrength(jSliderFractalStrength9.getValue()/100f);
    }               
    
    public void update(javax.swing.JPanel LoDChartPanel, javax.swing.JPanel tessellationFunctionPanel, javax.swing.JLabel dataVolumeValue){
    	
    	this.TessellationFactor_Slider1.setValue(DB.getTerrainConfiguration().getTessellationFactor());
    	this.TessellationFactor_Value1.setText(Integer.toString(TessellationFactor_Slider1.getValue()));
    	
    	this.TessellationShift_Slider1.setValue((int) (DB.getTerrainConfiguration().getTessellationShift()*1000f));
    	this.TessellationShift_Value1.setText(String.valueOf(this.TessellationShift_Slider1.getValue()/1000f));
    	
    	this.TessellationSlope_Slider1.setValue((int) (DB.getTerrainConfiguration().getTessellationSlope()*100f)); 
    	this.TessellationSlope_Value1.setText(String.valueOf(this.TessellationSlope_Slider1.getValue()/100f));
    	
    	this.DetailRange_Slider1.setValue(DB.getTerrainConfiguration().getDetailRange());
    	this.DetailRange_Value1.setText(Integer.toString(DetailRange_Slider1.getValue()));
    	
    	this.jLabelFractalStrength0.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(0).getStrength()));
    	this.jSliderFractalStrength0.setValue((int) (DB.getTerrainConfiguration().getFractals().get(0).getStrength()*100f));
    	this.jLabelFractalStrength1.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(1).getStrength()));
    	this.jSliderFractalStrength1.setValue((int) (DB.getTerrainConfiguration().getFractals().get(1).getStrength()*100f));
    	this.jLabelFractalStrength2.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(2).getStrength()));
    	this.jSliderFractalStrength2.setValue((int) (DB.getTerrainConfiguration().getFractals().get(2).getStrength()*100f));
    	this.jLabelFractalStrength3.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(3).getStrength()));
    	this.jSliderFractalStrength3.setValue((int) (DB.getTerrainConfiguration().getFractals().get(3).getStrength()*100f));
    	this.jLabelFractalStrength4.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(4).getStrength()));
    	this.jSliderFractalStrength4.setValue((int) (DB.getTerrainConfiguration().getFractals().get(4).getStrength()*100f));
    	this.jLabelFractalStrength5.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(5).getStrength()));
    	this.jSliderFractalStrength5.setValue((int) (DB.getTerrainConfiguration().getFractals().get(5).getStrength()*100f));
    	this.jLabelFractalStrength6.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(6).getStrength()));
    	this.jSliderFractalStrength6.setValue((int) (DB.getTerrainConfiguration().getFractals().get(6).getStrength()*100f));
    	this.jLabelFractalStrength7.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(7).getStrength()));
    	this.jSliderFractalStrength7.setValue((int) (DB.getTerrainConfiguration().getFractals().get(7).getStrength()*100f));
    	this.jLabelFractalStrength8.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(8).getStrength()));
    	this.jSliderFractalStrength8.setValue((int) (DB.getTerrainConfiguration().getFractals().get(8).getStrength()*100f));
    	this.jLabelFractalStrength9.setText(Float.toString(DB.getTerrainConfiguration().getFractals().get(9).getStrength()));
    	this.jSliderFractalStrength9.setValue((int) (DB.getTerrainConfiguration().getFractals().get(9).getStrength()*100f));
    	
    	this.LoD1Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[0]);
    	this.LoD1Range_Value1.setText(Integer.toString(LoD1Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[1] != 0){
    		this.LoD1Range_Slider1.setMinimum(DB.getTerrainConfiguration().getLod_range()[1] + 
    			2 * (DB.getTerrainConfiguration().getLod_range()[0] - DB.getTerrainConfiguration().getLod_morphing_area()[0]));
    	}
    	
    	this.LoD2Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[1]);
    	this.LoD2Range_Value1.setText(Integer.toString(LoD2Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[0] != 0){
    		this.LoD2Range_Slider1.setMaximum(DB.getTerrainConfiguration().getLod_range()[0] - 
    			2 * (DB.getTerrainConfiguration().getLod_range()[0] - DB.getTerrainConfiguration().getLod_morphing_area()[0]));
    	}
    	else this.LoD2Range_Slider1.setMaximum(0);
    	if (DB.getTerrainConfiguration().getLod_range()[2] != 0){
    		this.LoD2Range_Slider1.setMinimum(DB.getTerrainConfiguration().getLod_range()[2] + 
    			2 * (DB.getTerrainConfiguration().getLod_range()[1] - DB.getTerrainConfiguration().getLod_morphing_area()[1]));
    	}
    	
    	this.LoD3Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[2]);
    	this.LoD3Range_Value1.setText(Integer.toString(LoD3Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[1] != 0){
    		this.LoD3Range_Slider1.setMaximum(DB.getTerrainConfiguration().getLod_range()[1] - 
    			2 * (DB.getTerrainConfiguration().getLod_range()[1] - DB.getTerrainConfiguration().getLod_morphing_area()[1]));
    	}
    	else this.LoD3Range_Slider1.setMaximum(0);
    	if (DB.getTerrainConfiguration().getLod_range()[3] != 0){
    		this.LoD3Range_Slider1.setMinimum(DB.getTerrainConfiguration().getLod_range()[3] + 
    			2 * (DB.getTerrainConfiguration().getLod_range()[2] - DB.getTerrainConfiguration().getLod_morphing_area()[2]));
    	}
    	
    	this.LoD4Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[3]);
    	this.LoD4Range_Value1.setText(Integer.toString(LoD4Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[2] != 0){
    		this.LoD4Range_Slider1.setMaximum(DB.getTerrainConfiguration().getLod_range()[2] - 
    			2 * (DB.getTerrainConfiguration().getLod_range()[2] - DB.getTerrainConfiguration().getLod_morphing_area()[2]));
    	}
    	else this.LoD4Range_Slider1.setMaximum(0);
    	if (DB.getTerrainConfiguration().getLod_range()[4] != 0){
    		this.LoD4Range_Slider1.setMinimum(DB.getTerrainConfiguration().getLod_range()[4] + 
    			2 * (DB.getTerrainConfiguration().getLod_range()[3] - DB.getTerrainConfiguration().getLod_morphing_area()[3]));
    	}
    	
    	this.LoD5Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[4]);
    	this.LoD5Range_Value1.setText(Integer.toString(LoD5Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[3] != 0){
    		this.LoD5Range_Slider1.setMaximum(DB.getTerrainConfiguration().getLod_range()[3] - 
    			2 * (DB.getTerrainConfiguration().getLod_range()[3] - DB.getTerrainConfiguration().getLod_morphing_area()[3]));
    	}
    	else this.LoD5Range_Slider1.setMaximum(0);
    	if (DB.getTerrainConfiguration().getLod_range()[5] != 0){
    		this.LoD5Range_Slider1.setMinimum(DB.getTerrainConfiguration().getLod_range()[5] + 
    			2 * (DB.getTerrainConfiguration().getLod_range()[4] - DB.getTerrainConfiguration().getLod_morphing_area()[4]));
    	}
    	
    	this.LoD6Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[5]);
    	this.LoD6Range_Value1.setText(Integer.toString(LoD6Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[4] != 0){
    		this.LoD6Range_Slider1.setMaximum(DB.getTerrainConfiguration().getLod_range()[4] - 
    			2 * (DB.getTerrainConfiguration().getLod_range()[4] - DB.getTerrainConfiguration().getLod_morphing_area()[4]));
    	}
    	else this.LoD6Range_Slider1.setMaximum(0);
    	if (DB.getTerrainConfiguration().getLod_range()[6] != 0){
    		this.LoD6Range_Slider1.setMinimum(DB.getTerrainConfiguration().getLod_range()[6] + 
    			2 * (DB.getTerrainConfiguration().getLod_range()[5] - DB.getTerrainConfiguration().getLod_morphing_area()[5]));
    	}
    	
    	this.LoD7Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[6]);
    	this.LoD7Range_Value1.setText(Integer.toString(LoD7Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[5] != 0){
    		this.LoD7Range_Slider1.setMaximum(DB.getTerrainConfiguration().getLod_range()[5] - 
    			2 * (DB.getTerrainConfiguration().getLod_range()[5] - DB.getTerrainConfiguration().getLod_morphing_area()[5]));
    	}
    	else this.LoD7Range_Slider1.setMaximum(0);
    	if (DB.getTerrainConfiguration().getLod_range()[7] != 0){
    		this.LoD7Range_Slider1.setMinimum(DB.getTerrainConfiguration().getLod_range()[7] + 
    			2 * (DB.getTerrainConfiguration().getLod_range()[6] - DB.getTerrainConfiguration().getLod_morphing_area()[6]));
    	}
    	
    	this.LoD8Range_Slider1.setValue(DB.getTerrainConfiguration().getLod_range()[7]);
    	this.LoD8Range_Value1.setText(Integer.toString(LoD8Range_Slider1.getValue()));
    	if (DB.getTerrainConfiguration().getLod_range()[6] != 0){
    		this.LoD8Range_Slider1.setMaximum(DB.getTerrainConfiguration().getLod_range()[6] - 
    			2 * (DB.getTerrainConfiguration().getLod_range()[6] - DB.getTerrainConfiguration().getLod_morphing_area()[6]));
    	}
    	else this.LoD8Range_Slider1.setMaximum(0);
    	
    	TerrainEditorInterface.init = true;
    }

	// Variables declaration - do not modify                     
    private javax.swing.JSlider DetailRange_Slider1;
    private javax.swing.JLabel DetailRange_Value1;
    private javax.swing.JPanel FractalsPanel1;
    private javax.swing.JSlider LoD1Range_Slider1;
    private javax.swing.JLabel LoD1Range_Value1;
    private javax.swing.JSlider LoD2Range_Slider1;
    private javax.swing.JLabel LoD2Range_Value1;
    private javax.swing.JSlider LoD3Range_Slider1;
    private javax.swing.JLabel LoD3Range_Value1;
    private javax.swing.JSlider LoD4Range_Slider1;
    private javax.swing.JLabel LoD4Range_Value1;
    private javax.swing.JSlider LoD5Range_Slider1;
    private javax.swing.JLabel LoD5Range_Value1;
    private javax.swing.JSlider LoD6Range_Slider1;
    private javax.swing.JLabel LoD6Range_Value1;
    private javax.swing.JSlider LoD7Range_Slider1;
    private javax.swing.JLabel LoD7Range_Value1;
    private javax.swing.JSlider LoD8Range_Slider1;
    private javax.swing.JLabel LoD8Range_Value1;
    private javax.swing.JPanel LoDTessPanel1;
    private javax.swing.JSlider TessellationFactor_Slider1;
    private javax.swing.JLabel TessellationFactor_Value1;
    private javax.swing.JSlider TessellationShift_Slider1;
    private javax.swing.JLabel TessellationShift_Value1;
    private javax.swing.JSlider TessellationSlope_Slider1;
    private javax.swing.JLabel TessellationSlope_Value1;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabelFractalStrength0;
    private javax.swing.JLabel jLabelFractalStrength1;
    private javax.swing.JLabel jLabelFractalStrength2;
    private javax.swing.JLabel jLabelFractalStrength3;
    private javax.swing.JLabel jLabelFractalStrength4;
    private javax.swing.JLabel jLabelFractalStrength5;
    private javax.swing.JLabel jLabelFractalStrength6;
    private javax.swing.JLabel jLabelFractalStrength7;
    private javax.swing.JLabel jLabelFractalStrength8;
    private javax.swing.JLabel jLabelFractalStrength9;
    private javax.swing.JSlider jSliderFractalStrength0;
    private javax.swing.JSlider jSliderFractalStrength1;
    private javax.swing.JSlider jSliderFractalStrength2;
    private javax.swing.JSlider jSliderFractalStrength3;
    private javax.swing.JSlider jSliderFractalStrength4;
    private javax.swing.JSlider jSliderFractalStrength5;
    private javax.swing.JSlider jSliderFractalStrength6;
    private javax.swing.JSlider jSliderFractalStrength7;
    private javax.swing.JSlider jSliderFractalStrength8;
    private javax.swing.JSlider jSliderFractalStrength9;
    private javax.swing.JTabbedPane jTabbedPane1;

}
