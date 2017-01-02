import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.file.Path;

/**
 * Created by Kyle Gough on 27/12/2016.
 * Steganography Embedder and Extractor - ©2016 Kyle Gough
 */
public class StegDialog extends JDialog {
    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JButton btnEmbedGetCarrier;
    private JTextField textEmbedCarrier;
    private JButton btnGetMessage;
    private JTextField textMessage;
    private JPanel panelMessage;
    private JPanel panelEmbedCarrier;
    private JPanel panelKey;
    private JComboBox comboEmbedAlgorithm;
    private JTextArea textAreaEmbedStats;
    private JButton btnEmbedGetOutputFolder;
    private JTextField textEmbedOutputPath;
    private JPanel panelOutput;
    private JRadioButton radioPNG;
    private JButton btnEmbed;
    private JPanel panelOutputFormat;
    private JPanel panelEmbedAlgorithm;
    private JPanel panelEmbedOutputPath;
    private JPanel panelEmbedStats;
    private JButton btnExtractGetCarrier;
    private JTextField textExtractCarrier;
    private JPanel panelExtractCarrier;
    private JPanel panelExtractAlgorithm;
    private JPanel panelExtractStats;
    private JComboBox comboExtractAlgorithm;
    private JTextArea textAreaExtractStats;
    private JButton btnExtract;
    private JButton btnExtractGetOutputFolder;
    private JTextField textExtractOutputPath;
    private JPanel panelExtractOutputPath;

    private StegFile messageFile;
    private StegCarrierImage carrierFileEmbed;
    private StegCarrierImage carrierFileExtract;

    private StegDialog() {
        setContentPane(contentPane);
        setModal(true);
        //getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        btnGetMessage.addActionListener(event -> getMessage());
        btnEmbedGetCarrier.addActionListener(event -> getCarrier(Operation.EMBED));
        btnEmbedGetOutputFolder.addActionListener(event -> getOutputFolder(Operation.EMBED));
        btnEmbed.addActionListener(event -> embedMessage());
        btnExtractGetCarrier.addActionListener(event -> getCarrier(Operation.EXTRACT));
        btnExtractGetOutputFolder.addActionListener(event -> getOutputFolder(Operation.EXTRACT));
        btnExtract.addActionListener(event -> extractMessage());

        addContextMenus();
    }

