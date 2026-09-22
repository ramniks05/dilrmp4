$(document).ready(function () {
    $.ajax({
      url: '/chart/getStates',
      type: 'GET',
      success: function (data) {
       if (data && data.length > 0) {
          data.forEach(function (item) {
            $('#getState').append(
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

/*====================================================*/
$(document).ready(function() {
    let nakshaTable, groundTruthingTable, capacityBuildingTable;
    let allData = [];
    let stateSet = new Set();

    // Fetch all data once
    $.ajax({
        url: '/chart/unified-report', // Your backend endpoint
        method: 'GET',
        success: function(data) {
            allData = data;

            // Populate state filter dropdown
            data.forEach(d => stateSet.add(d.stateName));
            $('#stateFilter1').empty().append('<option value="">All States/UTs</option>');
            Array.from(stateSet).sort().forEach(s => {
                $('#stateFilter1').append(`<option value="${s}">${s}</option>`);
            });

            // Initial render (All States)
            renderTables("");
        },
        error: function(xhr) {
            alert('Failed to fetch data!');
        }
    });

    function renderTables(stateName) {
        let filtered = stateName ? allData.filter(d => d.stateName === stateName) : allData;

        let nakshaData = filtered.filter(d => d.entityType === "NakshaMIS");
        let groundData = filtered.filter(d => d.entityType === "GroundTruthing");
        let capacityData = filtered.filter(d => d.entityType === "CapacityBuilding");

        // ------------------- Naksha Table -------------------
        if ($.fn.DataTable.isDataTable('#nakshaTable')) {
            nakshaTable.clear().rows.add(nakshaData).draw();
        } else {
            nakshaTable = $('#nakshaTable').DataTable({
                data: nakshaData,
                columns: [
                    { data: null }, // S.No
                    { data: 'stateName' },
                    { data: 'spmuRecruitmentCompleted',
                      render: function(data) {
                          return `<span class="${getLabelClass(data)}">${data || '-'}</span>`;
                      }
                    },
                    { data: 'totalSPMUPositionsSanctioned' },
                    { data: 'totalProfessionalsRecruited' },
                    { data: 'teamsFormedForsanctioned' },
                    { data: 'teamsFormedForFieldSurvey' },
                    { data: 'roversSanctioned' },
                    { data: 'roversProcuredForFieldSurvey' },
                    { data: 'slcMeetingConducted' },
                    { data: 'slcMeetingDate' },
                    { data: 'legalFrameworkUrbanSurveyStatus'},

                    { data: 'legalFrameworkAmendmentStatus'}
                ],
                ordering: true,
                paging: false,
                searching: false,
                rowCallback: function(row, data, index) {
                    $('td:eq(0)', row).html(index + 1);
                }
            });
        }

        // ------------------- Ground Truthing Table -------------------
        if ($.fn.DataTable.isDataTable('#groundTruthingTable')) {
            groundTruthingTable.clear().rows.add(groundData).draw();
        } else {
            groundTruthingTable = $('#groundTruthingTable').DataTable({
                data: groundData,
                columns: [
                    { data: null }, // S.No
                    { data: 'stateName' },
                    { data: 'ulbName' },
                    { data: 'ulbFieldTeamsSanctioned' },
                    { data: 'ulbWiseFieldSurveyTeamsFormed' },
                    { data: 'surveyUnitNameOptions' },
                    { data: 'totalPlotsToSurvey' },
                    { data: 'plotsSurveyedCompleted' },
                    { data: 'surveyCompletionPercent' },
                    { data: 'totalUrProCardIssued' },
                    { data: 'claimsObjectionsReceived' },
                    { data: 'totalFinalUrProCardIssued' }
                ],
                ordering: true,
                paging: false,
                searching: false,
                rowCallback: function(row, data, index) {
                    $('td:eq(0)', row).html(index + 1);
                }
            });
        }

        // ------------------- Capacity Building Table -------------------
        if ($.fn.DataTable.isDataTable('#capacityBuildingTable')) {
            capacityBuildingTable.clear().rows.add(capacityData).draw();
        } else {
            capacityBuildingTable = $('#capacityBuildingTable').DataTable({
                data: capacityData,
                columns: [
                    { data: null }, // S.No
                    { data: 'stateName' },
                    { data: 'nameOfCoE' },
                    { data: 'nigstMasterTrainers' },
                    { data: 'coeMasterTrainers' },
                    { data: 'totalMasterTrainers' },
                    { data: 'fieldTeamsSanctioned' },
                     { data: 'fieldTeamsTrained' },
                    { data: 'membersTrained' },
                    { data: 'membersToBeTrained' },
                    { data: 'percentageTrained' },
                    { data: 'iecMaterialStatus' },
                    { data: 'iecMediaType' },
                    { data: 'iecMediaVariety' },
                    { data: 'iecActivityStatus' }
                ],
                ordering: true,
                paging: false,
                searching: false,
                rowCallback: function(row, data, index) {
                    $('td:eq(0)', row).html(index + 1);
                }
            });
        }
    }

    // State filter change
    $('#stateFilter1').on('change', function() {
        renderTables($(this).val());
    });
    // Table toggle functionality
            $('.table-toggle').on('click', function () {
                let targetTable = $(this).data('table');

                // Hide all containers
                $('#nakshaContainer, #groundTruthingContainer, #capacityBuildingContainer').hide();

                // Show the selected one
                if (targetTable === "nakshaTable") {
                    $('#nakshaContainer').show();
                } else if (targetTable === "groundTruthingTable") {
                    $('#groundTruthingContainer').show();
                } else if (targetTable === "capacityBuildingTable") {
                    $('#capacityBuildingContainer').show();
                }

                // Active button highlight
                $('.table-toggle').removeClass('active');
                $(this).addClass('active');
            });
});

// Label class function
function getLabelClass(status) {
    if (!status) return "label-default";
    switch (status) {
        case "Yes": return "label1 label-success";
        case "No": return "label1 label-danger";
        case "Under Process":
        case "Under Progress":
        case "Pending for Approval":
        case "Initiated": return "label1 label-warning";
        case "N/A":
        case "Not Started":
        case "Not Initiated": return "label1 label-danger";
        case "Adequate Provisions Available": return "label1 label-success";
        case "Amendments Required": return "label1 label-danger";
        default: return "label1 label-default";
    }
}








/*function render3DDonutChart(containerId, chartData, stateName) {
    // Define gradient pairs
    const gradientPairs = [
        ['#4099ff', '#73b4ff'], // Blue
        ['#FF5370', '#ff869a'], // Red
        ['#2ed8b6', '#59e0c5'], // Teal
        ['#FFB64D', '#ffcb80'], // Orange
        ['#FE8A7D', '#feb8b0'], // Coral
        ['#69CEC6', '#8fdbd5'], // Aqua
        ['#6f42c1', '#a074e8']  // Purple
    ];

    // Map gradients to chart data
    const gradientData = chartData.map((item, index) => {
        const gradient = gradientPairs[index % gradientPairs.length];
        return {
            name: item.name,
            y: item.y,
            color: {
                linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
                stops: [
                    [0, gradient[0]],
                    [1, gradient[1]]
                ]
            }
        };
    });

    Highcharts.chart(containerId, {
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0
            },
            backgroundColor: 'transparent'
        },
        title: {
            text: stateName + " - Component Summary"
        },
        tooltip: {
            useHTML: true,
            formatter: function () {
                return `<b>${this.point.name}</b>: <b>${this.point.y}</b>`;
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                depth: 45,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}: {point.y}',
                    style: { fontSize: '12px' }
                }
            }
        },
        series: [{
            name: 'Values',
            colorByPoint: true,
            data: gradientData
        }],
        responsive: {
            rules: [{
                condition: {
                    maxWidth: 600  // apply when screen width ≤ 600px
                },
                chartOptions: {
                    chart: {
                        height: 300
                    },
                    title: {
                        style: {
                            fontSize: '14px'
                        }
                    },
                    plotOptions: {
                        pie: {
                            dataLabels: {
                                style: {
                                    fontSize: '10px'
                                }
                            }
                        }
                    }
                }
            }]
        }
    });
}*/

/*$(document).ready(function () {
    // Initialize DataTable
    const dataTable = $('#stateDataTable').DataTable({
        paging: false,        // No pagination
        searching: false,      // Enable search
        ordering: true,       // Enable sorting
        order: [[1, 'asc']],  // Default sort by Component
        columnDefs: [
            { orderable: false, targets: 0 } // Disable sorting for S.No
        ],
        fixedHeader: false,    // Sticky header
        info: false           // Hide "Showing X to Y of Z entries"
    });

    // Load all states on page load
    loadTableData(null, "All States/UTs");

    // Dropdown change event
    $("#selectState").change(function () {
        const stateId = $(this).val();
        const stateName = $("#selectState option:selected").text();
        loadTableData(stateId === "all" ? null : stateId, stateName);
    });

    function loadTableData(stateId, stateName) {
        $.ajax({
            url: "/chart/state-mis-comparison",
            type: "GET",
            data: { stateId: stateId },
            success: function (response) {
                if (response.status === "success") {
                    // Clear existing table
                    dataTable.clear();

                    // Convert backend map into array for table
                    const dataArr = Object.entries(response.data).map(([key, value]) => [
                        null,                   // Placeholder for S.No
                        formatLabel(key),        // Component
                        value                     // Value
                    ]);

                    // Add rows to DataTable
                    dataTable.rows.add(dataArr).draw();

                    // Update S.No dynamically
                    dataTable.on('order.dt search.dt', function () {
                        dataTable.column(0, { search: 'applied', order: 'applied' }).nodes().each(function (cell, i) {
                            cell.innerHTML = i + 1;
                        });
                    }).draw();
                } else {
                    alert("No data found!");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading table data:", error);
            }
        });
    }

    // Format keys into user-friendly labels
  function formatLabel(key) {
      const labels = {
          ulbCount: "Total ULBs",
          teamsFormedForsanctioned: "Field Survey Teams Sanctioned",
          teamsFormedForFieldSurvey: "Field Survey Teams Formed",
          totalSPMUPositionsSanctioned: "SPMU  Positions Sanctioned",
          totalProfessionalsRecruited: "SPMU  Professionals Recruited ",
          roversSanctioned: "Rovers Sanctioned",
          roversProcuredForFieldSurvey: "Rovers Procured",


      };
      return labels[key] || key;
  }

});*/

$(document).ready(function () {
    // ✅ Sort dropdown options (ascending by text)
    let $select = $("#selectState");
    let $options = $select.find("option");

    // Exclude the first option ("All States/UTs") and sort the rest
    let $firstOption = $options.first();
    let $otherOptions = $options.slice(1);

    $otherOptions.sort(function (a, b) {
        return $(a).text().localeCompare($(b).text());
    });

    $select.empty().append($firstOption).append($otherOptions);

    // Initialize DataTable
    const dataTable = $('#stateDataTable').DataTable({
        paging: false,        // No pagination
        searching: true,      // Enable search
        ordering: true,       // Enable sorting
        order: [[1, 'asc']],  // Default sort by StateName
        columnDefs: [
            { orderable: false, targets: 0 } // Disable sorting for S.No
        ],
        fixedHeader: false,   // Sticky header
        info: false           // Hide "Showing X to Y of Z entries"
    });


    // Load all states on page load
    loadTableData(null, "All States/UTs");

    // Dropdown change event
    $("#selectState").change(function () {
        const stateId = $(this).val();
        const stateName = $("#selectState option:selected").text();
        loadTableData(stateId === "all" ? null : stateId, stateName);
    });

    function loadTableData(stateId, stateName) {
        $.ajax({
            url: "/chart/state-mis-comparison",
            type: "GET",
            data: { stateId: stateId },
            success: function (response) {
                if (response.status === "success") {
                    // ✅ Show total ULB count
                    $("#totalUlbDiv").text("Total ULBs: " + (response.totalUlb || 0));

                    // Clear existing table
                    dataTable.clear();

                    // ✅ response.details is a LIST of maps
                    const dataArr = response.details.map((row, index) => [
                        null,                               // Placeholder for S.No
                        row.stateName || "",
                        row.nodalDepartment || "",
                        row.nodalOfficer || "",
                        row.ulbCount || 0,                  // ULB Count

                        row.totalSPMU || 0,                 // SPMU Positions Sanctioned
                        row.professionals || 0,             // Professionals
                        row.teamsSanctioned || 0,           // Teams Sanctioned
                        row.teamsField || 0 ,
                        row.roversSanctioned || 0,          // Rovers Sanctioned
                        row.roversProcured || 0// Teams Field
                    ]);

                    // Add rows to DataTable
                    dataTable.rows.add(dataArr).draw();


                    // Update S.No dynamically
                    dataTable.on('order.dt search.dt', function () {
                        dataTable.column(0, { search: 'applied', order: 'applied' }).nodes()
                            .each(function (cell, i) {
                                cell.innerHTML = i + 1;
                            });
                    }).draw();

                } else {
                    $("#totalUlbDiv").text("Total ULBs: 0");
                    alert("No data found!");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading table data:", error);
            }
        });
    }
});




$(document).ready(function() {
    $('#btn-existing_rovers').click(function() {
        $.ajax({
            url: '/chart/state-with-existing-rovers',
            type: 'GET',
            success: function(response) {
                const labels = response.map(item => item.stateName);
                const data = response.map(item => item.roversProcuredForFieldSurvey);

                const ctx = document.getElementById('existingRoversChart').getContext('2d');

                new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Rovers Procured',
                            data: data,
                            backgroundColor: 'rgba(75, 192, 192, 0.7)',
                            borderColor: 'rgb(153, 102, 255)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: { display: false },
                        },
                        scales: {
                            y: {
                                beginAtZero: true
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
                                        ctx.font = 'bold 15px Arial';
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
            },
            error: function(xhr, status, error) {
                console.error("Error fetching existing rovers data:", error);
                alert("Failed to load chart data.");
            }
        });
    });
});














