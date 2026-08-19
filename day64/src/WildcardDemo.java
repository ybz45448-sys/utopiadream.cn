import java.util.ArrayList;
import java.util.List;

public class WildcardDemo {
    static class Animal {}
    static class Dog extends Animal {}
    static class Cat extends Animal {}

    public static void main(String[] args) {
        // 不变性：这行编译错（上一题）
        List<Dog> dogs = new ArrayList<>();
        // List<Animal> animals = dogs;    // ❌

        // ✅ 上界通配符：装"Animal 或其某种子类"的列表，谁都能接受
        List<? extends Animal> list = dogs;   // 现在 list 可以指向 dogs

        // 能读吗？
        Animal a = list.get(0);     // ✅ 读出来一定是 Animal，安全

        // 能写吗？下面两行呢？
        //list.add(new Dog());     // ❓
        //list.add(new Cat());     // ❓
    }
}

