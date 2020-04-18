import Json.JsonUtils;
import Structures.HttpResult;
import Structures.TabRowDataContainersBrief;
import Structures.TabRowDataImages;
import Structures.TabRowDataRegistryRepo;
import TabModels.TabModContainersBrief;
import TabModels.TabModImages;
import TabModels.TabModRegistryRepos;
import Utils.HttpUtils;
import Utils.RestApi;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

/*####################################################################################*/
public class frmMain extends JFrame implements ActionListener {
    private JPanel mainPanel;
    private JTabbedPane tabbedPane1;
    private JButton btnClearMainLog;
    private JList<String> lbMainLog;
    public JTable tblImages;
    public JTable tblContainers;
    private JButton btnUpdateAll;
    private JList<String> lbResponse;
    public JTextField txImageName;
    public JButton btnBrowseImgDockerfile;
    public JTextField txImageDockerfilePath;
    public JButton btnImageBuild;
    public JTextField txRegistryUrl;
    public JButton btnRegistryUpdate;
    public JList lbRegistryRepos;
    public JTable tblRegistryRepos;

    public DefaultListModel<String> dlmMainLog = new DefaultListModel<>();
    public DefaultListModel<String> dlmResponse = new DefaultListModel<>();
    public DefaultListModel<String> dlmregistryRepos = new DefaultListModel<>();

    private final Logger log = LoggerFactory.getLogger(frmMain.class);

    private JPopupMenu popImages, popContainers;
    private JMenuItem mnuPpImgRemove, mnuPpImgInspect, mnuPpImgRemoveByIndex;
    private JMenuItem mnuPpContRemove, mnuPpContStart, mnuPpContStop, mnuPpContInspect;


    private int selRowImg, selRowCont;

    static JFrame frame;

