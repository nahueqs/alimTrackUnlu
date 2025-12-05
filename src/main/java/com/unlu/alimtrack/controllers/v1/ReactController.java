package com.unlu.alimtrack.controllers.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReactController {

    // Exclude /ws from the catch-all mapping
    @RequestMapping(value = {"/", "/{path:^(?!ws$).*[^\\.]*}"})
    public String redirect() {
        log.trace("Redirigiendo al frontend de React (index.html)");
        return "forward:/index.html";
    }
}
