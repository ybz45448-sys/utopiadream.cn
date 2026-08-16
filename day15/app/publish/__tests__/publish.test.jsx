// 发布话题表单测试
// 验证：空标题/空内容时不提交，有内容时提交
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';

// 创建一个共享的 router mock 对象
// 让组件内部和测试里拿到的是同一个实例，才能断言 push 被调用
const mockRouter = {
    push: vi.fn(),
    back: vi.fn(),
};

// mock useRouter：始终返回同一个 router 实例
vi.mock('next/navigation', () => ({
    useRouter: () => mockRouter,
}));

// mock topicApi.create：不真的调后端
vi.mock('@/lib/api', () => ({
    topicApi: {
        create: vi.fn(),
    },
}));

// 保存 mock 引用，测试里能断言
import { topicApi } from '@/lib/api';
import PublishPage from '../page';

describe('发布话题表单', () => {
    beforeEach(() => {
        // 每个测试前清空 mock 调用记录
        vi.clearAllMocks();

        // mock alert，jsdom 没有 alert
        global.alert = vi.fn();
    });

    it('空标题和空内容时，提示错误且不提交', async () => {
        // 渲染页面
        render(<PublishPage />);

        // 点击提交按钮
        fireEvent.click(screen.getByRole('button', { name: '发布话题' }));

        // 断言：弹出了校验错误提示
        expect(global.alert).toHaveBeenCalledWith('标题和内容不能为空');

        // 断言：没有调用创建 API
        expect(topicApi.create).not.toHaveBeenCalled();
    });

    it('填写标题和内容后，调用创建 API 并跳转', async () => {
        // 准备：localStorage 里有用户
        localStorage.setItem('user', JSON.stringify({ name: 'ceshi' }));

        render(<PublishPage />);

        // 填写标题
        fireEvent.change(screen.getByPlaceholderText(/React 19/), {
            target: { value: '测试话题标题' },
        });

        // 填写内容
        fireEvent.change(screen.getByPlaceholderText(/写下你想分享的内容/), {
            target: { value: '这是测试正文' },
        });

        // 点击提交
        fireEvent.click(screen.getByRole('button', { name: '发布话题' }));

        // 等待异步操作完成
        await screen.findByText('发布中...');

        // 断言：调用了创建 API，参数正确
        // 注意：author 已移除，作者由后端从 JWT 获取
        expect(topicApi.create).toHaveBeenCalledWith({
            title: '测试话题标题',
            content: '这是测试正文',
            tag: '其他',   // 没选分类时默认 '其他'
        });

        // 断言：跳转到了讨论区
        expect(mockRouter.push).toHaveBeenCalledWith('/discussion');
    });
});
