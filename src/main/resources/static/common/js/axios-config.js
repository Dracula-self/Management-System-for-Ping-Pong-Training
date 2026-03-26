/**
 * Axios HTTP 请求配置
 * 智能版本，根据页面路径自动区分前台和后台的token
 *
 * 后端响应格式：
 * 成功: HTTP 200 + {code: 200, message: "操作成功", data: ...}
 * 业务异常: HTTP 200 + {code: 500, message: "错误信息", data: null}
 * 认证失败: HTTP 200 + {code: 401, message: "认证失败", data: null}
 */

// 使用IIFE封装所有逻辑，确保变量和函数在定义后立即挂载到window对象
(function () {
  // Token键名配置
  const TOKEN_KEYS = {
    FRONTEND: "frontend_token",
    BACKEND: "management_token",
  };
  // 立即挂载到window
  window.TOKEN_KEYS = TOKEN_KEYS;

  const USER_KEYS = {
    FRONTEND: "frontend_user",
    BACKEND: "management_user",
  };
  // 立即挂载到window
  window.USER_KEYS = USER_KEYS;

  /**
   * 根据当前页面路径判断是前台还是后台
   */
  function getCurrentSystemType() {
    const pathname = window.location.pathname;
    if (pathname.includes("/frontend/")) {
      return "FRONTEND";
    } else if (pathname.includes("/management-system/")) {
      return "BACKEND";
    } else {
      // 默认根据域名或其他逻辑判断，这里默认为后台
      return "BACKEND";
    }
  }
  // 立即挂载到window
  window.getCurrentSystemType = getCurrentSystemType;

  /**
   * 获取当前系统对应的token
   */
  function getCurrentToken() {
    const systemType = getCurrentSystemType();
    const tokenKey = TOKEN_KEYS[systemType];
    return sessionStorage.getItem(tokenKey);
  }
  // 立即挂载到window
  window.getCurrentToken = getCurrentToken;

  /**
   * 获取当前系统对应的用户信息键名
   */
  function getCurrentUserKey() {
    const systemType = getCurrentSystemType();
    return USER_KEYS[systemType];
  }
  // 立即挂载到window
  window.getCurrentUserKey = getCurrentUserKey;

  /**
   * 检查是否已登录的辅助函数
   */
  function isLoggedIn() {
    return !!getCurrentToken();
  }
  // 立即挂载到window
  window.isLoggedIn = isLoggedIn;

  /**
   * 获取当前用户信息的辅助函数
   */
  function getCurrentUserInfo() {
    const userKey = getCurrentUserKey();
    const userStr = sessionStorage.getItem(userKey);
    if (userStr) {
      try {
        return JSON.parse(userStr);
      } catch (error) {
        console.error("解析用户信息失败:", error);
        return null;
      }
    }
    return null;
  }
  // 立即挂载到window
  window.getCurrentUserInfo = getCurrentUserInfo;

  /**
   * 处理认证失败
   */
  function handleAuthenticationFailure() {
    const systemType = getCurrentSystemType();
    const tokenKey = TOKEN_KEYS[systemType];
    const userKey = USER_KEYS[systemType];

    // 清除对应系统的token和用户信息
    sessionStorage.removeItem(tokenKey);
    sessionStorage.removeItem(userKey);

    setTimeout(() => {
      if (!window.location.pathname.includes("login.html")) {
        const loginUrl =
          systemType === "FRONTEND"
            ? "/frontend/login.html"
            : "/management-system/login.html";

        // 如果在iframe中，跳出到顶层窗口
        if (window.top !== window.self) {
          window.top.location.href = loginUrl;
        } else {
          window.location.href = loginUrl;
        }
      }
    }, 1000);
  }

  /**
   * 保存登录信息的辅助函数
   */
  function saveLoginInfo(token, userInfo) {
    const systemType = getCurrentSystemType();
    const tokenKey = TOKEN_KEYS[systemType];
    const userKey = USER_KEYS[systemType];

    console.log(`[${systemType}] 保存登录信息:`, {
      tokenKey,
      userKey,
      token: token?.substring(0, 20) + "...",
      userInfo,
    });

    sessionStorage.setItem(tokenKey, token);
    sessionStorage.setItem(userKey, JSON.stringify(userInfo));
  }
  // 立即挂载到window
  window.saveLoginInfo = saveLoginInfo;

  /**
   * 退出登录的辅助函数
   */
  function logout() {
    const systemType = getCurrentSystemType();
    const tokenKey = TOKEN_KEYS[systemType];
    const userKey = USER_KEYS[systemType];

    sessionStorage.removeItem(tokenKey);
    sessionStorage.removeItem(userKey);

    const loginUrl =
      systemType === "FRONTEND"
        ? "/frontend/login.html"
        : "/management-system/login.html";
    window.location.href = loginUrl;
  }
  // 立即挂载到window
  window.logout = logout;

  // 创建 axios 实例
  const apiClient = axios.create({
    baseURL: "/api",
    timeout: 30000,
    headers: {
      "Content-Type": "application/json",
    },
  });
  // 立即挂载到window
  window.apiClient = apiClient;

  // 请求拦截器
  apiClient.interceptors.request.use(
    function (config) {
      const systemType = getCurrentSystemType();
      console.log(
        `[${systemType}] 发送请求:`,
        config.method?.toUpperCase(),
        config.url
      );

      // 根据当前系统自动添加对应的认证token
      const token = getCurrentToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }

      return config;
    },
    function (error) {
      console.error("请求错误:", error);
      return Promise.reject(error);
    }
  );

  // 响应拦截器
  apiClient.interceptors.response.use(
    function (response) {
      const systemType = getCurrentSystemType();
      console.log(
        `[${systemType}] 收到响应:`,
        response.status,
        response.config.url
      );

      const data = response.data;

      // 检查业务状态码
      if (data && typeof data.code !== "undefined") {
        if (data.code === 200) {
          // 成功
          return data;
        } else if (data.code === 401) {
          // 认证失败
          const errorMsg =
            systemType === "FRONTEND" ? "登录已过期，请重新登录" : "认证失败";
          ElementPlus.ElMessage.error(data.message || errorMsg);
          handleAuthenticationFailure();
          return Promise.reject(new Error(data.message));
        } else {
          // 其他业务错误
          ElementPlus.ElMessage.error(data.message || "操作失败");
          return Promise.reject(new Error(data.message));
        }
      }

      return data;
    },
    function (error) {
      console.error("请求失败:", error);
      // 网络错误或服务器错误
      let errorMessage = "网络错误，请稍后重试";
      if (error.response) {
        errorMessage = `服务器错误 (${error.response.status})`;
      } else if (error.request) {
        errorMessage = "网络连接失败";
      }
      ElementPlus.ElMessage.error(errorMessage);
      return Promise.reject(error);
    }
  );

  // 输出初始化完成日志
  const systemType = getCurrentSystemType();
  console.log(`🚀 [${systemType}] Axios 配置已加载，全局函数已挂载`);
})();
