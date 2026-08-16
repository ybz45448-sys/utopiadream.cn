// ============================================================
// app/register/page.js — 注册页面
// ============================================================
// 独立的注册表单页，替代导航栏里原来的 prompt() 弹窗。
// 字段校验和后端 RegisterRequest 保持一致：
//   用户名 3-50 字符、密码 6-100 字符、昵称必填且 ≤50 字符。
// 注册成功后显示绿色提示 + 去登录链接（不自动登录）。
// ============================================================

'use client';

import { useState } from 'react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useAuth } from '@/app/context/AuthContext';

export default function RegisterPage() {
    let { register } = useAuth();

    // 表单状态
    let [username, setUsername] = useState('');
    let [password, setPassword] = useState('');
    let [confirmPassword, setConfirmPassword] = useState('');
    let [nickname, setNickname] = useState('');
    let [loading, setLoading] = useState(false);
    let [error, setError] = useState('');
    let [success, setSuccess] = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');

        // 前端校验：和后端 RegisterRequest 保持一致
        if (username.trim().length < 3) {
            setError('用户名至少需要 3 个字符');
            return;
        }
        if (password.length < 6) {
            setError('密码至少需要 6 个字符');
            return;
        }
        if (password !== confirmPassword) {
            setError('两次输入的密码不一致');
            return;
        }
        if (!nickname.trim()) {
            setError('昵称不能为空');
            return;
        }

        setLoading(true);
        // register() 返回 { success: true } 或 { success: false, message }
        let result = await register(username.trim(), password, nickname.trim());
        setLoading(false);

        if (result.success) {
            setSuccess(true);  // 显示"注册成功，去登录"
        } else {
            setError(result.message || '注册失败');
        }
    }

    return (
        <main className="max-w-md mx-auto px-5 py-16">
            <div className="border rounded-lg p-8">
                <h1 className="text-2xl font-bold mb-1">注册</h1>
                <p className="text-sm text-gray-500 mb-6">加入乌托邦开发者社区</p>

                {/* 错误提示：行内红色 */}
                {error && (
                    <p className="text-red-500 mb-4">{error}</p>
                )}

                {/* 成功提示：行内绿色 + 去登录 */}
                {success && (
                    <p className="text-green-600 mb-4">
                        注册成功！{" "}
                        <Link
                            href="/login"
                            className="text-purple-600 hover:underline"
                        >
                            去登录
                        </Link>
                    </p>
                )}

                {!success && (
                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-medium mb-2">
                                用户名
                            </label>
                            <Input
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                placeholder="3-50 个字符"
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
                                placeholder="至少 6 个字符"
                                autoComplete="new-password"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-2">
                                确认密码
                            </label>
                            <Input
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="再输一次密码"
                                autoComplete="new-password"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium mb-2">
                                昵称
                            </label>
                            <Input
                                value={nickname}
                                onChange={(e) => setNickname(e.target.value)}
                                placeholder="别人看到你的名字"
                                maxLength={50}
                            />
                        </div>

                        <Button type="submit" className="w-full" disabled={loading}>
                            {loading ? '注册中...' : '注册'}
                        </Button>
                    </form>
                )}

                <p className="text-sm text-gray-500 mt-6 text-center">
                    已有账号？{" "}
                    <Link
                        href="/login"
                        className="text-purple-600 hover:underline"
                    >
                        去登录
                    </Link>
                </p>
            </div>
        </main>
    );
}
