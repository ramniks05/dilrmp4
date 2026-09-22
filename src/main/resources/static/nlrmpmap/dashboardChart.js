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

           // Call fetchStateData() after dashboard data is loaded
            fetchStateData();

// Get data from nationalDashBordDTOList
var top5States = data.top5States || [];
var bottom5States = data.bottom5States || [];
var statesDataList = data.stateDashboardDataList || [];

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
// All State Data
var allStateData = [];
var labelStateData = [];
statesDataList.forEach(function(stateDTO) {
    labelStateData.push(stateDTO.name || 'N/A');
     allStateData.push([
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

/// Bottom 5 States Bar Chart
 Highcharts.chart('bottom5StatesLineChart', {
     chart: {
         type: 'bar'  // Changed from 'line' to 'bar'
     },
     title: {
         text: 'Bottom 5 States/UTs'
     },
     xAxis: {
         categories: labelsBottom5,
         labels: {
             style: {
                 fontWeight: 'bold',  // Bold labels
                 fontSize: '14px'
             }
         }
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
     }
 });


// All States Line Chart
Highcharts.chart('allStatesLineChart', {
    chart: {
        type: 'line'
    },
    title: {
        text: 'All States/UTs'
    },
    xAxis: {
        categories: labelStateData

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
            data: allStateData.map(function(stateData) {
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

var colors = ['#FF6384', '#36A2EB', '#FFCE56', '#9FE2BF', '#40E0D0'];
var field = [
    'CLR Completed Villages (%)',
    'Digitized Mapsheets/FMBs/Tippans (%)',
    'Villages with Cadastral Maps linked With RoR (%)',
    'SRO Computerized (%)',
    'Completed Modern Record Room (%)'
];

function renderChart(selectedIndex) {
    var selectedField = field[selectedIndex];
    var selectedColor = colors[selectedIndex]; // Assign color based on index

    console.log("Selected Index:", selectedIndex);
    console.log("Selected Field:", selectedField);
    console.log("Selected Color:", selectedColor);

    // Combine state labels and data for sorting
    var combinedData = labelStateData.map((label, index) => ({
        label: label,
        value: allStateData[index][selectedIndex] // Get the selected component's data
    }));

    // Sort in descending order
    combinedData.sort((a, b) => b.value - a.value);

    // Extract sorted labels and values
    var sortedLabels = combinedData.map(item => item.label);
    var sortedValues = combinedData.map(item => item.value);

    Highcharts.chart('allStatesColumnChart', {
        chart: {
            type: 'column',
            options3d: {  // Enable 3D
                enabled: true,
                alpha: 15,  // Tilt angle
                beta: 15,   // Rotation angle
                depth: 50,  // Depth effect
                viewDistance: 25 // Adjust view distance
            }
        },
        title: {
            text: selectedField
        },
        xAxis: {
            categories: sortedLabels,
            labels: {
                style: {
                    fontWeight: 'bold',
                    fontSize: '12px'
                }
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Percentage'
            }
        },
        plotOptions: {
            column: {
                depth: 25, // Thickness of bars
                dataLabels: {
                    enabled: true,
                    format: '{y:.2f}%' // Display percentage with two decimal places
                }
            }
        },
        series: [{
            name: selectedField,
            data: sortedValues,
            color: selectedColor // Assign color dynamically
        }],
        tooltip: {
            pointFormat: '<b>{series.name}:</b> {point.y:.2f}%'
        }
    });
}

// Initial chart load
renderChart(0);

// Change chart on dropdown selection
$("#fieldSelector").change(function() {
    var selectedIndex = parseInt($(this).val());
    renderChart(selectedIndex);
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

 // Define the colors for each field (optional)
 var colors = ['#FF6384', '#36A2EB', '#FFCE56', '#9FE2BF', '#40E0D0'];

// Top 5 States Pie Charts by Field
fields.forEach(function(field, fieldIndex) {
    Highcharts.chart('top5StatesPieChart_' + fieldIndex, {
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0,
                depth: 100
            }
        },
        title: {
             text:  field
        },
        tooltip: {
            // Customize the tooltip to display actual value for each state
            pointFormat: '{point.name}: {point.y}%'
        },
        plotOptions: {
            pie: {
                innerSize: 100,
                depth: 45,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}: {point.y}%'
                }
            }
        },
        series: [{
            name: 'Percentage',
            data: labelsTop5.map(function(stateName, index) {
                var value = allDataTop5[index][fieldIndex] || 0;
                return {
                    name: stateName,
                    y: value // Directly use the actual percentage value from allDataTop5
                };
            }),
            colors: colors // Use predefined colors
        }]
    });
});

// Bottom 5 States Pie Charts by Field
fields.forEach(function(field, fieldIndex) {
    Highcharts.chart('bottom5StatesPieChart_' + fieldIndex, {
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0,
                depth: 100
            }
        },
        title: {
            text:  field
        },
        tooltip: {
            // Customize the tooltip to display actual value for each state
            pointFormat: '{point.name}: {point.y}%'
        },
        plotOptions: {
            pie: {
                innerSize: 100,
                depth: 45,
                dataLabels: {
                    enabled: true,
                    format: '{point.name}: {point.y}%'
                }
            }
        },
        series: [{
            name: 'Percentage',
            data: labelsBottom5.map(function(stateName, index) {
                var value = allDataBottom5[index][fieldIndex] || 0;
                return {
                    name: stateName,
                    y: value // Directly use the actual percentage value from allDataBottom5
                };
            }),
            colors: colors // Use predefined colors
        }]
    });
});

// Assuming you have the necessary data from `data.nationalDashBordDTOList`

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
Highcharts.chart('container', {
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
                text: 'Geographical Data Distribution'
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
                name: 'Geographical Data',
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

function fetchStateData() {
    var stateId = document.getElementById("stateId").value;
    $.ajax({
        url: "/getStateData",
        type: "GET",
        data: { stateId: stateId },
        dataType: "json", // Ensure response is parsed as JSON
        beforeSend: function() {
            $("#wait").css("display", "block"); // Show loader before request
        },
        success: function(response) {
            console.log("Response Data:", response); // Debugging

            // Ensure response is a valid array
            if (!Array.isArray(response) || response.length === 0) {
                console.error("Invalid or empty data format received:", response);
                $("#wait").css("display", "none");
                return;
            }

            // Assume the first item in the response contains the relevant data (if multiple items)
            var stateData = response[0]; // Adjust depending on how data is structured in the response
            var stateName=stateData.name;
            // Data Fields with both Value and Percentage (Access fields directly from the response)
      var fields = [
          {name: 'RoR Computerized', value: stateData.rorComputerized || 0, percentage: Number((stateData.rorComputerizedPercent || 0).toFixed(2))},
          {name: 'Digitized Mapsheets/FMBs/Tippans', value: stateData.totalDigitizedMapsFmbTippans || 0, percentage: Number((stateData.digitizedMapsFmbTippansPercent || 0).toFixed(2))},
          {name: 'MRR Completed (Out of Sanctioned)', value: stateData.mrrSanctioned || 0, percentage: Number((stateData.mrrCompletedOutOfTotalSanctionedPercent || 0).toFixed(2))},
          {name: 'Survey/Resurvey Completed',
              value: stateData.villagesDroneFlyingCompleted || 0,
              percentage: stateData.totalVillage && stateData.totalVillage > 0
                  ? Number((stateData.villagesDroneFlyingCompleted * 100 / stateData.totalVillage).toFixed(2))
                  : 0},
          {name: 'Computerized/Online Revenue Courts', value: stateData.revenueCourtsComputerized || 0, percentage: Number((stateData.totalRevenueCourtsPercent || 0).toFixed(2))},
          {name: 'Aadhaar Linked With RoR', value: stateData.villagesWith100PercentRorLinkedAadhaar || 0, percentage: Number((stateData.villagesWith100PercentRorLinkedAadhaarPercent || 0).toFixed(2))},
          {name: 'SROs Computerized', value: stateData.totalSro || 0, percentage: Number((stateData.sroComputerizedPercent || 0).toFixed(2))}
      ];
      // Sort in descending order based on percentage
      fields.sort((a, b) => b.percentage - a.percentage);

            // Define colors for the column bars (Optional)
            var colors = ['#FF6384', '#36A2EB', '#FFCE56', '#9FE2BF', '#40E0D0', '#FF8C00', '#32CD32'];



           // Create Column Bar Chart
           Highcharts.chart('stateColumnChart', {
               chart: {
                   type: 'column'
               },
               title: {
                   text: 'Physical Components Progress Report ( '+stateName+' )'
               },
             xAxis: {
                 categories: fields.map(function(field) {
                     return `<b>${field.name}</b>`; // Make text bold
                 }),
                 labels: {
                     useHTML: true,  // Enable HTML formatting
                     style: {
                        fontSize: '12px',    // Adjust font size
                        fontFamily: 'Arial, sans-serif', // Set font family
                        color: '#333'        // Set text color
                     }
                 }
             },
               yAxis: {
                   min: 0,
                   title: {
                       text: 'Percentage'
                   }
               },
              pointFormat: '<b>{point.name}:</b> {formattedValue} ({point.y:.2f}%)',
              tooltip: {
                  formatter: function () {
                      return `<b>${this.point.name}:</b> ${formatIndianNumber(this.point.value)} (${this.point.y.toFixed(2)}%)`;
                  }
              },

               plotOptions: {
                   column: {
                       dataLabels: {
                           enabled: true,
                           formatter: function() {
                               // Ensure this.percentage is a valid number
                               var percentage = this.percentage || 0; // Default to 0 if undefined
                               return `<b>${formatIndianNumber(this.y)}</b> (${percentage.toFixed(2)}%)`; // Custom data label format
                           },
                           style: {
                               fontSize: '14px',
                               fontWeight: 'bold',
                               color: '#333'
                           }
                       }
                   }
               },
               series: [{
                   name: stateName,
                   data: fields.map(function(field, index) {
                       return {
                           name: field.name,
                           y: field.percentage || 0, // Ensure percentage is set to 0 if undefined
                           value: field.value,  // Actual value
                           color: colors[index] // Use predefined colors
                       };
                   })
               }]
           });



            $("#wait").css("display", "none"); // Hide loader after success
        },
        error: function(xhr, status, error) {
            console.error("Error fetching data:", error);
            $("#wait").css("display", "none"); // Hide loader if an error occurs
        }
    });
}









