/*
 * Copyright © 2019 collin (1634753825@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.smart.cloud.starter.monitor.api.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sun.security.provider.certpath.SunCertPathBuilderException;

import java.security.cert.CertPathValidatorException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * 默认立即提醒的异常类型
 *
 * @author collin.li
 * @date 2026-01-11
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DefaultRemindExceptionType {

    /**
     * @see SQLException
     */
    SQL_EXCEPTION("SQLException"),
    /**
     * @see SQLTimeoutException
     */
    SQL_TIMEOUT_EXCEPTION("SQLTimeoutException"),
    /**
     * mysql表字段超长异常
     */
    MYSQL_DATA_TRUNCATION("MysqlDataTruncation"),
    /**
     * SQLTransientConnectionException
     */
    SQL_TRANSIENT_CONNECTION_EXCEPTION("SQLTransientConnectionException"),
    /**
     * SQLSyntaxErrorException
     */
    SQL_SYNTAX_ERROR_EXCEPTION("SQLSyntaxErrorException"),
    /**
     * BadSqlGrammarException
     */
    BAD_SQL_GRAMMAR_EXCEPTION("BadSqlGrammarException"),
    /**
     * TooManyResultsException
     */
    TOO_MANY_RESULTS_EXCEPTION("TooManyResultsException"),
    /**
     * MyBatisSystemException
     */
    MYBATIS_SYSTEM_EXCEPTION("MyBatisSystemException"),
    /**
     * TransactionTimedOutException
     */
    TRANSACTION_TIMEDOUT_EXCEPTION("TransactionTimedOutException"),
    /**
     * DataIntegrityViolationException
     */
    DATA_INTEGRITY_VIOLATION_EXCEPTION("DataIntegrityViolationException"),


    /**
     * @see NumberFormatException
     */
    NUMBER_FORMAT_EXCEPTION("NumberFormatException"),
    /**
     * @see ConcurrentModificationException
     */
    CONCURRENT_MODIFICATION_EXCEPTION("ConcurrentModificationException"),
    /**
     * @see NullPointerException
     */
    NULL_POINTER_EXCEPTION("NullPointerException"),
    /**
     * @see IndexOutOfBoundsException
     */
    INDEX_OUT_OF_BOUNDS_EXCEPTION("IndexOutOfBoundsException"),
    /**
     * @see ArrayIndexOutOfBoundsException
     */
    ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION("ArrayIndexOutOfBoundsException"),
    /**
     * @see StringIndexOutOfBoundsException
     */
    STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION("StringIndexOutOfBoundsException"),
    /**
     * @see ArrayStoreException
     */
    ARRAY_STORE_EXCEPTION("ArrayStoreException"),
    /**
     * @see IllegalMonitorStateException
     */
    ILLEGAL_MONITOR_STATE_EXCEPTION("IllegalMonitorStateException"),
    /**
     * IllegalStateException
     */
    ILLEGAL_STATE_EXCEPTION("IllegalStateException"),


    /**
     * @see ClassCastException
     */
    CLASS_CAST_EXCEPTION("ClassCastException"),
    /**
     * @see NoClassDefFoundError
     */
    NO_CLASS_DEF_FOUND_ERROR("NoClassDefFoundError"),
    /**
     * @see ClassNotFoundException
     */
    CLASS_NOT_FOUND_EXCEPTION("ClassNotFoundException"),
    /**
     * @see NoSuchElementException
     */
    NO_SUCH_ELEMENT_EXCEPTION("NoSuchElementException"),
    /**
     * NoSuchMethodError
     */
    NO_SUCH_METHOD_ERROR("NoSuchMethodError"),


    /**
     * @see StackOverflowError
     */
    STACK_OVERFLOW_ERROR("StackOverflowError"),
    /**
     * @see OutOfMemoryError
     */
    OUT_OF_MEMORY_ERROR("OutOfMemoryError"),
    /**
     * OutOfDirectMemoryError
     */
    OUT_OF_DIRECT_MEMORY_ERROR("OutOfDirectMemoryError"),
    /**
     * @see CertPathValidatorException
     */
    CERT_PATH_VALIDATOR_EXCEPTION("CertPathValidatorException"),
    /**
     * @see SunCertPathBuilderException
     */
    SUN_CERT_PATH_BUILDER_EXCEPTION("SunCertPathBuilderException"),


    /**
     * NoSuchBeanDefinitionException
     */
    NO_SUCH_BEAN_DEFINITION_EXCEPTION("NoSuchBeanDefinitionException"),
    /**
     * DiscoveryClientException
     */
    DISCOVERY_CLIENT_EXCEPTION("DiscoveryClientException"),
    /**
     * com.netflix.client.ClientException
     */
    CLIENT_EXCEPTION("ClientException"),
    /**
     * feign.RetryableException
     */
    RETRYABLE_EXCEPTION("RetryableException"),
    /**
     * ConnectTimeoutException
     */
    CONNECT_TIMEOUT_EXCEPTION("ConnectTimeoutException"),
    /**
     * SocketTimeoutException
     */
    SOCKET_TIMEOUT_EXCEPTION("SocketTimeoutException"),
    /**
     * TimeoutException
     */
    TIMEOUT_EXCEPTION("TimeoutException"),
    /**
     * ConnectException
     */
    CONNECT_EXCEPTION("ConnectException"),

    /**
     * RedisConnectionFailureException
     */
    REDIS_CONNECTION_FAILURE_EXCEPTION("RedisConnectionFailureException"),
    /**
     * UnknownHostException
     */
    UNKNOWN_HOST_EXCEPTION("UnknownHostException");

    /**
     * 异常类名
     */
    private String name;

}