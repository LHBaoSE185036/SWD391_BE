package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.dto.request.CreateAccountRequest;
import com.swd.gym_face_id_access.exception.AccountNotValidException;
import com.swd.gym_face_id_access.exception.UnauthorizedException;
import com.swd.gym_face_id_access.model.Account;
import com.swd.gym_face_id_access.model.Enum.Roles;
import com.swd.gym_face_id_access.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final HttpServletRequest request;

    private final JwtUtil jwtUtil;

    @Override
    public String Login(String userName, String password) {
        Account accountOptional = accountRepository.findByUserName(userName);

        if(accountOptional==null || !accountOptional.getStatus()) {
            throw new AccountNotValidException("Account is not valid");
        }

        if (passwordEncoder.matches(password, accountOptional.getPassword())) {
            return jwtUtil.generateToken(accountOptional.getUserName(), accountOptional.getRole());
        }

        return null;
    }

    @Override
    public String Register(CreateAccountRequest createAccountRequest) {

        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        String password = passwordEncoder.encode(createAccountRequest.getPassword());
        if(createAccountRequest.getUsername() == null || createAccountRequest.getUsername().isEmpty()  || createAccountRequest.getPassword() == null || createAccountRequest.getPassword().isEmpty()
           || createAccountRequest.getRole() == null || createAccountRequest.getRole().isEmpty()) {
            throw new AccountNotValidException("Account is not valid");
        }

        Account account = new Account();
        account.setUserName(createAccountRequest.getUsername());
        account.setPassword(password);
        account.setRole(Roles.valueOf(createAccountRequest.getRole()));
        account.setStatus(true);
        accountRepository.save(account);

        return "Created Successfully";
    }

}
