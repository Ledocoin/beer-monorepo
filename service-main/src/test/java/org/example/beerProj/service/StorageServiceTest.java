package org.example.beerProj.service;

import org.assertj.core.api.Assertions;
import org.example.beerProj.dto.StorageDto;
import org.example.beerProj.dto.StorageResponse;
import org.example.beerProj.entity.StorageEntity;
import org.example.beerProj.mapper.StorageMapper;
import org.example.beerProj.repository.StorageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StorageServiceTest {
    @Mock
    private StorageMapper storageMapper;

    @Mock
    private StorageRepository storageRepository;

    @InjectMocks
    private StorageService storageService;

    @Test
    void getAllStorages_ShouldReturnMappedPage() {
        int page = 0;
        int size = 2;

        StorageEntity entity1 = new StorageEntity();
        entity1.setId(1L);
        StorageEntity entity2 = new StorageEntity();
        entity2.setId(2L);
        Page<StorageEntity> pageResult = new PageImpl<>(List.of(entity1, entity2));

        StorageResponse response1 = new StorageResponse(1L,"b","s",4L);
        StorageResponse response2 = new StorageResponse(2L,"b","s",4L);

        Mockito.doReturn(pageResult).when(storageRepository).findAll(PageRequest.of(page, size));
        Mockito.doReturn(response1).when(storageMapper).toResponse(Mockito.refEq(entity1));
        Mockito.doReturn(response2).when(storageMapper).toResponse(Mockito.refEq(entity2));

        Page<StorageResponse> result = storageService.getAllStorages(page, size);

        Assertions.assertThat(result.getContent()).containsExactly(response1, response2);
        Mockito.verify(storageRepository).findAll(PageRequest.of(page, size));
        Mockito.verify(storageMapper).toResponse(entity1);
        Mockito.verify(storageMapper).toResponse(entity2);

    }

    @Test
    void createStorage_ShouldMapAndSaveAndReturnResponse() {
        StorageDto dto = new StorageDto("b","s",4L);
        StorageEntity entity = new StorageEntity(1L,"b","s",4L);
        StorageResponse expected = new StorageResponse(1L,"b","s",4L);

        Mockito.doReturn(entity).when(storageMapper).toEntity(dto);
        Mockito.doReturn(entity).when(storageRepository).save(entity);
        Mockito.doReturn(expected).when(storageMapper).toResponse(entity);

        StorageResponse result = storageService.createStorage(dto);

        Assertions.assertThat(result).isEqualTo(expected);
        Mockito.verify(storageMapper).toEntity(dto);
        Mockito.verify(storageRepository).save(entity);
        Mockito.verify(storageMapper).toResponse(entity);
    }

    @Test
    void deleteStorage_ShouldDeleteEntity_WhenIdExists() {
        Long id = 1L;
        StorageEntity entity = new StorageEntity();

        Mockito.doReturn(Optional.of(entity)).when(storageRepository).findById(id);

        storageService.deleteStorage(id);

        Mockito.verify(storageRepository).findById(id);
        Mockito.verify(storageRepository).delete(entity);
    }

    @Test
    void deleteStorage_ShouldThrow_WhenIdNotFound() {
        Long id = 1L;
        Mockito.doReturn(Optional.empty()).when(storageRepository).findById(id);

        Assertions.assertThatThrownBy(() -> storageService.deleteStorage(id))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");

        Mockito.verify(storageRepository).findById(id);
        Mockito.verify(storageRepository, Mockito.never()).delete(Mockito.any());
    }
}
