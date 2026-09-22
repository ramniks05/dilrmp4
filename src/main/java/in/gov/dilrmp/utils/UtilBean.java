package in.gov.dilrmp.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilBean {
    private Long sroId;
    private Long tehsilId;
    private Long districtID;
    private String sronameName;
    private String districtName;
    private String message;
    private String tehsil;
}
