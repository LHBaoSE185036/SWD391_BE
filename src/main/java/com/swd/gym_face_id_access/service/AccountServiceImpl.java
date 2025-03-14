package com.swd.gym_face_id_access.service;

import com.swd.gym_face_id_access.configuration.JwtUtil;
import com.swd.gym_face_id_access.dto.request.CreateAccountRequest;
import com.swd.gym_face_id_access.dto.response.AccountDetailResponse;
import com.swd.gym_face_id_access.dto.response.AccountResponse;
import com.swd.gym_face_id_access.exception.AccountNotFoundException;
import com.swd.gym_face_id_access.exception.AccountNotValidException;
import com.swd.gym_face_id_access.exception.UnauthorizedException;
import com.swd.gym_face_id_access.model.Account;
import com.swd.gym_face_id_access.model.Enum.Roles;
import com.swd.gym_face_id_access.model.Staff;
import com.swd.gym_face_id_access.repository.AccountRepository;
import com.swd.gym_face_id_access.repository.StaffRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final HttpServletRequest request;

    private final JwtUtil jwtUtil;

    private final StaffRepository staffRepository;

    @Override
    public String login(String userName, String password) {
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
    public String register(CreateAccountRequest createAccountRequest) {

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

    @Override
    public String deleteAccount(int accountId) {

        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }
        if(!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account is not found");
        }

        Account account = accountRepository.findById(accountId).get();
        account.setStatus(false);
        accountRepository.save(account);
        return "Deleted Successfully";
    }

    @Override
    @CachePut(value = "accounts", key = "accountId")
    public AccountDetailResponse getAccount(int accountId) {

        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }
        if(!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account is not found");
        }

        Account account = accountRepository.findById(accountId).get();
        if(!account.getStatus()){
            throw new AccountNotFoundException("Account is not found");
        }
        Staff staff =  staffRepository.findByAccountId(accountId);
        AccountDetailResponse accountResponse = new AccountDetailResponse();
        accountResponse.setUserName(account.getUserName());
        accountResponse.setPassword(account.getPassword());
        accountResponse.setStatus(account.getStatus());
        accountResponse.setRole(account.getRole());
        accountResponse.setStaffName(staff.getFullName());
        return accountResponse;
    }

    @Override
    @Cacheable(value = "accounts")
    public List<AccountResponse> getAllAccounts() {

        String token = jwtUtil.getCurrentToken(request);

        if(!jwtUtil.extractRole(token).equals(Roles.ADMIN)) {
            throw new UnauthorizedException("Unauthorized access");
        }


        List<Account> accounts = accountRepository.findAll();
        List<AccountResponse> accountResponses = new ArrayList<>();
        for(Account account : accounts) {
            AccountResponse accountResponse = new AccountResponse();
            accountResponse.setAccountId(account.getId());
            accountResponse.setUserName(account.getUserName());
            accountResponse.setRole(account.getRole());
            accountResponse.setStatus(account.getStatus());
            accountResponses.add(accountResponse);
        }
        return accountResponses;
    }

}
