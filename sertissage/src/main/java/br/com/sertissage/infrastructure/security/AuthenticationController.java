package br.com.sertissage.infrastructure.security;

import br.com.sertissage.application.dto.request.LoginRequest;
import br.com.sertissage.application.dto.response.JwtAuthenticationResponse;
import br.com.sertissage.domain.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Endpoints de autenticação e autorização")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Operation(summary = "Login", description = "Autentica usuário e retorna token JWT")
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getSenha()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        
        Usuario usuario = (Usuario) authentication.getPrincipal();

        JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
            .token(jwt)
            .tipo("Bearer")
            .usuarioId(usuario.getId())
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .empresaId(usuario.getEmpresa().getId())
            .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Validar Token", description = "Verifica se o token JWT é válido")
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            boolean isValid = tokenProvider.validateToken(jwt);
            return ResponseEntity.ok(isValid);
        }
        return ResponseEntity.ok(false);
    }
}