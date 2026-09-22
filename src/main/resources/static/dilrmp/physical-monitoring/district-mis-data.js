   // Event listener for district dropdown
   document.getElementById('district').addEventListener('change', function() {
   let selectedValue = this.value;
   alert("hii");
   // Tabs to enable/disable
   let tabs = ['tehsilsvillages-tab', 'clr-tab', 'mapd-tab', 'sro-tab', 'mrr-tab', 'survey-tab', 'ercms-tab', 'ror-tab'];

   if (selectedValue === "") {
   // Disable tabs if no district selected
   tabs.forEach(function(tab) {
   document.getElementById(tab).classList.add('disabled');
   });
   } else {
   // Enable all tabs if a district is selected
   tabs.forEach(function(tab) {
   document.getElementById(tab).classList.remove('disabled');
   });
   }
   });