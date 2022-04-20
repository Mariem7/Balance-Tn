package com.backendspringboot.employee;

import com.backendspringboot.constant.EmployeeServiceConstant;
import com.backendspringboot.email.EmailValidator;
import com.backendspringboot.employee.BruteForceAttackPrevent.LoginAttemptService;
import com.backendspringboot.employee.confirmationtoken.ConfirmationToken;
import com.backendspringboot.employee.confirmationtoken.ConfirmationTokenService;
import com.backendspringboot.exception.EmployeeNotFoundException;
import com.backendspringboot.exception.UsernameExistException;
import com.backendspringboot.email.EmailService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.backendspringboot.constant.EmployeeServiceConstant.NO_EMPLOYEE_FOUND_BY_USERNAME;
import static com.backendspringboot.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.logging.log4j.util.Strings.EMPTY;


@Service
//manage propagation
@Transactional
//The @Qualifier annotation in Spring is used to differentiate a bean among the same type of bean objects.
//If we have more than one bean of the same type and want to wire only one of them then use the @Qualifier
//annotation along with @Autowired to specify which exact bean will be wired.
@Qualifier("userDetailsService")
public class EmployeeService implements EmployeeServiceInterface, UserDetailsService {

    public static final String EMAIL_IS_NOT_VALID = "email is not valid";
    //A good logging infrastructure is necessary for any software project as it not only helps in understanding
    // what’s going on with the application but also to trace any unusual incident or error present in the project.
    //that's why we use LOGGER ocject (we need it to see how many time did the employee log to the app and if he pass
    // the 5 times we will block him
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private EmployeeRepository employeeRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    private final EmailValidator emailValidator;

