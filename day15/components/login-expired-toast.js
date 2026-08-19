'use client';

import { useEffect } from 'react';
import { toast } from 'sonner';

// ============================================================
// 登录过期提示组件
// 作用：页面加载时检查 sessionStorage 里有没有"登录过期"消息，
//       有就弹一个 toast 通知用户，弹完立刻清掉（只弹一次）。
// 为什么放组件里而不是 api.js：跳转后 api.js 已经不在运行了，
//       得让"新页面"自己来读消息。
// ============================================================
export default function LoginExpiredToast() {
    useEffect(function() {
        // 从 sessionStorage 读出登录过期消息
        let message = sessionStorage.getItem('login_expired');

        if (message) {
            // 先清掉，避免刷新页面再弹一次
            sessionStorage.removeItem('login_expired');
            // 弹红色错误风格 toast，用户一眼看到"要重新登录了"
            toast.error(message);
        }
    }, []);

    // 组件本身不渲染任何内容，只负责弹提示
    return null;
}
