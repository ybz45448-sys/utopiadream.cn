# API 文档：乌托邦开发者社区

> 后端：Spring Boot 4.1 + Spring Security + JWT，基础路径 `/api`
>
> 全部接口走 HTTP，请求/响应均为 JSON（删除、注册接口返回纯文本，见下）

---

## 一、认证方式

| 项目 | 说明 |
|---|---|
| 登录方式 | `POST /api/login` 成功后返回 `token` |
| Token 传递 | 请求头 `Authorization: Bearer <token>` |
| Token 类型 | JWT（HS256），subject = 用户名 |
| 有效期 | 24 小时（86400000ms，可在环境变量 `JWT_EXPIRATION` 配置） |
| 校验位置 | `JwtAuthenticationFilter`，解析成功写入 Spring Security `SecurityContext` |

前端拿到 token 后存 `localStorage`，统一在 `lib/api.js` 的 `request()` 里自动加 `Authorization` 头，无需每个请求手动携带。

**公开接口（无需登录）**：所有 GET 话题/评论/用户资料、注册、登录。
**需登录接口**：创建/删除话题、发表/删除评论、修改资料、点赞/取消点赞。

---

## 二、统一响应与错误

### 成功响应

- 除注册、删除外，成功返回对应业务 JSON。
- 注册返回纯文本 `"注册成功"` / `"注册失败"`。
- 删除话题/评论返回纯文本 `"删除成功"`。

### 错误响应（统一 JSON 结构 `ApiErrorResponse`）

```json
{
  "success": false,
  "message": "参数校验失败",
  "errors": {
    "username": "用户名不能为空"
  },
  "timestamp": "2026-08-16T10:30:00"
}
```

| 状态码 | 场景 | message 示例 |
|---|---|---|
| 400 | 参数校验失败（`@Valid`） | "参数校验失败"，`errors` 为字段级错误 |
| 403 | 无权操作（非资源作者） | "无权删除此话题" / "无权删除此评论" |
| 404 | 资源不存在 | "话题不存在" / "评论不存在" / "用户不存在" |

> 💬 **面试要点**：这是"全局异常处理"的体现——`@RestControllerAdvice` 统一捕获 `MethodArgumentNotValidException`（400）、`ResourceNotFoundException`（404）、`ForbiddenOperationException`（403），前端只需解析一种错误结构。注意：未携带 JWT 访问受保护接口时由 Spring Security 返回默认 403，不属于这个 JSON 结构。

---

## 三、话题接口（Topic）

### 3.1 分页查询话题列表

```
GET /api/topics
```

| 参数 | 类型 | 必填 | 默认 | 说明 |
|---|---|---|---|---|
| keyword | string | 否 | - | 关键词搜索（匹配标题/正文） |
| tag | string | 否 | - | 按分类筛选 |
| page | int | 否 | 1 | 页码，从 1 开始 |
| pageSize | int | 否 | 10 | 每页条数（服务端限制 1~100） |

**鉴权**：公开

**成功响应** `PageResponse<Topic>`：

```json
{
  "content": [
    {
      "id": 1,
      "title": "如何学习 Java？",
      "content": "正文内容……",
      "author": "xiaoming",
      "tag": "后端",
      "likes": 5,
      "createdAt": "2026-08-16T10:30:00+08:00",
      "replies": 3
    }
  ],
  "page": 1,
  "pageSize": 10,
  "total": 100,
  "totalPages": 10
}
```

> 💬 **面试要点**：`replies`（评论数）不是数据库列，而是查询时子查询 `COUNT(*) FROM comments` 动态计算的；`likes` 列虽在表里，但查询时被覆盖为 `topic_likes` 表的实时点赞数。**查询字段与存储字段分离**，避免冗余计数不准。

---

### 3.2 获取所有分类

```
GET /api/topics/tags
```

**鉴权**：公开

**成功响应**（数据库中实际出现过的分类去重，示例为前端发布页可选项）：

```json
["前端", "后端", "AI", "数据库", "面试", "资源", "职业", "新手"]
```

> 注：前端发布页提供这 8 个分类选项，用户不选时默认提交 `"其他"`（不在下拉列表里）。`getAllTags` 返回的是数据库 `DISTINCT tag`，实际内容取决于已有话题。

---

### 3.3 查询单个话题

```
GET /api/topics/{id}
```

| 参数 | 类型 | 说明 |
|---|---|---|
| id | int | 话题 ID（路径参数） |

**鉴权**：公开

**成功响应**：单个 `Topic` JSON（同 3.1 的 content 元素结构）

**失败**：话题不存在 → 404 `{"message": "话题不存在", ...}`

