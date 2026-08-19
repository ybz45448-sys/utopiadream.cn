// ============================================================
// WildcardSuperDemo.java — 下界通配符 ? super
// ============================================================

import java.util.ArrayList;
import java.util.List;

public class WildcardSuperDemo {
    static class Animal {}
    static class Dog extends Animal {}
    static class Cat extends Animal {}

    public static void main(String[] args) {
        // 下界：列表装的是 Dog 或它的父类（Dog / Animal / Object）
        List<? super Dog> list = new ArrayList<Animal>();

        // 能写吗？
        list.add(new Dog());        // ❓ 这行呢？
        // list.add(new Cat());     // ❓ 这行呢？

        // 能读吗？
        // Dog d = list.get(0);     // ❓ 这行呢？
        Object o = list.get(0);     // 这行一定编译，先不管
    }
}
