package com.dulcefina.service;

import com.dulcefina.entity.Config;

import java.util.List;
import java.util.Optional;

public interface ConfigService {
    List<Config> findAll();
    Optional<Config> findByKey(String key);
    Config create(Config config);
    Config update(String key, Config config);
    void delete(String key);
}
