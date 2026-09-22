package in.gov.dilrmp.controllers.user;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.gov.dilrmp.models.dashboard.DashBordDTO;
import in.gov.dilrmp.services.dashboard.DashBoardService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import in.gov.dilrmp.models.administrativeBoundry.*;
import in.gov.dilrmp.services.administrativeBoundry.*;
import in.gov.dilrmp.utils.Constant;
import in.gov.dilrmp.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import in.gov.dilrmp.models.user.User;
import in.gov.dilrmp.models.user.UserProfile;
import in.gov.dilrmp.repositories.user.UserRepositry;
import in.gov.dilrmp.services.user.UserProfileService;
import in.gov.dilrmp.services.user.UserService;
import in.gov.dilrmp.utils.LoogedInUserUtility;


@Controller
@RequestMapping("userProfile")
public class UserProfileController {
    @Autowired
    UserService userService;
    @Autowired
    LoogedInUserUtility loogedInUserUtility;
    @Autowired
    UserProfileService userpservice;
    @Autowired
    UserRepositry userrepositry;

    @Autowired
    StateService stateService;

    @Autowired
    UserLoginHistoryService userLoginHistoryService;

    @Autowired
    UserActivityService userActivityService;
    @Autowired
    DashBoardService dashBoardService;

    @Autowired
    private ServletContext servletContext;

    private static String token;


    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @RequestMapping(value = "/update-profile", method = RequestMethod.GET)
    public String updateProfileView(Model model, HttpServletRequest request, HttpServletResponse response) {
        // Check if the user is authenticated (session is active)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            // If user is not authenticated, redirect to the login page
            return "redirect:/loginPage";
        }

        // Fetch the context path
        String contextPath = servletContext.getContextPath();  // No need for a null check

        // Check if model already contains 'uProfile', otherwise fetch it
        if (!model.containsAttribute("uProfile")) {
            long userId = loogedInUserUtility.getLoggedinUser().getUserID();
            UserProfile userProfile = userpservice.getUserProfile(userId);

            // If userProfile is null, create a new UserProfile
            if (userProfile == null) {
                userProfile = new UserProfile();
            }

            // Add user profile and context path to the model
            model.addAttribute("uProfile", userProfile);
            model.addAttribute("contextPath", contextPath);
        }

