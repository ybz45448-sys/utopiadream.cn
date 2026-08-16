// ============================================================
// 登录页面组件测试
// 验证：空表单提示错误、填表后调 login()、失败显示错误
// ============================================================

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';

// mock useAuth：不真的调后端，login 用假函数
const mockLogin = vi.fn();
vi.mock('@/app/context/AuthContext', () => ({
    useAuth: () => ({
        user: null,
        login: mockLogin,
    }),
}));

// mock next/link：jsdom 里渲染成普通 <a>，避免依赖路由上下文
vi.mock('next/link', () => ({
    default: ({ href, children, ...props }) => (
        <a href={href} {...props}>{children}</a>
    ),
}));

// 页面引入的是 AuthContext 里的 useAuth，不是 @/lib/api
import LoginPage from '../page';

describe('登录页面', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('用户名密码为空时，提示错误且不调 login', async () => {
        render(<LoginPage />);

        fireEvent.click(screen.getByRole('button', { name: '登录' }));

        expect(screen.getByText('用户名不能为空')).toBeInTheDocument();
        expect(mockLogin).not.toHaveBeenCalled();
    });

    it('填写用户名密码后，调用 login 并传参正确', async () => {
        // login 成功：AuthContext 内部会存 token + 刷新页面
        mockLogin.mockResolvedValue({ success: true });

        render(<LoginPage />);

        fireEvent.change(screen.getByPlaceholderText('请输入用户名'), {
            target: { value: 'xiaoming' },
        });
        fireEvent.change(screen.getByPlaceholderText('请输入密码'), {
            target: { value: '123456' },
        });

        fireEvent.click(screen.getByRole('button', { name: '登录' }));

        await waitFor(() =>
            expect(mockLogin).toHaveBeenCalledWith('xiaoming', '123456')
        );
    });

    it('登录失败时，行内显示后端返回的错误信息', async () => {
        mockLogin.mockResolvedValue({
            success: false,
            message: '用户名或密码错误',
        });

        render(<LoginPage />);

        fireEvent.change(screen.getByPlaceholderText('请输入用户名'), {
            target: { value: 'xiaoming' },
        });
        fireEvent.change(screen.getByPlaceholderText('请输入密码'), {
            target: { value: 'wrong' },
        });

        fireEvent.click(screen.getByRole('button', { name: '登录' }));

        await screen.findByText('用户名或密码错误');
    });
});
