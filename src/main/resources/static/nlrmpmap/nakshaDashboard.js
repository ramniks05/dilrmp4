
$(document).ready(function () {

    // ✅ Call AJAX directly on page load
    $.ajax({
        url: "/chart/ground-truthing-detail-des",
        type: "GET",
        success: function (data) {
            let tbody = $("#ground-truthing-detail");
            tbody.empty(); // clear old rows

            // ✅ Handle null or empty list
            if (!data || data.length === 0) {
                tbody.append("<tr><td colspan='11' class='text-center text-warning'>No Ground Truthing Details Available</td></tr>");
                return;
            }

            // ✅ Build rows if data exists
            data.forEach((item, index) => {
                let row = `
                    <tr>
                        <td>${index + 1}</td>
                        <td class="state-col">${item.stateName || ''}</td>
                        <td>${item.ulbName || ''}</td>
                        <td>${item.ulbWiseFieldSurveyTeamsFormed || 0}</td>
                        <td>${item.surveyUnitNameOptions || 0}</td>
                        <td>${item.totalPlotsToSurvey || 0}</td>
                        <td>${item.plotsSurveyedCompleted || 0}</td>
                        <td>${item.surveyCompletionPercent != null ? item.surveyCompletionPercent.toFixed(2) : 0}</td>
                        <td>${item.totalUrProCardIssued || 0}</td>
                        <td>${item.claimsObjectionsReceived || 0}</td>
                        <td>${item.totalFinalUrProCardIssued || 0}</td>
                    </tr>
                `;
                tbody.append(row);
            });
        },
        error: function (xhr, status, error) {
            console.error("Error loading Ground Truthing data:", error);
            $("#ground-truthing-detail").html("<tr><td colspan='11' class='text-center text-danger'>Failed to load data</td></tr>");
        }
    });

    // ✅ Search filter by State/UT
    $("#searchGroundTruthingDetailState").on("keyup", function () {
        let searchText = $(this).val().toLowerCase();
        $("#ground-truthing-detail tr").filter(function () {
            let stateName = $(this).find(".state-col").text().toLowerCase();
            $(this).toggle(stateName.indexOf(searchText) > -1);
        });
    });
});


function fetchStateData(defaultUlb = "") {
    var state = document.getElementById("stateId").value;
    var gdDropdown = document.getElementById("ulbId");
    gdDropdown.innerHTML = '<option value="">Loading...</option>';

    if (state !== "") {
        fetch('/chart/ulb-names?stateName=' + encodeURIComponent(state))
            .then(response => response.json())
            .then(data => {
                gdDropdown.innerHTML = '<option value="">-- Select ULB Name --</option>';
                data.forEach(function (gd) {
                    let option = document.createElement("option");
                    option.value = gd;
                    option.text = gd;
                    gdDropdown.appendChild(option);
                });

                // Select default ULB if provided
                if (defaultUlb && data.includes(defaultUlb)) {
                    gdDropdown.value = defaultUlb;
                    handleUlbSelection(); // load chart
                } else {
                    clearAllCharts(); // clear chart if default not found
                }
            })
            .catch(error => {
                console.error("Error fetching ULB names:", error);
                gdDropdown.innerHTML = '<option value="">Error loading</option>';
                clearAllCharts();
            });
    } else {
        gdDropdown.innerHTML = '<option value="">-- Select ULB Name --</option>';
        clearAllCharts();
    }
}

function handleUlbSelection() {
    var ulbName = document.getElementById("ulbId").value;
    if (!ulbName) {
        clearAllCharts();
        return;
    }

    $.ajax({
        url: "/chart/ulb-data-chart",
        type: "GET",
        data: { ulbName: ulbName },
        dataType: "json",
        beforeSend: function () {
            $("#wait").show();
            clearAllCharts();
        },
        success: function (response) {
            console.log("Chart Data:", response);
            const colors = ['#FF6384', '#36A2EB', '#FFCE56', '#32CD32', '#9FE2BF', '#FF8C00', '#8A2BE2', '#00CED1', '#FF1493'];

            const chartIdMap = {
                "Cumulative Completed (Tech)": "techChart",
                "Grid Completion": "gridChart",
                "Data Acquisition (ORI)": "oriChart",
                "Data Acquisition (DEM/DSM/DTM)": "demChart",
                "Data Acquisition (3D Mesh Model)": "meshChart",
                "Data Acquisition (QA/QC)": "qaqcChart"
            };

            for (let section in response) {
                const divId = chartIdMap[section];
                if (!divId) continue;

                const dataMap = response[section];
                const categories = Object.keys(dataMap);
                const dataValues = Object.values(dataMap).map(v => {
                    const val = parseFloat(v.replace(/,/g, '')) || 0;
                    return val;
                });

                Highcharts.chart(divId, {
                    chart: { type: 'column' },
                    title: { text: section },
                    xAxis: {
                        categories: categories,
                        title: { text: null },
                        labels: { style: { fontSize: '13px' } }
                    },
                    yAxis: {
                        min: 0,
                        title: { text: 'Values', align: 'high' },
                        labels: { overflow: 'justify' }
                    },
                    tooltip: {
                        formatter: function () {
                            return `<b>${this.x}:</b> ${formatIndianNumber(this.y)}`;
                        }
                    },
                    plotOptions: {
                        column: {
                            colorByPoint: true,
                            dataLabels: {
                                enabled: true,
                                formatter: function () {
                                    return formatIndianNumber(this.y);
                                }
                            }
                        }
                    },
                    colors: colors,
                    series: [{ name: section, data: dataValues }]
                });
            }

            $("#wait").hide();
        },
        error: function (xhr, status, error) {
            console.error("Error fetching chart data:", error);
            $("#wait").hide();
            clearAllCharts();
        }
    });
}

