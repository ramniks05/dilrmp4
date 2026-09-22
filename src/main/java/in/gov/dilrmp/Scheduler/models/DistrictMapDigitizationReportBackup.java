package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
@Data
@Entity
public class DistrictMapDigitizationReportBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private LocalDate backupDate;
    private Integer districtId;
    private Integer districtLgdCode;
    private String districtName;
    private Long stateId;
    private String stateName;
    private Integer totalVillages;
    private Integer totalTehsils;
    private Integer totalCadastralMaps;
    private Integer digitizedCadastralMaps;
    private Integer totalFmbs;
    private Integer digitizedFmbs;
    private Integer totalTippans;
    private Integer digitizedTippans;
    private Integer villagesLinkedWithRor;
    private String mapsUpdatedBasedOnMutation;
    private Integer villagesGeoreferenced;
    private Integer georeferencedMaps;
    private Integer totalMapsFmbTippans;
    private Integer totalDigitizedMapsFmbTippans;
    private Integer totalLandParcels;
    private Integer georeferencedLandParcels;
    private Integer villagesWithUlipn;
    private Integer landParcelsWithUlipn;
    private Double digitizedCadastralMapsPercent;
    private Double digitizedFmbsPercent;
    private Double digitizedTippansPercent;
    private BigDecimal digitizedMapsFmbTippansPercent;
    private BigDecimal villagesLinkedWithRorPercent;
    private BigDecimal villagesGeoreferencedPercent;
    private Double georeferencedMapsPercent;
    private Double georeferencedLandParcelsPercent;
    private BigDecimal villagesWithUlipnPercent;
    private Double landParcelsWithUlipnPercent;
    private Date mapsUpdatePeriod;
}
