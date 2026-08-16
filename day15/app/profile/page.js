"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { userApi } from "@/lib/api";
import { useAuth } from "@/app/context/AuthContext";

export default function ProfilePage() {
    let router = useRouter();
    let { user, updateUser } = useAuth();

    let [nickname, setNickname] = useState("");
    let [avatar, setAvatar] = useState("");
    let [bio, setBio] = useState("");
    let [loading, setLoading] = useState(true);
    let [saving, setSaving] = useState(false);
    let [message, setMessage] = useState("");
    let [error, setError] = useState("");

    useEffect(function() {
        async function loadProfile() {
            try {
                // userApi 会自动携带当前 Token
                let profile = await userApi.getMe();

                // 后端返回错误对象时，主动处理
                if (profile.error || profile.success === false) {
                    throw new Error(profile.message || "无法获取用户资料");
                }

                setNickname(profile.nickname || "");
                setAvatar(profile.avatar || "");
                setBio(profile.bio || "");

                // 用后端最新资料同步 AuthContext 和 localStorage
                updateUser(profile);
            } catch (error) {
                console.error("加载个人资料失败:", error);
                setError("登录状态已失效，请重新登录");

                // 资料页无法访问时返回首页
                router.push("/");
            } finally {
                setLoading(false);
            }
        }

        loadProfile();
    }, [router, updateUser]);

    async function handleSubmit(event) {
        event.preventDefault();

        setMessage("");
        setError("");

        if (!nickname.trim()) {
            setError("昵称不能为空");
            return;
        }

        if (avatar.length > 500) {
            setError("头像链接不能超过 500 个字符");
            return;
        }

        if (bio.length > 500) {
            setError("个人简介不能超过 500 个字符");
            return;
        }

        try {
            setSaving(true);

            let result = await userApi.updateProfile({
                nickname: nickname.trim(),
                avatar: avatar.trim(),
                bio: bio.trim()
            });

            if (result.success === false || result.error) {
                throw new Error(result.message || "保存失败");
            }

            // 使用后端保存后的真实数据同步全局状态
            updateUser(result);
            setNickname(result.nickname || "");
            setAvatar(result.avatar || "");
            setBio(result.bio || "");
            setMessage("资料保存成功");
        } catch (error) {
            console.error("保存个人资料失败:", error);
            setError(error.message || "保存失败");
        } finally {
            setSaving(false);
        }
    }

    if (loading) {
        return (
            <main className="max-w-2xl mx-auto px-5 py-10">
                <p>正在加载个人资料...</p>
            </main>
        );
    }

    if (!user) {
        return (
            <main className="max-w-2xl mx-auto px-5 py-10">
                <p>请先登录</p>
            </main>
        );
    }

    return (
        <main className="max-w-2xl mx-auto px-5 py-10">
            <div className="border rounded-lg p-6">
                <h1 className="text-2xl font-bold mb-6">
                    个人资料
                </h1>

                {error && (
                    <p className="text-red-500 mb-4">
                        {error}
                    </p>
                )}

                {message && (
                    <p className="text-green-600 mb-4">
                        {message}
                    </p>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-sm font-medium mb-2">
                            用户名
                        </label>

                        {/* username 是登录身份，今天只读不允许修改 */}
                        <Input
                            value={user.username || ""}
                            disabled
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-2">
                            昵称
                        </label>

                        <Input
                            value={nickname}
                            onChange={event => setNickname(event.target.value)}
                            maxLength={50}
                            placeholder="请输入昵称"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-2">
                            头像链接
                        </label>
                        <Input
                            value={avatar}
                            onChange={event => setAvatar(event.target.value)}
                            maxLength={500}
                            placeholder="请输入头像链接"
                        />
                        {avatar && (
                            <img
                                src={avatar}
                                alt="头像"
                                className="mt-2 h-16 w-16 rounded-full object-cover"
                            />
                        )}
                        </div>
                    <div>
                        <label className="block text-sm font-medium mb-2">
                            个人简介
                        </label>
                        <textarea
                            className="w-full rounded-md border px-3 py-2 text-sm"
                            value={bio}
                            onChange={event => setBio(event.target.value)}
                            maxLength={500}
                            placeholder="请输入个人简介"
                        />
                    </div>

                    <div className="flex gap-3">
                        <Button type="submit" disabled={saving}>
                            {saving ? "保存中..." : "保存资料"}
                        </Button>

                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => router.back()}
                        >
                            返回
                        </Button>
                    </div>
                </form>
            </div>
        </main>
    );
}
