package com.qinyoucheng.supply.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TimeSlotVO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate slotDate;

    private String startTime;

    private String endTime;

    private Integer maxCapacity;

    private Integer currentBooked;

    private Integer remaining;

    private Integer status;

    public static TimeSlotVO fromEntity(com.qinyoucheng.supply.entity.TimeSlot entity) {
        TimeSlotVO vo = new TimeSlotVO();
        vo.setId(entity.getId());
        vo.setSlotDate(entity.getSlotDate());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        vo.setMaxCapacity(entity.getMaxCapacity());
        vo.setCurrentBooked(entity.getCurrentBooked());
        vo.setRemaining(entity.getMaxCapacity() - entity.getCurrentBooked());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
