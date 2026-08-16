// ============================================================
// 注册页面组件测试
// 验证：前端校验、两次密码一致、注册成功/失败提示
// ============================================================

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';

// mock useAuth：不真的调后端，register 用假函数
const mockRegister = vi.fn();
vi.mock('@/app/context/AuthContext', () => ({
    useAuth: () => ({
        register: mockRegister,
    }),
}));

// mock next/link：jsdom 里渲染成普通 <a>，避免依赖路由上下文
vi.mock('next/link', () => ({
    default: ({ href, children, ...props }) => (
        <a href={href} {...props}>{children}</a>
    ),
}));

import RegisterPage from '../page';

describe('注册页面', () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    it('用户名太短时，提示错误且不调 register', async () => {
        render(<RegisterPage />);

        fireEvent.click(screen.getByRole('button', { name: '注册' }));

        expect(screen.getByText('用户名至少需要 3 个字符')).toBeInTheDocument();
        expect(mockRegister).not.toHaveBeenCalled();
    });

    it('两次密码不一致时，提示错误且不调 register', async () => {
        render(<RegisterPage />);

        fireEvent.change(screen.getByPlaceholderText('3-50 个字符'), {
            target: { value: 'xiaoming' },
        });
        fireEvent.change(screen.getByPlaceholderText('至少 6 个字符'), {
            target: { value: '123456' },
        });
        fireEvent.change(screen.getByPlaceholderText('再输一次密码'), {
            target: { value: '654321' },
        });

        fireEvent.click(screen.getByRole('button', { name: '注册' }));

        expect(screen.getByText('两次输入的密码不一致')).toBeInTheDocument();
        expect(mockRegister).not.toHaveBeenCalled();
    });

    it('填写合法信息后，调用 register 并传参正确', async () => {
        mockRegister.mockResolvedValue({ success: true });

        render(<RegisterPage />);

        fireEvent.change(screen.getByPlaceholderText('3-50 个字符'), {
            target: { value: 'xiaoming' },
        });
        fireEvent.change(screen.getByPlaceholderText('至少 6 个字符'), {
            target: { value: '123456' },
        });
        fireEvent.change(screen.getByPlaceholderText('再输一次密码'), {
            target: { value: '123456' },
        });
        fireEvent.change(screen.getByPlaceholderText('别人看到你的名字'), {
            target: { value: '小明' },
        });

        fireEvent.click(screen.getByRole('button', { name: '注册' }));

        await waitFor(() =>
            expect(mockRegister).toHaveBeenCalledWith('xiaoming', '123456', '小明')
        );

        // 注册成功后显示绿色提示
        expect(screen.getByText('注册成功！')).toBeInTheDocument();
    });

    it('注册失败时，行内显示错误信息', async () => {
        mockRegister.mockResolvedValue({
            success: false,
            message: '用户名已存在',
        });

        render(<RegisterPage />);

        fireEvent.change(screen.getByPlaceholderText('3-50 个字符'), {
            target: { value: 'xiaoming' },
        });
        fireEvent.change(screen.getByPlaceholderText('至少 6 个字符'), {
            target: { value: '123456' },
        });
        fireEvent.change(screen.getByPlaceholderText('再输一次密码'), {
            target: { value: '123456' },
        });
        fireEvent.change(screen.getByPlaceholderText('别人看到你的名字'), {
            target: { value: '小明' },
        });

        fireEvent.click(screen.getByRole('button', { name: '注册' }));

        await screen.findByText('用户名已存在');
    });
});
