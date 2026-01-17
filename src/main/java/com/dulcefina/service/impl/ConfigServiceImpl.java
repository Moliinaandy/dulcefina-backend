package com.dulcefina.service.impl;

import com.dulcefina.entity.Config;
import com.dulcefina.repository.ConfigRepository;
import com.dulcefina.service.ConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    public ConfigServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public List<Config> findAll() {
        return configRepository.findAll();
    }

    @Override
    public Optional<Config> findByKey(String key) {
        return configRepository.findById(key);
    }

    @Override
    public Config create(Config config) {
        config.setUpdatedAt(LocalDateTime.now());
        return configRepository.save(config);
    }

    @Override
    public Config update(String key, Config config) {
        Config existing = configRepository.findById(key)
                .orElseThrow(() -> new NoSuchElementException("Config not found: " + key));
        existing.setConfigValue(config.getConfigValue());
        existing.setUpdatedAt(LocalDateTime.now());
        return configRepository.save(existing);
    }

    @Override
    public void delete(String key) {
        if (!configRepository.existsById(key)) {
            throw new NoSuchElementException("Config not found: " + key);
        }
        configRepository.deleteById(key);
    }
}
