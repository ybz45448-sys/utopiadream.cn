// ============================================================
// ThreadStateDemo.java — 用 getState() 亲眼看到状态流转
// ============================================================

public class ThreadStateDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(500);        // 子线程睡 0.5 秒（进入 TIMED_WAITING）
            } catch (InterruptedException e) { }
            System.out.println("子线程跑完了");
        });

        System.out.println("① 刚创建(还没start): " + t.getState());   // 猜: ?

        t.start();
        System.out.println("② start() 之后: " + t.getState());        // 猜: ?

        Thread.sleep(100);   // 主线程睡 100ms，让子线程先进入 sleep
        System.out.println("③ 子线程 sleep 中: " + t.getState());     // 猜: ?

        t.join();            // ⭐ 主线程【等】子线程跑完才继续
        System.out.println("④ join 等完后: " + t.getState());         // 猜: ?
    }
}
