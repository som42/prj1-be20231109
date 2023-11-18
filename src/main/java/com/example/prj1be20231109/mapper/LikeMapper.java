package com.example.prj1be20231109.mapper;

import com.example.prj1be20231109.domain.Like;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    @Delete("""
        DELETE FROM boardLike
        WHERE boardId = #{boardId}
          AND memberId = #{memberId}
        """)
    int delete(Like like);

    @Insert("""
        INSERT INTO boardLike (boardId, memberId)
        VALUES (#{boardId}, #{memberId})
        """)
    int insert(Like like);

    @Select("""
        SELECT COUNT(id) FROM boardLike
        WHERE boardId = #{boardId}
        """)
    int countByBoardId(Integer boardId);

    @Select("""
        SELECT * 
        FROM boardLike
        WHERE 
                boardId = #{boardId}
            AND memberId = #{memberId}   
        """)
    Like selectByBoardIdAndMemberId(@Param("boardId") Integer boardId, @Param("memberId") String memberId);
//     Like selectByBoardIdAndMemberId(Integer boardId, String memberId);
}
