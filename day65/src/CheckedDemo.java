// ============================================================
// CheckedDemo.java — 两种异常：一种必须处理，一种不用
// ============================================================

import java.io.IOException;

public class CheckedDemo {

    // 方法A：声称自己会抛 IOException
    static void readFile() throws IOException {
        throw new IOException("文件打不开");
    }

    // 方法B：运行时抛算术异常
    static void divide() {
        int x = 10 / 0;   // ArithmeticException
    }

    public static void main(String[] args) {
        // ① 调用方法A —— 不 try/catch，不声明，直接调
        //readFile();           // ❓ 这行编译能过吗？

        // ② 调用方法B —— 同样不 try/catch，直接调
        divide();             // ❓ 这行编译能过吗？
    }
}
