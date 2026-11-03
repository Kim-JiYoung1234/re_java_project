package com.human.ofim.mapper;

import com.human.ofim.dto.BoardDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RankingMapper {
    // 커뮤니티 사이드바용 Top 5
    List<BoardDto> selectTopRankers();
    
    // 전체 랭킹 페이지용
    List<BoardDto> selectAllRankers();
}