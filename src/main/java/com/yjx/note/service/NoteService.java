package com.yjx.note.service;

import cn.hutool.core.util.StrUtil;
import com.yjx.note.dao.NoteDao;
import com.yjx.note.po.Note;
import com.yjx.note.util.MybatisUtils;
import com.yjx.note.util.Page;
import com.yjx.note.vo.NoteVo;
import com.yjx.note.vo.ResultInfo;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {
    public ResultInfo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {
        try(SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            ResultInfo<Map<String, Object>> resultInfo = new ResultInfo<>();

            List<NoteVo> list = noteDao.findNoteCountByDate(userId);
            if(list != null && list.size() > 0) {
                List<String> monthList = new ArrayList<>();
                List<Integer> countList = new ArrayList<>();
                for(NoteVo noteVo : list) {
                    monthList.add(noteVo.getGroupName());
                    countList.add((int) noteVo.getNoteCount());
                }
                Map<String, Object> map = new HashMap<>();
                map.put("monthArray", monthList);
                map.put("dataArray", countList);
                resultInfo.setCode(1);
                resultInfo.setResult(map);
            }
            return resultInfo;
        }
    }

    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content, String noteId, String lon, String lat) {
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            ResultInfo<Note> resultInfo = new ResultInfo<>();

            // 1. 参数的非空判断
            if (StrUtil.isBlank(typeId)) {
                resultInfo.setCode(0);
                resultInfo.setMsg("请选择云记类型！");
                return  resultInfo;
            }
            if (StrUtil.isBlank(title)) {
                resultInfo.setCode(0);
                resultInfo.setMsg("云记标题不能为空！");
                return  resultInfo;
            }
            if (StrUtil.isBlank(content)) {
                resultInfo.setCode(0);
                resultInfo.setMsg("云记内容不能为空！");
                return  resultInfo;
            }

            if (lon == null || lat == null) {
                lon = "116.404";
                lat = "39.909";
            }
            // 2. 设置回显对象 Note对象
            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);
            note.setTypeId(Integer.parseInt(typeId));
            note.setLon(Float.parseFloat(lon));
            note.setLat(Float.parseFloat(lat));

            //--------增加-------------
            // 判断云记ID是否为空
            if (!StrUtil.isBlank(noteId)) {
                note.setNoteId(Integer.parseInt(noteId));
            }

            resultInfo.setResult(note);


            // 3. 调用Dao层，添加云记记录，返回受影响的行数
            int row = noteDao.addOrUpdate(note);

            // 4. 判断受影响的行数
            if (row > 0) {
                resultInfo.setCode(1);
            } else {
                resultInfo.setCode(0);
                resultInfo.setResult(note);
                resultInfo.setMsg("更新失败！");
            }


            return resultInfo;
        }
    }

    /**
     * 分页查询云记列表
     1. 参数的非空校验
     如果分页参数为空，则设置默认值
     2. 查询当前登录用户的云记数量，返回总记录数 （long类型）
     3. 判断总记录数是否大于0
     4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
     5. 查询当前登录用户下当前页的数据列表，返回note集合
     6. 将note集合设置到page对象中
     7. 返回Page对象
     * @param pageNumStr
     * @param pageSizeStr
     * @param userId
     * @param title  条件查询的参数：标题
     * @return
     */
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId,String title,String date,String typeId) {
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {

            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            // 设置分页参数的默认值
            Integer pageNum = 1; // 默认当前页是第一页
            Integer pageSize = 5; // 默认每页显示5条数据
            // 1. 参数的非空校验 （如果参数不为空，则设置参数）
            if (!StrUtil.isBlank(pageNumStr)) {
                // 设置当前页
                pageNum = Integer.parseInt(pageNumStr);
            }
            if (!StrUtil.isBlank(pageSizeStr)) {
                // 设置每页显示的数量
                pageSize = Integer.parseInt(pageSizeStr);
            }
            // 2. 查询当前登录用户的云记数量，返回总记录数 （long类型） //--------------参数添加 title----- date---typeId-------
            long count = noteDao.findNoteCount(userId,title,date,typeId);

            // 3. 判断总记录数是否大于0
            if (count < 1) {
                return null;
            }
            // 4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
            Page<Note> page = new Page<>(pageNum, pageSize, count);

            // 得到数据库中分页查询的开始下标
            Integer index = (pageNum -1) * pageSize;

            // 5. 查询当前登录用户下当前页的数据列表，返回note集合  //--------------参数添加 title-- date-------------
            List<Note> noteList = noteDao.findNoteListByPage(userId, index, pageSize,title,date,typeId);

            // 6. 将note集合设置到page对象中
            page.setDataList(noteList);

            // 7. 返回Page对象
            return page;
        }
    }

    /**
     * 通过日期分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            return noteDao.findNoteCountByDate(userId);
        }
    }

    /**
     * 通过类型分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            return noteDao.findNoteCountByType(userId);
        }
    }

    /**
     * 查询云记详情
     1. 参数的非空判断
     2. 调用Dao层的查询，通过noteId查询note对象
     3. 返回note对象
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            // 1. 参数的非空判断
            if (StrUtil.isBlank(noteId)) {
                return  null;
            }
            // 2. 调用Dao层的查询，通过noteId查询note对象
            return  noteDao.findNoteById(noteId);
        }
    }

    /**
     * 删除云记
     1. 判断参数
     2. 调用Dao层的更新方法，返回受影响的行数
     3. 判断受影响的行数是否大于0
     如果大于0，返回1；否则返回0
     * @param noteId
     * @return
     */
    public Integer deleteNote(String noteId) {
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            // 1. 参数非空判断
            if (StrUtil.isBlank(noteId)) {
                return 0;
            }
            // 2. 调用Dao层的更新方法，返回受影响的行数
            int row = noteDao.deleteNoteById(noteId);
            // 3. 判断受影响的行数
            if (row > 0) {
                return 1;
            }
            return 0;
        }
    }

    public ResultInfo<List<Note>> queryNoteLonAndLat(Integer userId) {
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            NoteDao noteDao = sqlSession.getMapper(NoteDao.class);
            ResultInfo<List<Note>> resultInfo = new ResultInfo<>();

            List<Note> noteList = noteDao.queryNoteList(userId);

            if (noteList != null && noteList.size() > 0) {
                resultInfo.setCode(1);
                resultInfo.setResult(noteList);
            }
            return resultInfo;
        }
    }
}
