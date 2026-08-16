// E2E：注册 + 登录 + 登录态保持
// 用 Playwright 打开真实浏览器，走完整用户流程
// 现在的注册/登录是独立页面（/register、/login），不再是 prompt 弹窗
import { test, expect } from '@playwright/test';

// 用一个唯一用户名，避免重复注册冲突
const testUsername = `e2e_user_${Date.now()}`;
const testPassword = 'test123456';
const testNickname = 'E2E测试用户';

test.describe('用户认证流程', () => {
    test('注册新用户并登录', async ({ page }) => {
        // ===== 阶段一：注册 =====
        // 打开注册页面
        await page.goto('/register');

        // 填写注册表单（placeholder 和页面一致）
        await page.getByPlaceholder('3-50 个字符').fill(testUsername);
        await page.getByPlaceholder('至少 6 个字符').fill(testPassword);
        await page.getByPlaceholder('再输一次密码').fill(testPassword);
        await page.getByPlaceholder('别人看到你的名字').fill(testNickname);

        // 点击注册按钮
        await page.getByRole('button', { name: '注册' }).click();

        // 注册成功后显示绿色提示"注册成功！去登录"
        await expect(
            page.getByText('注册成功！')
        ).toBeVisible({ timeout: 10000 });

        // 点击提示里的"去登录"，跳到登录页
        await page.getByText('注册成功！').locator('a').click();
        await expect(page).toHaveURL(/\/login/);

        // ===== 阶段二：登录 =====
        // 填写登录表单
        await page.getByPlaceholder('请输入用户名').fill(testUsername);
        await page.getByPlaceholder('请输入密码').fill(testPassword);

        // 点击登录按钮
        await page.getByRole('button', { name: '登录' }).click();

        // 登录成功后导航栏应显示昵称
        // 注意：登录会触发 location.reload()，页面会刷新
        await expect(
            page.getByText(testNickname)
        ).toBeVisible({ timeout: 10000 });

        // 刷新页面，验证登录态保持
        await page.reload();
        await expect(
            page.getByText(testNickname)
        ).toBeVisible({ timeout: 10000 });
    });
});
