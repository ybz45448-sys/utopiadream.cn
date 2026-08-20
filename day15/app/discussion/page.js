"use client";

import { useState, useEffect, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { topicApi } from "@/lib/api"; // ⭐ 引入 API 工具
import { formatRelativeTime } from "@/lib/time"; // ⭐ 时间格式化

export default function DiscussionsPage() {
  return (
    <Suspense
      fallback={
        <div className="max-w-5xl mx-auto px-5 py-8">
          <p className="text-gray-500">加载中...</p>
        </div>
      }
    >
      <DiscussionsContent />
    </Suspense>
  );
}

function DiscussionsContent() {
  let router = useRouter();
  let searchParams = useSearchParams();
  let [topics, setTopics] = useState([]); // 从后端获取的数据
  let [loading, setLoading] = useState(true);
  let [search, setSearch] = useState("");
  let [activeTag, setActiveTag] = useState("");
  let [error, setError] = useState("");
  // 保存全部标签，不随着当前搜索结果变化
  let [allTags, setAllTags] = useState([]);
  let [page, setPage] = useState(1);
  let [pageSize] = useState(10);
  let [total, setTotal] = useState(0);
  let [totalPages, setTotalPages] = useState(0);
  let [debouncedSearch, setDebouncedSearch] = useState("");
  // 防止 URL 参数还没有恢复时，先发出错误的默认查询
  let [urlReady, setUrlReady] = useState(false);
  // 当前页话题的点赞状态：{ topicId: true/false }
  let [likedByTopicId, setLikedByTopicId] = useState({});
  // 正在处理点赞的 topicId，防止重复点击
  let [likeLoadingId, setLikeLoadingId] = useState(null);  


  // ⭐ 组件加载时从后端获取话题数据
  useEffect(
    function () {
      if (!urlReady) {
        return;
      }
      async function loadTopics() {
        setLoading(true);
        setError("");

        try {
          // 有关键词时请求后端搜索，没有关键词时请求全部话题
          let data =
            debouncedSearch || activeTag
              ? await topicApi.search(debouncedSearch, activeTag, page, pageSize)
              : await topicApi.getAll(page, pageSize);

          // 分页接口返回对象，而不是数组
          if (!data || !Array.isArray(data.content)) {
            throw new Error("分页话题数据格式不正确");
          }

          setTopics(data.content);
          setTotal(data.total);
          setTotalPages(data.totalPages);

          // 当前页加载后，批量查询当前用户的点赞状态。
          // 游客没有 Token，不查询，统一显示未点赞。
          if (localStorage.getItem("token") && data.content.length > 0) {
            try {
              let topicIds = data.content.map((t) => t.id);

              let status = await topicApi.getLikeStatusBatch(topicIds);

              if (status && typeof status === "object") {
                setLikedByTopicId(status);
              }
            } catch (error) {
              console.error("加载点赞状态失败:", error);
              setLikedByTopicId({});
            }
          } else {
            setLikedByTopicId({});
          }
        } catch (error) {
          console.error("加载话题失败:", error);
          setError("话题加载失败，请稍后重试");
          setTopics([]);
        } finally {
          setLoading(false);
        }
      }

      loadTopics();
    },
    [debouncedSearch, activeTag, page, pageSize, urlReady],
  );

  useEffect(function() {
      // URL 参数尚未恢复时，不要立即覆盖地址栏
      if (!urlReady) {
          return;
      }

      let params = new URLSearchParams();

      // 使用防抖后的搜索词同步 URL，
      // 避免每个键盘字符都修改地址栏。
      if (debouncedSearch) {
          params.set("keyword", debouncedSearch);
      }

      if (activeTag) {
          params.set("tag", activeTag);
      }

      // page=1 可以省略，让 URL 更简洁
      if (page > 1) {
          params.set("page", page);
      }

      let queryString = params.toString();

      // replace 不会不断增加浏览器历史记录
      router.replace(
          queryString
              ? `/discussion?${queryString}`
              : "/discussion",
          { scroll: false }
      );
  }, [
      urlReady,
      debouncedSearch,
      activeTag,
      page,
      router
  ]);


  useEffect(function() {
      // 从 URL 读取搜索条件
      let keywordFromUrl = searchParams.get("keyword") || "";
      let tagFromUrl = searchParams.get("tag") || "";

      // 读取页码，并防止非法值进入 React 状态
      let pageFromUrl = Number.parseInt(
          searchParams.get("page") || "1",
          10
      );

      if (!Number.isInteger(pageFromUrl) || pageFromUrl < 1) {
          pageFromUrl = 1;
      }

      // 用 URL 中的值初始化页面状态
      setTimeout(() => {
        setSearch(keywordFromUrl);
        setDebouncedSearch(keywordFromUrl);
        setActiveTag(tagFromUrl);
        setPage(pageFromUrl);
        // URL 状态恢复完成后，才允许请求后端
        setUrlReady(true);
      }, 0);

      
  }, [searchParams]);


  //防抖
  useEffect(function() {
      // 用户每次输入都会重新执行这个 Effect。
      // 先创建一个400毫秒后的任务。
      let timer = setTimeout(function() {
          // 用户400毫秒没有继续输入，
          // 才把输入值交给真正的查询状态。
          setDebouncedSearch(search.trim());
      }, 400);

      // 如果用户在400毫秒内继续输入，
      // 清除旧任务，避免旧关键词触发请求。
      return function() {
          clearTimeout(timer);
      };
  }, [search]);


  // 列表内直接点赞/取消点赞
  async function handleToggleLike(event, topicId) {
    // 阻止外层卡片 Link 跳转
    event.preventDefault();
    event.stopPropagation();

    // 防止快速连续点击
    if (likeLoadingId !== null) {
      return;
    }

    // 未登录用户点击后提示登录
    if (!localStorage.getItem("token")) {
      alert("请先登录后再点赞");
      return;
    }

    try {
      setLikeLoadingId(topicId);

      // 后端根据 JWT 切换点赞状态，返回最新 liked 和 likes
      let result = await topicApi.toggleLike(topicId);

      if (result.error || result.success === false) {
        throw new Error(result.message || "点赞失败");
      }

      // 局部更新当前话题的点赞状态和数量
      setLikedByTopicId((prev) => ({
        ...prev,
        [topicId]: result.liked,
      }));

      setTopics((prevTopics) =>
        prevTopics.map((t) =>
          t.id === topicId
            ? { ...t, likes: result.likes }
            : t,
        ),
      );
    } catch (error) {
      console.error("点赞失败:", error);
      alert(error.message || "点赞失败，请稍后重试");
    } finally {
      setLikeLoadingId(null);
    }
  }

  // ⭐ 组件加载时从后端获取所有标签
  useEffect(function() {
      async function loadAllTags() {
          try {
              // 直接请求标签接口，不再加载话题列表提取标签。
              let tags = await topicApi.getTags();

              if (!Array.isArray(tags)) {
                  throw new Error("标签数据格式不正确");
              }

              setAllTags(tags);
          } catch (error) {
              console.error("加载标签失败:", error);
              setAllTags([]);
          }
      }

      loadAllTags();
  }, []);


  return (
    <div className="max-w-5xl mx-auto px-5 py-8">
      <h1 className="text-2xl font-bold mb-1">📝 讨论区</h1>
      <p className="text-gray-500 mb-6">和开发者一起交流技术话题</p>

      <div className="flex gap-3 mb-4">
        <Input
          placeholder="🔍 搜索话题..."
          value={search}
          onChange={(e) => {
            setSearch(e.target.value);
            setPage(1);
          }}
          className="max-w-md"
        />
        {search && (
          <Button
            variant="ghost"
            onClick={() => {
              setSearch("");
              setPage(1);
            }}
          >
            清除
          </Button>
        )}
      </div>

      {error && <p className="text-red-500 mb-4">{error}</p>}

      <div className="flex flex-wrap gap-2 mb-6">
        <Badge
          variant={!activeTag ? "default" : "outline"}
          className="cursor-pointer"
          onClick={() => {
            setActiveTag("");
            setPage(1);
          }}
        >
          全部
        </Badge>
        {allTags.map((tag) => (
          <Badge
            key={tag}
            variant={activeTag === tag ? "default" : "outline"}
            className="cursor-pointer"
            onClick={() => {
              setActiveTag(tag === activeTag ? "" : tag);
              setPage(1);
            }}
          >
            {tag}
          </Badge>
        ))}
      </div>

      {!loading && !error && (
        <p className="text-sm text-gray-500 mb-4">
          共 {total} 个话题，第 {page} / {totalPages || 1} 页
        </p>
      )}

      {/* 加载中 */}
      {loading ? (
        <div className="space-y-3">
          {[1, 2, 3].map((i) => (
            <div
              key={i}
              className="h-24 bg-gray-100 rounded-xl animate-pulse"
            />
          ))}
        </div>
      ) : error ? (
        <p className="text-red-500">{error}</p>
      ) : topics.length === 0 ? (
        <div className="py-12 text-center text-gray-500">
          暂无符合条件的话题
        </div>
      ) : (
        <div className="space-y-3">
          {topics.map((topic) => (
            <Link key={topic.id} href={`/discussion/${topic.id}`}>
              <Card className="hover:shadow-md transition-all cursor-pointer mt-3">
                <CardContent className="p-5">
                  <div className="flex justify-between items-start mb-2">
                    <h3 className="font-semibold">{topic.title}</h3>
                    <Badge variant="secondary">{topic.tag}</Badge>
                  </div>
                  <p className="text-sm text-gray-500 mb-3">
                    {topic.content?.substring(0, 60)}...
                  </p>
                  <div className="flex gap-4 text-xs text-gray-400">
                    <span>
                      👤{" "}
                      <span
                        className="hover:text-purple-600 hover:underline cursor-pointer"
                        onClick={(e) => {
                          e.preventDefault();
                          e.stopPropagation();
                          router.push(
                            `/users/${encodeURIComponent(topic.author)}`,
                          );
                        }}
                      >
                        {topic.author}
                      </span>
                    </span>
                    <span>🕐 {formatRelativeTime(topic.createdAt)}</span>
                    <span>💬 {topic.replies}</span>
                    <span
                      className={`flex items-center gap-1 cursor-pointer select-none ${
                        likedByTopicId[topic.id]
                          ? "text-red-500"
                          : "hover:text-red-400"
                      } ${
                        likeLoadingId === topic.id
                          ? "opacity-50 pointer-events-none"
                          : ""
                      }`}
                      onClick={(e) => handleToggleLike(e, topic.id)}
                    >
                      <span aria-hidden="true">
                        {likedByTopicId[topic.id] ? "❤️" : "♡"}
                      </span>
                      <span>{topic.likes || 0}</span>
                    </span>
                  </div>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      )}

      {!loading && !error && totalPages > 1 && (
        <div className="flex items-center justify-center gap-4 mt-8">
          <Button
            variant="outline"
            disabled={page <= 1}
            onClick={() => setPage((currentPage) => currentPage - 1)}
          >
            上一页
          </Button>

          <span className="text-sm text-gray-500">
            第 {page} / {totalPages} 页
          </span>

          <Button
            variant="outline"
            disabled={page >= totalPages}
            onClick={() => setPage((currentPage) => currentPage + 1)}
          >
            下一页
          </Button>
        </div>
      )}
    </div>
  );
}
