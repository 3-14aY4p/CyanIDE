/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

// custom packages
import components.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.tree.*;

/**
 *
 * @author tianmei
 */

/*
 MINIMUM REQUIREMENTS:
 - menu bar
 - tool bar
 - open existing text/code
 - save code with desired file ext.
 - multiple tabs feature
 - line numbering
 - code/syntax highlighter
 - code parsing for output
   + print statement
   + variable declaration
   + variable assignment
   + evaluate expressions
   + if-else statement

FEATURE LIST:
    [x] get session/user's name
    [x] get OS & determine font
    [x] get previous session's user 
        config (i.e. theme/LAF)
    [x] tab handling
        [x] new tab (on new and open file)
        [x] close tab (double-click LMB)
    [x] text handling
        [x] proper tabulation
        [x] * mark file with changes
        [?] undo & redo text            // FIXME: Undos initial text after opening files
        [x] syntax highlighter
    [x] file handling
        [x] create new file
        [x] open file
            [x] filepath cache (if a file is already open, 
                navigate to it instead of opening new tab)
        [x] save file
        [x] save file as
    [x] dir navigator
        [x] open folder/directory
        [x] display subfolders & files
        [x] open file on double-click
    [ ] parser
        [x] print statement
        [?] variable declaration & assignment
        [ ] variable re-assignment
        [x] parse arithmetic expressions
        [ ] parse logical expressions
        [ ] if-else statement

    [x] run code
        [x] display output
        [x] display status

    NOTES:
    -- no parsing for logical expressions
 */
public final class GUI_main extends javax.swing.JFrame {

    // retrieve machine OS & current session
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String USERNAME = System.getProperty("user.name");

    // for font defaults
    String fontFamily;      // determined by OS; i.e. Adwaita Mono for linux comp
    Font defaultFont;       // used by all JEditorPanes/JTextPanes for uniformity

    // for file handling
    final String customFileExt = ".rtxt";

    // monitor currently open files
    ArrayList<String> filepathCache = new ArrayList<>();

