package com.dulcefina.service.impl;

import com.dulcefina.dto.*;
import com.dulcefina.entity.*;
import com.dulcefina.repository.*;
import com.dulcefina.service.ConfiguratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ConfiguratorServiceImpl implements ConfiguratorService {

    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;
    private final RecipeItemRepository recipeItemRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ConfiguratorServiceImpl(OptionGroupRepository optionGroupRepository,
                                   OptionValueRepository optionValueRepository,
                                   RecipeItemRepository recipeItemRepository,
                                   RawMaterialRepository rawMaterialRepository) {
        this.optionGroupRepository = optionGroupRepository;
        this.optionValueRepository = optionValueRepository;
        this.recipeItemRepository = recipeItemRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    @Override
    public List<OptionGroupDto> getOptions() {
        List<OptionGroup> groups = optionGroupRepository.findAllByIsActiveTrue();
        return groups.stream().map(g -> {
            List<OptionValueDto> vals = g.getValues().stream()
                    .filter(Objects::nonNull)
                    .filter(ov -> ov.getIsActive() == null || ov.getIsActive())
                    .map(ov -> OptionValueDto.builder()
                            .optionValueId(ov.getOptionValueId())
                            .name(ov.getName())
                            .slug(ov.getSlug())
                            .priceModifier(ov.getPriceModifier() == null ? 0.0 : ov.getPriceModifier())
                            .multiplier(ov.getMultiplier() == null ? 1.0 : ov.getMultiplier())
                            .imageUrl(ov.getImageUrl())
                            .build())
                    .collect(Collectors.toList());
            return OptionGroupDto.builder()
                    .optionGroupId(g.getOptionGroupId())
                    .code(g.getCode())
                    .name(g.getName())
                    .values(vals)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public PreviewResponse preview(CustomizationRequest request) {
        // parámetros entrantes
        int quantity = (request.getQuantity() == null || request.getQuantity() < 1) ? 1 : request.getQuantity();
        Double basePrice = (request.getBasePrice() == null) ? 0.0 : request.getBasePrice();
        List<Long> selectedIds = request.getOptionValueIds() == null ? Collections.emptyList() : request.getOptionValueIds();

        // determinar tamaño (multiplier y price extra)
        double sizeMultiplier = 1.0;
        double sizeExtraPrice = 0.0;
        List<String> previewLayers = new ArrayList<>();

        if (request.getSizeOptionId() != null) {
            Optional<OptionValue> opt = optionValueRepository.findById(request.getSizeOptionId());
            if (opt.isPresent()) {
                OptionValue sizeOpt = opt.get();
                Double mul = sizeOpt.getMultiplier();
                if (mul != null) sizeMultiplier = mul;
                Double pm = sizeOpt.getPriceModifier();
                if (pm != null) sizeExtraPrice = pm;
                if (sizeOpt.getImageUrl() != null) previewLayers.add(sizeOpt.getImageUrl());
            }
        }

        // obtener todas las optionValues seleccionadas
        List<OptionValue> selectedValues = !selectedIds.isEmpty()
                ? optionValueRepository.findAllById(selectedIds)
                : Collections.emptyList();

        // sumar price modifiers
        double optionsExtra = selectedValues.stream()
                .mapToDouble(v -> v.getPriceModifier() == null ? 0.0 : v.getPriceModifier())
                .sum();

        // añadir imagenes de las opciones como capas (sabor, cobertura, etc.)
        selectedValues.stream()
                .map(OptionValue::getImageUrl)
                .filter(Objects::nonNull)
                .forEach(previewLayers::add);

        // calcular precio aproximado
        double subtotalPerUnit = (basePrice * sizeMultiplier) + sizeExtraPrice + optionsExtra;
        double estimatedPrice = subtotalPerUnit * quantity;

        // calcular requerimientos de materias primas
        Map<Long, Double> requiredMap = new HashMap<>();
        Map<Long, String> unitMeasures = new HashMap<>();
        for (OptionValue ov : selectedValues) {
            List<RecipeItem> recipeItems = recipeItemRepository.findByOptionValue_OptionValueId(ov.getOptionValueId());
            for (RecipeItem ri : recipeItems) {
                Long rawId = ri.getRawMaterial().getRawId();
                double qty = (ri.getQuantity() == null ? 0.0 : ri.getQuantity()) * sizeMultiplier * quantity;
                requiredMap.merge(rawId, qty, Double::sum);
                unitMeasures.putIfAbsent(rawId, ri.getUnitMeasure());
            }
        }

        // construir lista RequiredRawDto comparando con stock actual
        List<RequiredRawDto> requiredList = new ArrayList<>();
        for (Map.Entry<Long, Double> e : requiredMap.entrySet()) {
            Long rawId = e.getKey();
            Double needed = e.getValue();
            Optional<RawMaterial> maybeRaw = rawMaterialRepository.findById(rawId);
            if (maybeRaw.isPresent()) {
                RawMaterial raw = maybeRaw.get();
                Double available = raw.getStockQuantity() == null ? 0.0 : raw.getStockQuantity();
                RequiredRawDto r = RequiredRawDto.builder()
                        .rawId(rawId)
                        .name(raw.getName())
                        .requiredQuantity(round(needed))
                        .unitMeasure(unitMeasures.get(rawId))
                        .availableQuantity(round(available))
                        .ok(available >= needed)
                        .build();
                requiredList.add(r);
            } else {
                // raw material not found
                RequiredRawDto r = RequiredRawDto.builder()
                        .rawId(rawId)
                        .name("UNKNOWN")
                        .requiredQuantity(round(needed))
                        .unitMeasure(unitMeasures.get(rawId))
                        .availableQuantity(0.0)
                        .ok(false)
                        .build();
                requiredList.add(r);
            }
        }

        PreviewResponse resp = PreviewResponse.builder()
                .estimatedPrice(round(estimatedPrice))
                .previewLayerUrls(previewLayers)
                .requiredRawMaterials(requiredList)
                .build();

        return resp;
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
