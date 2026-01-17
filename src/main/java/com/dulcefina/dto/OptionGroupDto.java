package com.dulcefina.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionGroupDto {
    private Long optionGroupId;
    private String code;
    private String name;
    private List<OptionValueDto> values;
}
