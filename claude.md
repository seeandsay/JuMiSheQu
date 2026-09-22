## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

----------

# 剧迷社区 (Drama Community)

## 项目概述

前后端分离的 JavaWeb 影视社区平台，用户可以搜索剧集、查看详情、发布评价（1-10 分 + 文字），首页按时间倒序展示最新评价信息流。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| JDK | OpenJDK | 17 |
| 后端框架 | Spring Boot | 3.5.16 |
| 安全框架 | Spring Security | 6.5.x（内置） |
| ORM | MyBatis-Plus | 3.5.17 |
| 数据库 | MySQL | 8.0.26 |
| JWT | jjwt | 0.12.6 |
| 工具库 | Hutool | 5.8.32 |
| 构建工具 | Maven | 3.8.1 |
| 前端框架 | Vue | 2.7.16 |
| 前端路由 | Vue Router | 3.6.5 |
| 状态管理 | Vuex | 3.6.2 |
| UI 组件库 | Element UI | 2.15.14 |
| HTTP 客户端 | Axios | 1.11.0 |
| AI 服务 | Python + FastAPI | 3.9 / 0.11x |
| LLM | DeepSeek（文本）/ SiliconFlow（图像） | OpenAI 兼容 API |
| Agent 框架 | Function Calling（工具调用循环） | OpenAI SDK |

## 项目结构

```
drama-community/
├── drama-server/                    # 后端 Spring Boot
│   ├── pom.xml                      # Maven 依赖配置
│   └── src/main/
│       ├── java/com/drama/
│       │   ├── DramaApplication.java   # 启动类
│       │   ├── controller/             # 接口层
│       │   ├── service/                # 业务接口
│       │   ├── service/impl/           # 业务实现
│       │   ├── mapper/                 # MyBatis-Plus Mapper
│       │   ├── entity/                 # 数据库实体
│       │   ├── dto/                    # 请求/响应 DTO
│       │   ├── config/                 # Spring Security / JWT / CORS 配置
│       │   └── common/                 # 统一返回 Result / 异常处理
│       └── resources/
│           └── application.yml         # 数据库 / JWT / 服务端口配置
├── drama-web/                       # 前端 Vue 2
│   ├── package.json                 # npm 依赖
│   ├── vue.config.js                # Vue CLI 配置（devServer 代理 /api）
│   ├── babel.config.js
│   ├── public/index.html            # HTML 入口
│   └── src/
│       ├── main.js                  # Vue 入口（注册 ElementUI / Router / Store）
│       ├── App.vue                  # 根组件
│       ├── router/index.js          # 路由表 + 导航守卫
│       ├── store/index.js           # Vuex（token / userInfo）
│       ├── api/request.js           # Axios 封装（JWT 请求拦截 + 401 响应拦截）
│       ├── views/                   # 页面组件
│       └── components/              # 公共组件
└── drama.sql                        # 数据库建表脚本
```

## 数据库表

| 表名 | 用途 | 核心字段 |
|------|------|---------|
| `user` | 用户表 | id, phone, email, password(BCrypt), nickname, avatar, create_time |
| `drama` | 剧集表 | id, name, poster, description, genre, release_date, create_time |
| `review` | 评价表 | id, user_id, drama_id, rating(1-10), content(≤500字), create_time |
| `verify_code` | 验证码表 | id, phone/email, code, expire_time |

## 后端 API

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| POST | `/api/user/register` | 用户注册 | 否 |
| POST | `/api/user/login` | 用户登录，返回 JWT | 否 |
| GET | `/api/user/profile` | 获取当前用户信息 | 是 |
| PUT | `/api/user/profile` | 修改个人信息 | 是 |
| GET | `/api/review/feed` | 首页信息流（分页） | 否 |
| GET | `/api/drama/search` | 搜索剧集 `?keyword=xxx` | 否 |
| GET | `/api/drama/{id}` | 剧集详情 | 否 |
| POST | `/api/drama` | 创建剧集（剧名去重） | 是 |
| GET | `/api/drama/{id}/reviews` | 该剧所有评价（分页） | 否 |
| POST | `/api/review` | 发布评价 | 是 |
| GET | `/api/review/my` | 我的评价列表 | 是 |

## 前端路由

| 路径 | 页面 | 需登录 |
|------|------|--------|
| `/` | 首页信息流 | 否 |
| `/login` | 登录页 | 否 |
| `/register` | 注册页 | 否 |
| `/search?q=xxx` | 剧集搜索结果 | 否 |
| `/drama/:id` | 剧集详情页 | 否 |
| `/drama/:id/review` | 发布评价 | 是 |
| `/user/profile` | 个人中心 | 是 |

