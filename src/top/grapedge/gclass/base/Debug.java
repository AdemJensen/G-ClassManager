package top.grapedge.gclass.base;

/**
 * @program: G-ClassManager
 * @description: 调试信息
 * @author: Grapes
 * @create: 2019-03-11 13:01
 **/
public class Debug {
    public static void log(Object o) {
        System.out.println(o);
    }

    public static int outLength = 30;
    public static void out(Object o) {
        String msg = o.toString();
        if (msg.length() <= outLength - 2) {
            var t = (outLength - msg.length()) / 2;
            for (int i = 0; i < t; i++) System.out.print("=");
            System.out.print(msg);
            for (int i = 0; i < t; i++) System.out.print("=");
            System.out.println();
        }
    }

    public static void error(Object o) {
        System.out.print("Error: " + o);
    }
}
