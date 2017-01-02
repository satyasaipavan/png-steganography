import java.awt.image.BufferedImage;

/**
 * Created by Kyle Gough on 27/12/2016.
 * Steganography Embedder and Extractor - ©2016 Kyle Gough
 */
class MessageEmbedder {

    private static byte[] addBuffer(byte[] messageNoBuffer) {
        byte[] message = new byte[messageNoBuffer.length + 1];
        System.arraycopy(messageNoBuffer, 0, message, 0, messageNoBuffer.length);
        message[messageNoBuffer.length] = (byte)0xFF; //Buffer Byte to identify the end of transmission.
        return message;
    }

    static BufferedImage leastOneSigBit(byte[] messageContentsNoBuffer, BufferedImage outputImage, int[] pixelShifts) {
        byte[] messageContents = addBuffer(messageContentsNoBuffer);
        int rgbMask = 0xFFFEFEFE;
        return leastSigBit(messageContents, outputImage, rgbMask, pixelShifts);
    }

    static BufferedImage leastTwoSigBit(byte[] messageContentsNoBuffer, BufferedImage outputImage, int[] pixelShifts) {
        byte[] messageContents = addBuffer(messageContentsNoBuffer);
        int rgbMask = 0xFFFCFCFC;
        return leastSigBit(messageContents, outputImage, rgbMask, pixelShifts);
    }

    private static BufferedImage leastSigBit(byte[] messageContents, BufferedImage outputImage, int rgbMask, int[] pixelMasks) {
        int currentByte = 0;
        int currentByteIndex = 7;

        //Steganography goes from left to right, top to bottom. Same as english text.
        for (int i = 0; i < outputImage.getWidth(); i++) { //Traverses the entire width of the carrier/output image.
            for (int j = 0; j < outputImage.getHeight(); j++) { //Traverses the entire height of the carrier/output image.

                int newRGB = rgbMask & outputImage.getRGB(i,j); //Applies the mask to the oldRGB value;

                for (int k : pixelMasks) {
                    if (currentByte < messageContents.length) {
                        if (((messageContents[currentByte] >> currentByteIndex) & 1) == 1) {
                            newRGB = k | newRGB;
                        }
                    }
                    if (--currentByteIndex < 0) {
                        currentByteIndex = 7;
                        currentByte++;
                    }
                }

                outputImage.setRGB(i,j,newRGB); //Applies the new pixel colours to the image.
            }
        }
        return outputImage;
    }

    //###Algorithm Pixel Swap http://paper.ijcsns.org/07_book/201008/20100825.pdf
    //###some algorithms don't need key
    //###add popup dialog if algorithm needs key with features to type it in or randomly generate one.

    //PNG
    //RGBA, RGB, Gray, Gray with Alpha
}