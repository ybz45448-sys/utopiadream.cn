import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import { fileURLToPath } from 'node:url';
import { transformWithOxc } from 'vite';

// 自定义插件：把 .js 文件按 JSX 重新转换
// 原因：Next.js 项目里 .js 文件可以包含 JSX，
// 但 Vitest 4 的 oxc 转换器默认认为 .js 不是 JSX，会报错。
const transformJsxInJs = () => ({
    name: 'transform-jsx-in-js',
    enforce: 'pre',  // 在 oxc 转换之前执行
    async transform(code, id) {
        // 只处理 .js 文件，跳过 .jsx、.tsx 和其他文件
        if (!id.match(/\.js$/)) {
            return null;
        }

        // 跳过 node_modules 里的文件（第三方库自己处理 JSX）
        if (id.includes('node_modules')) {
            return null;
        }

        // 跳过配置文件本身，避免递归转换
        if (id.includes('vitest.config.js') || id.includes('vitest.setup.js')) {
            return null;
        }

        // 用 JSX 模式重新转换这个 .js 文件
        return await transformWithOxc(code, id, {
            lang: 'jsx',
        });
    },
});

export default defineConfig({
    // React 插件：处理 JSX 转换
    // transformJsxInJs：先处理 .js 文件的 JSX 解析
    plugins: [react(), transformJsxInJs()],
    // 配置 @/ 路径别名，和 jsconfig.json 一致
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('.', import.meta.url)),
        },
    },
    test: {
        environment: 'jsdom',
        globals: true,
        include: ['lib/**/*.test.{js,jsx}', 'app/**/*.test.{js,jsx}'],
        setupFiles: ['./vitest.setup.js'],
    },
});
