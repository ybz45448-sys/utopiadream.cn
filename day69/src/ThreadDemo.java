// ============================================================
// ThreadDemo.java — 创建线程的三种方式
// ============================================================

public class ThreadDemo {
    public static void main(String[] args) {
        // 方式1：继承 Thread，重写 run()
        Thread t1 = new Thread() {
            @Override
            public void run() {
                System.out.println("方式1: 继承 Thread, 我在跑");
            }
        };
        t1.start();          // ⭐ start() 开新线程

        // 方式2：实现 Runnable，传给 Thread 构造器
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("方式2: 实现 Runnable, 我在跑");
            }
        };
        Thread t2 = new Thread(task);
        t2.start();

        // 方式3：Lambda 一行（Runnable 是函数式接口——第 66 天学的正好用上）
        Thread t3 = new Thread(() -> System.out.println("方式3: Lambda, 我在跑"));
        t3.start();

        System.out.println("main: 我是主线程");
    }
}
