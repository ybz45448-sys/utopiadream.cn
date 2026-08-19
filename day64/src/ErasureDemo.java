// ============================================================
// ErasureDemo.java — 类型擦除：运行时没有泛型
// ============================================================

import java.util.ArrayList;
import java.util.List;

public class ErasureDemo {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        List<Integer> integers = new ArrayList<>();

        // ⭐ 运行时：两个 List 是【同一个类】
        System.out.println("getClass 相同? " + (strings.getClass() == integers.getClass()));
        // ↑ 输出 true！String 列表和 Integer 列表运行时一模一样

        System.out.println(strings.getClass());
        // ↑ 输出 class java.util.ArrayList —— 看不到 <String>，泛型被擦掉了

        // ⭐ 更直观：不带尖括号的"原始类型"List，运行时塞啥都行
        List raw = new ArrayList();      // raw = 原始类型
        raw.add("你好");                  // 放 String
        raw.add(42);                     // 放 Integer —— 运行时根本不拦！
        System.out.println("raw 装的东西: " + raw);
    }
}
