package audio.visualizer;

import audio.visualizer.model.*;
import audio.visualizer.graphics.*;
import java.util.logging.*;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Steven
 */
public class GUI extends JFrame {

    private static final Logger logger = Logger.getLogger(GUI.class.getName());
    private final Timer progressTimer; // Timer Declaration for use in time management regarding seekBar and labelDuration
    private Player player; // Player Declaration for use in button controls
    private Library library = new Library();
    private DefaultListModel<File> customModel = new DefaultListModel<>(); // JList 
    // DELETE  private File selectedFile;
    private Visualizer visual;
    private Color barColor; // colour picked in the Type tab; null = default green-to-red
    private int bandCount = 32; // number of bars/bands in the visualizer
    private FFT fftData;

    /* === GUI Constructor Start === */
    public GUI() {
        initComponents();

        /* === Default Playlist Handler === */
        playlistDefault.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String fileName = playlistDefault.getSelectedValue();
                if (fileName != null) {
                    playlistCustom.clearSelection();
                    library.select(fileName);
                }
            }
        });
        playlistDefault.setCellRenderer(new DefaultListCellRenderer());

        /* === Custom Playlist Handler === */
        playlistCustom.setModel(customModel);
        playlistCustom.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof File f) {
                    setText(f.getName());
                }
                return this;
            }
        });
        playlistCustom.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                File f = playlistCustom.getSelectedValue();
                if (f != null) {
                    playlistDefault.clearSelection();
                    library.select(f.getName());
                }
            }
        });

        blueBackgroundPane.setLayout(new BorderLayout());

        // Timer that checks the player every 100ms and updates the slider while it's not actively being dragged.
        progressTimer = new Timer(100, e -> {
            if (player != null && !seekBar.getValueIsAdjusting()) {
                updateTimeDisplay(); // newly added function to sync seek when paused.
            }
        });
        /*=== Seek-On-Click Handler ===*/
        seekBar.addMouseListener(
                new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e
            ) {
                if (player == null) {
                    return;
                }
                /* Uses a fraction to simulate physical distance on the slider 
                ** component, this allows us the a mouse click to update the 
                ** seekbar and the player accordingly. */
                double fraction = e.getX() / (double) seekBar.getWidth();
                int value = Math.min(seekBar.getMaximum(), Math.max(0, (int) (fraction * seekBar.getMaximum())));
                player.seekTo(value);
                updateTimeDisplay(); // newly added function to sync seek when paused. 
            }
        });

        /*=== Visual Style Selection Handler ===*/
        visualStyleList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = visualStyleList.getSelectedIndex();
                if (index < 0) {
                    return;
                }
                showVisualizer(index);
                playButtonActionPerformed(null);
            }
        });
        /*=== Compact Color Picker ===*/
        // @author Carson
        // The standard chooser tabs (swatches, HSV, RGB...) are much wider than this side
        // panel, so half of them was cut off and could not be reached. Swap them for one
        // compact panel that fits: a colour palette, "Custom..." (the full colour dialog)
        // and "Reset to Default". See CompactColorPanel.java.

        jColorChooser1.setChooserPanels(new javax.swing.colorchooser.AbstractColorChooserPanel[]{
            new CompactColorPanel(this::applyBarColor, this::resetBarColor)
        });
        jColorChooser1.setPreviewPanel(new JPanel()); // hide the sample-text preview
        if (jColorChooser1.getPreviewPanel().getParent() instanceof JComponent) {
            // The chooser wraps the preview in a box titled "Preview"; clear that title.
            ((JComponent) jColorChooser1.getPreviewPanel().getParent()).setBorder(BorderFactory.createEmptyBorder());
        }
        /*=== Bar Count Adjuster Handler ===*/
        // Even steps keep Mirrored and Radial symmetrical (they split bars into two halves).
        barAdjuster.setModel(new SpinnerNumberModel(32, 4, 100, 1));
        ((JSpinner.DefaultEditor) barAdjuster.getEditor()).getTextField().setFocusLostBehavior(JFormattedTextField.REVERT);
        
        barAdjuster.addChangeListener(e -> {
            bandCount = (Integer) barAdjuster.getValue();
            
            if (fftData != null) {
                fftData.updateBands(bandCount);
            }
            
            if (visual != null) {
                boolean wasRunning = visual.isRunning();
                showVisualizer(visualStyleList.getSelectedIndex());
                if (wasRunning) {
                    visual.start();
                }
            }
        });
        // Handles populating Custom Library between start ups as a form of persistence.
        refreshLibraryList();
    }

    /*=== GUI Constructor END ===*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TabbedPane = new javax.swing.JTabbedPane();
        panelTab_Files = new javax.swing.JPanel();
        scrollCustom = new javax.swing.JScrollPane();
        playlistCustom = new javax.swing.JList<>();
        scrollReference = new javax.swing.JScrollPane();
        playlistDefault = new javax.swing.JList<>();
        labelCustom = new javax.swing.JLabel();
        labelReference = new javax.swing.JLabel();
        importFileButton = new javax.swing.JButton();
        deleteFileButton = new JButton();
        customLibrarySeperator = new javax.swing.JSeparator();
        panelTab_Type = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        visualStyleList = new javax.swing.JList<>();
        jColorChooser1 = new javax.swing.JColorChooser();
        labelVisualizerStyle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        labelBarAdjuster = new javax.swing.JLabel();
        panelTab_Configs = new javax.swing.JPanel();
        panelMedia_Controls = new javax.swing.JPanel();
        labelCurrentDuration = new javax.swing.JLabel();
        labelMaxDuration = new javax.swing.JLabel();
        seekBar = new javax.swing.JSlider();
        skipBackButton = new javax.swing.JButton();
        skipForwardButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        stopButtonAction = new javax.swing.JButton();
        blueBackgroundPane = new javax.swing.JDesktopPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TabbedPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        TabbedPane.setFont(new java.awt.Font("SansSerif", 2, 14)); // NOI18N
        TabbedPane.setPreferredSize(new java.awt.Dimension(270, 446));

        panelTab_Files.setMinimumSize(new java.awt.Dimension(1, 1));

        playlistCustom.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        playlistCustom.setFont(new java.awt.Font("SansSerif", 2, 16)); // NOI18N
        scrollCustom.setViewportView(playlistCustom);

        playlistDefault.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        playlistDefault.setFont(new java.awt.Font("SansSerif", 2, 16)); // NOI18N
        playlistDefault.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "default_1.wav", "default_2.wav", "default_3.wav", "default_4.wav", "default_5.wav" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        playlistDefault.setMaximumSize(new java.awt.Dimension(0, 0));
        playlistDefault.setMinimumSize(new java.awt.Dimension(0, 0));
        playlistDefault.setPreferredSize(new java.awt.Dimension(0, 0));
        scrollReference.setViewportView(playlistDefault);
        playlistDefault.getAccessibleContext().setAccessibleDescription("");

        labelCustom.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        labelCustom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelCustom.setText("Custom Library");
        labelCustom.setDoubleBuffered(true);

        labelReference.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        labelReference.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelReference.setText("Default Library");

        importFileButton.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        importFileButton.setText("<html><center>Import<br> (.wav)</center></html>");
        importFileButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        importFileButton.setBorderPainted(false);
        importFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        importFileButton.setMinimumSize(new java.awt.Dimension(45, 38));
        importFileButton.setPreferredSize(new java.awt.Dimension(55, 45));
        importFileButton.addActionListener(this::importFileButtonActionPerformed);

        deleteFileButton.setFont(new java.awt.Font("SansSerif", 3, 12)); // NOI18N
        deleteFileButton.setText("<html><center>Delete<br>File</center></html>");
        deleteFileButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        deleteFileButton.setBorderPainted(false);
        deleteFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        deleteFileButton.setMinimumSize(new java.awt.Dimension(45, 38));
        deleteFileButton.setPreferredSize(new java.awt.Dimension(55, 45));
        deleteFileButton.addActionListener(this::deleteFileButtonActionPerformed);

        customLibrarySeperator.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        javax.swing.GroupLayout panelTab_FilesLayout = new javax.swing.GroupLayout(panelTab_Files);
        panelTab_Files.setLayout(panelTab_FilesLayout);
        panelTab_FilesLayout.setHorizontalGroup(
            panelTab_FilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollReference)
            .addComponent(customLibrarySeperator, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(panelTab_FilesLayout.createSequentialGroup()
                .addGroup(panelTab_FilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTab_FilesLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(importFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(deleteFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTab_FilesLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(labelReference))
                    .addGroup(panelTab_FilesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelCustom)))
                .addContainerGap(26, Short.MAX_VALUE))
            .addComponent(scrollCustom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        panelTab_FilesLayout.setVerticalGroup(
            panelTab_FilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTab_FilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelCustom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollCustom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTab_FilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customLibrarySeperator, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelReference, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollReference, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        TabbedPane.addTab("Files", panelTab_Files);

        visualStyleList.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        visualStyleList.setFont(new java.awt.Font("SansSerif", 0, 15)); // NOI18N
        visualStyleList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Horizontal Bars", "Mirrored Bars", "Radial Bars", "Vertical Bars" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        visualStyleList.setSelectedIndex(3); // GUI starts with Vertical Visualizer prepared
        jScrollPane1.setViewportView(visualStyleList);

        labelVisualizerStyle.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        labelVisualizerStyle.setText("Visualizer Style");

        jLabel1.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        jLabel1.setText("Visualizer Color");

        barAdjuster.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        barAdjuster.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        labelBarAdjuster.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        labelBarAdjuster.setText("Bar Adjuster");

        javax.swing.GroupLayout panelTab_TypeLayout = new javax.swing.GroupLayout(panelTab_Type);
        panelTab_Type.setLayout(panelTab_TypeLayout);
        panelTab_TypeLayout.setHorizontalGroup(
            panelTab_TypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTab_TypeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(barAdjuster, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTab_TypeLayout.createSequentialGroup()
                .addGroup(panelTab_TypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(panelTab_TypeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelTab_TypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelBarAdjuster, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                            .addComponent(labelVisualizerStyle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jColorChooser1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelTab_TypeLayout.setVerticalGroup(
            panelTab_TypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTab_TypeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelVisualizerStyle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelBarAdjuster)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barAdjuster, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TabbedPane.addTab("Type", panelTab_Type);

        javax.swing.GroupLayout panelTab_ConfigsLayout = new javax.swing.GroupLayout(panelTab_Configs);
        panelTab_Configs.setLayout(panelTab_ConfigsLayout);
        panelTab_ConfigsLayout.setHorizontalGroup(
            panelTab_ConfigsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTab_ConfigsLayout.setVerticalGroup(
            panelTab_ConfigsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        TabbedPane.addTab("Configs", panelTab_Configs);

        panelMedia_Controls.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        labelCurrentDuration.setFont(new java.awt.Font("Liberation Mono", 2, 24)); // NOI18N
        labelCurrentDuration.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCurrentDuration.setText("--:--");

        labelMaxDuration.setFont(new java.awt.Font("Liberation Mono", 2, 24)); // NOI18N
        labelMaxDuration.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelMaxDuration.setText("--:--");
        labelMaxDuration.setToolTipText("");

        seekBar.setPaintLabels(true);
        seekBar.setPaintTicks(true);

        skipBackButton.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        skipBackButton.setText("⏪");
        skipBackButton.setActionCommand("null");
        skipBackButton.setAlignmentX(1.0F);
        skipBackButton.setAlignmentY(1.0F);
        skipBackButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        skipBackButton.setBorderPainted(false);
        skipBackButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        skipBackButton.setDoubleBuffered(true);
        skipBackButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        skipBackButton.setMargin(new java.awt.Insets(4, 14, 4, 14));
        skipBackButton.setPreferredSize(new java.awt.Dimension(45, 45));
        skipBackButton.addActionListener(this::skipBackButtonActionPerformed);

        skipForwardButton.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        skipForwardButton.setText("⏩");
        skipForwardButton.setActionCommand("null");
        skipForwardButton.setAlignmentX(1.0F);
        skipForwardButton.setAlignmentY(1.0F);
        skipForwardButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        skipForwardButton.setBorderPainted(false);
        skipForwardButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        skipForwardButton.setDoubleBuffered(true);
        skipForwardButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        skipForwardButton.setMargin(new java.awt.Insets(4, 14, 4, 14));
        skipForwardButton.setPreferredSize(new java.awt.Dimension(45, 45));
        skipForwardButton.addActionListener(this::skipForwardButtonActionPerformed);

        playButton.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        playButton.setText(" ▶");
        playButton.setAlignmentX(1.0F);
        playButton.setAlignmentY(1.0F);
        playButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        playButton.setBorderPainted(false);
        playButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        playButton.setDoubleBuffered(true);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setMargin(new java.awt.Insets(4, 14, 4, 14));
        playButton.setPreferredSize(new java.awt.Dimension(45, 45));
        playButton.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        playButton.addActionListener(this::playButtonActionPerformed);

        pauseButton.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        pauseButton.setText("⏸");
        pauseButton.setActionCommand("null");
        pauseButton.setAlignmentX(1.0F);
        pauseButton.setAlignmentY(1.0F);
        pauseButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        pauseButton.setBorderPainted(false);
        pauseButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pauseButton.setDoubleBuffered(true);
        pauseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pauseButton.setMargin(new java.awt.Insets(4, 14, 4, 14));
        pauseButton.setPreferredSize(new java.awt.Dimension(45, 45));
        pauseButton.addActionListener(this::pauseButtonActionPerformed);

        stopButtonAction.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        stopButtonAction.setText("⏹");
        stopButtonAction.setActionCommand("null");
        stopButtonAction.setAlignmentX(1.0F);
        stopButtonAction.setAlignmentY(1.0F);
        stopButtonAction.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        stopButtonAction.setBorderPainted(false);
        stopButtonAction.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        stopButtonAction.setDoubleBuffered(true);
        stopButtonAction.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButtonAction.setMargin(new java.awt.Insets(4, 14, 4, 14));
        stopButtonAction.setPreferredSize(new java.awt.Dimension(45, 45));
        stopButtonAction.addActionListener(this::stopButtonActionActionPerformed);

        javax.swing.GroupLayout panelMedia_ControlsLayout = new javax.swing.GroupLayout(panelMedia_Controls);
        panelMedia_Controls.setLayout(panelMedia_ControlsLayout);
        panelMedia_ControlsLayout.setHorizontalGroup(
            panelMedia_ControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMedia_ControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMedia_ControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelMedia_ControlsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(skipBackButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(playButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pauseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(stopButtonAction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(skipForwardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelMedia_ControlsLayout.createSequentialGroup()
                        .addComponent(labelCurrentDuration)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(seekBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelMaxDuration)))
                .addContainerGap())
        );

        panelMedia_ControlsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {pauseButton, playButton, stopButtonAction});

        panelMedia_ControlsLayout.setVerticalGroup(
            panelMedia_ControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMedia_ControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMedia_ControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stopButtonAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMedia_ControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(skipBackButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(skipForwardButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMedia_ControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(seekBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelMaxDuration)
                    .addComponent(labelCurrentDuration))
                .addContainerGap())
        );

        panelMedia_ControlsLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {pauseButton, playButton, skipForwardButton});

        seekBar.setValue(0);

        blueBackgroundPane.setBorder(new javax.swing.border.MatteBorder(null));

        javax.swing.GroupLayout blueBackgroundPaneLayout = new javax.swing.GroupLayout(blueBackgroundPane);
        blueBackgroundPane.setLayout(blueBackgroundPaneLayout);
        blueBackgroundPaneLayout.setHorizontalGroup(
            blueBackgroundPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 802, Short.MAX_VALUE)
        );
        blueBackgroundPaneLayout.setVerticalGroup(
            blueBackgroundPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(blueBackgroundPane))
                    .addComponent(panelMedia_Controls, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .addComponent(blueBackgroundPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMedia_Controls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    /* === Visualizer Color === */
    // @author Creation
    // Uses a colour picked in the Type tab. It is remembered, so it carries over
    // when the visualizer style is changed.
    private void applyBarColor(Color color) {
        // This is never read, we might be able to remove this line.
        barColor = color;
        if (visual != null) {
            visual.setBarColor(color);
        }
    }

    // Goes back to the default green-to-red colours.
    private void resetBarColor() {
        applyBarColor(null);
    }

    /* === Refresh Library List === */
    private void refreshLibraryList() {
        customModel.clear();
        for (AudioFile a : library.getAudioFiles()) {
            if (!a.getName().startsWith("default_")) {
                customModel.addElement(a.getFile());
            }
        }
    }

    /* === Visualizer Creation === */
    private Visualizer createVisualizer(int index) {
        // The line below resolved an issue where fftData being null before a
        // file loaded, causing a null ptr exception on timer tick. Source prevents this issue
        Visualizer.FrequencyDataSource source = fftData != null ? fftData : new Visualizer.RandomFrequencyData(bandCount);
        Visualizer created = switch (index) {
            case 0 ->
                new HorizontalBarVisualizer(source, bandCount);
            case 1 ->
                new MirroredBarVisualizer(source, 2*bandCount);
            case 2 ->
                new RadialBarVisualizer(source, 2*bandCount);
            default ->
                new StandardBarVisualizer(source, bandCount);
        };
        created.setBarColor(barColor);
        return created;
    }

    // Visualizer Creation Helper
    private void showVisualizer(int index) {
        if (visual != null) {
            visual.stop();
            blueBackgroundPane.remove(visual);
        }
        visual = createVisualizer(index);
        blueBackgroundPane.add(visual, BorderLayout.CENTER);
        blueBackgroundPane.revalidate();
        blueBackgroundPane.repaint();
    }

    /* === Audio Loading === */
    private void loadAudioFile(File selectedFile) {
        AudioFile audioFile = new AudioFile(selectedFile.toPath());
        try {
            player = new Player(audioFile);
            fftData = new FFT(player, bandCount);
            seekBar.setMinimum(0);
            int totalMillis = (int) (player.getTotalTime() * 1000.0);
            seekBar.setMaximum(totalMillis);
            double totalSeconds = player.getTotalTime();
            int minutes = (int) (totalSeconds / 60);
            int seconds = (int) (totalSeconds % 60);
            labelMaxDuration.setText(String.format("%02d:%02d", minutes, seconds));
            labelCurrentDuration.setText("00:00");
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    /* Seek Bar Helper
    ** Syncs the seekbar if modified while paused.
    ** Used in skip player buttons and seek-on-click */
    private void updateTimeDisplay() {
        int millis = (int) (player.getCurrentFrame() * 1000L / player.getAudioFile().getSampleRate());
        seekBar.setValue(millis);
        labelCurrentDuration.setText(String.format("%02d:%02d", millis / 60000, (millis / 1000) % 60));
        
        if ((player.getTotalTime() - player.getCurrentTime()) < 0.01) {
            stopPlayer();
        }
    }
// === Media Control Handlers ===
    private void stopPlayer() {
        if (player != null) {
            try {
                player.stop();
                // Resets visual so a new one can load next time play is ckcked.
                if (visual != null) {
                    visual.stop();
                }
                player = null;
                fftData = null;
                showVisualizer(visualStyleList.getSelectedIndex());

                progressTimer.stop();
                seekBar.setValue(0);
                labelCurrentDuration.setText("--:--");
                labelMaxDuration.setText("--:--");
                
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        AudioFile current = library.getSelected();
        if (player == null && current != null) {
            File f = current.getFile();
            if (!f.exists()) {
                logger.log(Level.SEVERE, "File not found: {0}", f.getAbsolutePath());
                return;
            }
            loadAudioFile(f);
            showVisualizer(visualStyleList.getSelectedIndex());
        }
        if (player != null) {
            try {
                player.play();

                if (visual == null || visual.getParent() != blueBackgroundPane) {
                    showVisualizer(visualStyleList.getSelectedIndex());
                }
                visual.start();

                progressTimer.start();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_playButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        if (player != null && visual != null) {
            try {
                player.pause();
                visual.stop();
                progressTimer.stop();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void stopButtonActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionActionPerformed
        stopPlayer();
    }//GEN-LAST:event_stopButtonActionActionPerformed

    private void skipBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skipBackButtonActionPerformed
        if (player != null) {
            try {
                player.skipBack();
                updateTimeDisplay();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_skipBackButtonActionPerformed

    private void skipForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skipForwardButtonActionPerformed
        if (player != null) {
            try {
                player.skipForward();
                updateTimeDisplay();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_skipForwardButtonActionPerformed

    private void importFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFileButtonActionPerformed
        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setDialogTitle("Choose a .wav file");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("WAV audio (*.wav)", "wav"));
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File picked = chooser.getSelectedFile();
            library.importWav(picked.getAbsolutePath()); // copies the file into the Library folder
            File selected = new File(library.getLibraryLocation(), picked.getName());
            if (!selected.exists()) {
                return; // import failed
            }
//             File selected = chooser.getSelectedFile();
            if (!customModel.contains(selected)) {
                customModel.addElement(selected);
            }
            playlistDefault.clearSelection();
            playlistCustom.setSelectedValue(selected, true);

            // Added as a bug fix, to stop the active player and timer BEFORE changing a selectedFile or loading a new one.
            if (player != null) {
                try {
                    player.stop();
                } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
                    // handle or log exception
                }
            }
            progressTimer.stop();
            if (visual != null) {
                visual.stop();
                blueBackgroundPane.remove(visual);
                visual = null;
                blueBackgroundPane.revalidate();
                blueBackgroundPane.repaint();
            }
            loadAudioFile(selected);
        }
    }//GEN-LAST:event_importFileButtonActionPerformed

    private void deleteFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFileButtonActionPerformed
        File target = playlistCustom.getSelectedValue();
        if (target == null || target.getName().startsWith("default_")) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Permanently delete \"" + target.getName() + "\" from the Library folder?",
                "Delete File",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        stopButtonActionActionPerformed(null); // releases the file before deleting
        for (AudioFile a : new java.util.ArrayList<>(library.getAudioFiles())) {
            if (a.getName().equals(target.getName())) {
                library.delete(a);
                break;
            }
        }
        customModel.removeElement(target);
        playlistCustom.clearSelection();
    }//GEN-LAST:event_deleteFileButtonActionPerformed

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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Create and display the form*/
        EventQueue.invokeLater(() -> new GUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabbedPane;
    private final javax.swing.JSpinner barAdjuster = new javax.swing.JSpinner();
    private javax.swing.JDesktopPane blueBackgroundPane;
    private javax.swing.JSeparator customLibrarySeperator;
    private javax.swing.JButton deleteFileButton;
    private javax.swing.JButton importFileButton;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelBarAdjuster;
    private javax.swing.JLabel labelCurrentDuration;
    private javax.swing.JLabel labelCustom;
    private javax.swing.JLabel labelMaxDuration;
    private javax.swing.JLabel labelReference;
    private javax.swing.JLabel labelVisualizerStyle;
    private javax.swing.JPanel panelMedia_Controls;
    private javax.swing.JPanel panelTab_Configs;
    private javax.swing.JPanel panelTab_Files;
    private javax.swing.JPanel panelTab_Type;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton playButton;
    private javax.swing.JList<File> playlistCustom;
    private javax.swing.JList<String> playlistDefault;
    private javax.swing.JScrollPane scrollCustom;
    private javax.swing.JScrollPane scrollReference;
    private javax.swing.JSlider seekBar;
    private javax.swing.JButton skipBackButton;
    private javax.swing.JButton skipForwardButton;
    private javax.swing.JButton stopButtonAction;
    private javax.swing.JList<String> visualStyleList;
    // End of variables declaration//GEN-END:variables
}
