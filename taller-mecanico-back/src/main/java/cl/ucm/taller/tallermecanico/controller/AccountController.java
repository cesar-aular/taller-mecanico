package cl.ucm.taller.tallermecanico.controller;

import cl.ucm.taller.tallermecanico.dto.in.LoginDtoIn;
import cl.ucm.taller.tallermecanico.dto.in.RegisterDtoIn;
import cl.ucm.taller.tallermecanico.dto.out.LoginDtoOut;
import cl.ucm.taller.tallermecanico.dto.out.RegisterDtoOut;
import cl.ucm.taller.tallermecanico.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Endpoint: POST /auth/login 
    @PostMapping("/login")
    public ResponseEntity<LoginDtoOut> login(@Valid @RequestBody LoginDtoIn loginDtoIn) {
        return ResponseEntity.ok(accountService.login(loginDtoIn));
    }

    // Endpoint: POST /auth/register
    // Crea un recurso nuevo (el usuario), por eso responde 201 Created
    @PostMapping("/register")
    public ResponseEntity<RegisterDtoOut> register(@Valid @RequestBody RegisterDtoIn registerDtoIn) {
        return new ResponseEntity<>(accountService.register(registerDtoIn), HttpStatus.CREATED);
    }
}
