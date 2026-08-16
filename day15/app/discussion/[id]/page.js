"use client";

import { useState, useEffect, use } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { topicApi } from "@/lib/api";
import { commentApi } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { formatRelativeTime } from "@/lib/time"; // ⭐ 时间格式化

export default function TopicDetailPage({ params }) {
  let [topic, setTopic] = useState(null);
  let [loading, setLoading] = useState(true);
  let { id: topicId } = use(params);
  let [comments, setComments] = useState([]);
  let [newComment, setNewComment] = useState("");
  let [currentUser, setCurrentUser] = useState('');
  let [liked, setLiked] = useState(false);
  let [likeCount, setLikeCount] = useState(0);
  let [likeLoading, setLikeLoading] = useState(false);


    // 组件加载时从 localStorage 取当前用户
    useEffect(function() {
      setTimeout(() => {
        try {
            let userData = JSON.parse(localStorage.getItem('user') || '{}');
            setCurrentUser(userData.username || '');
        } catch (e) {console.error('读取用户信息失败:', e)}
      }, 0);
    }, []);


 //加载时获取话题数据
  useEffect(function() {
      async function loadTopic() {
          try {
              // 先加载公开的话题详情
              let data = await topicApi.getById(topicId);

              if (!data || data.error || data.success === false) {
                  throw new Error(data?.message || "话题不存在");
              }

              setTopic(data);
              setLikeCount(data.likes || 0);

              // 点赞状态是“当前用户”的个性化数据，
              // 只有登录用户才请求。
              if (localStorage.getItem("token")) {
                  let likeStatus = await topicApi.getLikeStatus(topicId);

                  if (likeStatus.error || likeStatus.success === false) {
                          throw new Error(
                              likeStatus.message || "无法获取点赞状态"
                          );}

                      setLiked(likeStatus.liked);
                      setLikeCount(likeStatus.likes);
              }
          } catch (error) {
              console.error("加载话题失败:", error);
          } finally {
              setLoading(false);
          }
      }

      loadTopic();
  }, [topicId]);



  async function handleLike() {
      // 防止用户快速连续点击，产生并发请求
      if (likeLoading) {
          return;
      }

      // 没有 Token 时，后端会拒绝请求。
      // 这里先给用户明确提示。
      if (!localStorage.getItem("token")) {
          alert("请先登录后再点赞");
          return;
      }

      try {
          setLikeLoading(true);

          // 后端根据 JWT 判断当前用户并切换点赞状态
          let result = await topicApi.toggleLike(topicId);

          if (result.error || result.success === false) {
              throw new Error(result.message || "点赞失败");
          }

          // 使用后端返回的真实状态和数量更新页面
          setLiked(result.liked);
          setLikeCount(result.likes);
      } catch (error) {
          console.error("点赞失败:", error);
          alert(error.message || "点赞失败，请稍后重试");
      } finally {
          setLikeLoading(false);
      }
  }


 //加载时获取评论数据
  useEffect(
    function () {
      commentApi.getByTopicId(topicId).then(setComments);
    },
    [topicId],
  );

  async function handleComment() {
    if (!newComment.trim()) return;

    // author 由后端从 JWT 获取，前端不再传递
    await commentApi.create(topicId, newComment.trim());
    setNewComment("");
    // 重新加载评论列表
    let updated = await commentApi.getByTopicId(topicId);
    setComments(updated);
  }

  async function handleDeleteComment(commentId) {
    // 先取当前登录用户名
        await commentApi.delete(commentId);
        // 重新加载评论列表
        let updated = await commentApi.getByTopicId(topicId);
        setComments(updated);
    }

      // ===== 删除话题 =====
  async function handleDeleteTopic() {
    if (!confirm('确定要删除这个话题吗？')) return;
    await topicApi.delete(topicId);
    window.location.href = '/discussion';
  }


  if (loading) {
    return (
      <div className="max-w-3xl mx-auto px-5 py-8">
        <div className="h-4 w-20 bg-gray-200 rounded animate-pulse mb-6" />
        <div className="h-8 w-3/4 bg-gray-200 rounded animate-pulse mb-4" />
        <div className="h-4 w-full bg-gray-200 rounded animate-pulse mb-2" />
        <div className="h-4 w-2/3 bg-gray-200 rounded animate-pulse" />
      </div>
    );
  }

  if (!topic) {
    return (
      <div className="max-w-3xl mx-auto px-5 py-20 text-center">
        <div className="text-6xl mb-4">🔍</div>
        <h2 className="text-2xl font-bold mb-2">话题不存在</h2>
        <Button>
          <Link href="/discussion">← 返回讨论区</Link>
        </Button>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-5 py-8">
      <Link
        href="/discussion"
        className="text-sm text-gray-400 hover:text-purple-600 transition mb-6 inline-block"
      >
        ← 返回讨论区
      </Link>

      {currentUser === topic.author && (
        <Button variant="ghost" size="sm"
            className="text-red-400 mb-4"
            onClick={handleDeleteTopic}>
            🗑️ 删除话题
        </Button>
      )}

      <h1 className="text-2xl font-bold mb-2">{topic.title}</h1>
      <Badge className="mb-4">{topic.tag}</Badge>
      <div className="flex gap-4 text-sm text-gray-400 mb-6 pb-4 border-b">
        <span>
            👤{" "}
            <Link
                href={`/users/${encodeURIComponent(topic.author)}`}
                className="hover:text-purple-600 hover:underline"
            >
                {topic.author}
            </Link>
        </span>
        <span>🕐 {formatRelativeTime(topic.createdAt)}</span>
        <span>💬 {topic.replies}</span>
        <span>
              <Button
                size="sm"
                className="h-auto px-2 py-0 text-sm"
                variant = "ghost"
                disabled={likeLoading}
                onClick={handleLike}
            >
                {
                   liked
                        ? "❤️" + likeCount
                        : "🤍" + likeCount
                }
            </Button>
        </span>

      </div>
      <div className="text-gray-700 leading-relaxed whitespace-pre-line">
        {topic.content}
      </div>
      <div className="mt-10 border-t pt-6">
        <h3 className="text-lg font-semibold mb-4">
          💬 评论 ({comments.length})
        </h3>

        {/* 评论列表 */}
        <div className="space-y-4 mb-6">
          {comments.map((c) => (
            <div key={c.id} className="border-b pb-4">
              <div className="flex items-center gap-2 mb-1">
                <span className="font-medium text-sm">{c.author}</span>
                <span className="text-xs text-gray-400">{formatRelativeTime(c.createdAt)}</span>
              </div>
              <p className="text-sm text-gray-700">{c.content}</p>

              {/* 只有评论作者才能看到删除按钮 */}
              {currentUser === c.author && (
                  <Button variant="ghost" size="sm"
                      className="text-xs text-red-400 h-auto px-2 py-0"
                      onClick={() => handleDeleteComment(c.id)}>
                      删除
                  </Button>
              )}

            </div>
          ))}
          {comments.length === 0 && (
            <p className="text-sm text-gray-400">暂无评论</p>
          )}
        </div>

        {/* 发表评论 — 只有登录用户才能看到 */}
        {currentUser ? (
            <div className="flex gap-3">
                <Input placeholder="写下你的评论..."
                    value={newComment}
                    onChange={e => setNewComment(e.target.value)} />
                <Button onClick={handleComment}>发表</Button>
            </div>
        ) : (
            <p className="text-sm text-gray-400">登录后才能发表评论</p>
        )}

      </div>
    </div>
  );
}
