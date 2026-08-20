// ============================================================
// RaceDemo.java — 竞态条件（Race Condition）
// ============================================================

public class RaceDemo {
    private static int count = 0;            // 两个线程共享的变量
    private static final int N = 10000;      // 每个线程加这么多次

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < N; i++) count++;     // t1 加 1 万次
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < N; i++) count++;     // t2 加 1 万次
        });

        t1.start();
        t2.start();
        t1.join();     // 等两个都跑完
        t2.join();

        System.out.println("count 最终值: " + count + "   (期望是 " + (2 * N) + ")" );
    }
}
