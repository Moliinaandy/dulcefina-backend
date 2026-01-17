package com.dulcefina.service;

import com.dulcefina.dto.CustomizationRequest;
import com.dulcefina.dto.OptionGroupDto;
import com.dulcefina.dto.PreviewResponse;

import java.util.List;

public interface ConfiguratorService {
    List<OptionGroupDto> getOptions();
    PreviewResponse preview(CustomizationRequest request);
}
