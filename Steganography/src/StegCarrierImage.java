import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.image.WritableRaster;

/**
 * Created by Kyle Gough on 27/12/2016.
 * Steganography Embedder and Extractor - ©2016 Kyle Gough
 */
class StegCarrierImage extends StegFile {

    private BufferedImage img;
    private final int bufferSize = 3; //Number of bytes required in image to store additional information. Requires one byte, but specified as 2 for safety reasons.

    StegCarrierImage(File imgFile) throws IOException {
        super(imgFile);
        this.img = ImageIO.read(imgFile);
    }

    long getAvailableBytesLSD() {
        return (long)(img.getHeight() * img.getWidth() * 3 / 8) - bufferSize;
    }

    long getAvailableBytes2LSD() {
        return (long)(img.getHeight() * img.getWidth() * 3 / 4) - bufferSize;
    }

    int getRGB(int x, int y) {
        return img.getRGB(x,y);
    }

    int getHeight() {
        return img.getHeight();
    }

    int getWidth() {
        return img.getWidth();
    }

    String getImageType() {
        switch (img.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR:
                return "3 Byte BGR";
            case BufferedImage.TYPE_4BYTE_ABGR:
                return "4 Byte ABGR";
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                return "4 Byte ABGR PRE";
            case BufferedImage.TYPE_BYTE_BINARY:
                return "Byte Binary";
            case BufferedImage.TYPE_BYTE_GRAY:
                return "Byte Gray";
            case BufferedImage.TYPE_BYTE_INDEXED:
                return "Byte Indexed";
            case BufferedImage.TYPE_CUSTOM:
                return "Custom";
            case BufferedImage.TYPE_INT_ARGB:
                return "ARGB";
            case BufferedImage.TYPE_INT_ARGB_PRE:
                return "ARGB PRE";
            case BufferedImage.TYPE_INT_BGR:
                return "BGR";
            case BufferedImage.TYPE_INT_RGB:
                return "RGB";
            case BufferedImage.TYPE_USHORT_555_RGB:
                return "UShort 555 RGB";
            case BufferedImage.TYPE_USHORT_565_RGB:
                return "UShort 565 RGB";
            case BufferedImage.TYPE_USHORT_GRAY:
                return "UShort Gray";
            default:
                return "";
        }
    }

    int getImageTypeByteCount() {
        switch (img.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR:
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
            case BufferedImage.TYPE_INT_BGR:
            case BufferedImage.TYPE_INT_RGB:
                return 3;
            default:
                return 0;
        }
    }

    static BufferedImage deepCopy(StegCarrierImage oldImage) {
        boolean isAlphaPremultiplied = oldImage.img.getColorModel().isAlphaPremultiplied();
        WritableRaster raster = oldImage.img.copyData(null);
        BufferedImage newImage = new BufferedImage(oldImage.img.getColorModel(),raster,isAlphaPremultiplied,null);
        return newImage;
    }

}
