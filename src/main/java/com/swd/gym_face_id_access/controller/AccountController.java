package com.swd.gym_face_id_access.controller;


import com.swd.gym_face_id_access.dto.request.CreateAccountRequest;
import com.swd.gym_face_id_access.dto.response.AccountDetailResponse;
import com.swd.gym_face_id_access.dto.response.AccountResponse;
import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.exception.AccountNotFoundException;
import com.swd.gym_face_id_access.exception.AccountNotValidException;
import com.swd.gym_face_id_access.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createAccount(@RequestBody CreateAccountRequest account) {
        try{
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                    .errorCode(null)
                    .message("success")
                    .data(accountService.register(account))
                    .success(true)
                    .build());
        } catch (AccountNotValidException ex){
            throw new AccountNotValidException(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@PathVariable int id) {
        try{
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                    .errorCode(null)
                    .message("success")
                    .data(accountService.deleteAccount(id))
                    .success(true)
                    .build());
        } catch (AccountNotFoundException ex){
            throw new AccountNotFoundException(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDetailResponse>> getAccount(@PathVariable int id) {
        try{
            return ResponseEntity.ok().body(ApiResponse.<AccountDetailResponse>builder()
                    .errorCode(null)
                    .message("success")
                    .data(accountService.getAccount(id))
                    .success(true)
                    .build());
        } catch (AccountNotFoundException ex){
            throw new AccountNotFoundException(ex.getMessage());
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccounts() {
        try{
            return ResponseEntity.ok().body(ApiResponse.<List<AccountResponse>>builder()
                    .errorCode(null)
                    .message("success")
                    .data(accountService.getAllAccounts())
                    .success(true)
                    .build());
        } catch (RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
