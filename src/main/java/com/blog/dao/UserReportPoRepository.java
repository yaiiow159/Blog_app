package com.blog.dao;

import com.blog.po.UserPo;
import com.blog.po.UserReportPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserReportPoRepository extends JpaRepository<UserReportPo, Long>, JpaSpecificationExecutor<UserReportPo> {
    @Query("SELECT u FROM UserReportPo u WHERE u.user.id = :userId")
    List<UserReportPo> findByUserId(@Param("userId") Long userId);

}