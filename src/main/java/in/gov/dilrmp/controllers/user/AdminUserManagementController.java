package in.gov.dilrmp.controllers.user;

import in.gov.dilrmp.models.user.AdminManagedUserRow;
import in.gov.dilrmp.models.user.Role;
import in.gov.dilrmp.services.user.AdminUserManagementService;
import in.gov.dilrmp.services.user.AdminUserManagementService.UserPdfPart;
import in.gov.dilrmp.utils.AdminUserListPdfExporter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/userProfile")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;

    public AdminUserManagementController(AdminUserManagementService adminUserManagementService) {
        this.adminUserManagementService = adminUserManagementService;
    }

    @GetMapping("/admin-user-list")
    public String list(@RequestParam(name = "role", required = false) String role,
                       @RequestParam(name = "stateId", required = false) Long stateId,
                       Authentication authentication,
                       Model model) {
        requireAdmin(authentication);
        String selectedRole = adminUserManagementService.normalizeRole(role);
        boolean stateRequired = adminUserManagementService.requiresState(selectedRole);
        Long selectedStateId = stateRequired ? stateId : null;
        String stateName = selectedStateId == null ? "" : adminUserManagementService.stateName(selectedStateId);

        model.addAttribute("selectedRole", selectedRole);
        model.addAttribute("selectedStateId", selectedStateId);
        model.addAttribute("selectedStateName", stateName);
        model.addAttribute("stateRequired", stateRequired);
        model.addAttribute("states", adminUserManagementService.states());
        model.addAttribute("users", adminUserManagementService.users(selectedRole, selectedStateId));
        model.addAttribute("awaitingState", stateRequired && selectedStateId == null);
        return "pages/userprofile/admin-user-list :: admin-user-list";
    }

    @GetMapping("/admin-user-list/pdf")
    public void pdf(@RequestParam(name = "role", required = false) String role,
                    @RequestParam(name = "stateId", required = false) Long stateId,
                    Authentication authentication,
                    HttpServletResponse response) throws IOException {
        requireAdmin(authentication);
        String selectedRole = adminUserManagementService.normalizeRole(role);
        boolean stateRequired = adminUserManagementService.requiresState(selectedRole);
        if (stateRequired && stateId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Select a state before downloading the PDF.");
            return;
        }
        Long selectedStateId = stateRequired ? stateId : null;
        String stateName = selectedStateId == null ? "" : adminUserManagementService.stateName(selectedStateId);
        List<AdminManagedUserRow> users = adminUserManagementService.users(selectedRole, selectedStateId);
        AdminUserListPdfExporter.write(response, users, selectedRole, stateName);
    }

    @GetMapping("/admin-user-list/zip")
    public void zip(Authentication authentication, HttpServletResponse response) throws IOException {
        requireAdmin(authentication);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"All_Users.zip\"");
        try (ZipOutputStream zip = new ZipOutputStream(response.getOutputStream())) {
            for (UserPdfPart part : adminUserManagementService.allParts()) {
                byte[] pdf = AdminUserListPdfExporter.toBytes(part.getRows(), part.getRole(), part.getStateName());
                String entryName = part.getFolder() + "/" + AdminUserListPdfExporter.fileName(part.getRole(), part.getStateName());
                zip.putNextEntry(new ZipEntry(entryName));
                zip.write(pdf);
                zip.closeEntry();
            }
        }
    }

    private void requireAdmin(Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> Role.USER_ROLE_ADMIN.equals(authority.getAuthority()));
        if (!admin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
