package in.gov.dilrmp.models.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashBordDTO {

    //National Profile & State Profile & District Profile Model........

    private Integer district_id;


    private Integer stateId;

    private String name;

    private String district_name;

    //Total Filed Name...........

    private Integer totalDistrict;

    private Integer totalTehsil;

    private Integer totalVillage;

    //CLR Filed Name..............
    private Integer villagesComputerizationCompleted;

    private BigDecimal villagesComputerizationCompletedPercent;

    private Integer rorComputerized;

    private BigDecimal rorComputerizedPercent;

    private Integer totalLandOwners;

    private Integer totalRor;

    private Integer rorBank;

    private BigDecimal rorBankPercent;

    //Map Digitalization Filed Name................

    private Integer totalMapsFmbTippans;

    private Integer totalDigitizedMapsFmbTippans;

    private Integer digitizedCadastralMaps;

    private BigDecimal digitizedMapsFmbTippansPercent;

    private Double digitizedCadastralMapsPercent;

    private Integer villagesWithUlipn;

    private BigDecimal villagesWithUlipnPercent;

    private Integer villagesLinkedWithRor;

    private BigDecimal villagesLinkedWithRoRPercent;

    private Integer villagesGeoreferenced;

    private BigDecimal villagesGeoreferencedPercent;

    //SRO Filed Name.....................

    private Integer totalSro;

    private Integer sroComputerized;

    private BigDecimal sroComputerizedPercent;

    private Integer sroLr;

    private BigDecimal sroLrPercent;


    //Survey Resurvey Filed Name.................

    private Integer villagesMap1Generated;

    private BigDecimal villagesMap1GeneratedPercent;

    private Integer villagesDroneFlyingCompleted;

    private BigDecimal villagesDroneFlyingCompletedPercent;

    private Double SurveyCompletedVillagePercent;

    //RCMS Filed Name...................

    private Integer totalRevenueCourts;

    private Integer revenueCourtsComputerized;

    private BigDecimal totalRevenueCourtsPercent;

    private Integer landRecordIntegration;

    private BigDecimal landRecordIntegrationPercent;

    private Integer sroIntegration;

    private BigDecimal sroIntegrationPercent;


    //AADHAR Filed Name................

    private Integer villagesWith100PercentRorLinkedAadhaar;

    private BigDecimal villagesWith100PercentRorLinkedAadhaarPercent;

    private Integer rorLinkedWithAadhaar;

    private BigDecimal rorLinkedWithAadhaarPercent;

    private Integer rorLinkedWithMobileNumber;

    private BigDecimal rorLinkedWithMobileNumberPercent;

    //MRR Filed Name.............................

    private Integer mrrSanctioned;

    private BigDecimal mrrCompletedOutOfTotalSanctionedPercent;

    private Double mrrCompletedOutOfTotalSanctionedPercentforDashboaerd;

    private Integer mrrCompleted;

    private BigDecimal mrrCompletedOutOfTotalTehsilsPercent;



/*============================== Sanjeev ===================================================================*/
    private String digitizedmapspercent1;

    private String cadastralmapslinkedtororpercent1;

    private String srocomputrizedpercent1;
    private String sroinreragllrpercent1;
    private String mrrcompletedpercent1;

    private String revenuecourtlinkedtehsils1;

    private String clrpercent1;

    private String clrcompletedaadharpercent1;
    private String banklinkagepercent1;

    private Integer platinum;

    private Integer gold;

    private Integer silver;

    private Integer rank;

    private String category;


    private String clrColor;


    private String digitizedMapsColor;


    private String sroComputrizedColor;


    private String sroInreragllrColor;


    private String clrcompletedaadharpercentColor;

    private String georefpercentColor;


    private String cadastralmapslinkedtororpercentColor;


    private String mrrcompletedpercentColor;


    private String surveypercentColor;

   /* ==============================================================*/

}
