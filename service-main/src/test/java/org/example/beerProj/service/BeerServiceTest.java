package org.example.beerProj.service;

import org.assertj.core.api.Assertions;
import org.example.beerProj.dto.BeerRequest;
import org.example.beerProj.dto.BeerResponse;
import org.example.beerProj.entity.BeerEntity;
import org.example.beerProj.mapper.BeerMapper;
import org.example.beerProj.repository.BeerRepository;
import org.example.beerProj.repository.TypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {
    @Mock
    private BeerRepository beerRepository;

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private BeerMapper beerMapper;

    @InjectMocks
    private BeerService beerService;

    /*@BeforeEach
        void setUp() {
           // Ручное создание мока
            storeRepository = Mockito.mock(StoreRepository.class);

            // Ручное внедрение мока в тестируемый класс
           storeService = new StoreService(storeRepository);
        }
    */

    

    @Test
    void getAllBeers_shouldReturnPage() {
        BeerEntity beer = new BeerEntity();
        List<BeerEntity> beerEntities = List.of(beer);
        BeerResponse mappedResponse = new BeerResponse("id", "name", "producer", BigDecimal.TEN, BigDecimal.ONE, "IPA");

        Mockito.when(beerRepository.findAll(Mockito.any())).thenReturn(new PageImpl<>(beerEntities));
        Mockito.when(beerMapper.toResponse(Mockito.any())).thenReturn(mappedResponse);

        // when
        Page<BeerResponse> result = beerService.getAllBeers(0, 10);

        // then
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.getContent().get(0).getName()).isEqualTo("name");
        Mockito.verify(beerRepository).findAll(Mockito.any());
        Mockito.verify(beerMapper).toResponse(beer);
    }

    @Test
    void getAllBeers_shouldReturnEmptyList() {
        Mockito.when(beerRepository.findAll(Mockito.any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<BeerResponse> result = beerService.getAllBeers(0, 10);

        Assertions.assertThat(result).isEmpty();
        Mockito.verify(beerRepository).findAll(Mockito.any());
        Mockito.verifyNoInteractions(beerMapper);
    }

    @Test
    void getBeerById_ShouldReturnBeerResponse_whenBeerExists() {
        String id = "id";
        BeerEntity beer = new BeerEntity();
        BeerResponse expectedResponse = BeerResponse.builder()
                .id(id)
                .name("name")
                .producer("producer")
                .price(BigDecimal.TEN)
                .alcohol(BigDecimal.ONE)
                .type("IPA")
                .build();

        Mockito.when(beerRepository.findById(id)).thenReturn(Optional.of(beer));
        Mockito.when(beerMapper.toResponse(beer)).thenReturn(expectedResponse);


        BeerResponse result = beerService.getBeerById(id);

        Assertions.assertThat(result).isEqualTo(expectedResponse);

        Mockito.verify(beerRepository).findById(id);
        Mockito.verify(beerMapper).toResponse(beer);
    }

    @Test
    void getBeerById_ShouldThrowException_whenBeerDoesNotExist() {
        String wrongId = "wrongId";

        Mockito.when(beerRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> beerService.getBeerById(wrongId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");

        Mockito.verify(beerRepository).findById(wrongId);
        Mockito.verifyNoInteractions(beerMapper);
    }

    @Test
    void createNewBeer_shouldSaveEntityAndReturnResponse() {
        BeerRequest requestedData = BeerRequest.builder()
                .name("name")
                .producer("PPB")
                .price(BigDecimal.TEN)
                .alcohol(BigDecimal.ONE)
                .type("IPA")
                .build();

        BeerEntity mappedEntity = new BeerEntity();
        BeerEntity savedEntity = new BeerEntity();

        BeerResponse responseData = new BeerResponse("id", "name", "producer", BigDecimal.TEN, BigDecimal.ONE, "IPA");

        Mockito.when(beerMapper.toEntity(requestedData)).thenReturn(mappedEntity);
        Mockito.when(beerRepository.save(mappedEntity)).thenReturn(savedEntity);
        Mockito.when(beerMapper.toResponse(savedEntity)).thenReturn(responseData);

        BeerResponse result = beerService.createNewBeer(requestedData);

        Assertions.assertThat(result).isEqualTo(responseData);
        Mockito.verify(beerMapper).toEntity(requestedData);
        Mockito.verify(beerRepository).save(mappedEntity);
        Mockito.verify(beerMapper).toResponse(savedEntity);


    }

    @Test
    void createNewBeer_shouldThrow_WhenTypeNameInvalid() {
        BeerRequest request = BeerRequest.builder()
                .name("2AM")
                .producer("ZXC")
                .price(BigDecimal.TEN)
                .alcohol(BigDecimal.ZERO)
                .type("InvalidType5").build();

        Mockito.when(beerMapper.toEntity(request)).thenThrow(new IllegalArgumentException("InvalidType"));

        Assertions.assertThatThrownBy(() -> beerService.createNewBeer(request))
                .isInstanceOf(IllegalArgumentException.class);

        Mockito.verify(beerMapper).toEntity(request);
        Mockito.verifyNoInteractions(beerRepository);
    }

    @Test
    void putBeer_ShouldUpdateBeer_whenBeerExists() {
        String id = "id";
        BeerRequest request = BeerRequest.builder()
                .name("UpdatedName")
                .producer("UpdatedProducer")
                .alcohol(BigDecimal.valueOf(30))
                .price(BigDecimal.TEN)
                .type("AlcoPops")
                .build();
        BeerEntity existingEntity = new BeerEntity("id", "originalName", "originalProducer", BigDecimal.TEN, BigDecimal.ONE, 3L);
        BeerEntity savedEntity = new BeerEntity();

        BeerResponse expectedResponse = new BeerResponse("id", "UpdatedName", "UpdatedProducer", BigDecimal.valueOf(30), BigDecimal.TEN, "AlcoPops");

        Mockito.when(beerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        Mockito.when(beerRepository.save(existingEntity)).thenReturn(savedEntity);
        Mockito.when(beerMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        BeerResponse result = beerService.putBeer(id, request);

        Assertions.assertThat(result).isEqualTo(expectedResponse);

        Mockito.verify(beerRepository).findById(id);
        Mockito.verify(beerMapper).updateEntityFromDto(request, existingEntity);
        Mockito.verify(beerRepository).save(existingEntity);
        Mockito.verify(beerMapper).toResponse(savedEntity);
    }

    @Test
    void putBeer_ShouldThrowException_whenBeerDoesNotExist() {
        String wrongId = "wrongId";
        BeerRequest request = BeerRequest.builder().build();
        Mockito.when(beerRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> beerService.putBeer(wrongId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");

        Mockito.verify(beerRepository).findById(wrongId);
        Mockito.verifyNoInteractions(beerMapper);
    }

    @Test
    void patchBeer_ShouldUpdateBeer_whenBeerExists() {
        String id = "id";
        BeerRequest request = BeerRequest.builder()
                .producer("UpdatedProducer")
                .alcohol(BigDecimal.valueOf(30))
                .price(BigDecimal.TEN)
                .type("AlcoPops")
                .build();
        BeerEntity existingEntity = new BeerEntity("id", "originalName", "originalProducer", BigDecimal.ONE, BigDecimal.ZERO, 4L);
        BeerEntity savedEntity = new BeerEntity();

        BeerResponse expectedResponse = new BeerResponse("id", "originalName", "UpdatedProducer", BigDecimal.valueOf(30), BigDecimal.TEN, "AlcoPops");

        Mockito.when(beerRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        Mockito.when(beerRepository.save(existingEntity)).thenReturn(savedEntity);
        Mockito.when(beerMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        BeerResponse result = beerService.patchBeer(id, request);

        Assertions.assertThat(result).isEqualTo(expectedResponse);

        Mockito.verify(beerRepository).findById(id);
        Mockito.verify(beerMapper).patchEntityFromDto(request, existingEntity);
        Mockito.verify(beerRepository).save(existingEntity);
        Mockito.verify(beerMapper).toResponse(savedEntity);

    }

    @Test
    void patchBeer_ShouldThrowException_whenBeerDoesNotExist() {
        String wrongId = "wrongId";
        BeerRequest request = BeerRequest.builder().build();
        Mockito.when(beerRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> beerService.patchBeer(wrongId, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    @Test
    void deleteBeer_ShouldDeleteEntity_whenBeerExists() {
        String id = "id";
        BeerEntity entity = new BeerEntity();
        Mockito.when(beerRepository.findById(id)).thenReturn(Optional.of(entity));

        beerService.deleteBeer(id);

        Mockito.verify(beerRepository).findById(id);
        Mockito.verify(beerRepository).delete(entity);

    }

    @Test
    void deleteBeer_ShouldThrowException_whenBeerDoesNotExist() {
        String wrongId = "wrongId";
        Mockito.when(beerRepository.findById(wrongId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> beerService.deleteBeer(wrongId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");

        Mockito.verify(beerRepository).findById(wrongId);
        Mockito.verify(beerRepository, Mockito.never()).delete(Mockito.any());
    }


}
