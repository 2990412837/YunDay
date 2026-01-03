package com.yjx.note.dao;

import com.yjx.note.po.Note;
import com.yjx.note.vo.NoteVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoteDao {
    //添加操作或者修改
    int addOrUpdate(Note note);

    long findNoteCount(@Param("userId")Integer userId,@Param("title")String title,@Param("date")String date,@Param("typeId")String typeId);

    List<Note> findNoteListByPage(@Param("userId") Integer userId, @Param("index")Integer index, @Param("pageSize") Integer pageSize, @Param("title") String title, @Param("date") String date, @Param("typeId") String typeId);

    List<NoteVo> findNoteCountByDate(Integer userId);

    List<NoteVo> findNoteCountByType(Integer userId);

    public Note findNoteById(String noteId);

    int deleteNoteById(String noteId);

    List<Note> queryNoteList(Integer userId);
}