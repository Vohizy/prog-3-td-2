package app.foot.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Exceptions {
    private String type;
    private String message;
}
