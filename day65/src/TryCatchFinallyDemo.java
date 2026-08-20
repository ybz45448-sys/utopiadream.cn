// ============================================================
// TryCatchFinallyDemo.java — try / catch / finally 执行顺序
// ============================================================

public class TryCatchFinallyDemo {
    public static void main(String[] args) {
        System.out.println("--- 情况1：正常执行，不抛异常 ---");
        testNormal();

        System.out.println("--- 情况2：抛异常，被 catch 接住 ---");
        testCatch();

        System.out.println("--- 情况3：finally 在 return 前还是后？ ---");
        System.out.println("testReturn 返回值: " + testReturn());
    }

    static void testNormal() {
        try {
            System.out.println("try: 执行");
        } catch (Exception e) {
            System.out.println("catch: 不执行");   // 没异常，这段会执行吗？
        } finally {
            System.out.println("finally: 一定执行吗？");
        }
    }

    static void testCatch() {
        try {
            System.out.println("try: 执行");
            int x = 10 / 0;                    // 💥 抛 ArithmeticException
        } catch (Exception e) {
            System.out.println("catch: 接住了 → " + e);
        } finally {
            System.out.println("finally: 一定执行吗？");
        }
    }

    static int testReturn() {
        try {
            return 1;
        } finally {
            System.out.println("finally: 我在 return 的什么时机跑？");
        }
    }
}