function clearAllCharts() {
    $("#techChart, #gridChart, #oriChart, #demChart, #meshChart, #qaqcChart").empty();
}

function formatIndianNumber(num) {
    return Number(num).toLocaleString('en-IN');
}

// Document Ready
$(function () {
    // Load summary data
    $.get("/chart/naksha-vender")
        .done(function (data) {
            if (data.error) {

                return;
            }
            $("#distinctStates").text(data.distinctStates);
            $("#distinctUlbs").text(data.distinctUlbs);
            $("#bufferArea").text(data.totalBufferAreaDataAcquisition.toFixed(2) + ' (sq. km)');
            $("#sanctionedArea").text(data.totalSanctionedArea.toFixed(2) + ' (sq. km)');
        })
        .fail(function () {

            $("#distinctStates, #distinctUlbs, #bufferArea, #sanctionedArea").text("Error");
        });

    // Set default state and ulb, then load data
    $('#stateId').val("Maharashtra"); // Default state
    fetchStateData("Baramati");       // Default ULB
});



//===== Get State name and Id  =====================================================

$(document).ready(function () {
    $.ajax({
      url: '/chart/getStates',
      type: 'GET',
      success: function (data) {
       if (data && data.length > 0) {
          data.forEach(function (item) {
            $('#stateDropdown').append(
              $('<option>', {
                value: item.stateId,
                text: item.stateName
              })
            );
          });
        }
      },
      error: function (xhr, status, error) {
        console.error("Error loading states:", error);
      }
    });
  });
//==============================================================================

//=================GET STATE LIABLE DATA =========================================

