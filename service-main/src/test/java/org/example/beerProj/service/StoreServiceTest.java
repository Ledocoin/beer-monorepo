package org.example.beerProj.service;


import org.assertj.core.api.Assertions;
import org.example.beerProj.dto.StoreRequest;
import org.example.beerProj.dto.StoreResponse;
import org.example.beerProj.entity.StoreEntity;
import org.example.beerProj.mapper.StoreMapper;
import org.example.beerProj.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreMapper storeMapper;

    @InjectMocks
    private StoreService storeService;

    @Test
    void getAllStores_ShouldReturnPage() {
        StoreEntity storeEntity = new StoreEntity();
        List<StoreEntity> list = List.of(storeEntity);
        StoreResponse storeResponse = StoreResponse.builder()
                .id("1L")
                .address("Address1")
                .phone("38094L")
                .build();

        Mockito.when(storeRepository.findAll(Mockito.any())).thenReturn(new PageImpl<>(list));
        Mockito.when(storeMapper.toResponse(Mockito.any())).thenReturn(storeResponse);

        Page<StoreResponse> response = storeService.getAllStores(0,10);


        Assertions.assertThat(response).hasSize(1);
        Assertions.assertThat(response.getContent().get(0).getAddress()).isEqualTo("Address1");
        Mockito.verify(storeRepository).findAll(Mockito.any());
        Mockito.verify(storeMapper).toResponse(Mockito.any());
    }

    @Test
    void getAllStores_shouldReturnEmptyList() {
        Mockito.when(storeRepository.findAll(Mockito.any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<StoreResponse> result = storeService.getAllStores(0,10);

        Assertions.assertThat(result).isEmpty();
        Mockito.verify(storeRepository).findAll(Mockito.any());
        Mockito.verifyNoInteractions(storeMapper);
    }

    @Test
    void getStoreById_ShouldReturnStoreResponse_whenStoreExists() {
        String id = "1L";
        StoreEntity store = new StoreEntity();
        StoreResponse expectedResponse = StoreResponse.builder()
                .id("1L")
                .address("Address1")
                .phone("38094L")
                .build();

        Mockito.when(storeRepository.findById(id)).thenReturn(Optional.of(store));
        Mockito.when(storeMapper.toResponse(store)).thenReturn(expectedResponse);

        StoreResponse actualResponse = storeService.getStoreById(id);

        Assertions.assertThat(actualResponse).isEqualTo(expectedResponse);
        Mockito.verify(storeRepository).findById(id);
        Mockito.verify(storeMapper).toResponse(store);
    }

    @Test
    void getStoreById_ShouldThrowException_whenStoreDoesNotExist() {
        String wrongId = "2L";

        Mockito.when(storeRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> storeService.getStoreById(wrongId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("404");

        Mockito.verify(storeRepository).findById(wrongId);
        Mockito.verifyNoInteractions(storeMapper);
    }

    @Test
    void createStore_ShouldSaveEntityAndReturnResponse() {
        StoreRequest request = new StoreRequest("Address2", "289305L");

        StoreEntity mappedEntity = new StoreEntity();
        StoreEntity savedEntity = new StoreEntity();

        StoreResponse response = new StoreResponse("1L","Address2", "289305L");

        Mockito.when(storeMapper.toEntity(request)).thenReturn(mappedEntity);
        Mockito.when(storeRepository.save(mappedEntity)).thenReturn(savedEntity);
        Mockito.when(storeMapper.toResponse(savedEntity)).thenReturn(response);

        StoreResponse actualResponse = storeService.createStore(request);

        Assertions.assertThat(actualResponse).isEqualTo(response);
        Mockito.verify(storeMapper).toEntity(request);
        Mockito.verify(storeRepository).save(mappedEntity);
        Mockito.verify(storeMapper).toResponse(savedEntity);
    }

    @Test
    void createStore_ShouldThrowException_whenDataIsInvalid() {
        StoreRequest invalidRequest = new StoreRequest("HAHAHHA", "289305L");

        Mockito.when(storeMapper.toEntity(invalidRequest)).thenThrow(new IllegalArgumentException("InvalidType"));

        Assertions.assertThatThrownBy(() -> storeService.createStore(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class);

        Mockito.verify(storeMapper).toEntity(invalidRequest);
        Mockito.verifyNoInteractions(storeRepository);
    }

    @Test
    void putStore_ShouldUpdateStore_whenEntityExists() {
        String id = "1L";
        StoreRequest request = new StoreRequest("NewAddress", "28900205L");
        StoreEntity currentEntity = new StoreEntity("1L", "OldAddress", "4500387L");
        StoreEntity savedEntity = new StoreEntity();

        StoreResponse response = new StoreResponse("1L","NewAddress", "28900205L");

        Mockito.when(storeRepository.findById(id)).thenReturn(Optional.of(currentEntity));
        Mockito.when(storeRepository.save(currentEntity)).thenReturn(savedEntity);
        Mockito.when(storeMapper.toResponse(savedEntity)).thenReturn(response);

        StoreResponse actualResponse = storeService.putStore(id, request);

        Assertions.assertThat(actualResponse).isEqualTo(response);

        Mockito.verify(storeRepository).findById(id);
        Mockito.verify(storeMapper).updateEntity(request, currentEntity);
        Mockito.verify(storeRepository).save(currentEntity);
        Mockito.verify(storeMapper).toResponse(savedEntity);
    }

    @Test
    void putStore_ShouldThrowException_whenDataIsInvalid() {
        String wrongId = "2L";
        StoreRequest invalidRequest = new StoreRequest("NewAddress", "28900205L");
        Mockito.when(storeRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> storeService.putStore(wrongId, invalidRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void patchStore_ShouldUpdateStore_whenEntityExists() {
        String id = "1L";
        StoreRequest request = new StoreRequest("NewAddress", "28900205L");

        StoreEntity currentEntity = new StoreEntity("1L", "OldAddress", "4500387L");

        StoreEntity savedEntity = new StoreEntity();

        StoreResponse response = new StoreResponse("1L","OldAddress", "28900205L");

        Mockito.when(storeRepository.findById(id)).thenReturn(Optional.of(currentEntity));
        Mockito.when(storeRepository.save(currentEntity)).thenReturn(savedEntity);
        Mockito.when(storeMapper.toResponse(savedEntity)).thenReturn(response);

        StoreResponse actualResponse = storeService.patchStore(id, request);

        Assertions.assertThat(actualResponse).isEqualTo(response);

        Mockito.verify(storeRepository).findById(id);
        Mockito.verify(storeMapper).patchEntityFromDto(request, currentEntity);
        Mockito.verify(storeRepository).save(currentEntity);
        Mockito.verify(storeMapper).toResponse(savedEntity);
    }

    @Test
    void patchStore_ShouldThrowException_whenDataIsInvalid() {
        String wrongId = "2L";
        StoreRequest invalidRequest = new StoreRequest("NewAddress", "28900205L");

        Mockito.when(storeRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> storeService.patchStore(wrongId, invalidRequest))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }
/*
    @Test
    void deleteStore_ShouldDeleteEntity_whenEntityExists() {
        String id = "1L";
        StoreEntity entity = new StoreEntity();
        Mockito.when(storeRepository.findById(id)).thenReturn(Optional.of(entity));

        storeService.deleteStore(id);

        Mockito.verify(storeRepository).findById(id);
        Mockito.verify(storeRepository).delete(entity);
    }

 */

    @Test
    void deleteStore_ShouldThrowException_whenDataIsInvalid() {
        String wrongId = "2L";
        Mockito.when(storeRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> storeService.deleteStore(wrongId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");

        Mockito.verify(storeRepository).findById(wrongId);
        Mockito.verify(storeRepository, Mockito.never()).delete(Mockito.any());

    }
}
