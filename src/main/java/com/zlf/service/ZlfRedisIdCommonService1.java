package com.zlf.service;

import com.zlf.dto.GeneratorIdDto;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public abstract class ZlfRedisIdCommonService1 {

    public String idConverter(Long id) {
        return String.valueOf(id);
    }

    public Long generatorIdByLength(GeneratorIdDto dto) {
        return nextGeneratorId(dto);
    }

    public String buildTab(GeneratorIdDto dto) {
        return dto.getApplicationName() + ":" + dto.getTabName();
    }

    protected void checkDto(GeneratorIdDto dto) {
        if (StringUtils.isBlank(dto.getApplicationName())) {
            throw new RuntimeException("applicationName必传");
        }
        if (StringUtils.isBlank(dto.getTabName())) {
            throw new RuntimeException("tabName必传");
        }
        if (Objects.nonNull(dto.getLength()) && (dto.getLength() <= 8 || dto.getLength() > 19)) {
            throw new RuntimeException("ZlfRedisIdByScripts1Service.id位数有误,id位数只能(8,19)位,因为mysql的bigint最大长度19位" + dto.getLength());
        }
    }

    protected Long loopIdCommon(String tab, String y_m_d_h_m_s, int len, int retryTimes) {
        for (int i = 0; i < retryTimes; ++i) {
            Long id = this.nextId1(tab, y_m_d_h_m_s, len);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    protected abstract Long nextGeneratorId(GeneratorIdDto dto);

    protected abstract Long loopId1(String tab, String y_m_d_h_m_s, int len, int retryTimes);

    protected abstract Long nextId1(String tab, String y_m_d_h_m_s, int len);

}