    private void addContextMenus() {
        JPopupMenu detailsEmbedPopupMenu = new JPopupMenu();
        JPopupMenu detailsExtractPopupMenu = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("Copy to Clipboard");
        menuItem.addActionListener(event ->
                {StringSelection selection = new StringSelection(textAreaEmbedStats.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);});
        detailsEmbedPopupMenu.add(menuItem);

        menuItem = new JMenuItem("Copy to Clipboard");
        menuItem.addActionListener(event ->
                {StringSelection selection = new StringSelection(textAreaExtractStats.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);});

        menuItem.addActionListener(event ->
                {StringSelection selection = new StringSelection(textAreaExtractStats.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);});

        detailsExtractPopupMenu.add(menuItem);

        menuItem = new JMenuItem("Clear");
        menuItem.addActionListener(event -> textAreaEmbedStats.setText(""));
        detailsEmbedPopupMenu.add(menuItem);

        menuItem = new JMenuItem("Clear");
        menuItem.addActionListener(event -> textAreaExtractStats.setText(""));
        detailsExtractPopupMenu.add(menuItem);

        textAreaEmbedStats.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    detailsEmbedPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        textAreaExtractStats.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    detailsExtractPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private enum Operation {
        EMBED, EXTRACT
    }

    //Opens a file chooser dialog to get the carrier file.
    private void getCarrier(Operation op) {
        JCarrierChooser carrierChooser = new JCarrierChooser();

        //Shows the dialog and changes the carrier text field is a file is chosen.
        int returnVal = carrierChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            //Selected file received from the file chooser.
            File chosenFile = carrierChooser.getSelectedFile();

            if (chosenFile.exists()) { //Checks if selected file exists.
                if (StegFile.getFileExtension(chosenFile).equals("png")) { //Valid PNG file
                    try {
                        StegCarrierImage testImg = new StegCarrierImage(chosenFile);
                        if (testImg.getImageTypeByteCount() != 0) {
                            if (op == Operation.EMBED) {
                                carrierFileEmbed = new StegCarrierImage(chosenFile);
                                textEmbedCarrier.setText(chosenFile.getPath());
                                setStatistics(op);
                            }
                            else {
                                carrierFileExtract = new StegCarrierImage(chosenFile);
                                textExtractCarrier.setText(chosenFile.getPath());
                                setStatistics(op);
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "- PNG Colour Model format was unrecognised.", "Error - Invalid PNG Type", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Unexpected IOException", "Error - Unexpected IOException", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else { //Invalid File Format
                    JOptionPane.showMessageDialog(this, "- Selected file's type is invalid.\n- Please choose PNG or BMP file format.", "Error - Invalid File Type", JOptionPane.ERROR_MESSAGE);
                }
            }
            else { //Files does not exist.
                JOptionPane.showMessageDialog(this, "- Chosen file does not exist.", "Error - File does not exist", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
            }
        }
    }

    //Opens a dialog to get the message file.
    private void getMessage() {
        //Creates the File Browser Dialog which filters only text files.
        JFileChooser messageChooser = new JFileChooser();

        //Only allows files and disallows the use of the All Files filter.
        messageChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        messageChooser.setDialogTitle("Select Message File");

        //Shows the dialog and changes the message text field is a file is chosen.
        int returnVal = messageChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            textMessage.setText(messageChooser.getSelectedFile().getPath());
            if (messageChooser.getSelectedFile().exists()) { //Checks if selected file exists.
                messageFile = new StegFile(messageChooser.getSelectedFile());
                setStatistics(Operation.EMBED);
            }
            else { //Files does not exist.
                JOptionPane.showMessageDialog(this, "- Chosen file does not exist.", "Error - File does not exist", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
            }
        }
    }

    //Opens a dialog to get the output folder path.
    private void getOutputFolder(Operation op) {
        //Creates the File Browser Dialog which filters only folders.
        JFileChooser outputChooser = new JFileChooser();

        //Only allows directories.
        outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputChooser.setDialogTitle("Select Output Path");

        //Shows the dialog and changes the message text field is a file is chosen.
        int returnVal = outputChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (outputChooser.getSelectedFile().exists()) { //Checks if selected directory exists.
                if (op == Operation.EMBED) {
                    textEmbedOutputPath.setText(outputChooser.getSelectedFile().getPath());
                }
                else {
                    textExtractOutputPath.setText(outputChooser.getSelectedFile().getPath());
                }
            }
            else { //Directory does not exist.
                JOptionPane.showMessageDialog(this, "- Chosen directory does not exist.", "Error - Directory does not exist", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
            }
        }
    }

    private void setStatistics(Operation op) {
        if (op == Operation.EMBED) {
            textAreaEmbedStats.setText("");
            addMessageStats();
            addCarrierImageStats(op);
        }
        else {
            textAreaExtractStats.setText("");
            addCarrierImageStats(op);
        }

    }

    //Adds information about the carrier image file to the details text field.
    private void addCarrierImageStats(Operation op) {
        if (op == Operation.EMBED) {
            if (carrierFileEmbed != null) {
                String carrierInfo = "Carrier Image: " + carrierFileEmbed.getFilePath() + "\nCarrier Dimensions: "
                        + carrierFileEmbed.getWidth() + " x " + carrierFileEmbed.getHeight() + "\nCarrier Image Colour Type: "
                        + carrierFileEmbed.getImageType()+ "\nAvailable space using 1 LSB: "
                        + NumberFormat.getIntegerInstance().format(carrierFileEmbed.getAvailableBytesLSD()) + " Bytes\nAvailable space using 2 LSB: "
                        + NumberFormat.getIntegerInstance().format(carrierFileEmbed.getAvailableBytes2LSD()) + " Bytes\n\n";
                appendStatistics(op, carrierInfo);
            }
        }
        else {
            if (carrierFileExtract != null) {
                String carrierInfo = "Carrier Image: " + carrierFileExtract.getFilePath() + "\nCarrier Image Dimensions: "
                        + carrierFileExtract.getWidth() + " x " + carrierFileExtract.getHeight() + "\nCarrier Image Colour Type: "
                        + carrierFileExtract.getImageType() + "\nCarrier File Size: "
                        + NumberFormat.getIntegerInstance().format(carrierFileExtract.getFileSize()) + " Bytes\n\n";
                appendStatistics(op, carrierInfo);
            }
        }
    }

    //Adds information about the message file to the details text field.
    private void addMessageStats() {
        if (messageFile != null) {
            String messageInfo = "Message File: " + messageFile.getFilePath() + "\nMessage File Size: "
            + NumberFormat.getIntegerInstance().format(messageFile.getFileSize()) + " Bytes\n\n";
            appendStatistics(Operation.EMBED, messageInfo);
        }
    }

    //Checks all embed fields have been appropriately assigned values.
    private String checkEmbedFields() {
        String error = "";
        if (messageFile == null) { //Message file
            error+= "- No message file has been set.\n";
        }
        if (carrierFileEmbed == null) { //Carrier file
            error+= "- No carrier file has been set.\n";
        }
        if (comboEmbedAlgorithm.getSelectedIndex() == 0) { //Algorithm selection
            error+= "- No algorithm has been chosen.\n";
        }
        if (textEmbedOutputPath.getText().equals("")) { //Output path
            error+= "- No output path has been set.";
        }
        return error;
    }

    //Checks all extract fields have been appropriately assigned values.
    private String checkExtractFields() {
        String error = "";
        if (carrierFileExtract == null) { //Carrier file
            error+= "- No carrier file has been set.\n";
        }
        if (comboExtractAlgorithm.getSelectedIndex() == 0) { //Algorithm selection
            error+= "- No algorithm has been chosen.\n";
        }
        if (textExtractOutputPath.getText().equals("")) { //Output path
            error+= "- No output path has been set.";
        }
        return error;
    }

    //Embeds the message file in the carrier image.
    private void embedMessage() {
        //Checks that all required fields have been completed, if not constructs an error message string to be displayed.
        String error = checkEmbedFields();
        if (!error.equals("")) {
            JOptionPane.showMessageDialog(this, error, "Error - Not all fields were completed", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
            return; //Exits the method as no all fields have been specified.
        }

        switch (comboEmbedAlgorithm.getSelectedIndex()) {
            case 1: //Least Significant Bit Algorithm.
                embedOneBitLSD();
                break;
            case 2: //Least Significant Two Bits
                embedTwoBitLSD();
                break;
            default:
                //Some error here about not choosing an option###
                break;
        }
    }

    //Extracts a message file from the carrier image.
    private void extractMessage() {
        //Checks that all required fields have been completed, if not constructs an error message string to be displayed.
        String error = checkExtractFields();
        if (!error.equals("")) {
            JOptionPane.showMessageDialog(this, error, "Error - Not all fields were completed", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
            return; //Exits the method as no all fields have been specified.
        }

        switch (comboExtractAlgorithm.getSelectedIndex()) {
            case 1: //Least Significant Bit Algorithm.
                int[] pixelShifts1LSD = {16,8,0};
                outputExtractedFile(MessageExtractor.extractLSD(carrierFileExtract, pixelShifts1LSD));
                break;
            case 2: //Least Significant Two Bits Algorithm.
                int[] pixelShifts2LSD = {17,16,9,8,1,0};
                outputExtractedFile(MessageExtractor.extractLSD(carrierFileExtract, pixelShifts2LSD));
                break;
            default:
                //Some error here about not choosing an option###
                break;
        }
    }

    private void outputExtractedFile(byte[] outputData) {

        appendStatistics(Operation.EXTRACT, "\nExtract Algorithm: " + comboExtractAlgorithm.getSelectedItem().toString() + "\n");
        appendStatistics(Operation.EXTRACT, "Extracted File Size: " + NumberFormat.getIntegerInstance().format(outputData.length) + " Bytes\n");

        //Attempts to get the MIME type of the extracted file.
        String mimeType;
        try {
            ByteArrayInputStream byteStream = new ByteArrayInputStream(outputData);
            mimeType = URLConnection.guessContentTypeFromStream(byteStream);
        }
        catch (IOException e) {
            e.printStackTrace();
            appendStatistics(Operation.EXTRACT, "Unexpected IOException whilst converting embedded data to file.\n\n");
            JOptionPane.showMessageDialog(this, "Unexpected IOException whilst converting embedded data to file.", "Error - IOException", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //No mime type was detected.
        if (mimeType == null) {
            mimeType = "UNKNOWN";
        }

        appendStatistics(Operation.EXTRACT, "Detected File Mime Type: " + mimeType + "\n");

        //Gets a unique file name for the extracted file using the Unix timestamp.
        long unixTime = System.currentTimeMillis() / 1000L; //Gets the current Unix Time.
        String filePath = textExtractOutputPath.getText() + "\\stegextract" + unixTime; //Unique filename based off of Unix Time.

        //Uses a file output stream to create the file from the byte array.
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(outputData);
            fileOutputStream.close();
            appendStatistics(Operation.EXTRACT, "Extracted file output to: " + filePath + "\n\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            appendStatistics(Operation.EXTRACT, "Unexpected IOException whilst converting embedded data to file.\n\n");
            JOptionPane.showMessageDialog(this, "Unexpected IOException whilst converting embedded data to file.", "Error - IOException", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void embedOneBitLSD() {
        if (carrierFileEmbed.getAvailableBytesLSD() > messageFile.getFileSize()) { //###change when add more file types, or remove if deciding not to add more for the carrier type.
            appendStatistics(Operation.EMBED, "\nEmbedding Message file...\n");
            byte[] fileData;

            try {
                fileData = messageFile.getAllBytes();
            }
            catch (IOException e) {
                e.printStackTrace();
                appendStatistics(Operation.EMBED, "Unexpected IOException caused process to fail.");
                JOptionPane.showMessageDialog(this, "- Message file caused an IOException.", "Error - Unexpected IOException", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
                return;
            }

            BufferedImage imageCopy = StegCarrierImage.deepCopy(carrierFileEmbed);
            int[] pixelShifts = {65536,256,1};
            BufferedImage processedIImage = MessageEmbedder.leastOneSigBit(fileData, imageCopy, pixelShifts); //Applies the algorithm to the carrier image.
            outputFinalImage(processedIImage);
        }
        else {
            JOptionPane.showMessageDialog(this, "- The message file is too large to fit in the carrier file.", "Error - Message file too large", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
        }
    }

    private void embedTwoBitLSD() {
        if (carrierFileEmbed.getAvailableBytes2LSD() > messageFile.getFileSize()) { //###change when add more file types, or remove if deciding not to add more for the carrier type.
            appendStatistics(Operation.EMBED, "\nEmbedding Message file...\n");
            byte[] fileData;

            try {
                fileData = messageFile.getAllBytes();
            }
            catch (IOException e) {
                e.printStackTrace();
                appendStatistics(Operation.EMBED, "Unexpected IOException caused process to fail.");
                JOptionPane.showMessageDialog(this, "- Message file caused an IOException.", "Error - Unexpected IOException", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
                return;
            }

            BufferedImage imageCopy = StegCarrierImage.deepCopy(carrierFileEmbed);
            int[] pixelShifts = {131072,65536,512,256,2,1};
            BufferedImage processedIImage = MessageEmbedder.leastTwoSigBit(fileData, imageCopy, pixelShifts); //Applies the algorithm to the carrier image.
            outputFinalImage(processedIImage);
        }
        else {
            JOptionPane.showMessageDialog(this, "- The message file is too large to fit in the carrier file.", "Error - Message file too large", JOptionPane.ERROR_MESSAGE); //Displays an error message to the user.
        }
    }

    private void outputFinalImage(BufferedImage finalImage) {
        appendStatistics(Operation.EMBED, "Embed Algorithm: " + comboEmbedAlgorithm.getSelectedItem().toString() + "\n");

        long unixTime = System.currentTimeMillis() / 1000L; //Gets the current Unix Time.
        String filePath = textEmbedOutputPath.getText() + "\\stegembed" + unixTime; //Unique filename based off of Unix Time.

        appendStatistics(Operation.EMBED, "Attempting to output image in PNG format.\n");
        try {
            ImageIO.write(finalImage, "png", new File(filePath + ".png"));
            appendStatistics(Operation.EMBED, "Image successfully output to " + filePath + ".png\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            appendStatistics(Operation.EMBED, "Error encountered whilst outputting image.\n");
        }
    }

    private void appendStatistics(Operation op, String newText) {
        if (op == Operation.EMBED) {
            textAreaEmbedStats.setText(textAreaEmbedStats.getText() + newText);
        }
        else {
            textAreaExtractStats.setText(textAreaExtractStats.getText() + newText);
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        StegDialog dialog = new StegDialog();
        dialog.pack();

        //Adds items to the embed algorithm combo box.
        dialog.comboEmbedAlgorithm.addItem("Select an Algorithm:");
        dialog.comboEmbedAlgorithm.addItem("Least Significant Bit");
        dialog.comboEmbedAlgorithm.addItem("Least Significant Two Bits");
        dialog.comboEmbedAlgorithm.setSelectedIndex(0);

        //Adds items to the extract algorithm combo box.
        dialog.comboExtractAlgorithm.addItem("Select an Algorithm:");
        dialog.comboExtractAlgorithm.addItem("Least Significant Bit");
        dialog.comboExtractAlgorithm.addItem("Least Significant Two Bits");
        dialog.comboExtractAlgorithm.setSelectedIndex(0);

        //Adds a border with text to various embed tab panels in the dialog.
        dialog.panelMessage.setBorder(BorderFactory.createTitledBorder("Message File"));
        dialog.panelEmbedCarrier.setBorder(BorderFactory.createTitledBorder("Carrier Image"));
        dialog.panelEmbedAlgorithm.setBorder(BorderFactory.createTitledBorder("Steganography Algorithm"));
        dialog.panelEmbedOutputPath.setBorder(BorderFactory.createTitledBorder("Output Path"));
        dialog.panelEmbedStats.setBorder(BorderFactory.createTitledBorder("Details"));

        //Adds a border with text to various extract tab panels in the dialog.
        dialog.panelExtractCarrier.setBorder(BorderFactory.createTitledBorder("Carrier Image"));
        dialog.panelExtractAlgorithm.setBorder(BorderFactory.createTitledBorder("Steganography Algorithm"));
        dialog.panelExtractOutputPath.setBorder(BorderFactory.createTitledBorder("Output Path"));
        dialog.panelExtractStats.setBorder(BorderFactory.createTitledBorder("Details"));

        //Adds the directory icon to the buttons.
        dialog.btnEmbedGetCarrier.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        dialog.btnEmbedGetOutputFolder.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        dialog.btnGetMessage.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        dialog.btnExtractGetCarrier.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        dialog.btnExtractGetOutputFolder.setIcon(UIManager.getIcon("FileView.directoryIcon"));

        //Sets the title of the dialog.
        dialog.setTitle("Steganography Embedder and Extractor - ©2017 Kyle Gough");

        //Disables window resizing.
        dialog.setResizable(false);

        dialog.setVisible(true);
        System.exit(0);
    }
}
