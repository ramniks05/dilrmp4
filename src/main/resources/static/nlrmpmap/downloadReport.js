
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
        document.getElementById('sroModernizationExcelUrl'),
        document.getElementById('legacyRevenueExcelUrl')
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

    function todayStamp() {
      const d = new Date();
      return String(d.getDate()).padStart(2, "0") + "-" + String(d.getMonth() + 1).padStart(2, "0") + "-" + d.getFullYear();
    }

    function zipFileName() {
      const select = document.getElementById("stateId");
      const scope = select.value === "00" ? "All_States_UTs"
        : select.options[select.selectedIndex].text.trim().replace(/[^A-Za-z0-9]+/g, "_").replace(/^_|_$/g, "");
      return "DILRMP_Physical_Progress_Reports_" + scope + "_" + todayStamp() + ".zip";
    }

    function loadJsZip() {
      if (window.JSZip) {
        return Promise.resolve(window.JSZip);
      }
      return new Promise((resolve, reject) => {
        const script = document.createElement("script");
        script.src = "/static/js/jszip.min.js";
        script.onload = () => window.JSZip ? resolve(window.JSZip) : reject(new Error("JSZip did not load"));
        script.onerror = reject;
        document.head.appendChild(script);
      });
    }

    function reportFileName(response, fallback) {
      const disposition = response.headers.get("Content-Disposition") || "";
      const match = disposition.match(/filename\*?=(?:UTF-8'')?"?([^";]+)"?/i);
      return (match ? decodeURIComponent(match[1]) : fallback).replace(/[\\/:*?"<>|]+/g, "_");
    }

    // Fetches every PDF/Excel shown in the table (same session-backed URLs as the individual buttons) and saves one ZIP.
    async function downloadAllReports() {
      const button = document.getElementById("downloadAllBtn");
      const label = document.getElementById("downloadAllLabel");
      const idleText = "Download all reports (ZIP)";
      const files = [];
      document.querySelectorAll("#data tbody tr").forEach(row => {
        if (row.style.display === "none") {
          return;
        }
        const name = (row.querySelector("td[id$='Label']") || {}).innerText || "Report";
        row.querySelectorAll("a[id$='Url']").forEach(link => {
          const href = link.getAttribute("href");
          if (!href || href === "#" || link.style.display === "none") {
            return;
          }
          const isExcel = /excel/i.test(link.id);
          files.push({ url: href, folder: isExcel ? "Excel" : "PDF", fallback: name.trim() + (isExcel ? ".xlsx" : ".pdf") });
        });
      });
      if (!files.length) {
        return;
      }

      const stateSelect = document.getElementById("stateId");
      button.disabled = true;
      stateSelect.disabled = true;
      const failed = [];
      try {
        const JSZip = await loadJsZip();
        const zip = new JSZip();
        for (let i = 0; i < files.length; i++) {
          label.innerText = "Preparing " + (i + 1) + "/" + files.length + "...";
          try {
            const response = await fetch(files[i].url, { credentials: "same-origin" });
            if (!response.ok) {
              throw new Error("HTTP " + response.status);
            }
            zip.folder(files[i].folder).file(reportFileName(response, files[i].fallback), await response.blob());
          } catch (e) {
            failed.push(files[i].fallback);
          }
        }
        if (failed.length === files.length) {
          alert("Could not download the reports. Please try again.");
          return;
        }
        if (failed.length) {
          zip.file("NOT_INCLUDED.txt", "These reports could not be generated:\r\n" + failed.join("\r\n"));
        }
        const blob = await zip.generateAsync({ type: "blob" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = zipFileName();
        document.body.appendChild(link);
        link.click();
        link.remove();
        setTimeout(() => URL.revokeObjectURL(link.href), 10000);
      } catch (e) {
        console.error("Download all failed", e);
        alert("Could not create the ZIP. Please try again.");
      } finally {
        label.innerText = idleText;
        button.disabled = false;
        stateSelect.disabled = false;
      }
    }
    function renumberReportRows() {
      let serial = 0;
      document.querySelectorAll("#data tbody tr").forEach(row => {
        const cell = row.querySelector("td.report-sno");
        if (cell && row.style.display !== "none") {
          cell.innerText = ++serial;
        }
      });
    }

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
            if (data.legacy_revenue) {
              document.getElementById("legacyRevenueLabel").innerText = data.legacy_revenue;
              document.getElementById("legacyRevenueUrl").href = data.prf_legacy_revenue_url;
              document.getElementById("legacyRevenueExcelUrl").href = data.excel_legacy_revenue_url;
              document.getElementById("legacyRevenueRow").style.display = "";
            } else {
              document.getElementById("legacyRevenueRow").style.display = "none";
            }

            renumberReportRows();

            // Show the table if there is data
            document.getElementById("data").style.display = "table";
            document.getElementById("downloadAllBar").style.display = "block";
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

