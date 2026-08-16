// ============================================================
// app/not-found.js — 全局 404 页面
// ============================================================
// 用户访问不存在的路径时自动显示
// ============================================================

import Link from 'next/link';
import { Button } from '@/components/ui/button';

export default function NotFound() {
    return (
        <div className="max-w-3xl mx-auto px-5 py-20 text-center">
            <div className="text-8xl font-bold text-gray-200 mb-4">404</div>
            <div className="text-6xl mb-4">🏜️</div>
            <h2 className="text-2xl font-bold mb-2">页面不存在</h2>
            <p className="text-gray-500 mb-8">你访问的页面可能已被删除或链接有误</p>
            <Button>
                <Link href="/">← 返回首页</Link>
            </Button>
        </div>
    );
}
