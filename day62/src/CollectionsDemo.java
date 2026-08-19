import java.util.*;

public class CollectionsDemo {
    public static void main(String[] args){

        //list类型 有序 可重复 有索引
        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("a");
        System.out.println("list内容" + list);
        System.out.println(list.size());
        System.out.println("第一个list:" + list.get(0));

        //set类型 无序不可重复
        Set<String> set = new HashSet<>();
        set.add("小明");
        set.add("小红");
        set.add("小红");
        System.out.println("set内容" + set);
        System.out.println("set长度" + set.size());
        System.out.println("set含小红?" + set.contains("小红"));

        //Map 键值对类型 键唯一
        Map<String,Integer> map = new HashMap<>();
        map.put("apple", 3);
        map.put("orange", 4);
        map.put("banana", 5);
        System.out.println("map内容" + map);
        System.out.println("map大小" + map.size());
        System.out.println("apple次数" + map.get("apple"));

        //遍历map值
        for(String key : map.keySet()){
            System.out.println("键:" + key + "---值:" + map.get(key));
        }

        for (Map.Entry<String,Integer> entry : map.entrySet()){
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

}
