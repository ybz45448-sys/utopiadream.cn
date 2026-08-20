import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamDemo {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));
        List<Integer> list2 = list.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .toList();
        System.out.println("结果:" + list2);
        System.out.println("原始结果:" + list);

        List<String> a = Arrays.asList("甲", "乙", "丙");
                a
                .stream()
                .filter(s -> s.length() == 1)   // 中间：过滤
                .forEach(s -> System.out.println("欢迎: " + s));

    }
}
