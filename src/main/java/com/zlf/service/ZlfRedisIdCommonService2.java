package com.zlf.service;

import com.zlf.dto.GeneratorIdDto;
import org.apache.commons.lang3.StringUtils;

public abstract class ZlfRedisIdCommonService2 {

    public String idConverter(Long id) {
        return String.valueOf(id);
    }

    public Long generatorId(GeneratorIdDto dto) {
        return nextGeneratorId(dto);
    }

    protected String buildTab(GeneratorIdDto dto) {
        return dto.getApplicationName() + ":" + dto.getTabName();
    }

    protected void checkDto(GeneratorIdDto dto) {
        if (StringUtils.isBlank(dto.getApplicationName())) {
            throw new RuntimeException("applicationName必传");
        }
        if (StringUtils.isBlank(dto.getTabName())) {
            throw new RuntimeException("tabName必传");
        }
    }

    protected Long loopIdCommon(String tab, int retryTimes) {
        for (int i = 0; i < retryTimes; ++i) {
            Long id = nextId(tab);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    protected abstract Long nextGeneratorId(GeneratorIdDto dto);

    protected abstract Long loopId(String tab, int retryTimes);

    protected abstract Long nextId(String tab);

}
