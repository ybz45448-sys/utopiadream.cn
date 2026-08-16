// ============================================================
// components/navbar.js — 响应式导航栏（含登录）
// ============================================================

"use client";

import Link from "next/link";
import { Button, buttonVariants } from "@/components/ui/button";
import { useAuth } from "@/app/context/AuthContext";
import {
  Sheet,
  SheetTrigger,
  SheetContent,
  SheetClose,
} from "@/components/ui/sheet";

export default function Navbar() {
  let { user, logout } = useAuth();

  let menuItems = [
    { label: "首页", href: "/" },
    { label: "讨论区", href: "/discussion" },
  ];

  return (
    <nav className="bg-white border-b sticky top-0 z-50">
      <div className="max-w-5xl mx-auto flex justify-between items-center h-14 px-5">
        <Link href="/" className="text-xl font-bold text-purple-600">
          🚀 乌托邦
        </Link>

        {/* 桌面端 */}
        <div className="hidden md:flex items-center gap-2">
          {menuItems.map((item) => (
            <Link
              key={item.label}
              href={item.href}
              className="px-4 py-2 text-sm text-gray-600 hover:text-purple-600 rounded-lg hover:bg-purple-50"
            >
              {item.label}
            </Link>
          ))}
          {/* 发布话题：未登录显示灰色，登录后可点击 */}
          {user ? (
            <Link href="/publish">✏️ 发布话题</Link>
          ) : (
            <span className="px-4 py-2 text-sm text-gray-300 cursor-not-allowed">
              ✏️ 发布话题
            </span>
          )}

          {user ? (
            <div className="flex items-center gap-2 ml-3 pl-3 border-l">
              <Link
                  href="/profile"
                  className="text-sm text-gray-600 hover:text-purple-600"
              >
                  个人主页
              </Link>
              <span className="text-sm font-medium">{user.name}</span>
              <Button variant="ghost" size="sm" onClick={logout}>
                退出
              </Button>
            </div>
          ) : (
            <div className="flex items-center gap-2 ml-3 pl-3 border-l">
              {/* 未登录：跳转到独立登录/注册页面（替代原来的 prompt 弹窗） */}
              <Link
                href="/login"
                className={buttonVariants({ variant: "outline" })}
              >
                登录
              </Link>
              <Link
                href="/register"
                className={buttonVariants({ variant: "outline" })}
              >
                注册
              </Link>
            </div>
          )}
        </div>

        {/* 手机端 */}
        <div className="md:hidden absolute right-0">
          <Sheet px-0>
            <SheetTrigger className="p-2 text-gray-500 hover:text-purple-600 text-2xl">
              ☰
            </SheetTrigger>
            <SheetContent className="max-w-64 ">
              <div className="flex flex-col gap-3 mt-6 ">
                {user && (
                  <p className="px-4 text-sm font-medium">👋 {user.name}</p>
                )}
                {menuItems.map((item) => (
                  <SheetClose key={item.label}>
                    <Link
                      href={item.href}
                      className="block px-4 py-3 text-gray-600 hover:text-purple-600 rounded-lg hover:bg-purple-50"
                    >
                      {item.label}
                    </Link>
                  </SheetClose>
                ))}
                {user ? (
                  <>
                    
                    <SheetClose>
                          <Link
                              href="/profile"
                              className="block px-4 py-3 text-gray-600 hover:text-purple-600"
                          >
                              个人主页
                          </Link>
                    </SheetClose>
                    <SheetClose>

                      <Link
                        href="/publish"
                        className="block px-4 py-3 bg-purple-600 text-white rounded-lg text-center w-full"
                      >
                        ✏️ 发布话题
                      </Link>
                    </SheetClose>
                    <Button variant="ghost" onClick={logout}>
                      退出
                    </Button>
                  </>
                ) : (
                  <>
                    <p className="text-sm text-gray-400 text-center px-4 py-2">
                      登录后可发布话题
                    </p>
                    <SheetClose>
                      <Link
                        href="/login"
                        className={buttonVariants({
                          variant: "default",
                          className: "w-full",
                        })}
                      >
                        登录
                      </Link>
                    </SheetClose>
                    <SheetClose>
                      <Link
                        href="/register"
                        className={buttonVariants({
                          variant: "outline",
                          className: "w-full",
                        })}
                      >
                        注册
                      </Link>
                    </SheetClose>
                  </>
                )}
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  );
}
