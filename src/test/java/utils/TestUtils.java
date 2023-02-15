package utils;

import app.foot.controller.rest.Player;
import app.foot.controller.rest.PlayerScorer;
import app.foot.exception.ApiException;
import app.foot.model.Team;
import app.foot.repository.entity.PlayerEntity;
import app.foot.repository.entity.PlayerScoreEntity;
import app.foot.repository.entity.TeamEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.function.Executable;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    public static PlayerScorer scorer1() {
        return PlayerScorer.builder()
                .player(player1())
                .isOG(false)
                .scoreTime(10)
                .build();
    }

    public static PlayerScorer scorer4() {
        return PlayerScorer.builder()
                .player(player4())
                .isOG(false)
                .scoreTime(60)
                .build();
    }

    public static PlayerScorer nullScoreTimeScorer() {
        return scorer1().toBuilder()
                .scoreTime(null)
                .build();
    }

    public static Player player1() {
        return Player.builder()
                .id(1)
                .name("J1")
                .teamName("E1")
                .isGuardian(false)
                .build();
    }
    public static Player player4() {
        return Player.builder()
                .id(4)
                .name("Rabe")
                .isGuardian(false)
                .teamName("E3")
                .build();
    }

    public static app.foot.model.PlayerScorer rakotoModelScorer(app.foot.model.Player playerModelRakoto, PlayerScoreEntity scorerRakoto) {
        return app.foot.model.PlayerScorer.builder()
                .player(playerModelRakoto)
                .isOwnGoal(false)
                .minute(scorerRakoto.getMinute())
                .build();
    }

    public static Team teamModelGhana(TeamEntity teamEntityGhana) {
        return Team.builder()
                .id(teamEntityGhana.getId())
                .name(teamEntityGhana.getName())
                .build();
    }

    public static Team teamModelBarea(TeamEntity teamEntityBarea) {
        return Team.builder()
                .id(teamEntityBarea.getId())
                .name(teamEntityBarea.getName())
                .build();
    }

    public static PlayerScoreEntity scorerRakoto(PlayerEntity playerEntityRakoto) {
        return PlayerScoreEntity.builder()
                .id(1)
                .player(playerEntityRakoto)
                .minute(10)
                .build();
    }

    public static app.foot.model.Player playerModelRakoto(PlayerEntity playerEntityRakoto) {
        return app.foot.model.Player.builder()
                .id(playerEntityRakoto.getId())
                .name(playerEntityRakoto.getName())
                .isGuardian(false)
                .teamName(teamBarea().getName())
                .build();
    }

    public static PlayerEntity playerEntityRakoto(TeamEntity teamEntityBarea) {
        return PlayerEntity.builder()
                .id(1)
                .name("Rakoto")
                .guardian(false)
                .team(teamEntityBarea)
                .build();
    }

    public static TeamEntity teamGhana() {
        return TeamEntity.builder()
                .id(2)
                .name("Ghana")
                .build();
    }

    public static TeamEntity teamBarea() {
        return TeamEntity.builder()
                .id(1)
                .name("Barea")
                .build();
    }

    public static void assertThrowsExceptionMessage(String message, Class exceptionClass, Executable executable) {
        Throwable exception = assertThrows(exceptionClass, executable);
        assertEquals(message, exception.getMessage());
    }
    public static void assertThrowsException(String expectedBody,  MockHttpServletResponse executable) throws Exception{
        MockHttpServletResponse response = executable;
        Exception exception = objectMapper.readValue(
                response.getContentAsString(), Exception.class);
        try{
            if (response.getStatus()!=200 && response.getStatus()!=201){
                throw new ApiException(response.getStatus(),exception.getMessage());
            };
        }catch(ApiException e){
            assertEquals(expectedBody, exception.getMessage());
        }
    }
}

