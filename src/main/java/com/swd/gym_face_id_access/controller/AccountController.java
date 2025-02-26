package com.swd.gym_face_id_access.controller;


import com.swd.gym_face_id_access.dto.request.CreateAccountRequest;
import com.swd.gym_face_id_access.dto.response.ApiResponse;
import com.swd.gym_face_id_access.exception.AccountNotValidException;
import com.swd.gym_face_id_access.model.Account;
import com.swd.gym_face_id_access.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gym-face-id-access/api/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createAccount(@RequestBody CreateAccountRequest account) {
        try{
            return ResponseEntity.ok().body(ApiResponse.<String>builder()
                    .errorCode(null)
                    .message("success")
                    .data(accountService.Register(account))
                    .success(true)
                    .build());
        } catch (AccountNotValidException ex){
            throw new AccountNotValidException(ex.getMessage());
        }
    }
}
