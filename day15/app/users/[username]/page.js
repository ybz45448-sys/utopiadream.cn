"use client";

import { use, useEffect, useState } from "react";
import Link from "next/link";
import { userApi } from "@/lib/api";
import { Button } from "@/components/ui/button";

export default function PublicUserPage({ params }) {
    let { username } = use(params);

    let [profile, setProfile] = useState(null);
    let [loading, setLoading] = useState(true);
    let [error, setError] = useState("");

    useEffect(function() {
        async function loadProfile() {
            try {
                let result = await userApi.getProfile(username);

                if (result.error || result.success === false) {
                    throw new Error(result.message || "用户不存在");
                }

                setProfile(result);
            } catch (error) {
                console.error("加载公开资料失败:", error);
                setError(error.message || "加载用户资料失败");
            } finally {
                setLoading(false);
            }
        }

        loadProfile();
    }, [username]);

    if (loading) {
        return (
            <main className="max-w-3xl mx-auto px-5 py-10">
                <p>正在加载用户资料...</p>
            </main>
        );
    }

    if (error || !profile) {
        return (
            <main className="max-w-3xl mx-auto px-5 py-10 text-center">
                <p className="text-red-500 mb-4">
                    {error || "用户不存在"}
                </p>

                <Button asChild>
                    <Link href="/discussion">
                        返回讨论区
                    </Link>
                </Button>
            </main>
        );
    }

    return (
        <main className="max-w-3xl mx-auto px-5 py-10">
            <div className="border rounded-lg p-6">
                <div className="flex items-center gap-4 mb-6">
                    {profile.avatar ? (
                        <img
                            src={profile.avatar}
                            alt={`${profile.nickname || profile.username} 的头像`}
                            className="h-24 w-24 rounded-full object-cover"
                        />
                    ) : (
                        <div className="h-24 w-24 rounded-full bg-purple-100 flex items-center justify-center text-2xl font-bold">
                            {(profile.nickname || profile.username)[0].toUpperCase()}
                        </div>
                    )}

                    <div>
                        <h1 className="text-2xl font-bold">
                            {profile.nickname || profile.username}
                        </h1>

                        <p className="text-sm text-gray-500">
                            @{profile.username}
                        </p>
                    </div>
                </div>

                <div className="border-t pt-5">
                    <h2 className="font-semibold mb-2">
                        个人简介
                    </h2>

                    <p className="text-gray-600 whitespace-pre-line">
                        {profile.bio || "这个用户还没有填写个人简介"}
                    </p>
                </div>
            </div>
        </main>
    );
}