document.addEventListener("DOMContentLoaded", function () {
    const pageSize = 5;
    let currentPage1 = 1;
    let currentPage2 = 1;
    let currentPage3 = 1;
    let currentPage4 = 1;

    // ---------- Table Rendering Functions ----------

let filteredData1 = []; // filtered data for table1

// Search logic
document.getElementById("searchState1").addEventListener("input", function () {
    const query = this.value.toLowerCase();
    filteredData1 = window.nakshaMISData.filter(entry =>
        entry.state_name && entry.state_name.toLowerCase().includes(query)
    );
    renderTable1(1); // always reset to page 1
});

function renderTable1(page) {
    if (!window.nakshaMISData) return;
    const tbody = document.getElementById("table1-body");
    tbody.innerHTML = "";

    // if no search applied, use all data
    const dataToRender = filteredData1.length ? filteredData1 : window.nakshaMISData;

    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    const pageData = dataToRender.slice(start, end);

    pageData.forEach((entry, index) => {
        const row = document.createElement("tr");

        let labelClass1 = getLabelClass(entry.spmuRecruitmentCompleted);
        let labelClass2 = getLabelClass(entry.legalFrameworkAmendmentStatus);

        row.innerHTML = `
            <td>${start + index + 1}</td>
            <td>${entry.state_name || ""}</td>
            <td><span class="${labelClass1}">${entry.spmuRecruitmentCompleted || ""}</span></td>
            <td>${entry.totalSPMUPositionsSanctioned ?? 0}</td>
            <td>${entry.totalProfessionalsRecruited ?? 0}</td>
            <td>${entry.teamsFormedForsanctioned ?? 0}</td>
            <td>${entry.teamsFormedForFieldSurvey ?? 0}</td>
            <td>${entry.roversSanctioned ?? 0}</td>
            <td>${entry.roversProcuredForFieldSurvey ?? 0}</td>
            <td>${entry.slcMeetingConducted ?? ""}</td>
            <td>${entry.slcMeetingDate || ""}</td>
            <td>${entry.legalFrameworkUrbanSurveyStatus || ""}</td>
            <td><span class="${labelClass2}">${entry.legalFrameworkAmendmentStatus || ""}</span></td>
        `;
        tbody.appendChild(row);
    });

    renderPagination("pagination1", dataToRender.length, page, (newPage) => {
        currentPage1 = newPage;
        renderTable1(newPage);
    });
}





    /*function renderTable2(page) {
        if (!window.nakshaMISData) return;
        const tbody = document.getElementById("table2-body");
        tbody.innerHTML = "";

        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        const pageData = window.nakshaMISData.slice(start, end);

        pageData.forEach((entry, index) => {
            const row = document.createElement("tr");
            let labelClass = getLabelClass(entry.spmuRecruitmentCompleted);

            row.innerHTML = `
                <td>${start + index + 1}</td>
                <td>${entry.state_name}</td>
                <td><span class="${labelClass}">${entry.spmuRecruitmentCompleted}</span></td>
                <td>${entry.totalSPMUPositionsSanctioned}</td>
                <td>${entry.totalProfessionalsRecruited}</td>
                <td>${entry.teamsFormedForsanctioned}</td>
                <td>${entry.teamsFormedForFieldSurvey}</td>
                <td>${entry.roversSanctioned}</td>
                <td>${entry.roversProcuredForFieldSurvey}</td>
            `;
            tbody.appendChild(row);
        });

        renderPagination("pagination2", window.nakshaMISData.length, page, (newPage) => {
            currentPage2 = newPage;
            renderTable2(newPage);
        });
    }*/
/*
    function renderTable3(page) {
        if (!window.nakshaMISData) return;
        const tbody = document.getElementById("table3-body");
        tbody.innerHTML = "";

        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        const pageData = window.nakshaMISData.slice(start, end);

        pageData.forEach((entry) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${entry.state_name}</td>
                <td>${entry.teamsFormedForsanctioned}</td>
                <td>${entry.teamsFormedForFieldSurvey}</td>
            `;
            tbody.appendChild(row);
        });

        renderPagination("pagination3", window.nakshaMISData.length, page, (newPage) => {
            currentPage3 = newPage;
            renderTable3(newPage);
        });
    }*/

   /* function renderTable4(page) {
        if (!window.nakshaMISData) return;
        const tbody = document.getElementById("table4-body");
        tbody.innerHTML = "";

        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        const pageData = window.nakshaMISData.slice(start, end);

        pageData.forEach((entry) => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${entry.state_name}</td>
                <td>${entry.roversSanctioned}</td>
                <td>${entry.roversProcuredForFieldSurvey}</td>
            `;
            tbody.appendChild(row);
        });

        renderPagination("pagination4", window.nakshaMISData.length, page, (newPage) => {
            currentPage4 = newPage;
            renderTable4(newPage);
        });
    }*/

   function getLabelClass(status) {
       if (!status) return "label1";
       switch (status) {
           case "Yes": return "label1 label-success";
           case "No": return "label1 label-danger";
           case "Under Process":
           case "Under Progress":
           case "Initiated": return "label1 label-warning";
           case "N/A":
           case "Not Started":
           case "Not Initiated": return "label1 label-danger";
           case "Adequate Provisions Available": return "label1 label-success";
           case "Amendments Required": return "label1 label-danger";
           default: return "label1";
       }
   }
   document.getElementById("fullscreenBtn").addEventListener("click", function () {
       const container = document.getElementById("fullscreenContainer");
       if (!document.fullscreenElement) {
           container.requestFullscreen().catch(err => {

           });
           this.innerHTML = '<i class="fa fa-compress"></i>'; // change icon
       } else {
           document.exitFullscreen();
           this.innerHTML = '<i class="fa fa-expand"></i>'; // reset icon
       }
   });

    // ---------- Pagination Function ----------
    function renderPagination(containerId, totalItems, currentPage, onPageClick) {
        const totalPages = Math.ceil(totalItems / pageSize);
        const pagination = document.getElementById(containerId);
        if (!pagination) return;
        pagination.innerHTML = "";

        for (let i = 1; i <= totalPages; i++) {
            const li = document.createElement("li");
            li.className = `page-item ${i === currentPage ? 'active' : ''}`;
            li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
            li.addEventListener("click", function (e) {
                e.preventDefault();
                onPageClick(i);
            });
            pagination.appendChild(li);
        }
    }

    // ---------- Fetch Naksha MIS Report ----------
    function fetchNakshaMISReport() {
        fetch("/chart/naksha-state-mis")
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => { throw err; });
                }
                return response.json();
            })
            .then(json => {
                if (json.status === "success" && json.data) {
                    window.nakshaMISData = json.data;

                    // Render all tables
                    renderTable1(currentPage1);

                } else {
                    console.warn("No data found for Naksha MIS report");
                    const errorDiv = document.getElementById("error-message");
                    if (errorDiv) errorDiv.textContent = json.message || "No data found for Naksha MIS report.";
                }
            })
            .catch(error => {
                console.error("Error fetching Naksha MIS report:", error);
                const errorDiv = document.getElementById("error-message");
                if (errorDiv) errorDiv.textContent = error?.message || error?.error || "An unknown error occurred.";
            });
    }

    // ---------- Initial Call ----------
    fetchNakshaMISReport();

});




//===============================================================================


