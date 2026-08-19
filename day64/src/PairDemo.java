// ============================================================
// PairDemo.java — 多个类型参数
// ============================================================

// 一个"键值对"类：K 是键类型，V 是值类型
class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey()   { return key; }
    public V getValue() { return value; }
}

public class PairDemo {
    public static void main(String[] args) {
        // 学号 → 姓名 的键值对：键是 Integer，值是 String
        Pair<Integer, String> pair = new Pair<>(2026001, "小明");

        Integer id   = pair.getKey();    // 不用强转
        String  name = pair.getValue();  // 不用强转

        System.out.println(id + " → " + name);   // 2026001 → 小明
    }
}
