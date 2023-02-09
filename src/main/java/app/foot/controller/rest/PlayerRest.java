package app.foot.controller.rest;

import lombok.*;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class PlayerRest {
    private Integer id;
    private String name;
    private Boolean isGuardian;

}