$(document).ready(function () {
  // Initial AJAX to load summary & charts
  $.ajax({
    url: "/chart/rovers-summary",
    method: "GET",
    beforeSend: function () {
            $("#wait").css("display", "block");
                },
    success: function (data) {
      $("#wait").css("display", "none");
      $('#totalStates').text(data.totalStates);
      $('#totalSPMUPositionsSanctioned').text(data.totalSPMUPositionsSanctioned);
      $('#totalProfessionalsRecruited').text(data.totalProfessionalsRecruited);
      $('#teamsFormedForsanctioned').text(data.teamsFormedForsanctioned);
      $('#teamsFormedForFieldSurvey').text(data.teamsFormedForFieldSurvey);
      $('#roversSanctioned').text(data.totalRoversSanctioned);
      $('#roversProcured').text(data.totalRoversProcured);
      $('#totalUlb').text(data.totalUlb);

      const labels = [
        'SPMU Sanctioned Positions',
        'SPMU Recruited Professionals',
        'Teams to be Formed as Sanctioned',
        'Teams Formed for Field Survey',
        'Rovers Sanctioned',
        'Rovers Procured',
        'ULB'
      ];

      const values = [
        data.totalSPMUPositionsSanctioned,
        data.totalProfessionalsRecruited,
        data.teamsFormedForsanctioned,
        data.teamsFormedForFieldSurvey,
        data.totalRoversSanctioned,
        data.totalRoversProcured,
        data.totalUlb
      ];

     const backgroundColors = [
       'rgba(255, 99, 132, 0.8)',   // dark red
       'rgba(255, 159, 64, 0.8)',   // dark orange
       'rgba(255, 205, 86, 0.8)',   // darker yellow
       'rgba(75, 192, 192, 0.8)',   // teal
       'rgba(54, 162, 235, 0.8)',   // deep sky blue
       'rgba(153, 102, 255, 0.8)',   // dark purple
       'rgba(100, 149, 237, 0.8)'   // dark purple
     ];


      const borderColors = [
        'rgb(180, 30, 50)',
        'rgb(200, 90, 30)',
        'rgb(180, 140, 40)',
        'rgb(20, 110, 110)',
        'rgb(20, 90, 180)',
        'rgb(80, 40, 180)',
        'rgb(65, 105, 225)'
      ];


      renderCombinedChart('barChart', labels, values, values, backgroundColors, borderColors);
      renderDonutChart('donutChart1', labels.slice(0, 2), values.slice(0, 2), backgroundColors.slice(0, 2), borderColors.slice(0, 2));
      renderDonutChart('donutChart2', labels.slice(2, 4), values.slice(2, 4), backgroundColors.slice(2, 4), borderColors.slice(2, 4));
      renderDonutChart('donutChart3', labels.slice(4, 6), values.slice(4, 6), backgroundColors.slice(4, 6), borderColors.slice(4, 6));
    },
    error: function () {
     $("#wait").css("display", "none");
      $('#roversSummary').html("<p style='color: red;'>Failed to load summary data.</p>");
    }
  });

  function renderCombinedChart(containerId, labels, barData, lineData, bgColor, borderColor) {
    new Chart(document.getElementById(containerId), {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            type: 'bar',
            label: 'Bar',
            data: barData,
            backgroundColor: bgColor,
            borderColor: borderColor,
            borderWidth: 1
          },
          {
            type: 'line',
            label: 'Line',
            data: lineData,
            borderColor: 'black',
            backgroundColor: 'transparent',
            tension: 0.4,
            pointBackgroundColor: 'black'
          }
        ]
      },
      options: {
          responsive: true,
          maintainAspectRatio: false, // 💡 important for modal responsiveness
          plugins: {
              tooltip: {
                  mode: 'index',
                  intersect: false
              },
              legend: {
                  position: 'bottom'
              }
          },
          scales: {
              x: { stacked: true },
              y: {
                  stacked: true,
                  beginAtZero: true,
                  title: {
                      display: true,
                      text: 'Number'
                  }
              }
          }
      }

    });
  }
//------------------------------------------------------------------------------------------------
  function renderDonutChart(containerId, labels, data, bgColor, borderColor) {
    new Chart(document.getElementById(containerId), {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          label: '',
          data: data,
          backgroundColor: bgColor,
          borderColor: borderColor,
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        cutout: '50%'
      }
    });
  }

  const metricColors = {
    totalSPMUPositionsSanctioned: 'rgba(255, 206, 86, 0.7)',
    totalProfessionalsRecruited: 'rgba(75, 192, 192, 0.7)',
    teamsFormedForsanctioned: 'rgba(153, 102, 255, 0.7)',
    teamsFormedForFieldSurvey: 'rgba(255, 159, 64, 0.7)',
    roversSanctioned: 'rgba(255, 99, 132, 0.7)',
        roversProcured: 'rgba(54, 162, 235, 0.7)'
  };

  let barChart = null;

  const metricLabels = {

    totalSPMUPositionsSanctioned: 'SPMU Positions Sanctioned',
    totalProfessionalsRecruited: 'Professionals Recruited For SPMU',
    teamsFormedForsanctioned: 'Teams Formed For Sanctioned',
    teamsFormedForFieldSurvey: 'Teams Formed For Field Survey',
    roversSanctioned: 'Rovers Sanctioned',
    roversProcured: 'Rovers Procured'
  };

function fetchAndRenderChart(metric) {
  fetch(`/chart/state-rovers-summary/all?metric=${metric}`)
    .then(res => res.json())
    .then(data => {
      const categories = data.map(item => item.stateName);
      const values = data.map(item => item[metric]);

      const readableLabel = metricLabels[metric] || metric;

      document.getElementById('metricTitle').textContent = readableLabel;

      const myModal = new bootstrap.Modal(document.getElementById('metricModal'));
      myModal.show();

      Highcharts.chart('metricBarChart', {
        chart: {
          type: 'column',   // ✅ Only column chart
          zoomType: 'xy'
        },
        title: {
          text: null
        },
        xAxis: {
          categories: categories,
          crosshair: true
        },
        yAxis: {
          min: 0,
          title: {
            text: readableLabel
          }
        },
        tooltip: {
          pointFormat: '<b>{point.y}</b>'
        },
        legend: {
          enabled: false   // ✅ Hide legend (since only 1 series)
        },
        series: [{
          name: readableLabel,
          data: values,
          colorByPoint: true,
          dataLabels: {
            enabled: true,       // ✅ show values
            inside: false,       // ✅ above bar (outside)
            format: '{point.y}'  // ✅ number formatting
          }
        }],
        credits: {
          enabled: false
        }
      });
    })
    .catch(err => console.error('Error fetching chart data:', err));
}






  $('.clickable-metric').on('click', function () {
    const metric = $(this).data('metric');

    fetchAndRenderChart(metric);

  });
});



//====================================================================================


function fetchAndRenderChart1(metric1, metric2, canvasId) {
    fetch(`/chart/state-compare?metric1=${metric1}&metric2=${metric2}`)
        .then(res => res.json())
        .then(data => {
            const states = data.map(d => d.stateName);
            const metric1Data = data.map(d => d[metric1]);
            const metric2Data = data.map(d => d[metric2]);

            renderCombinedChart1(canvasId, states, metric1Data, metric2Data, metric1, metric2);
            $('#metricComModal').modal('show'); // Show modal after rendering chart
        })
        .catch(err => {
            console.error('Error loading chart data:', err);
        });
}




