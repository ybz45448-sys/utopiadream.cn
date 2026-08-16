// ============================================================
// app/publish/page.js — 发布新话题页面
// ============================================================
// 用 shadcn/ui 的 Input、Textarea、Select、Button 组件
// 调 topicApi.create() 把数据发到后端
// ============================================================

'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { topicApi } from '@/lib/api';

export default function PublishPage() {
    // 表单数据
    let [title, setTitle] = useState('');
    let [tag, setTag] = useState('');
    let [content, setContent] = useState('');
    let [loading, setLoading] = useState(false);

    let router = useRouter();

    async function handleSubmit(e) {
        e.preventDefault();

        if (!title.trim() || !content.trim()) {
            alert('标题和内容不能为空');
            return;
        }

        setLoading(true);

        try {
            // 调后端 API 创建话题
            // 作者由后端从 JWT 获取，前端不再传递 author
            await topicApi.create({
                title: title.trim(),
                content: content.trim(),
                tag: tag || '其他',
            });

            // 发布成功，跳转到讨论区
            router.push('/discussion');
        } catch (err) {
            alert('发布失败，请重试');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="max-w-3xl mx-auto px-5 py-8">
            <h1 className="text-2xl font-bold mb-6">📝 发布新话题</h1>

            <form onSubmit={handleSubmit} className="space-y-5">
                {/* 标题 */}
                <div>
                    <label className="block text-sm font-medium mb-1.5">话题标题</label>
                    <Input placeholder="例如：React 19 新特性分享" value={title}
                        onChange={e => setTitle(e.target.value)} />
                </div>

                {/* 分类选择 */}
                <div>
                    <label className="block text-sm font-medium mb-1.5">选择分类</label>
                    <Select value={tag} onValueChange={setTag}>
                        <SelectTrigger>
                            <SelectValue placeholder="选择分类" />
                        </SelectTrigger>
                        <SelectContent>
                            {['前端', '后端', 'AI', '数据库', '面试', '资源', '职业', '新手'].map(t => (
                                <SelectItem key={t} value={t}>{t}</SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                </div>

                {/* 内容 */}
                <div>
                    <label className="block text-sm font-medium mb-1.5">话题内容</label>
                    <Textarea placeholder="写下你想分享的内容..." value={content}
                        onChange={e => setContent(e.target.value)} rows={8} />
                </div>

                {/* 按钮 */}
                <div className="flex gap-3 justify-end">
                    <Button variant="outline" type="button" onClick={() => router.back()}>
                        取消
                    </Button>
                    <Button type="submit" disabled={loading}>
                        {loading ? '发布中...' : '发布话题'}
                    </Button>
                </div>
            </form>
        </div>
    );
}
