package dr.dev.scoretuneapi.artist.service;

import dr.dev.scoretuneapi.artist.model.Artist;
import dr.dev.scoretuneapi.artist.model.ArtistType;
import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.artist.persistence.ArtistDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    class GetAllArtistsTests {
        @Test
        void givenNoArtists_whenGetAllArtists_thenReturnEmptyList() {
            when(artistDao.findAll()).thenReturn(List.of());

            List<ArtistDto> result = artistService.getAllArtists();

            assertThat(result).isEmpty();
            verify(artistDao).findAll();
        }

        @Test
        void givenArtistsExist_whenGetAllArtists_thenReturnListOfArtists() {
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

            when(artistDao.findAll()).thenReturn(List.of(artist1, artist2));

            List<ArtistDto> result = artistService.getAllArtists();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Artist 1");
            assertThat(result.get(1).name()).isEqualTo("Artist 2");
            verify(artistDao).findAll();
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

            when(artistDao.save(any(Artist.class))).thenReturn(savedArtist);

            ArtistDto result = artistService.createArtist(inputDto);

            assertThat(result).isNotNull();
            assertThat(result.photoLink()).isNull();
            verify(artistDao).save(any(Artist.class));
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
            when(artistDao.save(any(Artist.class))).thenReturn(updatedArtist);

            ArtistDto result = artistService.updateArtist(testId, updateDto);

            assertThat(result).isNotNull();
            assertThat(result.photoLink()).isNull();
            verify(artistDao).findById(testId);
            verify(artistDao).save(any(Artist.class));
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
