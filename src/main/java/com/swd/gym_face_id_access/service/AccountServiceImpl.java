package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.dto.request.CreateAccountRequest;
import com.swd.gym_face_id_access.model.Account;
import com.swd.gym_face_id_access.model.Enum.Roles;
import com.swd.gym_face_id_access.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;


    private final JwtUtil jwtUtil;

    @Override
    public String Login(String userName, String password) {
        Account accountOptional = accountRepository.findByUserName(userName);

        if (accountOptional != null) {
            if (passwordEncoder.matches(password, accountOptional.getPassword())) {
                return jwtUtil.generateToken(accountOptional.getUserName(), accountOptional.getRole());
            }
        }

        return null;
    }

    @Override
    public String Register(CreateAccountRequest createAccountRequest) {

        String password = passwordEncoder.encode(createAccountRequest.getPassword());

        Account account = new Account();
        account.setUserName(createAccountRequest.getUsername());
        account.setPassword(password);
        account.setRole(Roles.valueOf(createAccountRequest.getRole()));
        accountRepository.save(account);

        return "Created Successfully";
    }

}