const labelMap = {

    totalSPMUPositionsSanctioned: 'SPMU Professionals Sanctioned',
    totalProfessionalsRecruited: 'SPMU Professionals Recruited',
    teamsFormedForsanctioned: 'Teams for Field Survey - Sanctioned',
    teamsFormedForFieldSurvey: 'Teams for Field Survey - Formed',
    roversSanctioned: 'Rovers Sanctioned',
    roversProcuredForFieldSurvey: 'Rovers Procured'
};

const chartInstances = {}; // Map to store chart instances per canvasId

function renderCombinedChart1(canvasId, labels, dataset1, dataset2, label1, label2) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    const displayLabel1 = labelMap[label1] || label1;
    const displayLabel2 = labelMap[label2] || label2;

    if (chartInstances[canvasId]) {
        chartInstances[canvasId].destroy();
    }

    // ✅ Define color palette
    const colors = [
        'rgba(54, 162, 235, 0.7)',   // Blue
        'rgba(255, 99, 132, 0.7)',   // Red
        'rgba(255, 206, 86, 0.7)',   // Yellow
        'rgba(75, 192, 192, 0.7)',   // Teal
        'rgba(153, 102, 255, 0.7)',  // Purple
        'rgba(255, 159, 64, 0.7)'    // Orange
    ];

    chartInstances[canvasId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: displayLabel1,
                    data: dataset1,
                    backgroundColor: colors[0],
                    stack: 'Stack 0'
                },
                {
                    label: displayLabel2,
                    data: dataset2,
                    backgroundColor: colors[1],
                    stack: 'Stack 0'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                tooltip: {
                    mode: 'index',
                    intersect: false
                },
                legend: {
                    display: true,
                    labels: {
                        font: {
                            weight: 'bold',  // ✅ Bold legend labels
                            size: 15
                        }
                    }
                }
            },
            interaction: {
                mode: 'index',
                intersect: false
            },
            scales: {
                x: {
                    stacked: true,
                    ticks: {
                        autoSkip: false,
                        maxRotation: 45,
                        minRotation: 30,
                        font: {
                            size: 11,
                            weight: 'bold' // ✅ Bold x-axis labels
                        }
                    }
                },
                y: {
                    stacked: true,
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Quantity',
                        font: {
                            weight: 'bold'
                        }
                    },
                    ticks: {
                        font: {
                            weight: 'bold' // ✅ Bold y-axis numbers
                        }
                    }
                }
            }
        },
        plugins: [{
            id: 'bar-value-label',
            afterDatasetsDraw(chart) {
                const { ctx } = chart;

                chart.data.datasets.forEach((dataset, i) => {
                    const meta = chart.getDatasetMeta(i);
                    meta.data.forEach((bar, index) => {
                        const value = dataset.data[index];
                        if (value !== 0 && value != null) {
                            ctx.save();
                            ctx.fillStyle = '#000000';
                            ctx.font = ' 15px Arial';  // ✅ Bold inside bar values
                            ctx.textAlign = 'center';
                            ctx.textBaseline = 'middle';
                            ctx.fillText(value, bar.x, bar.y + bar.height / 4);
                            ctx.restore();
                        }
                    });
                });
            }
        }]
    });
}













$(document).ready(function () {
    $('.clickable-metric1').on('click', function () {
         const metric1 = $(this).data('metric1');
            const metric2 = $(this).data('metric2');
            const title = $(this).data('title');
            $('#metricComModalLabel').text(title);
           const canvasId = 'ComparisonByMetricID';

        fetchAndRenderChart1(metric1, metric2, canvasId);
    });
});

//====================GET ALL State ==========================


document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".clickable-metric2").forEach(element => {
        element.addEventListener("click", function () {
            const metric = this.getAttribute("data-metric");
            if (metric === "allStateComparison") {
                $('#stateComparisonModal').modal('show'); // 🔓 Open Modal
                fetchAllStateComparison();                 // 📊 Load Chart
            }
        });
    });
function sortAndRenderChart(selectedMetric) {
    if (!Array.isArray(globalChartData)) {
        console.error("globalChartData is not an array:", globalChartData);
         return;
    }

    if (!selectedMetric || typeof selectedMetric !== "string") {
        console.error("Invalid selectedMetric:", selectedMetric);
        return;
    }

    const sortedData = [...globalChartData].sort((a, b) => {
        const aVal = a[selectedMetric] ?? 0;
        const bVal = b[selectedMetric] ?? 0;
        return bVal - aVal;
    });

    renderStackedBarChart(sortedData);
}
function generateMetricRadioButtons() {
    const container = document.getElementById("metricRadioButtons");
    container.innerHTML = '';

    Object.keys(metricMap).forEach((metricKey, index) => {
        const div = document.createElement('div');
        div.className = "form-check mr-3";

        const input = document.createElement('input');
        input.type = 'radio';
        input.name = 'metricSelector';
        input.value = metricKey;
        input.className = 'form-check-input';
        input.id = `radio-${metricKey}`;
        if (index === 0) input.checked = true; // default check first

        input.addEventListener('change', () => {
            sortAndRenderChart(metricKey);
        });

        const label = document.createElement('label');
        label.className = 'form-check-label';
        label.setAttribute('for', input.id);
        label.textContent = metricMap[metricKey].label;

        div.appendChild(input);
        div.appendChild(label);
        container.appendChild(div);
    });
}



