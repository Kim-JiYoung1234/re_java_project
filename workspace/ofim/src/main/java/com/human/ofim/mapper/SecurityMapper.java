package com.human.ofim.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 🌟 추가됨
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SecurityMapper {

    @Select("SELECT violation_count FROM security_blacklist WHERE ip_address = #{ip}")
    Integer getViolationCount(@Param("ip") String ip); // 🌟 @Param 추가

    @Insert("INSERT INTO security_blacklist (ip_address, reason, violation_count, last_violation_date) " +
            "VALUES (#{ip}, #{reason}, 1, NOW())")
    void insertBlacklist(@Param("ip") String ip, @Param("reason") String reason); // 🌟 @Param 추가

    @Update("UPDATE security_blacklist SET violation_count = violation_count + 1, " +
            "reason = #{reason}, last_violation_date = NOW() WHERE ip_address = #{ip}")
    void updateBlacklist(@Param("ip") String ip, @Param("reason") String reason); // 🌟 @Param 추가
}