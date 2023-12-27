package co.com.svl.controlador;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Value("${app.login-title}")
    private String loginTittle;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginTittle", loginTittle);
        return "login";
    }
}