/*=================ground-truthing-detail data======================*/
$(document).ready(function () {

  // 🔹 Load States Dropdown
  $.ajax({
    url: '/chart/getStates',
    type: 'GET',
    dataType: 'json',
    success: function (data) {
      const $dropdown = $('#getGroundTruthingState');
      $dropdown.empty();
      $dropdown.append('<option value="">-- All States/UTs --</option>');

      if (data && data.length > 0) {
        const seen = new Set();

        data.forEach(function (item) {
          let stateName = item.stateName.replace('(UT)', '').trim();

          if (!seen.has(stateName.toLowerCase())) {
            seen.add(stateName.toLowerCase());
            $dropdown.append(
              $('<option>', {
                value: stateName, // ✅ stateName used for match
                text: stateName
              })
            );
          }
        });
      } else {
        $dropdown.append('<option disabled>No states available</option>');
      }
    },
    error: function (xhr, status, error) {
      console.error("Error loading states:", error);
      $('#getGroundTruthingState').append('<option disabled>Error loading states</option>');
    }
  });


  // 🔹 Load Table Data
  let table;
  let allData = [];

  // ✅ Define custom sorting for surveyType
  $.fn.dataTable.ext.type.order['surveyType-asc'] = function (a, b) {
    const rank = value => {
      if (value === 'Area') return 1;
      if (value === 'Property') return 2;
      if (!value || value.trim() === '') return 3;
      return 4;
    };
    return rank(a) - rank(b);
  };

  $.fn.dataTable.ext.type.order['surveyType-desc'] = function (a, b) {
    const rank = value => {
      if (value === 'Area') return 2;
      if (value === 'Property') return 1;
      if (!value || value.trim() === '') return 3;
      return 4;
    };
    return rank(a) - rank(b);
  };


  // ✅ Fetch Data and Initialize DataTable
  $.ajax({
    url: '/chart/ground-truthing-dashboard-data',
    type: 'GET',
    dataType: 'json',
    success: function (data) {
      allData = data;

      table = $('#groundTruthingDatailsTable').DataTable({
        data: data,
        columns: [
          { data: null },
          { data: 'stateName' },
          { data: 'ulbName' },
          { data: 'groundTruthingCommencementDate', render: formatDate },
          { data: 'groundTruthingCompletionDate', render: formatDate },
          { data: 'fieldTeamsSanctioned' },
          { data: 'fieldSurveyTeamsFormed' },
          { data: 'surveyUnitName' },
          { data: 'surveyType' },
          { data: 'totalAreaToBeSurveyed' },
          { data: 'areaSurveyedTillDate' },
          { data: 'areaWiseSurveyCompletionPercent' },
          { data: 'totalPropertiesForFieldSurvey' },
          { data: 'propertiesSurveyedTillDate' },
          { data: 'propertyWiseSurveyCompletionPercent' },
          { data: 'totalSurveyUnits' },
          { data: 'surveyUnitsSurveyed' },
          { data: 'surveyUnitsPending' },
          { data: 'surveyUnitWiseCompletionPercent' },
          { data: 'workValidationStatus' },
          { data: 'draftUrProCardIssues' },
          { data: 'claimsAndObjectionsReceived' },
          { data: 'claimsAndObjectionsResolved' },
          { data: 'claimsResolutionPercent' },
          { data: 'claimsPending' },
          { data: 'claimsPendingPercent' },
          { data: 'finalUrProCardIssued' }
        ],
        order: [[8, 'asc']],
        paging: false,
        language: {
          emptyTable: "No data available in table"
        },
        columnDefs: [
          {
            orderable: false,
            targets: 0,
            render: (data, type, row, meta) => meta.row + 1
          },
          {
            type: 'surveyType',
            targets: 8
          }
        ]
      });

      // Reindex serial numbers dynamically
      table.on('order.dt search.dt draw.dt', function () {
        table.column(0, { search: 'applied', order: 'applied' })
          .nodes()
          .each((cell, i) => (cell.innerHTML = i + 1));
      }).draw();
    },
    error: function () {
      alert('Error fetching Ground Truthing Dashboard data.');
    }
  });


  // 🔹 Filter table based on selected state name
  $('#getGroundTruthingState').on('change', function () {
    const selectedState = $(this).val();

    const filteredData =
      selectedState === ''
        ? allData
        : allData.filter(item =>
            item.stateName.replace('(UT)', '').trim().toLowerCase() ===
            selectedState.toLowerCase()
          );

    table.clear().rows.add(filteredData).draw();
  });


  // 🔹 Format Date Helper
  function formatDate(data) {
    if (!data) return '';
    const date = new Date(data);
    return date.toLocaleDateString('en-GB', {
      day: 'numeric', month: 'long', year: 'numeric'
    });
  }
});





