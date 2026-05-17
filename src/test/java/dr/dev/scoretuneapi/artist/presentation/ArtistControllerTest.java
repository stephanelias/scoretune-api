package dr.dev.scoretuneapi.artist.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import dr.dev.scoretuneapi.artist.model.ArtistType;
import dr.dev.scoretuneapi.artist.model.dto.ArtistDto;
import dr.dev.scoretuneapi.artist.service.ArtistService;
import dr.dev.scoretuneapi.core.dto.PageResponse;
import dr.dev.scoretuneapi.core.exception.ArtistException;
import dr.dev.scoretuneapi.core.utils.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArtistService artistService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID testId;
    private ArtistDto testArtistDto;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
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
        void givenNoArtists_whenSearchArtists_thenReturnEmptyPage() throws Exception {
            PageResponse<ArtistDto> emptyPage = new PageResponse<>(List.of(), 0, 10, 0, 0);
            when(artistService.searchArtists(0, 10, null)).thenReturn(emptyPage);

            mockMvc.perform(get("/api/artists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0));

            verify(artistService).searchArtists(0, 10, null);
        }

        @Test
        void givenArtistsExist_whenSearchArtists_thenReturnPagedArtists() throws Exception {
            ArtistDto artist1 = new ArtistDto(UUID.randomUUID(), "Artist 1", ArtistType.ARTIST, null);
            ArtistDto artist2 = new ArtistDto(UUID.randomUUID(), "Artist 2", ArtistType.PRODUCER, "photo.jpg");
            PageResponse<ArtistDto> page = new PageResponse<>(List.of(artist1, artist2), 0, 10, 2, 1);

            when(artistService.searchArtists(0, 10, null)).thenReturn(page);

            mockMvc.perform(get("/api/artists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].name").value("Artist 1"))
                    .andExpect(jsonPath("$.content[1].name").value("Artist 2"))
                    .andExpect(jsonPath("$.totalElements").value(2));

            verify(artistService).searchArtists(0, 10, null);
        }

        @Test
        void givenSearchQuery_whenSearchArtists_thenPassSearchParam() throws Exception {
            PageResponse<ArtistDto> page = new PageResponse<>(List.of(testArtistDto), 0, 10, 1, 1);
            when(artistService.searchArtists(0, 10, "week")).thenReturn(page);

            mockMvc.perform(get("/api/artists").param("search", "week"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].name").value("The Weeknd"));

            verify(artistService).searchArtists(0, 10, "week");
        }

        @Test
        void givenUnauthenticatedUser_whenSearchArtists_thenReturnOk() throws Exception {
            when(artistService.searchArtists(0, 10, null))
                    .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));

            mockMvc.perform(get("/api/artists"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetArtistByIdTests {
        @Test
        void givenArtistExists_whenGetArtistById_thenReturnArtistDto() throws Exception {
            when(artistService.getArtistById(testId)).thenReturn(testArtistDto);

            mockMvc.perform(get("/api/artists/{id}", testId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testId.toString()))
                    .andExpect(jsonPath("$.name").value("The Weeknd"))
                    .andExpect(jsonPath("$.type").value("ARTIST"))
                    .andExpect(jsonPath("$.photoLink").value("https://example.com/photo.jpg"));

            verify(artistService).getArtistById(testId);
        }

        @Test
        void givenArtistDoesNotExist_whenGetArtistById_thenReturnNotFound() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            when(artistService.getArtistById(nonExistentId))
                    .thenThrow(new ArtistException(ArtistException.Code.NOT_FOUND, null, "Artist not found with id: " + nonExistentId));

            mockMvc.perform(get("/api/artists/{id}", nonExistentId))
                    .andExpect(status().isNotFound());

            verify(artistService).getArtistById(nonExistentId);
        }

        @Test
        void givenUnauthenticatedUser_whenGetArtistById_thenReturnOk() throws Exception {
            when(artistService.getArtistById(testId)).thenReturn(testArtistDto);

            mockMvc.perform(get("/api/artists/{id}", testId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class CreateArtistTests {
        @Test
        @WithMockUser(roles = "MODO")
        void givenModoUser_whenCreateArtist_thenReturnCreated() throws Exception {
            ArtistDto inputDto = new ArtistDto(null, "New Artist", ArtistType.ARTIST, "photo.jpg");
            ArtistDto createdDto = new ArtistDto(testId, "New Artist", ArtistType.ARTIST, "photo.jpg");

            when(artistService.createArtist(any(ArtistDto.class))).thenReturn(createdDto);

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(testId.toString()))
                    .andExpect(jsonPath("$.name").value("New Artist"))
                    .andExpect(jsonPath("$.type").value("ARTIST"));

            verify(artistService).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void givenAdminUser_whenCreateArtist_thenReturnCreated() throws Exception {
            ArtistDto inputDto = new ArtistDto(null, "New Artist", ArtistType.PRODUCER, null);
            ArtistDto createdDto = new ArtistDto(testId, "New Artist", ArtistType.PRODUCER, null);

            when(artistService.createArtist(any(ArtistDto.class))).thenReturn(createdDto);

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isCreated());

            verify(artistService).createArtist(any(ArtistDto.class));
        }

        @Test
        void givenUnauthenticatedUser_whenCreateArtist_thenReturnForbidden() throws Exception {
            ArtistDto inputDto = new ArtistDto(null, "New Artist", ArtistType.ARTIST, null);

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isForbidden());

            verify(artistService, never()).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "USER")
        void givenRegularUser_whenCreateArtist_thenReturnForbidden() throws Exception {
            ArtistDto inputDto = new ArtistDto(null, "New Artist", ArtistType.ARTIST, null);

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isForbidden());

            verify(artistService, never()).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenArtistDtoWithNullPhotoLink_whenCreateArtist_thenReturnCreated() throws Exception {
            ArtistDto inputDto = new ArtistDto(null, "Artist Without Photo", ArtistType.GROUP, null);
            ArtistDto createdDto = new ArtistDto(testId, "Artist Without Photo", ArtistType.GROUP, null);

            when(artistService.createArtist(any(ArtistDto.class))).thenReturn(createdDto);

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.photoLink").isEmpty());

            verify(artistService).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenNullName_whenCreateArtist_thenReturnBadRequest() throws Exception {
            String jsonBody = "{\"name\": null, \"type\": \"ARTIST\", \"photoLink\": null}";

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").value("Name is required"));

            verify(artistService, never()).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenEmptyName_whenCreateArtist_thenReturnBadRequest() throws Exception {
            String jsonBody = "{\"name\": \"\", \"type\": \"ARTIST\", \"photoLink\": null}";

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").value("Name is required"));

            verify(artistService, never()).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenBlankName_whenCreateArtist_thenReturnBadRequest() throws Exception {
            String jsonBody = "{\"name\": \"   \", \"type\": \"ARTIST\", \"photoLink\": null}";

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").value("Name is required"));

            verify(artistService, never()).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenNullType_whenCreateArtist_thenReturnBadRequest() throws Exception {
            String jsonBody = "{\"name\": \"Artist Name\", \"type\": null, \"photoLink\": null}";

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type").value("Type is required"));

            verify(artistService, never()).createArtist(any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenInvalidJson_whenCreateArtist_thenReturnBadRequest() throws Exception {
            String invalidJson = "{\"name\": \"Artist\", \"type\": ";

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(artistService, never()).createArtist(any(ArtistDto.class));
        }
    }

    @Nested
    class UpdateArtistTests {
        @Test
        @WithMockUser(roles = "MODO")
        void givenModoUser_whenUpdateArtist_thenReturnOk() throws Exception {
            ArtistDto updateDto = new ArtistDto(testId, "Updated Name", ArtistType.LABEL, "updated.jpg");
            ArtistDto updatedDto = new ArtistDto(testId, "Updated Name", ArtistType.LABEL, "updated.jpg");

            when(artistService.updateArtist(eq(testId), any(ArtistDto.class))).thenReturn(updatedDto);

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.type").value("LABEL"));

            verify(artistService).updateArtist(eq(testId), any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void givenAdminUser_whenUpdateArtist_thenReturnOk() throws Exception {
            ArtistDto updateDto = new ArtistDto(testId, "Updated Name", ArtistType.ARTIST, null);
            ArtistDto updatedDto = new ArtistDto(testId, "Updated Name", ArtistType.ARTIST, null);

            when(artistService.updateArtist(eq(testId), any(ArtistDto.class))).thenReturn(updatedDto);

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk());

            verify(artistService).updateArtist(eq(testId), any(ArtistDto.class));
        }

        @Test
        void givenUnauthenticatedUser_whenUpdateArtist_thenReturnForbidden() throws Exception {
            ArtistDto updateDto = new ArtistDto(testId, "Updated Name", ArtistType.ARTIST, null);

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isForbidden());

            verify(artistService, never()).updateArtist(any(UUID.class), any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "USER")
        void givenRegularUser_whenUpdateArtist_thenReturnForbidden() throws Exception {
            ArtistDto updateDto = new ArtistDto(testId, "Updated Name", ArtistType.ARTIST, null);

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isForbidden());

            verify(artistService, never()).updateArtist(any(UUID.class), any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenArtistDoesNotExist_whenUpdateArtist_thenReturnNotFound() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            ArtistDto updateDto = new ArtistDto(nonExistentId, "Updated Name", ArtistType.ARTIST, null);

            when(artistService.updateArtist(eq(nonExistentId), any(ArtistDto.class)))
                    .thenThrow(new ArtistException(ArtistException.Code.NOT_FOUND, null, "Artist not found with id: " + nonExistentId));

            mockMvc.perform(put("/api/artists/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound());

            verify(artistService).updateArtist(eq(nonExistentId), any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenNullName_whenUpdateArtist_thenReturnBadRequest() throws Exception {
            String jsonBody = "{\"name\": null, \"type\": \"ARTIST\", \"photoLink\": null}";

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").value("Name is required"));

            verify(artistService, never()).updateArtist(any(UUID.class), any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenEmptyName_whenUpdateArtist_thenReturnBadRequest() throws Exception {
            String jsonBody = "{\"name\": \"\", \"type\": \"ARTIST\", \"photoLink\": null}";

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.name").value("Name is required"));

            verify(artistService, never()).updateArtist(any(UUID.class), any(ArtistDto.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenNullType_whenUpdateArtist_thenReturnBadRequest() throws Exception {
            String jsonBody = "{\"name\": \"Artist Name\", \"type\": null, \"photoLink\": null}";

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type").value("Type is required"));

            verify(artistService, never()).updateArtist(any(UUID.class), any(ArtistDto.class));
        }
    }

    @Nested
    class DeleteArtistTests {
        @Test
        @WithMockUser(roles = "ADMIN")
        void givenAdminUser_whenDeleteArtist_thenReturnNoContent() throws Exception {
            doNothing().when(artistService).deleteArtist(testId);

            mockMvc.perform(delete("/api/artists/{id}", testId))
                    .andExpect(status().isNoContent());

            verify(artistService).deleteArtist(testId);
        }

        @Test
        void givenUnauthenticatedUser_whenDeleteArtist_thenReturnForbidden() throws Exception {
            mockMvc.perform(delete("/api/artists/{id}", testId))
                    .andExpect(status().isForbidden());

            verify(artistService, never()).deleteArtist(any(UUID.class));
        }

        @Test
        @WithMockUser(roles = "USER")
        void givenRegularUser_whenDeleteArtist_thenReturnForbidden() throws Exception {
            mockMvc.perform(delete("/api/artists/{id}", testId))
                    .andExpect(status().isForbidden());

            verify(artistService, never()).deleteArtist(any(UUID.class));
        }

        @Test
        @WithMockUser(roles = "MODO")
        void givenModoUser_whenDeleteArtist_thenReturnForbidden() throws Exception {
            mockMvc.perform(delete("/api/artists/{id}", testId))
                    .andExpect(status().isForbidden());

            verify(artistService, never()).deleteArtist(any(UUID.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void givenArtistDoesNotExist_whenDeleteArtist_thenReturnNotFound() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ArtistException(ArtistException.Code.NOT_FOUND, null, "Artist not found with id: " + nonExistentId))
                    .when(artistService).deleteArtist(nonExistentId);

            mockMvc.perform(delete("/api/artists/{id}", nonExistentId))
                    .andExpect(status().isNotFound());

            verify(artistService).deleteArtist(nonExistentId);
        }
    }

    @Nested
    class SecurityTests {
        @Test
        void givenUnauthenticatedUser_whenSearchArtists_thenReturnOk() throws Exception {
            when(artistService.searchArtists(0, 10, null))
                    .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0));

            mockMvc.perform(get("/api/artists"))
                    .andExpect(status().isOk());
        }

        @Test
        void givenUnauthenticatedUser_whenGetArtistById_thenReturnOk() throws Exception {
            when(artistService.getArtistById(testId)).thenReturn(testArtistDto);

            mockMvc.perform(get("/api/artists/{id}", testId))
                    .andExpect(status().isOk());
        }

        @Test
        void givenUnauthenticatedUser_whenCreateArtist_thenReturnForbidden() throws Exception {
            ArtistDto inputDto = new ArtistDto(null, "New Artist", ArtistType.ARTIST, null);

            mockMvc.perform(post("/api/artists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void givenUnauthenticatedUser_whenUpdateArtist_thenReturnForbidden() throws Exception {
            ArtistDto updateDto = new ArtistDto(testId, "Updated Name", ArtistType.ARTIST, null);

            mockMvc.perform(put("/api/artists/{id}", testId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void givenUnauthenticatedUser_whenDeleteArtist_thenReturnForbidden() throws Exception {
            mockMvc.perform(delete("/api/artists/{id}", testId))
                    .andExpect(status().isForbidden());
        }
    }
}
