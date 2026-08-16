// ============================================================
// app/page.js — 首页
// ============================================================
// 页面结构：Hero 大图区 → 分类卡片 → CTA 号召区
// 使用 shadcn/ui 的 Card 组件替代手写卡片
// ============================================================

import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

// 分类数据
const categories = [
    { name: '前端开发',   icon: '🌐', desc: 'HTML / CSS / JavaScript / React' },
    { name: '后端开发',   icon: '⚙️', desc: 'Java / Spring Boot / 微服务' },
    { name: '人工智能',   icon: '🤖', desc: '机器学习 / 深度学习 / AIGC' },
    { name: '数据库',     icon: '🗄️', desc: 'MySQL / PostgreSQL / Redis' },
    { name: '职业发展',   icon: '💼', desc: '面试 / 简历 / 职场经验' },
    { name: '资源分享',   icon: '📁', desc: '免费 API / 工具 / 学习资料' },
];

export default function HomePage() {
    return (
        <div>
            {/* ===== Hero 大图区 ===== */}
            <section className="bg-gradient-to-br from-purple-600 to-purple-900 text-white py-20 px-5 text-center">
                <div className="max-w-3xl mx-auto">
                    <h1 className="text-4xl md:text-5xl font-bold mb-4">🚀 乌托邦开发者社区</h1>
                    <p className="text-lg md:text-xl opacity-90 mb-8">
                        一个公益、开放、自由的技术交流平台
                    </p>
                    <div className="flex justify-center gap-4 flex-wrap">
                        <Button size="lg" variant="secondary">
                            <Link href="/publish">✍️ 发布话题</Link>
                        </Button>
                        <Button size="lg" variant="secondary">
                            <Link href="/discussion">📖 浏览讨论</Link>
                        </Button>
                    </div>
                </div>
            </section>

            {/* ===== 分类卡片区 ===== */}
            <section className="max-w-5xl mx-auto px-5 py-12">
                <h2 className="text-2xl font-bold mb-2">📂 讨论分类</h2>
                <p className="text-gray-500 mb-8">选择你感兴趣的领域，开始交流学习</p>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {categories.map(cat => (
                        <Card key={cat.name} className="hover:shadow-lg hover:-translate-y-1 transition-all cursor-pointer">
                            <CardContent className="p-6 text-center">
                                <div className="text-4xl mb-3">{cat.icon}</div>
                                <h4 className="font-semibold mb-1">{cat.name}</h4>
                                <p className="text-sm text-gray-500">{cat.desc}</p>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </section>

            {/* ===== CTA 号召区 ===== */}
            <section className="bg-white py-16 px-5 text-center border-t">
                <h2 className="text-3xl font-bold mb-4">💡 准备好了吗？</h2>
                <p className="text-gray-500 mb-8">无论你是新手还是大牛，这里都有属于你的舞台。</p>
                <Button  size="lg">
                    <Link href="/discussion">🚀 开始探索</Link>
                </Button>
            </section>
        </div>
    );
}
