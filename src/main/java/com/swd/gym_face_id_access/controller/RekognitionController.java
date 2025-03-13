package com.swd.gym_face_id_access.controller;

import com.swd.gym_face_id_access.dto.response.*;
import com.swd.gym_face_id_access.service.FaceRecognitionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.swd.gym_face_id_access.dto.response.ApiResponse;

import java.io.IOException;

@RequestMapping("/gym-face-id-access/api/v1/rekognition")
@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RekognitionController {
    private final FaceRecognitionService faceRecognitionService;

    @PostMapping("/register/{customerID}")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("customerID") int customerID) {
        try {
            String key = faceRecognitionService.uploadFile(file, customerID);
            return ResponseEntity.ok("File uploaded: " + key);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestParam("file") MultipartFile file,
                                          HttpServletRequest request) {

        System.out.println("Content Type: " + request.getContentType());
        System.out.println("Is Multipart: " + (request.getContentType() != null && request.getContentType().contains("multipart/form-data")));

        if (file == null) {
            return ResponseEntity.badRequest().body("No file received");
        }

        try {
            FaceRecognitionResponse customer = faceRecognitionService.findCustomerByFace(file);

            if (customer != null) {
                return ResponseEntity.ok("This is: " + customer.getFullName());
            } else {
                return ResponseEntity.ok()
                        .body("I dont know who you are bruh");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("That ain't right");
        }
    }

    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(@RequestParam("file") MultipartFile file,
                                          HttpServletRequest request) {

        System.out.println("Content Type: " + request.getContentType());
        System.out.println("Is Multipart: " + (request.getContentType() != null && request.getContentType().contains("multipart/form-data")));


        try {
            CheckInResponse response = faceRecognitionService.customerCheckIn(file);
            return ResponseEntity.ok().body(ApiResponse.<CheckInResponse>builder()
                    .errorCode(null)
                    .message("success")
                    .data(response)
                    .success(true)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<CheckOutResponse>> checkOut(@RequestParam("file") MultipartFile file,
                                                                HttpServletRequest request) {

        System.out.println("Content Type: " + request.getContentType());
        System.out.println("Is Multipart: " + (request.getContentType() != null && request.getContentType().contains("multipart/form-data")));


        try {
            CheckOutResponse response = faceRecognitionService.customerCheckOut(file);
            return ResponseEntity.ok().body(ApiResponse.<CheckOutResponse>builder()
                    .errorCode(null)
                    .message("success")
                    .data(response)
                    .success(true)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
