// ============================================================
// lib/time.js 的单元测试
// 验证 formatRelativeTime 的各种输入情况
// ============================================================

import { describe, it, expect, vi, afterEach } from 'vitest';
import { formatRelativeTime } from '../time';

describe('formatRelativeTime 相对时间格式化', () => {
    // 每个测试后恢复真实时钟，避免影响其他测试
    afterEach(() => {
        vi.useRealTimers();
    });

    it('空值/非法值返回空串（不抛异常）', () => {
        expect(formatRelativeTime(null)).toBe('');
        expect(formatRelativeTime(undefined)).toBe('');
        expect(formatRelativeTime('')).toBe('');
        expect(formatRelativeTime('not-a-date')).toBe('');
    });

    it('几分钟前返回带"前"的中文相对时间', () => {
        // 固定"当前时间"为 2026-08-16 12:00 UTC
        vi.useFakeTimers();
        vi.setSystemTime(new Date('2026-08-16T12:00:00+00:00'));

        // 输入 5 分钟前的时间戳（ISO-8601 字符串，和后端返回一致）
        let result = formatRelativeTime('2026-08-16T11:55:00+00:00');

        expect(result).toContain('前');
        expect(result).toContain('分钟');
    });

    it('几天前返回带"天"的中文相对时间', () => {
        vi.useFakeTimers();
        vi.setSystemTime(new Date('2026-08-16T12:00:00+00:00'));

        // 输入 3 天前的时间戳
        let result = formatRelativeTime('2026-08-13T12:00:00+00:00');

        expect(result).toContain('天');
    });

    it('未来时间不会因为负数而报错（返回"后"）', () => {
        vi.useFakeTimers();
        vi.setSystemTime(new Date('2026-08-16T12:00:00+00:00'));

        // 输入 1 小时后的时间戳（异常数据，但函数不应崩溃）
        let result = formatRelativeTime('2026-08-16T13:00:00+00:00');

        expect(typeof result).toBe('string');
        expect(result.length).toBeGreaterThan(0);
    });
});