    /*#######################################################################

    ########################################################################*/
    public frmMain() {

        lbMainLog.setModel(dlmMainLog);
        lbResponse.setModel(dlmResponse);
        lbRegistryRepos.setModel(dlmregistryRepos);
        CreatePopups();

        URL imgURL = frmMain.class.getResource("docker-64.png");
        Image img = new ImageIcon(imgURL).getImage();
        frame.setIconImage(img);

        tblImages.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ImagesMouseHandler(e);
            }
        });
        tblContainers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                ContainersMouseHandler(e);
            }
        });

        UpdateContainers();
        UpdateImages();

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(null, "F5 pressed !");
                UpdateContainers();
                UpdateImages();
            }
        };
        mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "F5");
        mainPanel.getActionMap().put("F5", escapeAction);

        btnClearMainLog.addActionListener(e -> dlmMainLog.clear());
        btnUpdateAll.addActionListener(e -> {
            UpdateImages();
            UpdateContainers();
        });
        btnBrowseImgDockerfile.addActionListener(e -> BrowseImgBuildDockerFile());
        btnImageBuild.addActionListener(e -> BuildImage());
        btnRegistryUpdate.addActionListener(e -> Updateregistry());
    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void BuildImage() {
        String name = txImageName.getText();
        String path = txImageDockerfilePath.getText();
        dlmResponse.clear();
        dlmResponse.addElement("Building the image .....");
        HttpResult res = RestApi.BuildImage(name, path);
        ProcessResponse(res);
        UpdateImages();
    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void BrowseImgBuildDockerFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(mainPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txImageDockerfilePath.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void CreatePopups() {
        // IMAGES popup
        popImages = new JPopupMenu("pop_images");
        mnuPpImgRemove = new JMenuItem("Remove");
        mnuPpImgRemove.addActionListener(this);
        popImages.add(mnuPpImgRemove);
        mnuPpImgRemoveByIndex = new JMenuItem("Remove By Index");
        mnuPpImgRemoveByIndex.addActionListener(this);
        popImages.add(mnuPpImgRemoveByIndex);
        mnuPpImgInspect = new JMenuItem("Inspect");
        mnuPpImgInspect.addActionListener(this);
        popImages.add(mnuPpImgInspect);
        // CONTAINER popus
        popContainers = new JPopupMenu("pop_containers");
        mnuPpContStart = new JMenuItem("Start");
        mnuPpContStart.addActionListener(this);
        popContainers.add(mnuPpContStart);
        mnuPpContStop = new JMenuItem("Stop");
        mnuPpContStop.addActionListener(this);
        popContainers.add(mnuPpContStop);
        mnuPpContRemove = new JMenuItem("Remove");
        mnuPpContRemove.addActionListener(this);
        popContainers.add(mnuPpContRemove);
        mnuPpContInspect = new JMenuItem("Inspect");
        mnuPpContInspect.addActionListener(this);
        popContainers.add(mnuPpContInspect);

    }


    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem) (e.getSource());
        String mnuLabel = ((JPopupMenu) source.getComponent().getParent()).getLabel();
        log.info("Menu label: " + mnuLabel);

        switch (mnuLabel) {
            case "pop_images":
                if (source.getText().compareTo("Remove") == 0) {
                    String imageName = tblImages.getModel().getValueAt(selRowImg, 1).toString();
                    HttpResult res = RestApi.DeleteImage(imageName);
                    ProcessResponse(res);
                    UpdateImages();
                }
                if (source.getText().compareTo("Remove By Index") == 0) {
                    String imageName = tblImages.getModel().getValueAt(selRowImg, 0).toString();
                    HttpResult res = RestApi.DeleteImage(imageName);
                    ProcessResponse(res);
                    UpdateImages();
                }
                if (source.getText().compareTo("Inspect") == 0) {
                    String imageName = tblImages.getModel().getValueAt(selRowImg, 1).toString();
                    HttpResult res = RestApi.InspectImage(imageName);
                    DisplayJsonResult(res);
                }
                break;

            case "pop_containers":
                if (source.getText().compareTo("Remove") == 0) {
                    String contName = tblContainers.getModel().getValueAt(selRowCont, 0).toString();
                    HttpResult res = RestApi.DeleteContainer(contName);
                    ProcessResponse(res);
                    UpdateContainers();
                }
                if (source.getText().compareTo("Start") == 0) {
                    String contId = tblContainers.getModel().getValueAt(selRowCont, 0).toString();
                    HttpResult res = RestApi.StartContainer(contId);
                    ProcessResponse(res);
                    UpdateContainers();
                }
                if (source.getText().compareTo("Stop") == 0) {
                    String contId = tblContainers.getModel().getValueAt(selRowCont, 0).toString();
                    HttpResult res = RestApi.StopContainer(contId);
                    ProcessResponse(res);
                    UpdateContainers();
                }
                if (source.getText().compareTo("Inspect") == 0) {
                    String contName = tblContainers.getModel().getValueAt(selRowCont, 0).toString();
                    HttpResult res = RestApi.InspectContainer(contName);
                    DisplayJsonResult(res);
                }
                break;
        }

    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void DisplayJsonResult(HttpResult res) {
        dlmResponse.clear();
        if (res.ResultCode < 300) {
            dlmResponse.addElement("Operation processed succesfully - result in Diagnostics window!");

            String[] result = res.Response.split(",");
            for (int x = 0; x < result.length; x++)
                dlmMainLog.addElement(result[x]);

        } else {
            dlmResponse.addElement("Operation failed:");
            dlmResponse.addElement(res.Response);
        }
    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void ProcessResponse(HttpResult res) {

        dlmResponse.clear();
        if (res.ResultCode < 300) {
            dlmResponse.addElement("Operation processed succesfully !");
        } else {
            dlmResponse.addElement("Operation failed:");
            dlmResponse.addElement(res.Response);
        }
    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void ImagesMouseHandler(MouseEvent e) {
        int button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            selRowImg = tblImages.rowAtPoint(e.getPoint());
            popImages.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void ContainersMouseHandler(MouseEvent e) {
        int button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            selRowCont = tblContainers.rowAtPoint(e.getPoint());
            popContainers.show(e.getComponent(), e.getX(), e.getY());
        }
    }


    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    public void UpdateImages() {
        HttpResult hres = HttpUtils.SendHttpGet("http://localhost:2375/images/json");
        if (hres.ResultCode == 200) {
            Vector<TabRowDataImages> images = JsonUtils.GetImageList(hres.Response);
            if (images != null) {
                tblImages.setModel(new TabModImages(images));
            } else {
                log.error("Error parsing image list");
            }
        } else {
            log.error(String.format("Error reading image list: RESULTCODE=%d", hres.ResultCode));
            dlmResponse.addElement(String.format("Error reading image list: RESULTCODE=%d", hres.ResultCode));
        }
    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    public void UpdateContainers() {
        HttpResult hres = HttpUtils.SendHttpGet("http://localhost:2375/containers/json?all=true");
        if (hres.ResultCode == 200) {
            Vector<TabRowDataContainersBrief> containers = JsonUtils.GetContainerList(hres.Response);
            if (containers != null) {
                tblContainers.setModel(new TabModContainersBrief(containers));
            } else {
                log.error("Error parsing container list");
            }
        } else {
            log.error(String.format("Error reading container list: RESULTCODE=%d", hres.ResultCode));
            dlmResponse.addElement(String.format("Error reading container list: RESULTCODE=%d", hres.ResultCode));
        }
    }

    /*----------------------------------------------------------------------

    ------------------------------------------------------------------------*/
    private void Updateregistry() {
        String sUrl = txRegistryUrl.getText() + "/v2/_catalog";
        HttpResult hres = HttpUtils.SendHttpGet(sUrl);
        if (hres.ResultCode == 200) {
            Vector<String> repos = JsonUtils.GetReistryRepoList(hres.Response);
            if (repos != null) {
                Vector<TabRowDataRegistryRepo> images = new Vector<>();
                dlmregistryRepos.clear();
                for (String repo : repos) {
                    dlmregistryRepos.addElement(repo);

                    sUrl = txRegistryUrl.getText() + "/v2/" + repo + "/tags/list";
                    hres = HttpUtils.SendHttpGet(sUrl);
                    if (hres.ResultCode == 200) {
                        Vector<String> tags = JsonUtils.GetImagesForRepo(hres.Response);
                        for (String tag : tags) {
                            images.add(new TabRowDataRegistryRepo(repo, tag));
                        }
                    } else {
                        images.add(new TabRowDataRegistryRepo(repo, "Unknown !"));
                    }
                }
                tblRegistryRepos.setModel(new TabModRegistryRepos(images));
            } else {
                log.error("Error parsing repos list");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error occurred - see the log in main tab !");
            log.error(String.format("Error reading registry repos list: RESULTCODE=%d", hres.ResultCode));
            dlmResponse.addElement(String.format("Error reading registry repos list: RESULTCODE=%d", hres.ResultCode));
            dlmResponse.addElement(String.format(hres.Response));
        }
    }


    /**************************************************************************
     *
     * @param args command line parameters
     *************************************************************************/
    public static void main(String[] args) {
        frame = new JFrame("Docker UI");
        frame.setContentPane(new frmMain().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1200, 800);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setBackground(new Color(-3088659));
        tabbedPane1.setEnabled(true);
        tabbedPane1.setForeground(new Color(-16316665));
        mainPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-15329253));
        tabbedPane1.addTab("Main", panel1);
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblImages = new JTable();
        tblImages.setBackground(new Color(-16316665));
        Font tblImagesFont = this.$$$getFont$$$("Courier New", Font.BOLD, 14, tblImages.getFont());
        if (tblImagesFont != null) tblImages.setFont(tblImagesFont);
        tblImages.setForeground(new Color(-13378553));
        tblImages.setSelectionBackground(new Color(-13421000));
        tblImages.setSelectionForeground(new Color(-13190380));
        scrollPane1.setViewportView(tblImages);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblContainers = new JTable();
        tblContainers.setBackground(new Color(-16316665));
        Font tblContainersFont = this.$$$getFont$$$("Courier New", Font.BOLD, 14, tblContainers.getFont());
        if (tblContainersFont != null) tblContainers.setFont(tblContainersFont);
        tblContainers.setForeground(new Color(-2430187));
        tblContainers.setSelectionBackground(new Color(-13486793));
        tblContainers.setSelectionForeground(new Color(-657395));
        scrollPane2.setViewportView(tblContainers);
        final JScrollPane scrollPane3 = new JScrollPane();
        panel1.add(scrollPane3, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lbResponse = new JList();
        lbResponse.setBackground(new Color(-16777216));
        Font lbResponseFont = this.$$$getFont$$$("Courier New", Font.BOLD, 14, lbResponse.getFont());
        if (lbResponseFont != null) lbResponse.setFont(lbResponseFont);
        lbResponse.setForeground(new Color(-792));
        lbResponse.setSelectionBackground(new Color(-14407895));
        lbResponse.setSelectionForeground(new Color(-1049089));
        scrollPane3.setViewportView(lbResponse);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Commands", panel2);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, Font.BOLD, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Build Image:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, Font.PLAIN, 14, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Image Name:");
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txImageName = new JTextField();
        Font txImageNameFont = this.$$$getFont$$$(null, -1, 14, txImageName.getFont());
        if (txImageNameFont != null) txImageName.setFont(txImageNameFont);
        panel2.add(txImageName, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, Font.PLAIN, 14, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("Dockerfile Path:");
        panel2.add(label3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txImageDockerfilePath = new JTextField();
        Font txImageDockerfilePathFont = this.$$$getFont$$$(null, -1, 14, txImageDockerfilePath.getFont());
        if (txImageDockerfilePathFont != null) txImageDockerfilePath.setFont(txImageDockerfilePathFont);
        panel2.add(txImageDockerfilePath, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnBrowseImgDockerfile = new JButton();
        btnBrowseImgDockerfile.setText("...");
        panel2.add(btnBrowseImgDockerfile, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnImageBuild = new JButton();
        btnImageBuild.setText("Build");
        panel2.add(btnImageBuild, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Diagnostics", panel3);
        btnClearMainLog = new JButton();
        btnClearMainLog.setText("Clear");
        panel3.add(btnClearMainLog, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        panel3.add(scrollPane4, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lbMainLog = new JList();
        lbMainLog.setBackground(new Color(-16316665));
        Font lbMainLogFont = this.$$$getFont$$$("Courier New", -1, 14, lbMainLog.getFont());
        if (lbMainLogFont != null) lbMainLog.setFont(lbMainLogFont);
        lbMainLog.setForeground(new Color(-2892564));
        scrollPane4.setViewportView(lbMainLog);
        btnUpdateAll = new JButton();
        btnUpdateAll.setText("Update");
        panel3.add(btnUpdateAll, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Registry", panel4);
        txRegistryUrl = new JTextField();
        Font txRegistryUrlFont = this.$$$getFont$$$(null, -1, 14, txRegistryUrl.getFont());
        if (txRegistryUrlFont != null) txRegistryUrl.setFont(txRegistryUrlFont);
        txRegistryUrl.setText("http://192.168.9.20:5000");
        panel4.add(txRegistryUrl, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnRegistryUpdate = new JButton();
        btnRegistryUpdate.setText("Update");
        panel4.add(btnRegistryUpdate, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        Font label4Font = this.$$$getFont$$$(null, Font.PLAIN, 14, label4.getFont());
        if (label4Font != null) label4.setFont(label4Font);
        label4.setText("Registry URL:");
        panel4.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lbRegistryRepos = new JList();
        lbRegistryRepos.setBackground(new Color(-16382199));
        Font lbRegistryReposFont = this.$$$getFont$$$("Courier New", Font.BOLD, 14, lbRegistryRepos.getFont());
        if (lbRegistryReposFont != null) lbRegistryRepos.setFont(lbRegistryReposFont);
        lbRegistryRepos.setForeground(new Color(-657395));
        lbRegistryRepos.setSelectionBackground(new Color(-16053475));
        lbRegistryRepos.setSelectionForeground(new Color(-657395));
        panel4.add(lbRegistryRepos, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JScrollPane scrollPane5 = new JScrollPane();
        panel4.add(scrollPane5, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblRegistryRepos = new JTable();
        tblRegistryRepos.setBackground(new Color(-16053475));
        Font tblRegistryReposFont = this.$$$getFont$$$("Courier New", Font.BOLD, 14, tblRegistryRepos.getFont());
        if (tblRegistryReposFont != null) tblRegistryRepos.setFont(tblRegistryReposFont);
        tblRegistryRepos.setForeground(new Color(-13190380));
        tblRegistryRepos.setSelectionBackground(new Color(-16053480));
        tblRegistryRepos.setSelectionForeground(new Color(-13190380));
        scrollPane5.setViewportView(tblRegistryRepos);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
