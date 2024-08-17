package com.blog.dao;

import com.blog.po.UserReportPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserReportPoRepository extends JpaRepository<UserReportPo, Long>, JpaSpecificationExecutor<UserReportPo> {
    @Query("SELECT u FROM UserReportPo u WHERE u.user.id = :userId")
    List<UserReportPo> findByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM UserReportPo u WHERE u.status = :status")
    List<UserReportPo> findByStatus(@Param("status") String status);

    @Query("SELECT u FROM UserReportPo u WHERE u.status = :status")
    List<UserReportPo> findByStatusIsPending(@Param("status") String status);

    @Modifying
    @Query("UPDATE UserReportPo u SET u.status = :status WHERE u.id = :id")
    void updateStatusToAccept(@Param("status") String status, @Param("id") Long id);

    @Modifying
    @Query("UPDATE UserReportPo u SET u.status = :status WHERE u.id = :id")
    void updateStatusToReject(@Param("status") String status,@Param("id") Long id);

    @Modifying
    @Query("UPDATE UserReportPo u SET u.status = :status WHERE u.id IN :batchList")
    void updateStatusToAcceptInBatch(@Param("status") String status,@Param("batchList") List batchList);

    @Modifying
    @Query("UPDATE UserReportPo u SET u.status = :status WHERE u.id IN :batchList")
    void updateStatusToRejectInBatch(@Param("status") String status,@Param("batchList") List partitionList);
}