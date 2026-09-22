package in.gov.dilrmp.models.reportDTO.MapDigitizationReport;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "map_digitization_report_view")
public class MapDigitizationReport {
    @Id
    private Integer lgdCode;
    private String stateName;


    private Integer totalDistrict;
    @Transient
    private String formattingTotalDistrict;


    private Integer totalVillages;
    @Transient
    private String formattingTotalVillages;


    private Integer totalTehsils;
    @Transient
    private String formattingTotalTehsils;


    private Integer totalCadastralMaps;
    @Transient
    private String formattingtotalCadastralMaps;


    private Integer digitizedCadastralMaps;
    @Transient
    private String formattingdigitizedCadastralMaps;

    //v5 Cadastral Maps report (S.No. [11.1], good condition, display formatting)
    @Transient
    private Integer totalDamagedMissingMaps;
    @Transient
    private String formattingTotalDamagedMissingMaps;
    @Transient
    private Integer mapsInGoodCondition;
    @Transient
    private String formattingMapsInGoodCondition;
    //v5 Digitized % vs maps in good condition (denominator [11]-[11.1]); total-based % uses digitizedCadastralMapsPercent
    @Transient
    private Double digitizedCadastralMapsPercentGoodCondition;


    private Integer totalFmbs;
    @Transient
    private String formattingtotalFmbs;


    private Integer digitizedFmbs;
    @Transient
    private String formattingdigitizedFmbs;


    private Integer totalTippans;
    @Transient
    private String formattingtotalTippans;


    private Integer digitizedTippans;
    @Transient
    private String formattingdigitizedTippans;


    private Integer villagesLinkedWithRor;
    @Transient
    private String formattingvillagesLinkedWithRor;


    private String mapsUpdatedBasedOnMutation;
    @Transient
    private String formattingmapsUpdatedBasedOnMutation;


    @Transient
    private String mapsUpdatedBasedOnMutationPercentage;


    private Integer villagesGeoreferenced;
    @Transient
    private String formattingvillagesGeoreferenced;


    private Integer georeferencedMaps;
    @Transient
    private String formattinggeoreferencedMaps;


    private Integer totalMapsFmbTippans;
    @Transient
    private String formattingtotalMapsFmbTippans;


    private Integer totalDigitizedMapsFmbTippans;
    @Transient
    private String formattingtotalDigitizedMapsFmbTippans;


    private Integer totalLandParcels;
    @Transient
    private String formattingtotalLandParcels;


    private Integer georeferencedLandParcels;
    @Transient
    private String formattinggeoreferencedLandParcels;


    private Integer villagesWithUlipn;
    @Transient
    private String formattingvillagesWithUlipn;


    private Integer landParcelsWithUlipn;
    @Transient
    private String formattinglandParcelsWithUlipn;


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
