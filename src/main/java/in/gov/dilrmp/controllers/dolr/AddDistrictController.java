package in.gov.dilrmp.controllers.dolr;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.http.converter.HttpMessageNotReadableException;

import in.gov.dilrmp.models.administrativeBoundry.District;
import in.gov.dilrmp.services.administrativeBoundry.AddDistrictException;
import in.gov.dilrmp.services.administrativeBoundry.AddDistrictService;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import lombok.Getter;
import lombok.Setter;

@Controller
@RequestMapping("/dolr/add-new-district")
public class AddDistrictController {

    private static final Logger logger = LoggerFactory.getLogger(AddDistrictController.class);

    @Autowired
    private StateService stateService;

    @Autowired
    private AddDistrictService addDistrictService;

    @GetMapping
    public String form(@RequestParam(value = "mode", defaultValue = "add") String mode, Model model) {
        model.addAttribute("states", stateService.findActiveStatesOrderByName());
        model.addAttribute("mode", "edit".equalsIgnoreCase(mode) ? "edit" : "add");
        return "pages/dolr/add-new-district :: add-new-district";
    }

    @GetMapping("/districts")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> districts(@RequestParam(value = "stateId", required = false) Long stateId) {
        try {
            return ResponseEntity.ok(addDistrictService.districtsForState(stateId));
        } catch (AddDistrictException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/district")
    @ResponseBody
    public ResponseEntity<?> district(@RequestParam(value = "id", required = false) Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body(error("Please select a district to edit."));
            }
            return ResponseEntity.ok(addDistrictService.districtForEdit(id));
        } catch (AddDistrictException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @GetMapping("/check-lgd")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkLgd(
            @RequestParam(value = "lgdCode", required = false) String lgdCode,
            @RequestParam(value = "districtId", required = false) Long districtId) {
        try {
            Integer code = parseLgdCode(lgdCode);
            String conflict = addDistrictService.lgdConflict(code, districtId).orElse(null);
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("exists", conflict != null);
            body.put("message", conflict == null ? "LGD Code is available." : conflict);
            return ResponseEntity.ok(body);
        } catch (AddDistrictException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@RequestBody AddDistrictRequest request) {
        try {
            District district = addDistrictService.createDistrict(
                    request.getStateId(), request.getDistrictName(), request.getLgdCode());
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", "success");
            body.put("districtName", district.getName());
            body.put("lgdCode", district.getLgdCode());
            String userName = district.getMuser() == null ? null : district.getMuser().getUserName();
            String password = district.getMuser() == null ? null : district.getMuser().getNewPassword();
            body.put("userName", userName);
            body.put("password", password);
            body.put("message", "District " + district.getName()
                    + " with LGD Code " + district.getLgdCode() + " has been added.");
            return ResponseEntity.ok(body);
        } catch (AddDistrictException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (DataIntegrityViolationException ex) {
            logger.warn("District insert rejected by the database: {}", ex.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(duplicateMessage(ex)));
        } catch (Exception ex) {
            logger.error("Failed to add a district", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("The district could not be added. Please try again."));
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(@RequestBody EditDistrictRequest request) {
        try {
            District district = addDistrictService.updateDistrict(
                    request.getId(), request.getStateId(), request.getDistrictName(), request.getLgdCode());
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("status", "success");
            body.put("id", district.getId());
            body.put("districtName", district.getName());
            body.put("lgdCode", district.getLgdCode());
            body.put("stateId", district.getState() == null ? null : district.getState().getId());
            body.put("userName", district.getMuser() == null ? null : district.getMuser().getUserName());
            body.put("message", "District " + district.getName()
                    + " with LGD Code " + district.getLgdCode() + " has been updated.");
            return ResponseEntity.ok(body);
        } catch (AddDistrictException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        } catch (DataIntegrityViolationException ex) {
            logger.warn("District update rejected by the database: {}", ex.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(duplicateMessage(ex)));
        } catch (Exception ex) {
            logger.error("Failed to update a district", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("The district could not be updated. Please try again."));
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(error("Enter a valid numeric LGD Code."));
    }

    private Integer parseLgdCode(String raw) {
        if (raw == null || !raw.matches("[0-9]{1,9}")) {
            throw new AddDistrictException("Enter a valid numeric LGD Code.");
        }
        try {
            int code = Integer.parseInt(raw);
            if (code <= 0) {
                throw new AddDistrictException("Enter a valid numeric LGD Code.");
            }
            return code;
        } catch (NumberFormatException ex) {
            throw new AddDistrictException("Enter a valid numeric LGD Code.");
        }
    }

    private String duplicateMessage(DataIntegrityViolationException ex) {
        String text = databaseDetail(ex);
        if (text.contains("lgd")) {
            return "LGD Code already exists. Enter a unique LGD Code.";
        }
        if (text.contains("user_name") || text.contains("username")) {
            return "A login with the generated username already exists. Use a different district name or LGD Code.";
        }
        return "This district could not be saved because a unique value is already in use.";
    }

    /**
     * PostgreSQL puts the column name in the server detail, not in the
     * constraint name. The existing LGD index is named with a generated hash,
     * so the column has to be read from the server error as well.
     */
    private String databaseDetail(Throwable ex) {
        StringBuilder all = new StringBuilder();
        Throwable current = ex;
        while (current != null) {
            if (current.getMessage() != null) {
                all.append(current.getMessage()).append('\n');
            }
            appendServerDetail(current, all);
            current = current.getCause();
        }
        return all.toString().toLowerCase();
    }

    private void appendServerDetail(Throwable current, StringBuilder all) {
        try {
            java.lang.reflect.Method serverError = current.getClass().getMethod("getServerErrorMessage");
            Object server = serverError.invoke(current);
            if (server == null) {
                return;
            }
            for (String methodName : new String[] {"getDetail", "getConstraint", "getColumn", "getTable"}) {
                java.lang.reflect.Method method = server.getClass().getMethod(methodName);
                Object value = method.invoke(server);
                if (value != null) {
                    all.append(value).append('\n');
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // Not a PostgreSQL exception.
        }
    }

    private Map<String, Object> error(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "error");
        body.put("message", message);
        return body;
    }

    @Getter
    @Setter
    public static class AddDistrictRequest {
        private Long stateId;
        private String districtName;
        private Integer lgdCode;
    }

    @Getter
    @Setter
    public static class EditDistrictRequest extends AddDistrictRequest {
        private Long id;
    }
}
