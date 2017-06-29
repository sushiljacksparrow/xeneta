
package app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void noParamRateShouldReturnDefaultMessage() throws Exception {

        this.mockMvc.perform(get("/rates?date_from=2016-01-01&date_to=2016-01-10&origin=CNSGH&destination=NLRTM")).andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello, World!"));
    }

    @Test
    public void paramRateShouldReturnTailoredMessage() throws Exception {

        this.mockMvc.perform(get("/rates?date_from=2016-01-01&date_to=2016-01-10&origin=CNSGH&destination=NLRTM").param("name", "Spring Community"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello, Spring Community!"));
    }

}
