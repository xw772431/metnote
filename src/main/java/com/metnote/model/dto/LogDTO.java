package com.metnote.model.dto;

import com.metnote.model.dto.base.OutputConverter;
import com.metnote.model.entity.Log;
import com.metnote.model.enums.LogType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author johnniang
 * @date 3/19/19
 */
@Data
@ToString
@EqualsAndHashCode
public class LogDTO implements OutputConverter<LogDTO, Log> {

    private Long id;

    private String logKey;

    private LogType type;

    private String content;

    private String ipAddress;

    private Date createTime;
}
