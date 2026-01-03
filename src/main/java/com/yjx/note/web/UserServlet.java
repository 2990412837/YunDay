package com.yjx.note.web;

import com.yjx.note.po.User;
import com.yjx.note.service.UserService;
import com.yjx.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {

    private UserService userService = new UserService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 设置首页导航高亮（可选，用于前端菜单激活）
        request.setAttribute("menu_page", "user");

        // 接收用户行为
        String actionName = request.getParameter("actionName");

        // 判断用户行为，调用对应的方法
        if ("login".equals(actionName)) {
            userLogin(request, response);
        } else if ("logout".equals(actionName)) {
            userLogOut(request, response);
        } else if ("userCenter".equals(actionName)) {
            userCenter(request, response);
        } else if ("userHead".equals(actionName)) {
            // 加载头像
            userHead(request, response);
        } else if ("checkNick".equals(actionName)) {
            // 验证昵称的唯一性
            checkNick(request, response);
        } else if ("updateUser".equals(actionName)) {
            // 修改用户信息
            updateUser(request, response);
        } else {
            // 可选：非法 action 处理
            response.sendRedirect("login.jsp");
        }
    }

    /**
     * 用户登录
     */
    private void userLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = request.getParameter("userName");
        String userPwd = request.getParameter("userPwd");

        ResultInfo<User> resultInfo = userService.userLogin(userName, userPwd);

        if (resultInfo.getCode() == 1) {
            request.getSession().setAttribute("user", resultInfo.getResult());
            String rem = request.getParameter("rem");
            Cookie cookie = new Cookie("user", null);
            if ("1".equals(rem)) {
                cookie = new Cookie("user", userName + "-" + userPwd);
                cookie.setMaxAge(3 * 24 * 60 * 60); // 3天
            } else {
                cookie.setMaxAge(0); // 清除
            }
            response.addCookie(cookie);
            response.sendRedirect("index");
        } else {
            request.setAttribute("resultInfo", resultInfo);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * 用户退出
     * 1. 销毁Session对象
     * 2. 删除Cookie对象
     * 3. 重定向跳转到登录页面
     */
    private void userLogOut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 1. 销毁Session
        request.getSession().invalidate();
        // 2. 删除Cookie
        Cookie cookie = new Cookie("user", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        // 3. 跳转到登录页
        response.sendRedirect("login.jsp");
    }

    /**
     * 进入个人中心
     * 1. 设置首页动态包含的页面值
     * 2. 请求转发跳转到index.jsp
     */
    private void userCenter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("changePage", "user/info.jsp");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
    /**
     * 验证昵称的唯一性
     * 1. 获取参数（昵称）
     * 2. 从session作用域获取用户对象，得到用户ID
     * 3. 调用Service层的方法，得到返回的结果
     * 4. 通过字符输出流将结果响应给前台的ajax的回调函数
     * 5. 关闭资源
     * @param request
     * @param response
     */
    private void checkNick(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 1. 获取参数（昵称）
        String nick  = request.getParameter("nick");
        // 2. 从session作用域获取用户对象，得到用户ID
        User user = (User) request.getSession().getAttribute("user");
        // 3. 调用Service层的方法，得到返回的结果
        Integer code = userService.checkNick(nick, user.getUserId());
        // 4. 通过字符输出流将结果响应给前台的ajax的回调函数
        response.getWriter().write(code + "");
        // 5. 关闭资源
        response.getWriter().close();
    }

    private void userHead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 获取参数 （图片名称）
        String head = request.getParameter("imageName");
        // 2. 得到图片的存放路径 （得到项目的真实路径：request.getServletContext().getealPathR("/")）
        String realPath = request.getServletContext().getRealPath("/WEB-INF/upload/");
        // 3. 通过图片的完整路径，得到file对象
        File file = new File(realPath + "/" + head);
        // 4. 通过截取，得到图片的后缀
        String pic = head.substring(head.lastIndexOf(".")+1);
        // 5. 通过不同的图片后缀，设置不同的响应的类型
        if ("PNG".equalsIgnoreCase(pic)) {
            response.setContentType("image/png");
        } else if ("JPG".equalsIgnoreCase(pic) || "JPEG".equalsIgnoreCase(pic)) {
            response.setContentType("image/jpeg");
        } else if ("GIF".equalsIgnoreCase(pic)) {
            response.setContentType("image/gif");
        }
        // 6. 利用FileUtils的copyFile()方法，将图片拷贝给浏览器
        FileUtils.copyFile(file, response.getOutputStream());
    }

    /**
     * 修改用户信息
     注：文件上传必须在Servlet类上提那家注解！！！ @MultipartConfig
     1. 调用Service层的方法，传递request对象作为参数，返回resultInfo对象
     2. 将resultInfo对象存到request作用域中
     3. 请求转发跳转到个人中心页面 （user?actionName=userCenter）
     * @param request
     * @param response
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. 调用Service层的方法，传递request对象作为参数，返回resultInfo对象
        ResultInfo<User> resultInfo = userService.updateUser(request);
        // 2. 将resultInfo对象存到request作用域中
        request.setAttribute("resultInfo", resultInfo);
        // 3. 请求转发跳转到个人中心页面 （user?actionName=userCenter）
        request.getRequestDispatcher("user?actionName=userCenter").forward(request,
                response);
    }
}