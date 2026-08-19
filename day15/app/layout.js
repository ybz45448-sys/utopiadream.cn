// ============================================================
// app/layout.js — 全局布局
// ============================================================

import './globals.css';
import Navbar from '@/components/navbar';
import { AuthProvider } from '@/app/context/AuthContext';
import Footer from '@/components/footer';
import { Toaster } from '@/components/ui/sonner';
import LoginExpiredToast from '@/components/login-expired-toast';

export const metadata = {
    title: '乌托邦 - 开发者社区',
    description: '一个公益、开放、自由的技术交流平台',
};

export default function RootLayout({ children }) {
    return (
        <html lang="zh-CN">
            <body className="min-h-screen flex flex-col">
                <AuthProvider>
                    <Navbar />
                    <main className="flex-1">{children}</main>
                    <Footer />
                    <LoginExpiredToast />
                    <Toaster />
                </AuthProvider>
            </body>
        </html>
    );
}
