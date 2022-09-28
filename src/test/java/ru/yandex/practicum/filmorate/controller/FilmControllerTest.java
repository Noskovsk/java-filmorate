package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    InMemoryFilmStorage inMemoryFilmStorage;
    @Autowired
    private MockMvc mvc;

    @Test
    void shouldPostNewFilm() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/films")
                                .content("{\"name\": \"filmname\",\"description\": \"film desc\",\"releaseDate\": \"1989-08-20\",\"duration\": \"100\",\"mpa\":{\"id\":3,\"name\":null,\"description\":null}}")
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldNotPostFilmWithEmptyName() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/films")
                                .content("{\"name\": \"\",\"description\": \"film desc\",\"releaseDate\": \"1989-08-20\",\"duration\": \"100\",\"mpa\":{\"id\":3,\"name\":null,\"description\":null}}")
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostFilmWithLongDescription() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/films")
                                .content("{\"name\": \"filmname\",\"description\": \""
                                        + new String(new char[201]).replace("\0", "s")
                                        + "\",\"releaseDate\": \"1989-08-20\",\"duration\": \"100\"}")
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldPostFilmWith199LengthDescription() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/films")
                                .content("{\"name\": \"filmname\",\"description\": \""
                                        + new String(new char[199]).replace("\0", "s")
                                        + "\",\"releaseDate\": \"1989-08-20\",\"duration\": \"100\",\"mpa\":{\"id\":3,\"name\":null,\"description\":null}}")
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldNotPostFilmWithReleaseDateEarlier18951228() throws Exception {
        final NestedServletException exception = assertThrows(
                NestedServletException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        // здесь блок кода, который хотим проверить
                        // при делении на 0 ожидаем ArithmeticException
                        final ResultActions result =
                                mvc.perform(
                                        post("/films")
                                                .content("{\"name\": \"filmname\",\"description\": \"descr\",\"releaseDate\": \"1895-12-27\",\"duration\": \"100\",\"mpa\":{\"id\":3,\"name\":null,\"description\":null}}")
                                                .contentType(MediaType.APPLICATION_JSON));
                    }
                });

        // можно проверить, находится ли в exception ожидаемый текст
        assertEquals("Request processing failed; nested exception is javax.validation.ValidationException: Дата релиза должна быть больше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    void shouldPostFilmWithReleaseDateAfter18951228() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/films")
                                .content("{\"name\": \"filmname\",\"description\": \"descr\",\"releaseDate\": \"1895-12-28\",\"duration\": \"100\",\"mpa\":{\"id\":3,\"name\":null,\"description\":null}}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldNotPostFilmWithDurationBelowZero() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/films")
                                .content("{\"name\": \"filmname\",\"description\": \"descr\",\"releaseDate\": \"1895-12-28\",\"duration\": \"-1\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }

    @Test
    void shouldNotPostFilmWithZeroDuration() throws Exception {
        final ResultActions result =
                mvc.perform(
                        post("/films")
                                .content("{\"name\": \"filmname\",\"description\": \"descr\",\"releaseDate\": \"1895-12-28\",\"duration\": \"0\"}")
                                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is4xxClientError());
    }
}