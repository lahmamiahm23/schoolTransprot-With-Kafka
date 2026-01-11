package com.example.transport.securite;

import com.example.transport.dto.LoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {

        String token = authenticationService.authenticate(request);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer"
        ));
    }
}
