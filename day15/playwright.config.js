// Playwright E2E 测试配置
import { defineConfig } from '@playwright/test';

export default defineConfig({
    // 测试文件位置
    testDir: './e2e',

    // 每个测试都访问这个基础地址（前端 dev server）
    use: {
        baseURL: 'http://localhost:3000',
        // 测试失败时自动截图，方便排查
        screenshot: 'only-on-failure',
        // 测试失败时保留测试痕迹
        trace: 'retain-on-failure',
    },

    // 串行运行测试（E2E 之间有依赖，按顺序执行）
    workers: 1,
});
