package cdk.tools.terrainEditor;

import javax.swing.JFrame;

public class TerrainEditorInterface extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
        FractalStage0_Strength_Textfield1 = new javax.swing.JTextField();
        FractalStage0_TileFactor_Textfield1 = new javax.swing.JTextField();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        FractalStage1_Strength_Textfield1 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        FractalStage1_TileFactor_Textfield1 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        FractalStage2_Strength_Textfield1 = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        FractalStage2_TileFactor_Textfield1 = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        FractalStage5_Strength_Textfield1 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        FractalStage5_TileFactor_Textfield1 = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        FractalStage4_Strength_Textfield1 = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        FractalStage4_TileFactor_Textfield1 = new javax.swing.JTextField();
        FractalStage3_TileFactor_Textfield1 = new javax.swing.JTextField();
        FractalStage3_Strength_Textfield1 = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        FractalStage7_Strength_Textfield1 = new javax.swing.JTextField();
        FractalStage6_Strength_Textfield1 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        FractalStage6_TileFactor_Textfield1 = new javax.swing.JTextField();
        FractalStage7_TileFactor_Textfield1 = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        FractalStage9_Strength_Textfield1 = new javax.swing.JTextField();
        FractalStage8_Strength_Textfield1 = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        FractalStage8_TileFactor_Textfield1 = new javax.swing.JTextField();
        FractalStage9_TileFactor_Textfield1 = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();

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
                .addGroup(LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel45)
                        .addGap(48, 48, 48))
                    .addGroup(LoDTessPanel1Layout.createSequentialGroup()
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
                        .addContainerGap())))
        );
        LoDTessPanel1Layout.setVerticalGroup(
            LoDTessPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoDTessPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel45)
                .addGap(51, 51, 51)
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

        FractalStage0_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage0_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel60.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(125, 255, 76));
        jLabel60.setText("strength");

        jLabel61.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(125, 255, 76));
        jLabel61.setText("tile factor");

        jLabel62.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(125, 255, 76));
        jLabel62.setText("Fractal stage 1");

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(125, 255, 76));
        jLabel63.setText("strength");

        FractalStage1_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage1_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(125, 255, 76));
        jLabel64.setText("tile factor");

        jLabel65.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(125, 255, 76));
        jLabel65.setText("Fractal stage 2");

        jLabel66.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(125, 255, 76));
        jLabel66.setText("strength");

        FractalStage2_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage2_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(125, 255, 76));
        jLabel67.setText("tile factor");

        FractalStage2_TileFactor_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage2_TileFactor_Textfield1ActionPerformed(evt);
            }
        });

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(125, 255, 76));
        jLabel68.setText("Fractal stage 5");

        jLabel69.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(125, 255, 76));
        jLabel69.setText("strength");

        FractalStage5_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage5_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel70.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(125, 255, 76));
        jLabel70.setText("tile factor");

        FractalStage5_TileFactor_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage5_TileFactor_Textfield1ActionPerformed(evt);
            }
        });

        jLabel71.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(125, 255, 76));
        jLabel71.setText("Fractal stage 4");

        jLabel72.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(125, 255, 76));
        jLabel72.setText("strength");

        FractalStage4_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage4_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel73.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(125, 255, 76));
        jLabel73.setText("tile factor");

        FractalStage3_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage3_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel74.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(125, 255, 76));
        jLabel74.setText("strength");

        jLabel75.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(125, 255, 76));
        jLabel75.setText("tile factor");

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

        FractalStage7_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage7_Strength_Textfield1ActionPerformed(evt);
            }
        });

        FractalStage6_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage6_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel81.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(125, 255, 76));
        jLabel81.setText("tile factor");

        FractalStage7_TileFactor_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage7_TileFactor_Textfield1ActionPerformed(evt);
            }
        });

        jLabel82.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel82.setForeground(new java.awt.Color(125, 255, 76));
        jLabel82.setText("tile factor");

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

        FractalStage9_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage9_Strength_Textfield1ActionPerformed(evt);
            }
        });

        FractalStage8_Strength_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage8_Strength_Textfield1ActionPerformed(evt);
            }
        });

        jLabel87.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel87.setForeground(new java.awt.Color(125, 255, 76));
        jLabel87.setText("tile factor");

        FractalStage9_TileFactor_Textfield1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FractalStage9_TileFactor_Textfield1ActionPerformed(evt);
            }
        });

        jLabel88.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(125, 255, 76));
        jLabel88.setText("tile factor");

        javax.swing.GroupLayout FractalsPanel1Layout = new javax.swing.GroupLayout(FractalsPanel1);
        FractalsPanel1.setLayout(FractalsPanel1Layout);
        FractalsPanel1Layout.setHorizontalGroup(
            FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage3_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage3_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage1_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage1_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel83, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(jLabel84, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel77, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel78, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                                    .addGap(28, 28, 28)
                                                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(FractalStage5_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(FractalStage5_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FractalsPanel1Layout.createSequentialGroup()
                                                    .addGap(20, 20, 20)
                                                    .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(FractalStage6_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(FractalStage6_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FractalsPanel1Layout.createSequentialGroup()
                                                .addGap(28, 28, 28)
                                                .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(FractalStage7_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(FractalStage7_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FractalsPanel1Layout.createSequentialGroup()
                                            .addGap(28, 28, 28)
                                            .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(FractalStage8_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(FractalStage8_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FractalsPanel1Layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(FractalStage9_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(FractalStage9_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(FractalsPanel1Layout.createSequentialGroup()
                        .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(jLabel58))
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage2_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage2_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage4_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage4_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                                .addGap(128, 128, 128)
                                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FractalStage0_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(FractalStage0_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)))
                        .addContainerGap(42, Short.MAX_VALUE))))
        );
        FractalsPanel1Layout.setVerticalGroup(
            FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FractalsPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage0_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage0_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60)
                    .addComponent(jLabel61))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage1_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage1_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63)
                    .addComponent(jLabel64))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage2_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage2_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel66)
                    .addComponent(jLabel67))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage3_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage3_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel74)
                    .addComponent(jLabel75))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage4_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage4_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel72)
                    .addComponent(jLabel73))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage5_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage5_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel69)
                    .addComponent(jLabel70))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage6_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage6_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79)
                    .addComponent(jLabel81))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage7_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage7_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80)
                    .addComponent(jLabel82))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage8_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage8_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel85)
                    .addComponent(jLabel87))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(FractalsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage9_Strength_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FractalStage9_TileFactor_Textfield1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86)
                    .addComponent(jLabel88))
                .addContainerGap(199, Short.MAX_VALUE))
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
        // TODO add your handling code here:
    }                                                       

    private void TessellationSlope_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                                       
        // TODO add your handling code here:
    }                                                      

    private void TessellationShift_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                                       
        // TODO add your handling code here:
    }                                                      

    private void DetailRange_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                                 
        // TODO add your handling code here:
    }                                                

    private void LoD1Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void LoD2Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void LoD3Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void LoD4Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void LoD5Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void LoD6Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void LoD7Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void LoD8Range_Slider1StateChanged(javax.swing.event.ChangeEvent evt) {                                               
        // TODO add your handling code here:
    }                                              

    private void FractalStage0_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage1_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage2_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage2_TileFactor_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                    
        // TODO add your handling code here:
    }                                                                   

    private void FractalStage5_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage5_TileFactor_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                    
        // TODO add your handling code here:
    }                                                                   

    private void FractalStage4_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage3_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage7_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage6_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage7_TileFactor_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                    
        // TODO add your handling code here:
    }                                                                   

    private void FractalStage9_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage8_Strength_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                  
        // TODO add your handling code here:
    }                                                                 

    private void FractalStage9_TileFactor_Textfield1ActionPerformed(java.awt.event.ActionEvent evt) {                                                                    
        // TODO add your handling code here:
    }                                                                   

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
            java.util.logging.Logger.getLogger(TerrainEditorInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TerrainEditorInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TerrainEditorInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TerrainEditorInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TerrainEditorInterface().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JSlider DetailRange_Slider1;
    private javax.swing.JLabel DetailRange_Value1;
    private javax.swing.JTextField FractalStage0_Strength_Textfield1;
    private javax.swing.JTextField FractalStage0_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage1_Strength_Textfield1;
    private javax.swing.JTextField FractalStage1_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage2_Strength_Textfield1;
    private javax.swing.JTextField FractalStage2_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage3_Strength_Textfield1;
    private javax.swing.JTextField FractalStage3_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage4_Strength_Textfield1;
    private javax.swing.JTextField FractalStage4_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage5_Strength_Textfield1;
    private javax.swing.JTextField FractalStage5_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage6_Strength_Textfield1;
    private javax.swing.JTextField FractalStage6_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage7_Strength_Textfield1;
    private javax.swing.JTextField FractalStage7_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage8_Strength_Textfield1;
    private javax.swing.JTextField FractalStage8_TileFactor_Textfield1;
    private javax.swing.JTextField FractalStage9_Strength_Textfield1;
    private javax.swing.JTextField FractalStage9_TileFactor_Textfield1;
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
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration   

}
