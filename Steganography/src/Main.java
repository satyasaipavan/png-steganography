public class Main {

    public static void main(String[] args) {
        //Testing area


        int a = 0b11110101101010100000000011111111;

        //11110101
        //10101010
        //00000000
        //11111111

        int r = (a >>> 16) & 0xFF;
        int g = (a >>> 8) & 0xFF;
        int b = a & 0xFF;


        System.out.println("Red" + r);
        System.out.println("Green" + g);
        System.out.println("Blue" + b);

        int all = 0b11111111111111111111111111111111;

        int shift16 = all >>> 16;
        int shift8 = all >>> 8;

        System.out.println("Shift16: " + shift16);
        System.out.println("Shift8 : " + shift8);
        System.out.println("Shift0 : " + all);


        int current = 2;
        //int m = (current <<< 1);
        System.out.println(current << 1);
        //System.out.println(current <<< 1);*/


    }
}
