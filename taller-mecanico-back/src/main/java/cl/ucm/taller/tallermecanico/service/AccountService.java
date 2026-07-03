package cl.ucm.taller.tallermecanico.service;

import cl.ucm.taller.tallermecanico.dto.in.LoginDtoIn;
import cl.ucm.taller.tallermecanico.dto.in.RegisterDtoIn;
import cl.ucm.taller.tallermecanico.dto.out.LoginDtoOut;
import cl.ucm.taller.tallermecanico.dto.out.RegisterDtoOut;

public interface AccountService {
    LoginDtoOut login(LoginDtoIn in);
    RegisterDtoOut register(RegisterDtoIn in);
}
