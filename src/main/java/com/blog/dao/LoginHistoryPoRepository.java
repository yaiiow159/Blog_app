package com.blog.dao;

import com.blog.po.LoginHistoryPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface LoginHistoryPoRepository extends JpaRepository<LoginHistoryPo, Long>, JpaSpecificationExecutor<LoginHistoryPo> {
    @Query("SELECT lg FROM LoginHistoryPo lg WHERE lg.username = :username")
    List<LoginHistoryPo> findByUsername(@Param("username") String username);
    void deleteByUsername(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM LoginHistoryPo lg WHERE lg.loginTimestamp < :localDateTime")
    void deleteByLoginTimestampBefore(@Param("localDateTime") LocalDateTime localDateTime);


    @Query("SELECT lg FROM LoginHistoryPo lg WHERE lg.username = :username ORDER BY lg.loginTimestamp DESC limit 1")
    Optional<LoginHistoryPo> findFirstByUsernameOrderByLoginTimestampDesc(@Param("username") String username);

    @Query("SELECT lg FROM LoginHistoryPo lg WHERE lg.id = :id AND lg.username = :username")
    Optional<LoginHistoryPo> findByIdAndUsername(@Param("id") Long id,@Param("username") String username);
}