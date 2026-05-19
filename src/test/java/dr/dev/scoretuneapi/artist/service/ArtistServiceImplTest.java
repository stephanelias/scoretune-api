package dr.dev.scoretuneapi.artist.service;

import dr.dev.scoretuneapi.artist.model.Artist;
import dr.dev.scoretuneapi.artist.model.ArtistType;
import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.artist.persistence.ArtistDao;
import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.core.exception.ArtistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {

    @Mock
    private ArtistDao artistDao;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private UUID testId;
    private Artist testArtist;
    private ArtistDto testArtistDto;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testArtist = new Artist.Builder()
                .withId(testId)
                .withName("The Weeknd")
                .withType(ArtistType.ARTIST)
                .withPhotoLink("https://example.com/photo.jpg")
                .build();
        testArtistDto = new ArtistDto(
                testId,
                "The Weeknd",
                ArtistType.ARTIST,
                "https://example.com/photo.jpg"
        );
    }

    @Nested
    class SearchArtistsTests {
        @Test
        void givenNoArtists_whenSearchArtists_thenReturnEmptyPage() {
            when(artistDao.findAll(any(Pageable.class))).thenReturn(Page.empty());

            PageResponse<ArtistDto> result = artistService.searchArtists(0, 10, null);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
            verify(artistDao).findAll(any(Pageable.class));
        }

        @Test
        void givenArtistsExist_whenSearchArtists_thenReturnPagedArtists() {
            Artist artist1 = new Artist.Builder()
                    .withId(UUID.randomUUID())
                    .withName("Artist 1")
                    .withType(ArtistType.ARTIST)
                    .build();
            Artist artist2 = new Artist.Builder()
                    .withId(UUID.randomUUID())
                    .withName("Artist 2")
                    .withType(ArtistType.PRODUCER)
                    .build();

            when(artistDao.findAll(any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(artist1, artist2)));

            PageResponse<ArtistDto> result = artistService.searchArtists(0, 10, null);

            assertThat(result.content()).hasSize(2);
            assertThat(result.content().get(0).name()).isEqualTo("Artist 1");
            assertThat(result.content().get(1).name()).isEqualTo("Artist 2");
            assertThat(result.totalElements()).isEqualTo(2);
            verify(artistDao).findAll(any(Pageable.class));
        }

        @Test
        void givenSearchQuery_whenSearchArtists_thenSearchByName() {
            when(artistDao.findByNameContainingIgnoreCase(eq("daft"), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(testArtist)));

            PageResponse<ArtistDto> result = artistService.searchArtists(0, 10, "daft");

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("The Weeknd");
            verify(artistDao).findByNameContainingIgnoreCase(eq("daft"), any(Pageable.class));
            verify(artistDao, never()).findAll(any(Pageable.class));
        }

        @Test
        void givenBlankSearch_whenSearchArtists_thenFindAll() {
            when(artistDao.findAll(any(Pageable.class))).thenReturn(Page.empty());

            artistService.searchArtists(0, 10, "   ");

            verify(artistDao).findAll(any(Pageable.class));
            verify(artistDao, never()).findByNameContainingIgnoreCase(any(), any(Pageable.class));
        }

        @Test
        void givenInvalidSize_whenSearchArtists_thenClampSize() {
            when(artistDao.findAll(any(Pageable.class))).thenReturn(Page.empty());

            artistService.searchArtists(0, 0, null);

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(artistDao).findAll(pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(1);
        }
    }

    @Nested
    class GetArtistByIdTests {
        @Test
        void givenArtistExists_whenGetArtistById_thenReturnArtistDto() {
            when(artistDao.findById(testId)).thenReturn(Optional.of(testArtist));

            ArtistDto result = artistService.getArtistById(testId);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(testId);
            assertThat(result.name()).isEqualTo("The Weeknd");
            assertThat(result.type()).isEqualTo(ArtistType.ARTIST);
            assertThat(result.photoLink()).isEqualTo("https://example.com/photo.jpg");
            verify(artistDao).findById(testId);
        }

        @Test
        void givenArtistDoesNotExist_whenGetArtistById_thenThrowRuntimeException() {
            UUID nonExistentId = UUID.randomUUID();
            when(artistDao.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> artistService.getArtistById(nonExistentId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Artist not found with id: " + nonExistentId);

            verify(artistDao).findById(nonExistentId);
        }
    }

    @Nested
    class CreateArtistTests {
        @Test
        void givenValidArtistDto_whenCreateArtist_thenReturnCreatedArtistDto() {
            ArtistDto inputDto = new ArtistDto(
                    null,
                    "Metro Boomin",
                    ArtistType.PRODUCER,
                    "https://example.com/metro.jpg"
            );

            Artist savedArtist = new Artist.Builder()
                    .withId(testId)
                    .withName("Metro Boomin")
                    .withType(ArtistType.PRODUCER)
                    .withPhotoLink("https://example.com/metro.jpg")
                    .build();

            when(artistDao.existsByNameIgnoreCase("Metro Boomin")).thenReturn(false);
            when(artistDao.save(any(Artist.class))).thenReturn(savedArtist);

            ArtistDto result = artistService.createArtist(inputDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(testId);
            assertThat(result.name()).isEqualTo("Metro Boomin");
            assertThat(result.type()).isEqualTo(ArtistType.PRODUCER);
            assertThat(result.photoLink()).isEqualTo("https://example.com/metro.jpg");
            verify(artistDao).save(any(Artist.class));
        }

        @Test
        void givenArtistDtoWithoutPhotoLink_whenCreateArtist_thenReturnCreatedArtistDto() {
            ArtistDto inputDto = new ArtistDto(
                    null,
                    "Daft Punk",
                    ArtistType.GROUP,
                    null
            );

            Artist savedArtist = new Artist.Builder()
                    .withId(testId)
                    .withName("Daft Punk")
                    .withType(ArtistType.GROUP)
                    .withPhotoLink(null)
                    .build();

            when(artistDao.existsByNameIgnoreCase("Daft Punk")).thenReturn(false);
            when(artistDao.save(any(Artist.class))).thenReturn(savedArtist);

            ArtistDto result = artistService.createArtist(inputDto);

            assertThat(result).isNotNull();
            assertThat(result.photoLink()).isNull();
            verify(artistDao).save(any(Artist.class));
        }

        @Test
        void givenExistingName_whenCreateArtist_thenThrowNameAlreadyExists() {
            ArtistDto inputDto = new ArtistDto(null, "The Weeknd", ArtistType.ARTIST, null);
            when(artistDao.existsByNameIgnoreCase("The Weeknd")).thenReturn(true);

            assertThatThrownBy(() -> artistService.createArtist(inputDto))
                    .isInstanceOf(ArtistException.class)
                    .satisfies(ex -> assertThat(((ArtistException) ex).code)
                            .isEqualTo(ArtistException.Code.NAME_ALREADY_EXISTS));

            verify(artistDao, never()).save(any(Artist.class));
        }
    }

    @Nested
    class UpdateArtistTests {
        @Test
        void givenArtistExists_whenUpdateArtist_thenReturnUpdatedArtistDto() {
            ArtistDto updateDto = new ArtistDto(
                    testId,
                    "Updated Name",
                    ArtistType.LABEL,
                    "https://example.com/updated.jpg"
            );

            Artist existingArtist = new Artist.Builder()
                    .withId(testId)
                    .withName("Old Name")
                    .withType(ArtistType.ARTIST)
                    .build();

            Artist updatedArtist = new Artist.Builder()
                    .withId(testId)
                    .withName("Updated Name")
                    .withType(ArtistType.LABEL)
                    .withPhotoLink("https://example.com/updated.jpg")
                    .build();

            when(artistDao.findById(testId)).thenReturn(Optional.of(existingArtist));
            when(artistDao.existsByNameIgnoreCaseAndIdNot("Updated Name", testId)).thenReturn(false);
            when(artistDao.save(any(Artist.class))).thenReturn(updatedArtist);

            ArtistDto result = artistService.updateArtist(testId, updateDto);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Updated Name");
            assertThat(result.type()).isEqualTo(ArtistType.LABEL);
            assertThat(result.photoLink()).isEqualTo("https://example.com/updated.jpg");
            verify(artistDao).findById(testId);
            verify(artistDao).save(any(Artist.class));
        }

        @Test
        void givenArtistExists_whenUpdateArtistWithNullPhotoLink_thenReturnUpdatedArtistDto() {
            ArtistDto updateDto = new ArtistDto(
                    testId,
                    "Updated Name",
                    ArtistType.ARTIST,
                    null
            );

            Artist existingArtist = new Artist.Builder()
                    .withId(testId)
                    .withName("Old Name")
                    .withType(ArtistType.ARTIST)
                    .withPhotoLink("https://example.com/old.jpg")
                    .build();

            Artist updatedArtist = new Artist.Builder()
                    .withId(testId)
                    .withName("Updated Name")
                    .withType(ArtistType.ARTIST)
                    .withPhotoLink(null)
                    .build();

            when(artistDao.findById(testId)).thenReturn(Optional.of(existingArtist));
            when(artistDao.existsByNameIgnoreCaseAndIdNot("Updated Name", testId)).thenReturn(false);
            when(artistDao.save(any(Artist.class))).thenReturn(updatedArtist);

            ArtistDto result = artistService.updateArtist(testId, updateDto);

            assertThat(result).isNotNull();
            assertThat(result.photoLink()).isNull();
            verify(artistDao).findById(testId);
            verify(artistDao).save(any(Artist.class));
        }

        @Test
        void givenExistingNameOnAnotherArtist_whenUpdateArtist_thenThrowNameAlreadyExists() {
            UUID otherId = UUID.randomUUID();
            ArtistDto updateDto = new ArtistDto(otherId, "The Weeknd", ArtistType.ARTIST, null);

            when(artistDao.findById(otherId)).thenReturn(Optional.of(testArtist));
            when(artistDao.existsByNameIgnoreCaseAndIdNot("The Weeknd", otherId)).thenReturn(true);

            assertThatThrownBy(() -> artistService.updateArtist(otherId, updateDto))
                    .isInstanceOf(ArtistException.class)
                    .satisfies(ex -> assertThat(((ArtistException) ex).code)
                            .isEqualTo(ArtistException.Code.NAME_ALREADY_EXISTS));

            verify(artistDao, never()).save(any(Artist.class));
        }

        @Test
        void givenArtistDoesNotExist_whenUpdateArtist_thenThrowRuntimeException() {
            UUID nonExistentId = UUID.randomUUID();
            ArtistDto updateDto = new ArtistDto(
                    nonExistentId,
                    "Updated Name",
                    ArtistType.ARTIST,
                    null
            );

            when(artistDao.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> artistService.updateArtist(nonExistentId, updateDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Artist not found with id: " + nonExistentId);

            verify(artistDao).findById(nonExistentId);
            verify(artistDao, never()).save(any(Artist.class));
        }
    }

    @Nested
    class DeleteArtistTests {
        @Test
        void givenArtistExists_whenDeleteArtist_thenDeleteSuccessfully() {
            when(artistDao.findById(testId)).thenReturn(Optional.of(testArtist));
            doNothing().when(artistDao).deleteById(testId);

            artistService.deleteArtist(testId);

            verify(artistDao).findById(testId);
            verify(artistDao).deleteById(testId);
        }

        @Test
        void givenArtistDoesNotExist_whenDeleteArtist_thenThrowRuntimeException() {
            UUID nonExistentId = UUID.randomUUID();
            when(artistDao.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> artistService.deleteArtist(nonExistentId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Artist not found with id: " + nonExistentId);

            verify(artistDao).findById(nonExistentId);
            verify(artistDao, never()).deleteById(any(UUID.class));
        }
    }

    @Nested
    class ToDtoMappingTests {
        @Test
        void givenArtistWithAllFields_whenToDto_thenMapAllFieldsCorrectly() {
            Artist artist = new Artist.Builder()
                    .withId(testId)
                    .withName("Test Artist")
                    .withType(ArtistType.ARTIST)
                    .withPhotoLink("https://example.com/photo.jpg")
                    .build();

            when(artistDao.findById(testId)).thenReturn(Optional.of(artist));

            ArtistDto result = artistService.getArtistById(testId);

            assertThat(result.id()).isEqualTo(testId);
            assertThat(result.name()).isEqualTo("Test Artist");
            assertThat(result.type()).isEqualTo(ArtistType.ARTIST);
            assertThat(result.photoLink()).isEqualTo("https://example.com/photo.jpg");
        }

        @Test
        void givenArtistWithNullPhotoLink_whenToDto_thenMapPhotoLinkAsNull() {
            Artist artist = new Artist.Builder()
                    .withId(testId)
                    .withName("Test Artist")
                    .withType(ArtistType.PRODUCER)
                    .withPhotoLink(null)
                    .build();

            when(artistDao.findById(testId)).thenReturn(Optional.of(artist));

            ArtistDto result = artistService.getArtistById(testId);

            assertThat(result.photoLink()).isNull();
        }
    }
}
