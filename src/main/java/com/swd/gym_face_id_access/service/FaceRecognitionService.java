package com.swd.gym_face_id_access.service;
import com.swd.gym_face_id_access.dto.response.CheckInResponse;
import com.swd.gym_face_id_access.dto.response.CustomerMembershipResponse;
import com.swd.gym_face_id_access.dto.response.CustomerResponse;
import com.swd.gym_face_id_access.dto.response.FaceRecognitionResponse;
import com.swd.gym_face_id_access.model.CheckInLog;
import com.swd.gym_face_id_access.model.Customer;
import com.swd.gym_face_id_access.model.CustomerMembership;
import com.swd.gym_face_id_access.repository.CheckInLogRepository;
import com.swd.gym_face_id_access.repository.CustomerMembershipRepository;
import com.swd.gym_face_id_access.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.awspring.cloud.s3.S3Template;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class FaceRecognitionService {

    private final S3Client s3Client;
    private final RekognitionClient rekognitionClient;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final CustomerMembershipService customerMembershipService;
    private final CustomerMembershipRepository customerMembershipRepository;
    private final CheckInLogRepository checkinLogRepository;

    private final String CHECK_IN_SUCCESS = "Success";
    private final String CHECK_IN_FAILED = "Failed";
    private final String CHECK_IN_ERROR = "ERROR";
    private final CheckInLogRepository checkInLogRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    // REGISTER FACE
    public String uploadFile(MultipartFile file, int customerID) throws IOException {
        String key = "uploads/customer" + customerID;

        Map<String, String> metadata = new HashMap<>();
        metadata.put("customerId", String.valueOf(customerID)); // Set metadata
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .metadata(metadata)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return key;
    }


    public FaceRecognitionResponse findCustomerByFace(MultipartFile file) throws IOException {
        SdkBytes imageBytes = SdkBytes.fromByteArray(file.getBytes());

        Image image = Image.builder().bytes(imageBytes).build();

        SearchFacesByImageRequest request = SearchFacesByImageRequest.builder()
                .collectionId("swd_facerecognition_collection")
                .image(image)
                .maxFaces(1)
                .faceMatchThreshold(90F)
                .build();

        try {
            SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(request);
            List<FaceMatch> faceMatches = response.faceMatches();

            if (faceMatches != null && !faceMatches.isEmpty()) {
                String faceId = faceMatches.get(0).face().faceId();
                System.out.println("Found face ID: " + faceId);

                // Find customer by face ID
                CustomerResponse customer = findCustomerByFaceID(faceId);

                FaceRecognitionResponse frs = new FaceRecognitionResponse();
                frs.setCustomerId(customer.getCustomerId());
                frs.setFullName(customer.getFullName());
                frs.setPhoneNumber(customer.getPhoneNumber());
                frs.setEmail(customer.getEmail());
                frs.setStatus(customer.getStatus());

                return frs;
            }

            System.out.println("Person cannot be recognized");
            return null;

        } catch (InvalidParameterException e) {
            System.out.println("No faces detected in the image");
            return null;
        } catch (Exception e) {
            System.out.println("Error during face recognition: " + e.getMessage());
//            throw e;
            return null;
        }
    }
    public CustomerResponse findCustomerByFaceID(String faceId) {
        return customerService.findByFaceFeature(faceId);
    }

    public CheckInResponse customerCheckIn(MultipartFile file)  throws IOException{
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        CheckInResponse cr = new CheckInResponse();
        try{
            // check if person scanned is a member
            FaceRecognitionResponse frs = findCustomerByFace(file);
            if(frs == null){
                cr.setCheckInResult(CHECK_IN_FAILED);
                cr.setMessage("Cannot recognize member");
                cr.setIsSuccess(false);
                return cr;
            }
            // check for active memberships ( is still in date and session > 0 )
            List<CustomerMembershipResponse> activeCustomerMemberships =
                    customerMembershipService.findActiveMemberships(frs.getCustomerId());
            if(activeCustomerMemberships.isEmpty()){
                cr.setCheckInResult(CHECK_IN_FAILED);
                cr.setMessage("Member does not have active memberships");
                cr.setIsSuccess(false);
                return cr;
            }

            // Get the active membership with the Earliest endDate;
            CustomerMembershipResponse earliestMembership = activeCustomerMemberships.stream()
                    .min(Comparator.comparing(CustomerMembershipResponse::getEndDate))
                    .orElse(null);
            // the "or else return null" is for situations where the activeCustomerMemberships is null
            // even though it can never be null, I'll still leave it there.

            // Get the customer membership, lower its sessionCounter by 1, save
            CustomerMembership chosenMembership = customerMembershipRepository.findById(earliestMembership.getId());
            chosenMembership.setSessionCounter(chosenMembership.getSessionCounter() -1);
            customerMembershipRepository.save(chosenMembership);

            // Create check-in log
            CheckInLog checkInLog = new CheckInLog();
            Customer customer = customerRepository.getById(earliestMembership.getCustomerId());
            checkInLog.setCustomer(customer);
            checkInLog.setCheckInTime(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            checkInLogRepository.save(checkInLog);

            cr.setCheckInResult(CHECK_IN_SUCCESS);
            cr.setMessage("Check in successfully!");
            cr.setIsSuccess(true);
            return cr;

        }catch(Exception e){
            System.out.println("Error during check in " + e.getMessage());
            cr.setCheckInResult(CHECK_IN_ERROR);
            cr.setIsSuccess(false);
            return cr;
        }
    }
}
