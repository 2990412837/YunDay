package com.yjx.note.web;

import cn.hutool.json.JSONUtil;
import com.yjx.note.po.NoteType;
import com.yjx.note.po.User;
import com.yjx.note.service.NoteTypeService;
import com.yjx.note.util.JsonUtil;
import com.yjx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/type")
public class NoteTypeServlet extends HttpServlet {
    private final NoteTypeService noteTypeService = new NoteTypeService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("menu_page", "type");
        String actionName = request.getParameter("actionName");

        if ("list".equals(actionName)) {
            typeList(request, response);
        } else if ("delete".equals(actionName)) {
            // 删除类型
            deleteType(request, response);
        } else if ("addOrUpdate".equals(actionName)) {
            // 添加或修改类型
            addOrUpdate(request, response);
        }
    }

    /**
     * 添加或修改类型
     1. 接收参数 （类型名称、类型ID）
     2. 获取Session作用域中的user对象，得到用户ID
     3. 调用Service层的更新方法，返回ResultInfo对象
     4. 将ResultInfo转换成JSON格式的字符串，响应给ajax的回调函数
     * @param request
     * @param response
     */
    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) {
        // 1. 接收参数 （类型名称、类型ID）
        String typeName = request.getParameter("typeName");
        String typeId = request.getParameter("typeId");
        // 2. 获取Session作用域中的user对象，得到用户ID
        User user = (User) request.getSession().getAttribute("user");
        // 3. 调用Service层的更新方法，返回ResultInfo对象
        ResultInfo<Integer> resultInfo = noteTypeService.addOrUpdate(typeName, user.getUserId(), typeId);
        // 4. 将ResultInfo转换成JSON格式的字符串，响应给ajax的回调函数
        JsonUtil.toJson(response, resultInfo);
    }

    /**
     * 删除类型
     1. 接收参数（类型ID）
     2. 调用Service的更新操作，返回ResultInfo对象
     3. 将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
     * @param request
     * @param response
     */
    private void deleteType(HttpServletRequest request, HttpServletResponse response) {
        // 1. 接收参数（类型ID）
        String typeId = request.getParameter("typeId");
        // 2. 调用Service的更新操作，返回ResultInfo对象
        ResultInfo<NoteType> resultInfo = noteTypeService.deleteType(typeId);
        // 3. 将ResultInfo对象转换成JSON格式的字符串，响应给ajax的回调函数
        JsonUtil.toJson(response, resultInfo);
    }


    private void typeList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        List<NoteType> noteTypes = noteTypeService.queryTypeList(user.getUserId());
        request.setAttribute("typeList", noteTypes);
        request.setAttribute("changePage", "type/list.jsp");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
