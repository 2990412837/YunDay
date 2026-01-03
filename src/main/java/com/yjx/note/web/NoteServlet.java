package com.yjx.note.web;

import cn.hutool.core.util.StrUtil;
import com.yjx.note.po.Note;
import com.yjx.note.po.NoteType;
import com.yjx.note.po.User;
import com.yjx.note.service.NoteService;
import com.yjx.note.service.NoteTypeService;
import com.yjx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("menu_page", "note");

        String actionName = request.getParameter("actionName");
        if ("view".equals(actionName)) {
            noteView(request, response);
        } else if ("addOrUpdate".equals(actionName)) {
            addOrUpdate(request, response);
        } else if ("detail".equals(actionName)) {
            // 查询云记详情
            noteDetail(request, response);
        } else if ("delete".equals(actionName)) {
            // 删除云记
            noteDelete(request, response);
        }
    }

    /**
     * 删除云记
     1. 接收参数 （noteId）
     2. 调用Service层删除方法，返回状态码 （1=成功，0=失败）
     3. 通过流将结果响应给ajax的回调函数 （输出字符串）
     * @param request
     * @param response
     */
    private void noteDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 接收参数 （noteId）
        String noteId = request.getParameter("noteId");
        // 2. 调用Service层删除方法，返回状态码 （1=成功，0=失败）
        Integer code = noteService.deleteNote(noteId);
        // 3. 通过流将结果响应给ajax的回调函数 （输出字符串）
        response.getWriter().write(code + "");
        response.getWriter().close();
    }

    /**
     * 查询云记详情
     1. 接收参数 （noteId）
     2. 调用Service层的查询方法，返回Note对象
     3. 将Note对象设置到request请求域中
     4. 设置首页动态包含的页面值
     5. 请求转发跳转到index.jsp
     * @param request
     * @param response
     */
    private void noteDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 接收参数 （noteId）
        String noteId = request.getParameter("noteId");
        // 2. 调用Service层的查询方法，返回Note对象
        Note note = noteService.findNoteById(noteId);
        // 3. 将Note对象设置到request请求域中
        request.setAttribute("note", note);
        // 4. 设置首页动态包含的页面值
        request.setAttribute("changePage","note/detail.jsp");
        // 5. 请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    private void addOrUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 1. 接收参数 （类型ID、标题、内容）
        String typeId = request.getParameter("typeId");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        String lon = request.getParameter("lon");
        String lat = request.getParameter("lat");

        // -----------如果是修改操作，需要接收noteId------------
        String noteId = request.getParameter("noteId");

        // 2. 调用Service层方法，返回resultInfo对
        ResultInfo<Note> resultInfo = noteService.addOrUpdate(typeId, title, content,noteId, lon, lat);

        // 3. 判断resultInfo的code值
        if (resultInfo.getCode() == 1) {
            // 重定向跳转到首页 index
            response.sendRedirect("index");
        } else {
            // 将resultInfo对象设置到request作用域
            request.setAttribute("resultInfo", resultInfo);
            // 请求转发跳转到note?actionName=view
            String url = "note?actionName=view";
            // 如果是修改操作，需要传递noteId
            if (!StrUtil.isBlank(noteId)) {
                url += "&noteId="+noteId;
            }
            request.getRequestDispatcher(url).forward(request, response);
        }
    }


    private void noteView(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /* 修改操作 */
        // 得到要修改的云记ID
        String noteId = request.getParameter("noteId");
        // 通过noteId查询云记对象
        Note note = noteService.findNoteById(noteId);
        // 将note对象设置到请求域中
        request.setAttribute("noteInfo", note);
        /* 修改操作 */
        // 1. 从Session对象中获取用户对象
        User user = (User) request.getSession().getAttribute("user");
        // 2. 通过用户ID查询对应的类型列表
        List<NoteType> typeList = new NoteTypeService().queryTypeList(user.getUserId());
        // 3. 将类型列表设置到request请求域中

        request.setAttribute("typeList", typeList);
        // 4. 设置首页动态包含的页面值
        request.setAttribute("changePage","note/view.jsp");
        // 5. 请求转发跳转到index.jsp
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