## 关键设计决策

- **不引入 Redis**：JWT 无状态认证，验证码存 MySQL 临时表
- **Spring Security + JWT**：自定义 `JwtAuthenticationFilter` 插入过滤器链，不存 Session
- **跨域处理**：Vue CLI `devServer.proxy` 代理 `/api` → `localhost:8080`；后端同时配 `CorsFilter` 兜底
- **分页**：MyBatis-Plus `PaginationInnerInterceptor`，统一分页返回格式
- **密码加密**：BCrypt，Spring Security 自带 `BCryptPasswordEncoder`
- **统一返回格式**：`Result { code, message, data }`

## 简历素材

### 技术栈速览

- 后端：Java 17 / Spring Boot 3.5 / Spring Security 6（JWT 无状态认证）/ MyBatis-Plus 3.5 / MySQL 8.0 / Maven
- 前端：Vue 2.7 / Vue Router 3 / Vuex 3 / Element UI 2.15 / Axios / Vue CLI
- AI：Python 3.9 / FastAPI / OpenAI SDK / DeepSeek（文本）/ SiliconFlow（图像生成）/ Function Calling / PyMySQL

### 技术亮点（可写进简历）

1. **前后端分离架构**：Vue 单页应用 + Spring Boot REST API，Vue CLI 代理跨域，统一 `Result{code, message, data}` 返回与全局异常处理
2. **无状态 JWT 认证**：Spring Security 6 过滤器链自定义 `OncePerRequestFilter` 解析 Bearer Token，BCrypt 密码加密，路由守卫 + Axios 拦截器（401/403 自动跳登录）
3. **LLM Agent 工具调用（Function Calling）**：Python FastAPI 服务定义 `query_dramas` / `query_reviews` 工具 Schema，Agent 循环执行「意图解析 → 查库 → 结果回填 → 生成推荐」，异构服务（Python + Java）通过 HTTP 协作，Java 侧零改动
4. **Prompt 工程与结构化输出**：`response_format=json_object` 约束输出；通过系统提示词要求推荐必须引用工具返回的真实数据，抑制幻觉
5. **AI 多场景落地**：评价草稿生成（写作助手）、社区口碑聚合总结、对话式找剧推荐、文生图海报生成（图像 API + 本地落盘）
6. **MyBatis-Plus 查询优化**：分页插件 + 批量 ID 查询组装 VO，避免列表场景 N+1 查询
7. **前端主题化**：CSS 变量驱动的暗色主题，Element UI 深色适配，通栏轮播 + 模糊背景填充（解决竖版海报横屏展示的裁切问题）

## 启动方式

### 后端（drama-server）

```bash
cd drama-community/drama-server

# 启动（注意：不能用 mvn spring-boot:run 简写，阿里云镜像解析不了插件前缀）
mvn org.springframework.boot:spring-boot-maven-plugin:3.5.16:run
```

- 端口：`http://localhost:8080`
- 前置条件：MySQL 已启动，数据库 `drama_community` 已建（脚本 `drama.sql`）
- 停服后端口残留处理：
  ```bash
  netstat -ano | findstr "8080"
  taskkill /F /PID <对应PID>
  ```

### 前端（drama-web）

```bash
cd drama-community/drama-web

npm install        # 首次运行才需要
npm run serve
```

- 端口：`http://localhost:8081`
- `/api` 请求由 `vue.config.js` 自动代理到后端 `localhost:8080`；`/agent` 代理到 `localhost:8000`

### Agent 服务（drama-agent）

```bash
cd drama-community/drama-agent

# 首次运行：安装依赖（虚拟环境位于 drama-community/.venv）
pip install -r requirements.txt

# 启动（先激活虚拟环境，再启动服务）
..\.venv\Scripts\activate.bat
uvicorn main:app --port 8000
```

- 端口：`http://localhost:8000`（健康检查 `/agent/health`）
- 前置条件：`.env` 中已配置 `DEEPSEEK_API_KEY`（A/B/C 功能必需）和 `SILICONFLOW_API_KEY`（H 海报生成必需）
- Agent 只读 MySQL，与后端互不依赖

### 完整验证流程

1. 启动后端 → `http://localhost:8080`
2. 启动前端 → 访问 `http://localhost:8081`
3. 首页可看到评价信息流（测试数据已内置）
4. 登录测试账号：手机号 `17803844547`，密码按注册时设置
5. 注册/登录 → 搜索剧集 → 查看详情 → 发布评价 → 首页刷新


