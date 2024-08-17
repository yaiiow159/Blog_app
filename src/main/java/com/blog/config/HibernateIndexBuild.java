package com.blog.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.CacheMode;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;


@Configuration
@RequiredArgsConstructor
public class HibernateIndexBuild implements ApplicationListener<ApplicationEvent> {
    private final EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(HibernateIndexBuild.class);
    @Override
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public void onApplicationEvent(ApplicationEvent event) {
        logger.debug("初始化 hibernate index 中...");
        SearchSession searchSession = Search.session(entityManager);
        MassIndexer indexer = searchSession.massIndexer();
        indexer.idFetchSize(100);
        indexer.threadsToLoadObjects(2);
        indexer.batchSizeToLoadObjects(25);
        indexer.cacheMode(CacheMode.REFRESH);
        try {
            indexer.startAndWait();
        } catch (InterruptedException e) {
            logger.error("初始化 hibernate index 失敗 原因: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        logger.debug("初始化 hibernate index 完成");
    }
}
