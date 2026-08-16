// E2E：发布话题完整流程
import { test, expect } from '@playwright/test';

const testUsername = `publish_user_${Date.now()}`;
const testPassword = 'test123456';
const testNickname = '发布测试用户';
const testTitle = `E2E话题_${Date.now()}`;

// 辅助函数：注册并登录
// 现在的注册/登录是独立页面（/register、/login），不再是 prompt 弹窗
async function registerAndLogin(page) {
    // 注册页注册
    await page.goto('/register');
    await page.getByPlaceholder('3-50 个字符').fill(testUsername);
    await page.getByPlaceholder('至少 6 个字符').fill(testPassword);
    await page.getByPlaceholder('再输一次密码').fill(testPassword);
    await page.getByPlaceholder('别人看到你的名字').fill(testNickname);
    await page.getByRole('button', { name: '注册' }).click();

    // 注册成功提示
    await expect(
        page.getByText('注册成功！')
    ).toBeVisible({ timeout: 10000 });

    // 去登录页登录
    await page.getByText('注册成功！').locator('a').click();
    await page.getByPlaceholder('请输入用户名').fill(testUsername);
    await page.getByPlaceholder('请输入密码').fill(testPassword);
    await page.getByRole('button', { name: '登录' }).click();

    // 登录后导航栏显示昵称
    await expect(
        page.getByText(testNickname)
    ).toBeVisible({ timeout: 15000 });
}

test.describe('发布话题流程', () => {
    test('登录后发布话题', async ({ page }) => {
        // 注册并登录
        await registerAndLogin(page);

        // 打开发布页
        await page.goto('/publish');

        // 填写标题
        await page.getByPlaceholder(/React 19/).fill(testTitle);

        // 填写内容（不选分类，默认"其他"）
        await page.getByPlaceholder(/写下你想分享的内容/).fill('这是 E2E 测试发布的正文内容');

        // 点击发布
        await page.getByRole('button', { name: '发布话题' }).click();

        // 发布成功会跳转到讨论区
        await page.waitForURL('/discussion');

        // 在讨论区应该能看到刚发布的话题标题
        await expect(
            page.getByText(testTitle)
        ).toBeVisible({ timeout: 10000 });
    });
});
