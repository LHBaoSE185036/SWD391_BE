package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.model.Account;
import com.swd.gym_face_id_access.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        // Kiểm tra email trong database
        Account account = accountRepository.findByEmail(email);
        if (account != null) {

            // Tạo JWT Token
            String token = jwtUtil.generateToken(account.getUserName(), account.getRole());

            // Trả token về JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + token + "\"}");
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Email not registered");
        }
    }
}
