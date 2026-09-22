package in.gov.dilrmp.models.reportDTO.MapDigitizationReport;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "district_map_digitization_report_view")
public class DistrictMapDigitizationReport {


    @Id
    private Long districtId;
    private Integer districtLgdCode;
    private String districtName;
    private Long stateId;
    private String stateName;
    private Integer totalVillages;
    private Integer totalTehsils;
    private Integer totalCadastralMaps;
    private Integer digitizedCadastralMaps;

    //v5 Cadastral Maps report (S.No. [11.1], good condition — computed at read time)
    @Transient
    private Integer totalDamagedMissingMaps;
    @Transient
    private Integer mapsInGoodCondition;
    //v5 Digitized % vs maps in good condition
    @Transient
    private Double digitizedCadastralMapsPercentGoodCondition;

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

    @Transient
    private String  mapsUpdatePeriodDate;


}
