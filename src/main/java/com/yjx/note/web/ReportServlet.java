package com.yjx.note.web;

import com.yjx.note.po.Note;
import com.yjx.note.po.User;
import com.yjx.note.service.NoteService;
import com.yjx.note.util.JsonUtil;
import com.yjx.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    private NoteService noteService = new NoteService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("menu_page", "report");

        String actionName = request.getParameter("actionName");
        if ("info".equals(actionName)) {
            reportInfo(request, response);
        } else if ("month".equals(actionName)) {
            queryNoteCountByMonth(request, response);
        } else if ("location".equals(actionName)) {
             queryNoteLonAndLat(request, response);
        }
    }

    private void queryNoteLonAndLat(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        ResultInfo<List<Note>> resultInfo = noteService.queryNoteLonAndLat(user.getUserId());
        JsonUtil.toJson(response, resultInfo);
    }

    private void queryNoteCountByMonth(HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("user");
        ResultInfo<Map<String, Object>> resultInfo = noteService.queryNoteCountByMonth(user.getUserId());
        JsonUtil.toJson(response, resultInfo);
    }

    private void reportInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("changePage", "report/info.jsp");

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
