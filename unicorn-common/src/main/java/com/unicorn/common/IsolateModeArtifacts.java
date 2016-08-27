package com.unicorn.common;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.cassandra.config.CassandraCqlSessionFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.util.ReflectionUtils;

/**
 * <p>
 * Classes here makes it possible to run without real Cassandra or REDIS up and running.
 * Very useful while running Rapid on laptop, use -Disolate.mode=true to enable this in
 * yaml file or as a -D JVM argument.
 * </p>
 */
class IsolateModeArtifacts extends CassandraSessionFactoryBean {

    static class CassandraCqlSessionFactoryBeanFake extends CassandraCqlSessionFactoryBean {

        @Override
        public void afterPropertiesSet() throws Exception {

            ReflectionUtils.setField(CassandraCqlSessionFactoryBean.class.getDeclaredField("session"), this, new CassandraSessionFake());
        }
    }


    static class CassandraSessionFake implements Session {

        @Override
        public String getLoggedKeyspace() {
            return null;
        }

        @Override
        public Session init() {
            return null;
        }

        @Override
        public ResultSet execute(String s) {
            return null;
        }

        @Override
        public ResultSet execute(String s, Object... objects) {
            return null;
        }

        @Override
        public ResultSet execute(Statement statement) {
            return null;
        }

        @Override
        public ResultSetFuture executeAsync(String s) {
            return null;
        }

        @Override
        public ResultSetFuture executeAsync(String s, Object... objects) {
            return null;
        }

        @Override
        public ResultSetFuture executeAsync(Statement statement) {
            return null;
        }

        @Override
        public PreparedStatement prepare(String s) {
            return null;
        }

        @Override
        public PreparedStatement prepare(RegularStatement regularStatement) {
            return null;
        }

        @Override
        public ListenableFuture<PreparedStatement> prepareAsync(String s) {
            return null;
        }

        @Override
        public ListenableFuture<PreparedStatement> prepareAsync(RegularStatement regularStatement) {
            return null;
        }

        @Override
        public CloseFuture closeAsync() {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public Cluster getCluster() {
            return null;
        }

        @Override
        public State getState() {
            return null;
        }
    }


}

