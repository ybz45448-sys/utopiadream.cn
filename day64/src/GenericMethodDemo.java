class Util{
    public static <T> T getLast(T[] array){
        return array[array.length-1];
    }
}

public class GenericMethodDemo {
    public static void main(String[] args) {
        String[] strings = {"a", "b", "c"};
        System.out.println("最后一个" + Util.getLast(strings));

        Integer[] nums = {1, 2, 3};
        System.out.println("最后一个" + Util.getLast(nums));
    }
}
