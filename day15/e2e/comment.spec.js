// E2E：评论 + 删除权限完整流程
import { test, expect } from '@playwright/test';

const testUsername = `comment_user_${Date.now()}`;
const testPassword = 'test123456';
const testNickname = '评论测试用户';
const testTitle = `评论E2E_${Date.now()}`;
const testComment = '这是一条 E2E 测试评论';

// 辅助函数：注册并登录（和 publish.spec.js 相同的模式）
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

test.describe('评论与删除权限流程', () => {
    test('发布话题后评论并删除', async ({ page }) => {
        // 注册并登录
        await registerAndLogin(page);

        // 打开发布页，发布话题
        await page.goto('/publish');
        await page.getByPlaceholder(/React 19/).fill(testTitle);
        await page.getByPlaceholder(/写下你想分享的内容/).fill('评论测试的正文');
        await page.getByRole('button', { name: '发布话题' }).click();
        await page.waitForURL('/discussion');

        // 点击刚发布的话题进入详情页
        await page.getByText(testTitle).click();
        await page.waitForURL(/\/discussion\/\d+/);

        // 填写评论并发表
        await page.getByPlaceholder('写下你的评论...').fill(testComment);
        await page.getByRole('button', { name: '发表' }).click();

        // 评论应该显示在页面上
        await expect(
            page.getByText(testComment)
        ).toBeVisible({ timeout: 10000 });

        // 自己发布的评论，作者是自己，应该能看到"删除"按钮
        // 注意用 exact: true，避免匹配到"🗑️ 删除话题"按钮
        await expect(
            page.getByRole('button', { name: '删除', exact: true })
        ).toBeVisible({ timeout: 10000 });

        // 点击删除，评论消失
        // handleDeleteComment 没有 confirm 弹窗，直接删除
        await page.getByRole('button', { name: '删除', exact: true }).first().click();

        // 等待评论被删除
        await expect(
            page.getByText(testComment)
        ).toBeHidden({ timeout: 10000 });
    });
});
