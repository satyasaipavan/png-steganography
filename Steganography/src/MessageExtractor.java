/**
 * Created by Kyle Gough on 29/12/2016.
 * Steganography Embedder and Extractor - ©2016 Kyle Gough
 */

class MessageExtractor {

    static byte[] extractLSD(StegCarrierImage carrierImage, int[] pixelShifts) {
        int width = carrierImage.getWidth();
        int height = carrierImage.getHeight();
        int channelCount = 3; //###
        int byteCount = (int)Math.floor((double)(width * height * channelCount) / 8);
        byte[] outputFile = new byte[byteCount];
        int byteIndex = 0;
        int bitIndex = 0;
        int currentByte = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                int pixelRGB = carrierImage.getRGB(i,j);

                for (int k : pixelShifts) {
                    if (byteIndex < byteCount) {
                        int channel = (pixelRGB >>> k) & 0x1;
                        if (bitIndex < 7) {
                            currentByte = currentByte | channel; //###
                            currentByte = currentByte << 1;
                            bitIndex++;
                        } else {
                            currentByte = currentByte | channel; //###
                            outputFile[byteIndex++] = (byte) currentByte;
                            currentByte = 0;
                            bitIndex = 0;
                        }
                    }
                }
            }
        }

        return trimExtract(outputFile);
    }

    private static byte[] trimExtract(byte[] extractedData) {
        int endIndex = extractedData.length; //Index of the ending byte (-1)

        for (int i = extractedData.length - 1; i >= 0; i--) {
            if (extractedData[i] != 0) {
                endIndex = i;
                break;
            }
        }

        byte[] trimmedData = new byte[endIndex];
        System.arraycopy(extractedData,0,trimmedData,0,endIndex);
        return trimmedData;
    }

}