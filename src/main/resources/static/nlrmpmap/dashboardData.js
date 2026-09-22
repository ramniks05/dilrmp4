// Helper function for Indian number format
function formatIndianNumber(num) {
    if (num === null || num === undefined) return '0';

        // Convert to string and split into integer and decimal parts if any
        const numParts = num.toString().split('.');
        let integerPart = numParts[0];
        const decimalPart = numParts.length > 1 ? '.' + numParts[1] : '';

        // Apply Indian format for integer part
        integerPart = integerPart.replace(/(\d)(?=(\d\d)+\d$)/g, "$1,");

        // Combine integer and decimal parts
        return integerPart + decimalPart;
}

 $(document).ready(function() {
        // AJAX call to fetch dashboard data
        $.ajax({
            url: '/getDashboardData',  // Replace with your actual endpoint
            method: 'GET',
             beforeSend: function () {
        $("#wait").css("display", "block");
            },
            success: function(data) {
            $("#wait").css("display", "none");
                // Populate the dashboard with the data
                   $('#rorComputerzed').text(formatIndianNumber(data.nationalDashBordDTOList.rorComputerized || '0'));
                          $('#rorComputerzedPercent').text(data.nationalDashBordDTOList.rorComputerizedPercent != null ? '(' + data.nationalDashBordDTOList.rorComputerizedPercent.toFixed(2) + '%)' : '0');

                          $('#digitizedCadustralMaps').text(formatIndianNumber(data.nationalDashBordDTOList.digitizedCadastralMaps || '0'));
                          $('#digitizedCadustralMapsPercent').text(data.nationalDashBordDTOList.digitizedCadastralMapsPercent != null ? '(' + data.nationalDashBordDTOList.digitizedCadastralMapsPercent.toFixed(2) + '%)' : '0');

                          $('#mrrCompleted').text(formatIndianNumber(data.nationalDashBordDTOList.mrrCompleted || '0'));
                          $('#mrrCompletedOutOfTotalSanctionedPercent').text(data.nationalDashBordDTOList.mrrCompletedOutOfTotalSanctionedPercentforDashboaerd != null ? '(' + data.nationalDashBordDTOList.mrrCompletedOutOfTotalSanctionedPercentforDashboaerd.toFixed(2) + '%)' : '0');

                          $('#villagesDroneFlyingCompleted').text(formatIndianNumber(data.nationalDashBordDTOList.villagesDroneFlyingCompleted || '0'));
                          $('#villagesDroneFlyingCompletedPercent').text(data.nationalDashBordDTOList.villagesDroneFlyingCompletedPercent != null ? '(' + data.nationalDashBordDTOList.villagesDroneFlyingCompletedPercent.toFixed(2) + '%)' : '0');

                          // New data fields with formatting
                          $('#revenueCourtsComputerized').text(formatIndianNumber(data.nationalDashBordDTOList.revenueCourtsComputerized || '0'));
                          $('#totalRevenueCourtsPercent').text(data.nationalDashBordDTOList.totalRevenueCourtsPercent != null ? '(' + data.nationalDashBordDTOList.totalRevenueCourtsPercent.toFixed(2) + '%)' : '0');

                          $('#villagesWith100PercentRorLinkedAadhaar').text(formatIndianNumber(data.nationalDashBordDTOList.villagesWith100PercentRorLinkedAadhaar || '0'));
                          $('#villagesWith100PercentRorLinkedAadhaarPercent').text(data.nationalDashBordDTOList.villagesWith100PercentRorLinkedAadhaarPercent != null ? '(' + data.nationalDashBordDTOList.villagesWith100PercentRorLinkedAadhaarPercent.toFixed(2) + '%)' : '0');

                          $('#totalSro').text(formatIndianNumber(data.nationalDashBordDTOList.totalSro || '0'));
                          $('#sroComputerizedPercent').text(data.nationalDashBordDTOList.sroComputerizedPercent != null ? '(' + data.nationalDashBordDTOList.sroComputerizedPercent.toFixed(2) + '%)' : '0');

                          $('#villagesGeoreferenced').text(formatIndianNumber(data.nationalDashBordDTOList.villagesGeoreferenced || '0'));
                          $('#villagesWithUlipn').text(formatIndianNumber(data.nationalDashBordDTOList.villagesWithUlipn || '0'));
                          $('#totalDistrict').text(formatIndianNumber(data.nationalDashBordDTOList.totalDistrict || '0'));
                          $('#totalTehsil').text(formatIndianNumber(data.nationalDashBordDTOList.totalTehsil || '0'));
                          $('#totalVillage').text(formatIndianNumber(data.nationalDashBordDTOList.totalVillage || '0'));
                          $('#totalState').text('36');


                        /*  $('#villagesGeoreferenced').text(formatIndianNumber(data.nationalDashBordDTOList.villagesGeoreferenced || '0'));
                          $('#villagesWithUlipn').text(formatIndianNumber(data.nationalDashBordDTOList.villagesWithUlipn || '0'));*/

                // Clear existing table rows
            $('#example5 tbody').empty();
            $('#example6 tbody').empty();

            // Populate top 5 states table (example5)
            if (data.top5States && data.top5States.length > 0) {
                data.top5States.forEach(function(stateDTO, index) {
                    var row = '<tr>' +
                        '<td>' + (index + 1) + '</td>' +
                        '<td>' + (stateDTO.name || 'N/A') + '</td>' +
                        '<td>' + (stateDTO.villagesComputerizationCompletedPercent != null ? stateDTO.villagesComputerizationCompletedPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '<td>' + (stateDTO.digitizedMapsFmbTippansPercent != null ? stateDTO.digitizedMapsFmbTippansPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '<td>' + (stateDTO.villagesLinkedWithRoRPercent != null ? stateDTO.villagesLinkedWithRoRPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '<td>' + (stateDTO.sroComputerizedPercent != null ? stateDTO.sroComputerizedPercent.toFixed(2) + '%' : '0') + '</td>' +
                        /*'<td>' + (stateDTO.sroLrPercent != null ? stateDTO.sroLrPercent.toFixed(2) + '%' : '0') + '</td>' +*/
                        '<td>' + (stateDTO.mrrCompletedOutOfTotalSanctionedPercent != null ? stateDTO.mrrCompletedOutOfTotalSanctionedPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '</tr>';
                    $('#example5 tbody').append(row);
                });
            } else {
                $('#example5 tbody').append('<tr><td colspan="8" class="text-center">No data available.</td></tr>');
            }

            // Populate bottom 5 states table (example6)
            if (data.bottom5States && data.bottom5States.length > 0) {
                data.bottom5States.forEach(function(stateDTO, index) {
                    var row = '<tr>' +
                        '<td>' + (index + 1) + '</td>' +
                        '<td>' + (stateDTO.name || 'N/A') + '</td>' +
                        '<td>' + (stateDTO.villagesComputerizationCompletedPercent != null ? stateDTO.villagesComputerizationCompletedPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '<td>' + (stateDTO.digitizedMapsFmbTippansPercent != null ? stateDTO.digitizedMapsFmbTippansPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '<td>' + (stateDTO.villagesLinkedWithRoRPercent != null ? stateDTO.villagesLinkedWithRoRPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '<td>' + (stateDTO.sroComputerizedPercent != null ? stateDTO.sroComputerizedPercent.toFixed(2) + '%' : '0') + '</td>' +
                       /* '<td>' + (stateDTO.sroLrPercent != null ? stateDTO.sroLrPercent.toFixed(2) + '%' : '0') + '</td>' +*/
                        '<td>' + (stateDTO.mrrCompletedOutOfTotalSanctionedPercent != null ? stateDTO.mrrCompletedOutOfTotalSanctionedPercent.toFixed(2) + '%' : '0') + '</td>' +
                        '</tr>';
                    $('#example6 tbody').append(row);
                });
            } else {
                $('#example6 tbody').append('<tr><td colspan="8" class="text-center">No data available.</td></tr>');
            }

// Get data from nationalDashBordDTOList
var top5States = data.top5States || [];
var bottom5States = data.bottom5States || [];

// Top5 States Data
var labelsTop5 = [];
var allDataTop5 = [];

top5States.forEach(function(stateDTO) {
    labelsTop5.push(stateDTO.name || 'N/A');
    allDataTop5.push([
        stateDTO.villagesComputerizationCompletedPercent || 0,
        stateDTO.digitizedMapsFmbTippansPercent || 0,
        stateDTO.villagesLinkedWithRoRPercent || 0,
        stateDTO.sroComputerizedPercent || 0,
        stateDTO.mrrCompletedOutOfTotalSanctionedPercent || 0
    ]);
});

// Bottom5 States Data
var labelsBottom5 = [];
var allDataBottom5 = [];

bottom5States.forEach(function(stateDTO) {
    labelsBottom5.push(stateDTO.name || 'N/A');
    allDataBottom5.push([
        stateDTO.villagesComputerizationCompletedPercent || 0,
        stateDTO.digitizedMapsFmbTippansPercent || 0,
        stateDTO.villagesLinkedWithRoRPercent || 0,
        stateDTO.sroComputerizedPercent || 0,
        stateDTO.mrrCompletedOutOfTotalSanctionedPercent || 0
    ]);
});

// Define colors and fields
var colors = ['#FF6384', '#36A2EB', '#FFCE56', '#9FE2BF', '#40E0D0'];
var fields = [
    'CLR Completed Villages(%)',
    'Digitized Mapsheets/FMBs/Tippans (%)',
    'Villages with Cadastral Maps linked With RoR (%)',
    'SRO Computerized (%)',
    'Completed Modern Record Room(%)'
];

// Top 5 States Bar Chart

Highcharts.chart('top5StatesBarChart', {
    chart: {
        type: 'bar'
    },
    title: {
        text: 'Top 5 States/UTs '
    },
    xAxis: {
        categories: labelsTop5
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Percentage'
        }
    },
    series: fields.map(function(field, index) {
        return {
            name: field,
            data: allDataTop5.map(function(stateData) {
                return stateData[index];
            }),
            color: colors[index]
        };
    }),
    tooltip: {
        pointFormat: '{point.y}%'
    },
    plotOptions: {
        bar: {
            dataLabels: {
                enabled: true
            }
        }
    }
});

// Bottom 5 States Bar Chart
Highcharts.chart('bottom5StatesBarChart', {
    chart: {
        type: 'bar'
    },
    title: {
        text: 'Bottom 5 States/UTs '
    },
    xAxis: {
        categories: labelsBottom5
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Percentage'
        }
    },
    series: fields.map(function(field, index) {
        return {
            name: field,
            data: allDataBottom5.map(function(stateData) {
                return stateData[index];
            }),
            color: colors[index]
        };
    }),
    tooltip: {
        pointFormat: '{point.y}%'
    },
    plotOptions: {
        bar: {
            dataLabels: {
                enabled: true
            }
        }
    }
});

// Top 5 States Line Chart
Highcharts.chart('top5StatesLineChart', {
    chart: {
        type: 'line'
    },
    title: {
        text: 'Top 5 States/UTs'
    },
    xAxis: {
        categories: labelsTop5
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Percentage'
        }
    },
    series: fields.map(function(field, index) {
        return {
            name: field,
            data: allDataTop5.map(function(stateData) {
                return stateData[index];
            }),
            color: colors[index],
            lineWidth: 2,
            marker: {
                enabled: true
            }
        };
    }),
    tooltip: {
        pointFormat: '{point.y}%'
    }
});

// Bottom 5 States Line Chart
Highcharts.chart('bottom5StatesLineChart', {
    chart: {
        type: 'line'
    },
    title: {
        text: 'Bottom 5 States/UTs'
    },
    xAxis: {
        categories: labelsBottom5
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Percentage'
        }
    },
    series: fields.map(function(field, index) {
        return {
            name: field,
            data: allDataBottom5.map(function(stateData) {
                return stateData[index];
            }),
            color: colors[index],
            lineWidth: 2,
            marker: {
                enabled: true
            }
        };
    }),
    tooltip: {
        pointFormat: '{point.y}%'
    }
});








 // ✅ Get Data from `nationalDashBordDTOList`
 let villagesGeoreferenced = data.nationalDashBordDTOList.villagesGeoreferenced || 0;
 let villagesWithUlipn = data.nationalDashBordDTOList.villagesWithUlipn || 0;
 let villages = data.nationalDashBordDTOList.totalVillage || 0;

 // ✅ Format Number in Indian Style
 function formatIndianNumber(num) {
     return num.toLocaleString('en-IN');
 }

 // ✅ Prepare Data for Highcharts
 let chartData = [
     {
         name: `Geo-Referenced (Villages) (${formatIndianNumber(villagesGeoreferenced)})`,
         y: villagesGeoreferenced,
         color: '#FF6384'
     },
     {
         name: `ULPIN (Villages) (${formatIndianNumber(villagesWithUlipn)})`,
         y: villagesWithUlipn,
         color: '#36A2EB'
     }
 ];

 // ✅ Highcharts Donut Chart with Center Text
 Highcharts.chart('villagesDonutChart', {
     chart: {
         type: 'pie',
         options3d: {
             enabled: true,
             alpha: 45
         },
         events: {
             render: function () {
                 let chart = this;
                 if (!chart.customLabel) {
                     chart.customLabel = chart.renderer.text(
                         `Total Villages   <br>${formatIndianNumber(villages)}</b>`,
                         chart.plotLeft + chart.plotWidth / 2 - 65, // X Position
                         chart.plotTop + chart.plotHeight / 2 // Y Position
                     )
                     .css({
                         color: '#333',
                         fontSize: '14px',
                         fontWeight: 'bold',
                         textAlign: 'center'
                     })
                     .add();
                 }
             }
         }
     },
     title: {
         text: 'Additional Key Performance Indicators'
     },
     plotOptions: {
         pie: {
             innerSize: '70%', // Create the donut hole
             depth: 45, // Give it a 3D effect
             dataLabels: {
                 enabled: true,
                 format: '{point.name}',
                 style: {
                     fontWeight: 'bold',
                     fontSize: '14px'
                 }
             }
         }
     },
     series: [{
         data: chartData
     }]
 });


 // Define chart labels and fields
 var fields = [
     'CLR Completed Villages(%)',
     'Digitized Mapsheets/FMBs/Tippans (%)',
     'Villages with Cadastral Maps linked With RoR (%)',
     'SRO Computerized (%)',
     'Completed Modern Record Room(%)'
 ];


// Data Fields with both Value and Percentage
var fields = [
    {name: 'RoR Computerized', value: data.nationalDashBordDTOList.rorComputerized || 0, percentage: data.nationalDashBordDTOList.rorComputerizedPercent || 0},
    {name: 'Digitized Cadastral Maps', value: data.nationalDashBordDTOList.digitizedCadastralMaps || 0, percentage: data.nationalDashBordDTOList.digitizedCadastralMapsPercent || 0},
    {name: 'MRR Completed (Out of Sanctioned)', value: data.nationalDashBordDTOList.mrrCompleted || 0, percentage: data.nationalDashBordDTOList.mrrCompletedOutOfTotalSanctionedPercentforDashboaerd || 0},
    {name: 'Survey/Resurvey Completed', value: data.nationalDashBordDTOList.villagesDroneFlyingCompleted || 0, percentage: data.nationalDashBordDTOList.villagesDroneFlyingCompletedPercent || 0},
    {name: 'Computerized/Online Revenue Courts', value: data.nationalDashBordDTOList.revenueCourtsComputerized || 0, percentage: data.nationalDashBordDTOList.totalRevenueCourtsPercent || 0},
    {name: 'Aadhaar Linked With RoR', value: data.nationalDashBordDTOList.villagesWith100PercentRorLinkedAadhaar || 0, percentage: data.nationalDashBordDTOList.villagesWith100PercentRorLinkedAadhaarPercent || 0},
    {name: 'SROs Computerized', value: data.nationalDashBordDTOList.totalSro || 0, percentage: data.nationalDashBordDTOList.sroComputerizedPercent || 0}
];

// Function to format numbers in Indian style (e.g., 35,75,26,763)
function formatIndianNumber(num) {
    return num.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",");
}

// Define colors for the pie chart slices (Optional)
var colors = ['#FF6384', '#36A2EB', '#FFCE56', '#9FE2BF', '#40E0D0', '#FF8C00', '#32CD32'];

// Create 3D Pie Chart
Highcharts.chart('physicalProgressChart', {
    chart: {
        type: 'pie',
        options3d: {
            enabled: true,
            alpha: 45, // angle of rotation for the 3D effect
            beta: 0,   // depth of 3D effect
            depth: 100 // depth of the pie chart slices
        }
    },
    title: {
        text: 'Physical Components Progress Report'
    },
    tooltip: {
        pointFormat: '<b>{point.name}:</b> {point.value} ({point.y:.2f}%)'
    },
    plotOptions: {
        pie: {
            innerSize: 100,  // Hollow center
            depth: 45,       // Depth of each slice
            dataLabels: {
                enabled: true,
                format: '{point.label}', // Custom label format
                style: {
                    fontSize: '14px',
                    fontWeight: 'bold',
                    color: '#333'
                }
            }
        }
    },
    series: [{
        name: 'Percentage',
        data: fields.map(function(field, index) {
            return {
                name: field.name,
                y: field.percentage, // Percentage value
                value: field.value,  // Actual value
                label: `<b>${formatIndianNumber(field.value)} (${field.percentage.toFixed(2)}%)</b> ${field.name}`, // Custom label format
                color: colors[index] // Use predefined colors
            };
        })
    }]
});





 // Function to format numbers in Indian style (e.g., 35,75,26,763)
        function formatIndianNumber(num) {
            return num.toString().replace(/\B(?=(\d{2})+(?!\d))/g, ",");
        }

        // Data Fields with Values Only (No Percentage)
        var totalDistrict = data.nationalDashBordDTOList.totalDistrict || 0;
        var totalTehsil = data.nationalDashBordDTOList.totalTehsil || 0;
        var totalVillage = data.nationalDashBordDTOList.totalVillage || 0;
        var totalState = 36; // Fixed Value

        // Pie Chart Data (Values Only, No Percentage)
        var stateChartData = [
            { name: 'Districts', value: totalDistrict },
            { name: 'Tehsils', value: totalTehsil },
            { name: 'Villages', value: totalVillage },
            { name: 'States/UTs', value: totalState }
        ];

        // Define colors for the pie chart slices
        var colors = ['#FF5733', '#40E0D0', '#9FE2BF', '#F3C300'];

        // Create 3D Pie Chart (Without Percentage)
        Highcharts.chart('containerState', {
            chart: {
                type: 'pie',
                options3d: {
                    enabled: true,
                    alpha: 45, // Rotation angle for 3D effect
                    beta: 0,
                    depth: 100
                }
            },
            title: {
                text: 'Master Details'
            },
            tooltip: {
                pointFormat: '<b>{point.name}:</b> {point.formattedValue}'
            },
            plotOptions: {
                pie: {
                    innerSize: 100,  // Hollow center
                    depth: 45,       // Depth of each slice
                    dataLabels: {
                        enabled: true,
                        format: '{point.label}', // Custom label format
                        style: {
                            fontSize: '14px',
                            fontWeight: 'bold',
                            color: '#333'
                        }
                    }
                }
            },
            series: [{
                name: 'Master Details',
                data: stateChartData.map(function(item, index) {
                    return {
                        name: item.name,
                        y: item.value, // Only the value (No Percentage)
                        formattedValue: formatIndianNumber(item.value), // Indian number format
                        label: `<b>${formatIndianNumber(item.value)}</b> ${item.name}`, // Custom label (Value Only)
                        color: colors[index] // Assign color to each slice
                    };
                })
            }]
        });

            },
            error: function(xhr, status, error) {
                $("#wait").css("display", "none");
                 console.error("Error fetching data: " + error);
                // Optionally, handle the error and display a message
            }
        });
    });

document.addEventListener("DOMContentLoaded", function () {
    let chartVillageLabel = document.getElementById('ChartVillage');
    let masterDetailsLabel = document.getElementById('masterDetails');
    let top5StatesLabel = document.getElementById('top5States');
    let bottom5StatesLabel = document.getElementById('bottom5States');
    let physicalProgressLabel = document.getElementById('physicalProgress');

    if (chartVillageLabel) {
        chartVillageLabel.addEventListener('click', function () {
            var additionalKeyModal = new bootstrap.Modal(document.getElementById('additionalKeyModal'));
            additionalKeyModal.show();
        });
    } else {
        console.error("Element with id 'ChartVillage' not found!");
    }

    if (masterDetailsLabel) {
        masterDetailsLabel.addEventListener('click', function () {
            var masterDetailsModal = new bootstrap.Modal(document.getElementById('masterDetailsModal'));
            masterDetailsModal.show();
        });
    } else {
        console.error("Element with id 'masterDetails' not found!");
    }

    if (top5StatesLabel) {
        top5StatesLabel.addEventListener('click', function () {
            var top5StatesModal = new bootstrap.Modal(document.getElementById('top5StatesModal'));
            top5StatesModal.show();
        });
    } else {
        console.error("Element with id 'top5States' not found!");
    }

    if (bottom5StatesLabel) {
        bottom5StatesLabel.addEventListener('click', function () {
            var bottom5StatesModal = new bootstrap.Modal(document.getElementById('bottom5StatesModal'));
            bottom5StatesModal.show();
        });
    } else {
        console.error("Element with id 'bottom5States' not found!");
    }

    if (physicalProgressLabel) {
        physicalProgressLabel.addEventListener('click', function () {
            var physicalProgressModal = new bootstrap.Modal(document.getElementById('physicalProgressModal'));
            physicalProgressModal.show();
        });
    } else {
        console.error("Element with id 'physicalProgress' not found!");
    }
});