/*============================================================================*/

 function fetchAllStateComparison() {
     fetch("/chart/naksha-all-state-compare")
         .then(response => {
             if (!response.ok) {
                 return response.json().then(err => { throw err; });
             }
             return response.json();
         })
         .then(data => {
             globalChartData = data;

             // Step 1: Sort and render chart first using default metric
             const defaultMetric = "roversSanctioned";
             sortAndRenderChart(defaultMetric);

             // Step 2: After chart is shown, generate radio buttons
             generateMetricRadioButtons();

         })
         .catch(error => {
             console.error("Error fetching state comparison data:", error);

         });
 }




   // 🔁 MOVE THIS OUTSIDE DOMContentLoaded
   function renderStackedBarChart(data) {
       const labels = data.map(d => d.stateName);
       const roversSanctioned = data.map(d => d.roversSanctioned || 0);
       const roversProcured = data.map(d => d.roversProcured || 0);
       const totalSPMUPositionsSanctioned = data.map(d => d.totalSPMUPositionsSanctioned || 0);
       const totalProfessionalsRecruited = data.map(d => d.totalProfessionalsRecruited || 0);
       const teamsFormedForsanctioned = data.map(d => d.teamsFormedForsanctioned || 0);
       const teamsFormedForFieldSurvey = data.map(d => d.teamsFormedForFieldSurvey || 0);

       const canvas = document.getElementById('stateComparisonChart');
       if (!canvas) {

           return;
       }

       const ctx = canvas.getContext('2d');
       if (window.stateComparisonChart && typeof window.stateComparisonChart.destroy === 'function') {
           window.stateComparisonChart.destroy();
       }

       window.stateComparisonChart = new Chart(ctx, {
           type: 'bar',
           data: {
               labels: labels,
               datasets: [
                   {
                       label: 'Rovers Sanctioned',
                       data: roversSanctioned,
                       backgroundColor: 'rgba(255, 99, 132, 0.7)'
                   },
                   {
                       label: 'Rovers Procured',
                       data: roversProcured,
                       backgroundColor: 'rgba(54, 162, 235, 0.7)'
                   },
                   {
                       label: 'SPMU Positions Sanctioned',
                       data: totalSPMUPositionsSanctioned,
                       backgroundColor: 'rgba(255, 206, 86, 0.7)'
                   },
                   {
                       label: 'Professionals Recruited',
                       data: totalProfessionalsRecruited,
                       backgroundColor: 'rgba(75, 192, 192, 0.7)'
                   },
                   {
                       label: 'Teams Formed for Sanctioned',
                       data: teamsFormedForsanctioned,
                       backgroundColor: 'rgba(153, 102, 255, 0.7)'
                   },
                   {
                       label: 'Teams Formed for Field Survey',
                       data: teamsFormedForFieldSurvey,
                       backgroundColor: 'rgba(255, 159, 64, 0.7)'
                   }
               ]
           },
           options: {
               responsive: true,
               maintainAspectRatio: false, // 💡 important for modal responsiveness
               plugins: {
                   tooltip: {
                       mode: 'index',
                       intersect: false
                   },
                   legend: {
                       position: 'bottom'
                   }
               },
               scales: {
                   x: { stacked: true },
                   y: {
                       stacked: true,
                       beginAtZero: true,
                       title: {
                           display: true,
                           text: 'Number'
                       }
                   }
               }
           }

       });
   }


});

const metricMap = {
    roversSanctioned: { label: 'Rovers Sanctioned', color: 'rgba(255, 99, 132, 0.7)' },
    roversProcured: { label: 'Rovers Procured', color: 'rgba(54, 162, 235, 0.7)' },
    totalSPMUPositionsSanctioned: { label: 'SPMU Positions Sanctioned', color: 'rgba(255, 206, 86, 0.7)' },
    totalProfessionalsRecruited: { label: 'Professionals Recruited', color: 'rgba(75, 192, 192, 0.7)' },
    teamsFormedForsanctioned: { label: 'Teams Formed for Sanctioned', color: 'rgba(153, 102, 255, 0.7)' },
    teamsFormedForFieldSurvey: { label: 'Teams Formed for Field Survey', color: 'rgba(255, 159, 64, 0.7)' }
};





document.addEventListener("DOMContentLoaded", () => {
    fetchAllStateComparison();
});

//============================================================================

function hideMetricSection() {
    document.getElementById('metricSection').style.display = 'none';
}
function hideMetricComparison() {
    document.getElementById('metricComparison').style.display = 'none';
}
function hideMetricSections() {
    document.getElementById('metricSections').style.display = 'none';
}

function hideMetricUlb() {
    document.getElementById('ulbCountChartSection').style.display = 'none';
}

/*============Get All State/UT======================================*/



$(document).ready(function () {
    $('.clickable-metric-state').on('click', function () {
        const metric = $(this).data('metric');

        if (metric === 'stateCount') {
            $.ajax({
                url: "/chart/states",   // ✅ Controller URL
                type: "GET",
                success: function (response) {
                    var tbody = $("#stateTable tbody");
                    tbody.empty(); // Clear old data

                    if (!response || response.length === 0) {
                        tbody.append("<tr><td colspan='2'>No states found</td></tr>");
                    } else {
                        $.each(response, function (index, stateName) {
                            var row = "<tr><td>" + (index + 1) + "</td><td>" + stateName + "</td></tr>";
                            tbody.append(row);
                        });
                    }

                    // ✅ Show modal after table is populated
                    $('#stateModal').modal('show');
                },
                error: function () {
                    alert("Error fetching state names.");
                }
            });
        }
    });
});


