package com.yjx.note.dao;

import com.yjx.note.po.NoteType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoteTypeDao {
    List<NoteType> queryNoteTypeList(Integer userId);

    /**
     * 通过类型ID查询云记记录的数量，返回云记数量
     *
     * @param typeId
     * @return
     */
    long findNoteCountByTypeId(String typeId);

    /**
     * 通过类型ID删除指定的类型记录，返回受影响的行数
     * @param typeId
     * @return
     */
    int deleteTypeById(String typeId);

    /**
     * 查询当前登录用户下，类型名称是否唯一，返回查询对象
     * @param typeName
     * @param userId
     * @return
     */
    NoteType checkTypeName(@Param("typeName") String typeName, @Param("userId") Integer userId);

    /**
     * 添加方法，返回主键
     * @param noteType
     * @return
     */
    Integer insertNoteType(NoteType noteType);

    /**
     * 修改方法，返回受影响的行数
     * @param typeName
     * @param typeId
     * @return
     */
    Integer updateType(@Param("typeName") String typeName,@Param("typeId") String typeId);

}
