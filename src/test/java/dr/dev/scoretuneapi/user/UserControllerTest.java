package dr.dev.scoretuneapi.user;

import dr.dev.scoretuneapi.core.utils.WithMockCustomUser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc ;

    @Nested
    class SecurityTests {
        @Test
        void givenUserOrModo_whenGetAllUsers_thenReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void givenAdmin_whenGetAllUsers_thenReturnOk() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockCustomUser
        void givenAuthenticatedUser_whenGetOwnProfile_thenReturnOk() throws Exception {
            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isOk());
        }

        @Test
        void givenUnauthenticatedUser_whenGetOwnProfile_thenReturnForbidden() throws Exception {
            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    class ResponseTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void givenAdmin_whenGetAllUsers_thenReturnListOfUsers() throws Exception {
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].fullName").value("Useradmin"))
                    .andExpect(jsonPath("$[0].email").value("admin@test.com"))
                    .andExpect(jsonPath("$[0].roles[0]").value("ROLE_ADMIN"))
                    .andExpect(jsonPath("$[0].createdAt").exists())
                    .andExpect(jsonPath("$[1].fullName").value("Modo User"))
                    .andExpect(jsonPath("$[1].email").value("modo@test.com"))
                    .andExpect(jsonPath("$[1].roles[0]").value("ROLE_MODO"))
                    .andExpect(jsonPath("$[0].createdAt").exists());

        }

        @Test
        @WithMockCustomUser(username = "admin@test.com", fullName = "Useradmin", roles = {"ROLE_ADMIN"})
        void givenAuthenticatedUser_whenGetOwnProfile_thenReturnUserProfile() throws Exception {
            mockMvc.perform(get("/api/users/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fullName").value("Useradmin"))
                    .andExpect(jsonPath("$.email").value("admin@test.com"))
                    .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"))
                    .andExpect(jsonPath("$.roles").isArray())
                    .andExpect(jsonPath("$.roles.length()").value(1));
        }



    }
}
