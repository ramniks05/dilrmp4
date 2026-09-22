package in.gov.dilrmp.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class ExcelExporterComponent {
 private String reportName;
 private int noOfheaderRows;
 private int noOfColumns;
 private String[] reportHeading;
 private String[] headerText;
 private String[] headerSpanMerged;
 private String[] headerSpanUnMerged;
 List<List<String>> reportDataList = new ArrayList<>();
}