        return "pages/userprofile/update-profile::update-user-profile";
    }


    @PostMapping("/uprofile")
    public ResponseEntity<Object> profileUpdate(UserProfile uProfile, BindingResult bindingResult, Model model, HttpServletRequest request) {
        LocalDate releaseDate = LocalDate.now();

        try {
            String storedCaptcha = (String) request.getSession().getAttribute("captchaText");
            // Check for validation errors in the BindingResult
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body("Validation error");
            }

            // Check email format
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(uProfile.getEmail());
            if (!matcher.matches()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid email format");
            }

            String mobileRegex = "^[6-9]\\d{9}$";
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            Matcher mobileMatcher = mobilePattern.matcher(uProfile.getMobileNo());

            if (!mobileMatcher.matches()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid mobile number format");
            }


            // Check if captcha is present in session

            if (storedCaptcha == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Captcha verification failed!");
            }

            // Validate the captcha input
            if (uProfile.getCaptcha() == null || !storedCaptcha.equals(uProfile.getCaptcha())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Captcha verification failed!");
            }

            // Remove captcha from session after successful validation
            request.getSession().removeAttribute("captchaText");

            // Clean and transform the user's name
            uProfile.setName(uProfile.getName().replaceAll("[^a-zA-Z0-9]", " ").toUpperCase());

            // Set the user and current date
            uProfile.setMuser(loogedInUserUtility.getLoggedinUser().getUser());
            uProfile.setCurrentDate(releaseDate);

            // Save user profile
            userpservice.addUserProfile(uProfile);

            // Return success response
           /* return ResponseEntity.ok("Profile Updated Successfully!");*/
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Profile Updated Successfully!");

        } catch (Exception e) {
            // Log and return error response in case of exceptions
            logger.error("An error occurred while updating the profile.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong!");
        }
    }




    @RequestMapping(value = "/change-password", method = RequestMethod.GET)
    public String changePaswordView(Model model) {
        model.addAttribute("changepassword", new User());
        return "pages/userprofile/change-password::change-password";
    }



    @PostMapping("/changepass")
    public ResponseEntity<Object> changePassword(User changepassword,
                                                 BindingResult bindingResult,
                                                 Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(bindingResult.getAllErrors());
        }

        // UserService userDetails = (UserService) authentication.getPrincipal();
        User userDetails = loogedInUserUtility.getLoggedinUser().getUser();

        if (!userDetails.getPassword().equals(changepassword.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Incorrect old password!");
        }

        if (userDetails.getPassword().equals(changepassword.getNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("New password and old password are same!");
        } else {
            if (!changepassword.getNewPassword().equals("") || !changepassword.getConfirmPassword().equals("")) {
                if (changepassword.getNewPassword().equals(changepassword.getConfirmPassword())) {
                    try {
                        User u = loogedInUserUtility.getLoggedinUser().getUser();
                        u.setPassword(changepassword.getNewPassword());
                        u.setLastPasswordChangeDate(LocalDateTime.now());

                        userrepositry.save(u);
                        userrepositry.updateUserActive(u.getUserName(),false);

                        // Log successful password update
                        logger.info("Password updated successfully for user: {}", u.getUserName());

                        String message = "Password Updated Successfully!";
                        String redirectUrl = "/loginPage";
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", message);
                        response.put("redirectUrl", redirectUrl);
                        return ResponseEntity.ok(response);

                    } catch (Exception e) {
                        // Log the error message and exception
                        logger.error("An error occurred while updating the password for user: {}.",
                                userDetails.getUserName(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Something went wrong!");
                    }

                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Password Not matched!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Must not be blank above both fields");
            }
        }
    }

    @RequestMapping(value = "/forget-password", method = RequestMethod.GET)
    public String forgotPaswordView() {
        return "pages/userprofile/forget-password";
    }

    @RequestMapping(value = "/contact-us", method = RequestMethod.GET)
    public String contactView() {
        return "pages/userprofile/contact-us";
    }



    @RequestMapping(value = "/document", method = RequestMethod.GET)
    public String documentView(Model model) {
        DashBordDTO nationalDashBordDTOList = dashBoardService.getNationalDashBoardData();
        model.addAttribute("nationalDashBordDTOList",nationalDashBordDTOList);
        return "pages/reportlink/document";
    }

    @RequestMapping(value = "/about-us", method = RequestMethod.GET)
    public String aboutView() {
        return "pages/userprofile/about-us";
    }


    @GetMapping("/user-list")
    public String UserList(Model model,HttpSession session){

        Long user_id = (Long) session.getAttribute("userid");
        State state = stateService.findStateByUserID(user_id);
        String stateName = (state != null) ? state.getName() : "Unknown State";
        List<Object> userlist = userService.getUsersByMuserId(user_id);
        model.addAttribute("userlist",userlist);
        model.addAttribute("stateName",stateName);
        session.setAttribute("userlist",userlist);
        session.setAttribute("stateName",stateName);
        return "pages/userprofile/user-list::user-list-fragment";
    }

    @RequestMapping(value = "/data-entry-details", method = RequestMethod.GET)
    public String auditview(Model model, HttpSession session) {
        // Format to the desired pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss ");

        // Retrieve a list of the most recent login history records for each user
        List<UserLoginHistory> loginHistoryList = userLoginHistoryService.loginHistoryList();
        session.setAttribute("loginHistoryList", loginHistoryList);

        // Create a Map to store the last login, logout times, and IP address for each
        // user
        Map<String, Object[]> userLoginLogoutTimes = new HashMap<>();

        // Iterate through the login history records to find the latest login, logout
        // times, and IP address for each user
        for (UserLoginHistory loginHistory : loginHistoryList) {
            String username = loginHistory.getUser().getUserName();
            Object[] info = userLoginLogoutTimes.get(username);
            State state = stateService.findStateByUserID(loginHistory.getUser().getId());

            if (info == null) {
                info = new Object[] { null, null, null, null, null, null, null };
                userLoginLogoutTimes.put(username, info);
            }
            if (loginHistory.getUser().getRoleId() != null) {

                info[0] = loginHistory.getUser().getRoleId();
            }
            if (state != null) {
                info[1] = state.getName();
            }


            if (loginHistory.getLoginTime() != null) {

                String loginDateTime = loginHistory.getLoginTime().format(formatter);
                info[3] = loginDateTime;
            }

            if (loginHistory.getLogoutTime() != null) {
                String logoutDateTime = loginHistory.getLogoutTime().format(formatter);
                info[4] = logoutDateTime;
            }

            if (loginHistory.isLoginStatus() == true) {

                info[5] = "Login";
            } else {
                info[5] = "Logout";
            }
            if (loginHistory.getId() != null) {
                info[6] = loginHistory.getUser().getId();
            }

        }
        // Add the data to the model
        model.addAttribute("userLoginLogoutTimes", userLoginLogoutTimes);
        return "pages/userprofile/data-entry-details";
    }

    @PostMapping("/getUserActivity/{id}")
    public ResponseEntity<List<UserActivityBean>> getUserLoginHistory(@PathVariable("id") long id) {
        List<UserActivity> userActivityList = userActivityService.findAllLoginHistoryByID(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss ");
        List<UserActivityBean> beans = new ArrayList<>();
        for (UserActivity userActivity : userActivityList) {
            UserActivityBean bean = new UserActivityBean();
            bean.setId(userActivity.getId());
            bean.setName(userActivity.getUserLoginHistory().getUser().getUserName());
            bean.setActivity(userActivity.getActivity());
            if (userActivity.getActiveTime() != null) {
                bean.setActiveTime(userActivity.getActiveTime().format(formatter));
            }
            beans.add(bean);
        }

        if (beans.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(beans, HttpStatus.OK);
    }

    public static String getOneTimeToken(){
        return token;
    }



   /* public static boolean validateCaptcha(String captcha, HttpServletRequest request) {

        String storedCaptchaText = (String) request.getSession().getAttribute("captchaText");// Assuming this method exists
        request.getSession().removeAttribute("captchaText");
       // Check if both CAPTCHA and user input are not null
        return storedCaptchaText != null && captcha != null && storedCaptchaText.equals(captcha);
    }*/

    public static String hashWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isValid(String s)
    {

        Pattern p = Pattern.compile(Constant.REGEX_WITH_MOBILE_NO);


        Matcher m = p.matcher(s);


        return (m.matches());
    }


    public static boolean isValidUsername(String name)
    {
        Pattern p = Pattern.compile(Constant.REGEX_WITH_SPACE);
        if (name == null) {
            return false;
        }
        Matcher m = p.matcher(name);

        return m.matches();
    }


    @RequestMapping(value = "/reset-password", method = RequestMethod.GET)
    public String resetPassword() {
        return "pages/userprofile/reset-password::reset-password";
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String userName) {
        try {
            // Check if the user exists
            boolean exists = userService.doesUserNameExist(userName);

            // If the user does not exist, log the event and return an error response
            if (!exists) {
                logger.warn("Attempt to reset password for non-existing user: {}", userName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Use a hardcoded hashed password (for example purposes)
            String hashedPassword = "633eca23d39973d68da8af63962fba9ac2f9aa19920920e9977387d2851b3bdf";

            // Call the service to update the password in the database
            boolean success = userService.resetPassword(userName, hashedPassword);

            if (success) {
                logger.info("Password reset successfully for user: {}", userName);
                return ResponseEntity.ok("Password reset successfully with default password( Dolr$wE3Q@1A3 )");
            } else {
                logger.error("Failed to reset password for user: {}", userName);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reset password.");
            }
        } catch (Exception e) {
            // Log the exception
            logger.error("An error occurred while resetting the password for user: {}. Error: {}", userName, e.getMessage(), e);
            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while resetting the password.");
        }
    }


}





