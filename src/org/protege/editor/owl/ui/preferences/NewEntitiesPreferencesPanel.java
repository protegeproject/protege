package org.protege.editor.owl.ui.preferences;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.model.entity.AutoIDGenerator;
import org.protege.editor.owl.model.entity.CustomLabelDescriptor;
import org.protege.editor.owl.model.entity.EntityCreationPreferences;
import org.protege.editor.owl.model.entity.IterativeAutoIDGenerator;
import org.protege.editor.owl.model.entity.LabelDescriptor;
import org.protege.editor.owl.model.entity.MatchRendererLabelDescriptor;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.entity.PseudoRandomAutoIDGenerator;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.util.SimpleIRIShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 24, 2008<br><br>
 *
 * see http://protegewiki.stanford.edu/index.php/Protege4NamingAndRendering
 */
public class NewEntitiesPreferencesPanel extends OWLPreferencesPanel {

    private Logger logger = Logger.getLogger(NewEntitiesPreferencesPanel.class);

    // URI panel
    private JRadioButton uriBaseActiveOntology;
    private JRadioButton uriBaseSpecifiedURI;
    private JTextField uriDefaultBaseField;
    private JRadioButton nameAsURIFragment;
    private JRadioButton autoIDURIFragment;
    private JRadioButton hashButton;
    private JRadioButton slashButton;
    private JRadioButton colonButton;

    // label panel
    private JCheckBox nameAsLabel;
    private JCheckBox autoIDLabel;
    private JTextField annotationURILabel;
    private JButton annotationSelectButton;
    private JComboBox annotationLangSelector;
    private IRI labelAnnotation = null;
    private JComponent customLabelPane;
    private JRadioButton sameAsRendererLabelButton;
    private JRadioButton customLabelButton;

    // auto ID panel
    private JRadioButton pseudoRandomButton;
    private JRadioButton iterativeButton;
    private JSpinner autoIDStart;
    private JSpinner autoIDEnd;
    private JSpinner autoIDDigitCount;
    private JTextField autoIDPrefix;
    private JTextField autoIDSuffix;

    private JComponent autoIDPane;

    private ChangeListener updateListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event) {
            refreshState();
        }
    };

    private JComponent labelOptionsPane;
    private JComponent rangePanel;

    private static final int VERTICAL_SPACE = 20;

    private static final int HORIZONTAL_SPACE = 25;

    private static final String SEP_HASH = "#";

    private static final String SEP_SLASH = "/";

    private static final String SEP_COLON = ":";

