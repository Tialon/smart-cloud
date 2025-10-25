package io.github.smart.cloud.starter.monitor.api.core;

import io.github.smart.cloud.monitor.common.dto.wework.AbstractWeworkRobotMessageDTO;
import io.github.smart.cloud.starter.monitor.api.dto.ApiAlertCommonDTO;

import java.util.List;

/**
 * 消息工厂
 *
 * @param <T>
 * @author collin.li
 * @date 2025-10-25
 */
public interface IMessageFactory<T extends ApiAlertCommonDTO> {

    /**
     * 构建企业微信机器人消息体（汇总通知）
     *
     * @param apiExceptions
     * @return
     */
    AbstractWeworkRobotMessageDTO buildSummaryAlertMessages(List<T> apiExceptions);

    /**
     * 构建企业微信机器人消息体（立即通知）
     *
     * @param apiExceptionAlert
     * @return
     */
    AbstractWeworkRobotMessageDTO buildImmediateAlertMessage(T apiExceptionAlert);

}