// ============================================================
// lib/api.js — 统一 API 调用工具
// ============================================================
// 所有调后端的请求都走这里
// 好处：后端地址只写一次，以后上线改地址也只改这一个文件
// ============================================================

// 前端公开环境变量：Next.js 会在构建时注入 NEXT_PUBLIC_ 前缀的变量
// 生产环境留空（"" = 相对路径 /api，跟页面协议走：
//   http 页面调 http、https 页面调 https，一次解决证书/混合内容/CORS）
// 本地开发没配置时（undefined）回退到 localhost:8080
// ⚠️ 用 ?? 而不是 ||：|| 会把空字符串当"假值"回退掉，?? 只在 null/undefined 时回退
const BASE_URL =
    process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';


// ===== 通用请求函数 =====
// ===== 通用请求函数（自动带 Token） =====
async function request(url, options = {}) {
    let token = localStorage.getItem('token');
    let headers = { 'Content-Type': 'application/json' };

    // ⭐ 确保 Token 只包含 ASCII 字符
    if (token) {
        // 只取前50个字符，去掉可能的非法字符
        let cleanToken = token.replace(/[^\x00-\x7F]/g, '').trim();
        if (cleanToken.length > 0) {
            headers['Authorization'] = 'Bearer ' + cleanToken;
        }
    }

    let response = await fetch(`${BASE_URL}${url}`, {
        method: options.method || 'GET',
        headers: headers,
        body: options.body || null,
    });
    // ⭐ token 过期/无效时后端返回 401，这里统一处理：
    //   清掉本地登录缓存 + 刷新页面 → 自动变回游客态
    if (response.status === 401) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    //   不能直接 toast()：location.href 是整页跳转，页面卸载后 toast 显示不出来
    sessionStorage.setItem('login_expired', '登录已过期，请重新登录');

    
    // ⭐ 不要 reload 当前页！如果当前页挂载时就发鉴权请求（如 /profile），
    //   刷新回来还是游客、还是 403、又刷新 → 死循环。
    //   直接跳回首页 —— 首页只用公开接口，游客访问永远不会 403，循环自然断了。
    window.location.href = '/';
    return { success: false, message: '登录已过期，请重新登录' };
}
    let text = await response.text();
    try {
        return JSON.parse(text);  // 如果是 JSON，转成对象
    } catch {
        return text;  // 如果是普通文本，直接返回字符串
    }
}




// ===== 话题 API =====
export const topicApi = {
// 获取全部话题的第一页
    getAll: (page = 1, pageSize = 10) =>
        request(`/api/topics?page=${page}&pageSize=${pageSize}`),

    // 获取所有不重复的话题标签
    getTags: () => request('/api/topics/tags'),



    // 根据关键词、分类和分页查询话题
    search: (
        keyword = "",
        tag = "",
        page = 1,
        pageSize = 10
    ) => {
        let params = new URLSearchParams();

        if (keyword.trim()) {
            params.set("keyword", keyword.trim());
        }

        if (tag.trim()) {
            params.set("tag", tag.trim());
        }

        // 分页参数始终传递，保证后端返回统一分页结构
        params.set("page", page);
        params.set("pageSize", pageSize);

        return request(`/api/topics?${params.toString()}`);
    },



    

    // 根据 ID 获取单个话题
    getById: (id) => request(`/api/topics/${id}`),

    // 创建话题
    create: (data) => request('/api/topics', {
        method: 'POST',
        body: JSON.stringify(data),
    }),

    delete: (id) => request(`/api/topics/${id}`, { method: 'DELETE' }),

    // 点赞或取消点赞
    toggleLike: (topicId) =>
        request(`/api/topics/${topicId}/like`, {
            method: 'POST',
        }),

    getLikeStatus: (topicId) => request(`/api/topics/${topicId}/like`),

    // 批量查询当前用户对多个话题的点赞状态
    // 返回 { [topicId]: true/false }
    getLikeStatusBatch: (topicIds) =>
        request(`/api/topics/like-status?ids=${topicIds.join(",")}`),


};

// ===== 评论 API =====
export const commentApi = {
    // 获取某个话题的评论
    getByTopicId: (topicId) => request(`/api/topics/${topicId}/comments`),

    // 发表评论（author 由后端从 JWT 获取，不再前端传递）
    create: (topicId, content) => request(`/api/topics/${topicId}/comments`, {
        method: 'POST',
        body: JSON.stringify({ content }),
    }),

        // 删除评论
    delete: (id) => request(`/api/comments/${id}`, { method: 'DELETE' }),

};


// ===== 用户 API =====
export const authApi = {
    login: (username, password) =>
        fetch(`${BASE_URL}/api/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        }).then(async response => {
            let result = await response.json();
            if (!response.ok) {
                throw new Error(result.message || '登录失败');
            }
            return result;
        }),

    register: (username, password, nickname) =>
        fetch(`${BASE_URL}/api/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, nickname }),
        }).then(r => r.text()),
};

// ===== 用户资料 API =====
export const userApi = {
    // 获取当前登录用户资料
    getMe: () => request('/api/me'),

    // 获取公开用户资料
    // encodeURIComponent 可以避免用户名中的特殊字符破坏 URL
    getProfile: (username) =>
        request(`/api/users/${encodeURIComponent(username)}`),

    // 修改当前登录用户的昵称
    updateProfile: (data) => request('/api/me', {
        method: 'PUT',
        body: JSON.stringify(data),
    }),
};

