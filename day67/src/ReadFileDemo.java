// ============================================================
// ReadFileDemo.java — 同一个文件，字节流 vs 字符流两种读法
// ============================================================

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class ReadFileDemo {
    public static void main(String[] args) throws IOException {
        // 先准备一个 UTF-8 编码的中文文件
        //（Files.writeString 是工具类，今天先当"写文件的捷径"用，明天细讲）
        Files.writeString(Path.of("demo.txt"), "你好\n世界\n", StandardCharsets.UTF_8);

        // ① 字节流：把文件当"一串字节数字"读
        System.out.println("--- ① 字节流 InputStream ---");
        InputStream in = new FileInputStream("demo.txt");
        int b;
        while ((b = in.read()) != -1) {      // read() 一次读 1 个字节，读到结尾返回 -1
            System.out.print(b + " ");
        }
        in.close();                          // 用完必须关（第 65 天讲过的清理）
        System.out.println();

        // ② 字符流：把文件当"文本"读，一次读一行
        System.out.println("--- ② 字符流 BufferedReader ---");
        BufferedReader reader = new BufferedReader(
                new FileReader("demo.txt", StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {   // 读一行，读完结尾返回 null
            System.out.println("读到: " + line);
        }
        reader.close();
    }
}
