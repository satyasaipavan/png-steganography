import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by Kyle Gough on 29/12/2016.
 * Steganography Embedder and Extractor - ©2016 Kyle Gough
 */
class JCarrierChooser extends JFileChooser {


    JCarrierChooser() {
        //Sets the filter to only PNG files. ###
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "png");
        setFileFilter(filter);

        //Only allows files and disallows the use of the All Files filter.
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        setAcceptAllFileFilterUsed(false);
        setDialogTitle("Select Carrier Image File");
    }

}
