// api.js 的 request 函数测试
// 验证：URL 拼接、Token 自动携带、JSON 解析
// 从 vitest 显式导入，不依赖 globals 配置
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('topicApi.getAll', () => {
    // 每个测试前：重置 fetch mock，清空 localStorage
    beforeEach(() => {
        vi.restoreAllMocks();
        localStorage.clear();
    });

    it('应使用正确的后端 URL', async () => {
        // 准备：mock fetch，返回空 JSON
        global.fetch = vi.fn().mockResolvedValue({
            ok: true,
            text: async () => '{}',
        });

        // 动态导入 api.js（request 未导出，通过 topicApi 触发）
        const { topicApi } = await import('../api.js');
        await topicApi.getAll();

        // 验证 fetch 被调用，URL 正确
        expect(global.fetch).toHaveBeenCalledWith(
            'http://localhost:8080/api/topics?page=1&pageSize=10',
            expect.any(Object)
        );
    });

    it('localStorage 有 Token 时，请求头带 Bearer', async () => {
        // 准备：放一个 Token，mock fetch
        localStorage.setItem('token', 'my-secret-token');
        global.fetch = vi.fn().mockResolvedValue({
            ok: true,
            text: async () => '{}',
        });

        const { topicApi } = await import('../api.js');
        await topicApi.getAll();

        // 取出 fetch 的第二个参数（options）
        const fetchOptions = global.fetch.mock.calls[0][1];

        // 验证 Authorization 头
        expect(fetchOptions.headers['Authorization'])
            .toBe('Bearer my-secret-token');
    });

    it('无 Token 时，请求头不带 Authorization', async () => {
        // 不设置 token
        global.fetch = vi.fn().mockResolvedValue({
            ok: true,
            text: async () => '{}',
        });

        const { topicApi } = await import('../api.js');
        await topicApi.getAll();

        const fetchOptions = global.fetch.mock.calls[0][1];

        // 验证没有 Authorization 头
        expect(fetchOptions.headers['Authorization']).toBeUndefined();
    });
});
