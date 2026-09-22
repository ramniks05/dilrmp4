
window.addEventListener("pageshow", function(event) {
    // Check if the event is persisted, meaning it's loaded from the cache (e.g., back/forward buttons)
   document.getElementById("stateId").selectedIndex = 0;
  });


function toggleExcelLinks() {
    const stateDropdown = document.getElementById('stateId');
    const selectedValue = stateDropdown.value;

    // Get all Excel links
    const excelLinks = [
        document.getElementById('clrExcelUrl'),
        document.getElementById('sroExcelUrl'),
        document.getElementById('surveyExcelUrl'),
        document.getElementById('mrrExcelUrl'),
        document.getElementById('mapExcelUrl'),
        document.getElementById('aadhaarExcelUrl'),
        document.getElementById('rcmExcelUrl'),
        document.getElementById('legacyExcelUrl'),
        document.getElementById('sroModernizationExcelUrl')
    ];

    // Show or hide links based on the selected value
    if (selectedValue === "00") {
        excelLinks.forEach(link => link.style.display = 'inline-block');
    } else {
        //excelLinks.forEach(link => link.style.display = 'none');
        excelLinks.forEach(link => link.style.display = 'inline-block');
    }
}


document.addEventListener("DOMContentLoaded", function () {
    const sroLabel = document.getElementById("sroLabel");
    const sroRow = document.getElementById("sroRow");

    // Check if sroLabel is null, undefined, or empty
    if (!sroLabel || !sroLabel.innerText.trim()) {
      sroRow.style.display = "none"; // Hide the entire row
    }
  });


   document.addEventListener("DOMContentLoaded", function() {
      function fetchReportData() {
        var stateSelect = document.getElementById("stateId");
        var dataTable = document.getElementById("data");

        // Show or hide table based on selected state
        if (stateSelect.value === "0") {
          dataTable.style.display = "none"; // Hide the table
        } else {
          dataTable.style.display = "table"; // Show the table
        }
      }

      // Initial check on page load
      fetchReportData();

      // Attach event handler to select dropdown
      document.getElementById("stateId").addEventListener("change", fetchReportData);
    });


    document.addEventListener("DOMContentLoaded", () => {
      // Set default value for stateId to "00"
      const stateIdElement = document.getElementById("stateId");
      stateIdElement.value = "00";

      // Call fetchReportData on page load
      fetchReportData();
      toggleExcelLinks();
    });

    function fetchReportData() {
      const stateId = document.getElementById("stateId").value;

      // Show or hide the 'sro' row based on selected state
      const sroRow = document.querySelector("tr[id='sro']");
      if (stateId === "00") {
        sroRow.style.display = ""; // Show the row
      } else {
        sroRow.style.display = "none"; // Hide the row
      }

      if (stateId && stateId !== "0") { // Ensure stateId is not the default value
        // Show the loader
        $("#wait").css("display", "block");

        fetch(`/reports/download-physical-progress-report?stateId=${stateId}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        })
        .then(response => response.json())
        .then(data => {
          // Hide the loader
          $("#wait").css("display", "none");

          // Clear the table before updating if there is data
          if (data.error) {
            dataTable.clear().draw();
          } else {
            // Update labels and URLs with response data
            document.getElementById("clrLabel").innerText = data.clr;
            document.getElementById("clrUrl").href = data.prf_clr_url;
            document.getElementById("clrExcelUrl").href = data.excel_clr_url;

            document.getElementById("mapLabel").innerText = data.map;
            document.getElementById("mapUrl").href = data.prf_map_url;
            document.getElementById("mapExcelUrl").href = data.excel_map_url;

            document.getElementById("mrrLabel").innerText = data.mrr;
            document.getElementById("mrrUrl").href = data.prf_mrr_url;
            document.getElementById("mrrExcelUrl").href = data.excel_mrr_url;

            document.getElementById("surveyLabel").innerText = data.survey_resurvey;
            document.getElementById("surveyUrl").href = data.prf_survey_url;
            document.getElementById("surveyExcelUrl").href = data.excel_survey_url;

            document.getElementById("rcmLabel").innerText = data.rcm;
            document.getElementById("rcmUrl").href = data.prf_rcm_url;
            document.getElementById("rcmExcelUrl").href = data.excel_rcm_url;

            document.getElementById("aadhaarLabel").innerText = data.aadhaar_linkage;
            document.getElementById("aadhaarUrl").href = data.prf_aadhaar_linkage_url;
            document.getElementById("aadhaarExcelUrl").href = data.excel_aadhaar_linkage_url;

            document.getElementById("sroLabel").innerText = data.sro;
            document.getElementById("sroUrl").href = data.prf_sro_url;
            document.getElementById("sroExcelUrl").href = data.excel_sro_url;

            document.getElementById("legacyLabel").innerText = data.legacy_digitization;
            document.getElementById("legacyUrl").href = data.prf_legacy_digitization_url;
            document.getElementById("legacyExcelUrl").href = data.excel_legacy_digitization_url;
            if (data.sro_modernization) {
              document.getElementById("sroModernizationLabel").innerText = data.sro_modernization;
              document.getElementById("sroModernizationUrl").href = data.prf_sro_modernization_url;
              document.getElementById("sroModernizationExcelUrl").href = data.excel_sro_modernization_url;
              document.getElementById("sroModernizationRow").style.display = "";
            } else {
              document.getElementById("sroModernizationRow").style.display = "none";
            }

            // Show the table if there is data
            document.getElementById("data").style.display = "table";
          }
        })
        .catch(error => {
          console.error('Error fetching report data:', error);
          // Hide the loader and clear the table in case of an error
          $("#wait").css("display", "none");
          dataTable.clear().draw();
        });
      } else {
        // Hide the table if no valid state is selected
        dataTable.clear().draw();
        document.getElementById("data").style.display = "none";
      }
    }