    // for dir navigator
    private final DefaultTreeModel treeModel;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GUI_main.class.getName());

    /**
     * Creates new form GUI_main
     */
    public GUI_main() {
        initComponents();

        // initialize JTree component
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Open folder");
        treeModel = new DefaultTreeModel(root);

        fileTree.setModel(treeModel);
        fileTree.setShowsRootHandles(true);

        // revert components into their clean state
        tbpn_editor.removeAll();    // removes sample editor pane 
        filepathCache.clear();      // this just ensures it clear

        // initialize default font
        if (OS.matches("^windows.*")) {
            this.fontFamily = "Courier New";
        } else {
            // for linux; idk about mac users
            this.fontFamily = "Adwaita Mono";
        }
        this.defaultFont = new Font(fontFamily, Font.PLAIN, 16);

        tpn_output.setFont(defaultFont);
        tpn_status.setFont(defaultFont);

        // creating a welcome tab with basic syntax as reference
        createTab("welcome" + customFileExt, true);
        JEditorPane welcomeTab = getSelectedTextPane();
        welcomeTab.setText(
                welcomeTab.getText() + "\n"
                + """  
                >> this is a comment
                  
                >> this is a print statement
                echo("hello world");
                  
                >> this language uses only 4 datatypes:
                >> int  : integer
                >> dec  : decimal
                >> str  : string
                >> bool : boolean
                  
                >> this is how you declare a variable
                int x <- 5;
                  
                >> you can use arithmetic on this!
                int y <- x - 3;
                echo(y);
                  
                >> this is how you write conditionals
                if (condition 1):
                  ...
                elif (condition 2):
                  ...
                elif (condition 3):
                  ...
                else:
                  ...
                endif
                  
                  """
        );
        resetTabIcon();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_toolbar = new javax.swing.JPanel();
        btn_new = new javax.swing.JButton();
        btn_open = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(24, 0), new java.awt.Dimension(24, 0), new java.awt.Dimension(32767, 0));
        btn_undo = new javax.swing.JButton();
        btn_redo = new javax.swing.JButton();
        btn_cut = new javax.swing.JButton();
        btn_copy = new javax.swing.JButton();
        btn_paste = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(54, 0), new java.awt.Dimension(54, 0), new java.awt.Dimension(32767, 0));
        btn_run = new javax.swing.JButton();
        btn_debug = new javax.swing.JButton();
        btn_stop = new javax.swing.JButton();
        pnl_main = new javax.swing.JPanel();
        treeParent = new javax.swing.JScrollPane();
        fileTree = new javax.swing.JTree();
        tbpn_editor = new javax.swing.JTabbedPane();
        sampleParent = new javax.swing.JScrollPane();
        sampleTextPane = new javax.swing.JTextPane();
        tbpn_info = new javax.swing.JTabbedPane();
        outputParent = new javax.swing.JScrollPane();
        tpn_output = new javax.swing.JTextPane();
        statusParent = new javax.swing.JScrollPane();
        tpn_status = new javax.swing.JTextPane();
        menuBar = new javax.swing.JMenuBar();
        mn_file = new javax.swing.JMenu();
        mni_new = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mni_openFile = new javax.swing.JMenuItem();
        mni_openFolder = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mni_save = new javax.swing.JMenuItem();
        mni_saveAs = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mni_exit = new javax.swing.JMenuItem();
        mn_edit = new javax.swing.JMenu();
        mni_undo = new javax.swing.JMenuItem();
        mni_redo = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mni_cut = new javax.swing.JMenuItem();
        mni_copy = new javax.swing.JMenuItem();
        mni_paste = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mni_options = new javax.swing.JMenuItem();
        mn_run = new javax.swing.JMenu();
        mni_run = new javax.swing.JMenuItem();
        mni_debug = new javax.swing.JMenuItem();
        mni_stop = new javax.swing.JMenuItem();
        mn_view = new javax.swing.JMenu();
        mni_zoomIn = new javax.swing.JMenuItem();
        mni_zoomOut = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CyanIDE");
        setResizable(false);
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0);
        flowLayout1.setAlignOnBaseline(true);
        getContentPane().setLayout(flowLayout1);

        pnl_toolbar.setMinimumSize(new java.awt.Dimension(1280, 40));
        pnl_toolbar.setPreferredSize(new java.awt.Dimension(1280, 40));
        pnl_toolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 6, 8));

        btn_new.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/new.png"))); // NOI18N
        btn_new.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_new.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_new.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_new.addActionListener(this::btn_newActionPerformed);
        pnl_toolbar.add(btn_new);

        btn_open.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open.png"))); // NOI18N
        btn_open.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_open.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_open.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_open.addActionListener(this::btn_openActionPerformed);
        pnl_toolbar.add(btn_open);

        btn_save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        btn_save.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_save.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_save.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_save.addActionListener(this::btn_saveActionPerformed);
        pnl_toolbar.add(btn_save);
        pnl_toolbar.add(filler1);

        btn_undo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/undo.png"))); // NOI18N
        btn_undo.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_undo.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_undo.setPreferredSize(new java.awt.Dimension(24, 24));
        pnl_toolbar.add(btn_undo);

        btn_redo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/redo.png"))); // NOI18N
        btn_redo.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_redo.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_redo.setPreferredSize(new java.awt.Dimension(24, 24));
        pnl_toolbar.add(btn_redo);

        btn_cut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cut.png"))); // NOI18N
        btn_cut.setEnabled(false);
        btn_cut.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_cut.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_cut.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_cut.addActionListener(this::btn_cutActionPerformed);
        pnl_toolbar.add(btn_cut);

        btn_copy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/copy.png"))); // NOI18N
        btn_copy.setEnabled(false);
        btn_copy.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_copy.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_copy.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_copy.addActionListener(this::btn_copyActionPerformed);
        pnl_toolbar.add(btn_copy);

        btn_paste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/paste.png"))); // NOI18N
        btn_paste.setEnabled(false);
        btn_paste.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_paste.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_paste.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_paste.addActionListener(this::btn_pasteActionPerformed);
        pnl_toolbar.add(btn_paste);
        pnl_toolbar.add(filler2);

        btn_run.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/run.png"))); // NOI18N
        btn_run.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_run.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_run.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_run.addActionListener(this::btn_runActionPerformed);
        pnl_toolbar.add(btn_run);

        btn_debug.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/debug.png"))); // NOI18N
        btn_debug.setEnabled(false);
        btn_debug.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_debug.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_debug.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_debug.addActionListener(this::btn_debugActionPerformed);
        pnl_toolbar.add(btn_debug);

        btn_stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit.png"))); // NOI18N
        btn_stop.setEnabled(false);
        btn_stop.setMaximumSize(new java.awt.Dimension(24, 24));
        btn_stop.setMinimumSize(new java.awt.Dimension(24, 24));
        btn_stop.setPreferredSize(new java.awt.Dimension(24, 24));
        btn_stop.addActionListener(this::btn_stopActionPerformed);
        pnl_toolbar.add(btn_stop);

        getContentPane().add(pnl_toolbar);

        pnl_main.setMinimumSize(new java.awt.Dimension(1280, 686));
        pnl_main.setPreferredSize(new java.awt.Dimension(1280, 686));
        pnl_main.setLayout(null);

        fileTree.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("RootFolder");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("SubFolder1");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("fileSample.txt");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("SubFolder2");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("fileSample.txt");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("SubFolder3");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("fileSample.txt");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        fileTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        fileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileItemClicked(evt);
            }
        });
        treeParent.setViewportView(fileTree);

        pnl_main.add(treeParent);
        treeParent.setBounds(20, 20, 180, 460);

        tbpn_editor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onCloseTabClicked(evt);
            }
        });

        sampleTextPane.setFont(new java.awt.Font("Adwaita Mono", 0, 16)); // NOI18N
        sampleTextPane.setText(">> Author     : %s\n>> Filename   : %s\n>> Date       : %s   ");
        sampleParent.setViewportView(sampleTextPane);

        tbpn_editor.addTab("untitled-0.txt", sampleParent);

        pnl_main.add(tbpn_editor);
        tbpn_editor.setBounds(210, 10, 1050, 470);

        tbpn_info.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        outputParent.setViewportView(tpn_output);

        tbpn_info.addTab("OUTPUT", outputParent);

        statusParent.setViewportView(tpn_status);

        tbpn_info.addTab("STATUS", statusParent);

        pnl_main.add(tbpn_info);
        tbpn_info.setBounds(20, 490, 1240, 180);

        getContentPane().add(pnl_main);

        mn_file.setText("File");

        mni_new.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_new.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/new.png"))); // NOI18N
        mni_new.setText("New");
        mni_new.addActionListener(this::mni_newActionPerformed);
        mn_file.add(mni_new);
        mn_file.add(jSeparator1);

        mni_openFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_openFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open.png"))); // NOI18N
        mni_openFile.setText("Open file");
        mni_openFile.addActionListener(this::mni_openFileActionPerformed);
        mn_file.add(mni_openFile);

        mni_openFolder.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_openFolder.setText("Open folder");
        mni_openFolder.addActionListener(this::mni_openFolderActionPerformed);
        mn_file.add(mni_openFolder);
        mn_file.add(jSeparator2);

        mni_save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_save.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        mni_save.setText("Save");
        mni_save.addActionListener(this::mni_saveActionPerformed);
        mn_file.add(mni_save);

        mni_saveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_saveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save-as.png"))); // NOI18N
        mni_saveAs.setText("Save as");
        mni_saveAs.addActionListener(this::mni_saveAsActionPerformed);
        mn_file.add(mni_saveAs);
        mn_file.add(jSeparator3);

        mni_exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mni_exit.setText("Exit");
        mni_exit.addActionListener(this::mni_exitActionPerformed);
        mn_file.add(mni_exit);

        menuBar.add(mn_file);

        mn_edit.setText("Edit");

        mni_undo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_undo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/undo.png"))); // NOI18N
        mni_undo.setText("Undo");
        mn_edit.add(mni_undo);

        mni_redo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_redo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/redo.png"))); // NOI18N
        mni_redo.setText("Redo");
        mn_edit.add(mni_redo);
        mn_edit.add(jSeparator4);

        mni_cut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_cut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cut.png"))); // NOI18N
        mni_cut.setText("Cut");
        mni_cut.addActionListener(this::mni_cutActionPerformed);
        mn_edit.add(mni_cut);

        mni_copy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_copy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/copy.png"))); // NOI18N
        mni_copy.setText("Copy");
        mni_copy.addActionListener(this::mni_copyActionPerformed);
        mn_edit.add(mni_copy);

        mni_paste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_paste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/paste.png"))); // NOI18N
        mni_paste.setText("Paste");
        mni_paste.addActionListener(this::mni_pasteActionPerformed);
        mn_edit.add(mni_paste);
        mn_edit.add(jSeparator5);

        mni_options.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_options.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/options.png"))); // NOI18N
        mni_options.setText("Options");
        mni_options.addActionListener(this::mni_optionsActionPerformed);
        mn_edit.add(mni_options);

        menuBar.add(mn_edit);

        mn_run.setText("Run");

        mni_run.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        mni_run.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/run.png"))); // NOI18N
        mni_run.setText("Run");
        mni_run.addActionListener(this::mni_runActionPerformed);
        mn_run.add(mni_run);

        mni_debug.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        mni_debug.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/debug.png"))); // NOI18N
        mni_debug.setText("Debug");
        mni_debug.addActionListener(this::mni_debugActionPerformed);
        mn_run.add(mni_debug);

        mni_stop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        mni_stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/exit.png"))); // NOI18N
        mni_stop.setText("Stop");
        mni_stop.addActionListener(this::mni_stopActionPerformed);
        mn_run.add(mni_stop);

        menuBar.add(mn_run);

        mn_view.setText("View");

        mni_zoomIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_zoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom-in.png"))); // NOI18N
        mni_zoomIn.setText("Zoom in");
        mni_zoomIn.addActionListener(this::mni_zoomInActionPerformed);
        mn_view.add(mni_zoomIn);

        mni_zoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DOWN, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mni_zoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom-out.png"))); // NOI18N
        mni_zoomOut.setText("Zoom out");
        mni_zoomOut.addActionListener(this::mni_zoomOutActionPerformed);
        mn_view.add(mni_zoomOut);

        menuBar.add(mn_view);

        setJMenuBar(menuBar);

        setSize(new java.awt.Dimension(1280, 751));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // GETTERS
    MyTextPane getTextPane(JScrollPane tab) {
        JViewport tabViewport = (JViewport) tab.getComponent(0);
        return (MyTextPane) tabViewport.getView();
    }

    MyTextPane getSelectedTextPane() {
        JScrollPane selectedTab = (JScrollPane) tbpn_editor.getSelectedComponent();
        return getTextPane(selectedTab);
    }

    String getSelectedTabTitle() {
        int tabIndex = tbpn_editor.getSelectedIndex();
        return tbpn_editor.getTitleAt(tabIndex);
    }

    // TAB HANDLING
    void createTab(String tabName, boolean addHeader) {
        JScrollPane tab = new JScrollPane();
        tab.setName(tabName);

        MyTextPane tabView = new MyTextPane(btn_undo, btn_redo, mni_undo, mni_redo);
        tabView.setFont(defaultFont);

        MyTextLineNumber lineNumber = new MyTextLineNumber(tabView);
        tab.setRowHeaderView(lineNumber);

        if (addHeader) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            tabView.setText(String.format("""
        >> Author     : %s
        >> Filename   : %s
        >> Date       : %s
                                      
        """, USERNAME, tabName, today));
        }

        // adding it here 'cause somehow adding it
        // to my actual class didn't work?? bruh..
        tabView.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                tabView.setUndoManagerFocusState(true);
            }

            @Override
            public void focusLost(FocusEvent fe) {
                tabView.setUndoManagerFocusState(false);
            }
        });

        tbpn_editor.add(tab);
        tab.setViewportView(tabView);

        // this is for marking when you have added unsaved changes onto the file
        tbpn_editor.setSelectedComponent(tab);
        tabView.getDocument().addDocumentListener(new DocumentListener() {
            ImageIcon icon = new ImageIcon("src/main/resources/icons/asterisk.png");
            
            @Override
            public void insertUpdate(DocumentEvent de) {
                updateIcon();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                updateIcon();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                updateIcon();
            }
            
            public void updateIcon() {
                tbpn_editor.setIconAt(tbpn_editor.getSelectedIndex(), icon);
            }
        });
    }

    void closeTab(JTabbedPane tabbedPane, int index) {
        tabbedPane.removeTabAt(index);
    }

    // VISUAL FILE UPDATES INDICATOR
    void updateTabTitle(String newTitle) {
        int index = tbpn_editor.getSelectedIndex();
        tbpn_editor.setTitleAt(index, newTitle);
    }

    void resetTabIcon() {
        int index = tbpn_editor.getSelectedIndex();
        tbpn_editor.setIconAt(index, null);
    }

    // TREE HANDLING
    private void loadSelectedFolder(File folder) {
        SwingWorker<DefaultMutableTreeNode, Void> worker
                = new SwingWorker<DefaultMutableTreeNode, Void>() {

            @Override
            protected DefaultMutableTreeNode doInBackground() {
                // Create root node using selected folder
                DefaultMutableTreeNode root
                        = new DefaultMutableTreeNode(folder);
                // Load subfolders and files
                loadDirectories(folder, root);

                return root;
            }

            @Override
            protected void done() {
                try {
                    // Get completed folder structure
                    DefaultMutableTreeNode root = get();

                    // Update JTree
                    treeModel.setRoot(root);

                    // Expand root node
                    fileTree.expandRow(0);

                    fileTree.revalidate();
                    fileTree.repaint();

                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(
                            GUI_main.this,
                            "Error loading folder: " + e.getMessage()
                    );
                }
            }
        };
        worker.execute();
    }

    private void loadDirectories(File directory, DefaultMutableTreeNode parentNode) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (!file.canRead()) {
                continue;
            }
            // Create child node
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
            parentNode.add(childNode);

            // Check if folder
            if (file.isDirectory()) {
                loadDirectories(file, childNode);
            }
        }
    }

    private void displayFiles() {
        SwingWorker<DefaultMutableTreeNode, Void> worker
                = new SwingWorker<DefaultMutableTreeNode, Void>() {

            @Override
            protected DefaultMutableTreeNode doInBackground() {
                // Create root node
                DefaultMutableTreeNode root
                        = new DefaultMutableTreeNode("My Computer");

                // Get computer drives
                File[] drives = File.listRoots();
                if (drives != null) {
                    for (File drive : drives) {
                        DefaultMutableTreeNode driveNode = new DefaultMutableTreeNode(drive);
                        root.add(driveNode);

                        // Load actual folders
                        loadDirectories(drive, driveNode);
                    }
                }
                return root;
            }

            @Override
            protected void done() {
                try {
                    // Get completed folder structure
                    DefaultMutableTreeNode root = get();

                    // Update existing JTree
                    treeModel.setRoot(root);

                    fileTree.expandRow(0);

                    fileTree.revalidate();
                    fileTree.repaint();
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(
                            GUI_main.this,
                            "Error loading folders: " + e.getMessage()
                    );
                }
            }
        };
        worker.execute();
    }

    // FILE HANDLING
    void openFile(File file) {
        createTab(file.getName(), false);
        MyTextPane textPane = getSelectedTextPane();

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            textPane.setText("");

            while ((line = br.readLine()) != null) {
                textPane.setText(
                        textPane.getText().stripLeading() + "\n" + line
                );
            }

            br.close();

            filepathCache.add(file.getPath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to open file.");
        }
    }

    void saveFile(String filePath) {
        JEditorPane editorPane = getSelectedTextPane();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(editorPane.getText());
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to save file.");
        }
    }

    void saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text", customFileExt.replace(".", "")));
        fileChooser.setSelectedFile(new File(getSelectedTabTitle())); // Default filename

        fileChooser.setDialogTitle("Save file as");

        int choice = fileChooser.showSaveDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            saveFile(file.getPath());

            filepathCache.add(file.getPath());
            updateTabTitle(file.getName());
            resetTabIcon();
        }
    }

    // EVENT HANDLERS
    void onNewFilePressed() {
        String nameInput = JOptionPane.showInputDialog(null, "Please enter file name");

        if (nameInput != null) {
            if (nameInput.trim().isEmpty()) {
                int tab_count = 0;
                for (int i = 0; i < tbpn_editor.getComponentCount(); i++) {
                    if (tbpn_editor.getTitleAt(i).matches("^untitled-.*")) {
                        tab_count++;
                    }
                }
                createTab("untitled-" + tab_count + customFileExt, true);
            } else {
                createTab(nameInput + customFileExt, true);
            }
        }
    }

    void onOpenFilePressed() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "text", customFileExt.replace(".", "")));
        fileChooser.setDialogTitle("Open file");

        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();

            for (Component c : tbpn_editor.getComponents()) {
                if (c.getName().equals(fileName)) {
                    tbpn_editor.setSelectedComponent(c);
                    break;
                } else {
                    openFile(file);
                    break;
                }
            }
        }
    }

    void onOpenFolderPressed() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(
                JFileChooser.DIRECTORIES_ONLY
        );
        fileChooser.setCurrentDirectory(
                new File(System.getProperty("user.home"))
        );

        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            loadSelectedFolder(selectedFolder);
        }
    }

    void onSavePressed() {
        if (tbpn_editor.getComponents().length > 0) {
            String tabTitle = getSelectedTabTitle();

            if (filepathCache.isEmpty()) {
                saveFileAs();
            } else {
                for (String path : filepathCache) {
                    if (path.matches(".*/" + tabTitle + "$")) {
                        saveFile(path);
                        resetTabIcon();
                        break;
                    } else {
                        saveFileAs();
                        break;
                    }
                }
            }
        }
    }

    void onSaveAsPressed() {
        if (tbpn_editor.getComponents().length > 0) {
            saveFileAs();
        }
    }

    void onOptionsPressed() {
        new GUI_options().setVisible(true);
    }

    // TODO: RUNNING THE PROGRAM
    void onRunPressed() {
        String sourceCode = getSelectedTextPane().getText();

        HashMap<String, Stack> outputHm
                = MyCodeCompiler.compileProgram(sourceCode);

        Stack<HashMap<String, Object>> vars = outputHm.get("VARS");
        Stack<HashMap<String, Object>> errs = outputHm.get("ERRS");
        Stack<HashMap<String, Object>> prnt = outputHm.get("PRNT");

        if (errs.isEmpty()) {
            tpn_output.setText("""
                --- no errors found ---
                 -------------------------------------------
                    BUILD SUCCESS
                 -------------------------------------------
                """);
            tpn_status.setText("[STATUS]: NO ERRORS found in program.\n");

            for (HashMap hm : prnt) {
                tpn_output.setText(tpn_output.getText() + "\n" + hm.get("TEXT"));
            }
            for (HashMap hm : vars) {
                tpn_status.setText(tpn_status.getText() + "\n" + hm);
            }
        } else {
            for (HashMap hm : errs) {
                tpn_output.setText("""
                    --- errors found in program ---
                     -------------------------------------------
                        BUILD FAILED
                     -------------------------------------------
                    """);
                tpn_status.setText("[STATUS]: ERRORS found in program.\n");

                tpn_status.setText(
                        tpn_status.getText() + "\n" + String.format("+ <ln %d>\t%s", hm.get("LNUM"), hm.get("ERROR"))
                );
            }
        }
    }

    // MENU: FILE
    private void mni_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_newActionPerformed
        onNewFilePressed();
    }//GEN-LAST:event_mni_newActionPerformed

    private void mni_openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_openFileActionPerformed
        onOpenFilePressed();
    }//GEN-LAST:event_mni_openFileActionPerformed

    private void mni_openFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_openFolderActionPerformed
        onOpenFolderPressed();
    }//GEN-LAST:event_mni_openFolderActionPerformed

    private void mni_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_saveActionPerformed
        onSavePressed();
    }//GEN-LAST:event_mni_saveActionPerformed

    private void mni_saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_saveAsActionPerformed
        onSaveAsPressed();
    }//GEN-LAST:event_mni_saveAsActionPerformed

    private void mni_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_exitActionPerformed
        if (tbpn_editor.getComponents().length > 0) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Do you want to exit the application?\n(Does not save progress)",
                    "Confirm close", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }//GEN-LAST:event_mni_exitActionPerformed

    // MENU: EDIT
    private void mni_cutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_cutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mni_cutActionPerformed

    private void mni_copyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_copyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mni_copyActionPerformed

    private void mni_pasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_pasteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mni_pasteActionPerformed

    private void mni_optionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_optionsActionPerformed
        onOptionsPressed();
    }//GEN-LAST:event_mni_optionsActionPerformed

    // MENU: RUN
    private void mni_runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_runActionPerformed
        onRunPressed();
    }//GEN-LAST:event_mni_runActionPerformed

    private void mni_debugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_debugActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mni_debugActionPerformed

    private void mni_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_stopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mni_stopActionPerformed

    // TOOL: FILE
    private void btn_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_newActionPerformed
        onNewFilePressed();
    }//GEN-LAST:event_btn_newActionPerformed

    private void btn_openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_openActionPerformed
        onOpenFilePressed();
    }//GEN-LAST:event_btn_openActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        onSavePressed();
    }//GEN-LAST:event_btn_saveActionPerformed

    // TOOL: EDIT
    private void btn_cutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_cutActionPerformed

    private void btn_copyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_copyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_copyActionPerformed

    private void btn_pasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_pasteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_pasteActionPerformed

    // TOOL: RUN
    private void btn_runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_runActionPerformed
        onRunPressed();
    }//GEN-LAST:event_btn_runActionPerformed

    private void btn_debugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_debugActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_debugActionPerformed

    private void btn_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_stopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_stopActionPerformed

    // TREE INTERACTIONS
    /*  ** SOME NOTES ON MOUSE CLICKS **
     *  MOUSE BUTTON:
     *   1 - left
     *   2 - middle
     *   3 - right
     */

    private void fileItemClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileItemClicked
        if (evt.getButton() == 1 && evt.getClickCount() == 2) {
            try {
                String rawPath = fileTree.getSelectionPath().toString().replaceAll(", ", "/");
                String filePath = rawPath.replaceAll(".*\\[|\\]", "");

                if (filePath.equals("Open folder")) {
                    onOpenFolderPressed();
                } else {
                    File file = new File(filePath);

                    if (file.isFile()) {
                        String fileName = file.getName();

                        if (tbpn_editor.getComponentCount() == 0) {
                            openFile(file);
                        } else {
                            for (Component c : tbpn_editor.getComponents()) {
                                if (c.getName().equals(fileName)) {
                                    tbpn_editor.setSelectedComponent(c);
                                    break;
                                } else {
                                    openFile(file);
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Folder path not found.");
            }
        }
    }//GEN-LAST:event_fileItemClicked

    // TAB INTERACTIONS
    private void onCloseTabClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onCloseTabClicked
        if (evt.getButton() == 1 && evt.getClickCount() == 2 && tbpn_editor.getComponents().length > 0) {
            int index = tbpn_editor.getSelectedIndex();

            int choice = JOptionPane.showConfirmDialog(this,
                    "Do you want to close this tab?\n(Does not save progress)",
                    "Confirm close", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                closeTab(tbpn_editor, index);

                if (!filepathCache.isEmpty()) {
                    String tabTitle = getSelectedTabTitle();
                    for (String path : filepathCache) {
                        if (path.matches(".*/" + tabTitle + "$")) {
                            filepathCache.remove(path);
                            break;
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_onCloseTabClicked

    private void mni_zoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_zoomInActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mni_zoomInActionPerformed

    private void mni_zoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mni_zoomOutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mni_zoomOutActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GUI_main().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_copy;
    private javax.swing.JButton btn_cut;
    private javax.swing.JButton btn_debug;
    private javax.swing.JButton btn_new;
    private javax.swing.JButton btn_open;
    private javax.swing.JButton btn_paste;
    private javax.swing.JButton btn_redo;
    private javax.swing.JButton btn_run;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton btn_stop;
    private javax.swing.JButton btn_undo;
    private javax.swing.JTree fileTree;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu mn_edit;
    private javax.swing.JMenu mn_file;
    private javax.swing.JMenu mn_run;
    private javax.swing.JMenu mn_view;
    private javax.swing.JMenuItem mni_copy;
    private javax.swing.JMenuItem mni_cut;
    private javax.swing.JMenuItem mni_debug;
    private javax.swing.JMenuItem mni_exit;
    private javax.swing.JMenuItem mni_new;
    private javax.swing.JMenuItem mni_openFile;
    private javax.swing.JMenuItem mni_openFolder;
    private javax.swing.JMenuItem mni_options;
    private javax.swing.JMenuItem mni_paste;
    private javax.swing.JMenuItem mni_redo;
    private javax.swing.JMenuItem mni_run;
    private javax.swing.JMenuItem mni_save;
    private javax.swing.JMenuItem mni_saveAs;
    private javax.swing.JMenuItem mni_stop;
    private javax.swing.JMenuItem mni_undo;
    private javax.swing.JMenuItem mni_zoomIn;
    private javax.swing.JMenuItem mni_zoomOut;
    private javax.swing.JScrollPane outputParent;
    private javax.swing.JPanel pnl_main;
    private javax.swing.JPanel pnl_toolbar;
    private javax.swing.JScrollPane sampleParent;
    private javax.swing.JTextPane sampleTextPane;
    private javax.swing.JScrollPane statusParent;
    private javax.swing.JTabbedPane tbpn_editor;
    private javax.swing.JTabbedPane tbpn_info;
    private javax.swing.JTextPane tpn_output;
    private javax.swing.JTextPane tpn_status;
    private javax.swing.JScrollPane treeParent;
    // End of variables declaration//GEN-END:variables
}
