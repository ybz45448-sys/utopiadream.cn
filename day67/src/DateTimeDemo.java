// ============================================================
// DateTimeDemo.java — java.time 全家福
// ============================================================

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeDemo {
    public static void main(String[] args) {
        // ① 本地日期 / 日期时间（不带时区）
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        System.out.println("today: " + today);
        System.out.println("now:   " + now);

        // ② 手动指定（参数就是"人类能看懂"的写法）
        LocalDate birth = LocalDate.of(2004, 5, 20);
        System.out.println("birth: " + birth);

        // ③ Instant：世界统一时刻（UTC 时间戳，不受时区影响）
        Instant instant = Instant.now();
        System.out.println("instant: " + instant);

        // ④ 格式化：LocalDateTime → 自定义字符串
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        System.out.println("格式化: " + now.format(fmt));

        // ⑤ 解析：字符串 → LocalDateTime
        LocalDateTime parsed = LocalDateTime.parse("2026-08-19T10:30:00");
        System.out.println("解析: " + parsed);
    }
}