---

### 3.4 创建话题

```
POST /api/topics
Authorization: Bearer <token>
```

**请求体** `CreateTopicRequest`：

```json
{
  "title": "我的第一个话题",
  "content": "正文内容……",
  "tag": "后端"
}
```

**校验规则**：`title` 非空 ≤200；`content` 非空 ≤10000；`tag` 非空 ≤20。

**鉴权**：需登录。**author 不由前端提交，后端从 JWT 的 username 获取。**

**成功响应**：HTTP 200 + 创建的 `Topic` JSON

**失败**：
- 校验失败 → 400，`errors` 字段级提示
- 未登录 → Spring Security 403

> 💬 **面试要点**：**身份不信任请求体**——`author` 从 `authentication.getName()`（JWT 解析出的用户名）获取，前端即使传 `author` 字段也会被忽略。这是防止越权/伪造的基础设计。

---

### 3.5 删除话题

```
DELETE /api/topics/{id}
Authorization: Bearer <token>
```

| 参数 | 类型 | 说明 |
|---|---|---|
| id | int | 话题 ID |

**鉴权**：需登录，且**必须是话题作者**（服务端比对 JWT 用户名与 `author`）。

**成功响应**：HTTP 200 + 纯文本 `"删除成功"`

**失败**：
- 非作者 → 403 `{"message": "无权删除此话题", ...}`
- 不存在 → 404

> 💬 **面试要点**：**服务端校验资源所有权**——能否删除由服务端根据 JWT 身份判断，而不是靠前端"隐藏删除按钮"。前端隐藏只是体验优化，真正的安全防线在后端。

---

## 四、评论接口（Comment）

### 4.1 获取话题的评论列表

```
GET /api/topics/{topicId}/comments
```

| 参数 | 类型 | 说明 |
|---|---|---|
| topicId | int | 话题 ID |

**鉴权**：公开

**成功响应** `Comment[]`：

```json
[
  {
    "id": 1,
    "topicId": 1,
    "author": "xiaoming",
    "content": "写得很清楚！",
    "createdAt": "2026-08-16T11:00:00+08:00"
  }
]
```

---

### 4.2 发表评论

```
POST /api/topics/{topicId}/comments
Authorization: Bearer <token>
```

**请求体** `CreateCommentRequest`：

```json
{
  "content": "写得很清楚！"
}
```

**校验规则**：`content` 非空 ≤1000。

**鉴权**：需登录。author 同样由后端从 JWT 取。

**成功响应**：HTTP 200 + 创建的 `Comment` JSON

---

### 4.3 删除评论

```
DELETE /api/comments/{id}
Authorization: Bearer <token>
```

| 参数 | 类型 | 说明 |
|---|---|---|
| id | int | 评论 ID |

**鉴权**：需登录，且必须是评论作者。

**成功响应**：HTTP 200 + 纯文本 `"删除成功"`

**失败**：非作者 → 403 `{"message": "无权删除此评论", ...}`；不存在 → 404

---

## 五、用户接口（User）

### 5.1 注册

```
POST /api/register
```

**请求体** `RegisterRequest`：

```json
{
  "username": "xiaoming",
  "password": "123456",
  "nickname": "小明"
}
```

**校验规则**：`username` 3~50；`password` 6~100；`nickname` 非空 ≤50。

**鉴权**：公开

**成功响应**：HTTP 200 + 纯文本 `"注册成功"`
**用户名已存在**：HTTP 200 + 纯文本 `"注册失败"`（服务端返回 -1）

> 💬 **面试要点**：密码用 BCrypt 哈希存储（`BCryptPasswordEncoder`），不存明文。注意注册失败也返回 200 纯文本，这是历史遗留，与登录的统一 JSON 结构不一致——面试时可以说"后续可以重构为统一错误结构"。

---

### 5.2 登录

```
POST /api/login
```

**请求体** `LoginRequest`：

```json
{
  "username": "xiaoming",
  "password": "123456"
}
```

**校验规则**：`username` 非空 ≤50；`password` 非空 4~100。

**鉴权**：公开

**成功响应**：

```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "username": "xiaoming",
    "nickname": "小明",
    "avatar": "",
    "bio": ""
  }
}
```

**失败**：

```json
{
  "success": false,
  "message": "用户名或密码错误"
}
```

> 💬 **面试要点**：响应里的 `user` 是**安全视图**——只含 id/username/nickname/avatar/bio，**绝不含 password**。与 `GET /api/me` 共用一个 `UserResponse` 结构，避免"用户接口意外泄露密码哈希"这类事故。

---

