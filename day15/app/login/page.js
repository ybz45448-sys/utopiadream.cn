// ============================================================
// app/login/page.js — 登录页面
// ============================================================
// 独立的登录表单页，替代导航栏里原来的 prompt() 弹窗。
// 调 useAuth() 的 login()：成功时 AuthContext 会存 token 并刷新页面，
// 所以这里只需要处理失败的情况（行内红色错误提示）。
// ============================================================

'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useAuth } from '@/app/context/AuthContext';

export default function LoginPage() {
    let { user, login } = useAuth();

    // 表单状态
    let [username, setUsername] = useState('');
    let [password, setPassword] = useState('');
    let [loading, setLoading] = useState(false);
    let [error, setError] = useState('');

    // 已经登录的用户直接看"已登录"，不再展示登录表单
    if (user) {
        return (
            <main className="max-w-md mx-auto px-5 py-16 text-center">
                <h1 className="text-2xl font-bold mb-4">你已经登录了</h1>
                <p className="text-gray-500 mb-6">当前账号：{user.name}</p>
                <Link href="/" className="text-purple-600 hover:underline">
                    返回首页
                </Link>
            </main>
        );
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');

        // 前端校验：和后端 LoginRequest 保持一致
        if (!username.trim()) {
            setError('用户名不能为空');
            return;
        }
        if (!password) {
            setError('密码不能为空');
            return;
        }

        setLoading(true);
        // login() 成功时内部会存 token + location.reload() 刷新页面
        // 失败时返回 { success: false, message }
        let result = await login(username.trim(), password);
        setLoading(false);

        if (!result.success) {
            setError(result.message || '登录失败');
        }
    }

    return (
        <main className="max-w-md mx-auto px-5 py-16">
            <div className="border rounded-lg p-8">
                <h1 className="text-2xl font-bold mb-1">登录</h1>
                <p className="text-sm text-gray-500 mb-6">回到乌托邦开发者社区</p>

                {/* 错误提示：行内红色 */}
                {error && (
                    <p className="text-red-500 mb-4">{error}</p>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-sm font-medium mb-2">
                            用户名
                        </label>
                        <Input
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="请输入用户名"
                            autoComplete="username"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-2">
                            密码
                        </label>
                        <Input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="请输入密码"
                            autoComplete="current-password"
                        />
                    </div>

                    <Button type="submit" className="w-full" disabled={loading}>
                        {loading ? '登录中...' : '登录'}
                    </Button>
                </form>

                <p className="text-sm text-gray-500 mt-6 text-center">
                    还没有账号？{" "}
                    <Link
                        href="/register"
                        className="text-purple-600 hover:underline"
                    >
                        去注册
                    </Link>
                </p>
            </div>
        </main>
    );
}
