import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SetTest {
    public static void main(String[] args) {
        // 两个"学号都是 1"的学生，逻辑上是同一个人
        Student s1 = new Student(1, "xiaoming");
        Student s2 = new Student(1, "xiaoming");

        Set<Student> set = new HashSet<>();
        set.add(s1);
        set.add(s2);

        System.out.println("set size = " + set.size());

        // 三个小侦探，看它们分别怎么判断
        System.out.println("s1 == s2      ? " + (s1 == s2));          // 地址比较
        System.out.println("s1.equals(s2) ? " + s1.equals(s2));      // 内容比较？
    }
}

// 注意：这个 Student 类【没有】重写 hashCode / equals
class Student {
    private int id;
    private String name;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;              // 同一个对象（地址一样）→ true
        if (o == null || getClass() != o.getClass()) return false;  // 类型不同 → false
        Student s = (Student) o;
        return id == s.id && name.equals(s.name);  // ⭐ 核心：学号和姓名都一样才算同一个人
    }

    @Override
    public int hashCode() {
        // ⭐ 必须和 equals 用【同一批字段】：
        //   equals 用 id 和 name 判断，hashCode 也得用 id 和 name 算
        //   这样"equals 认为相同"的两个对象，hashCode 也必然相同
        return Objects.hash(id, name);   // java.util.Objects 的静态工具，一行算好
    }


}
