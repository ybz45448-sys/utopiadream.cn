// ============================================================
// OffsetDateTimeDemo.java — 项目 created_at 的类型 + 跨时区
// ============================================================

import java.time.*;

public class OffsetDateTimeDemo {
    public static void main(String[] args) {
        // ① 模拟项目里存的 created_at：一个带 +08:00 偏移的时刻
        OffsetDateTime createdAt = OffsetDateTime.of(
                2026, 8, 19, 21, 43, 19, 0, ZoneOffset.ofHours(8));
        System.out.println("数据库存的: " + createdAt);

        // ② 同一个时刻，换成纽约用户看
        ZonedDateTime userView = createdAt.atZoneSameInstant(ZoneId.of("America/New_York"));
        System.out.println("纽约用户看到: " + userView);

        // ③ 无论在哪时区，这个时刻本身是唯一的
        System.out.println("时刻唯一: " + createdAt.toInstant());
    }
}
