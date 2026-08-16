// ============================================================
// app/context/AuthContext.js — 用户认证上下文
// 登录 → authApi.login() → 拿 Token → 存 localStorage → 刷新
// 所有组件都能通过 useAuth() 获取用户状态
// ============================================================

'use client';

import { createContext, useContext, useState, useEffect, useCallback} from 'react';
import { authApi } from '@/lib/api';  // ⭐ 引入统一 API

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    let [user, setUser] = useState(null);
    let [token, setToken] = useState(null);

    // ===== 注册 =====
    async function register(username, password, nickname) {
        try {
            let result = await authApi.register(username, password, nickname);
            if (result.includes('注册成功')) {
                return { success: true };
            }
            return { success: false, message: result };
        } catch (err) {
            return { success: false, message: '无法连接到服务器' };
        }
    }

    // ===== 登录 =====
    async function login(username, password) {
        try {
            
            let result = await authApi.login(username, password);

            if (result.success && result.token) {
                let userData = result.user || { username };
                let displayName = userData.nickname || userData.username || username;
                let currentUser = {
                    name: displayName,
                    username: userData.username || username,
                    initial: displayName[0].toUpperCase(),
                    userAvatar: userData.avatar || '',
                    userBio: userData.bio || ''
                };
                setUser(currentUser);
                setToken(result.token);
                localStorage.setItem('token', result.token);
                localStorage.setItem('user', JSON.stringify(currentUser));
                location.reload();
                return { success: true };
            }
            return { success: false, message: result.message || '登录失败' };
        } catch (err) {
            return { success: false, message: '无法连接到服务器' };
        }
    }

    // ===== 同步最新用户资料 =====
    const updateUser = useCallback(function(userData) {
    // nickname 用于页面显示，username 用于身份判断。
        let displayName = userData.nickname || userData.username;

        let currentUser = {
            name: displayName,
            username: userData.username,
            initial: displayName[0].toUpperCase(),
            userAvatar: userData.avatar || '',
            userBio: userData.bio || ''
        };

        // 更新 React 状态，让当前页面立即刷新
        setUser(currentUser);

        // 更新 localStorage，保证刷新后仍然是最新昵称
        localStorage.setItem('user', JSON.stringify(currentUser));
    }, []);


    // ===== 退出 =====
    function logout() {
        setUser(null);
        setToken(null);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        location.reload();
    }

    // ===== 页面加载时从 localStorage 恢复登录状态 =====
    useEffect(function() {
        let savedToken = localStorage.getItem('token');
        let savedUser = localStorage.getItem('user');

        if (savedToken && savedUser) {
            try {
                let userData = JSON.parse(savedUser);
                setTimeout(function() {
                let displayName = userData.name || userData.username;

                    setUser({
                        name: displayName,
                        username: userData.username,
                        initial: displayName[0].toUpperCase(),
                        userAvatar: userData.userAvatar || '',
                        userBio: userData.userBio || ''
                    });
                    setToken(savedToken);
                }, 0);
            } catch (e) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
        }
    }, []);

    return (
        <AuthContext.Provider value={{
            user,
            token,
            login,
            logout,
            register,
            updateUser
        }}>

            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    let context = useContext(AuthContext);
    if (!context) throw new Error("useAuth 必须在 AuthProvider 内部使用");
    return context;
}