// Can't do this as the entity renderer always uses the current prefs
//    private JLabel exampleURILabel;


    public void initialise() throws Exception {
        JComponent uriPanel = createURIPanel();
        JComponent labelPane = createLabelPanel();
        autoIDPane = createAutoIDPanel();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(uriPanel);
        add(Box.createVerticalStrut(VERTICAL_SPACE));
        add(Box.createVerticalGlue());
        add(labelPane);
        add(Box.createVerticalStrut(VERTICAL_SPACE));
        add(autoIDPane);

        loadUIWithPrefs();

        setupChangeListeners();
    }


    private void loadUIWithPrefs() {
        uriBaseActiveOntology.setSelected(!EntityCreationPreferences.useDefaultBaseIRI());
        uriBaseSpecifiedURI.setSelected(EntityCreationPreferences.useDefaultBaseIRI());
        uriDefaultBaseField.setText(EntityCreationPreferences.getDefaultBaseIRI().toString());
        uriDefaultBaseField.setEnabled(EntityCreationPreferences.useDefaultBaseIRI());

        hashButton.setSelected(EntityCreationPreferences.getDefaultSeparator().equals(SEP_HASH));
        slashButton.setSelected(EntityCreationPreferences.getDefaultSeparator().equals(SEP_SLASH));
        colonButton.setSelected(EntityCreationPreferences.getDefaultSeparator().equals(SEP_COLON));

        nameAsURIFragment.setSelected(!EntityCreationPreferences.isFragmentAutoGenerated());
        nameAsLabel.setSelected(EntityCreationPreferences.isGenerateNameLabel());
        autoIDURIFragment.setSelected(EntityCreationPreferences.isFragmentAutoGenerated());
        autoIDLabel.setSelected(EntityCreationPreferences.isGenerateIDLabel());

        final Class<? extends LabelDescriptor> labelDescrCls = EntityCreationPreferences.getLabelDescriptorClass();
        sameAsRendererLabelButton.setSelected(labelDescrCls.equals(MatchRendererLabelDescriptor.class));
        customLabelButton.setSelected(labelDescrCls.equals(CustomLabelDescriptor.class));

        labelAnnotation = EntityCreationPreferences.getNameLabelIRI();
        if (labelAnnotation == null){
            labelAnnotation = IRI.create(OWLRDFVocabulary.RDFS_LABEL.getURI());
        }
        annotationURILabel.setText(labelAnnotation.toString());
        annotationLangSelector.setSelectedItem(EntityCreationPreferences.getNameLabelLang());

        final Class<? extends AutoIDGenerator> autoIDGenCls = EntityCreationPreferences.getAutoIDGeneratorClass();
        pseudoRandomButton.setSelected(autoIDGenCls.equals(PseudoRandomAutoIDGenerator.class));
        iterativeButton.setSelected(autoIDGenCls.equals(IterativeAutoIDGenerator.class));
        autoIDStart.setValue(EntityCreationPreferences.getAutoIDStart());
        autoIDEnd.setValue(EntityCreationPreferences.getAutoIDEnd());

        autoIDDigitCount.setValue(EntityCreationPreferences.getAutoIDDigitCount());
        autoIDPrefix.setText(EntityCreationPreferences.getPrefix());
        autoIDSuffix.setText(EntityCreationPreferences.getSuffix());

        refreshState();
    }

    private void setupChangeListeners(){
        uriBaseSpecifiedURI.addChangeListener(updateListener);
        autoIDURIFragment.addChangeListener(updateListener);
        hashButton.addChangeListener(updateListener);
        slashButton.addChangeListener(updateListener);
        colonButton.addChangeListener(updateListener);
        autoIDLabel.addChangeListener(updateListener);
        nameAsLabel.addChangeListener(updateListener);
        iterativeButton.addChangeListener(updateListener);
        customLabelButton.addChangeListener(updateListener);
    }

    private void refreshState() {
        uriDefaultBaseField.setEnabled(uriBaseSpecifiedURI.isSelected());

        nameAsLabel.setEnabled(!autoIDURIFragment.isSelected());

//        exampleURILabel.setText(generateExampleURI());

        performRecursiveEnable(labelOptionsPane, autoIDLabel.isSelected() || nameAsLabel.isSelected());

        final boolean enableAutoIDPane = autoIDLabel.isSelected() || autoIDURIFragment.isSelected();

        performRecursiveEnable(autoIDPane, enableAutoIDPane);
        if (autoIDURIFragment.isSelected()){
            nameAsLabel.setSelected(true); // user name always used for something
        }

        performRecursiveEnable(customLabelPane, customLabelButton.isSelected());

        performRecursiveEnable(rangePanel, enableAutoIDPane && iterativeButton.isSelected());
    }


    private String generateExampleURI() {
        try {
            OWLEntityCreationSet<OWLClass> entityCreationSet = getOWLModelManager().getOWLEntityFactory().preview(OWLClass.class, "TestName", null);
            return entityCreationSet.getOWLEntity().getURI().toString();
        }
        catch (OWLEntityCreationException e) {
            return "No URI could be created: " + e.getMessage();
        }
    }


    public void applyChanges() {
        EntityCreationPreferences.setUseDefaultBaseIRI(uriBaseSpecifiedURI.isSelected());
        try {
            IRI defaultBase = IRI.create(new URI(uriDefaultBaseField.getText()));
            EntityCreationPreferences.setDefaultBaseIRI(defaultBase);
        }
        catch (URISyntaxException e) {
            logger.error("Ignoring invalid base URI (" + uriDefaultBaseField.getText() + ")");
        }

        if (hashButton.isSelected()){
            EntityCreationPreferences.setDefaultSeparator(SEP_HASH);
        }
        else if (slashButton.isSelected()){
            EntityCreationPreferences.setDefaultSeparator(SEP_SLASH);
        }
        else if (colonButton.isSelected()){
            EntityCreationPreferences.setDefaultSeparator(SEP_COLON);
        }

        EntityCreationPreferences.setFragmentAutoGenerated(autoIDURIFragment.isSelected());

        EntityCreationPreferences.setGenerateNameLabel(nameAsLabel.isSelected());
        EntityCreationPreferences.setGenerateIDLabel(autoIDLabel.isSelected());

        if (sameAsRendererLabelButton.isSelected()){
            EntityCreationPreferences.setLabelDescriptorClass(MatchRendererLabelDescriptor.class);
        }
        if (customLabelButton.isSelected()){
            EntityCreationPreferences.setLabelDescriptorClass(CustomLabelDescriptor.class);
        }

        EntityCreationPreferences.setNameLabelIRI(IRI.create(annotationURILabel.getText()));
        Object lang = annotationLangSelector.getSelectedItem();
        if (lang != null && !lang.equals("")){
            EntityCreationPreferences.setNameLabelLang((String)lang);
        }
        else{
            EntityCreationPreferences.setNameLabelLang(null);
        }

        if (pseudoRandomButton.isSelected()){
            EntityCreationPreferences.setAutoIDGeneratorClass(PseudoRandomAutoIDGenerator.class);
        }
        if (iterativeButton.isSelected()){
            EntityCreationPreferences.setAutoIDGeneratorClass(IterativeAutoIDGenerator.class);
        }

        EntityCreationPreferences.setAutoIDStart((Integer)autoIDStart.getValue());
        EntityCreationPreferences.setAutoIDEnd((Integer)autoIDEnd.getValue());

        EntityCreationPreferences.setAutoIDDigitCount((Integer)autoIDDigitCount.getValue());
        EntityCreationPreferences.setPrefix(autoIDPrefix.getText());
        EntityCreationPreferences.setSuffix(autoIDSuffix.getText());
    }


    private JComponent createURIPanel() {
        JComponent c = createPane("Entity URI", BoxLayout.PAGE_AXIS);
        JComponent basePane = createBasePanel();
        JComponent separatorPane = createSeparatorPanel();
        JComponent namePane = createNamePanel();

//        exampleURILabel = new JLabel();
//        exampleURILabel.setFont(exampleURILabel.getFont().deriveFont(10.0f));
//        exampleURILabel.setForeground(Color.GRAY);

        c.add(basePane);
        c.add(separatorPane);
        c.add(namePane);
        c.add(Box.createVerticalStrut(VERTICAL_SPACE));
//        c.add(exampleURILabel);
        return c;
    }


    private JComponent createBasePanel() {
        JComponent c = createPane(null, BoxLayout.LINE_AXIS);

        uriBaseActiveOntology = new JRadioButton("active ontology URI");
        uriBaseSpecifiedURI = new JRadioButton("specified URI");
        uriDefaultBaseField = new JTextField(){
            public Dimension getPreferredSize() {
                return new Dimension(0, super.getPreferredSize().height);
            }
        };


        ButtonGroup bg = new ButtonGroup();
        bg.add(uriBaseActiveOntology);
        bg.add(uriBaseSpecifiedURI);

        c.add(new JLabel("Start with: "));
        c.add(uriBaseActiveOntology);
        c.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        c.add(uriBaseSpecifiedURI);
        c.add(uriDefaultBaseField);
        return c;
    }


    private JComponent createSeparatorPanel() {
        JComponent c = createPane(null, BoxLayout.LINE_AXIS);
        hashButton = new JRadioButton(SEP_HASH);
        slashButton = new JRadioButton(SEP_SLASH);
        colonButton = new JRadioButton(SEP_COLON);

        ButtonGroup bg = new ButtonGroup();
        bg.add(hashButton);
        bg.add(slashButton);
        bg.add(colonButton);

        c.add(new JLabel("Followed by: "));
        c.add(hashButton);
        c.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        c.add(slashButton);
        c.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        c.add(colonButton);

        return c;
    }

    private JComponent createNamePanel() {
        JComponent c = createPane(null, BoxLayout.LINE_AXIS);
        nameAsURIFragment = new JRadioButton("user supplied name");
        autoIDURIFragment = new JRadioButton("auto ID");

        ButtonGroup bg = new ButtonGroup();
        bg.add(nameAsURIFragment);
        bg.add(autoIDURIFragment);

        c.add(new JLabel("End with: "));
        c.add(nameAsURIFragment);
        c.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        c.add(autoIDURIFragment);
        return c;
    }


    private JComponent createLabelPanel() {
        JComponent c = createPane("Entity label", BoxLayout.PAGE_AXIS);
        nameAsLabel = new JCheckBox("create label using user supplied name");
        autoIDLabel = new JCheckBox("create label using auto ID");
        labelOptionsPane = createLabelOptionsPanel();

        c.add(nameAsLabel);
        c.add(autoIDLabel);
        c.add(Box.createVerticalStrut(12));
        c.add(labelOptionsPane);
        return c;
    }


    private JComponent createLabelOptionsPanel(){
        JComponent c = createPane(null, BoxLayout.PAGE_AXIS);

        // @@TODO should get an annotation property and render that
        sameAsRendererLabelButton = new JRadioButton("Same as label renderer (currently " +
                                                     new SimpleIRIShortFormProvider().getShortForm(getFirstRendererLabel()) + ")");
        customLabelButton = new JRadioButton("Custom label");

        ButtonGroup bg = new ButtonGroup();
        bg.add(sameAsRendererLabelButton);
        bg.add(customLabelButton);

        c.add(sameAsRendererLabelButton);
        c.add(customLabelButton);

        annotationURILabel = new JTextField(){
            public Dimension getPreferredSize() {
                return new Dimension(0, super.getPreferredSize().height);
            }
        };
        annotationURILabel.setEditable(false);
        annotationSelectButton = new JButton(new AbstractAction("..."){
            public void actionPerformed(ActionEvent event) {
                handleSelectAnnotation();
            }
        });
        annotationLangSelector = new UIHelper(getOWLEditorKit()).getLanguageSelector();

        JComponent labelAnnotationPane = new JPanel();
        labelAnnotationPane.setLayout(new BoxLayout(labelAnnotationPane, BoxLayout.LINE_AXIS));
        labelAnnotationPane.setAlignmentX(0.0f);
        labelAnnotationPane.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        labelAnnotationPane.add(new JLabel("URI "));
        labelAnnotationPane.add(annotationURILabel);
        labelAnnotationPane.add(annotationSelectButton);

        JComponent labelLanguagePane = new JPanel();
        labelLanguagePane.setLayout(new BoxLayout(labelLanguagePane, BoxLayout.LINE_AXIS));
        labelLanguagePane.setAlignmentX(0.0f);
        labelLanguagePane.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        labelLanguagePane.add(new JLabel("Lang"));
        labelLanguagePane.add(annotationLangSelector);
        labelLanguagePane.add(Box.createHorizontalGlue());

        customLabelPane = new JPanel();
        customLabelPane.setLayout(new BoxLayout(customLabelPane, BoxLayout.PAGE_AXIS));
        customLabelPane.setAlignmentX(0.0f);
        customLabelPane.add(labelAnnotationPane);
        customLabelPane.add(labelLanguagePane);

        c.add(customLabelPane);

        return c;
    }


    private JComponent createAutoIDPanel() {
        JComponent c = createPane("Auto ID", BoxLayout.PAGE_AXIS);

        pseudoRandomButton = new JRadioButton("Numeric (pseudo random)");
        pseudoRandomButton.setAlignmentX(0.0f);

        iterativeButton = new JRadioButton("Numeric (iterative)");
        iterativeButton.setAlignmentX(0.0f);

        autoIDStart = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        autoIDStart.setPreferredSize(new Dimension(60, 0));
        autoIDStart.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event) {
                if ((Integer)autoIDEnd.getValue() != -1 &&
                    (Integer)autoIDEnd.getValue() <= (Integer)autoIDStart.getValue()){
                    autoIDEnd.setValue(autoIDStart.getValue());
                }
            }
        });
        autoIDEnd = new JSpinner(new SpinnerNumberModel(-1, -1, Integer.MAX_VALUE, 1));
        autoIDEnd.setPreferredSize(new Dimension(60, 0));
        autoIDEnd.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent event) {
                if ((Integer)autoIDEnd.getValue() != -1 &&
                    (Integer)autoIDEnd.getValue() <= (Integer)autoIDStart.getValue()){
                    autoIDStart.setValue(autoIDEnd.getValue());
                }
            }
        });

        rangePanel = new JPanel();
        rangePanel.setLayout(new BoxLayout(rangePanel, BoxLayout.LINE_AXIS));
        rangePanel.setAlignmentX(0.0f);
        rangePanel.setBorder(new EmptyBorder(2, 20, 2, 2));
        rangePanel.add(new JLabel("Start"));
        rangePanel.add(autoIDStart);
        rangePanel.add(Box.createHorizontalGlue());
        rangePanel.add(new JLabel("End"));
        rangePanel.add(autoIDEnd);

        JComponent iterativePanel = new JPanel();
        iterativePanel.setLayout(new BoxLayout(iterativePanel, BoxLayout.LINE_AXIS));
        iterativePanel.add(iterativeButton);
        iterativePanel.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        iterativePanel.add(rangePanel);
        iterativePanel.setAlignmentX(0.0f);

        autoIDDigitCount = new JSpinner(new SpinnerNumberModel(6, 0, 255, 1));
        JComponent paddingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        paddingPanel.setAlignmentX(0.0f);
        paddingPanel.add(new JLabel("Digit count"));
        paddingPanel.add(autoIDDigitCount);

        autoIDPrefix = new JTextField();
        autoIDSuffix = new JTextField();
        JComponent xFixPanel = new JPanel();
        xFixPanel.setLayout(new BoxLayout(xFixPanel, BoxLayout.LINE_AXIS));
        xFixPanel.setAlignmentX(0.0f);
        xFixPanel.add(new JLabel("Prefix"));
        xFixPanel.add(autoIDPrefix);
        xFixPanel.add(Box.createHorizontalStrut(HORIZONTAL_SPACE));
        xFixPanel.add(new JLabel("Suffix"));
        xFixPanel.add(autoIDSuffix);

        ButtonGroup bg = new ButtonGroup();
        bg.add(pseudoRandomButton);
        bg.add(iterativeButton);

        c.add(pseudoRandomButton);
        c.add(iterativePanel);
        c.add(Box.createVerticalStrut(VERTICAL_SPACE));
        c.add(paddingPanel);
        c.add(xFixPanel);

        return c;
    }


    private JComponent createPane(String title, int orientation) {
        JComponent c = new Box(orientation){
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
            }
        };
        c.setAlignmentX(0.0f);
        if (title != null){
            c.setBorder(ComponentFactory.createTitledBorder(title));
        }
        return c;
    }


    protected void handleSelectAnnotation() {
        OWLAnnotationProperty prop = new UIHelper(getOWLEditorKit()).pickAnnotationProperty();
        if (prop != null){
            labelAnnotation = prop.getIRI();
            annotationURILabel.setText(labelAnnotation.toString());
        }
    }

    private void performRecursiveEnable(Component c, boolean enabled){
        c.setEnabled(enabled);
        if (c instanceof Container){
            for (Component child : ((Container)c).getComponents()){
                performRecursiveEnable(child, enabled);
            }
        }
    }

    public void dispose() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public IRI getFirstRendererLabel() {
        final java.util.List<IRI> iris = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        if (!iris.isEmpty()){
            return iris.get(0);
        }
        return IRI.create(OWLRDFVocabulary.RDFS_LABEL.getURI());
    }

//     public static void main(String[] args) {
//         NewEntitiesPreferencesPanel panel = new NewEntitiesPreferencesPanel(){
//             protected void handleSelectAnnotation() {
//                 System.out.println("Pick annotation");
//             }
//         };
//         try {
//             panel.initialise();
//             if (JOptionPaneEx.showConfirmDialog(null,
//                                                 "Prefs test",
//                                                 panel,
//                                                 JOptionPane.PLAIN_MESSAGE,
//                                                 JOptionPane.OK_CANCEL_OPTION,
//                                                 null) == JOptionPane.OK_OPTION){
//                 panel.applyChanges();
//             }
//         }
//         catch (Exception e) {
//             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//         }
//         System.exit(0);
//     }
}
