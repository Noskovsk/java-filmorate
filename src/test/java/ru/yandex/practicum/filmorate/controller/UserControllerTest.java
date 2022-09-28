package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void shouldPostNewUser() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"doloreullamco\",\"name\": \"ivan sidorov\",\"email\": \"mail@mail.ru\",\"birthday\": \"1989-08-20\"}")
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ivan sidorov"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldPostNewUserWithEmptyName() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"newdoloreullamco\",\"name\": \"\",\"email\": \"newmail@mail.ru\",\"birthday\": \"1989-08-20\"}")
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("newdoloreullamco"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldNotPostUserWithSpaceInLogin() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"dolore ullamco\",\"email\": \"mail@mail.ru\",\"birthday\": \"1989-08-20\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostUserWithEmptyLogin() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"\",\"email\": \"mail@mail.ru\",\"birthday\": \"1989-08-20\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostUserWithLoginOnlySpace() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"     \",\"email\": \"mail@mail.ru\",\"birthday\": \"1989-08-20\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostUserWithNoLoginField() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"email\": \"mail@mail.ru\",\"birthday\": \"1989-08-20\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostUserWithInvalidEmail() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"doloreullamco\",\"email\": \"mailmail.ru\",\"birthday\": \"1989-08-20\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostUserBirthdayToday() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"doloreullamco\",\"email\": \"mail@mail.ru\",\"birthday\": \" " + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostUserBirthdayInFuture() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users")
                                .content("{\"login\": \"doloreullamco\",\"email\": \"mail@mail.ru\",\"birthday\": \" " + LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE) + "\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }


    @Test
    void shouldNotPostUserWithEmptyRequestBody() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/users").content("").contentType(MediaType.ALL_VALUE));
        result.andExpect(status().is4xxClientError());
    }
}