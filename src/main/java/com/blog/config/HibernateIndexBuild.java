package com.blog.config;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.CacheMode;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class HibernateIndexBuild implements ApplicationListener<ApplicationEvent> {
    private final EntityManager entityManager;
    @Override
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("build hibernate index");
        SearchSession searchSession = Search.session(entityManager);
        MassIndexer indexer = searchSession.massIndexer();
        indexer.idFetchSize(100);
        indexer.threadsToLoadObjects(2);
        indexer.batchSizeToLoadObjects(25);
        indexer.cacheMode(CacheMode.REFRESH);
        try {
            indexer.startAndWait();
        } catch (InterruptedException e) {
            log.error("build hibernate index error", e);
            Thread.currentThread().interrupt();
        }
        log.info("build hibernate index success");
    }
}
