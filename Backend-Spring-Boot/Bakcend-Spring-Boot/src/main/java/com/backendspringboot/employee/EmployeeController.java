package com.backendspringboot.employee;

import com.backendspringboot.exception.EmployeeNotFoundException;
import com.backendspringboot.exception.ExceptionHandling;
import com.backendspringboot.exception.UsernameExistException;
import com.backendspringboot.jwt.HttpResponse;
import com.backendspringboot.jwt.JWTTokenProvider;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.backendspringboot.constant.FileConstant.*;
import static com.backendspringboot.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
//the based Url of Employee class
@RequestMapping(path ={"/", "/employee"})
//we need to extend the exception Handling class that will handle every exception in our app
public class EmployeeController extends ExceptionHandling {
    public static final String EMAIL_SENT = "an email with new password was sent to: ";
    //private final RegistrationService registrationService;

    public static final String EMPLOYEE_DELETED_SUCCESSFULLY = "Employee Deleted Successfully";
    private EmployeeServiceInterface employeeServiceInterface;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;


    @Autowired
    public EmployeeController(EmployeeServiceInterface employeeServiceInterface, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.employeeServiceInterface = employeeServiceInterface;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<Employee> register(@RequestBody Employee employee) throws EmployeeNotFoundException, UsernameExistException, MessagingException {
        //we will call it from the interface
        Employee newEmployee = employeeServiceInterface.register(
                                employee.getFirstName(), employee.getLastName(),employee.getUsername(), employee.getPassword(),
                                employee.getFunction(), employee.getPhoneNumber(), employee.getNicNumber());
        return new ResponseEntity<>(newEmployee, HttpStatus.OK);
    }


    @GetMapping(path = "/confirmAccount")
    public void confirm(@RequestParam("token") String token, HttpServletResponse httpResponse) throws IOException {
        employeeServiceInterface.confirmToken(token);
        httpResponse.sendRedirect("/employee/login");
    }


    @PostMapping("/login")
    public ResponseEntity<Employee> login(@RequestBody Employee employee) {
        authenticate(employee.getUsername(),employee.getPassword());
        Employee loginEmployee = employeeServiceInterface.findEmployeeByUsername(employee.getUsername());
        EmployeePrincipal employeePrincipal = new EmployeePrincipal(loginEmployee);
        HttpHeaders jwtHeader = getJwtHeader(employeePrincipal);
        return new ResponseEntity<>(loginEmployee,jwtHeader, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Employee> addNewEmployee(@RequestParam("firstName") String firstName,
                                                   @RequestParam("lastName") String lastName,
                                                   @RequestParam("username") String username,
                                                   @RequestParam("function") String function,
                                                   @RequestParam("phoneNumber") Long phoneNumber,
                                                   @RequestParam("nicNumber") Long nicNumber,
                                                   @RequestParam("role") String role,
                                                   @RequestParam("isEnabled") String isEnabled,
                                                   @RequestParam("isNotlocked") String isNotlocked,
                                                   @RequestParam(value="profileImage", required=false) MultipartFile profileImage) throws IOException, UsernameExistException, EmployeeNotFoundException {

        Employee newEmployee = employeeServiceInterface.addNewEmployee(
                firstName,lastName,username,function,phoneNumber,nicNumber,role,Boolean.parseBoolean(isEnabled),Boolean.parseBoolean(isNotlocked),profileImage);
        return new ResponseEntity<>(newEmployee,HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Employee> updateEmployee(@RequestParam("currentUsername") String currentUsername,
                                                   @RequestParam("firstName") String firstName,
                                                   @RequestParam("lastName") String lastName,
                                                   @RequestParam("username") String username,
                                                   @RequestParam("function") String function,
                                                   @RequestParam("phoneNumber") Long phoneNumber,
                                                   @RequestParam("nicNumber") Long nicNumber,
                                                   @RequestParam("role") String role,
                                                   @RequestParam("isEnabled") String isEnabled,
                                                   @RequestParam("isNotlocked") String isNotlocked,
                                                   @RequestParam(value="profileImage", required=false) MultipartFile profileImage) throws IOException, UsernameExistException, EmployeeNotFoundException {

        Employee updatedEmployee = employeeServiceInterface.updateEmployee(
                currentUsername,firstName,lastName,username,function,phoneNumber,nicNumber,role,Boolean.parseBoolean(isEnabled),Boolean.parseBoolean(isNotlocked),profileImage);
        return new ResponseEntity<>(updatedEmployee,HttpStatus.OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<Employee> getEmployee(@PathVariable("username") String username){
        Employee employee = employeeServiceInterface.findEmployeeByUsername(username);
        return new ResponseEntity<>(employee,HttpStatus.OK);
    }

    @GetMapping("/find-all")
    public ResponseEntity<List<Employee>> getAllEmployees(){
        List<Employee> employees = employeeServiceInterface.getEmployees();
        return new ResponseEntity<>(employees,HttpStatus.OK);
    }

    @GetMapping("/reset-password/{username}")
    public ResponseEntity <HttpResponse> resetPassword(@PathVariable("username") String username) throws MessagingException {
        employeeServiceInterface.resetPassword(username);
        return response(HttpStatus.OK, EMAIL_SENT+ username);
    }

    @DeleteMapping("/delete/{employeeIdDataBase}")
    //have authority on method level
    @PreAuthorize("hasAnyAuthority('employee:delete')")
    public ResponseEntity<HttpResponse> deleteEmployee(@PathVariable("employeeIdDataBase") Long employeeIdDataBase){
        employeeServiceInterface.deleteEmployee(employeeIdDataBase);
        return response(HttpStatus.NO_CONTENT,EMPLOYEE_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<Employee> updateProfileImage(
            @RequestParam("username") String username,
            @RequestParam(value="profileImage") MultipartFile profileImage) throws IOException, UsernameExistException, EmployeeNotFoundException {
        Employee employee = employeeServiceInterface.updateProfileImage(username,profileImage);
        return new ResponseEntity<>(employee,HttpStatus.OK);
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username , @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(EMPLOYEE_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url =  new URL(TEMP_PROFILE_IMAGE_URL);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0){
                byteArrayOutputStream.write(chunk,0,bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(),message.toUpperCase());
        return new ResponseEntity<>(body,httpStatus);
    }


    private HttpHeaders getJwtHeader(EmployeePrincipal employeePrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(employeePrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

}
