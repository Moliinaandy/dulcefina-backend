package com.dulcefina.controller;

import com.dulcefina.dto.CustomizationRequest;
import com.dulcefina.dto.OptionGroupDto;
import com.dulcefina.dto.PreviewResponse;
import com.dulcefina.service.ConfiguratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/configurator")
public class ConfiguratorController {

    private final ConfiguratorService configuratorService;

    public ConfiguratorController(ConfiguratorService configuratorService) {
        this.configuratorService = configuratorService;
    }



    /** calcula el precio estimado y los requerimientos de materias primas **/
    @PostMapping("/preview")
    public ResponseEntity<PreviewResponse> preview(@RequestBody CustomizationRequest request) {
        PreviewResponse resp = configuratorService.preview(request);
        return ResponseEntity.ok(resp);
    }

    /** retorna ok/false y detalles **/
    @PostMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(@RequestBody CustomizationRequest request) {
        PreviewResponse resp = configuratorService.preview(request);
        boolean ok = resp.getRequiredRawMaterials().stream().allMatch(r -> r.isOk());
        return ResponseEntity.ok(Map.of("ok", ok, "details", resp.getRequiredRawMaterials()));
    }
}
