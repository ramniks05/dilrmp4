// Coupler.io-inspired color palette
var COUPLER_COLORS = [
    '#4c6ef5', '#5c7cfa', '#12b886', '#7950f2',
    '#fd7e14', '#15aabf', '#e64980', '#40c057'
];

var COUPLER_CHART_THEME = {
    chart: {
        backgroundColor: 'transparent',
        style: { fontFamily: 'Inter, sans-serif' }
    },
    title: { style: { fontSize: '14px', fontWeight: '600', color: '#374151' } },
    credits: { enabled: false },
    legend: { itemStyle: { fontSize: '12px', color: '#6b7280', fontWeight: '500' } },
    tooltip: {
        backgroundColor: '#ffffff',
        borderColor: '#e8ecf1',
        borderRadius: 8,
        shadow: true,
        style: { fontSize: '13px' }
    }
};

function formatIndianNumber(num) {
    if (num === null || num === undefined) return '0';
    const numParts = num.toString().split('.');
    let integerPart = numParts[0];
    const decimalPart = numParts.length > 1 ? '.' + numParts[1] : '';
    integerPart = integerPart.replace(/(\d)(?=(\d\d)+\d$)/g, "$1,");
    return integerPart + decimalPart;
}

function showLoader() {
    $("#wait").addClass("active");
}

function hideLoader() {
    $("#wait").removeClass("active");
}

