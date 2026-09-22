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
public class PdfExporterComponent {

    private String[] reportHeading;
    private String reportName;
    private float col_width[];
    private String col_head[];
    Integer[] rowspan1;
    Integer[] rowspn2;
    Integer[] rowspn3;
    Integer[] rowspn4;
    Integer[] rowspn5;
    int columnBreakCountNo;
    // example 4 is index of coumn and 10 is no.of column whose wants span
    // colspanval.add(new ArrayList<Integer>(Arrays.asList(4, 10)));
    List<List<Integer>> columnspan;
    // example 5 is position of coumn and 10 is no.of partion like (5a,5b)
    // colspanval.add(new ArrayList<Integer>(Arrays.asList(5, 2)));
    List<List<Integer>> columnnumber;
    List<List<String>> reportDataList = new ArrayList<List<String>>();
    List<List<String>> grandTotal = new ArrayList<List<String>>();
   

}
