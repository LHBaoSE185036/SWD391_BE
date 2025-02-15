package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.model.Account;
import com.swd.gym_face_id_access.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final JwtUtil jwtUtil;

    @Override
    public String Login(String userName, String password) {
        Account accountOptional = accountRepository.findByUserName(userName);

        if (accountOptional != null) {
            if (accountOptional.getPassword().equals(password)) { // Kiểm tra password gốc
                return jwtUtil.generateToken(userName); // Trả về JWT token nếu đúng mật khẩu
            }
        }

        return null;
    }
}
