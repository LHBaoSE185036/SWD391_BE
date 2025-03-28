package com.swd.gym_face_id_access.service;
import com.swd.gym_face_id_access.dto.response.*;
import com.swd.gym_face_id_access.exception.FaceNotFoundException;
import com.swd.gym_face_id_access.model.CheckInLog;
import com.swd.gym_face_id_access.model.Customer;
import com.swd.gym_face_id_access.model.CustomerMembership;
import com.swd.gym_face_id_access.model.Membership;
import com.swd.gym_face_id_access.repository.CheckInLogRepository;
import com.swd.gym_face_id_access.repository.CustomerMembershipRepository;
import com.swd.gym_face_id_access.repository.CustomerRepository;
import com.swd.gym_face_id_access.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class FaceRecognitionService {

    private final S3Client s3Client;
    private final RekognitionClient rekognitionClient;
    private final CustomerServiceImpl customerService;
    private final CustomerRepository customerRepository;
    private final CustomerMembershipService customerMembershipService;
    private final CustomerMembershipRepository customerMembershipRepository;
    private final CheckInLogRepository checkinLogRepository;
    private final CloudinaryService cloudinaryService;
    private final MembershipRepository membershipRepository;

    private final String CHECK_IN_SUCCESS = "Success";
    private final String CHECK_IN_RETURN = "Rechecked";
    private final String CHECK_IN_FAILED = "Failed";
    private final String CHECK_IN_ERROR = "ERROR";

    private final String CHECK_OUT_SUCCESS = "Success";
    private final String CHECK_OUT_FAILED = "Failed";
    private final String CHECK_OUT_ERROR = "ERROR";

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

        String ImgUrl = cloudinaryService.uploadFile(file);
        Customer customer = customerRepository.findById(customerID).get();
        customer.setFaceImage(ImgUrl);
        customerRepository.save(customer);

        return key;
    }


    public FaceRecognitionResponse findCustomerByFace(MultipartFile file) throws IOException {
        SdkBytes imageBytes = SdkBytes.fromByteArray(file.getBytes());

        Image image = Image.builder().bytes(imageBytes).build();

        SearchFacesByImageRequest request = SearchFacesByImageRequest.builder()
                .collectionId("swd_facerecognition_collection")
                .image(image)
                .maxFaces(1)
                .faceMatchThreshold(98F)
                .build();

        try {
            SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(request);
            List<FaceMatch> faceMatches = response.faceMatches();

            if (faceMatches != null && !faceMatches.isEmpty()) {
                String faceId = faceMatches.get(0).face().faceId();
                System.out.println("Found face ID: " + faceId);

                // Find customer by face ID
                Customer customer = findCustomerByFaceID(faceId);

                FaceRecognitionResponse frs = new FaceRecognitionResponse();
                frs.setCustomerId(customer.getId());
                frs.setFullName(customer.getFullName());
                frs.setPhoneNumber(customer.getPhoneNumber());
                frs.setEmail(customer.getEmail());
                frs.setStatus(customer.getStatus());
                frs.setPresent_status(customer.getPresentStatus());
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
    public Customer findCustomerByFaceID(String faceId) {
        return customerRepository.getByFaceFeature(faceId);
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
                cr.setMessage("Does not recognize member.");
                cr.setIsSuccess(false);
                return cr;
            }

            //Check if customer has already checked in (present_status = true)
            if(frs.getPresent_status()){
                cr.setCheckInResult(CHECK_IN_RETURN);
                cr.setMessage("Member has already checked in");
                cr.setIsSuccess(true);
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


            List<CustomerMembership> customerMembershipList = customerMembershipRepository.findByCustomerId(frs.getCustomerId());

            System.out.println("Starting to check for membership slot time.");
            Membership membership = new Membership();
            for(CustomerMembership customerMembership : customerMembershipList){
                membership = customerMembership.getMembership();
            }
            ZoneId zone = ZoneId.systemDefault();
            LocalTime currentTime = LocalTime.now(zone);

            if(membership.getSlotTimeType().equals("Khung giờ A")){
                System.out.println("This guy is in khung gio A");
                LocalTime startTime1 = LocalTime.of(5, 30);
                LocalTime endTime1 = LocalTime.of(8, 0);

                LocalTime startTime2 = LocalTime.of(18, 0);
                LocalTime endTime2 = LocalTime.of(21, 0);

                if ((!currentTime.isBefore(startTime1) && !currentTime.isAfter(endTime1)) || (!currentTime.isBefore(startTime2) && !currentTime.isAfter(endTime2))) {
                    System.out.println("ok bro");
                }
                else{
                    cr.setCheckInResult(CHECK_IN_FAILED);
                    cr.setMessage("Incorrect Time Slot");
                    cr.setIsSuccess(false);
                    return cr;
                }
            }else if(membership.getSlotTimeType().equals("Khung giờ B")){
                System.out.println("This guy is in khung gio B");
//                LocalTime startTime1 = LocalTime.of(8, 0);
//                LocalTime endTime1 = LocalTime.of(14, 0);
//
//                LocalTime startTime2 = LocalTime.of(15, 0);
//                LocalTime endTime2 = LocalTime.of(18, 0);
//
//                if ((!currentTime.isBefore(startTime1) && !currentTime.isAfter(endTime1)) || (!currentTime.isBefore(startTime2) && !currentTime.isAfter(endTime2))) {
//                    System.out.println("ok bro");
//                }
//                else{
//                    cr.setCheckInResult(CHECK_IN_FAILED);
//                    cr.setMessage("Incorrect Time Slot");
//                    cr.setIsSuccess(false);
//                    return cr;
//                }
            }else{
                System.out.println("Sum ting wong");
                cr.setCheckInResult(CHECK_IN_FAILED);
                cr.setMessage("Invalid Time Slot");
                cr.setIsSuccess(false);
                return cr;
            }

            System.out.println("Slot time validated");



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

            // Set customer's present_status to true
            customer.setPresentStatus(true);
            customerRepository.save(customer);

            customerService.notifyClients();

            cr.setCheckInResult(CHECK_IN_SUCCESS);
            cr.setMessage("Welcome: " + customer.getFullName());
            cr.setIsSuccess(true);
            return cr;

        }catch(Exception e){
            System.out.println("Error during check in " + e.getMessage());
            cr.setCheckInResult(CHECK_IN_ERROR);
            cr.setIsSuccess(false);
            return cr;
        }
    }

    public CheckOutResponse customerCheckOut(MultipartFile file)  throws IOException{
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        CheckOutResponse cr = new CheckOutResponse();
        try{
            // check if person scanned is a member
            FaceRecognitionResponse frs = findCustomerByFace(file);
            if(frs == null){
                cr.setCheckOutResult(CHECK_OUT_FAILED);
                cr.setMessage("Does not recognize member.");
                cr.setIsSuccess(false);
                return cr;
            }
            //Check if customer has already checked in (present_status = true)
            if(frs.getPresent_status()){
                Customer customer = customerRepository.getById(frs.getCustomerId());
                customer.setPresentStatus(false);
                customerRepository.save(customer);

                customerService.notifyClients();

                cr.setCheckOutResult(CHECK_OUT_SUCCESS);
                cr.setMessage("Checked out successfully!");
                cr.setIsSuccess(true);
                return cr;
            }else{
                cr.setCheckOutResult(CHECK_OUT_FAILED);
                cr.setMessage("Checkout failure: Customer has not checked in!");
                cr.setIsSuccess(false);
                return cr;
            }

        }catch(Exception e){
            System.out.println("Error during check out " + e.getMessage());
            cr.setCheckOutResult(CHECK_OUT_ERROR);
            cr.setIsSuccess(false);
            return cr;
        }
    }
}
