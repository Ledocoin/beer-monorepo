package org.example.beerProj.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beerProj.config.properties.KafkaProperties;
import org.example.beerProj.dto.CreateStoreWithEmployeeDto;
import org.example.beerProj.entity.StoreEntity;
import org.example.beerProj.mapper.StoreMapper;
import org.example.beerProj.repository.StoreRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncStoreService {
    private final KafkaProperties properties;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Transactional
    public void create(CreateStoreWithEmployeeDto createStoreDto) {
        log.info("Работает. Получены данные: {}", createStoreDto);
 /// mapper show
        StoreEntity store = StoreEntity.builder()
                .address(createStoreDto.getAddress())
                .phone(createStoreDto.getPhone())
                .build();
        storeRepository.save(store);

        var topic = properties.getFlows().getEmployeeCreation().getTopic();
        var source = properties.getFlows().getEmployeeCreation().getSource();

        createStoreDto.getEmployees().forEach(emp -> {
            var msg = MessageBuilder
                    .withPayload(emp)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader("source", source)
                    .build();

            kafkaTemplate.send(msg);
            log.info("Sent employee to topic {}: {}", topic, emp);
        });
        log.info("Store created id={}, address={}", store.getId(), store.getAddress());
    }
}