//===== ULB COUNT BY GD  Bar Chart ======================================================
$(document).ready(function () {
    $('.clickable-metric-ulb').on('click', function () {
        const metric = $(this).data('metric');

        if (metric === 'ulbCountByGd') {
            $.ajax({
                url: "/chart/ulb-detail",
                type: "GET",
                success: function (response) {
                    var tbody = $("#ulbTable tbody");
                    var stateDropdown = $("#stateFilter");

                    tbody.empty();
                    stateDropdown.empty().append('<option value="">-- All States/UTs --</option>');

                    if (!response || response.length === 0) {
                        tbody.append("<tr><td colspan='3'>No ULBs found</td></tr>");
                    } else {
                        // ✅ Populate table
                        $.each(response, function (index, item) {
                            var row = "<tr>" +
                                "<td class='row-index'>" + (index + 1) + "</td>" +
                                "<td class='state-col'>" + item.stateName + "</td>" +
                                "<td>" + item.ulbName + "</td>" +
                                "<td>" + item.population + "</td>" +
                                "<td>" + item.fieldSurveyArea + "</td>" +
                                "<td>" + item.aerialSurveyArea + "</td>" +
                                "<td>" + item.bufferGridAreaForFlying + "</td>" +
                                "</tr>";
                            tbody.append(row);
                        });

                        // ✅ Populate dropdown with unique state names
                       // ✅ Populate dropdown with unique state names
                       let uniqueStates = [...new Set(response.map(item => item.stateName))];

                       // 🔽 Sort alphabetically (A → Z)
                       uniqueStates.sort((a, b) => a.toLowerCase().localeCompare(b.toLowerCase()));

                       $.each(uniqueStates, function (i, state) {
                           stateDropdown.append('<option value="' + state + '">' + state + '</option>');
                       });

                    }
                   $("#ulbCountLabel").text("Total ULBs: " + response.length);
                    // Show modal
                    $('#ulbModal').modal('show');
                },
                error: function () {
                    alert("Error fetching ULBs.");
                }
            });
        }
    });

    // 🔽 Filter by dropdown selection & re-index
  // 🔽 Filter by dropdown selection & re-index
  $(document).on("change", "#stateFilter", function () {
      var selectedState = $(this).val().toLowerCase();
      var tbodyRows = $("#ulbTable tbody tr");
      var counter = 1;
      var visibleCount = 0;

      tbodyRows.each(function () {
          var stateText = $(this).find(".state-col").text().toLowerCase();

          if (selectedState === "" || stateText === selectedState) {
              $(this).show();
              $(this).find(".row-index").text(counter++);  // ✅ reset numbering
               visibleCount++;
          } else {
              $(this).hide();
          }
           $("#ulbCountLabel").text("Total ULBs: " + visibleCount);
      });

      // ✅ Reapply Bootstrap striping manually
      $("#ulbDetails tbody tr:visible").each(function (i) {
          $(this).removeClass("table-primary table-secondary"); // clear old
          if (i % 2 === 0) {
              $(this).addClass("table-light");   // even rows
          } else {
              $(this).removeClass("table-light"); // keep striped look clean
          }
      });
  });

});







/*$(document).ready(function () {
    $('.clickable-metric-ulb').on('click', function () {
        const metric = $(this).data('metric');

        if (metric === 'ulbCountByGd') {
            $.ajax({
                url: '/chart/ulb-count-by-gd',
                method: 'GET',
                success: function (data) {
                    if (!data || data.length === 0) {
                        $('#ulbCountChart').html('<div class="text-center text-danger">Data not available</div>');
                    } else {
                        const gdNames = data.map(item => item.gdName);
                        const counts = data.map(item => item.ulbCount);
                        renderULBHighChart(gdNames, counts);
                    }
                    $('#ulbChartModal').modal('show'); // Show modal
                },
                error: function () {
                    $('#ulbCountChart').html('<div class="text-center text-danger">Failed to load ULB count data.</div>');
                    $('#ulbChartModal').modal('show');
                }
            });
        }
    });
});

function renderULBHighChart(categories, data) {
    Highcharts.chart('ulbCountChart', {
        chart: {
            type: 'column'
        },
        title: {
            text: 'Number of ULBs per GD'
        },
        xAxis: {
            categories: categories,
            title: {
                text: 'GD'
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Number of ULBs'
            }
        },
        tooltip: {
            pointFormat: '<b>{point.y} ULB(s)</b>'
        },
        series: [{
            name: 'ULBs',
            data: data,
            colorByPoint: true
        }],
        credits: {
            enabled: false
        }
    });
}*/








function enableSortableHeaders() {
    const getCellValue = (tr, idx) => tr.children[idx]?.innerText || "";

    const comparer = (idx, asc) => (a, b) => {
        const v1 = getCellValue(asc ? a : b, idx);
        const v2 = getCellValue(asc ? b : a, idx);

        const f1 = parseFloat(v1);
        const f2 = parseFloat(v2);
        if (!isNaN(f1) && !isNaN(f2)) return f1 - f2;
        return v1.localeCompare(v2);
    };

    document.querySelectorAll(".sortable").forEach(th => {
        let asc = true;
        th.style.cursor = "pointer";

        th.addEventListener("click", () => {
            const table = th.closest("table");
            const tbody = table.querySelector("tbody");
            const index = parseInt(th.getAttribute("data-column"));
            const rows = Array.from(tbody.querySelectorAll("tr"));

            rows.sort(comparer(index, asc));
            asc = !asc;

            rows.forEach(row => tbody.appendChild(row));
        });
    });
}



function hideULBCountChart() {
    $('#ulbCountChartSection').hide();
}






document.querySelector('#ulbInfoModal .btn-danger').addEventListener('click', function() {
  const modal = bootstrap.Modal.getInstance(document.getElementById('ulbInfoModal'));
  modal.hide();
});



/*===============================================================================================*/














