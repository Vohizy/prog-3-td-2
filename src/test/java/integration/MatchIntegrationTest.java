package integration;

import app.foot.FootApi;
import app.foot.controller.rest.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.*;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
@Slf4j
class MatchIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();  //Allow 'java.time.Instant' mapping

    @Test
    void read_match_by_id_ok() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Match actual = objectMapper.readValue(
                response.getContentAsString(), Match.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedMatch2(), actual);
    }

    @Test
    void read_matches_ok() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<Match> actual = convertFromHttpResponse(response);

        assertEquals(3, actual.size());
        assertTrue(actual.contains(expectedMatch2()));
        //TODO: add these checks and its values
        //assertTrue(actual.contains(expectedMatch1()));
        //assertTrue(actual.contains(expectedMatch3()));
    }

    @Test
    void create_match_by_id_with_goal()throws Exception{
        List<PlayerScorer> scorersCreate = List.of(playerScorer1());
        MockHttpServletResponse response = mockMvc
                .perform(post("/matches/{matchId}/goals",3)
                        .content(objectMapper.writeValueAsString(scorersCreate))
                        .contentType("application/json")
                        .accept("application/json"))
                .andReturn()
                .getResponse();
        Match actual = convertFromHttpResponseMatchNoList(response);
        assertEquals(HttpStatus.OK.value(),response.getStatus());
        assertTrue(actual.equals(Match.builder()
                .id(3)
                .teamA(teamMatchA3().toBuilder()
                        .score(1)
                        .team(team1())
                        .scorers(List.of(playerScorer1()))
                        .build())
                .teamB(teamMatchB3())
                .stadium("S3")
                .datetime(Instant.parse("2023-01-01T18:00:00Z"))

                .build()));

    }
    void add_goals_scoretime_ko() throws Exception {
        String Message = "400 BAD_REQUEST : Player#"+player6().getId()+" is a guardian so they cannot score.";
        assertThrowsException(Message,
                mockMvc.perform(post("/matches/3/goals")
                                .content(objectMapper.writeValueAsString(List.of(playerScorer1().toBuilder()
                                        .scoreTime(-1)
                                        .build() )))
                                .contentType("application/json").accept("application/json"))
                        .andReturn()
                        .getResponse());

    }

    private static Match expectedMatch2() {
        return Match.builder()
                .id(2)
                .teamA(teamMatchA())
                .teamB(teamMatchB())
                .stadium("S2")
                .datetime(Instant.parse("2023-01-01T14:00:00Z"))
                .build();
    }
    private static Match expectedMatch3() {
        return Match.builder()
                .id(3)
                .teamA(teamMatchA().toBuilder()
                        .team(team2().toBuilder().id(1).name("E1").build())
                        .score(0)
                        .scorers(List.of())
                        .build())
                .teamB(teamMatchB().toBuilder()
                        .team(team3())
                        .score(1)
                        .scorers(List.of(PlayerScorer.builder()
                                .player(player6())
                                .scoreTime(70)
                                .isOG(false)
                                .build()))
                        .build())
                .stadium("S3")
                .datetime(Instant.parse("2023-01-01T18:00:00Z"))
                .build();
    }

    private static TeamMatch teamMatchB() {
        return TeamMatch.builder()
                .team(team3())
                .score(0)
                .scorers(List.of())
                .build();
    }
    private static TeamMatch teamMatchB3(){
        return TeamMatch.builder()
                .team(team3())
                .score(0)
                .scorers(List.of())
                .build();
    }

    private static TeamMatch teamMatchA() {
        return TeamMatch.builder()
                .team(team2())
                .score(2)
                .scorers(List.of(PlayerScorer.builder()
                                .player(player3())
                                .scoreTime(70)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player6())
                                .scoreTime(80)
                                .isOG(true)
                                .build()))
                .build();
    }
    private static TeamMatch teamMatchA3(){
        return TeamMatch.builder()
                .team(team1())
                .score(0)
                .scorers(List.of())
                .build();
    }

    private static Team team3() {
        return Team.builder()
                .id(3)
                .name("E3")
                .build();
    }

    private static Player player6() {
        return Player.builder()
                .id(6)
                .name("J6")
                .teamName("E3")
                .isGuardian(false)
                .build();
    }

    private static Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .teamName("E2")
                .isGuardian(false)
                .build();
    }

    private static Team team2() {
        return Team.builder()
                .id(2)
                .name("E2")
                .build();
    }
    private static Team team1() {
        return Team.builder()
                .id(1)
                .name("E1")
                .build();
    }
    private static PlayerScorer playerScorer1(){
        return PlayerScorer.builder()
                .player(player1())
                .scoreTime(30)
                .isOG(false)
                .build();
    }


    private List<Match> convertFromHttpResponse(MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        CollectionType playerListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Match.class);
        return objectMapper.readValue(
                response.getContentAsString(),
                playerListType);
    }
    private Match convertFromHttpResponseMatchNoList(MockHttpServletResponse response)
            throws JsonProcessingException , UnsupportedEncodingException {
        return objectMapper.readValue(response.getContentAsString(),Match.class);
    }
}
