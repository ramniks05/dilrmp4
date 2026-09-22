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
            url: '/reports/getDocument', // Your REST API endpoint
            method: 'GET',
            beforeSend: function() {
                $("#wait").css("display", "block"); // Show loader if necessary
            },
            success: function(data) {
                $("#wait").css("display", "none"); // Hide loader

                // Assuming data is the nationalDashBordDTOList
                $('#villagesComputerizationCompleted').text(formatIndianNumber(data.villagesComputerizationCompleted || '0'));
                $('#totalDigitizedMapsFmbTippans').text(formatIndianNumber(data.totalDigitizedMapsFmbTippans || '0'));
                $('#mrrCompleted').text(formatIndianNumber(data.mrrCompleted || '0'));
                $('#villagesDroneFlyingCompleted').text(formatIndianNumber(data.villagesDroneFlyingCompleted || '0'));
                $('#totalSro').text(formatIndianNumber(data.totalSro || '0'));
                $('#revenueCourtsComputerized').text(formatIndianNumber(data.revenueCourtsComputerized || '0'));
                $('#villagesWith100PercentRorLinkedAadhaar').text(formatIndianNumber(data.villagesWith100PercentRorLinkedAadhaar || '0'));
            },
            error: function(xhr, status, error) {
                $("#wait").css("display", "none"); // Hide loader
                console.error("Error fetching data: " + error);
                // Optionally, display an error message
            }
        });
    });