    private final ConfirmationTokenService confirmationTokenService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, EmailService emailService, EmailValidator emailValidator, ConfirmationTokenService confirmationTokenService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.emailService = emailService;
        this.emailValidator = emailValidator;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    public Employee register(String firstName, String lastName, String username,String password, String function, Long phoneNumber, Long nicNumber) throws UsernameExistException, EmployeeNotFoundException, MessagingException {
        //before register the employee we need to check if the username has already exist or not
        validateNewUsername(StringUtils.EMPTY, username);
        //check if the username is valid
        //check if the email is valid
        boolean isValidEmail = emailValidator.test(username);
        if(!isValidEmail){
            throw new IllegalStateException(String.format(EMAIL_IS_NOT_VALID,username));
        }
        Employee employee = new Employee();
        employee.setEmployeeId(generateEmployeeId());
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setUsername(username);
        employee.setFunction(function);
        employee.setPhoneNumber(phoneNumber);
        employee.setNicNumber(nicNumber);
        employee.setJoindDate(new Date());
        employee.setPassword(encodePassword(password));
        //we need to change it when we will implement the email verification
        employee.setNotlocked(true);
        employee.setRole(Role.ROLE_USER.name());
        employee.setAuthorities(Role.ROLE_USER.getAuthorities());
        employee.setProfileImageUrl(getTemporaryProfileImageURL(username));

        //save the employee
        employeeRepository.save(employee);

        //this is the type of token
        //UUID is a class that represents an immutable Universally Unique Identifie
        String token = UUID.randomUUID().toString();
        //Send confirmation token
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60), employee);
        //save the confirmation token
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        //after saving the employee we will send the token
        String link = "http://localhost:8090/employee/confirmAccount?token=" + token;
        //email to send the confirmation link
        String email = emailService.buildConfirmationEmail(employee.getFirstName(),link);
        emailService.sendConfirmationEmail(firstName,email,username);
        return employee;
    }

    @Transactional
    @Override
    public String confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));
        if (confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("Email Already Confirmed");}
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if(expiredAt.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("token expired");}
        confirmationTokenService.setConfirmedAt(token);
        employeeRepository.enableEmployee(
                confirmationToken.getEmployee().getUsername());
        return "confirmed";
    }

    @Override
    public Employee addNewEmployee(String firstName, String lastName, String username, String function, Long phoneNumber, Long nicNumber, String role, boolean isEnabled, boolean isNotlocked, MultipartFile profileImage) throws UsernameExistException, EmployeeNotFoundException, IOException {
        validateNewUsername(EMPTY,username);
        Employee employee = new Employee();
        employee.setEmployeeId(generateEmployeeId());
        String password = generatePassword();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setUsername(username);
        employee.setFunction(function);
        employee.setPhoneNumber(phoneNumber);
        employee.setNicNumber(nicNumber);
        employee.setJoindDate(new Date());
        employee.setPassword(encodePassword(password));
        //we need to change it when we will implement the email verification
        employee.setEnabled(isEnabled);
        employee.setNotlocked(isNotlocked);
        employee.setRole(getRoleEnumName(role).name());
        employee.setAuthorities(getRoleEnumName(role).getAuthorities());
        employee.setProfileImageUrl(getTemporaryProfileImageURL(username));
        //save the employee
        employeeRepository.save(employee);
        saveProfileImage(employee,profileImage);
        return employee;
    }

    @Override
    public Employee updateEmployee(String currentUsername, String newFirstName, String newLastName, String newUsername, String newFunction, Long newPhoneNumber, Long newCicNumber, String role, boolean isEnabled, boolean isNotlocked, MultipartFile profileImage) throws UsernameExistException, EmployeeNotFoundException, IOException {
        Employee currentEmployee = validateNewUsername(currentUsername,newUsername);
        currentEmployee.setFirstName(newFirstName);
        currentEmployee.setLastName(newLastName);
        currentEmployee.setUsername(newUsername);
        currentEmployee.setFunction(newFunction);
        currentEmployee.setPhoneNumber(newPhoneNumber);
        currentEmployee.setNicNumber(newCicNumber);
        currentEmployee.setJoindDate(new Date());
        currentEmployee.setEnabled(isEnabled);
        currentEmployee.setNotlocked(isNotlocked);
        currentEmployee.setRole(getRoleEnumName(role).name());
        currentEmployee.setAuthorities(getRoleEnumName(role).getAuthorities());
        //save the employee
        employeeRepository.save(currentEmployee);
        saveProfileImage(currentEmployee,profileImage);
        return currentEmployee;
    }

    @Override
    public void deleteEmployee(long employeeIdDataBase) {
        employeeRepository.deleteById(employeeIdDataBase);
    }

    @Override
    public void resetPassword(String username) throws MessagingException {
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        if(employee == null){
            throw new UsernameNotFoundException(NO_EMPLOYEE_FOUND_BY_USERNAME + username);
        }
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        employee.setPassword(encodedPassword);
        employeeRepository.save(employee);
        String email = emailService.buildResetPasswordEmail(employee.getFirstName(),password);
        emailService.sendNewPasswordEmail(employee.getFirstName(),email,employee.getUsername());
    }

    @Override
    public Employee updateProfileImage(String username, MultipartFile profileImage) throws UsernameExistException, EmployeeNotFoundException, IOException {
        Employee employee = validateNewUsername(username,null);
        saveProfileImage(employee,profileImage);
        return employee;
    }

    private void saveProfileImage(Employee employee, MultipartFile profileImage) throws IOException {
        if(profileImage != null){
            Path employeeFolder = Paths.get(EMPLOYEE_FOLDER + employee.getUsername()).toAbsolutePath().normalize();
            //if the folder doesn't exists, then we will create a folder
            if(!Files.exists(employeeFolder)){
                Files.createDirectories(employeeFolder);
                LOGGER.info(DIRECTORY_CREATED + employeeFolder);
            }
            //if there is a file that exists, we will delete it
            Files.deleteIfExists(Paths.get(employeeFolder + employee.getUsername() + DOT + JPG_EXTENSION));
            //replace the image
            Files.copy(profileImage.getInputStream(), employeeFolder.resolve(employee.getUsername() + DOT + JPG_EXTENSION)
                    ,REPLACE_EXISTING);
            employee.setProfileImageUrl(setProfileImageUrl(employee.getUsername()));
            employeeRepository.save(employee);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    @Override
    public List<Employee> getEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findEmployeeByUsername(String username) {
        return employeeRepository.findEmployeeByUsername(username);
    }

    private String getTemporaryProfileImageURL(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_EMPLOYEE_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    //method to generate the employeeID that we will use in the app (not in the database)
    private String generateEmployeeId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase(Locale.ROOT));
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(EMPLOYEE_IMAGE_PATH + username + FORWARD_SLASH
        + username + DOT + JPG_EXTENSION).toUriString();
    }


    //spring security call this method to find the user
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //find the employee by username
        Employee employee = employeeRepository.findEmployeeByUsername(username);
        if(employee == null){
            LOGGER.error(EmployeeServiceConstant.EMPLOYEE_NOT_FOUND_BY_USERNAME +username);
            throw new UsernameNotFoundException(EmployeeServiceConstant.EMPLOYEE_NOT_FOUND_BY_USERNAME +username);
        }else{
            //we will check if the user is actually not locked
            validateLoginAttempt(employee);
            //here we know that the employee has been authenticated successfully
            //we will display the last time that he log in
            employee.setLastLoginDateDisplay(employee.getLastLoginDate());
            //then we will set the time that he log in which is now
            employee.setLastLoginDate(new Date());
            //we need to save this current informations (lastlogindate..)
            employeeRepository.save(employee);
            EmployeePrincipal employeePrincipal = new EmployeePrincipal(employee);
            LOGGER.info(EmployeeServiceConstant.RETURNING_FOUND_EMPLOYEE_BY_USERNAME +username);
            return employeePrincipal;
        }

    }

    //method to validate the login attempt
    private void validateLoginAttempt(Employee employee){
        if(employee.isNotlocked()){
            //if the account is not locked then we will check if the employee has exceeded the number of attempts
            if(loginAttemptService.hasExceededMaxAttempts(employee.getUsername())){
                //the employee will be locked
                employee.setNotlocked(false);
            }else{
                //then the account is not locked (it means that the actual employee didn't reach the max of attempt)
                employee.setNotlocked(true);
            }
        }else{
            //when the account is locked then we will remove the employee from the cache
            loginAttemptService.evictEmployeeFromLoginAttemptCache(employee.getUsername());
        }
    }

    //method to validate the new username
    private Employee validateNewUsername(String currentUsername, String newUsername) throws EmployeeNotFoundException, UsernameExistException {
        Employee employeeByNewUsername = findEmployeeByUsername(newUsername);
        //we use the currentUnsername if we are in the case of updating the username
        //otherwise, we know that we are dealing with a new employee
        if(StringUtils.isNotBlank(currentUsername)){
            Employee currentEmployee = findEmployeeByUsername(currentUsername);
            //if the currentEmployee is null then there is no employee with this username
            if(currentEmployee == null){
                throw new EmployeeNotFoundException(NO_EMPLOYEE_FOUND_BY_USERNAME + currentUsername);
            }
            //check if the newUsername is not taken by an other employee
            if(employeeByNewUsername != null && !currentEmployee.getEmployeeId().equals(employeeByNewUsername.getEmployeeId())){
                //there we know that the employee exists
                throw new UsernameExistException(EmployeeServiceConstant.USERNAME_ALREADY_EXISTS);
            }
            return currentEmployee;
        }else{
            //if the username is not null that means that we can register with an existing username
            if(employeeByNewUsername != null){
                throw new UsernameExistException(EmployeeServiceConstant.USERNAME_ALREADY_EXISTS);
            }
            return null;
        }

    }

    public int enableEmployee(String username){
        return employeeRepository.enableEmployee(username);
    }



}
