// ============================================================
// lib/time.js — 相对时间格式化工具
// ============================================================
// 后端存的是带时区的时间戳（created_at），序列化成 ISO-8601 字符串，
// 例如 "2026-08-16T10:30:00+08:00"。
// 前端拿到后用 date-fns 换算成中文相对时间："3 分钟前"、"昨天"、"2 个月前"。
// ============================================================

import { formatDistanceToNow } from 'date-fns';
import { zhCN } from 'date-fns/locale';

// value 可能来自老数据或缺失字段：
//   - null / undefined / ""（老数据、字段缺失）
//   - 非法字符串（后端改了格式）
// 守卫：任何异常情况都返回空串，页面不崩、不显示乱七八糟的内容
export function formatRelativeTime(value) {
    if (!value) return '';
    let date = new Date(value);
    if (Number.isNaN(date.getTime())) return '';
    return formatDistanceToNow(date, { addSuffix: true, locale: zhCN });
}