### 5.3 获取当前用户信息

```
GET /api/me
Authorization: Bearer <token>
```

**鉴权**：需登录。

**成功响应**：`UserResponse`：

```json
{
  "id": 1,
  "username": "xiaoming",
  "nickname": "小明",
  "avatar": "https://.../avatar.png",
  "bio": "后端学习者"
}
```

**失败**：用户已删除 → 404

---

### 5.4 修改当前用户资料

```
PUT /api/me
Authorization: Bearer <token>
```

**请求体** `UpdateProfileRequest`：

```json
{
  "nickname": "小明新昵称",
  "avatar": "https://.../new-avatar.png",
  "bio": "新的简介"
}
```

**校验规则**：`nickname` 非空 ≤50；`avatar`、`bio` 可选，≤500。

**鉴权**：需登录。修改对象由 JWT 用户名确定，不由请求体决定。

**成功响应**：更新后的 `UserResponse`

> 💬 **面试要点**：**身份始终来自 JWT，不允许请求体决定修改对象**——请求体只允许改"内容字段"（昵称/头像/简介），`username` 只读不可改。

---

### 5.5 查看公开用户主页

```
GET /api/users/{username}
```

| 参数 | 类型 | 说明 |
|---|---|---|
| username | string | 用户名（路径参数，前端会 `encodeURIComponent`） |

**鉴权**：公开

**成功响应**：`UserResponse`（不含 password）

**失败**：用户不存在 → 404

---

## 六、点赞接口（Like）

### 6.1 点赞 / 取消点赞（切换）

```
POST /api/topics/{topicId}/like
Authorization: Bearer <token>
```

| 参数 | 类型 | 说明 |
|---|---|---|
| topicId | int | 话题 ID |

**鉴权**：需登录。

**行为**：如果当前用户已点赞则取消，未点赞则点赞（toggle）。

**成功响应** `LikeResponse`：

```json
{
  "liked": true,
  "likes": 6
}
```

**失败**：话题或用户不存在 → 404

### 6.2 查询单个话题点赞状态

```
GET /api/topics/{topicId}/like
Authorization: Bearer <token>
```

**鉴权**：⚠️ **实际需要登录**。安全配置里该路径被 `GET /api/topics/*` 通配放行，但代码直接 `authentication.getName()`，未携带合法 JWT 会报 NPE/500。文档按真实行为标注：**需登录**。

**成功响应**：`LikeResponse`（同上）

### 6.3 批量查询点赞状态

```
GET /api/topics/like-status?ids=1,2,3
Authorization: Bearer <token>
```

| 参数 | 类型 | 说明 |
|---|---|---|
| ids | string | 话题 ID 逗号分隔（必填） |

**鉴权**：⚠️ 同上，**实际需要登录**（配置上被通配放行，代码需 JWT）。

**成功响应**：

```json
{
  "1": true,
  "2": false,
  "3": true
}
```

> 💬 **面试要点**：批量接口是为了**减少请求次数**——讨论区一页 10 个话题，如果不批量，加载后要发 10 次点赞状态请求；用一个 `ids` 参数一次查回。这是"避免 N+1 请求"的典型优化。

---

## 七、接口总览表

| # | 方法 | 路径 | 鉴权 | 响应 |
|---|---|---|---|---|
| 1 | GET | /api/topics | 公开 | PageResponse |
| 2 | GET | /api/topics/tags | 公开 | string[] |
| 3 | GET | /api/topics/{id} | 公开 | Topic |
| 4 | POST | /api/topics | 登录 | Topic |
| 5 | DELETE | /api/topics/{id} | 登录+作者 | "删除成功" |
| 6 | GET | /api/topics/{topicId}/comments | 公开 | Comment[] |
| 7 | POST | /api/topics/{topicId}/comments | 登录 | Comment |
| 8 | DELETE | /api/comments/{id} | 登录+作者 | "删除成功" |
| 9 | POST | /api/register | 公开 | "注册成功/失败" |
| 10 | POST | /api/login | 公开 | token + user |
| 11 | GET | /api/me | 登录 | UserResponse |
| 12 | PUT | /api/me | 登录 | UserResponse |
| 13 | GET | /api/users/{username} | 公开 | UserResponse |
| 14 | POST | /api/topics/{topicId}/like | 登录 | LikeResponse |
| 15 | GET | /api/topics/{topicId}/like | 需登录* | LikeResponse |
| 16 | GET | /api/topics/like-status | 需登录* | Map<id,bool> |

> `*` 标注的两个 GET like 端点：安全配置实际放行，但代码需要 JWT，详见 6.2/6.3。
