export default function Footer() {
    return (
        <footer className="bg-gray-900 text-gray-400 py-10 px-5 text-sm text-center">
                        <div className="max-w-5xl mx-auto">
                            <div className="flex justify-center gap-8 mb-6 flex-wrap">
                                <div className="text-left">
                                    <h4 className="text-white font-medium mb-3">关于</h4>
                                    <p className="hover:text-white cursor-pointer py-1">关于我们</p>
                                    <p className="hover:text-white cursor-pointer py-1">联系方式</p>
                                </div>
                                <div className="text-left">
                                    <h4 className="text-white font-medium mb-3">社区</h4>
                                    <p className="hover:text-white cursor-pointer py-1">讨论区</p>
                                    <p className="hover:text-white cursor-pointer py-1">学习路线</p>
                                </div>
                                <div className="text-left">
                                    <h4 className="text-white font-medium mb-3">支持</h4>
                                    <p className="hover:text-white cursor-pointer py-1">帮助中心</p>
                                    <p className="hover:text-white cursor-pointer py-1">反馈建议</p>
                                </div>
                            </div>
                            <div className="pt-6 border-t border-gray-800">
                                <p>© 2026 乌托邦开发者社区 · 公益项目</p>
                            </div>
                        </div>
                    </footer>
    );
}