$(document).ready(function () {
    $.ajax({
        url: '/getDolrDashboardData',
        method: 'GET',
        beforeSend: showLoader,
        success: function (data) {
            hideLoader();

            $('#totalDistrict').text(formatIndianNumber(data.totalDistrict || '0'));
            $('#totalTehsil').text(formatIndianNumber(data.totalTehsil || '0'));
            $('#totalVillage').text(formatIndianNumber(data.totalVillage || '0'));
            $('#villagesComputerizationCompleted').text(formatIndianNumber(data.villagesComputerizationCompleted || '0'));
            $('#totalDigitizedMapsFmbTippans').text(formatIndianNumber(data.totalDigitizedMapsFmbTippans || '0'));
            $('#mrrCompleted').text(formatIndianNumber(data.mrrCompleted || '0'));
            $('#villagesDroneFlyingCompleted').text(formatIndianNumber(data.villagesDroneFlyingCompleted || '0'));
            $('#revenueCourtsComputerized').text(formatIndianNumber(data.revenueCourtsComputerized || '0'));
            $('#villagesWith100PercentRorLinkedAadhaar').text(formatIndianNumber(data.villagesWith100PercentRorLinkedAadhaar || '0'));
            $('#totalSro').text(formatIndianNumber(data.totalSro || '0'));
            $('#villagesWithUlipn').text(formatIndianNumber(data.villagesWithUlipn || '0'));

            let allChartData = [
                { label: 'CLR Completed', value: data.villagesComputerizationCompleted || 0 },
                { label: 'Digitized Mapsheets/FMBs/Tippan', value: data.totalDigitizedMapsFmbTippans || 0 },
                { label: 'Modern Record Room', value: data.mrrCompleted || 0 },
                { label: 'Surveyed Villages', value: data.villagesDroneFlyingCompleted || 0 },
                { label: 'e-RCCMS Computerized', value: data.revenueCourtsComputerized || 0 },
                { label: 'Aadhaar Linked With RoR', value: data.villagesWith100PercentRorLinkedAadhaar || 0 },
                { label: 'SROs Computerized', value: data.totalSro || 0 },
                { label: 'ULPIN Generated', value: data.villagesWithUlipn || 0 }
            ];

            var barChart = null;
            var hasBarChart = !!document.getElementById('dashboardBarChart');
            var hasChartFilters = !!document.getElementById('chartFilters');

            function updateCheckboxes() {
                if (!hasChartFilters) {
                    return;
                }
                let filterContainer = document.getElementById("chartFilters");
                filterContainer.innerHTML = "";

                allChartData.forEach((item, index) => {
                    let checkboxLabel = document.createElement("label");
                    let color = COUPLER_COLORS[index];

                    checkboxLabel.innerHTML = `
                        <input type="checkbox" class="chart-filter" checked data-index="${index}" style="accent-color: ${color};">
                        ${item.label} (<span id="label-${index}">${formatIndianNumber(item.value)}</span>)
                    `;
                    filterContainer.appendChild(checkboxLabel);
                });

                document.querySelectorAll(".chart-filter").forEach(checkbox => {
                    checkbox.addEventListener("change", updateCharts);
                });
            }

            function getFilteredData() {
                return allChartData.filter((_, i) => {
                    var el = document.querySelector(`.chart-filter[data-index="${i}"]`);
                    return el && el.checked;
                });
            }

            function updateCharts() {
                let filteredData = getFilteredData();

                pieChart.series[0].setData(filteredData.map((d) => ({
                    name: d.label,
                    y: d.value,
                    color: COUPLER_COLORS[allChartData.indexOf(d)]
                })));

                if (barChart) {
                    let filteredLabels = filteredData.map(d => d.label);
                    let filteredValues = filteredData.map(d => d.value);
                    barChart.xAxis[0].setCategories(filteredLabels);
                    barChart.series[0].setData(filteredValues.map((v, i) => ({
                        y: v,
                        color: COUPLER_COLORS[allChartData.findIndex(d => d.label === filteredLabels[i])]
                    })));
                }
            }

            updateCheckboxes();

            let pieChart = Highcharts.chart('dashboardPieChart', Highcharts.merge(COUPLER_CHART_THEME, {
                chart: { type: 'pie' },
                title: { text: null },
                series: [{
                    name: 'Count',
                    colorByPoint: true,
                    data: allChartData.map((d, i) => ({
                        name: d.label,
                        y: d.value,
                        color: COUPLER_COLORS[i]
                    }))
                }],
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            format: '<b>{point.percentage:.1f}%</b>',
                            style: { fontSize: '11px', fontWeight: '500', color: '#374151' },
                            distance: 15
                        },
                        showInLegend: true
                    }
                }
            }));

            if (hasBarChart) {
                barChart = Highcharts.chart('dashboardBarChart', Highcharts.merge(COUPLER_CHART_THEME, {
                    chart: { type: 'bar' },
                    title: { text: null },
                    xAxis: {
                        categories: allChartData.map(d => d.label),
                        labels: { style: { fontSize: '12px', color: '#6b7280' } },
                        lineColor: '#e8ecf1',
                        tickColor: '#e8ecf1'
                    },
                    yAxis: {
                        title: { text: null },
                        gridLineColor: '#f0f2f5',
                        labels: { style: { fontSize: '12px', color: '#6b7280' } }
                    },
                    series: [{
                        name: 'Count',
                        data: allChartData.map((d, i) => ({ y: d.value, color: COUPLER_COLORS[i] })),
                        borderRadius: 4,
                        borderWidth: 0
                    }],
                    plotOptions: {
                        bar: {
                            dataLabels: {
                                enabled: true,
                                format: '{y:,.0f}',
                                style: { fontSize: '11px', fontWeight: '600', color: '#374151' }
                            }
                        }
                    }
                }));
            }

            var totalDistrict = data.totalDistrict || 0;
            var totalTehsil = data.totalTehsil || 0;
            var totalVillage = data.totalVillage || 0;
            var totalState = 36;

            var stateChartData = [
                { name: 'Districts', value: totalDistrict },
                { name: 'Tehsils', value: totalTehsil },
                { name: 'Villages', value: totalVillage },
                { name: 'States/UTs', value: totalState }
            ];

            var geoColors = ['#4c6ef5', '#12b886', '#7950f2', '#fd7e14'];

            Highcharts.chart('containerState', Highcharts.merge(COUPLER_CHART_THEME, {
                chart: { type: 'pie' },
                title: { text: null },
                tooltip: {
                    pointFormat: '<b>{point.name}:</b> {point.formattedValue}'
                },
                plotOptions: {
                    pie: {
                        innerSize: '55%',
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            format: '{point.label}',
                            style: { fontSize: '12px', fontWeight: '600', color: '#374151' }
                        },
                        showInLegend: true
                    }
                },
                series: [{
                    name: 'Master Details',
                    data: stateChartData.map(function (item, index) {
                        return {
                            name: item.name,
                            y: item.value,
                            formattedValue: formatIndianNumber(item.value),
                            label: formatIndianNumber(item.value),
                            color: geoColors[index]
                        };
                    })
                }]
            }));

            let villagesGeoreferenced = data.villagesDroneFlyingCompleted || 0;
            let villagesWithUlipn = data.villagesWithUlipn || 0;
            let villages = data.totalVillage || 0;

            let chartData = [
                { name: 'Surveyed Villages', y: villagesGeoreferenced, color: '#4c6ef5' },
                { name: 'ULPIN Villages', y: villagesWithUlipn, color: '#12b886' }
            ];

            Highcharts.chart('villagesDonutChart', Highcharts.merge(COUPLER_CHART_THEME, {
                chart: {
                    type: 'pie',
                    events: {
                        render: function () {
                            let chart = this;
                            if (chart.customLabel) {
                                chart.customLabel.destroy();
                            }
                            chart.customLabel = chart.renderer.text(
                                'Total Villages<br><span style="font-size:18px;font-weight:700">' + formatIndianNumber(villages) + '</span>',
                                chart.plotLeft + chart.plotWidth / 2,
                                chart.plotTop + chart.plotHeight / 2 + 6
                            )
                            .css({ color: '#1a1d26', fontSize: '12px', fontWeight: '500', textAlign: 'center' })
                            .attr({ align: 'center' })
                            .add();
                        }
                    }
                },
                title: { text: null },
                plotOptions: {
                    pie: {
                        innerSize: '65%',
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            format: '{point.name}',
                            style: { fontWeight: '600', fontSize: '12px', color: '#374151' }
                        },
                        showInLegend: true
                    }
                },
                series: [{ data: chartData }]
            }));

        },
        error: function (xhr, status, error) {
            hideLoader();
            console.error("Error fetching data: " + error);
            alert("Failed to fetch dashboard data. Please try again later.");
        }
    });
});
